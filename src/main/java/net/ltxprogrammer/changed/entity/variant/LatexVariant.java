package net.ltxprogrammer.changed.entity.variant;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.beast.*;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Color3;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class LatexVariant<T extends LatexEntity> extends ForgeRegistryEntry<LatexVariant<?>> {
    public static final String NBT_PLAYER_ID = "changed:player_id";
    public static final ResourceLocation SPECIAL_LATEX = Changed.modResource("form_special");
    private static final Map<ResourceLocation, LatexVariant<?>> ALL_LATEX_FORMS = new HashMap<>();
    public static final List<ResourceLocation> PUBLIC_LATEX_FORMS = new ArrayList<>();
    public static final List<ResourceLocation> FUSION_LATEX_FORMS = new ArrayList<>();
    public static final List<ResourceLocation> MOB_FUSION_LATEX_FORMS = new ArrayList<>();
    public static final List<ResourceLocation> SPECIAL_LATEX_FORMS = new ArrayList<>();
    public static final EnumMap<LatexType, List<ResourceLocation>> VARIANTS_BY_TYPE = new EnumMap<>(LatexType.class);

    public static List<LatexVariant<?>> getFusionCompatible(LatexVariant<?> source, LatexVariant<?> other) {
        List<LatexVariant<?>> list = new ArrayList<>();
        ChangedRegistry.LATEX_VARIANT.get().forEach(variant -> {
            if (variant.isFusionOf(source, other))
                list.add(variant);
        });
        return list;
    }

    public static List<LatexVariant<?>> getFusionCompatible(LatexVariant<?> source, Class<? extends LivingEntity> clazz) {
        List<LatexVariant<?>> list = new ArrayList<>();
        ChangedRegistry.LATEX_VARIANT.get().forEach(variant -> {
            if (variant.isFusionOf(source, clazz))
                list.add(variant);
        });
        return list;
    }

    public static final GenderedVariant<LightLatexWolfMale, LightLatexWolfFemale> LIGHT_LATEX_WOLF = register(GenderedVariant.Builder.of(ChangedEntities.LIGHT_LATEX_WOLF_MALE, ChangedEntities.LIGHT_LATEX_WOLF_FEMALE)
            .addAbility(ChangedAbilities.SWITCH_GENDER).split(Builder::ignored, Builder::absorbing)
            .buildGendered(Changed.modResource("form_light_latex_wolf")));

    public static final LatexVariant<AerosolLatexWolf> AEROSOL_LATEX_WOLF = register(Builder.of(ChangedEntities.AEROSOL_LATEX_WOLF)
            .groundSpeed(1.075f).swimSpeed(0.95f).stepSize(0.7f).sound(ChangedSounds.SOUND3.getLocation())
            .build(Changed.modResource("form_aerosol_latex_wolf")));
    public static final LatexVariant<DarkLatexDragon> DARK_LATEX_DRAGON = register(Builder.of(ChangedEntities.DARK_LATEX_DRAGON)
            .groundSpeed(1.0F).swimSpeed(0.85f).glide().sound(ChangedSounds.SOUND3.getLocation()).faction(LatexType.DARK_LATEX)
            .build(Changed.modResource("form_dark_latex_dragon")));
    public static final LatexVariant<DarkLatexYufeng> DARK_LATEX_YUFENG = register(Builder.of(ChangedEntities.DARK_LATEX_YUFENG)
            .groundSpeed(1.0F).swimSpeed(0.85f).glide().faction(LatexType.DARK_LATEX)
            .build(Changed.modResource("form_dark_latex_yufeng")));
    public static final LatexVariant<LatexAlien> LATEX_ALIEN = register(Builder.of(ChangedEntities.LATEX_ALIEN)
            .groundSpeed(1.0f).swimSpeed(1.0f).stepSize(0.7f).nightVision()
            .build(Changed.modResource("form_latex_alien")));
    public static final LatexVariant<LatexBee> LATEX_BEE = register(Builder.of(ChangedEntities.LATEX_BEE)
            .groundSpeed(1.05f).swimSpeed(0.75f).extraJumps(4).reducedFall().extraHands().addAbility(ChangedAbilities.CREATE_HONEYCOMB).breatheMode(BreatheMode.WEAK)
            .build(Changed.modResource("form_latex_bee")));
    public static final LatexVariant<LatexBeifeng> LATEX_BEIFENG = register(Builder.of(ChangedEntities.LATEX_BEIFENG)
            .groundSpeed(1.05f).swimSpeed(1.0f).stepSize(0.7f).sound(ChangedSounds.SOUND3.getLocation())
            .build(Changed.modResource("form_latex_beifeng")));
    public static final LatexVariant<LatexBenignWolf> LATEX_BENIGN_WOLF = register(Builder.of(ChangedEntities.LATEX_BENIGN_WOLF)
            .groundSpeed(0.15f).swimSpeed(0.15f).noVision().disableItems()
            .build(Changed.modResource("form_latex_benign_wolf")));
    public static final LatexVariant<LatexBlueDragon> LATEX_BLUE_DRAGON = register(Builder.of(ChangedEntities.LATEX_BLUE_DRAGON)
            .groundSpeed(1.05f).swimSpeed(0.98f).stepSize(0.7f)
            .build(Changed.modResource("form_latex_blue_dragon")));
    public static final LatexVariant<LatexBlueWolf> LATEX_BLUE_WOLF = register(Builder.of(ChangedEntities.LATEX_BLUE_WOLF)
            .groundSpeed(1.075f).swimSpeed(0.95f).stepSize(0.7f).absorbing()
            .build(Changed.modResource("form_latex_blue_wolf")));
    public static final LatexVariant<LatexCrocodile> LATEX_CROCODILE = register(Builder.of(ChangedEntities.LATEX_CROCODILE)
            .groundSpeed(0.925f).swimSpeed(1.1f).additionalHealth(12).breatheMode(BreatheMode.STRONG)
            .build(Changed.modResource("form_latex_crocodile")));
    public static final LatexVariant<LatexCrystalWolf> LATEX_CRYSTAL_WOLF = register(Builder.of(ChangedEntities.LATEX_CRYSTAL_WOLF)
            .groundSpeed(1.075f).swimSpeed(0.95f).stepSize(0.7f).sound(ChangedSounds.SOUND3.getLocation())
            .build(Changed.modResource("form_latex_crystal_wolf")));
    public static final LatexVariant<LatexCrystalWolfHorned> LATEX_CRYSTAL_WOLF_HORNED = register(Builder.of(LATEX_CRYSTAL_WOLF, ChangedEntities.LATEX_CRYSTAL_WOLF_HORNED)
            .sound(ChangedSounds.SOUND3.getLocation())
            .build(Changed.modResource("form_latex_crystal_wolf_horned")));
    public static final LatexVariant<LatexDeer> LATEX_DEER = register(Builder.of(ChangedEntities.LATEX_DEER)
            .groundSpeed(1.1f).swimSpeed(0.95f).stepSize(0.7f)
            .build(Changed.modResource("form_latex_deer")));
    public static final LatexVariant<LatexHypnoCat> LATEX_HYPNO_CAT = register(Builder.of(ChangedEntities.LATEX_HYPNO_CAT)
            .groundSpeed(1.15f).swimSpeed(0.9f).stepSize(0.7f).breatheMode(BreatheMode.WEAK).reducedFall().scares(Creeper.class).nightVision().addAbility(ChangedAbilities.HYPNOSIS)
            .build(Changed.modResource("form_latex_hypno_cat")));
    public static final LatexVariant<LatexKeonWolf> LATEX_KEON_WOLF = register(Builder.of(ChangedEntities.LATEX_KEON_WOLF)
            .groundSpeed(1.075f).swimSpeed(0.95f).stepSize(0.7f)
            .build(Changed.modResource("form_latex_keon_wolf")));
    public static final LatexVariant<LatexLeaf> LATEX_LEAF = register(Builder.of(ChangedEntities.LATEX_LEAF)
            .groundSpeed(1.05f).swimSpeed(0.95f).stepSize(0.7f).absorbing()
            .build(Changed.modResource("form_latex_leaf")));
    public static final LatexVariant<LatexMedusaCat> LATEX_MEDUSA_CAT = register(Builder.of(ChangedEntities.LATEX_MEDUSA_CAT)
            .groundSpeed(1.15f).swimSpeed(0.9f).stepSize(0.7f).breatheMode(BreatheMode.WEAK).reducedFall().scares(Creeper.class).nightVision().absorbing()
            .build(Changed.modResource("form_latex_medusa_cat")));
    public static final LatexVariant<LatexMimicPlant> LATEX_MIMIC_PLANT = register(Builder.of(ChangedEntities.LATEX_MIMIC_PLANT)
            .groundSpeed(1.05f).swimSpeed(0.95f).stepSize(0.7f).reducedFall().absorbing()
            .build(Changed.modResource("form_latex_mimic_plant")));
    public static final LatexVariant<LatexMingCat> LATEX_MING_CAT = register(Builder.of(ChangedEntities.LATEX_MING_CAT)
            .groundSpeed(1.15f).swimSpeed(0.9f).stepSize(0.7f).breatheMode(BreatheMode.WEAK).reducedFall().scares(Creeper.class).nightVision()
            .build(Changed.modResource("form_latex_ming_cat")));
    public static final LatexVariant<LatexMoth> LATEX_MOTH = register(Builder.of(ChangedEntities.LATEX_MOTH)
            .groundSpeed(1.05f).swimSpeed(0.75f).extraJumps(6).reducedFall().breatheMode(BreatheMode.WEAK)
            .build(Changed.modResource("form_latex_moth")));
    public static final LatexVariant<LatexOrca> LATEX_ORCA = register(Builder.of(ChangedEntities.LATEX_ORCA)
            .groundSpeed(0.875f).swimSpeed(1.40f).stepSize(0.7f).gills()
            .build(Changed.modResource("form_latex_orca")));
    public static final LatexVariant<LatexOtter> LATEX_OTTER = register(Builder.of(ChangedEntities.LATEX_OTTER)
            .groundSpeed(1.05f).swimSpeed(1.2f).breatheMode(BreatheMode.STRONG)
            .build(Changed.modResource("form_latex_otter")));
    public static final LatexVariant<LatexPinkDeer> LATEX_PINK_DEER = register(Builder.of(ChangedEntities.LATEX_PINK_DEER)
            .groundSpeed(1.075f).swimSpeed(0.95f).stepSize(0.7f)
            .build(Changed.modResource("form_latex_pink_deer")));
    public static final LatexVariant<LatexPinkWyvern> LATEX_PINK_WYVERN = register(Builder.of(ChangedEntities.LATEX_PINK_WYVERN)
            .groundSpeed(1.05f).swimSpeed(0.95f).stepSize(0.7f)
            .build(Changed.modResource("form_latex_pink_wyvern")));
    public static final LatexVariant<LatexPinkYuinDragon> LATEX_PINK_YUIN_DRAGON = register(Builder.of(ChangedEntities.LATEX_PINK_YUIN_DRAGON)
            .groundSpeed(1.0F).swimSpeed(0.85f).glide()
            .build(Changed.modResource("form_latex_pink_yuin_dragon")));
    public static final LatexVariant<LatexPurpleFox> LATEX_PURPLE_FOX = register(Builder.of(ChangedEntities.LATEX_PURPLE_FOX)
            .groundSpeed(1.075f).swimSpeed(0.95f).stepSize(0.7f)
            .build(Changed.modResource("form_latex_purple_fox")));
    public static final LatexVariant<LatexRaccoon> LATEX_RACCOON = register(Builder.of(ChangedEntities.LATEX_RACCOON)
            .groundSpeed(0.95f).swimSpeed(0.97f).noVision()
            .build(Changed.modResource("form_latex_raccoon")));
    public static final LatexVariant<LatexRedDragon> LATEX_RED_DRAGON = register(Builder.of(ChangedEntities.LATEX_RED_DRAGON)
            .groundSpeed(1.0F).swimSpeed(0.85f).glide()
            .build(Changed.modResource("form_latex_red_dragon")));
    public static final LatexVariant<LatexRedPanda> LATEX_RED_PANDA = register(Builder.of(ChangedEntities.LATEX_RED_PANDA)
            .groundSpeed(1.05f).swimSpeed(0.95f).stepSize(0.7f).reducedFall()
            .build(Changed.modResource("form_latex_red_panda")));
    public static final LatexVariant<LatexShark> LATEX_SHARK = register(Builder.of(ChangedEntities.LATEX_SHARK)
            .groundSpeed(0.875f).swimSpeed(1.40f).stepSize(0.7f).gills().absorbing()
            .build(Changed.modResource("form_latex_shark")));
    public static final LatexVariant<LatexSnake> LATEX_SNAKE = register(Builder.of(ChangedEntities.LATEX_SNAKE)
            .groundSpeed(1.0F).swimSpeed(0.95f).additionalHealth(6).stepSize(1.1f).noLegs().addAbility(ChangedAbilities.SLITHER)
            .build(Changed.modResource("form_latex_snake")));
    public static final LatexVariant<LatexSniperDog> LATEX_SNIPER_DOG = register(Builder.of(ChangedEntities.LATEX_SNIPER_DOG)
            .groundSpeed(1.075f).swimSpeed(0.95f).stepSize(0.7f).sound(ChangedSounds.SOUND3.getLocation())
            .build(Changed.modResource("form_latex_sniper_dog")));
    public static final LatexVariant<LatexSquirrel> LATEX_SQUIRREL = register(Builder.of(ChangedEntities.LATEX_SQUIRREL)
            .groundSpeed(1.1f).swimSpeed(0.9f).stepSize(0.7f).reducedFall()
            .build(Changed.modResource("form_latex_squirrel")));
    public static final LatexVariant<LatexStiger> LATEX_STIGER = register(Builder.of(ChangedEntities.LATEX_STIGER)
            .canClimb().extraHands().nightVision().addAbility(ChangedAbilities.CREATE_COBWEB)
            .build(Changed.modResource("form_latex_stiger")));
    public static final LatexVariant<LatexTigerShark> LATEX_TIGER_SHARK = register(Builder.of(ChangedEntities.LATEX_TIGER_SHARK)
            .groundSpeed(0.925f).swimSpeed(1.25f).additionalHealth(10).gills().addAbility(ChangedAbilities.SUMMON_SHARKS)
            .build(Changed.modResource("form_latex_tiger_shark")));
    public static final LatexVariant<LatexTrafficConeDragon> LATEX_TRAFFIC_CONE_DRAGON = register(Builder.of(ChangedEntities.LATEX_TRAFFIC_CONE_DRAGON)
            .groundSpeed(1.05f).swimSpeed(0.95f).stepSize(0.7f).absorbing()
            .build(Changed.modResource("form_latex_traffic_cone_dragon")));
    public static final LatexVariant<LatexTranslucentLizard> LATEX_TRANSLUCENT_LIZARD = register(Builder.of(ChangedEntities.LATEX_TRANSLUCENT_LIZARD)
            .groundSpeed(1.05f).swimSpeed(1.0f).stepSize(0.7f).absorbing()
            .build(Changed.modResource("form_latex_translucent_lizard")));
    public static final LatexVariant<LatexWatermelonCat> LATEX_WATERMELON_CAT = register(Builder.of(ChangedEntities.LATEX_WATERMELON_CAT)
            .groundSpeed(1.15f).swimSpeed(0.9f).stepSize(0.7f).breatheMode(BreatheMode.WEAK).reducedFall().scares(Creeper.class).nightVision().absorbing()
            .build(Changed.modResource("form_latex_watermelon_cat")));
    public static final LatexVariant<LatexWhiteTiger> LATEX_WHITE_TIGER = register(Builder.of(ChangedEntities.LATEX_WHITE_TIGER)
            .groundSpeed(1.15f).swimSpeed(0.9f).stepSize(0.7f).breatheMode(BreatheMode.WEAK).reducedFall().scares(Creeper.class).nightVision()
            .build(Changed.modResource("form_latex_white_tiger")));
    public static final LatexVariant<LatexYuin> LATEX_YUIN = register(Builder.of(ChangedEntities.LATEX_YUIN)
            .groundSpeed(1.05f).swimSpeed(0.98f).stepSize(0.7f).reducedFall().absorbing()
            .build(Changed.modResource("form_latex_yuin")));
    public static final LatexVariant<LightLatexCentaur> LIGHT_LATEX_CENTAUR = register(Builder.of(ChangedEntities.LIGHT_LATEX_CENTAUR)
            .groundSpeed(1.20f).swimSpeed(0.9f).stepSize(1.1f).additionalHealth(8).cameraZOffset(7.0f / 16.0f).rideable().reducedFall()
            .build(Changed.modResource("form_light_latex_centaur")));
    public static final LatexVariant<LightLatexKnight> LIGHT_LATEX_KNIGHT = register(Builder.of(ChangedEntities.LIGHT_LATEX_KNIGHT)
            .groundSpeed(1.075f).swimSpeed(0.95f).stepSize(0.7f).absorbing()
            .build(Changed.modResource("form_light_latex_knight")));
    public static final LatexVariant<LightLatexKnightFusion> LIGHT_LATEX_KNIGHT_FUSION = register(Builder.of(LIGHT_LATEX_KNIGHT, ChangedEntities.LIGHT_LATEX_KNIGHT_FUSION)
            .additionalHealth(8).fusionOf(LIGHT_LATEX_WOLF.male(), LIGHT_LATEX_KNIGHT)
            .build(Changed.modResource("form_light_latex_knight_fusion")));
    public static final LatexVariant<LightLatexWolfOrganic> LIGHT_LATEX_WOLF_ORGANIC = register(Builder.of(ChangedEntities.LIGHT_LATEX_WOLF_ORGANIC)
            .groundSpeed(1.075f).swimSpeed(0.95f).stepSize(0.7f).sound(ChangedSounds.SOUND3.getLocation())
            .build(Changed.modResource("form_light_latex_wolf_organic")));
    public static final LatexVariant<WhiteLatexWolf> WHITE_LATEX_WOLF = register(Builder.of(ChangedEntities.WHITE_LATEX_WOLF)
            .groundSpeed(1.05f).swimSpeed(0.9f).stepSize(0.7f).faction(LatexType.WHITE_LATEX)
            .build(Changed.modResource("form_white_latex_wolf")));

    public static final GenderedVariant<DarkLatexWolfMale, DarkLatexWolfFemale> DARK_LATEX_WOLF = register(GenderedVariant.Builder.of(LIGHT_LATEX_WOLF.male(), ChangedEntities.DARK_LATEX_WOLF_MALE, ChangedEntities.DARK_LATEX_WOLF_FEMALE)
            .split(Builder::ignored, Builder::absorbing).faction(LatexType.DARK_LATEX)
            .buildGendered(Changed.modResource("form_dark_latex_wolf")));
    public static final LatexVariant<DarkLatexPup> DARK_LATEX_PUP = register(Builder.of(DARK_LATEX_WOLF.male(), ChangedEntities.DARK_LATEX_PUP)
            .transfurMode(TransfurMode.NONE).holdItemsInMouth().additionalHealth(-8).groundSpeed(1.25F).reducedFall().addAbility(ChangedAbilities.PUDDLE)
            .build(Changed.modResource("form_dark_latex_pup")));
    public static final GenderedVariant<LatexMantaRayMale, LatexMantaRayFemale> LATEX_MANTA_RAY = register(GenderedVariant.Builder.of(LatexVariant.LATEX_SHARK, ChangedEntities.LATEX_MANTA_RAY_MALE, ChangedEntities.LATEX_MANTA_RAY_FEMALE)
            .split(Builder::ignored, female -> female.groundSpeed(0.26F).swimSpeed(2.9F).absorbing().additionalHealth(8).noLegs())
            .buildGendered(Changed.modResource("form_latex_manta_ray")));
    public static final GenderedVariant<LatexMermaidShark, LatexSiren> LATEX_MERMAID_SHARK = register(GenderedVariant.Builder.of(LatexVariant.LATEX_SHARK, ChangedEntities.LATEX_MERMAID_SHARK, ChangedEntities.LATEX_SIREN)
            .groundSpeed(0.26F).swimSpeed(2.9F).split(male -> male.replicating(), female -> female.absorbing().addAbility(ChangedAbilities.SIREN_SING)).additionalHealth(8).noLegs()
            .buildGendered(Changed.modResource("form_latex_mermaid_shark")));
    public static final GenderedVariant<LatexSharkMale, LatexSharkFemale> LATEX_SHARK_FUSION = register(GenderedVariant.Builder.of(LATEX_SHARK, ChangedEntities.LATEX_SHARK_MALE, ChangedEntities.LATEX_SHARK_FEMALE)
            .groundSpeed(0.9f).swimSpeed(1.5f).stepSize(0.7f).additionalHealth(8).split(Builder::ignored, Builder::absorbing).fusionOf(LATEX_SHARK, LATEX_SHARK)
            .buildGendered(Changed.modResource("form_latex_shark")));
    public static final GenderedVariant<LatexSnowLeopardMale, LatexSnowLeopardFemale> LATEX_SNOW_LEOPARD = register(GenderedVariant.Builder.of(ChangedEntities.LATEX_SNOW_LEOPARD_MALE, ChangedEntities.LATEX_SNOW_LEOPARD_FEMALE)
            .groundSpeed(1.15f).swimSpeed(0.9f).stepSize(0.7f).breatheMode(BreatheMode.WEAK).reducedFall().scares(Creeper.class).split(Builder::ignored, Builder::absorbing).nightVision()
            .buildGendered(Changed.modResource("form_latex_snow_leopard")));
    public static final GenderedVariant<LatexSquidDogMale, LatexSquidDogFemale> LATEX_SQUID_DOG = register(GenderedVariant.Builder.of(ChangedEntities.LATEX_SQUID_DOG_MALE, ChangedEntities.LATEX_SQUID_DOG_FEMALE)
            .groundSpeed(0.925f).swimSpeed(1.1f).additionalHealth(10).gills().extraHands().split(male -> male.replicating(), female -> female.absorbing()).addAbility(ChangedAbilities.CREATE_INKBALL)
            .buildGendered(Changed.modResource("form_latex_squid_dog")));

    public static final LatexVariant<?> FALLBACK_VARIANT = LIGHT_LATEX_WOLF.male();

    public ResourceLocation getFormId() {
        return getRegistryName();
    }

    public boolean isReducedFall() {
        return reducedFall;
    }

    public TransfurMode transfurMode() { return transfurMode; }

    public boolean isGendered() {
        for (Gender g : Gender.values())
            if (this.getFormId().getPath().endsWith(g.name().toLowerCase(Locale.ROOT)))
                return true;
        return false;
    }

    public int getTicksRequiredToFreeze(Level level) {
        return ChangedEntities.getCachedEntity(level, ctor.get()).getTicksRequiredToFreeze();
    }

    public boolean isFusionOf(LatexVariant<?> variantA, LatexVariant<?> variantB) {
        if (variantA == null || variantB == null)
            return false;

        if (fusionOf.isPresent()) {
            return
                    (fusionOf.get().getFirst().getFormId().equals(variantA.getFormId()) &&
                            fusionOf.get().getSecond().getFormId().equals(variantB.getFormId())) ||

                    (fusionOf.get().getSecond().getFormId().equals(variantA.getFormId()) &&
                            fusionOf.get().getFirst().getFormId().equals(variantB.getFormId()));
        }

        return false;
    }

    public boolean isFusionOf(LatexVariant<?> variantA, Class<? extends LivingEntity> clazz) {
        if (variantA == null || clazz == null)
            return false;

        if (mobFusionOf.isPresent()) {
            return mobFusionOf.get().getFirst().getFormId().equals(variantA.getFormId()) &&
                            mobFusionOf.get().getSecond().isAssignableFrom(clazz);
        }

        return false;
    }

    public float swimMultiplier() {
        return swimSpeed;
    }

    public float landMultiplier() {
        return groundSpeed;
    }

    public boolean is(@Nullable LatexVariant<?> variant) {
        if (variant == null)
            return false;
        return getEntityType() == variant.getEntityType();
    }

    public boolean is(TagKey<LatexVariant<?>> tagKey) {
        return ChangedRegistry.LATEX_VARIANT.get().tags().getTag(tagKey).contains(this);
    }

    public enum BreatheMode {
        NORMAL,
        WEAK,
        STRONG,
        WATER,
        ANY;

        public boolean canBreatheWater() {
            return this == WATER || this == ANY;
        }

        public boolean canBreatheAir() {
            return this == NORMAL || this == ANY || this == WEAK || this == STRONG;
        }

        public boolean hasAquaAffinity() { return canBreatheWater(); }
    }

    private static final AtomicInteger NEXT_ENTITY_ID = new AtomicInteger(-70000000);

    public static int getNextEntId() {
        return NEXT_ENTITY_ID.getAndDecrement();
    }

    public String toString() {
        return getRegistryName().toString();
    }

    // Variant properties
    public final Supplier<EntityType<T>> ctor;
    public final LatexType type;
    public final float groundSpeed;
    public final float swimSpeed;
    public final float jumpStrength;
    public final BreatheMode breatheMode;
    public final float stepSize;
    public final boolean canGlide;
    public final int extraJumpCharges;
    public final int additionalHealth;
    public final boolean reducedFall;
    public final boolean canClimb;
    public final boolean nightVision;
    public final boolean noVision;
    public final boolean hasLegs;
    public final UseItemMode itemUseMode;
    public final List<Class<? extends PathfinderMob>> scares;
    public final TransfurMode transfurMode;
    public final Optional<Pair<LatexVariant<?>, LatexVariant<?>>> fusionOf;
    public final Optional<Pair<LatexVariant<?>, Class<? extends LivingEntity>>> mobFusionOf;
    public final ImmutableList<Function<EntityType<?>, ? extends AbstractAbility<?>>> abilities;
    public final float cameraZOffset;
    public final ResourceLocation sound;

    public LatexVariant(Supplier<EntityType<T>> ctor, LatexType type, float groundSpeed, float swimSpeed,
                        float jumpStrength, BreatheMode breatheMode, float stepSize, boolean canGlide, int extraJumpCharges, int additionalHealth,
                        boolean reducedFall, boolean canClimb,
                        boolean nightVision, boolean noVision, boolean hasLegs, UseItemMode itemUseMode, List<Class<? extends PathfinderMob>> scares, TransfurMode transfurMode,
                        Optional<Pair<LatexVariant<?>, LatexVariant<?>>> fusionOf,
                        Optional<Pair<LatexVariant<?>, Class<? extends LivingEntity>>> mobFusionOf, List<Function<EntityType<?>, ? extends AbstractAbility<?>>> abilities, float cameraZOffset, ResourceLocation sound) {
        this.ctor = ctor;
        this.type = type;
        this.groundSpeed = groundSpeed;
        this.swimSpeed = swimSpeed;
        this.jumpStrength = jumpStrength;
        this.breatheMode = breatheMode;
        this.stepSize = stepSize;
        this.noVision = noVision;
        this.canGlide = canGlide;
        this.extraJumpCharges = extraJumpCharges;
        this.additionalHealth = additionalHealth;
        this.nightVision = nightVision;
        this.hasLegs = hasLegs;
        this.itemUseMode = itemUseMode;
        this.abilities = ImmutableList.<Function<EntityType<?>, ? extends AbstractAbility<?>>>builder().addAll(abilities).build();
        this.reducedFall = reducedFall;
        this.canClimb = canClimb;
        this.scares = scares;
        this.transfurMode = transfurMode;
        this.fusionOf = fusionOf;
        this.mobFusionOf = mobFusionOf;
        this.cameraZOffset = cameraZOffset;
        this.sound = sound;
    }

    public LatexType getLatexType() {
        return type;
    }

    private T createLatexEntity(Level level) {
        T entity = ctor.get().create(level);
        entity.setId(getNextEntId()); //to prevent ID collision
        entity.setSilent(true);
        entity.goalSelector.removeAllGoals();
        return entity;
    }

    public EntityType<T> getEntityType() {
        return ctor.get();
    }

    public T generateForm(@NotNull Player player, Level level) {
        T latexForm = createLatexEntity(level);
        latexForm.moveTo((player.getX()), (player.getY()), (player.getZ()), player.getYRot(), 0);
        if (latexForm instanceof SpecialLatex specialLatex)
            specialLatex.setSpecialLatexForm(UUID.fromString(
                    getFormId().toString().substring(Changed.modResourceStr("special/form_").length())));

        latexForm.setCustomName(PatreonBenefits.getPlayerName(player));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            if (player == Minecraft.getInstance().player)
                latexForm.setCustomNameVisible(false);
        });

        return latexForm;
    }


    public LatexEntity spawnAtEntity(@NotNull LivingEntity entity) {
        LatexEntity newEntity = ctor.get().create(entity.level);
        newEntity.finalizeSpawn((ServerLevelAccessor) entity.level, entity.level.getCurrentDifficultyAt(newEntity.blockPosition()), MobSpawnType.MOB_SUMMONED, null,
                null);
        newEntity.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
        entity.level.addFreshEntity(newEntity);
        if (newEntity instanceof SpecialLatex specialLatex) {
            specialLatex.setSpecialLatexForm(UUID.fromString(
                    getFormId().toString().substring(Changed.modResourceStr("special/form_").length())));
        }
        return newEntity;
    }

    public LatexEntity replaceEntity(@NotNull LivingEntity entity) {
        var newEntity = spawnAtEntity(entity);
        if (entity.hasCustomName()) {
            newEntity.setCustomName(entity.getCustomName());
            newEntity.setCustomNameVisible(entity.isCustomNameVisible());
        }

        if (entity instanceof Mob mob) {
            newEntity.setLeftHanded(mob.isLeftHanded());
        }

        if (entity instanceof Player player) {
            ProcessTransfur.killPlayerBy(player, newEntity);
        } else {
            // Take armor
            Arrays.stream(EquipmentSlot.values()).filter(slot -> slot.getType() == EquipmentSlot.Type.ARMOR).forEach(slot -> {
                newEntity.setItemSlot(slot, entity.getItemBySlot(slot).copy());
            });
            // Drop held items
            Arrays.stream(EquipmentSlot.values()).filter(slot -> slot.getType() == EquipmentSlot.Type.HAND).forEach(slot -> {
                if (entity.getRandom().nextFloat() < 0.05f) // 5% Drop rate
                    Block.popResource(entity.level, entity.blockPosition(), entity.getItemBySlot(slot).copy());
            });

            entity.discard();
        }
        return newEntity;
    }

    public BreatheMode getBreatheMode() {
        return breatheMode;
    }

    public boolean canDoubleJump() { return extraJumpCharges > 0; }

    public boolean rideable() { return this.abilities.contains(ChangedAbilities.ACCESS_SADDLE); }

    public static class Builder<T extends LatexEntity> {
        final Supplier<EntityType<T>> entityType;
        LatexType type = LatexType.NEUTRAL;
        float groundSpeed = 1.0F;
        float swimSpeed = 1.0F;
        float jumpStrength = 1.0F;
        BreatheMode breatheMode = BreatheMode.NORMAL;
        float stepSize = 0.6F;
        boolean canGlide = false;
        int extraJumpCharges = 0;
        int additionalHealth = 4;
        boolean reducedFall = false;
        boolean noVision = false;
        boolean canClimb = false;
        boolean nightVision = false;
        boolean hasLegs = true;
        UseItemMode itemUseMode = UseItemMode.NORMAL;
        List<Class<? extends PathfinderMob>> scares = new ArrayList<>(ImmutableList.of(AbstractVillager.class));
        TransfurMode transfurMode = TransfurMode.REPLICATION;
        Optional<Pair<LatexVariant<?>, LatexVariant<?>>> fusionOf = Optional.empty();
        Optional<Pair<LatexVariant<?>, Class<? extends LivingEntity>>> mobFusionOf = Optional.empty();
        List<Function<EntityType<?>, ? extends AbstractAbility<?>>> abilities = new ArrayList<>();
        float cameraZOffset = 0.0F;
        ResourceLocation sound = ChangedSounds.POISON.getLocation();

        public Builder(Supplier<EntityType<T>> entityType) {
            this.entityType = entityType;
            // vvv-- Add universal abilities here --vvv
            this.abilities.add(type -> type.is(ChangedTags.EntityTypes.ORGANIC_LATEX) ? null : ChangedAbilities.SWITCH_TRANSFUR_MODE.get());
            //this.abilities.add(type -> type.is(ChangedTags.EntityTypes.ORGANIC_LATEX) ? null : ChangedAbilities.HOLD_ENTITY_ABILITY.get());
            this.abilities.add(type -> ChangedAbilities.SELECT_HAIRSTYLE.get());
        }

        private void ignored() {}

        public static <T extends LatexEntity> Builder<T> of(Supplier<EntityType<T>> entityType) {
            return new Builder<T>(entityType);
        }

        public static <T extends LatexEntity> Builder<T> of(LatexVariant<?> variant, Supplier<EntityType<T>> entityType) {
            return (new Builder<T>(entityType)).faction(variant.type).groundSpeed(variant.groundSpeed).swimSpeed(variant.swimSpeed)
                    .jumpStrength(variant.jumpStrength).breatheMode(variant.breatheMode).stepSize(variant.stepSize).glide(variant.canGlide).extraJumps(variant.extraJumpCharges)
                    .abilities(variant.abilities).reducedFall(variant.reducedFall).canClimb(variant.canClimb).nightVision(variant.nightVision).hasLegs(variant.hasLegs).scares(variant.scares)
                    .transfurMode(variant.transfurMode).cameraZOffset(variant.cameraZOffset).noVision(variant.noVision).itemUseMode(variant.itemUseMode);
        }

        public static <T extends LatexEntity> Builder<T> of(GenderedVariant<?, ?> variant, Supplier<EntityType<T>> entityType) {
            throw new InvalidParameterException("Invalid variant supplied");
        }

        public Builder<T> faction(LatexType type) {
            this.type = type; return this;
        }

        public Builder<T> groundSpeed(float factor) {
            this.groundSpeed = factor; return this;
        }

        public Builder<T> swimSpeed(float factor) {
            this.swimSpeed = factor; return this;
        }

        public Builder<T> jumpStrength(float factor) {
            this.jumpStrength = factor; return this;
        }

        public Builder<T> gills() {
            return gills(false);
        }

        public Builder<T> gills(boolean suffocate_on_land) {
            this.breatheMode = suffocate_on_land ? BreatheMode.WATER : BreatheMode.ANY; return this;
        }

        public Builder<T> breatheMode(BreatheMode mode) {
            this.breatheMode = mode; return this;
        }

        public Builder<T> reducedFall() {
            this.reducedFall = true; return this;
        }

        public Builder<T> noVision() {
            this.noVision = true; return this;
        }

        public Builder<T> noVision(boolean v) {
            this.noVision = v; return this;
        }

        public Builder<T> reducedFall(boolean v) {
            this.reducedFall = v; return this;
        }

        public Builder<T> canClimb() {
            this.canClimb = true; return this;
        }

        public Builder<T> canClimb(boolean v) {
            this.canClimb = v; return this;
        }

        public <E extends PathfinderMob> Builder<T> scares(Class<E> type) {
            scares.add(type); return this;
        }

        public Builder<T> scares(List<Class<? extends PathfinderMob>> v) {
            scares = v; return this;
        }

        public Builder<T> stepSize(float factor) {
            this.stepSize = factor; return this;
        }

        public Builder<T> glide() {
            return glide(true);
        }

        public Builder<T> glide(boolean enable) {
            this.canGlide = enable; return this;
        }

        public Builder<T> doubleJump() {
            return extraJumps(1);
        }

        public Builder<T> extraJumps(int count) {
            this.extraJumpCharges = count; return this;
        }

        public Builder<T> additionalHealth(int value) {
            this.additionalHealth = value; return this;
        }
        
        public Builder<T> addAbility(Function<EntityType<?>, ? extends AbstractAbility<?>> ability) {
            if (ability != null)
                this.abilities.add(ability);
            return this;
        }
        
        public Builder<T> addAbility(Supplier<? extends AbstractAbility<?>> ability) {
            if (ability != null)
                this.abilities.add(type -> ability.get());
            return this;
        }
        
        public Builder<T> abilities(List<Function<EntityType<?>, ? extends AbstractAbility<?>>> abilities) {
            this.abilities = new ArrayList<>(abilities); return this;
        }

        public Builder<T> extraHands() {
            return addAbility(ChangedAbilities.EXTRA_HANDS).addAbility(ChangedAbilities.SWITCH_HANDS);
        }

        public Builder<T> rideable() {
            return addAbility(ChangedAbilities.ACCESS_SADDLE).addAbility(ChangedAbilities.ACCESS_CHEST);
        }

        public Builder<T> absorbing() {
            return transfurMode(TransfurMode.ABSORPTION);
        }
        
        public Builder<T> replicating() {
            return transfurMode(TransfurMode.REPLICATION);
        }

        public Builder<T> nightVision() {
            this.nightVision = true; return this;
        }

        public Builder<T> nightVision(boolean v) {
            this.nightVision = v; return this;
        }


        public Builder<T> transfurMode(TransfurMode mode) {
            this.transfurMode = mode; return this;
        }

        public Builder<T> fusionOf(LatexVariant<?> formA, LatexVariant<?> formB) {
            this.fusionOf = Optional.of(Pair.of(formA, formB)); return this;
        }

        public Builder<T> fusionOf(LatexVariant<?> formA, Class<? extends LivingEntity> mobClass) {
            this.mobFusionOf = Optional.of(Pair.of(formA, mobClass)); return this;
        }

        public Builder<T> noLegs() {
            this.hasLegs = false;
            return this;
        }

        public Builder<T> hasLegs(boolean v) {
            this.hasLegs = v;
            return this;
        }

        public Builder<T> disableItems() {
            this.itemUseMode = UseItemMode.NONE;
            return this;
        }

        public Builder<T> holdItemsInMouth() {
            this.itemUseMode = UseItemMode.MOUTH;
            return this;
        }

        public Builder<T> itemUseMode(UseItemMode v) {
            this.itemUseMode = v;
            return this;
        }

        public Builder<T> cameraZOffset(float v) {
            this.cameraZOffset = v; return this;
        }

        public Builder<T> sound(ResourceLocation event) {
            this.sound = event; return this;
        }

        public LatexVariant<T> build(ResourceLocation formId) {
            var variant = new LatexVariant<>(entityType, type, groundSpeed, swimSpeed, jumpStrength, breatheMode, stepSize, canGlide, extraJumpCharges, additionalHealth,
                    reducedFall, canClimb, nightVision, noVision, hasLegs, itemUseMode, scares, transfurMode, fusionOf, mobFusionOf, abilities, cameraZOffset, sound);
            variant.setRegistryName(formId);
            return variant;
        }
    }

    @SubscribeEvent
    public static void onRegister(RegistryEvent.Register<LatexVariant<?>> event) {
        var registry = event.getRegistry();
        ALL_LATEX_FORMS.values().forEach(registry::register);
    }

    public static <T extends LatexEntity> LatexVariant<T> register(LatexVariant<T> variant) {
        ALL_LATEX_FORMS.put(variant.getFormId(), variant);
        PUBLIC_LATEX_FORMS.add(variant.getFormId());
        if (variant.type != LatexType.NEUTRAL)
            VARIANTS_BY_TYPE.computeIfAbsent(variant.type, t -> new ArrayList<>()).add(variant.getFormId());
        if (variant.fusionOf.isPresent())
            FUSION_LATEX_FORMS.add(variant.getFormId());
        if (variant.mobFusionOf.isPresent())
            MOB_FUSION_LATEX_FORMS.add(variant.getFormId());
        return variant;
    }

    public static <M extends LatexEntity & GenderedEntity, F extends LatexEntity & GenderedEntity> GenderedVariant<M, F> register(GenderedVariant<M, F> variant) {
        ALL_LATEX_FORMS.put(variant.male.getFormId(), variant.male);
        ALL_LATEX_FORMS.put(variant.female.getFormId(), variant.female);
        PUBLIC_LATEX_FORMS.add(variant.male.getFormId());
        PUBLIC_LATEX_FORMS.add(variant.female.getFormId());
        if (variant.male.type != LatexType.NEUTRAL)
            VARIANTS_BY_TYPE.computeIfAbsent(variant.male.type, t -> new ArrayList<>()).add(variant.male.getFormId());
        if (variant.female.type != LatexType.NEUTRAL)
            VARIANTS_BY_TYPE.computeIfAbsent(variant.female.type, t -> new ArrayList<>()).add(variant.female.getFormId());
        if (variant.male.fusionOf.isPresent())
            FUSION_LATEX_FORMS.add(variant.male.getFormId());
        if (variant.female.fusionOf.isPresent())
            FUSION_LATEX_FORMS.add(variant.female.getFormId());
        if (variant.male.mobFusionOf.isPresent())
            MOB_FUSION_LATEX_FORMS.add(variant.male.getFormId());
        if (variant.female.mobFusionOf.isPresent())
            MOB_FUSION_LATEX_FORMS.add(variant.female.getFormId());
        return variant;
    }

    public static <T extends LatexEntity> LatexVariant<T> registerSpecial(LatexVariant<T> variant) {
        ALL_LATEX_FORMS.put(variant.getFormId(), variant);
        SPECIAL_LATEX_FORMS.add(variant.getFormId());
        return variant;
    }

    public static LatexVariant<?> findLatexEntityVariant(LatexEntity entity) {
        for (LatexVariant<?> variant : ALL_LATEX_FORMS.values())
            if (variant.ctor != null && variant.ctor.get().equals(entity.getType()))
                return variant;
        return null;
    }

    public static LatexVariant<?> getEntityTransfur(LivingEntity entity) {
        return ProcessTransfur.ifPlayerLatex(EntityUtil.playerOrNull(entity),
                variant -> variant.getLatexEntity().getTransfurVariant(), () -> {
            if (entity instanceof LatexEntity latexEntity)
                return latexEntity.getTransfurVariant();
            return null;
        });
    }

    public static LatexVariant<?> getEntityVariant(LivingEntity entity) {
        return ProcessTransfur.ifPlayerLatex(EntityUtil.playerOrNull(entity),
                LatexVariantInstance::getParent,
                () -> {
                    if (entity instanceof LatexEntity latexEntity)
                        return latexEntity.getSelfVariant();
                    return null;
                });
    }

    // Parses variant from JSON, does not register variant
    public static LatexVariant<?> fromJson(ResourceLocation id, JsonObject root) {
        return fromJson(id, root, List.of());
    }

    public static LatexVariant<?> fromJson(ResourceLocation id, JsonObject root, List<AbstractAbility<?>> injectAbilities) {
        ResourceLocation entityType = ResourceLocation.tryParse(GsonHelper.getAsString(root, "entity", ChangedEntities.SPECIAL_LATEX.getId().toString()));

        List<Class<? extends PathfinderMob>> scares = new ArrayList<>(ImmutableList.of(AbstractVillager.class));
        GsonHelper.getAsJsonArray(root, "scares", new JsonArray()).forEach(element -> {
            try {
                scares.add((Class<? extends PathfinderMob>) Class.forName(element.getAsString()));
            } catch (Exception e) {
                Changed.LOGGER.error("Invalid class given: {}", element.getAsString());
            }
        });

        List<LatexVariant<?>> fusionOf = new ArrayList<>();
        GsonHelper.getAsJsonArray(root, "fusionOf", new JsonArray()).forEach(element -> {
            fusionOf.add(ALL_LATEX_FORMS.get(ResourceLocation.tryParse(element.getAsString())));
        });

        AtomicReference<LatexVariant<?>> mobFusionLatex = new AtomicReference<>(null);
        AtomicReference<Class<? extends LivingEntity>> mobFusionMob = null;
        GsonHelper.getAsJsonArray(root, "mobFusionOf", new JsonArray()).forEach(element -> {
            mobFusionLatex.set(ALL_LATEX_FORMS.getOrDefault(ResourceLocation.tryParse(element.getAsString()), mobFusionLatex.get()));
            try {
                mobFusionMob.compareAndSet(null, (Class<? extends LivingEntity>)Class.forName(element.getAsString()));
            } catch (ClassNotFoundException ignored) {}
        });

        List<AbstractAbility<?>> abilities = new ArrayList<>(injectAbilities);
        GsonHelper.getAsJsonArray(root, "abilities", new JsonArray()).forEach(element -> {
            abilities.add(ChangedRegistry.ABILITY.get().getValue(ResourceLocation.tryParse(element.getAsString())));
        });

        List<Function<EntityType<?>, ? extends AbstractAbility<?>>> nAbilitiesList = abilities.stream().map(a -> (Function<EntityType<?>, AbstractAbility<?>>) type -> a).collect(Collectors.toList());

        return new LatexVariant<>(
                () -> (EntityType<LatexEntity>) Registry.ENTITY_TYPE.get(entityType),
                LatexType.valueOf(GsonHelper.getAsString(root, "latexType", LatexType.NEUTRAL.toString())),
                GsonHelper.getAsFloat(root, "groundSpeed", 1.0f),
                GsonHelper.getAsFloat(root, "swimSpeed", 1.0f),
                GsonHelper.getAsFloat(root, "jumpStrength", 1.0f),
                BreatheMode.valueOf(GsonHelper.getAsString(root, "breatheMode", BreatheMode.NORMAL.toString())),
                GsonHelper.getAsFloat(root, "stepSize", 0.6f),
                GsonHelper.getAsBoolean(root, "canGlide", false),
                GsonHelper.getAsInt(root, "extraJumpCharges", 0),
                GsonHelper.getAsInt(root, "additionalHealth", 4),
                GsonHelper.getAsBoolean(root, "reducedFall", false),
                GsonHelper.getAsBoolean(root, "canClimb", false),
                GsonHelper.getAsBoolean(root, "nightVision", false),
                GsonHelper.getAsBoolean(root, "noVision", false),
                GsonHelper.getAsBoolean(root, "hasLegs", true),
                UseItemMode.valueOf(GsonHelper.getAsString(root, "itemUseMode", UseItemMode.NORMAL.toString())),
                scares,
                TransfurMode.valueOf(GsonHelper.getAsString(root, "transfurMode", TransfurMode.REPLICATION.toString())),
                fusionOf.size() < 2 ? Optional.empty() : Optional.of(new Pair<>(
                        fusionOf.get(0), fusionOf.get(1)
                )),
                mobFusionLatex.get() != null && mobFusionMob.get() != null ? Optional.of(new Pair<>(mobFusionLatex.getAcquire(), mobFusionMob.getAcquire())) : Optional.empty(),
                nAbilitiesList,
                GsonHelper.getAsFloat(root, "cameraZOffset", 0.0F),
                ResourceLocation.tryParse(GsonHelper.getAsString(root, "sound", ChangedSounds.POISON.getLocation().toString()))).setRegistryName(id);
    }


    public Pair<Color3, Color3> getColors() {
        var ints = ChangedEntities.getEntityColor(getEntityType().getRegistryName());
        return new Pair<>(
                Color3.fromInt(ints.getFirst()),
                Color3.fromInt(ints.getSecond()));
    }
}
