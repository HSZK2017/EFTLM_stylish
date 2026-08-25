#version 150

uniform sampler2D DiffuseSampler;
uniform float Brightness;        // 亮度倍数
uniform float GlowIntensity;     // 辉光强度

// 冷灰渐变效果
uniform float ColdFade;          // 冷灰效果渐变因子 (二次函数: 1.0→0.0)
uniform float ColdIntensity;     // 冷灰效果强度
uniform float ColdColdness;      // 冷色调强度
uniform float ColdGrayness;      // 灰白程度

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);

    // ========== 冷灰渐变 (先于flash, 确保初始染色与ColdGrayEffect一致) ==========
    // ColdFade 二次函数 1.0→0.0: 初始完全冷灰, 逐渐恢复原始色彩
    // 辅助函数逻辑: 灰度化 → 蓝增/红减 → mix原色
    // ============================================================================

    // 确保亮度不低于1.0（原亮度）
    float finalBrightness = max(Brightness, 1.0);

    // 色差强度随 GlowIntensity 变化，爆闪时明显，恢复时消失
    float chromaStrength = GlowIntensity * 0.005;

    // 分别采样RGB通道（基于冷灰后的颜色），产生色差效果
    vec2 offsetRed = vec2(chromaStrength, 0.0);
    vec2 offsetBlue = vec2(-chromaStrength, 0.0);
    vec2 offsetGreen = vec2(0.0, 0.0);

    // 对冷灰后的颜色 + 偏移重新采样RGB
    float r = texture(DiffuseSampler, clamp(texCoord + offsetRed, 0.0, 1.0)).r;
    float g = texture(DiffuseSampler, clamp(texCoord + offsetGreen, 0.0, 1.0)).g;
    float b = texture(DiffuseSampler, clamp(texCoord + offsetBlue, 0.0, 1.0)).b;

    // 对偏移采样也应用冷灰处理
    vec3 offsetColor = vec3(r, g, b);
    float grayOff = dot(offsetColor, vec3(0.299, 0.587, 0.114));
    vec3 desatOff = mix(offsetColor, vec3(grayOff), ColdGrayness * ColdFade);
    vec3 coldOff = desatOff;
    coldOff.b = desatOff.b * (1.0 + ColdColdness * 0.5 * ColdFade);
    coldOff.r = desatOff.r * (1.0 - ColdColdness * 0.3 * ColdFade);
    vec3 colorWithChroma = mix(offsetColor, coldOff, ColdIntensity * ColdFade);

    // 基础亮度提升
    vec3 brightColor = colorWithChroma * finalBrightness;

    // 辉光效果 - 采样周围像素
    vec2 glowOffsets[4];
    glowOffsets[0] = vec2(0.006, 0.0);
    glowOffsets[1] = vec2(-0.006, 0.0);
    glowOffsets[2] = vec2(0.0, 0.006);
    glowOffsets[3] = vec2(0.0, -0.006);

    vec3 glow = brightColor;
    for(int i = 0; i < 4; i++) {
        vec2 sampleCoord = clamp(texCoord + glowOffsets[i], 0.0, 1.0);
        float rSample = texture(DiffuseSampler, clamp(sampleCoord + offsetRed, 0.0, 1.0)).r;
        float gSample = texture(DiffuseSampler, clamp(sampleCoord + offsetGreen, 0.0, 1.0)).g;
        float bSample = texture(DiffuseSampler, clamp(sampleCoord + offsetBlue, 0.0, 1.0)).b;
        vec3 sampleColor = vec3(rSample, gSample, bSample);
        float grayS = dot(sampleColor, vec3(0.299, 0.587, 0.114));
        vec3 desatS = mix(sampleColor, vec3(grayS), ColdGrayness * ColdFade);
        vec3 coldS = desatS;
        coldS.b = desatS.b * (1.0 + ColdColdness * 0.5 * ColdFade);
        coldS.r = desatS.r * (1.0 - ColdColdness * 0.3 * ColdFade);
        vec3 sampleWithChroma = mix(sampleColor, coldS, ColdIntensity * ColdFade);
        glow += sampleWithChroma * finalBrightness;
    }
    glow /= 5.0;

    // 混合原色和辉光
    float finalGlow = max(GlowIntensity, 0.0);
    vec3 finalColor = mix(brightColor, glow, finalGlow);

    fragColor = vec4(finalColor, color.a);
}