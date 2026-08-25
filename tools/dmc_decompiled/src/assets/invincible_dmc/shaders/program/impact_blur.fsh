#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 center;

uniform float intensity;
uniform float strength;
uniform int samples;

in vec2 texCoord;
out vec4 fragColor;

void main()
{
    vec2 dir = texCoord - center;
    float dist = length(dir);
    vec2 ndir = (dist > 0.00001) ? dir / dist : vec2(0.0);

    float distFactor = smoothstep(0.0, 0.8, dist);
    float blurBoost = mix(0.2, 1.5, distFactor);
    float blast = 1.0 - intensity;
    float core = 1.0 - smoothstep(0.0, 0.4, dist);

    float centerProtect = smoothstep(0.0, 0.2, dist);
    float blurScale = strength * blast * blurBoost * distFactor * (1.0 + core * 1.5) * centerProtect;
    vec2 offset = ndir * blurScale;

    vec4 color = texture(DiffuseSampler, texCoord);
    float total = 1.0;

    for (int i = 1; i <= samples; i++){
        float t = float(i) / float(samples);
        float weight = pow(1.0 - t, 2.0);
        vec2 uv = texCoord - offset * t;
        uv = clamp(uv, vec2(0.001), vec2(0.999));
        color += texture(DiffuseSampler, uv) * weight;
        total += weight;
    }
    color /= total;

    float chromaFade = blast * smoothstep(0.05, 0.75, dist);
    vec2 chromaOffset = ndir * mix(0.0015, 0.003, distFactor) * chromaFade;
    vec3 chromaColor = color.rgb;
    chromaColor.r = texture(DiffuseSampler, clamp(texCoord + chromaOffset, vec2(0.001), vec2(0.999))).r;
    chromaColor.b = texture(DiffuseSampler, clamp(texCoord - chromaOffset, vec2(0.001), vec2(0.999))).b;
    color.rgb = mix(color.rgb, chromaColor, 0.35 * chromaFade);


    float blueIntensity = 1.0 - intensity * 0.7;
    float rangeFactor = smoothstep(0.0, 0.65, dist);
    float centerClear = smoothstep(0.0, 0.03, dist);
    rangeFactor = mix(0.3, 1.0, rangeFactor) * centerClear;

    float alpha = 0.82 * rangeFactor;
    alpha = clamp(alpha, 0.0, 1.0);

    vec3 impactColor = vec3(0.015, 0.035, 0.42);
    float brightness = 0.65 + 0.35 * rangeFactor;
    impactColor *= brightness;

    vec3 blueOverlay = impactColor * alpha * blueIntensity;
    color.rgb += blueOverlay;

    float tint = alpha * 0.28 * blueIntensity;
    color.r *= (1.0 - tint * 0.65);
    color.g *= (1.0 - tint * 0.3);
    color.b *= (1.0 + tint * 0.7);

    float bloodBand = smoothstep(0.04, 0.2, dist)
            * (1.0 - smoothstep(0.55, 0.95, dist));
    float bloodTint = alpha * blueIntensity * bloodBand * (0.055 + core * 0.035);
    vec3 bloodColor = vec3(0.3, 0.012, 0.02);

    color.rgb += bloodColor * bloodTint;
    color.r *= (1.0 + bloodTint * 0.8);
    color.g *= (1.0 - bloodTint * 0.18);
    color.b *= (1.0 - bloodTint * 0.1);

    color.rgb = clamp(color.rgb, 0.0, 1.0);

    fragColor = vec4(color.rgb, 1.0);
}
