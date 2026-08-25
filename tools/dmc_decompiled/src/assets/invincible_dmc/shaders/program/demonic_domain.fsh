#version 330 core

uniform sampler2D DiffuseSampler;
uniform sampler2D DepthSampler;

uniform mat4 InverseTransformMatrix;
uniform mat4 ModelViewMat;
uniform vec3 CameraPosition;

uniform vec3 DomainCenter;
uniform float DomainRadius;
uniform float ScanTime;
uniform float ScanSpeed;
uniform float ScanFrequency;
uniform float SweepSpeed;
uniform float TintAlpha;
uniform vec3 TintColor;
uniform vec3 GlowColor;
uniform float LifetimeSeconds;
uniform float FadeFactor;

in vec2 texCoord;
out vec4 fragColor;

// ── Hash / noise ──
float hash3(vec3 p) {
    return fract(sin(dot(p, vec3(127.1, 311.7, 74.7))) * 43758.5453);
}
float noise3D(vec3 p) {
    vec3 i = floor(p);
    vec3 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    return mix(
        mix(mix(hash3(i), hash3(i+vec3(1,0,0)), f.x),
            mix(hash3(i+vec3(0,1,0)), hash3(i+vec3(1,1,0)), f.x), f.y),
        mix(mix(hash3(i+vec3(0,0,1)), hash3(i+vec3(1,0,1)), f.x),
            mix(hash3(i+vec3(0,1,1)), hash3(i+vec3(1,1,1)), f.x), f.y),
        f.z);
}

// Reconstruct world position from screen UV + depth
vec3 worldPosFromDepth(vec3 screenPos) {
    vec3 ndc = screenPos * 2.0 - 1.0;
    vec4 homPos = InverseTransformMatrix * vec4(ndc, 1.0);
    vec3 viewPos = homPos.xyz / homPos.w;
    return (inverse(ModelViewMat) * vec4(viewPos, 1.0)).xyz + CameraPosition;
}

float sphereGridLine(float coordinate, float frequency, float width) {
    float distanceToLine = abs(fract(coordinate * frequency + 0.5) - 0.5);
    float antiAlias = max(fwidth(coordinate * frequency), 0.002);
    return 1.0 - smoothstep(width, width + antiAlias, distanceToLine);
}

