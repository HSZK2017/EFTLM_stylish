package org.eftlm.stylish.rl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * MLP 策略网络推理器（无第三方依赖）。
 * <p>
 * 权重文件格式（与 Python 训练脚本导出一致，大端字节序）：
 * <pre>
 *   int32    numLayers（隐藏层数）
 *   int32    sizes[0..numLayers]（输入/各隐藏/输出维度）
 *   每层 i:  float32 weights[sizes[i] * sizes[i+1]]（行优先 out*in）
 *            float32 biases[sizes[i+1]]
 * </pre>
 * 前向：线性 → ReLU × 隐藏层 → 线性 → Softmax（输出行动概率分布）。
 */
public final class RlModel {

    private static final Logger LOGGER = LogManager.getLogger("eftlm_stylish");

    private final int inputDim;
    private final int outputDim;
    /** weights[layer][out][in] */
    private final float[][][] weights;
    private final float[][] biases;
    /** 激活函数：ReLU */
    private boolean loaded;

    private RlModel(int inputDim, int outputDim, float[][][] weights, float[][] biases) {
        this.inputDim = inputDim;
        this.outputDim = outputDim;
        this.weights = weights;
        this.biases = biases;
        this.loaded = true;
    }

    public static RlModel load(Path file) {
        if (file == null || !Files.exists(file)) {
            return null;
        }
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(file)))) {
            int numLayers = in.readInt();
            if (numLayers < 1 || numLayers > 8) {
                throw new IOException("invalid numLayers: " + numLayers);
            }
            int[] sizes = new int[numLayers + 1];
            for (int i = 0; i <= numLayers; i++) {
                sizes[i] = in.readInt();
                if (sizes[i] < 1 || sizes[i] > 4096) {
                    throw new IOException("invalid size[" + i + "]: " + sizes[i]);
                }
            }
            float[][][] weights = new float[numLayers][][];
            float[][] biases = new float[numLayers][];
            for (int i = 0; i < numLayers; i++) {
                int inDim = sizes[i];
                int outDim = sizes[i + 1];
                weights[i] = new float[outDim][inDim];
                for (int o = 0; o < outDim; o++) {
                    for (int k = 0; k < inDim; k++) {
                        weights[i][o][k] = in.readFloat();
                    }
                }
                biases[i] = new float[outDim];
                for (int o = 0; o < outDim; o++) {
                    biases[i][o] = in.readFloat();
                }
            }
            LOGGER.info("[RL] model loaded: sizes={} dims={}->{}", java.util.Arrays.toString(sizes), sizes[0], sizes[numLayers]);
            return new RlModel(sizes[0], sizes[numLayers], weights, biases);
        } catch (IOException e) {
            LOGGER.error("[RL] failed to load model {}: {}", file, e.toString());
            return null;
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

    public int getInputDim() {
        return inputDim;
    }

    public int getOutputDim() {
        return outputDim;
    }

    /** 层数（含输入输出；3 层 MLP = 16/18-64-64-27） */
    public int getLayerCount() {
        return weights.length + 1;
    }

    /**
     * 前向推理：输入状态向量，返回各行动的概率分布（Softmax）。
     */
    public float[] forward(float[] input) {
        if (input.length != inputDim) {
            throw new IllegalArgumentException("input dim mismatch: " + input.length + " != " + inputDim);
        }
        float[] cur = input;
        for (int i = 0; i < weights.length; i++) {
            boolean last = i == weights.length - 1;
            cur = linear(cur, weights[i], biases[i], !last); // 隐藏层 ReLU，输出层线性
        }
        return softmax(cur);
    }

    private static float[] linear(float[] input, float[][] w, float[] b, boolean relu) {
        float[] out = new float[b.length];
        for (int o = 0; o < b.length; o++) {
            float sum = b[o];
            float[] row = w[o];
            for (int k = 0; k < input.length; k++) {
                sum += row[k] * input[k];
            }
            out[o] = relu ? Math.max(0.0F, sum) : sum;
        }
        return out;
    }

    private static float[] softmax(float[] logits) {
        float max = Float.NEGATIVE_INFINITY;
        for (float v : logits) {
            if (v > max) max = v;
        }
        float sum = 0.0F;
        float[] probs = new float[logits.length];
        for (int i = 0; i < logits.length; i++) {
            probs[i] = (float) Math.exp(logits[i] - max);
            sum += probs[i];
        }
        for (int i = 0; i < probs.length; i++) {
            probs[i] /= sum;
        }
        return probs;
    }
}
