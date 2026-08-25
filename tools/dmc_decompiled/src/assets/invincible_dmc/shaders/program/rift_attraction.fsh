#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 OutSize;
uniform vec2 LineStart;
uniform vec2 LineEnd;
uniform float Strength;
uniform float Radius;
uniform float Intensity;
uniform float StartTipFade;
uniform float EndTipFade;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 uv = texCoord;
    float aspect = OutSize.x / max(OutSize.y, 1.0);
    vec2 metricScale = vec2(aspect, 1.0);
    vec2 point = uv * metricScale;
    vec2 start = LineStart * metricScale;
    vec2 end = LineEnd * metricScale;
    vec2 segment = end - start;
    float segmentLengthSquared = max(dot(segment, segment), 0.000001);
    float along = clamp(dot(point - start, segment) / segmentLengthSquared, 0.0, 1.0);
    vec2 closest = start + segment * along;
    vec2 normal = normalize(vec2(-segment.y, segment.x));
    float signedDistance = dot(point - closest, normal);
    float distanceToRift = abs(signedDistance);

    float radial = 1.0 - smoothstep(0.0, Radius, distanceToRift);
    radial *= radial;
    float startMask = mix(1.0, smoothstep(0.0, 0.14, along), StartTipFade);
    float endMask = mix(1.0, smoothstep(0.0, 0.14, 1.0 - along), EndTipFade);
    float influence = radial * startMask * endMask * Intensity;

    float side = sign(signedDistance);
    vec2 outwardSampleOffset = normal * side * Strength * influence;
    outwardSampleOffset.x /= aspect;
    vec2 sampleUv = clamp(uv + outwardSampleOffset, vec2(0.001), vec2(0.999));

    fragColor = texture(DiffuseSampler, sampleUv);
}
