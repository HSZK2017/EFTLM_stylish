#version 150

#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat3 IViewRotMat;
uniform int FogShape;

out float vertexDistance;
out vec2 texCoord0;
out vec4 edgeTint;

void main() {
    float level = clamp(Color.a, 0.0, 1.0);
    float outlineWidth = mix(0.00675, 0.027, level);
    vec3 outwardNormal = -normalize(Normal);
    vec3 expandedPosition = Position + outwardNormal * outlineWidth;
    vec4 viewPosition = ModelViewMat * vec4(expandedPosition, 1.0);
    gl_Position = ProjMat * viewPosition;

    vertexDistance = fog_distance(ModelViewMat, IViewRotMat * expandedPosition, FogShape);
    texCoord0 = UV0;
    edgeTint = Color;
}
