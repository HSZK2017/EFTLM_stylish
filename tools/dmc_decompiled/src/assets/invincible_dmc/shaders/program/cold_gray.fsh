#version 150

uniform sampler2D DiffuseSampler;
uniform float Intensity;      // 效果强度
uniform float Coldness;       // 冷色调强度
uniform float Grayness;       // 灰白程度
uniform float Progress;       // 归一化寿命 0→1

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);

    // ── 过渡: 前 9.5% (≈6/63tick) 二次递增, 之后保持 ──
    float fadeIn = Progress < 0.095
        ? (Progress / 0.095) * (Progress / 0.095)   // t² ease-in
        : 1.0;

    float effectiveIntensity = Intensity * fadeIn;

    // 1. 转换为灰度
    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
    vec3 desaturated = mix(color.rgb, vec3(gray), Grayness);

    // 2. 增加冷色调 (增强蓝色，减弱红色)
    vec3 coldColor = desaturated;
    coldColor.b = desaturated.b * (1.0 + Coldness * 0.5);
    coldColor.r = desaturated.r * (1.0 - Coldness * 0.3);

    // 3. 混合原色和效果色
    vec3 finalColor = mix(color.rgb, coldColor, effectiveIntensity);

    fragColor = vec4(finalColor, color.a);
}