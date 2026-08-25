#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 OutSize;
uniform float Intensity;
uniform float Radius;
uniform float Softness;

in vec2 texCoord;
out vec4 fragColor;

float luminance(vec3 color) {
    return dot(color, vec3(0.299, 0.587, 0.114));
}

void main() {
    vec4 original = texture(DiffuseSampler, texCoord);
    vec2 delta = texCoord - vec2(0.5);
    delta.x *= OutSize.x / max(OutSize.y, 1.0);

    float distanceFromCenter = length(delta);
    float edge = smoothstep(
            max(Radius, 0.0),
            max(Radius, 0.0) + max(Softness, 0.001),
            distanceFromCenter
    );
    float pressure = clamp(Intensity, 0.0, 1.0) * edge;
    float darkness = pressure * (0.68 + edge * 0.24);

    vec3 shadowed = original.rgb * (1.0 - darkness);
    vec3 tintedGray = vec3(luminance(shadowed)) * vec3(0.86, 0.80, 0.96);
    vec3 color = mix(shadowed, tintedGray, pressure * 0.24);
    fragColor = vec4(max(color, vec3(0.0)), original.a);
}
