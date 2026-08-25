#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler3;
uniform sampler2D Sampler4;
uniform sampler2D Sampler5;
uniform sampler2D Sampler6;
uniform sampler2D Sampler7;
uniform sampler2D Sampler8;

uniform vec4 ColorModulator;
uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float NormalStrength;
uniform float NormalYSign;
uniform float ParallaxStrength;
uniform float EnvironmentStrength;
uniform float EmissiveStrength;
uniform int UsePackedMer;
uniform int HasNormalMap;
uniform int HasHeightMap;
uniform int HasAmbientOcclusion;

in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;
in vec3 modelPosition;
in vec3 modelNormal;
in vec3 surfaceViewDirection;

out vec4 fragColor;

const float PI = 3.14159265359;

mat3 tangentBasis() {
    vec3 geometricNormal = normalize(modelNormal);
    if (!gl_FrontFacing) {
        geometricNormal = -geometricNormal;
    }

    vec3 positionDx = dFdx(modelPosition);
    vec3 positionDy = dFdy(modelPosition);
    vec2 uvDx = dFdx(texCoord0);
    vec2 uvDy = dFdy(texCoord0);
    float determinant = uvDx.x * uvDy.y - uvDx.y * uvDy.x;

    if (abs(determinant) < 0.000001) {
        vec3 tangent = abs(geometricNormal.y) < 0.999
            ? normalize(cross(vec3(0.0, 1.0, 0.0), geometricNormal))
            : vec3(1.0, 0.0, 0.0);
        return mat3(tangent, normalize(cross(geometricNormal, tangent)), geometricNormal);
    }

    vec3 tangent = (positionDx * uvDy.y - positionDy * uvDx.y) / determinant;
    tangent -= geometricNormal * dot(geometricNormal, tangent);
    if (dot(tangent, tangent) < 0.000001) {
        tangent = abs(geometricNormal.y) < 0.999
            ? normalize(cross(vec3(0.0, 1.0, 0.0), geometricNormal))
            : vec3(1.0, 0.0, 0.0);
    } else {
        tangent = normalize(tangent);
    }

    vec3 rawBitangent = (positionDy * uvDx.x - positionDx * uvDy.x) / determinant;
    float handedness = dot(cross(geometricNormal, tangent), rawBitangent) < 0.0 ? -1.0 : 1.0;
    vec3 bitangent = normalize(cross(geometricNormal, tangent)) * handedness;
    return mat3(tangent, bitangent, geometricNormal);
}

vec2 parallaxTexCoord(mat3 basis, vec3 viewDirection) {
    if (HasHeightMap == 0) {
        return texCoord0;
    }

    vec4 heightSample = texture(Sampler6, texCoord0);
    if (heightSample.a < 0.001) {
        return texCoord0;
    }

    vec3 tangentView = transpose(basis) * viewDirection;
    float viewDepth = max(abs(tangentView.z), 0.35);
    vec2 offset = tangentView.xy / viewDepth;
    offset *= (heightSample.r - 0.5) * ParallaxStrength * heightSample.a;
    return clamp(texCoord0 - offset, vec2(0.001), vec2(0.999));
}

vec3 mappedNormal(mat3 basis, vec2 materialUv, float materialMask) {
    if (HasNormalMap == 0) {
        return normalize(basis[2]);
    }

    vec3 tangentNormal = texture(Sampler3, materialUv).rgb * 2.0 - 1.0;
    tangentNormal.xy *= NormalStrength;
    tangentNormal.y *= NormalYSign;
    tangentNormal = dot(tangentNormal, tangentNormal) > 0.000001
        ? normalize(tangentNormal)
        : vec3(0.0, 0.0, 1.0);
    tangentNormal = normalize(mix(vec3(0.0, 0.0, 1.0), tangentNormal, materialMask));
    return normalize(basis * tangentNormal);
}

float distributionGgx(vec3 normal, vec3 halfwayDirection, float roughness) {
    float alpha = roughness * roughness;
    float alphaSquared = alpha * alpha;
    float normalDotHalfway = max(dot(normal, halfwayDirection), 0.0);
    float denominator = normalDotHalfway * normalDotHalfway * (alphaSquared - 1.0) + 1.0;
    return alphaSquared / max(PI * denominator * denominator, 0.0001);
}

float geometrySchlickGgx(float normalDotDirection, float roughness) {
    float k = roughness + 1.0;
    k = k * k / 8.0;
    return normalDotDirection / max(normalDotDirection * (1.0 - k) + k, 0.0001);
}

float geometrySmith(vec3 normal, vec3 viewDirection, vec3 lightDirection, float roughness) {
    return geometrySchlickGgx(max(dot(normal, viewDirection), 0.0), roughness)
        * geometrySchlickGgx(max(dot(normal, lightDirection), 0.0), roughness);
}

vec3 fresnelSchlick(float cosine, vec3 reflectance) {
    return reflectance + (1.0 - reflectance) * pow(clamp(1.0 - cosine, 0.0, 1.0), 5.0);
}

vec3 fresnelSchlickRoughness(float cosine, vec3 reflectance, float roughness) {
    return reflectance
        + (max(vec3(1.0 - roughness), reflectance) - reflectance)
        * pow(clamp(1.0 - cosine, 0.0, 1.0), 5.0);
}

