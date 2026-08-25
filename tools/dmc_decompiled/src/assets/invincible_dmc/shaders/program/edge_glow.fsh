#version 150

uniform sampler2D DiffuseSampler;   // 主屏幕
uniform sampler2D Mask;             // 粒子缓冲区
uniform vec2 OutSize;

uniform float EdgeIntensity;        // 边缘深蓝强度 (0.0 ~ 1.0, 默认 0.8)
uniform float GlowIntensity;        // 紫色发光强度 (0.0 ~ 1.5, 默认 0.9)
uniform float GlowRadius;           // 发光扩散半径 (1.0 ~ 8.0, 默认 4.0)
uniform float Time;                 // 时间 (秒), 驱动所有动态效果

in vec2 texCoord;
out vec4 fragColor;

// ===========================================================================
//  噪声函数
// ===========================================================================

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

float hash13(vec3 p) {
    return fract(sin(dot(p, vec3(127.1, 311.7, 74.7))) * 43758.5453);
}

// 2D 值噪声
float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);

    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));

    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

// FBM (分形布朗运动) — 多层次噪声叠加
float fbm(vec2 p) {
    float value = 0.0;
    float amp = 0.5;
    float freq = 1.0;
    for (int i = 0; i < 3; i++) {
        value += amp * noise(p * freq);
        freq *= 2.0;
        amp *= 0.5;
    }
    return value;
}

// ===========================================================================
//  边缘检测 & 发光扩散 (保留原有)
// ===========================================================================

float sobelEdgeAlpha(sampler2D tex, vec2 uv, vec2 texel) {
    float tl = texture(tex, uv + vec2(-1.0, -1.0) * texel).a;
    float t  = texture(tex, uv + vec2( 0.0, -1.0) * texel).a;
    float tr = texture(tex, uv + vec2( 1.0, -1.0) * texel).a;
    float l  = texture(tex, uv + vec2(-1.0,  0.0) * texel).a;
    float r  = texture(tex, uv + vec2( 1.0,  0.0) * texel).a;
    float bl = texture(tex, uv + vec2(-1.0,  1.0) * texel).a;
    float b  = texture(tex, uv + vec2( 0.0,  1.0) * texel).a;
    float br = texture(tex, uv + vec2( 1.0,  1.0) * texel).a;

    float gx = -tl - 2.0 * l - bl + tr + 2.0 * r + br;
    float gy = -tl - 2.0 * t - tr + bl + 2.0 * b + br;
    return sqrt(gx * gx + gy * gy);
}

float ringBlur(sampler2D tex, vec2 uv, vec2 texel, float radius) {
    float sum = 0.0;
    int samples = 12;
    for (int i = 0; i < samples; i++) {
        float angle = float(i) * 6.283185307 / float(samples);
        vec2 offset = vec2(cos(angle), sin(angle)) * radius * texel;
        sum += texture(tex, uv + offset).a;
    }
    return sum / float(samples);
}

// ===========================================================================
//  传送门表面效果
// ===========================================================================

