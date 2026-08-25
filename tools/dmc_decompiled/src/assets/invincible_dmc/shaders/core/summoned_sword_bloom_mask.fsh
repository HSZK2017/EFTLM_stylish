#version 150

uniform sampler2D Sampler0;
uniform float GameTime;

in vec2 texCoord0;
in vec4 bloomColor;

out vec4 fragColor;

void main() {
    vec4 baseColor = texture(Sampler0, texCoord0);
    if (baseColor.a < 0.1) {
        discard;
    }

    float time = GameTime * 24000.0;
    float primaryFlow = pow(
        0.5 + 0.5 * sin(texCoord0.y * 22.0 - time * 0.52),
        4.0
    );
    float secondaryFlow = pow(
        0.5 + 0.5 * sin(texCoord0.y * 43.0 - time * 0.91 + texCoord0.x * 5.0),
        7.0
    );
    float flowIntensity = 0.62 + primaryFlow * 0.95 + secondaryFlow * 0.38;

    float textureBrightness = max(max(baseColor.r, baseColor.g), baseColor.b);
    float bloomMask = baseColor.a
        * mix(0.72, 1.0, textureBrightness)
        * flowIntensity;
    fragColor = vec4(
        bloomColor.rgb * bloomMask,
        bloomColor.a * baseColor.a * clamp(flowIntensity, 0.45, 1.0)
    );
}
