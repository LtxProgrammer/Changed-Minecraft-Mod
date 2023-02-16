package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.common.IExtensibleEnum;

import javax.annotation.Nullable;
import java.util.*;

public enum HairStyle implements IExtensibleEnum, StringRepresentable {
    BALD(Gender.MALE, null, null),
    LEGACY_MALE(Gender.MALE,
            new ModelLayerLocation(Changed.modResource("hair/legacy_male"), "main"),
            Changed.modResource("textures/hair/legacy_male.png")),
    LEGACY_FEMALE(Gender.FEMALE,
            new ModelLayerLocation(Changed.modResource("hair/legacy_female"), "main"),
            Changed.modResource("textures/hair/legacy_female.png")),
    FEMALE_NO_BANGS(Gender.FEMALE,
            new ModelLayerLocation(Changed.modResource("hair/female_no_bangs"), "main"),
            Changed.modResource("textures/hair/legacy_female.png")),
    MOHAWK(Gender.MALE,
            new ModelLayerLocation(Changed.modResource("hair/mohawk_hair"), "main"),
            Changed.modResource("textures/hair/mohawk.png")),
    HEAD_FUZZ(Gender.MALE,
            new ModelLayerLocation(Changed.modResource("hair/head_fuzz"), "main"),
            Changed.modResource("textures/hair/head_fuzz.png")),
    MALE_STANDARD(Gender.MALE,
            new ModelLayerLocation(Changed.modResource("hair/male_standard"), "main"),
            Changed.modResource("textures/hair/male_standard.png")),
    MALE_NWE(Gender.MALE,
            new ModelLayerLocation(Changed.modResource("hair/male_nwe"), "main"),
            Changed.modResource("textures/hair/male_nwe.png"));
    @Nullable
    public final ModelLayerLocation model;
    @Nullable
    public final ResourceLocation texture;

    public boolean hasModel() {
        return model != null;
    }

    public boolean hasTexture() {
        return texture != null;
    }

    public static class Sorted {
        public static final EnumMap<Gender, List<HairStyle>> BY_GENDER = new EnumMap<>(Gender.class);
    }

    HairStyle(Gender gender, @Nullable ModelLayerLocation model, @Nullable ResourceLocation texture) {
        Sorted.BY_GENDER.computeIfAbsent(gender, ignored -> new ArrayList<>()).add(this);
        this.model = model;
        this.texture = texture;
    }

    public static HairStyle randomStyle(Random random) {
        return HairStyle.values()[random.nextInt(HairStyle.values().length)];
    }

    public static HairStyle randomStyle(Gender gender, Random random) {
        if (!Sorted.BY_GENDER.containsKey(gender)) {
            return LEGACY_MALE;
        }

        else {
            var list = Sorted.BY_GENDER.get(gender);
            return list.get(random.nextInt(list.size()));
        }
    }

    public static HairStyle create(String name, @Nullable Gender gender, @Nullable ModelLayerLocation model, @Nullable ResourceLocation texture) {
        throw new IllegalStateException("Enum not extended");
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
