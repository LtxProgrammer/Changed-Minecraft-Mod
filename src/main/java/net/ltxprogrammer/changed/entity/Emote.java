package net.ltxprogrammer.changed.entity;

import com.mojang.serialization.Codec;

public enum Emote {
    STARTLED,
    CONFUSED,
    CASUAL,
    HEART,
    ANGRY,
    NERVOUS,
    DENY,
    PAUSE,
    IDEA,
    SLEEPY;

    public static final Codec<Emote> CODEC = Codec.INT.xmap(idx -> values()[idx], Emote::ordinal);
}
