#version 150

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler1;
uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 PoseMat;
uniform mat4 InversePoseMat;

uniform float GameTime;
uniform float TurbulenceStrength;
uniform float FlowSpeed;

out vec4 vertexColor;
out vec4 lightMapColor;
out vec4 overlayColor;
out vec2 texCoord0;
out float turbulenceAmount;
out float bladeAmount;
out float phaseOffset;

void main() {
    const float TAU = 6.28318530718;

    float time = GameTime * 24000.0 * FlowSpeed;
    float instancePhase = Color.r * TAU;
    vec3 localPosition = (InversePoseMat * vec4(Position, 1.0)).xyz;
    float bladeMask = smoothstep(-0.03, 0.18, localPosition.y);
    float tipAmount = smoothstep(0.06, 0.82, localPosition.y);
    float tipBoost = mix(0.48, 1.55, tipAmount);

    float primaryWave = sin(
        localPosition.y * 13.5
        + time * 2.8
        + instancePhase
        + localPosition.z * 10.0
    );
    float crossWave = sin(
        localPosition.y * 31.0
        - time * 4.4
        + localPosition.x * 24.0
        + instancePhase * 1.73
    );
    float rippleWave = sin(
        localPosition.y * 78.0
        + time * 7.6
        + localPosition.z * 31.0
        - instancePhase * 2.1
    );
    float spiralWave = cos(
        localPosition.y * 21.0
        - time * 3.6
        + localPosition.x * 18.0
        - localPosition.z * 12.0
        + instancePhase
    );

    float turbulence = primaryWave * 0.48
        + crossWave * 0.28
        + rippleWave * 0.15
        + spiralWave * 0.09;
    float displacement = TurbulenceStrength
        * bladeMask
        * tipBoost
        * 1.55;

    vec3 displacedLocalPosition = localPosition;
    displacedLocalPosition.x += (turbulence + spiralWave * 0.22) * displacement;
    displacedLocalPosition.z += (
        crossWave * 0.70
        - primaryWave * 0.32
        + rippleWave * 0.18
    ) * displacement * 0.90;
    displacedLocalPosition.y += (
        rippleWave * 0.12
        + spiralWave * 0.08
    ) * displacement;

    vec3 displacedPosition = (PoseMat * vec4(displacedLocalPosition, 1.0)).xyz;
    gl_Position = ProjMat * ModelViewMat * vec4(displacedPosition, 1.0);
    vertexColor = vec4(1.0, 1.0, 1.0, Color.a);

    lightMapColor = texelFetch(Sampler2, UV2 / 16, 0);
    overlayColor = texelFetch(Sampler1, UV1, 0);
    texCoord0 = UV0;
    turbulenceAmount = clamp(abs(turbulence) * bladeMask, 0.0, 1.0);
    bladeAmount = bladeMask;
    phaseOffset = instancePhase;
}
