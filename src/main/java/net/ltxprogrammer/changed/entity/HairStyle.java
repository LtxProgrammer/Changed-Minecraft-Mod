package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.data.DeferredModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.common.IExtensibleEnum;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public enum HairStyle implements IExtensibleEnum, StringRepresentable { // TODO maybe have some sort of pattern system?
    BALD(Gender.MALE),
    LEGACY_MALE(Gender.MALE,
            DeferredModelLayerLocation.main(Changed.modResource("hair/legacy_male")),
            Changed.modResource("textures/hair/legacy_male.png")),
    LEGACY_FEMALE_RIGHT_BANG(Gender.FEMALE,
            DeferredModelLayerLocation.main(Changed.modResource("hair/legacy_female_right_bang")),
            Changed.modResource("textures/hair/legacy_collection.png")),
    LEGACY_FEMALE_RIGHT_BANG_L(Gender.FEMALE,
            DeferredModelLayerLocation.main(Changed.modResource("hair/legacy_female_right_bang")),
            DeferredModelLayerLocation.main(Changed.modResource("hair/legacy_female_lower")),
            Changed.modResource("textures/hair/legacy_collection.png")),
    LEGACY_FEMALE_LEFT_BANG(Gender.FEMALE,
            DeferredModelLayerLocation.main(Changed.modResource("hair/legacy_female_left_bang")),
            Changed.modResource("textures/hair/legacy_collection.png")),
    LEGACY_FEMALE_LEFT_BANG_L(Gender.FEMALE,
            LEGACY_FEMALE_LEFT_BANG.headHair,
            LEGACY_FEMALE_RIGHT_BANG_L.lowerHair,
            Changed.modResource("textures/hair/legacy_collection.png")),
    LEGACY_FEMALE_DUAL_BANGS(Gender.FEMALE,
            DeferredModelLayerLocation.main(Changed.modResource("hair/legacy_female_dual_bangs")),
            Changed.modResource("textures/hair/legacy_collection.png")),
    LEGACY_FEMALE_DUAL_BANGS_L(Gender.FEMALE,
            LEGACY_FEMALE_DUAL_BANGS.headHair,
            LEGACY_FEMALE_RIGHT_BANG_L.lowerHair,
            Changed.modResource("textures/hair/legacy_collection.png")),
    LEGACY_FEMALE_TRIPLE_BANGS(Gender.FEMALE,
            DeferredModelLayerLocation.main(Changed.modResource("hair/legacy_female_triple_bangs")),
            Changed.modResource("textures/hair/legacy_collection.png")),
    LEGACY_FEMALE_TRIPLE_BANGS_L(Gender.FEMALE,
            LEGACY_FEMALE_TRIPLE_BANGS.headHair,
            LEGACY_FEMALE_RIGHT_BANG_L.lowerHair,
            Changed.modResource("textures/hair/legacy_collection.png")),
    FEMALE_NO_BANGS(Gender.FEMALE,
            DeferredModelLayerLocation.main(Changed.modResource("hair/female_no_bangs")),
            Changed.modResource("textures/hair/legacy_collection.png")),
    FEMALE_NO_BANGS_L(Gender.FEMALE,
            FEMALE_NO_BANGS.headHair,
            LEGACY_FEMALE_RIGHT_BANG_L.lowerHair,
            Changed.modResource("textures/hair/legacy_collection.png")),
    MALE_SHORT_FRONT(Gender.MALE,
            DeferredModelLayerLocation.main(Changed.modResource("hair/male_short_front")),
            Changed.modResource("textures/hair/legacy_collection.png"), Changed.modResource("textures/hair/stiger_layer.png")),
    FEMALE_SIDE_BANGS(Gender.FEMALE,
            DeferredModelLayerLocation.main(Changed.modResource("hair/female_side_bangs")),
            Changed.modResource("textures/hair/legacy_collection.png")),
    FEMALE_SIDE_BANGS_L(Gender.FEMALE,
            FEMALE_SIDE_BANGS.headHair,
            LEGACY_FEMALE_RIGHT_BANG_L.lowerHair,
            Changed.modResource("textures/hair/legacy_collection.png")),
    MOHAWK(Gender.MALE,
            DeferredModelLayerLocation.main(Changed.modResource("hair/mohawk_hair")),
            Changed.modResource("textures/hair/legacy_collection.png")),
    HEAD_FUZZ(Gender.MALE,
            DeferredModelLayerLocation.main(Changed.modResource("hair/head_fuzz")),
            Changed.modResource("textures/hair/legacy_collection.png")),
    MALE_SIDEBURN(Gender.MALE,
            DeferredModelLayerLocation.main(Changed.modResource("hair/male_sideburn")),
            Changed.modResource("textures/hair/legacy_collection.png")),
    MALE_STANDARD(Gender.MALE,
            DeferredModelLayerLocation.main(Changed.modResource("hair/male_standard")),
            Changed.modResource("textures/hair/legacy_collection.png"), Changed.modResource("textures/hair/squirrel_layer.png")),
    MALE_BANGS(Gender.MALE,
            DeferredModelLayerLocation.main(Changed.modResource("hair/male_bangs")),
            Changed.modResource("textures/hair/legacy_collection.png")),
    MALE_NWE(Gender.MALE,
            DeferredModelLayerLocation.main(Changed.modResource("hair/male_nwe")),
            Changed.modResource("textures/hair/legacy_collection.png")),
    LEGACY_FEMALE_RIGHT_BANG_S(Gender.FEMALE,
            LEGACY_FEMALE_RIGHT_BANG.headHair,
            Changed.modResource("textures/hair/legacy_collection_s.png")),
    LEGACY_FEMALE_RIGHT_BANG_S_L(Gender.FEMALE,
            LEGACY_FEMALE_RIGHT_BANG_S.headHair,
            LEGACY_FEMALE_RIGHT_BANG_L.lowerHair,
            Changed.modResource("textures/hair/legacy_collection_s.png")),
    LEGACY_FEMALE_LEFT_BANG_S(Gender.FEMALE,
            LEGACY_FEMALE_LEFT_BANG.headHair,
            Changed.modResource("textures/hair/legacy_collection_s.png")),
    LEGACY_FEMALE_LEFT_BANG_S_L(Gender.FEMALE,
            LEGACY_FEMALE_LEFT_BANG_S.headHair,
            LEGACY_FEMALE_RIGHT_BANG_L.lowerHair,
            Changed.modResource("textures/hair/legacy_collection_s.png")),
    LEGACY_FEMALE_DUAL_BANGS_S(Gender.FEMALE,
            LEGACY_FEMALE_DUAL_BANGS.headHair,
            Changed.modResource("textures/hair/legacy_collection_s.png")),
    LEGACY_FEMALE_DUAL_BANGS_S_L(Gender.FEMALE,
            LEGACY_FEMALE_DUAL_BANGS_S.headHair,
            LEGACY_FEMALE_RIGHT_BANG_L.lowerHair,
            Changed.modResource("textures/hair/legacy_collection_s.png")),
    LEGACY_FEMALE_TRIPLE_BANGS_S(Gender.FEMALE,
            LEGACY_FEMALE_TRIPLE_BANGS.headHair,
            Changed.modResource("textures/hair/legacy_collection_s.png"), Changed.modResource("textures/hair/siren_layer.png")),
    LEGACY_FEMALE_TRIPLE_BANGS_S_L(Gender.FEMALE,
            LEGACY_FEMALE_TRIPLE_BANGS_S.headHair,
            LEGACY_FEMALE_RIGHT_BANG_L.lowerHair,
            Changed.modResource("textures/hair/legacy_collection_s.png")),
    FEMALE_NO_BANGS_S(Gender.FEMALE,
            FEMALE_NO_BANGS.headHair,
            Changed.modResource("textures/hair/legacy_collection_s.png")),
    FEMALE_NO_BANGS_S_L(Gender.FEMALE,
            FEMALE_NO_BANGS_S.headHair,
            LEGACY_FEMALE_RIGHT_BANG_L.lowerHair,
            Changed.modResource("textures/hair/legacy_collection_s.png")),
    FEMALE_SIDE_BANGS_S(Gender.FEMALE,
            FEMALE_SIDE_BANGS.headHair,
            Changed.modResource("textures/hair/legacy_collection_s.png")),
    FEMALE_SIDE_BANGS_S_L(Gender.FEMALE,
            FEMALE_SIDE_BANGS_S.headHair,
            LEGACY_FEMALE_RIGHT_BANG_L.lowerHair,
            Changed.modResource("textures/hair/legacy_collection_s.png")),
    MOHAWK_S(Gender.MALE,
            MOHAWK.headHair,
            Changed.modResource("textures/hair/legacy_collection_s.png")),
    HEAD_FUZZ_S(Gender.MALE,
            HEAD_FUZZ.headHair,
            Changed.modResource("textures/hair/legacy_collection_s.png")),
    MALE_STANDARD_S(Gender.MALE,
            MALE_STANDARD.headHair,
            Changed.modResource("textures/hair/legacy_collection_s.png")),
    MALE_BANGS_S(Gender.MALE,
            MALE_BANGS.headHair,
            Changed.modResource("textures/hair/legacy_collection_s.png")),
    MALE_NWE_S(Gender.MALE,
            MALE_NWE.headHair,
            Changed.modResource("textures/hair/legacy_collection_s.png")),
    MALE_SHORT_FRONT_S(Gender.MALE,
            MALE_SHORT_FRONT.headHair,
            Changed.modResource("textures/hair/legacy_collection_s.png"), Changed.modResource("textures/hair/raccoon_layer.png")),
    MALE_SIDEBURN_S(Gender.MALE,
            MALE_SIDEBURN.headHair,
            Changed.modResource("textures/hair/legacy_collection_s.png"));

    public static class Collections {
        private static Map<ResourceLocation, List<HairStyle>> REGISTRY = new HashMap<>();

        public static List<HairStyle> NONE = named("none", of(BALD));
        
        public static List<HairStyle> MALE = named("male", of(BALD, MOHAWK, HEAD_FUZZ, MALE_SHORT_FRONT, MALE_STANDARD, MALE_SIDEBURN, MALE_BANGS));
        public static List<HairStyle> MALE_SHADED = named("male_shaded", of(BALD, MOHAWK_S, HEAD_FUZZ_S, MALE_SHORT_FRONT_S, MALE_STANDARD_S, MALE_SIDEBURN_S, MALE_BANGS_S));
        public static List<HairStyle> FEMALE = named("female", of(LEGACY_FEMALE_DUAL_BANGS, LEGACY_FEMALE_DUAL_BANGS_L, FEMALE_NO_BANGS, FEMALE_NO_BANGS_L, LEGACY_FEMALE_RIGHT_BANG, LEGACY_FEMALE_RIGHT_BANG_L,
                LEGACY_FEMALE_LEFT_BANG, LEGACY_FEMALE_LEFT_BANG_L, LEGACY_FEMALE_TRIPLE_BANGS, LEGACY_FEMALE_TRIPLE_BANGS_L, FEMALE_SIDE_BANGS, FEMALE_SIDE_BANGS_L));
        public static List<HairStyle> FEMALE_SHADED = named("female_shaded", of(LEGACY_FEMALE_DUAL_BANGS_S, LEGACY_FEMALE_DUAL_BANGS_S_L, FEMALE_NO_BANGS_S, FEMALE_NO_BANGS_S_L, LEGACY_FEMALE_RIGHT_BANG_S, LEGACY_FEMALE_RIGHT_BANG_S_L,
                LEGACY_FEMALE_LEFT_BANG_S, LEGACY_FEMALE_LEFT_BANG_S_L, LEGACY_FEMALE_TRIPLE_BANGS_S, LEGACY_FEMALE_TRIPLE_BANGS_S_L, FEMALE_SIDE_BANGS_S, FEMALE_SIDE_BANGS_S_L));

        public static List<HairStyle> MALE_FEMALE = named("male_female", combine(MALE, FEMALE));
        public static List<HairStyle> MALE_FEMALE_SHADED = named("male_female_shaded", combine(MALE_SHADED, FEMALE_SHADED));

        public static List<HairStyle> MALE_NO_WOLF_EARS = named("male_no_wolf_ears", of(BALD, MOHAWK, HEAD_FUZZ, MALE_SHORT_FRONT, MALE_NWE, MALE_SIDEBURN, MALE_BANGS));
        public static List<HairStyle> MALE_NO_WOLF_EARS_SHADED = named("male_no_wolf_ears_shaded", of(BALD, MOHAWK_S, HEAD_FUZZ_S, MALE_SHORT_FRONT_S, MALE_NWE_S, MALE_SIDEBURN_S, MALE_BANGS_S));

        public static List<HairStyle> getCollection(ResourceLocation name) {
            return REGISTRY.getOrDefault(name, NONE);
        }

        public static List<HairStyle> of(HairStyle... styles) {
            return List.of(styles);
        }

        public static List<HairStyle> combine(List<HairStyle> collectionA, List<HairStyle> collectionB) {
            List<HairStyle> list = new ArrayList<>();
            list.addAll(collectionA);
            list.addAll(collectionB);
            return java.util.Collections.unmodifiableList(list);
        }

        private static List<HairStyle> named(String collection, List<HairStyle> styles) {
            return named(Changed.modResource(collection), styles);
        }

        public static List<HairStyle> named(ResourceLocation collection, List<HairStyle> styles) {
            REGISTRY.put(collection, styles);
            return styles;
        }
    }

    @Nullable
    public final DeferredModelLayerLocation headHair;
    @Nullable
    public final DeferredModelLayerLocation lowerHair;
    @NotNull
    public final ResourceLocation[] textures;

    public boolean hasModel() {
        return headHair != null || lowerHair != null;
    }

    public boolean hasTexture() {
        return textures != null;
    }

    public static class Sorted {
        public static final EnumMap<Gender, List<HairStyle>> BY_GENDER = new EnumMap<>(Gender.class);
    }

    HairStyle(Gender gender) {
        Sorted.BY_GENDER.computeIfAbsent(gender, ignored -> new ArrayList<>()).add(this);
        this.headHair = null;
        this.lowerHair = null;
        this.textures = new ResourceLocation[0];
    }

    HairStyle(Gender gender, @Nullable DeferredModelLayerLocation headHair, @NotNull ResourceLocation... textures) {
        Sorted.BY_GENDER.computeIfAbsent(gender, ignored -> new ArrayList<>()).add(this);
        this.headHair = headHair;
        this.lowerHair = null;
        this.textures = textures;
    }

    HairStyle(Gender gender, @Nullable DeferredModelLayerLocation headHair, @Nullable DeferredModelLayerLocation lowerHair, @NotNull ResourceLocation... textures) {
        Sorted.BY_GENDER.computeIfAbsent(gender, ignored -> new ArrayList<>()).add(this);
        this.headHair = headHair;
        this.lowerHair = lowerHair;
        this.textures = textures;
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

    public static HairStyle create(String name, Gender gender) {
        throw new IllegalStateException("Enum not extended");
    }

    public static HairStyle create(String name, Gender gender, @NotNull DeferredModelLayerLocation headHair, @NotNull ResourceLocation... texture) {
        throw new IllegalStateException("Enum not extended");
    }

    public static HairStyle create(String name, Gender gender, @NotNull DeferredModelLayerLocation headHair,
                                   @NotNull DeferredModelLayerLocation lowerHair, @NotNull ResourceLocation... texture) {
        throw new IllegalStateException("Enum not extended");
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
