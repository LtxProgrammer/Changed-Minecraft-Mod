#version 150

#moj_import <fog.glsl>
#moj_import <changed/wave.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float WaveEffect;
uniform vec3 WaveResonance;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec4 normal;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    if (color.a < 0.5) {
        discard;
    }
    color = waveVision(color, WaveEffect, vertexDistance, WaveResonance * texture(Sampler1, texCoord0).rgb);
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