void main() {
    vec3 sceneColor = texture(DiffuseSampler, texCoord).rgb;
    float depth = texture(DepthSampler, texCoord).r;

    vec3 worldPos = vec3(0.0);
    float sceneDistance = 1.0e20;
    if (depth < 0.9999) {
        worldPos = worldPosFromDepth(vec3(texCoord, depth));
        sceneDistance = length(worldPos - CameraPosition);
    }

    float lifeProgress = clamp(ScanTime / max(LifetimeSeconds, 0.001), 0.0, 1.0);
    float introFactor = smoothstep(0.0, 0.12, lifeProgress);
    float fieldPresence = introFactor * FadeFactor;
    float animatedTime = ScanTime * max(ScanSpeed, 0.01);

    // A restrained shell gives the startup field a readable silhouette even against the sky.
    vec3 rayFarPosition = worldPosFromDepth(vec3(texCoord, 0.9999));
    vec3 rayDirection = normalize(rayFarPosition - CameraPosition);
    vec3 rayToCenter = CameraPosition - DomainCenter;
    float sphereB = dot(rayToCenter, rayDirection);
    float sphereC = dot(rayToCenter, rayToCenter) - DomainRadius * DomainRadius;
    float sphereDiscriminant = sphereB * sphereB - sphereC;
    float shellDistance = 1.0e20;
    vec3 shellNormal = vec3(0.0, 1.0, 0.0);
    float shellVisible = 0.0;

    if (sphereDiscriminant > 0.0) {
        float sphereRoot = sqrt(sphereDiscriminant);
        float nearDistance = -sphereB - sphereRoot;
        float farDistance = -sphereB + sphereRoot;
        shellDistance = nearDistance > 0.0 ? nearDistance : farDistance;
        if (shellDistance > 0.0) {
            vec3 shellPosition = CameraPosition + rayDirection * shellDistance;
            shellNormal = normalize(shellPosition - DomainCenter);
            shellVisible = step(shellDistance, sceneDistance + 0.15);

            vec3 viewNormal = normalize((ModelViewMat * vec4(shellNormal, 0.0)).xyz);
            float fresnel = pow(1.0 - abs(dot(shellNormal, -rayDirection)), 3.0);
            float longitude = atan(shellNormal.z, shellNormal.x) / 6.2831853 + 0.5;
            float latitude = asin(clamp(shellNormal.y, -1.0, 1.0)) / 3.1415926 + 0.5;
            float shellGrid = max(
                    sphereGridLine(longitude, 18.0, 0.018),
                    sphereGridLine(latitude, 10.0, 0.014));

            vec3 sweepAxis = normalize(vec3(
                    cos(animatedTime * SweepSpeed),
                    0.22,
                    sin(animatedTime * SweepSpeed)));
            float sealingSweep = exp(-abs(dot(shellNormal, sweepAxis)) * 90.0);
            float shellPulse = 0.78 + 0.22 * sin(animatedTime * ScanFrequency + longitude * 8.0);
            float shellIntensity = (0.18 * fresnel + 0.16 * shellGrid + 0.34 * sealingSweep)
                    * shellPulse * fieldPresence * shellVisible;

            vec2 distortionOffset = viewNormal.xy * (0.0025 + 0.004 * fresnel) * shellIntensity;
            vec3 refractedScene = texture(DiffuseSampler, clamp(texCoord + distortionOffset, 0.001, 0.999)).rgb;
            sceneColor = mix(sceneColor, refractedScene, shellIntensity * 0.30);
            sceneColor = mix(sceneColor, mix(TintColor * 0.75, vec3(0.62, 0.72, 1.0), fresnel), shellIntensity * 0.48);
            sceneColor += GlowColor * shellIntensity * 0.24;
        }
    }

    if (depth >= 0.9999) {
        fragColor = vec4(sceneColor, 1.0);
        return;
    }

    // ── Domain mask ──
    vec3 toPos = worldPos - DomainCenter;
    float dist = length(toPos);
    float boundarySoftness = max(1.0, DomainRadius * 0.08);
    float domainMask = 1.0 - smoothstep(DomainRadius - boundarySoftness, DomainRadius + boundarySoftness, dist);

    // Inner safe zone keeps the caster readable during the startup pose.
    // 内圈安全区：半径 4 格内无效果 + 1 格羽化过渡（玩家视野清晰）
    float innerRadius = min(4.0, DomainRadius * 0.28);
    float innerMask = smoothstep(innerRadius - 0.8, innerRadius + 0.2, dist);
    domainMask *= innerMask;

    if (domainMask <= 0.001) {
        fragColor = vec4(sceneColor, 1.0);
        return;
    }

    // ═══════════════════════════════════════════════════════════
    // 3D GRID on geometry via depth reconstruction
    // ═══════════════════════════════════════════════════════════
    float cellSize = 1.2;
    vec3 gridCoord = (worldPos - DomainCenter) / cellSize;
    vec3 cellFrac = fract(gridCoord + 0.5) - 0.5;
    vec3 cellIdx = floor(gridCoord + 0.5);

    float distX = 0.5 - abs(cellFrac.x);
    float distY = 0.5 - abs(cellFrac.y);
    float distZ = 0.5 - abs(cellFrac.z);
    float wallDist = min(min(distX, distY), distZ);

    // ── Grid spread animation ──
    // All phases scale proportionally with LifetimeSeconds (from Java LIFETIME_TICKS)
    float expansionDuration = LifetimeSeconds * 0.50;
    float t = clamp(ScanTime / expansionDuration, 0.0, 1.0);
    float frontProgress = 1.0 - (1.0 - t) * (1.0 - t); // easeOutQuad
    float frontDist = frontProgress * DomainRadius;

    // Per-cell activation: cell at normalized distance d activates when frontProgress >= d
    float cellCenterDist = length(vec3(cellIdx) * cellSize);
    float cellNormDist = cellCenterDist / max(DomainRadius, 0.01);
    float cellProgress = clamp((frontProgress - cellNormDist * 0.7) * 4.0, 0.0, 1.0);
    cellProgress = cellProgress * cellProgress; // sharpen

    // ── Expansion wavefront (sweep stripe) highlight ──
    float frontWidth = cellSize * 1.8;
    float frontRing = exp(-abs(dist - frontDist) / frontWidth) * 0.7;
    float sharpFront = exp(-abs(dist - frontDist) * 4.0) * 0.5;
    float expansionFront = frontRing + sharpFront;

    // ═══════════════════════════════════════════════════════════
    // GRID WALL — the field locks in place during the charge
    // ═══════════════════════════════════════════════════════════
    float baseWallWidth = 0.008;
    float baseWallSoft  = 0.028;

    float wallFocus = 1.0 - smoothstep(0.78, 1.0, lifeProgress) * 0.22;
    float wallWidth = baseWallWidth * wallFocus;
    float wallSoft  = max(baseWallSoft * wallFocus, 0.001);
    float wallMask = 1.0 - smoothstep(wallWidth, wallWidth + wallSoft, wallDist);
    wallMask *= cellProgress; // walls appear as cells activate

    // Sweep stripe: bright flash at the expansion front only
    float distToFront = dist - frontDist;
    float sweepFlash = exp(-max(-distToFront, 0.0) / (cellSize * 1.5));
    sweepFlash *= step(distToFront, cellSize * 2.0) * cellProgress;

    // Intersection boost at grid corners
    float nearX = 1.0 - smoothstep(wallWidth, wallWidth + wallSoft, distX);
    float nearY = 1.0 - smoothstep(wallWidth, wallWidth + wallSoft, distY);
    float nearZ = 1.0 - smoothstep(wallWidth, wallWidth + wallSoft, distZ);
    float intersection = nearX + nearY + nearZ;
    float intersectionBoost = 1.0 + max(0.0, intersection - 0.6) * 0.7;

    // ═══════════════════════════════════════════════════════════
    // CELL INTERIOR — gray-white with dark blue corruption near walls
    // ═══════════════════════════════════════════════════════════
    float interiorDist = wallDist - wallWidth;
    float centerWeight = smoothstep(0.0, cellSize * 0.25, interiorDist);

    // ── Vergil palette: cold steel → deep royal blue ──
    vec3 coldSteel   = vec3(0.42, 0.44, 0.48);  // 冷钢银
    vec3 coldCenter  = vec3(0.52, 0.54, 0.58);  // 格子中心微亮
    vec3 royalBlue   = vec3(0.05, 0.12, 0.40);  // 深皇家蓝 (Vergil)
    vec3 indigoEdge  = vec3(0.08, 0.04, 0.25);  // 靛蓝侵蚀 (魔气)

    float corruptWeight = (1.0 - centerWeight) * 0.45;
    vec3 interiorColor = mix(coldSteel, indigoEdge, corruptWeight);
    interiorColor = mix(interiorColor, coldCenter, centerWeight * 0.5);

    // Cold steel → royal blue starting at 45% lifetime
    float colorShiftStart = LifetimeSeconds * 0.45;
    float colorShiftDuration = LifetimeSeconds * 0.3;
    float colorShift = smoothstep(0.0, colorShiftDuration, ScanTime - colorShiftStart);
    interiorColor = mix(interiorColor, royalBlue, colorShift * 0.8);

    // Wall color: blue → quickly syncs to interior after cell activates
    // 网格线颜色：蔓延后短时间内即与方格内部颜色同步
    float wallSync = smoothstep(0.2, 0.7, cellProgress);
    vec3 wallColor = mix(TintColor * 0.85, interiorColor, wallSync);

    // Gray-white replacement is part of the field and fades with the startup handoff.
    float grayZone = (1.0 - wallMask) * cellProgress * domainMask * fieldPresence;
    // Sharp threshold activation, then lock at max
    float grayReplace = smoothstep(0.3, 0.7, grayZone) * 0.95;
    vec3 grayScene = mix(sceneColor, interiorColor, clamp(grayReplace, 0.0, 1.0));

    // Subtle darkening for atmosphere
    grayScene *= 1.0 - domainMask * 0.08;

    // ═══════════════════════════════════════════════════════════
    // DESTRUCTION CRACKS — 破坏裂痕：方块表面碎裂效果
    // ═══════════════════════════════════════════════════════════
    // Multi-layer crack noise for sharp, angular fracture lines
    float crackN1 = noise3D(worldPos * 12.0 + vec3(0.0, 0.0, animatedTime * 0.15));
    float crackN2 = noise3D(worldPos * 7.0  + vec3(animatedTime * 0.1, 0.0, 0.0));
    float crackN3 = noise3D(worldPos * 5.0  + vec3(0.0, animatedTime * 0.08, 0.0));
    // Combine and threshold for sharp crack edges
    float crackRaw = max(max(crackN1 * 0.7, crackN2 * 0.5), crackN3 * 0.4);
    float crackMask = smoothstep(0.46, 0.52, crackRaw); // thin sharp lines
    crackMask += smoothstep(0.53, 0.56, crackRaw) * 0.6; // secondary thinner cracks
    crackMask = clamp(crackMask, 0.0, 1.0);
    // Only in cell interiors (not on grid walls), only in activated cells
    crackMask *= (1.0 - wallMask) * cellProgress;
    // Intensify as domain matures
    crackMask *= 0.3 + colorShift * 0.5;
    vec3 crackColor = vec3(0.01, 0.01, 0.06); // near-black void

    // ═══════════════════════════════════════════════════════════
    // YAMATO SLASH CRACKS — 树杈刀割裂痕，角向连续，径向分叉
    // ═══════════════════════════════════════════════════════════
    float azimuth = atan(toPos.z, toPos.x);
    float normDist = dist / max(DomainRadius, 0.01);

    // Angular noise field: varies mostly with angle, slightly with radius for branching
    float angNx = sin(azimuth) * 5.0;
    float angNz = cos(azimuth) * 5.0;
    // Radial perturbation for branch points (small, smooth)
    float radPerturb = normDist * 2.5;
    // Height adds slight variation for 3D surfaces
    float hPerturb = toPos.y * 0.3;

    // Crack density field — continuous along angle, slight variation along radius
    float crackField = noise3D(vec3(angNx, radPerturb + hPerturb, angNz));

    // Second field at different frequency for branch splits
    float crackField2 = noise3D(vec3(angNx * 1.4 + 3.0, radPerturb * 0.8 + 1.5, angNz * 1.4 + 5.0));

    // Third field for fine twigs
    float crackField3 = noise3D(vec3(angNx * 2.3 + 7.0, radPerturb * 0.5 + 3.0, angNz * 2.3 + 2.0));

    // Crack width: wider near center, razor-thin at tips
    float crackWidth = mix(0.05, 0.01, normDist * normDist);

    // Map field values to sharp crack lines using abs() + narrow smoothstep
    // Each threshold value = one radial crack line
    float bladeEdge = 0.0;
    // Main cracks (wider)
    bladeEdge = max(bladeEdge, 1.0 - smoothstep(0.0, crackWidth, abs(crackField - 0.35)));
    bladeEdge = max(bladeEdge, 1.0 - smoothstep(0.0, crackWidth, abs(crackField - 0.60)));
    bladeEdge = max(bladeEdge, 1.0 - smoothstep(0.0, crackWidth, abs(crackField - 0.82)));
    // Branch cracks (medium)
    bladeEdge = max(bladeEdge, 1.0 - smoothstep(0.0, crackWidth * 0.7, abs(crackField2 - 0.25)));
    bladeEdge = max(bladeEdge, 1.0 - smoothstep(0.0, crackWidth * 0.7, abs(crackField2 - 0.55)));
    bladeEdge = max(bladeEdge, 1.0 - smoothstep(0.0, crackWidth * 0.7, abs(crackField2 - 0.78)));
    // Fine twigs (thinnest)
    bladeEdge = max(bladeEdge, 1.0 - smoothstep(0.0, crackWidth * 0.4, abs(crackField3 - 0.30)));
    bladeEdge = max(bladeEdge, 1.0 - smoothstep(0.0, crackWidth * 0.4, abs(crackField3 - 0.65)));
    bladeEdge = max(bladeEdge, 1.0 - smoothstep(0.0, crackWidth * 0.4, abs(crackField3 - 0.90)));
    bladeEdge = min(bladeEdge, 1.0);

    // Glow halo around cracks
    float bladeGlow = 0.0;
    bladeGlow = max(bladeGlow, exp(-abs(crackField - 0.35) / (crackWidth * 3.5)));
    bladeGlow = max(bladeGlow, exp(-abs(crackField - 0.60) / (crackWidth * 3.5)));
    bladeGlow = max(bladeGlow, exp(-abs(crackField - 0.82) / (crackWidth * 3.5)));
    bladeGlow *= 0.55;

    // Only behind expansion front, in cell interiors
    bladeEdge  *= (1.0 - wallMask) * cellProgress;
    bladeGlow  *= (1.0 - wallMask) * cellProgress;

    // Hide near player and fade at boundary
    float crackFade = smoothstep(3.0, 5.0, dist)
                    * (1.0 - smoothstep(DomainRadius * 0.9, DomainRadius, dist));
    bladeEdge *= crackFade;
    bladeGlow *= crackFade;

    // Brightness grows as domain matures
    float bladeBrightness = smoothstep(0.22, 0.72, lifeProgress) * 0.28;

    vec3 bladeEdgeColor = vec3(0.55, 0.60, 0.85);
    vec3 bladeGlowColor = vec3(0.45, 0.50, 0.90);

    // ═══════════════════════════════════════════════════════════
    // DARK VEINS — 魔气黑丝在网格壁上蔓延
    // ═══════════════════════════════════════════════════════════
    float veinNoise = noise3D(worldPos * 8.0 + vec3(0.0, animatedTime * 0.3, 0.0));
    float veinMask = smoothstep(0.45, 0.6, veinNoise) * wallMask * 0.5;
    vec3 veinColor = vec3(0.01, 0.03, 0.12); // dark navy

    // ═══════════════════════════════════════════════════════════
    // RISING CORRUPTION WISPS — 上升的魔气絮丝
    // ═══════════════════════════════════════════════════════════
    float heightFade = 1.0 - smoothstep(0.0, DomainRadius * 0.5, abs(toPos.y));
    vec3 wispCoord = worldPos * 2.2;
    wispCoord.y -= animatedTime * SweepSpeed * 1.4;
    float wispNoise = noise3D(wispCoord);
    float wispNoise2 = noise3D(wispCoord * 1.7 + vec3(3.0, 1.5, 0.0));
    float wisps = wispNoise * wispNoise2;
    wisps = smoothstep(0.58, 0.72, wisps) * smoothstep(0.12, 0.32, abs(fract(wispCoord.y * 0.5) - 0.5));
    wisps *= heightFade * cellProgress;

    // ═══════════════════════════════════════════════════════════
    // EDGE GLOW — 领域边界
    // ═══════════════════════════════════════════════════════════
    float edgeDist = abs(dist - DomainRadius);
    float edgeGlow = exp(-edgeDist * 2.5) * 0.5 * smoothstep(0.0, 1.0, domainMask);
    float edgePulse = sin(animatedTime * ScanFrequency * 1.4 + dist * 0.4) * 0.12 + 0.88;
    edgeGlow *= edgePulse;

    // Outer dark ring just inside the boundary — dark blue demonic aura
    float darkRingDist = abs(dist - DomainRadius * 0.92);
    float darkRing = exp(-darkRingDist * 3.5) * 0.25 * domainMask;
    vec3 darkRingColor = vec3(0.01, 0.02, 0.10); // dark navy aura

    // ═══════════════════════════════════════════════════════════
    // COMPOSITE — the entire startup field hands off cleanly
    // ═══════════════════════════════════════════════════════════
    float blueFade = fieldPresence * TintAlpha / 0.58;
    vec3 result = grayScene;

    // Fine spatial stress marks, kept deliberately quiet before the attack.
    result = mix(result, crackColor, crackMask * blueFade * 0.28);

    // Yamato blade cracks: edge + glow halo, brightens over time
    result = mix(result, bladeEdgeColor, bladeEdge * bladeBrightness * blueFade * 0.8);
    result += bladeGlowColor * bladeGlow * bladeBrightness * blueFade * 0.55;

    // Dark veins on grid walls (fading)
    result = mix(result, veinColor, veinMask * blueFade * 0.7);

    // Dark ring (fading)
    result = mix(result, darkRingColor, darkRing * blueFade);

    // Blue sweep flash at expansion front (brief bright stripe)
    result = mix(result, TintColor * 0.95, sweepFlash * intersectionBoost * domainMask * blueFade * 0.8);

    // Grid walls remain locked until the handoff fade.
    result = mix(result, wallColor, wallMask * intersectionBoost * domainMask * 0.7 * fieldPresence);

    // Corruption wisps — indigo mist (fading)
    result += vec3(0.04, 0.02, 0.15) * wisps * domainMask * 0.5 * blueFade;

    // Wall glow bloom (fading)
    result += GlowColor * 0.2 * wallMask * domainMask * blueFade;

    // Edge barrier glow (fading)
    result = mix(result, GlowColor * 1.6 * blueFade, edgeGlow);
    result += GlowColor * 0.45 * edgeGlow * edgePulse * blueFade;

    // Expansion wavefront — only visible during expansion (t < 1.0)
    float frontFade = (1.0 - t) * blueFade; // fades after expansion complete
    result += GlowColor * 0.6 * expansionFront * domainMask * frontFade;

    // Subtle ambient blue radiance (fading)
    float ambientGlow = (1.0 - dist / DomainRadius) * 0.07 * domainMask;
    ambientGlow *= 0.7 + 0.3 * sin(animatedTime * ScanFrequency);
    result += GlowColor * 0.18 * ambientGlow * blueFade;

    float effectOpacity = 0.72;
    result = mix(sceneColor, result, effectOpacity);
    fragColor = vec4(result, 1.0);
}
