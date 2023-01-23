package net.ltxprogrammer.changed.entity.variant;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.entity.GenderedEntity;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.beast.*;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Changed.MODID)
public class LatexVariant<T extends LatexEntity> {
    public static final String NBT_PLAYER_ID = "changed:player_id";

    public static final ResourceLocation SPECIAL_LATEX = Changed.modResource("form_special");

    public static Map<ResourceLocation, LatexVariant<?>> ALL_LATEX_FORMS = new HashMap<>();
    public static Map<ResourceLocation, LatexVariant<?>> PUBLIC_LATEX_FORMS = new HashMap<>();
    public static Map<ResourceLocation, LatexVariant<?>> FUSION_LATEX_FORMS = new HashMap<>();
    public static Map<ResourceLocation, LatexVariant<?>> MOB_FUSION_LATEX_FORMS = new HashMap<>();
    public static Map<ResourceLocation, LatexVariant<?>> SPECIAL_LATEX_FORMS = new HashMap<>();

    public static List<LatexVariant<?>> getFusionCompatible(LatexVariant<?> source, LatexVariant<?> other) {
        List<LatexVariant<?>> list = new ArrayList<>();
        FUSION_LATEX_FORMS.forEach((name, variant) -> {
            if (variant.isFusionOf(source, other))
                list.add(variant);
        });
        return list;
    }

    public static List<LatexVariant<?>> getFusionCompatible(LatexVariant<?> source, Class<? extends LivingEntity> clazz) {
        List<LatexVariant<?>> list = new ArrayList<>();
        MOB_FUSION_LATEX_FORMS.forEach((name, variant) -> {
            if (variant.isFusionOf(source, clazz))
                list.add(variant);
        });
        return list;
    }
    
    public static final LatexVariant<LatexSilverFox> LATEX_SILVER_FOX = register(Builder.of(ChangedEntities.LATEX_SILVER_FOX)
            .groundSpeed(1.075f).swimSpeed(0.85f).stepSize(0.7f)
            .build(Changed.modResource("form_latex_silver_fox")));
    
    public static final GenderedVariant<LightLatexWolfMale, LightLatexWolfFemale> LIGHT_LATEX_WOLF = register(GenderedVariant.Builder.of(ChangedEntities.LIGHT_LATEX_WOLF_MALE, ChangedEntities.LIGHT_LATEX_WOLF_FEMALE)
            .addAbility(ChangedAbilities.SWITCH_GENDER).split(Builder::ignored, Builder::absorbing)
            .buildGendered(Changed.modResource("form_light_latex_wolf")));
    public static final LatexVariant<LightLatexKnight> LIGHT_LATEX_KNIGHT = register(Builder.of(LATEX_SILVER_FOX, ChangedEntities.LIGHT_LATEX_KNIGHT).absorbing()
            .build(Changed.modResource("form_light_latex_knight")));
    public static final LatexVariant<LatexBlueWolf> LATEX_BLUE_WOLF = register(Builder.of(LATEX_SILVER_FOX, ChangedEntities.LATEX_BLUE_WOLF).absorbing()
            .build(Changed.modResource("form_latex_blue_wolf")));
    public static final LatexVariant<LightLatexKnightFusion> LIGHT_LATEX_KNIGHT_FUSION = register(Builder.of(LIGHT_LATEX_KNIGHT, ChangedEntities.LIGHT_LATEX_KNIGHT_FUSION).additionalHealth(8)
            .fusionOf(LIGHT_LATEX_WOLF.male(), LIGHT_LATEX_KNIGHT)
            .build(Changed.modResource("form_light_latex_knight_fusion")));
    public static final LatexVariant<LightLatexCentaur> LIGHT_LATEX_CENTAUR = register(Builder.of(LATEX_SILVER_FOX, ChangedEntities.LIGHT_LATEX_CENTAUR).groundSpeed(1.20f).swimSpeed(0.8f).stepSize(1.1f).additionalHealth(8).cameraZOffset(7.0f / 16.0f).rideable().reducedFall().fusionOf(LIGHT_LATEX_WOLF.male(), AbstractHorse.class)
            .build(Changed.modResource("form_light_latex_centaur")));
    public static final LatexVariant<AerosolLatexWolf> AEROSOL_LATEX_WOLF = register(Builder.of(LATEX_SILVER_FOX, ChangedEntities.AEROSOL_LATEX_WOLF).sound(ChangedSounds.SOUND3.getLocation())
            .build(Changed.modResource("form_aerosol_latex_wolf")));
    public static final GenderedVariant<DarkLatexWolfMale, DarkLatexWolfFemale> DARK_LATEX_WOLF = register(GenderedVariant.Builder.of(LATEX_SILVER_FOX, ChangedEntities.DARK_LATEX_WOLF_MALE, ChangedEntities.DARK_LATEX_WOLF_FEMALE)
            .split(Builder::ignored, Builder::absorbing).faction(LatexType.DARK_LATEX).buildGendered(Changed.modResource("form_dark_latex_wolf")));
    public static final LatexVariant<WhiteLatexWolf> WHITE_LATEX_WOLF = register(Builder.of(LATEX_SILVER_FOX, ChangedEntities.WHITE_LATEX_WOLF).faction(LatexType.WHITE_LATEX)
            .build(Changed.modResource("form_white_latex_wolf")));
    public static final LatexVariant<LatexPurpleFox> LATEX_PURPLE_FOX = register(Builder.of(LATEX_SILVER_FOX, ChangedEntities.LATEX_PURPLE_FOX)
            .build(Changed.modResource("form_latex_purple_fox")));
    public static final LatexVariant<LatexCrystalWolf> LATEX_CRYSTAL_WOLF = register(Builder.of(LATEX_SILVER_FOX, ChangedEntities.LATEX_CRYSTAL_WOLF).sound(ChangedSounds.SOUND3.getLocation())
            .build(Changed.modResource("form_latex_crystal_wolf")));
    public static final LatexVariant<LatexSniperDog> LATEX_SNIPER_DOG = register(Builder.of(LATEX_SILVER_FOX, ChangedEntities.LATEX_SNIPER_DOG).sound(ChangedSounds.SOUND3.getLocation())
            .build(Changed.modResource("form_latex_sniper_dog")));
    public static final LatexVariant<LightLatexWolfOrganic> LIGHT_LATEX_WOLF_ORGANIC = register(Builder.of(LATEX_SILVER_FOX, ChangedEntities.LIGHT_LATEX_WOLF_ORGANIC).sound(ChangedSounds.SOUND3.getLocation())
            .build(Changed.modResource("form_light_latex_wolf_organic")));
    public static final LatexVariant<LatexTrafficConeDragon> LATEX_TRAFFIC_CONE_DRAGON = register(Builder.of(LATEX_SILVER_FOX, ChangedEntities.LATEX_TRAFFIC_CONE_DRAGON)
            .build(Changed.modResource("form_latex_traffic_cone_dragon")));
    public static final LatexVariant<LatexLeaf> LATEX_LEAF = register(Builder.of(LATEX_TRAFFIC_CONE_DRAGON, ChangedEntities.LATEX_LEAF)
            .build(Changed.modResource("form_latex_leaf")));
    public static final LatexVariant<LatexMimicPlant> LATEX_MIMIC_PLANT = register(Builder.of(LATEX_LEAF, ChangedEntities.LATEX_MIMIC_PLANT)
            .build(Changed.modResource("form_latex_mimic_plant")));
    public static final GenderedVariant<LatexSnowLeopardMale, LatexSnowLeopardFemale> LATEX_SNOW_LEOPARD = register(GenderedVariant.Builder.of(ChangedEntities.LATEX_SNOW_LEOPARD_MALE, ChangedEntities.LATEX_SNOW_LEOPARD_FEMALE)
            .groundSpeed(1.15f).swimSpeed(0.8f).stepSize(0.7f).weakLungs().reducedFall().scares(Creeper.class).split(Builder::ignored, Builder::absorbing).nightVision()
            .buildGendered(Changed.modResource("form_latex_snow_leopard")));
    public static final LatexVariant<LatexWatermelonCat> LATEX_WATERMELON_CAT = register(Builder.of(LATEX_SNOW_LEOPARD.male(), ChangedEntities.LATEX_WATERMELON_CAT)
            .build(Changed.modResource("form_latex_watermelon_cat")));
    public static final LatexVariant<LatexWhiteTiger> LATEX_WHITE_TIGER = register(Builder.of(LATEX_SNOW_LEOPARD.male(), ChangedEntities.LATEX_WHITE_TIGER)
            .build(Changed.modResource("form_latex_white_tiger")));
    public static final LatexVariant<LatexHypnoCat> LATEX_HYPNO_CAT = register(Builder.of(LATEX_SNOW_LEOPARD.male(), ChangedEntities.LATEX_HYPNO_CAT).addAbility(ChangedAbilities.USE_VARIANT_EFFECT)
            .build(Changed.modResource("form_latex_hypno_cat")));

