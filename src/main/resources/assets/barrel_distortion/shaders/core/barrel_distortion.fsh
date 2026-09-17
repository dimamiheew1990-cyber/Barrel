#version 150

uniform sampler2D DiffuseSampler;
uniform float DistortionStrength;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 centered = texCoord * 2.0 - 1.0;
    float radiusSquared = dot(centered, centered);
    // Inverse lookup makes the visible image bulge outward (barrel distortion).
    vec2 distortedUv = centered * (1.0 - DistortionStrength * radiusSquared);
    distortedUv = distortedUv * 0.5 + 0.5;

    // Preserve a clean black border instead of edge-smearing beyond the source image.
    if (any(lessThan(distortedUv, vec2(0.0))) || any(greaterThan(distortedUv, vec2(1.0)))) {
        fragColor = vec4(0.0, 0.0, 0.0, 1.0);
    } else {
        fragColor = texture(DiffuseSampler, distortedUv);
    }
}
