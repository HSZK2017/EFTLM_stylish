#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 OutSize;
uniform float Contrast;
uniform float Brightness;
uniform float Time;
uniform float Intensity;
uniform float Speed;
uniform float Mode;
uniform vec3 ColorDark;
uniform vec3 ColorLight;
uniform float ImpactThreshold;
uniform float ImpactThresholdLerp;
uniform float InvertFactor;
uniform float CappedGrayscale;
uniform vec2 FocalUV;
uniform float FocalVisibility;
uniform float RadialStrength;
uniform int Samples;
uniform float ChromaticStrength;
uniform float LensDistortStrength;

in vec2 texCoord;
out vec4 fragColor;

const float PI = 3.14159265359;

float saturate(float value) {
    return clamp(value, 0.0, 1.0);
}

float luminance(vec3 color) {
    return dot(color, vec3(0.299, 0.587, 0.114));
}

float aspectRatio() {
    return OutSize.x / max(OutSize.y, 1.0);
}

vec2 rotate2D(vec2 value, float angle) {
    float sine = sin(angle);
    float cosine = cos(angle);
    return vec2(cosine * value.x - sine * value.y, sine * value.x + cosine * value.y);
}

vec2 aspectDelta(vec2 uv) {
    vec2 delta = uv - FocalUV;
    delta.x *= aspectRatio();
    return delta;
}

vec2 aspectToUV(vec2 delta) {
    delta.x /= max(aspectRatio(), 0.001);
    return delta;
}

vec2 impactCoordinates(vec2 uv) {
    vec2 coordinates = rotate2D(aspectDelta(uv), -0.12);
    return coordinates * vec2(0.80, 1.18);
}

float impactRadius(vec2 coordinates) {
    float angle = atan(coordinates.y, coordinates.x);
    float brokenContour = 1.0
            + sin(angle * 3.0 + 0.75) * 0.050
            + sin(angle * 7.0 - 1.10) * 0.026
            + sin(angle * 11.0 + 1.85) * 0.014;
    return length(coordinates) * brokenContour;
}

float gaussianBand(float value, float center, float width) {
    float normalized = (value - center) / max(width, 0.0001);
    return exp(-normalized * normalized);
}

float arcFragments(float angle, float time) {
    float structure = sin(angle * 3.0 + 0.70) * 0.48
            + sin(angle * 7.0 - 1.45) * 0.31
            + sin(angle * 13.0 + 0.35 + time * Speed * 1.8) * 0.21;
    float fragments = smoothstep(-0.22, 0.38, structure);
    float broadSections = 0.74 + 0.26 * smoothstep(-0.60, 0.22, sin(angle * 2.0 - 0.40));
    return mix(0.16, 1.0, fragments) * broadSections;
}

vec3 sampleChromatic(vec2 uv, vec3 centerSample, float strength) {
    if (strength <= 0.0001) {
        return centerSample;
    }

    vec2 delta = aspectDelta(uv);
    float distanceFromFocal = length(delta);
    vec2 direction = distanceFromFocal > 0.0001 ? delta / distanceFromFocal : vec2(1.0, 0.0);
    float edgeFactor = smoothstep(0.025, 0.92, distanceFromFocal);
    vec2 offset = aspectToUV(direction * strength * edgeFactor);

    return vec3(
            texture(DiffuseSampler, clamp(uv + offset, vec2(0.0), vec2(1.0))).r,
            centerSample.g,
            texture(DiffuseSampler, clamp(uv - offset, vec2(0.0), vec2(1.0))).b
    );
}

vec3 sampleRadialStreak(vec2 uv, vec3 centerSample, float strength) {
    if (strength <= 0.0001) {
        return centerSample;
    }

    vec2 ray = uv - FocalUV;
    vec2 offset = ray * strength;
    vec3 nearSample = texture(DiffuseSampler, clamp(uv - offset * 0.32, vec2(0.0), vec2(1.0))).rgb;
    vec3 farSample = texture(DiffuseSampler, clamp(uv - offset * 0.72, vec2(0.0), vec2(1.0))).rgb;
    vec3 reboundSample = texture(DiffuseSampler, clamp(uv + offset * 0.12, vec2(0.0), vec2(1.0))).rgb;
    return centerSample * 0.52 + nearSample * 0.25 + farSample * 0.15 + reboundSample * 0.08;
}

