#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float GameTime;
uniform float UvDistortionStrength;
uniform float FlowSpeed;
uniform vec3 EnergyColor;
uniform vec3 SecondaryEnergyColor;
uniform vec3 CoreEnergyColor;
uniform float MagicVeinStrength;
uniform float EmissiveStrength;
uniform vec3 TintColor;
uniform float TintStrength;

in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;
in float turbulenceAmount;
in float bladeAmount;
in float phaseOffset;

out vec4 fragColor;

float hash21(vec2 point) {
    return fract(sin(dot(point, vec2(127.1, 311.7))) * 43758.5453123);
}

float valueNoise(vec2 point) {
    vec2 cell = floor(point);
    vec2 local = fract(point);
    vec2 curve = local * local * (3.0 - 2.0 * local);

    float bottomLeft = hash21(cell);
    float bottomRight = hash21(cell + vec2(1.0, 0.0));
    float topLeft = hash21(cell + vec2(0.0, 1.0));
    float topRight = hash21(cell + vec2(1.0, 1.0));

    return mix(
        mix(bottomLeft, bottomRight, curve.x),
        mix(topLeft, topRight, curve.x),
        curve.y
    );
}

float fbm(vec2 point) {
    float value = 0.0;
    float amplitude = 0.5;
    mat2 rotation = mat2(0.80, -0.60, 0.60, 0.80);

    for (int octave = 0; octave < 3; ++octave) {
        value += valueNoise(point) * amplitude;
        point = rotation * point * 2.03 + vec2(17.17, 9.23);
        amplitude *= 0.5;
    }

    return value;
}

void main() {
    float time = GameTime * 24000.0 * FlowSpeed;
    float localTime = time + phaseOffset * 0.37;
    vec2 noiseSpace = vec2(texCoord0.x * 9.0, texCoord0.y * 18.0);

    float warpNoiseX = fbm(
        noiseSpace
        + vec2(localTime * 0.35, -localTime * 0.85)
        + phaseOffset
    );
    float warpNoiseY = fbm(
        noiseSpace * 1.61
        + vec2(-localTime * 0.62, localTime * 0.38)
        + vec2(11.7 + phaseOffset, 4.3)
    );
    vec2 domainWarp = (vec2(warpNoiseX, warpNoiseY) - 0.5) * 2.0;

    float flowPhase = texCoord0.y * 86.0
        + localTime * 5.2
        + turbulenceAmount * 7.0
        + domainWarp.x * 9.0;
    float crossPhase = texCoord0.x * 63.0
        - localTime * 3.8
        + texCoord0.y * 19.0
        + domainWarp.y * 8.0;
    float fineRipple = sin(
        (texCoord0.x + texCoord0.y) * 128.0
        - localTime * 8.5
        + domainWarp.x * 11.0
    );

    vec2 waveWarp = vec2(sin(flowPhase), cos(crossPhase));
    waveWarp += vec2(fineRipple, -fineRipple) * 0.42;
    float distortionMask = mix(0.42, 1.20, bladeAmount);
    vec2 uvOffset = (
        domainWarp * 0.78
        + waveWarp * 0.55
    ) * UvDistortionStrength * distortionMask * 1.55 * 0.72;
    vec2 distortedUv = clamp(
        texCoord0 + uvOffset,
        vec2(0.001),
        vec2(0.999)
    );

    vec4 baseColor = texture(Sampler0, texCoord0);
    if (baseColor.a < 0.1) {
        discard;
    }

    vec4 warpedColor = texture(Sampler0, distortedUv);

    float alphaGuard = smoothstep(0.16, 0.72, baseColor.a);
    vec4 color = vec4(
        mix(baseColor.rgb, warpedColor.rgb, alphaGuard * 0.93),
        baseColor.a
    );

    float veinField = fbm(
        vec2(texCoord0.x * 13.0, texCoord0.y * 38.0)
        + vec2(domainWarp.x * 2.0, -localTime * 1.7)
        + phaseOffset
    );
    float magicVein = 1.0 - smoothstep(0.055, 0.20, abs(veinField - 0.52));
    float movingBand = pow(
        max(
            0.0,
            0.5 + 0.5 * sin(
                texCoord0.y * 110.0
                - localTime * 9.5
                + domainWarp.x * 11.0
                + phaseOffset
            )
        ),
        10.0
    );
    float sparkBand = pow(
        max(
            0.0,
            0.5 + 0.5 * sin(
                texCoord0.x * 71.0
                + texCoord0.y * 133.0
                + localTime * 13.0
                + warpNoiseY * 18.0
            )
        ),
        18.0
    );
    float magicField = max(magicVein * 0.78, movingBand);
    magicField = max(magicField, sparkBand * 0.70) * bladeAmount;

    float spectralMix = 0.5 + 0.5 * sin(
        localTime * 2.4
        + texCoord0.y * 20.0
        + domainWarp.y * 5.0
    );
    vec3 magicColor = mix(SecondaryEnergyColor, EnergyColor, spectralMix);
    magicColor = mix(magicColor, CoreEnergyColor, magicField * 0.68);

    color *= vertexColor * ColorModulator;
    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
    color.rgb *= mix(lightMapColor.rgb, vec3(1.0), EmissiveStrength);
    vec3 litMagicColor = magicColor * lightMapColor.rgb;
    color.rgb = mix(
        color.rgb,
        litMagicColor,
        clamp(magicField * MagicVeinStrength * 0.42, 0.0, 0.82)
    );
    color.rgb = mix(color.rgb, TintColor, TintStrength);

    fragColor = color;
}
