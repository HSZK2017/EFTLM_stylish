#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float GameTime;

in float vertexDistance;
in vec2 texCoord0;
in vec4 edgeTint;

out vec4 fragColor;

void main() {
    if (!gl_FrontFacing) {
        discard;
    }

    vec4 mask = texture(Sampler0, texCoord0);
    if (mask.a < 0.05) {
        discard;
    }

    float level = clamp(edgeTint.a, 0.0, 1.0);
    float pulse = 0.92 + sin(GameTime * 3015.9289) * 0.08;
    float alpha = mask.a * mix(0.48, 0.92, level) * pulse;
    if (alpha < 0.01) {
        discard;
    }

    vec3 glowColor = edgeTint.rgb * mix(1.0, 1.55, level);
    vec4 color = vec4(glowColor, alpha) * ColorModulator;
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