    public static final LatexVariant<LatexShark> LATEX_SHARK = register(Builder.of(ChangedEntities.LATEX_SHARK).groundSpeed(0.875f).swimSpeed(1.40f).stepSize(0.7f).gills().absorbing()
            .build(Changed.modResource("form_latex_shark")));
    public static final GenderedVariant<LatexSharkMale, LatexSharkFemale> LATEX_SHARK_FUSION = register(GenderedVariant.Builder.of(LATEX_SHARK, ChangedEntities.LATEX_SHARK_MALE, ChangedEntities.LATEX_SHARK_FEMALE).groundSpeed(0.9f).swimSpeed(1.5f).stepSize(0.7f).additionalHealth(8).split(Builder::ignored, Builder::absorbing).fusionOf(LATEX_SHARK, LATEX_SHARK)
            .buildGendered(Changed.modResource("form_latex_shark")));
    public static final LatexVariant<LatexTigerShark> LATEX_TIGER_SHARK = register(Builder.of(LATEX_SHARK, ChangedEntities.LATEX_TIGER_SHARK).groundSpeed(0.925f).swimSpeed(1.25f).additionalHealth(10).replicating()
            .build(Changed.modResource("form_latex_tiger_shark")));
    public static final LatexVariant<LatexOrca> LATEX_ORCA = register(Builder.of(LATEX_SHARK, ChangedEntities.LATEX_ORCA).replicating()
            .build(Changed.modResource("form_latex_orca")));
    public static final GenderedVariant<LatexMantaRayMale, LatexMantaRayFemale> LATEX_MANTA_RAY = register(GenderedVariant.Builder.of(LatexVariant.LATEX_SHARK, ChangedEntities.LATEX_MANTA_RAY_MALE, ChangedEntities.LATEX_MANTA_RAY_FEMALE)
            .split(Builder::ignored, female -> female.groundSpeed(0.3F).swimSpeed(2.7F).absorbing().additionalHealth(10).noLegs().cannotWalk())
            .buildGendered(Changed.modResource("form_latex_manta_ray")));
    public static final LatexVariant<LatexMedusaCat> LATEX_MEDUSA_CAT = register(Builder.of(LATEX_SNOW_LEOPARD.male(), ChangedEntities.LATEX_MEDUSA_CAT)
            .build(Changed.modResource("form_latex_medusa_cat")));
    public static final GenderedVariant<LatexMermaidShark, LatexSiren> LATEX_MERMAID_SHARK = register(GenderedVariant.Builder.of(LatexVariant.LATEX_SHARK, ChangedEntities.LATEX_MERMAID_SHARK, ChangedEntities.LATEX_SIREN)
            .groundSpeed(0.3F).swimSpeed(2.7F).split(male -> male.replicating(), female -> female.absorbing().addAbility(ChangedAbilities.USE_VARIANT_EFFECT)).additionalHealth(10).noLegs().cannotWalk()
            .buildGendered(Changed.modResource("form_latex_mermaid_shark")));

