float colorIntensity(vec3 color) {
    return dot(color.rgb, vec3(0.2989, 0.5870, 0.1140));
}

vec4 waveColoring(vec4 colorIn) {
    return vec4(vec3(colorIntensity(colorIn.rgb)), colorIn.a);
}

const float WAVE_DECAY = 0.995f;
const float WAVE_DECAY_RESONANT = 0.8f;
const float WAVE_INTERVAL = 60.0f;
const float WAVE_SPEED = 20.0f;

float waveStrength(float effectValue, float depth, float decay, float interval, float speed) {
    float time = (depth / interval) - (effectValue / speed);
    float waved = (sin(mod(time, 3.141592 * 0.5)) * decay) - decay + 1.0;
    if (waved < 0.9995f) waved *= 0.6f;
    if (time > 0) waved = 0.0f;
    return clamp(waved, 0.0f, 1.0f);
}

vec4 waveVision(vec4 colorIn, float effectValue, float depth, vec3 resonanceColor) {
    vec4 colored = waveColoring(colorIn);
    float waveFast = waveStrength(effectValue, depth, WAVE_DECAY, WAVE_INTERVAL, WAVE_SPEED);
    float waveRecv = waveStrength(effectValue, depth, WAVE_DECAY_RESONANT, WAVE_INTERVAL, WAVE_SPEED) * colorIntensity(resonanceColor);
    colored.rgb = mix(waveFast * colored.rgb, mix(colored.rgb, resonanceColor, 0.7f), waveRecv);
    return colored;
}

vec4 waveVision(vec4 colorIn, float effectValue, float depth) {
    return waveVision(colorIn, effectValue, depth, vec3(0));
}