vec3 directLight(
        vec3 normal,
        vec3 viewDirection,
        vec3 lightDirection,
        vec3 radiance,
        vec3 albedo,
        float metallic,
        float roughness,
        vec3 reflectance) {
    lightDirection = normalize(lightDirection);
    vec3 halfwayDirection = normalize(viewDirection + lightDirection);
    float normalDotLight = max(dot(normal, lightDirection), 0.0);
    float normalDotView = max(dot(normal, viewDirection), 0.0);
    if (normalDotLight <= 0.0 || normalDotView <= 0.0) {
        return vec3(0.0);
    }

    float distribution = distributionGgx(normal, halfwayDirection, roughness);
    float geometry = geometrySmith(normal, viewDirection, lightDirection, roughness);
    vec3 fresnel = fresnelSchlick(max(dot(halfwayDirection, viewDirection), 0.0), reflectance);
    vec3 specular = distribution * geometry * fresnel
        / max(4.0 * normalDotView * normalDotLight, 0.0001);

    vec3 diffuseWeight = (vec3(1.0) - fresnel) * (1.0 - metallic);
    return (diffuseWeight * albedo / PI + specular) * radiance * normalDotLight;
}

vec3 proceduralEnvironment(vec3 reflectionDirection, float roughness) {
    vec3 groundColor = vec3(0.035, 0.032, 0.030);
    vec3 skyColor = vec3(0.20, 0.24, 0.30);
    float skyBlend = smoothstep(-0.45, 0.75, reflectionDirection.y);
    vec3 environment = mix(groundColor, skyColor, skyBlend);

    float horizon = pow(max(1.0 - abs(reflectionDirection.y), 0.0), 8.0);
    environment += vec3(0.42, 0.46, 0.52) * horizon * (1.0 - roughness * 0.75);

    vec3 studioDirection = normalize(vec3(-0.35, 0.72, 0.60));
    float highlightPower = mix(96.0, 5.0, roughness);
    float studioHighlight = pow(max(dot(reflectionDirection, studioDirection), 0.0), highlightPower);
    environment += vec3(1.35, 1.15, 0.88) * studioHighlight * (1.0 - roughness * 0.5);

    vec3 broadStudioDirection = normalize(vec3(0.55, 0.18, 0.82));
    float broadHighlight = smoothstep(0.05, 0.9, dot(reflectionDirection, broadStudioDirection));
    environment += vec3(0.55, 0.62, 0.78) * broadHighlight * (1.0 - roughness * 0.35);
    return environment;
}

void main() {
    vec4 baseSample = texture(Sampler0, texCoord0);
    if (baseSample.a < 0.1) {
        discard;
    }

    mat3 basis = tangentBasis();
    vec3 viewDirection = normalize(surfaceViewDirection);
    vec2 materialUv = parallaxTexCoord(basis, viewDirection);

    vec4 diffuseSample = texture(Sampler4, materialUv);
    float diffuseMask = diffuseSample.a;
    float materialMask = UsePackedMer != 0 ? baseSample.a : diffuseMask;
    vec3 albedoSrgb = mix(baseSample.rgb, diffuseSample.rgb, diffuseMask);
    vec3 albedo = pow(max(albedoSrgb, vec3(0.0)), vec3(2.2));

    vec4 aoSample = texture(Sampler5, materialUv);
    vec4 metallicSample = texture(Sampler7, materialUv);
    vec4 roughnessSample = texture(Sampler8, materialUv);
    float ambientOcclusion = HasAmbientOcclusion != 0
        ? mix(1.0, aoSample.r, aoSample.a * materialMask)
        : 1.0;
    float metallicValue = metallicSample.r;
    float roughnessValue = UsePackedMer != 0 ? metallicSample.b : roughnessSample.r;
    float emissive = UsePackedMer != 0 ? metallicSample.g * materialMask : 0.0;
    float metallic = mix(0.0, metallicValue, materialMask);
    float roughness = clamp(mix(0.65, roughnessValue, materialMask), 0.045, 1.0);

    vec3 normal = mappedNormal(basis, materialUv, materialMask);
    float normalDotView = max(dot(normal, viewDirection), 0.0);
    vec3 reflectance = mix(vec3(0.04), albedo, metallic);

    vec3 lighting = directLight(
        normal, viewDirection, Light0_Direction,
        vec3(1.15, 1.02, 0.86), albedo, metallic, roughness, reflectance
    );
    lighting += directLight(
        normal, viewDirection, Light1_Direction,
        vec3(0.52, 0.64, 0.92), albedo, metallic, roughness, reflectance
    );

    vec3 reflectionDirection = reflect(-viewDirection, normal);
    vec3 environment = proceduralEnvironment(reflectionDirection, roughness);
    float environmentLuminance = dot(environment, vec3(0.2126, 0.7152, 0.0722));
    environment = mix(vec3(environmentLuminance), environment, 0.25);
    vec3 environmentFresnel = fresnelSchlickRoughness(normalDotView, reflectance, roughness);
    vec3 diffuseAmbient = albedo * (1.0 - metallic) * 0.22;
    lighting += (diffuseAmbient + environment * environmentFresnel * EnvironmentStrength) * ambientOcclusion;

    vec3 lightMap = max(lightMapColor.rgb, vec3(0.06));
    vec3 colorLinear = lighting * lightMap + albedo * emissive * EmissiveStrength;
    vec3 colorSrgb = pow(max(colorLinear, vec3(0.0)), vec3(1.0 / 2.2));
    colorSrgb *= vertexColor.rgb * ColorModulator.rgb;
    colorSrgb = mix(overlayColor.rgb, colorSrgb, overlayColor.a);

    float alpha = baseSample.a * vertexColor.a * ColorModulator.a;
    fragColor = linear_fog(vec4(colorSrgb, alpha), vertexDistance, FogStart, FogEnd, FogColor);
}
