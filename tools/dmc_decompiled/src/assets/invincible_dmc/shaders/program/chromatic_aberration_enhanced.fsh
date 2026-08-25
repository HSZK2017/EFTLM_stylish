#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D Mask;
uniform float DistortionStrength;
uniform float Progress;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 originalColor = texture(DiffuseSampler, texCoord);
    vec4 mask = texture(Mask, texCoord);
    float alpha = mask.a;

    // 边缘羽化
    float featherAlpha = smoothstep(0.05, 0.7, alpha);

    if (alpha < 0.001) {
        fragColor = originalColor;
        return;
    }

    vec2 centerVec = texCoord - vec2(0.5);
    float centerDist = length(centerVec);

    // Progress 调制整体强度
    float strength = DistortionStrength * Progress * featherAlpha;

    // ══════════════════════════════════════════
    //  鱼眼内缩（中间放大）
    // ══════════════════════════════════════════
    float fisheyeR2 = centerDist * centerDist;
    float fisheyeR4 = fisheyeR2 * fisheyeR2;
    float fishEyeStrength = strength * 22.5;
    vec2 fisheyeOffset = -centerVec * fishEyeStrength * (fisheyeR2 + 0.3 * fisheyeR4);

    vec2 distortedCoord = texCoord + fisheyeOffset;

    // ══════════════════════════════════════════
    //  色差效果（RGB 通道径向分离）
    // ══════════════════════════════════════════
    vec2 radialDir = vec2(0.0);
    if (centerDist > 0.001) {
        radialDir = centerVec / centerDist;
    }

    float chromaStrength = strength * 0.9 * 0.6667;

    vec2 chromaRed  = fisheyeOffset * 0.85 + radialDir * chromaStrength * 8.0 + vec2(chromaStrength * 3.0, 0.0);
    vec2 chromaBlue = fisheyeOffset * 0.15 - radialDir * chromaStrength * 8.0 - vec2(chromaStrength * 3.0, 0.0);

    vec4 baseColor = texture(DiffuseSampler, distortedCoord);
    float r = texture(DiffuseSampler, distortedCoord + chromaRed).r;
    float g = baseColor.g;
    float b = texture(DiffuseSampler, distortedCoord + chromaBlue).b;

    vec4 distortedColor = vec4(r, g, b, originalColor.a);

    // Progress 调制混合
    float blendFactor = featherAlpha * featherAlpha * Progress;

    fragColor = mix(originalColor, distortedColor, blendFactor);
    fragColor.a = originalColor.a;
}
