float colorIntensity(vec3 color) {
    return dot(color.rgb, vec3(0.2989, 0.5870, 0.1140));
}

const float WAVE_DECAY = 0.995f;
const float WAVE_DECAY_RESONANT = 0.7f;
const float WAVE_INTERVAL = 60.0f;
const float WAVE_SPEED = 20.0f;
const float WAVE_SATURATION = 0.2f;
const float BRIGHTNESS_THRESHOLD = 0.8f;
const float SATURATION_THRESHOLD = 0.2f;
const float RESONANCE_SATURATION = 1.2f;

float threshold(float value, float minimum) {
    if (value > minimum)
        return value;
    return 0f;
}

vec4 waveColoring(vec4 colorIn) {
    float intensity = colorIntensity(colorIn.rgb);
    return mix(vec4(vec3(intensity), colorIn.a), colorIn, WAVE_SATURATION);
}

float waveStrength(float effectValue, float depth, float decay, float interval, float speed) {
    float time = (depth / interval) - (effectValue / speed);
    float waved = (sin(mod(time, 3.141592 * 0.5)) * decay) - decay + 1.0;
    if (waved < 0.9995f) waved *= 0.6f;
    if (time > 0) waved = 0.0f;
    return clamp(waved, 0.0f, 1.0f);
}

vec4 resonanceColoring(vec4 colorIn) {
    float gray = colorIntensity(colorIn.rgb);
    return mix(vec4(vec3(gray), colorIn.a), colorIn, RESONANCE_SATURATION);
}

vec4 waveVision(vec4 colorIn, float effectValue, float depth, vec3 resonanceColor) {
    vec4 colored = waveColoring(colorIn);
    float waveFast = waveStrength(effectValue, depth, WAVE_DECAY, WAVE_INTERVAL, WAVE_SPEED);
    float waveRecv = waveStrength(effectValue, depth, WAVE_DECAY_RESONANT, WAVE_INTERVAL, WAVE_SPEED) * colorIntensity(resonanceColor) * 2f;
    colored.rgb = mix(waveFast * colored.rgb, mix(resonanceColoring(colorIn).rgb, resonanceColor, 0.35f), waveRecv);
    return colored;
}

vec4 waveVision(vec4 colorIn, float effectValue, float depth) {
    return waveVision(colorIn, effectValue, depth, vec3(0));
}