void main() {
    vec2 texelSize = 1.0 / OutSize;

    vec4 scene    = texture(DiffuseSampler, texCoord);
    vec4 particle = texture(Mask, texCoord);

    // ---- 归一化 UV (相对于屏幕空间) ----
    // 用于传送门表面效果的世界空间 UV
    vec2 portalUV = texCoord * OutSize * 0.008;

    // ========================================================================
    //  1. 边缘检测 + 发光扩散
    // ========================================================================
    float edge = sobelEdgeAlpha(Mask, texCoord, texelSize);
    float edgeSharp = smoothstep(0.08, 0.35, edge);

    float glowNear  = ringBlur(Mask, texCoord, texelSize, GlowRadius);
    float glowMid   = ringBlur(Mask, texCoord, texelSize, GlowRadius * 1.8);
    float glowFar   = ringBlur(Mask, texCoord, texelSize, GlowRadius * 3.0);

    // ========================================================================
    //  2. 蓝紫交替色板
    // ========================================================================
    float cycle  = sin(Time * 2.5) * 0.5 + 0.5;
    float cycle2 = sin(Time * 4.7 + 1.8) * 0.5 + 0.5;
    float blueWeight = 1.0 - cycle;

    vec3 blueEdge   = vec3(0.02, 0.06, 0.35);
    vec3 blueInner  = vec3(0.08, 0.18, 0.50);
    vec3 blueMid    = vec3(0.15, 0.28, 0.62);
    vec3 blueOuter  = vec3(0.22, 0.38, 0.78);

    vec3 purpEdge   = vec3(0.16, 0.02, 0.32);
    vec3 purpInner  = vec3(0.35, 0.08, 0.55);
    vec3 purpMid    = vec3(0.50, 0.15, 0.72);
    vec3 purpOuter  = vec3(0.60, 0.25, 0.88);

    vec3 edgeColor  = mix(purpEdge,  blueEdge,  blueWeight);
    vec3 innerColor = mix(purpInner, blueInner, blueWeight);
    vec3 midColor   = mix(purpMid,   blueMid,   blueWeight);
    vec3 outerColor = mix(purpOuter, blueOuter, blueWeight);

    vec3 innerColor2 = mix(innerColor, purpInner, cycle2 * 0.3);
    vec3 midColor2   = mix(midColor,   purpMid,   cycle2 * 0.3);
    vec3 outerColor2 = mix(outerColor, purpOuter, cycle2 * 0.25);

    // ========================================================================
    //  3. 传送门表面效果 (仅在 alpha > 0 区域生效)
    // ========================================================================
    float particlePresence = smoothstep(0.01, 0.3, particle.a);

    // --- 3a. FBM 噪声纹理 — 表面能量流动 ---
    float fbmVal = fbm(portalUV * 3.0 + Time * 0.4);
    float fbmFine = fbm(portalUV * 7.0 - Time * 0.6);
    // 用噪声调制粒子亮度，创造不规则的"能量纹路"
    float energyTexture = fbmVal * 0.55 + fbmFine * 0.25;
    energyTexture = energyTexture * 0.6 + 0.4; // 0.4..1.0, 不完全变黑

    // --- 3b. 径向能量波纹 — 从中心向外扩散的同心波 ---
    vec2 center = texCoord - vec2(0.5);
    float dist = length(center);
    // 多层波纹，不同频率 + 速度
    float ripple1 = sin(dist * 12.0 - Time * 3.5) * 0.5 + 0.5;
    float ripple2 = cos(dist * 20.0 + Time * 2.8) * 0.5 + 0.5;
    float ripple3 = sin(dist * 32.0 - Time * 5.2) * 0.5 + 0.5;
    float ripple = ripple1 * 0.5 + ripple2 * 0.3 + ripple3 * 0.2;
    // 波纹在中心强、到边缘衰减 (防止外部过亮)
    float rippleFalloff = 1.0 - smoothstep(0.0, 0.45, dist);
    ripple = ripple * rippleFalloff + (1.0 - rippleFalloff) * 0.5;
    // 微调: 波纹影响粒子亮度
    float rippleBoost = mix(1.0, ripple, 0.25);

    // --- 3c. 漩涡旋转失真 — 角度坐标随时间旋转 ---
    float angle = atan(center.y, center.x);
    float swirl = sin(dist * 5.0 + angle * 2.0 - Time * 1.8) * 0.08;
    swirl += cos(dist * 8.0 - angle * 3.0 + Time * 2.2) * 0.05;
    // 用漩涡偏移去重新采样 Mask，产生空间扭曲感
    vec2 swirlOffset = vec2(cos(angle + swirl), sin(angle + swirl)) * dist * 0.015;
    vec4 particleSwirled = texture(Mask, texCoord + swirlOffset);
    // 漩涡仅在粒子内部生效
    float swirlFactor = particlePresence * 0.3;

    // --- 3d. 闪烁光点 — 随机亮斑 ---
    float sparkleField = hash13(vec3(floor(portalUV * 16.0), 0.0));
    float sparkleTime  = hash13(vec3(floor(portalUV * 16.0), 1.0));
    float sparkle = smoothstep(0.992, 1.0, sparkleField)   // 密度 0.8%, 仅为原来的 1/10
                  * smoothstep(0.0, 1.0, sin(Time * 8.0 + sparkleTime * 6.28) * 0.5 + 0.5); // 随时间闪烁
    sparkle *= particlePresence; // 只在粒子内部
    // 聚集到波纹波峰处更亮
    sparkle *= 0.6 + ripple * 0.4;

    // ========================================================================
    //  4. 合成 — 所有效果叠加
    // ========================================================================
    vec3 result = scene.rgb;

    // --- 4a. 边缘发光 (原有) ---
    float edgeFactor = edgeSharp * EdgeIntensity * particlePresence;
    result = mix(result, edgeColor, edgeFactor);

    float glowNearFactor = clamp((glowNear - particle.a) * GlowIntensity, 0.0, 1.0);
    result = mix(result, innerColor2, glowNearFactor * 0.7 * (1.0 - particlePresence * 0.6));

    float glowMidFactor = clamp((glowMid - particle.a * 0.8) * GlowIntensity * 0.7, 0.0, 1.0);
    result = mix(result, midColor2, glowMidFactor * 0.5 * (1.0 - particlePresence * 0.3));

    float glowFarFactor = clamp((glowFar - particle.a * 0.5) * GlowIntensity * 0.4, 0.0, 1.0);
    result = mix(result, outerColor2, glowFarFactor * 0.35);

    float innerGlow = glowNear * GlowIntensity * 0.3 * particlePresence;
    result = mix(result, midColor2, innerGlow * 0.25);

    // --- 4b. 传送门表面效果叠加 (以原始贴图为基础) ---
    vec3 surfaceEnergy = mix(blueInner, purpInner, cycle);   // 基底色调
    vec3 surfaceBright = mix(blueOuter, purpOuter, cycle2);  // 亮部色调
    vec3 sparkleColor  = mix(vec3(0.8, 0.7, 1.0), vec3(0.6, 0.9, 1.0), cycle);

    // 以粒子原始贴图颜色为基础
    vec3 portalBase = particle.rgb;

    // 噪声能量纹路 — 乘算调制贴图亮度 (10% 强度)
    portalBase = mix(portalBase, portalBase * (0.7 + energyTexture * 0.6), particlePresence * 0.4);

    // 径向波纹 — 加算亮部色调
    portalBase += surfaceBright * rippleBoost * particlePresence * 0.12;

    // 漩涡扭曲 — 微调亮度
    portalBase = mix(portalBase, portalBase * 1.08, particleSwirled.a * swirlFactor);

    // 闪烁光点 — 加算
    portalBase += sparkleColor * sparkle * 0.7;

    // 合成: 原始贴图保留, 特效叠加其上
    // 粒子不透明核心
    float opaqueCore = smoothstep(0.5, 1.0, particle.a);
    float surfaceBlend = opaqueCore * (1.0 - edgeSharp * 0.3);
    result = mix(result, portalBase, surfaceBlend);

    // 粒子半透明区域: 原始贴图 + 特效 50/50
    float semiTransparent = smoothstep(0.1, 0.5, particle.a) * (1.0 - opaqueCore);
    result = mix(result, mix(particle.rgb, portalBase, 0.5), semiTransparent * 0.6);

    fragColor = vec4(result, 1.0);
}