    public static final LatexVariant<LatexSquidDog> LATEX_SQUID_DOG = register(Builder.of(ChangedEntities.LATEX_SQUID_DOG).groundSpeed(0.925f).swimSpeed(1.1f).additionalHealth(10).gills().extraHands().addAbility(ChangedAbilities.CREATE_INKBALL)
            .build(Changed.modResource("form_latex_squid_dog")));
    public static final LatexVariant<LatexSquirrel> LATEX_SQUIRREL = register(Builder.of(LATEX_SILVER_FOX, ChangedEntities.LATEX_SQUIRREL).reducedFall()
            .build(Changed.modResource("form_latex_squirrel")));
    public static final LatexVariant<LatexCrocodile> LATEX_CROCODILE = register(Builder.of(ChangedEntities.LATEX_CROCODILE).groundSpeed(0.925f).swimSpeed(1.1f).additionalHealth(12).breatheMode(BreatheMode.STRONG)
            .build(Changed.modResource("form_latex_crocodile")));
    public static final LatexVariant<LatexRaccoon> LATEX_RACCOON = register(Builder.of(ChangedEntities.LATEX_RACCOON).groundSpeed(0.95f).swimSpeed(0.97f).noVision()
            .build(Changed.modResource("form_latex_raccoon")));
    public static final LatexVariant<LatexBenignWolf> LATEX_BENIGN_WOLF = register(Builder.of(ChangedEntities.LATEX_BENIGN_WOLF).groundSpeed(0.25f).swimSpeed(0.15f).noVision().cannotWalk()
            .build(Changed.modResource("form_latex_benign_wolf")));
    public static final LatexVariant<DarkLatexDragon> DARK_LATEX_DRAGON = register(LatexVariant.Builder.of(ChangedEntities.DARK_LATEX_DRAGON).groundSpeed(1.0F).swimSpeed(0.75f).glide().sound(ChangedSounds.SOUND3.getLocation())
            .stepSize(0.7f).faction(LatexType.DARK_LATEX).build(Changed.modResource("form_dark_latex_dragon")));
    public static final LatexVariant<DarkLatexYufeng> DARK_LATEX_YUFENG = register(Builder.of(DARK_LATEX_DRAGON, ChangedEntities.DARK_LATEX_YUFENG)
            .build(Changed.modResource("form_dark_latex_yufeng")));
    public static final LatexVariant<LatexBeifeng> LATEX_BEIFENG = register(Builder.of(ChangedEntities.LATEX_BEIFENG).groundSpeed(1.05f).swimSpeed(1.0f).stepSize(0.7f).sound(ChangedSounds.SOUND3.getLocation())
            .build(Changed.modResource("form_latex_beifeng")));
    public static final LatexVariant<LatexBlueDragon> LATEX_BLUE_DRAGON = register(Builder.of(ChangedEntities.LATEX_BLUE_DRAGON).groundSpeed(1.1f).swimSpeed(0.9f).stepSize(0.7f)
            .build(Changed.modResource("form_latex_blue_dragon")));
    public static final LatexVariant<LatexPinkWyvern> LATEX_PINK_WYVERN = register(Builder.of(LATEX_BLUE_DRAGON, ChangedEntities.LATEX_PINK_WYVERN).faction(LatexType.NEUTRAL)
            .build(Changed.modResource("form_latex_pink_wyvern")));
    public static final LatexVariant<LatexRedDragon> LATEX_RED_DRAGON = register(Builder.of(DARK_LATEX_YUFENG, ChangedEntities.LATEX_RED_DRAGON).faction(LatexType.NEUTRAL)
            .build(Changed.modResource("form_latex_red_dragon")));
    public static final LatexVariant<LatexYuin> LATEX_YUIN = register(Builder.of(LATEX_SILVER_FOX, ChangedEntities.LATEX_YUIN)
            .build(Changed.modResource("form_latex_yuin")));
    public static final LatexVariant<LatexDeer> LATEX_DEER = register(Builder.of(LATEX_SILVER_FOX, ChangedEntities.LATEX_DEER)
            .build(Changed.modResource("form_latex_deer")));
    public static final LatexVariant<LatexRedPanda> LATEX_RED_PANDA = register(Builder.of(LATEX_SILVER_FOX, ChangedEntities.LATEX_RED_PANDA)
            .build(Changed.modResource("form_latex_red_panda")));
    public static final LatexVariant<LatexTranslucentLizard> LATEX_TRANSLUCENT_LIZARD = register(Builder.of(LATEX_BEIFENG, ChangedEntities.LATEX_TRANSLUCENT_LIZARD).sound(ChangedSounds.POISON.getLocation()).absorbing()
            .build(Changed.modResource("form_latex_translucent_lizard")));

    public static final LatexVariant<LatexStiger> LATEX_STIGER = register(Builder.of(ChangedEntities.LATEX_STIGER).canClimb().extraHands().nightVision().addAbility(ChangedAbilities.CREATE_COBWEB)
            .build(Changed.modResource("form_latex_stiger")));

    public static final LatexVariant<?> FALLBACK_VARIANT = LIGHT_LATEX_WOLF.male();

    public ResourceLocation getFormId() {
        return formId;
    }

    public boolean isReducedFall() {
        return reducedFall;
    }

    public TransfurMode transfurMode() { return transfurMode; }

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

    public void activateAbility(Player player, ResourceLocation name) {
        if (abilities.contains(name)) {
            var ability = abilityInstances.get(name);
            if (ability.canUse()) {
                activeAbilities.put(name, abilityInstances.get(name));
                ability.startUsing();
            }
        }
    }

    public float swimMultiplier() {
        return swimSpeed;
    }

