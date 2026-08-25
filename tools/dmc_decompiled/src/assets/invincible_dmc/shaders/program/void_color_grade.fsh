#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
out vec4 fragColor;

float luminance(vec3 color) {
    return dot(color, vec3(0.2126, 0.7152, 0.0722));
}

void main() {
    vec4 source = texture(DiffuseSampler, texCoord);
    vec3 original = max(source.rgb, vec3(0.0));
    float sourceLuminance = luminance(original);
    float maximumChannel = max(max(original.r, original.g), original.b);
    float minimumChannel = min(min(original.r, original.g), original.b);
    float chroma = maximumChannel - minimumChannel;

    float highlightAmount = smoothstep(0.15, 0.85, sourceLuminance);
    vec3 coolScale = mix(
            vec3(0.52, 0.70, 1.10),
            vec3(0.78, 0.88, 1.03),
            highlightAmount
    );
    vec3 coolTone = vec3(sourceLuminance) * coolScale;
    float neutralAmount = 1.0 - smoothstep(0.08, 0.42, chroma);
    float gradeStrength = mix(0.26, 0.52, neutralAmount);
    vec3 graded = mix(original, coolTone, gradeStrength);

    float redWarmth = original.r - max(original.g, original.b);
    float yellowWarmth = min(original.r, original.g) - original.b;
    float warmMask = smoothstep(0.04, 0.30, max(redWarmth, yellowWarmth))
            * smoothstep(0.08, 0.45, chroma);
    vec3 preservedWarmColor = original * vec3(1.08, 1.02, 0.88);
    graded = mix(graded, preservedWarmColor, warmMask * 0.78);

    float targetLuminance = clamp(pow(max(sourceLuminance, 0.0), 0.84) * 1.10, 0.0, 1.0);
    graded *= targetLuminance / max(luminance(graded), 0.001);
    graded = (graded - vec3(0.5)) * 1.03 + vec3(0.5);

    fragColor = vec4(clamp(graded, 0.0, 1.0), source.a);
}
