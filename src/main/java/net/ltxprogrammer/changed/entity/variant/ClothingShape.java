package net.ltxprogrammer.changed.entity.variant;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.common.IExtensibleEnum;

import javax.annotation.Nullable;

public abstract class ClothingShape {
    public enum Head implements IExtensibleEnum, StringRepresentable {
        NONE("none"),
        ANTHRO("anthro");

        public static final Head DEFAULT = ANTHRO;

        private final String serialName;

        Head(String serialName) {
            this.serialName = serialName;
        }

        @Override
        public String getSerializedName() {
            return serialName;
        }

        @Nullable
        public ResourceLocation getEmptyArmorSlot() {
            if (this == NONE || this == ANTHRO)
                return null;
            return Changed.modResource("items/empty_armor_slot_" + serialName + "_head");
        }

        public static Head create(String name, String serialName) {
            throw new IllegalStateException("Enum not extended");
        }
    }

    public enum Torso implements IExtensibleEnum, StringRepresentable {
        NONE("none"),
        ANTHRO("anthro");

        public static final Torso DEFAULT = ANTHRO;

        private final String serialName;

        Torso(String serialName) {
            this.serialName = serialName;
        }

        @Override
        public String getSerializedName() {
            return serialName;
        }

        @Nullable
        public ResourceLocation getEmptyArmorSlot() {
            if (this == NONE || this == ANTHRO)
                return null;
            return Changed.modResource("items/empty_armor_slot_" + serialName + "_torso");
        }

        public static Torso create(String name, String serialName) {
            throw new IllegalStateException("Enum not extended");
        }
    }

    public enum Legs implements IExtensibleEnum, StringRepresentable {
        NONE("none"),
        BIPEDAL("bipedal"),
        QUADRUPEDAL("quadrupedal"),
        TAIL("tail");

        public static final Legs DEFAULT = BIPEDAL;

        private final String serialName;

        Legs(String serialName) {
            this.serialName = serialName;
        }

        @Override
        public String getSerializedName() {
            return serialName;
        }

        @Nullable
        public ResourceLocation getEmptyArmorSlot() {
            if (this == NONE || this == BIPEDAL)
                return null;
            return Changed.modResource("items/empty_armor_slot_" + serialName + "_legs");
        }

        public static Legs create(String name, String serialName) {
            throw new IllegalStateException("Enum not extended");
        }
    }

    public enum Feet implements IExtensibleEnum, StringRepresentable {
        NONE("none"),
        BIPEDAL("bipedal"),
        QUADRUPEDAL("quadrupedal"),
        TAIL("tail");

        public static final Feet DEFAULT = BIPEDAL;

        private final String serialName;

        Feet(String serialName) {
            this.serialName = serialName;
        }

        @Override
        public String getSerializedName() {
            return serialName;
        }

        @Nullable
        public ResourceLocation getEmptyArmorSlot() {
            if (this == NONE || this == BIPEDAL)
                return null;
            return Changed.modResource("items/empty_armor_slot_" + serialName + "_feet");
        }

        public static Feet create(String name, String serialName) {
            throw new IllegalStateException("Enum not extended");
        }
    }
}