    public float landMultiplier() {
        return groundSpeed;
    }

    public CompoundTag saveAbilities() {
        CompoundTag tagAbilities = new CompoundTag();
        abilityInstances.forEach((name, ability) -> {
            CompoundTag tagAbility = new CompoundTag();
            ability.saveData(tagAbility);
            if (!tagAbility.isEmpty())
                tagAbilities.put(name.toString(), tagAbility);
        });
        return tagAbilities;
    }

    public void loadAbilities(CompoundTag tagAbilities) {
        abilityInstances.forEach((name, instance) -> {
            if (!tagAbilities.contains(name.toString()))
                return;
            CompoundTag abilityTag = tagAbilities.getCompound(name.toString());
            instance.readData(abilityTag);
        });
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

    private LatexEntity latexForm = null;

    private static final AtomicInteger NEXT_ENTITY_ID = new AtomicInteger(-70000000);

    public static int getNextEntId() {
        return NEXT_ENTITY_ID.getAndDecrement();
    }

    public String toString() {
        return formId.toString();
    }

    // Variant properties
    public final ResourceLocation formId;
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
    public final boolean cannotWalk;
    public final boolean hasLegs;
    public final List<Class<? extends PathfinderMob>> scares;
    public TransfurMode transfurMode;
    public final Optional<Pair<LatexVariant<?>, LatexVariant<?>>> fusionOf;
    public final Optional<Pair<LatexVariant<?>, Class<? extends LivingEntity>>> mobFusionOf;
    public final ImmutableList<ResourceLocation> abilities;
    public final Map<ResourceLocation, AbstractAbilityInstance> abilityInstances = new HashMap<>();
    private final Map<ResourceLocation, AbstractAbilityInstance> activeAbilities = new HashMap<>();
    public final float cameraZOffset;
    public final ResourceLocation sound;

    public <T extends AbstractAbilityInstance> T getAbilityInstance(AbstractAbility<T> ability) {
        try {
            return (T) abilityInstances.get(ability.getId());
        } catch (Exception unused) {
            return null;
        }
    }

    private boolean dead;
    public int ticksBreathingUnderwater;
    public int ticksWhiteLatex;

    private final AttributeModifier attributeModifierSwimSpeed;
    private final AttributeModifier attributeModifierAdditionalHealth;

    // Varient data
    protected int air = -100;
    protected int jumpCharges = 0;
    protected float lastSwimMul = 1F;

    public LatexVariant(ResourceLocation formId, Supplier<EntityType<T>> ctor, LatexType type, float groundSpeed, float swimSpeed,
                        float jumpStrength, BreatheMode breatheMode, float stepSize, boolean canGlide, int extraJumpCharges, int additionalHealth,
                        boolean reducedFall, boolean canClimb,
                        boolean nightVision, boolean noVision, boolean cannotWalk, boolean hasLegs, List<Class<? extends PathfinderMob>> scares, TransfurMode transfurMode,
                        Optional<Pair<LatexVariant<?>, LatexVariant<?>>> fusionOf,
                        Optional<Pair<LatexVariant<?>, Class<? extends LivingEntity>>> mobFusionOf, List<ResourceLocation> abilities, float cameraZOffset, ResourceLocation sound) {
        this.formId = formId;
        this.ctor = ctor;
        this.type = type;
        this.groundSpeed = groundSpeed;
        this.swimSpeed = swimSpeed;
        this.jumpStrength = jumpStrength;
        this.breatheMode = breatheMode;
        this.stepSize = stepSize;
        this.noVision = noVision;
        this.cannotWalk = cannotWalk;
        this.canGlide = canGlide;
        this.extraJumpCharges = extraJumpCharges;
        this.additionalHealth = additionalHealth;
        this.nightVision = nightVision;
        this.hasLegs = hasLegs;
        this.abilities = ImmutableList.<ResourceLocation>builder().addAll(abilities).build();
        this.reducedFall = reducedFall;
        this.canClimb = canClimb;
        this.scares = scares;
        this.transfurMode = transfurMode;
        this.fusionOf = fusionOf;
        this.mobFusionOf = mobFusionOf;
        this.cameraZOffset = cameraZOffset;

        attributeModifierSwimSpeed = new AttributeModifier(UUID.fromString("5c40eef3-ef3e-4d8d-9437-0da1925473d7"), "changed:trait_swim_speed", swimSpeed, AttributeModifier.Operation.MULTIPLY_BASE);
        attributeModifierAdditionalHealth = new AttributeModifier(UUID.fromString("5c40eef3-ef3e-4d8d-9437-0da1925473d8"), "changed:trait_additional_health", additionalHealth, AttributeModifier.Operation.ADDITION);
        this.sound = sound;
    }

    public LatexEntity getLatexEntity() {
        return latexForm;
    }

    public LatexType getLatexType() {
        return type;
    }

    public LatexVariant<T> clone() {
        return new LatexVariant<>(formId, ctor, type, groundSpeed, swimSpeed, jumpStrength, breatheMode, stepSize, canGlide, extraJumpCharges, additionalHealth,
                reducedFall, canClimb, nightVision, noVision, cannotWalk, hasLegs, scares, transfurMode, fusionOf, mobFusionOf, abilities, cameraZOffset, sound);
    }

    private LatexEntity createLatexEntity(Level level) {
        LatexEntity entity = ctor.get().create(level);
        entity.setId(getNextEntId()); //to prevent ID collision
        entity.setSilent(true);
        entity.goalSelector.removeAllGoals();
        return entity;
    }

    public EntityType<T> getEntityType() {
        return ctor.get();
    }

    public LatexEntity generateForm(@NotNull Player player, Level level) {
        if (latexForm == null) {
            latexForm = createLatexEntity(level);
            latexForm.moveTo((player.getX()), (player.getY()), (player.getZ()), player.getYRot(), 0);
            if (latexForm instanceof SpecialLatex specialLatex)
                specialLatex.setSpecialLatexForm(UUID.fromString(
                        formId.toString().substring(Changed.modResourceStr("special/form_").length())));

            latexForm.setCustomName(PatreonBenefits.getPlayerName(player));
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                if (player == Minecraft.getInstance().player)
                    latexForm.setCustomNameVisible(false);
            });

            abilities.forEach(name -> abilityInstances.put(name, ChangedAbilities.getAbility(name).makeInstance(player, this)));
        }
        return latexForm;
    }

    public LatexEntity replaceEntity(@NotNull LivingEntity entity) {
        LatexEntity newEntity = ctor.get().create(entity.level);
        newEntity.finalizeSpawn((ServerLevelAccessor) entity.level, entity.level.getCurrentDifficultyAt(newEntity.blockPosition()), MobSpawnType.MOB_SUMMONED, null,
                null);
        newEntity.moveTo((entity.getX()), (entity.getY()), (entity.getZ()), entity.getYRot(), 0);
        entity.level.addFreshEntity(newEntity);
        if (newEntity instanceof SpecialLatex specialLatex) {
            specialLatex.setSpecialLatexForm(UUID.fromString(
                    formId.toString().substring(Changed.modResourceStr("special/form_").length())));
        }
        if (entity instanceof Player) {
            entity.setLastHurtByMob(newEntity);
            entity.hurt(ChangedDamageSources.TRANSFUR, 999999999.0f);
        } else
            entity.discard();
        return newEntity;
    }

    public BreatheMode getBreatheMode() {
        return breatheMode;
    }

    public boolean canDoubleJump() { return extraJumpCharges > 0; }

    public boolean rideable() { return this.abilities.contains(ChangedAbilities.ACCESS_SADDLE.getId()); }

    public int getJumpCharges() { return jumpCharges; }
    public void decJumpCharges() { jumpCharges -= 1; }

    public void sync(Player player) {
        if (latexForm == null) return;

        syncInventory(latexForm, player, true); //reset the inventory so the entity doesn't actually use our equipment when ticking.

        syncEntityAndPlayer(latexForm, player);

        syncInventory(latexForm, player, false); //sync the inventory for rendering purposes.

        //latexForm.getDataManager().setClean(); //we don't want to flood the client with packets for an entity it can't find.
    }
    public void tick(Player player) {
        if (player == null) return;

        player.refreshDimensions();
        if (player.isOnGround())
            jumpCharges = extraJumpCharges;

        if (rideable() || !hasLegs)
            player.stopRiding();

        if (!hasLegs) {
            player.getArmorSlots().forEach(itemStack -> { // Force unequip invalid items
                if (itemStack.getItem() instanceof ArmorItem armorItem) {
                    switch (armorItem.getSlot()) {
                        case FEET:
                        case LEGS:
                            ItemStack copy = itemStack.copy();
                            itemStack.setCount(0);
                            player.addItem(copy);
                        default:
                            break;
                    }
                }
            });
        }

        // Repulse villagers
        if(!player.level.isClientSide) {
            final double distance = 8D;
            final double farRunSpeed = 0.5D;
            final double nearRunSpeed = 0.6666D;
            // Scare mobs
            for (Class<? extends PathfinderMob> entityClass : scares) {
                List<? extends PathfinderMob> entitiesScared = player.level.getEntitiesOfClass(entityClass, player.getBoundingBox().inflate(distance, 3D, distance), Objects::nonNull);
                for(var v : entitiesScared) {
                    //if the creature has no path, or the target path is < distance, make the creature run.
                    if(v.getNavigation().getPath() == null || player.distanceToSqr(v.getNavigation().getTargetPos().getX(), v.getNavigation().getTargetPos().getY(), v.getNavigation().getTargetPos().getZ()) < distance * distance)
                    {
                        Vec3 vector3d = DefaultRandomPos.getPosAway(v, 16, 7, new Vec3(player.getX(), player.getY(), player.getZ()));

                        if(vector3d != null && player.distanceToSqr(vector3d) > player.distanceToSqr(v))
                        {
                            Path path = v.getNavigation().createPath(vector3d.x, vector3d.y, vector3d.z, 0);

                            if(path != null)
                            {
                                double speed = v.distanceToSqr(player) < 49D ? nearRunSpeed : farRunSpeed;
                                v.getNavigation().moveTo(path, speed);
                            }
                        }
                    }
                    else //the creature is still running away from us
                    {
                        double speed = v.distanceToSqr(player) < 49D ? nearRunSpeed : farRunSpeed;
                        v.getNavigation().setSpeedModifier(speed);
                    }

                    if (v.getTarget() == player)
                        v.setTarget(null);
                }
            }
        }

        // Breathing
        if(player.isAlive() && breatheMode.canBreatheWater()) {
            if(air == -100)
            {
                air = player.getAirSupply();
            }

            //if the player is in water, add air
            if (player.isEyeInFluid(FluidTags.WATER)) {
                //Taken from determineNextAir in LivingEntity
                air = Math.min(air + 4, player.getMaxAirSupply());
                player.setAirSupply(air);
                this.ticksBreathingUnderwater++;
                if (player instanceof ServerPlayer serverPlayer)
                    ChangedCriteriaTriggers.AQUATIC_BREATHE.trigger(serverPlayer, this.ticksBreathingUnderwater);
            }
            else if (!breatheMode.canBreatheAir()) //if the player is on land and the entity suffocates
            {
                //taken from decreaseAirSupply in Living Entity
                int i = EnchantmentHelper.getRespiration(player);
                air = i > 0 && player.getRandom().nextInt(i + 1) > 0 ? air : air - 1;

                if(air == -20)
                {
                    air = 0;

                    player.hurt(DamageSource.DROWN, 2F);
                }

                player.setAirSupply(air);
                this.ticksBreathingUnderwater = 0;
            }
            else {
                this.ticksBreathingUnderwater = 0;
            }
        }

        else if (player.isAlive() && !breatheMode.canBreatheWater() && breatheMode == BreatheMode.WEAK) {
            //if the player is in water, remove more air
            if (player.isEyeInFluid(FluidTags.WATER)) {
                int air = player.getAirSupply();
                if (air > -10)
                    player.setAirSupply(air-1);
                this.ticksBreathingUnderwater = 0;
            }
        }

        else if (player.isAlive() && !breatheMode.canBreatheWater() && breatheMode == BreatheMode.STRONG) {
            //if the player is in water, add 1 air every other tick
            if (player.isEyeInFluid(FluidTags.WATER)) {
                int air = player.getAirSupply();
                if (air > -10 && player.tickCount % 2 == 0)
                    player.setAirSupply(air+1);
                this.ticksBreathingUnderwater = 0;
            }
        }

        // Speed
        if(swimSpeed != 0F) {
            if(player.isInWaterOrBubble()) {
                if (swimSpeed < 1f) {
                    if (player.isSwimming())
                        multiplyMotion(player, swimSpeed * 1.2f);
                    else
                        multiplyMotion(player, swimSpeed);
                }
                else if (!player.level.isClientSide) {
                    if (!player.getAttribute(ForgeMod.SWIM_SPEED.get()).hasModifier(attributeModifierSwimSpeed))
                        player.getAttribute(ForgeMod.SWIM_SPEED.get()).addPermanentModifier(attributeModifierSwimSpeed);
                }
            }

            else {
                if (player.getAttribute(ForgeMod.SWIM_SPEED.get()).hasModifier(attributeModifierSwimSpeed))
                    player.getAttribute(ForgeMod.SWIM_SPEED.get()).removePermanentModifier(attributeModifierSwimSpeed.getId());
            }
        }

        if(groundSpeed != 0F) {
            if(!player.isInWaterOrBubble() && player.isOnGround()) {
                if (groundSpeed > 1f) {
                    if (!player.isCrouching())
                        multiplyMotion(player, groundSpeed);
                }
                else {
                    multiplyMotion(player, groundSpeed);
                }
            }
        }

        // Step size
        if (player.isCrouching() && stepSize > 0.6f)
            player.maxUpStep = 0.6f;
        else
            player.maxUpStep = stepSize;

        List<ResourceLocation> toRemove = new ArrayList<>();
        activeAbilities.forEach((name, ability) -> {
            if (!ability.canKeepUsing()) {
                ability.stopUsing();
                toRemove.add(name);
                return;
            }

            ability.tick();
        });
        toRemove.forEach(activeAbilities::remove);

        sync(player);
    }

    public void unhookAll(Player player) {
        activeAbilities.forEach((name, ability) -> {
            ability.stopUsing();
        });
        activeAbilities.clear();
        abilityInstances.forEach((name, ability) -> {
            ability.onRemove();
        });
        if (player.getAttribute(ForgeMod.SWIM_SPEED.get()).hasModifier(attributeModifierSwimSpeed))
            player.getAttribute(ForgeMod.SWIM_SPEED.get()).removePermanentModifier(attributeModifierSwimSpeed.getId());
        player.setHealth(Math.min(player.getMaxHealth(), player.getHealth()));
        player.maxUpStep = 0.6F;
        player.refreshDimensions();
    }

    public void setDead() {
        dead = true;
    }

    public boolean isDead() {
        return dead;
    }

    private static void syncEntityPosRotWithPlayer(LatexEntity living, Player player) {
        living.tickCount = player.tickCount;

        living.setPos(player.getX(), player.getY(), player.getZ());
        living.setXRot(player.getXRot());
        living.setYRot(player.getYRot());

        living.xRotO = player.xRotO;
        living.yRotO = player.yRotO;
        living.xOld = player.xOld;
        living.yOld = player.yOld;
        living.zOld = player.zOld;
        living.yBodyRot = player.yBodyRot;
        living.yBodyRotO = player.yBodyRotO;
        living.yHeadRot = player.yHeadRot;
        living.yHeadRotO = player.yHeadRotO;
        living.xo = player.xo;
        living.yo = player.yo;
        living.zo = player.zo;
        living.xxa = player.xxa;
        living.yya = player.yya;
        living.zza = player.zza;
        living.walkDist = player.walkDist;
        living.walkDistO = player.walkDistO;
        living.moveDist = player.moveDist;

        living.getActiveEffectsMap().clear();
    }

    public static void syncEntityAndPlayer(LatexEntity living, Player player) {
        syncEntityPosRotWithPlayer(living, player); //re-sync with the player position in case the entity moved whilst ticking.

        living.setUnderlyingPlayer(player);

        //Others
        living.swingingArm = player.swingingArm;
        living.swinging = player.swinging;
        living.swingTime = player.swingTime;

        living.setDeltaMovement(player.getDeltaMovement());

        //Entity stuff
        living.horizontalCollision = player.horizontalCollision;
        living.verticalCollision = player.verticalCollision;
        living.setOnGround(player.isOnGround());
        living.setShiftKeyDown(player.isCrouching());
        if (living.isSprinting() != player.isSprinting())
            living.setSprinting(player.isSprinting());
        living.setSwimming(player.isSwimming());

        living.setHealth(living.getMaxHealth() * (player.getHealth() / player.getMaxHealth()));
        living.setAirSupply(player.getAirSupply());
        living.hurtTime = player.hurtTime;
        living.deathTime = player.deathTime;
        living.animationPosition = player.animationPosition;
        living.animationSpeed = player.animationSpeed;
        living.animationSpeedOld = player.animationSpeedOld;
        living.attackAnim = player.attackAnim;
        living.oAttackAnim = player.oAttackAnim;
        living.flyDist = player.flyDist;
        living.flyingSpeed = player.flyingSpeed;

        living.wasTouchingWater = player.wasTouchingWater;
        living.swimAmount = player.swimAmount;
        living.swimAmountO = player.swimAmountO;

        living.fallFlyTicks = player.fallFlyTicks;

        living.setSharedFlag(7, player.isFallFlying());

        living.vehicle = player.vehicle;

        living.useItemRemaining = player.useItemRemaining;

        Pose pose = living.getPose();
        living.setPose(player.getPose());

        if(pose != living.getPose()) {
            living.refreshDimensions();
        }

        if(player.getSleepingPos().isPresent())
        {
            living.setSleepingPos(player.getSleepingPos().get());
        }
        else
        {
            living.clearSleepingPos();
        }

        living.setInvisible(player.isInvisible());
        living.setInvulnerable(player.isInvulnerable());

        living.setUUID(player.getUUID());

        living.setGlowingTag(player.isCurrentlyGlowing());

        //EntityRendererManager stuff
        living.setRemainingFireTicks(player.getRemainingFireTicks());
        living.setTicksFrozen(player.getTicksFrozen());
        living.setArrowCount(player.getArrowCount());

        //Sync potions for rendering purposes
        living.getActiveEffectsMap().putAll(player.getActiveEffectsMap());

        TagUtil.replace(player.getPersistentData(), living.getPersistentData());

        specialEntityPlayerSync(living, player);
    }

    private static void specialEntityPlayerSync(LatexEntity living, Player player) {
        living.setLeftHanded(player.getMainArm() == HumanoidArm.LEFT);
        living.setAggressive(player.isUsingItem());
    }

    public static void syncInventory(LatexEntity living, Player player, boolean reset) {
        for (EquipmentSlot value : EquipmentSlot.values()) {
            boolean shouldReset = reset && (value == EquipmentSlot.MAINHAND || value == EquipmentSlot.OFFHAND);
            if(!ItemStack.isSameIgnoreDurability(living.getItemBySlot(value), shouldReset ? ItemStack.EMPTY : player.getItemBySlot(value))) {
                living.setItemSlot(value, shouldReset ? ItemStack.EMPTY : player.getItemBySlot(value).copy());
            }
        }

        /*if(player.isUsingItem())
        {
            if(player.getItemInUseMaxCount() == 1) {
                living.swingingArm = player.swingingArm;
                Hand hand = player.getActiveHand();
                living.setActiveHand(hand);
                living.setLivingFlag(1, true);
                living.setLivingFlag(2, hand == Hand.OFF_HAND);
            }
        }
        else {
            living.setLivingFlag(1, false);
            living.resetActiveHand();
        }*/
    }

    protected double lerp(double a, double b, double x) {
        return a * (1 - x) + b * x;
    }

    protected double clamp(double min, double max, double x) {
        return Math.max(Math.min(x, max), min);
    }

    protected void multiplyMotion(Player player, double mul) {
        var dP = player.getDeltaMovement();

        if (mul > 1f) {
            if (player.isOnGround()) {
                float friction = player.getLevel().getBlockState(player.blockPosition().below())
                        .getFriction(player.getLevel(), player.blockPosition(), player);
                double mdP = dP.length();
                mul = clamp(0.75, mul, lerp(mul, 0.8 * mul / Math.pow(mdP, 1.0/6.0), mdP * 3));
                mul /= clamp(0.6, 1, friction) * 0.65 + 0.61;
            }
        }

        player.setDeltaMovement(dP.multiply(mul, mul, mul));
    }

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
        boolean weakLungs = false;
        boolean reducedFall = false;
        boolean noVision = false;
        boolean cannotWalk = false;
        boolean canClimb = false;
        boolean nightVision = false;
        boolean hasLegs = true;
        List<Class<? extends PathfinderMob>> scares = new ArrayList<>(ImmutableList.of(AbstractVillager.class));
        TransfurMode transfurMode = TransfurMode.REPLICATION;
        Optional<Pair<LatexVariant<?>, LatexVariant<?>>> fusionOf = Optional.empty();
        Optional<Pair<LatexVariant<?>, Class<? extends LivingEntity>>> mobFusionOf = Optional.empty();
        List<ResourceLocation> abilities = new ArrayList<>();
        float cameraZOffset = 0.0F;
        ResourceLocation sound = ChangedSounds.POISON.getLocation();

        public Builder(Supplier<EntityType<T>> entityType) {
            this.entityType = entityType;
            // vvv-- Add universal abilities here --vvv
            this.abilities.add(ChangedAbilities.SWITCH_TRANSFUR_MODE.getId());
        }

        private void ignored() {}

        public static <T extends LatexEntity> Builder<T> of(Supplier<EntityType<T>> entityType) {
            return new Builder<T>(entityType);
        }

        public static <T extends LatexEntity> Builder<T> of(LatexVariant<?> variant, Supplier<EntityType<T>> entityType) {
            return (new Builder<T>(entityType)).faction(variant.type).groundSpeed(variant.groundSpeed).swimSpeed(variant.swimSpeed)
                    .jumpStrength(variant.jumpStrength).breatheMode(variant.breatheMode).stepSize(variant.stepSize).glide(variant.canGlide).extraJumps(variant.extraJumpCharges)
                    .abilities(variant.abilities).reducedFall(variant.reducedFall).canClimb(variant.canClimb).nightVision(variant.nightVision).hasLegs(variant.hasLegs).scares(variant.scares)
                    .transfurMode(variant.transfurMode).cameraZOffset(variant.cameraZOffset).noVision(variant.noVision).cannotWalk(variant.cannotWalk);
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
        public Builder<T> cannotWalk() {
            this.cannotWalk = true; return this;
        }
        public Builder<T> cannotWalk(boolean v) {
            this.cannotWalk = v; return this;
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

        public Builder<T> weakLungs() {
            this.weakLungs = true; return this;
        }

        public Builder<T> weakLungs(boolean v) {
            this.weakLungs = v; return this;
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
        
        public Builder<T> addAbility(AbstractAbility<?> ability) {
            if (ability != null)
                this.abilities.add(ability.getId());
            return this;
        }
        
        public Builder<T> abilities(List<ResourceLocation> abilities) {
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

        public Builder<T> cameraZOffset(float v) {
            this.cameraZOffset = v; return this;
        }

        public Builder<T> sound(ResourceLocation event) {
            this.sound = event; return this;
        }

        public LatexVariant<T> build(ResourceLocation formId) {
            return new LatexVariant<>(formId, entityType, type, groundSpeed, swimSpeed, jumpStrength, breatheMode, stepSize, canGlide, extraJumpCharges, additionalHealth,
                    reducedFall, canClimb, nightVision, noVision, cannotWalk, hasLegs, scares, transfurMode, fusionOf, mobFusionOf, abilities, cameraZOffset, sound);
        }
    }

    public static <T extends LatexEntity> LatexVariant<T> register(LatexVariant<T> variant) {
        ALL_LATEX_FORMS.put(variant.formId, variant);
        PUBLIC_LATEX_FORMS.put(variant.formId, variant);
        if (variant.fusionOf.isPresent())
            FUSION_LATEX_FORMS.put(variant.formId, variant);
        if (variant.mobFusionOf.isPresent())
            MOB_FUSION_LATEX_FORMS.put(variant.formId, variant);
        return variant;
    }

    public static <M extends LatexEntity & GenderedEntity, F extends LatexEntity & GenderedEntity> GenderedVariant<M, F> register(GenderedVariant<M, F> variant) {
        ALL_LATEX_FORMS.put(variant.male.formId, variant.male);
        ALL_LATEX_FORMS.put(variant.female.formId, variant.female);
        PUBLIC_LATEX_FORMS.put(variant.male.formId, variant.male);
        PUBLIC_LATEX_FORMS.put(variant.female.formId, variant.female);
        if (variant.male.fusionOf.isPresent())
            FUSION_LATEX_FORMS.put(variant.male.formId, variant.male);
        if (variant.female.fusionOf.isPresent())
            FUSION_LATEX_FORMS.put(variant.female.formId, variant.female);
        if (variant.male.mobFusionOf.isPresent())
            MOB_FUSION_LATEX_FORMS.put(variant.male.formId, variant);
        if (variant.female.mobFusionOf.isPresent())
            MOB_FUSION_LATEX_FORMS.put(variant.female.formId, variant);
        return variant;
    }

    public static <T extends LatexEntity> LatexVariant<T> registerSpecial(LatexVariant<T> variant) {
        ALL_LATEX_FORMS.put(variant.formId, variant);
        SPECIAL_LATEX_FORMS.put(variant.formId, variant);
        return variant;
    }

    public static LatexVariant<?> findLatexEntityVariant(LatexEntity entity) {
        for (LatexVariant<?> variant : ALL_LATEX_FORMS.values())
            if (variant.ctor != null && variant.ctor.get().equals(entity.getType()))
                return variant;
        return null;
    }

    public static LatexVariant<?> getEntityTransfur(LivingEntity entity) {
        if (entity instanceof Player player && ProcessTransfur.isPlayerLatex(player))
            return ProcessTransfur.getPlayerLatexVariant(player).getLatexEntity().getTransfurVariant();
        else if (entity instanceof LatexEntity latexEntity)
            return latexEntity.getTransfurVariant();
        return null;
    }

    public static LatexVariant<?> getEntityVariant(LivingEntity entity) {
        if (entity instanceof Player player && ProcessTransfur.isPlayerLatex(player))
            return ProcessTransfur.getPlayerLatexVariant(player);
        else if (entity instanceof LatexEntity latexEntity)
            return latexEntity.getSelfVariant();
        return null;
    }

    @SubscribeEvent
    public static void onLivingFallEvent(LivingFallEvent event) {
        LatexVariant<?> variant = getEntityVariant(event.getEntityLiving());
        if (variant != null && variant.isReducedFall()) {
            event.setDistance(event.getDistance() * 0.5f);
        }
    }

    @SubscribeEvent
    public static void onSizeEvent(EntityEvent.Size event) {
        if (event.getEntity() instanceof Player player) {
            if (player.isAddedToWorld() && ProcessTransfur.isPlayerLatex(player)) {
                LatexEntity latexEntity = ProcessTransfur.getPlayerLatexVariant(player).getLatexEntity();

                event.setNewSize(latexEntity.getDimensions(event.getPose()));
                event.setNewEyeHeight(latexEntity.getEyeHeight(event.getPose()));
            }
        }
    }

    // Parses variant from JSON, does not register variant
    public static LatexVariant<?> fromJson(ResourceLocation id, JsonObject root) {
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

        List<ResourceLocation> abilities = new ArrayList<>();
        GsonHelper.getAsJsonArray(root, "abilities", new JsonArray()).forEach(element -> {
            ResourceLocation location = ResourceLocation.tryParse(element.getAsString());
            abilities.add(location);
        });

        return new LatexVariant<>(
                id,
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
                GsonHelper.getAsBoolean(root, "cannotWalk", false),
                GsonHelper.getAsBoolean(root, "hasLegs", true),
                scares,
                TransfurMode.valueOf(GsonHelper.getAsString(root, "transfurMode", TransfurMode.REPLICATION.toString())),
                fusionOf.size() < 2 ? Optional.empty() : Optional.of(new Pair<>(
                        fusionOf.get(0), fusionOf.get(1)
                )),
                mobFusionLatex.get() != null && mobFusionMob.get() != null ? Optional.of(new Pair<>(mobFusionLatex.getAcquire(), mobFusionMob.getAcquire())) : Optional.empty(),
                abilities,
                GsonHelper.getAsFloat(root, "cameraZOffset", 0.0F),
                ResourceLocation.tryParse(GsonHelper.getAsString(root, "sound", ChangedSounds.POISON.getLocation().toString())));
    }
}
