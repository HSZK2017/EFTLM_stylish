#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 Center;
uniform float ChromaIntensity;
uniform float ChromaProgress;  // 0→1, 色差强度曲线 (Java端计算: 0→20%递增, 20%-80%保持, 80%-100%递减)

in vec2 texCoord;
out vec4 fragColor;

vec3 getDistortedRGB(vec2 uv, vec2 c, float strR, float strG, float strB) {
    vec2 offset = uv - c;
    float r = length(offset);

    if (r < 0.001) {
        return texture(DiffuseSampler, uv).rgb;
    }

    float distortionR = 1.0 + strR * r * 1.0;
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
    // ── 纯色差强度 (受 ChromaProgress 调制) ──
    float caBase = ChromaIntensity * 0.36 * ChromaProgress;

    float strR = caBase * 3.5;
    float strG = caBase * 0.4;
    float strB = caBase * 2.5;

    vec3 distorted = getDistortedRGB(texCoord, Center, strR, strG, strB);

    // ── 与原图混合，确保强度为0时完全还原 ──
    vec4 original = texture(DiffuseSampler, texCoord);
    float blendFactor = clamp(ChromaProgress, 0.0, 1.0);

    fragColor = vec4(mix(original.rgb, distorted, blendFactor), original.a);
}
