package net.ltxprogrammer.changed.world.features.structures.facility;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

public enum Zone implements StringRepresentable {
    RED_ZONE("red_zone"),
    GRAY_ZONE("gray_zone"),
    MAINTENANCE_ZONE("maintenance_zone"),
    GREENHOUSE_ZONE("greenhouse_zone"),
    BLUE_ZONE("blue_zone");

    private final String name;

    Zone(String name) {
        this.name = name;
    }

    public static Zone random(Random r) {
        return values()[r.nextInt(values().length)];
    }

    public String getSerializedName() {
        return this.name;
    }

    public static Optional<Zone> byName(String name) {
        return Arrays.stream(values()).filter((value) -> {
            return value.getSerializedName().equals(name);
        }).findFirst();
    }

    public Component getTranslatedName() {
        return new TranslatableComponent("facility.zone." + this.name);
    }

    public Zone next() {
        int nextOrdinal = this.ordinal() + 1;
        if (nextOrdinal >= values().length)
            nextOrdinal = 0;
        return values()[nextOrdinal];
    }

    public boolean canConnectTo(Zone other) {
        return this != other;
    }
}