float bicolorFactor(vec3 color, float additionalInvert) {
    float luma = luminance(color) / max(CappedGrayscale, 0.001);
    luma = pow(saturate(luma), 1.0 / max(Contrast, 0.001));
    float thresholdWidth = max(ImpactThresholdLerp, 0.001);
    float factor = smoothstep(
            ImpactThreshold - thresholdWidth,
            ImpactThreshold + thresholdWidth,
            luma
    );
    return mix(factor, 1.0 - factor, saturate(InvertFactor + additionalInvert));
}

float screenEdgeMask(vec2 uv) {
    vec2 edgeDistance = abs(uv * 2.0 - 1.0);
    return smoothstep(0.50, 1.0, max(edgeDistance.x, edgeDistance.y));
}

void main() {
    vec4 original = texture(DiffuseSampler, texCoord);
    float time = saturate(Time);
    float heavy = step(0.5, Mode);
    float intensity = max(Intensity, 0.0);
    float focalVisibility = saturate(FocalVisibility);

    float impactWindow = mix(0.36, 0.48, heavy);
    float impact = 1.0 - smoothstep(0.0, impactWindow, time);
    float burst = exp(-time * mix(25.0, 16.0, heavy));
    float aftershockOffset = (time - 0.31) / 0.085;
    float aftershock = heavy * exp(-aftershockOffset * aftershockOffset);

    float cutoutIn = smoothstep(0.018, 0.095, time);
    float cutoutOut = 1.0 - smoothstep(mix(0.48, 0.70, heavy), 0.97, time);
    float cutout = cutoutIn * cutoutOut;

    float flickerEnd = mix(0.52, 0.70, heavy);
    float flickerProgress = saturate(time / max(flickerEnd, 0.001));
    float flickerIn = smoothstep(0.025, 0.10, time);
    float flickerOut = 1.0 - smoothstep(flickerEnd * 0.74, flickerEnd, time);
    float flickerEnvelope = flickerIn * flickerOut;
    float flickerWave = step(0.0, sin(flickerProgress * PI * mix(8.0, 6.0, heavy)));

    vec2 coordinates = impactCoordinates(texCoord);
    float radius = impactRadius(coordinates);
    float angle = atan(coordinates.y, coordinates.x);
    float fragments = arcFragments(angle, time);

    float frontDuration = mix(0.68, 0.90, heavy);
    float frontProgress = saturate(time / frontDuration);
    float frontEase = 1.0 - pow(1.0 - frontProgress, mix(2.25, 2.65, heavy));
    float frontRadius = mix(0.025, mix(0.86, 1.10, heavy), frontEase);
    float frontWidth = mix(0.035, 0.052, heavy) * (1.0 + frontProgress * 0.38);
    float frontEnvelope = smoothstep(0.012, 0.070, time)
            * (1.0 - smoothstep(frontDuration * 0.62, min(frontDuration + 0.08, 0.99), time));
    float frontBand = gaussianBand(radius, frontRadius, frontWidth);
    float frontEnergy = frontBand * fragments * frontEnvelope;
    float wakeBand = gaussianBand(radius, max(frontRadius - frontWidth * 2.1, 0.0), frontWidth * 2.4);
    float outerBand = gaussianBand(radius, frontRadius + frontWidth * 1.35, frontWidth * 1.55);

    float secondaryStart = 0.14;
    float secondaryProgress = saturate((time - secondaryStart) / 0.68);
    float secondaryEase = 1.0 - pow(1.0 - secondaryProgress, 2.15);
    float secondaryRadius = mix(0.035, 0.90, secondaryEase);
    float secondaryWidth = 0.035 + secondaryProgress * 0.020;
    float secondaryEnvelope = heavy * smoothstep(secondaryStart, secondaryStart + 0.075, time)
            * (1.0 - smoothstep(0.72, 0.98, time));
    float secondaryBand = gaussianBand(radius, secondaryRadius, secondaryWidth);
    float secondaryFragments = arcFragments(angle + 0.65, time * 0.72);
    float secondaryEnergy = secondaryBand * secondaryFragments * secondaryEnvelope;

    float coreProgress = saturate(time / mix(0.28, 0.38, heavy));
    float coreEase = 1.0 - pow(1.0 - coreProgress, 2.0);
    float coreRadius = mix(0.018, mix(0.10, 0.16, heavy), coreEase);
    float coreShape = 1.0 - smoothstep(coreRadius * 0.36, coreRadius, radius);
    float coreRim = gaussianBand(radius, coreRadius, max(0.009, coreRadius * 0.16))
            * mix(0.30, 1.0, arcFragments(angle + 0.38, time * 0.55));
    float coreEnvelope = focalVisibility * impact * (1.0 - smoothstep(0.46, 0.72, time));

    vec2 focalDelta = aspectDelta(texCoord);
    float focalDistance = length(focalDelta);
    vec2 warpDirection = focalDistance > 0.0001 ? focalDelta / focalDistance : vec2(0.0);
    float warpSignal = frontEnergy * 0.090
            + wakeBand * frontEnvelope * 0.018
            + secondaryEnergy * 0.055
            + coreShape * impact * 0.012;
    float warpAmount = LensDistortStrength * intensity * focalVisibility * warpSignal;
    vec2 sampleUV = clamp(texCoord + aspectToUV(warpDirection * warpAmount), vec2(0.0), vec2(1.0));
    vec3 color = texture(DiffuseSampler, sampleUV).rgb;

    float chromatic = min(max(ChromaticStrength, 0.0), 0.025) * intensity * focalVisibility
            * (frontEnergy * 0.95 + secondaryEnergy * 0.62 + impact * 0.10 + aftershock * 0.18);
    color = sampleChromatic(sampleUV, color, chromatic);

    float sampleScale = clamp(float(Samples) / 16.0, 0.5, 1.5);
    float radial = (abs(RadialStrength) * 1.15 + 0.004) * sampleScale * intensity * focalVisibility
            * (frontEnergy + secondaryEnergy * 0.68 + wakeBand * frontEnvelope * 0.12);
    color = sampleRadialStreak(sampleUV, color, radial);

    float contrastCurve = impact * 0.32 + cutout * 0.68 + aftershock * 0.14
            + frontEnergy * focalVisibility * 0.16;
    float contrastStrength = 1.0 + (max(Contrast, 1.0) - 1.0) * contrastCurve;
    color = (color - 0.5) * contrastStrength + 0.5;
    color += vec3((Brightness - 0.5) * 0.34 * cutout + impact * 0.085);

    vec3 bicolor = mix(
            ColorDark,
            ColorLight,
            bicolorFactor(color, flickerWave * flickerEnvelope + aftershock * 0.22)
    );
    float bicolorBlend = saturate(cutout * intensity * mix(0.80, 0.95, heavy));
    color = mix(color, bicolor, bicolorBlend);

    float whiteFlash = burst * intensity * mix(0.76, 0.88, heavy)
            + (1.0 - flickerWave) * flickerEnvelope * intensity * 0.18;
    color = mix(color, vec3(1.0), saturate(whiteFlash));

    float pressureShadow = focalVisibility * intensity
            * (wakeBand * frontEnvelope * mix(0.10, 0.16, heavy)
            + outerBand * frontEnvelope * mix(0.08, 0.14, heavy)
            + secondaryBand * secondaryEnvelope * 0.07);
    color *= 1.0 - saturate(pressureShadow);

    float rupturePattern = 0.78 + 0.22 * sin(angle * 5.0 + radius * 68.0 - time * Speed * 4.0);
    float coreDarkness = coreShape * coreEnvelope * intensity * rupturePattern * mix(0.52, 0.76, heavy);
    color = mix(color, ColorDark, saturate(coreDarkness));

    float shellLight = focalVisibility * intensity
            * (frontEnergy * mix(0.42, 0.56, heavy)
            + secondaryEnergy * 0.36
            + coreRim * coreEnvelope * mix(0.32, 0.48, heavy));
    vec3 shellColor = mix(ColorLight, vec3(1.0), 0.45);
    color = mix(color, shellColor, saturate(shellLight));

    float edgeCrush = screenEdgeMask(texCoord) * intensity
            * (impact * 0.09 + cutout * mix(0.18, 0.25, heavy) + aftershock * 0.12);
    color *= 1.0 - edgeCrush;

    fragColor = vec4(clamp(color, 0.0, 1.0), original.a);
}
