#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 Center;
uniform float Intensity;
uniform int Samples;
uniform float ChromaProgress;  // 0→1, 色差强度曲线 (Java端计算: 0→20%递增, 20%-80%保持, 80%-100%递减)

in vec2 texCoord;
out vec4 fragColor;

// RGB 通道径向分离 (色差核心)
vec3 getDistortedRGB(vec2 uv, vec2 c, float strR, float strG, float strB) {
    vec2 offset = uv - c;
    float r = length(offset);

    if (r < 0.001) {
        return texture(DiffuseSampler, uv).rgb;
    }

    float distortionR = 1.0 + strR * r;
    float distortionG = 1.0 + strG * r * 0.5;
    float distortionB = 1.0 + strB * r * -1.0;

    vec2 uvR = clamp(c + offset * distortionR, 0.0, 1.0);
    vec2 uvG = clamp(c + offset * distortionG, 0.0, 1.0);
    vec2 uvB = clamp(c + offset * distortionB, 0.0, 1.0);

    return vec3(
        texture(DiffuseSampler, uvR).r,
        texture(DiffuseSampler, uvG).g,
        texture(DiffuseSampler, uvB).b
    );
}

void main() {
    vec2 dir = texCoord - Center;
    float dist = length(dir);

    // ── 径向模糊方向 ──
    vec2 blurDir = vec2(0.0);
    if (dist > 0.001) blurDir = dir / dist;

    float blurRadius = Intensity * dist;

    // ── 色差强度 (受 ChromaProgress 调制, 模糊不受影响) ──
    float caBase = Intensity * 0.36 * ChromaProgress;

    // 统一色差参数 (所有采样点使用相同的 CA 强度)
    float strR = caBase * 3.5;
    float strG = caBase * 0.4;
    float strB = caBase * 2.5;

    // ═══════════════════════════════════════════════
    //  升级版径向模糊: 高斯加权采样
    //  权重集中在中心, 远离中心的采样贡献递减
    //  替代原来的 uniform 平均 (box filter),
    //  得到更平滑、更自然的径向拖尾
    // ═══════════════════════════════════════════════
    vec3 accum = vec3(0.0);
    float totalWeight = 0.0;

    // 高斯 sigma: 控制模糊扩散曲线
    // 值越小 → 中心越锐利, 拖尾越紧凑
    // 值越大 → 扩散越均匀, 接近原版 box filter
    float sigma = 0.42;

    for (int i = -Samples; i <= Samples; ++i) {
        float t = float(i) / float(Samples);             // -1.0 → 1.0
        float weight = exp(-(t * t) / (2.0 * sigma * sigma));
        vec2 sampleUV = clamp(texCoord + blurDir * blurRadius * t, 0.0, 1.0);

        accum += getDistortedRGB(sampleUV, Center, strR, strG, strB) * weight;
        totalWeight += weight;
    }

    vec3 result = accum / totalWeight;

    fragColor = vec4(result, 1.0);
}
