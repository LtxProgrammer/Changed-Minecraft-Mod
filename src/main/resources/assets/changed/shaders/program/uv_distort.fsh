#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DistortionSampler;
uniform float Strength;
uniform float changed_PostChainStrength;

in vec2 texCoord;
in vec2 scaledCoord;

out vec4 fragColor;

void main() {
    vec4 DistortionTexel = texture(DistortionSampler, texCoord);
    vec4 ScaledTexel = texture(DiffuseSampler, texCoord + (DistortionTexel.rg * 2 - 1) * Strength * changed_PostChainStrength);
    fragColor = vec4(ScaledTexel.rgb, 1.0);
}
