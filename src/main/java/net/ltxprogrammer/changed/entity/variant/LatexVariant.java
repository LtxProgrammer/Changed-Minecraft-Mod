package net.ltxprogrammer.changed.entity.variant;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.GenderedLatexEntity;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.beast.*;
import net.ltxprogrammer.changed.init.ChangedCriteriaTriggers;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;


public class LatexVariant<T extends LatexEntity> {
    public static final String NBT_PLAYER_ID = "changed:player_id";

    public static final ResourceLocation SPECIAL_LATEX = Changed.modResource("form_special");

    public static Map<ResourceLocation, LatexVariant<?>> ALL_LATEX_FORMS = new HashMap<>();
    public static Map<ResourceLocation, LatexVariant<?>> PUBLIC_LATEX_FORMS = new HashMap<>();
    public static Map<ResourceLocation, LatexVariant<?>> FUSION_LATEX_FORMS = new HashMap<>();
    public static Map<ResourceLocation, LatexVariant<?>> MOB_FUSION_LATEX_FORMS = new HashMap<>();
    public static Map<ResourceLocation, LatexVariant<?>> SPECIAL_LATEX_FORMS = new HashMap<>();
    public static final GenderedVariant<LightLatexWolfMale, LightLatexWolfFemale> LIGHT_LATEX_WOLF = register(GenderedVariant.Builder.of(ChangedEntities.LIGHT_LATEX_WOLF_MALE, ChangedEntities.LIGHT_LATEX_WOLF_FEMALE)
            .groundSpeed(1.075f).swimSpeed(0.85f).stepSize(0.7f).split(Builder::ignored, Builder::absorbing)
            .buildGendered(Changed.modResource("form_light_latex_wolf")));
    public static final LatexVariant<LightLatexKnight> LIGHT_LATEX_KNIGHT = register(Builder.of(LIGHT_LATEX_WOLF.male(), ChangedEntities.LIGHT_LATEX_KNIGHT).absorbing()
            .build(Changed.modResource("form_light_latex_knight")));
    public static final LatexVariant<LightLatexKnightFusion> LIGHT_LATEX_KNIGHT_FUSION = register(Builder.of(LIGHT_LATEX_KNIGHT, ChangedEntities.LIGHT_LATEX_KNIGHT_FUSION).additionalHealth(8)
            .fusionOf(LIGHT_LATEX_WOLF.male(), LIGHT_LATEX_KNIGHT)
            .build(Changed.modResource("form_light_latex_knight_fusion")));
    public static final LatexVariant<LightLatexCentaur> LIGHT_LATEX_CENTAUR = register(Builder.of(LIGHT_LATEX_WOLF.male(), ChangedEntities.LIGHT_LATEX_CENTAUR).groundSpeed(1.09f).swimSpeed(0.8f).stepSize(1.1f).additionalHealth(2)
            .build(Changed.modResource("form_light_latex_centaur")));
    public static final LatexVariant<AerosolLatexWolf> AEROSOL_LATEX_WOLF = register(Builder.of(LIGHT_LATEX_WOLF.male(), ChangedEntities.AEROSOL_LATEX_WOLF)
            .build(Changed.modResource("form_aerosol_latex_wolf")));
    public static final GenderedVariant<DarkLatexWolfMale, DarkLatexWolfFemale> DARK_LATEX_WOLF = register(GenderedVariant.Builder.of(LIGHT_LATEX_WOLF, ChangedEntities.DARK_LATEX_WOLF_MALE, ChangedEntities.DARK_LATEX_WOLF_FEMALE)
            .faction(LatexType.DARK_LATEX).buildGendered(Changed.modResource("form_dark_latex_wolf")));
    public static final LatexVariant<WhiteLatexWolf> WHITE_LATEX_WOLF = register(Builder.of(LIGHT_LATEX_WOLF.male(), ChangedEntities.WHITE_LATEX_WOLF).faction(LatexType.WHITE_LATEX)
            .build(Changed.modResource("form_white_latex_wolf")));
    public static final LatexVariant<LatexSilverFox> LATEX_SILVER_FOX = register(Builder.of(LIGHT_LATEX_WOLF.male(), ChangedEntities.LATEX_SILVER_FOX)
            .build(Changed.modResource("form_latex_silver_fox")));
    public static final LatexVariant<LatexCrystalWolf> LATEX_CRYSTAL_WOLF = register(Builder.of(LIGHT_LATEX_WOLF.male(), ChangedEntities.LATEX_CRYSTAL_WOLF)
            .build(Changed.modResource("form_latex_crystal_wolf")));
    public static final LatexVariant<LatexTrafficConeDragon> LATEX_TRAFFIC_CONE_DRAGON = register(Builder.of(LIGHT_LATEX_WOLF.male(), ChangedEntities.LATEX_TRAFFIC_CONE_DRAGON)
            .build(Changed.modResource("form_latex_traffic_cone_dragon")));
    public static final LatexVariant<LatexLeaf> LATEX_LEAF = register(Builder.of(LATEX_TRAFFIC_CONE_DRAGON, ChangedEntities.LATEX_LEAF)
            .build(Changed.modResource("form_latex_leaf")));
    public static final GenderedVariant<LatexSnowLeopardMale, LatexSnowLeopardFemale> LATEX_SNOW_LEOPARD = register(GenderedVariant.Builder.of(ChangedEntities.LATEX_SNOW_LEOPARD_MALE, ChangedEntities.LATEX_SNOW_LEOPARD_FEMALE)
            .groundSpeed(1.1f).swimSpeed(0.8f).stepSize(0.7f).weakLungs().fallImmune().scares(Creeper.class).split(Builder::ignored, Builder::absorbing)
            .buildGendered(Changed.modResource("form_latex_snow_leopard")));
    public static final LatexVariant<LatexRedPanda> LATEX_RED_PANDA = register(Builder.of(LATEX_SNOW_LEOPARD.male(), ChangedEntities.LATEX_RED_PANDA).faction(LatexType.NEUTRAL)
            .build(Changed.modResource("form_latex_red_panda")));
    public static final GenderedVariant<LatexWatermelonCatMale, LatexWatermelonCatFemale> LATEX_WATERMELON_CAT = register(GenderedVariant.Builder.of(LATEX_SNOW_LEOPARD, ChangedEntities.LATEX_WATERMELON_CAT_MALE, ChangedEntities.LATEX_WATERMELON_CAT_FEMALE)
            .buildGendered(Changed.modResource("form_latex_watermelon_cat")));
    public static final LatexVariant<LatexHypnoCat> LATEX_HYPNO_CAT = register(Builder.of(LATEX_SNOW_LEOPARD.male(), ChangedEntities.LATEX_HYPNO_CAT)
            .build(Changed.modResource("form_latex_hypno_cat")));

    public static final GenderedVariant<LatexSharkMale, LatexSharkFemale> LATEX_SHARK = register(GenderedVariant.Builder.of(ChangedEntities.LATEX_SHARK_MALE, ChangedEntities.LATEX_SHARK_FEMALE).groundSpeed(0.875f).swimSpeed(1.40f).stepSize(0.7f).gills().split(Builder::ignored, Builder::absorbing)
            .buildGendered(Changed.modResource("form_latex_shark")));
    public static final LatexVariant<LatexTigerShark> LATEX_TIGER_SHARK = register(Builder.of(LATEX_SHARK.male(), ChangedEntities.LATEX_TIGER_SHARK).groundSpeed(0.925f).swimSpeed(1.25f)
            .build(Changed.modResource("form_latex_tiger_shark")));
    public static final LatexVariant<LatexOrca> LATEX_ORCA = register(Builder.of(LATEX_SHARK.male(), ChangedEntities.LATEX_ORCA)
            .build(Changed.modResource("form_latex_orca")));

    public static final LatexVariant<LatexSquidDog> LATEX_SQUID_DOG = register(Builder.of(ChangedEntities.LATEX_SQUID_DOG).groundSpeed(0.925f).swimSpeed(1.1f).additionalHealth(20).gills().extraHands()
            .build(Changed.modResource("form_latex_squid_dog")));

    public static final GenderedVariant<DarkLatexDragonMale, DarkLatexDragonFemale> DARK_LATEX_DRAGON = register(GenderedVariant.Builder.of(ChangedEntities.DARK_LATEX_DRAGON_MALE, ChangedEntities.DARK_LATEX_DRAGON_FEMALE).groundSpeed(1.0F).swimSpeed(0.75f).glide()
            .stepSize(0.7f).faction(LatexType.DARK_LATEX).split(Builder::ignored, Builder::absorbing).buildGendered(Changed.modResource("form_dark_latex_dragon")));
    public static final LatexVariant<DarkLatexYufeng> DARK_LATEX_YUFENG = register(Builder.of(DARK_LATEX_DRAGON.male(), ChangedEntities.DARK_LATEX_YUFENG)
            .build(Changed.modResource("form_dark_latex_yufeng")));

    public static final LatexVariant<LatexBeifeng> LATEX_BEIFENG = register(Builder.of(ChangedEntities.LATEX_BEIFENG).groundSpeed(1.05f).swimSpeed(1.0f).stepSize(0.7f)
            .build(Changed.modResource("form_latex_beifeng")));
    public static final LatexVariant<LatexBlueDragon> LATEX_BLUE_DRAGON = register(Builder.of(ChangedEntities.LATEX_BLUE_DRAGON).groundSpeed(1.1f).swimSpeed(0.9f).stepSize(0.7f)
            .build(Changed.modResource("form_latex_blue_dragon")));
    public static final LatexVariant<LatexRedDragon> LATEX_RED_DRAGON = register(Builder.of(DARK_LATEX_YUFENG, ChangedEntities.LATEX_RED_DRAGON).faction(LatexType.NEUTRAL)
            .build(Changed.modResource("form_latex_red_dragon")));
    public static final LatexVariant<LatexYuin> LATEX_YUIN = register(Builder.of(LIGHT_LATEX_WOLF.male(), ChangedEntities.LATEX_YUIN)
            .build(Changed.modResource("form_latex_yuin")));
    public ResourceLocation getFormId() {
        return formId;
    }

    public boolean canHoldExtra() {
        return extraHands;
    }

    public boolean isFallImmune() {
        return fallImmunity;
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
                            mobFusionOf.get().getSecond().equals(clazz);
        }

        return false;
    }

    public enum BreatheMode {
        NORMAL,
        WATER,
        ANY;

        public boolean canBreatheWater() {
            return this == WATER || this == ANY;
        }

        public boolean canBreatheAir() {
            return this == NORMAL || this == ANY;
        }
    }

    private LatexEntity latexForm = null;

    private static final AtomicInteger NEXT_ENTITY_ID = new AtomicInteger(-70000000);

    public static int getNextEntId() {
        return NEXT_ENTITY_ID.getAndDecrement();
    }

    public String toString() {
        return formId.toString();
    }

    // Varient properties
    private final ResourceLocation formId;
    private final Supplier<EntityType<T>> ctor;
    private final LatexType type;
    private final float groundSpeed;
    private final float swimSpeed;
    private final BreatheMode breatheMode;
    private final float stepSize;
    private final boolean canGlide;
    private final int extraJumpCharges;
    private final int additionalHealth;
    private final boolean extraHands;
    private final boolean weakLungs;
    private final boolean fallImmunity;
    private final List<Class<? extends PathfinderMob>> scares;
    private final TransfurMode transfurMode;
    private final Optional<Pair<LatexVariant<?>, LatexVariant<?>>> fusionOf;
    private final Optional<Pair<LatexVariant<?>, Class<? extends LivingEntity>>> mobFusionOf;

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
                        BreatheMode breatheMode, float stepSize, boolean canGlide, int extraJumpCharges, int additionalHealth, boolean extraHands,
                        boolean weakLungs, boolean fallImmunity, List<Class<? extends PathfinderMob>> scares, TransfurMode transfurMode,
                        Optional<Pair<LatexVariant<?>, LatexVariant<?>>> fusionOf,
                        Optional<Pair<LatexVariant<?>, Class<? extends LivingEntity>>> mobFusionOf) {
        this.formId = formId;
        this.ctor = ctor;
        this.type = type;
        this.groundSpeed = groundSpeed;
        this.swimSpeed = swimSpeed;
        this.breatheMode = breatheMode;
        this.stepSize = stepSize;
        this.canGlide = canGlide;
        this.extraJumpCharges = extraJumpCharges;
        this.additionalHealth = additionalHealth;
        this.extraHands = extraHands;
        this.weakLungs = weakLungs;
        this.fallImmunity = fallImmunity;
        this.scares = scares;
        this.transfurMode = transfurMode;
        this.fusionOf = fusionOf;
        this.mobFusionOf = mobFusionOf;

        attributeModifierSwimSpeed = new AttributeModifier(UUID.fromString("5c40eef3-ef3e-4d8d-9437-0da1925473d7"), "changed:trait_swim_speed", swimSpeed, AttributeModifier.Operation.MULTIPLY_BASE);
        attributeModifierAdditionalHealth = new AttributeModifier(UUID.fromString("5c40eef3-ef3e-4d8d-9437-0da1925473d8"), "changed:trait_additional_health", additionalHealth, AttributeModifier.Operation.ADDITION);
    }

    public LatexEntity getLatexEntity() {
        return latexForm;
    }

    public LatexType getLatexType() {
        return type;
    }

    public LatexVariant<T> clone() {
        return new LatexVariant<>(formId, ctor, type, groundSpeed, swimSpeed, breatheMode, stepSize, canGlide, extraJumpCharges, additionalHealth, extraHands,
                weakLungs, fallImmunity, scares, transfurMode, fusionOf, mobFusionOf);
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

            latexForm.setCustomName(PatreonBenefits.getPlayerName(player));
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                if (player == Minecraft.getInstance().player)
                    latexForm.setCustomNameVisible(false);
            });
        }
        return latexForm;
    }

    public LatexEntity replaceEntity(@NotNull LivingEntity entity) {
        LatexEntity newEntity = ctor.get().create(entity.level);
        newEntity.finalizeSpawn((ServerLevelAccessor) entity.level, entity.level.getCurrentDifficultyAt(newEntity.blockPosition()), MobSpawnType.MOB_SUMMONED, null,
                null);
        newEntity.moveTo((entity.getX()), (entity.getY()), (entity.getZ()), entity.getYRot(), 0);
        entity.level.addFreshEntity(newEntity);
        if (entity instanceof Player)
            entity.kill();
        else
            entity.discard();
        return newEntity;
    }

    public BreatheMode getBreatheMode() {
        return breatheMode;
    }

    public boolean canGlide() { return canGlide; }

    public boolean canDoubleJump() { return extraJumpCharges > 0; }

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
        player.refreshDimensions();
        if (!player.getAttribute(Attributes.MAX_HEALTH).hasModifier(attributeModifierAdditionalHealth))
            player.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(attributeModifierAdditionalHealth);

        if (player == null) return;

        if (player.isOnGround())
            jumpCharges = extraJumpCharges;

        // Repulse villagers
        if(!player.level.isClientSide) {
            final double distance = 8D;
            final double farRunSpeed = 1D;
            final double nearRunSpeed = 1D;
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

        else if (player.isAlive() && !breatheMode.canBreatheWater() && weakLungs) {
            //if the player is in water, remove more air
            if (player.isEyeInFluid(FluidTags.WATER)) {
                int air = player.getAirSupply();
                if (air > -10)
                    player.setAirSupply(air-1);
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
        player.maxUpStep = stepSize;

        sync(player);
    }

    public void unhookAll(Player player) {
        if (player.getAttribute(ForgeMod.SWIM_SPEED.get()).hasModifier(attributeModifierSwimSpeed))
            player.getAttribute(ForgeMod.SWIM_SPEED.get()).removePermanentModifier(attributeModifierSwimSpeed.getId());
        if (player.getAttribute(Attributes.MAX_HEALTH).hasModifier(attributeModifierAdditionalHealth))
            player.getAttribute(Attributes.MAX_HEALTH).removePermanentModifier(attributeModifierAdditionalHealth.getId());
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

    protected void multiplyMotion(Player player, float mul) {
        player.setDeltaMovement(player.getDeltaMovement().multiply(mul, mul, mul));
    }

    public static class Builder<T extends LatexEntity> {
        final Supplier<EntityType<T>> entityType;
        LatexType type = LatexType.NEUTRAL;
        float groundSpeed = 1.0F;
        float swimSpeed = 1.0F;
        BreatheMode breatheMode = BreatheMode.NORMAL;
        float stepSize = 0.6F;
        boolean canGlide = false;
        int extraJumpCharges = 0;
        int additionalHealth = 4;
        boolean extraHands = false;
        boolean weakLungs = false;
        boolean fallImmunity = false;
        List<Class<? extends PathfinderMob>> scares = new ArrayList<>(ImmutableList.of(AbstractVillager.class));
        TransfurMode transfurMode = TransfurMode.REPLICATION;
        Optional<Pair<LatexVariant<?>, LatexVariant<?>>> fusionOf = Optional.empty();
        Optional<Pair<LatexVariant<?>, Class<? extends LivingEntity>>> mobFusionOf = Optional.empty();

        public Builder(Supplier<EntityType<T>> entityType) {
            this.entityType = entityType;
        }

        private void ignored() {}

        public static <T extends LatexEntity> Builder<T> of(Supplier<EntityType<T>> entityType) {
            return new Builder<T>(entityType);
        }

        public static <T extends LatexEntity> Builder<T> of(LatexVariant<?> variant, Supplier<EntityType<T>> entityType) {
            return (new Builder<T>(entityType)).faction(variant.type).groundSpeed(variant.groundSpeed)
                    .swimSpeed(variant.swimSpeed).breatheMode(variant.breatheMode).stepSize(variant.stepSize).glide(variant.canGlide).extraJumps(variant.extraJumpCharges)
                    .extraHands(variant.extraHands).weakLungs(variant.weakLungs).fallImmune(variant.fallImmunity).scares(variant.scares)
                    .transfurMode(variant.transfurMode);
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

        public Builder<T> gills() {
            return gills(false);
        }

        public Builder<T> gills(boolean suffocate_on_land) {
            this.breatheMode = suffocate_on_land ? BreatheMode.WATER : BreatheMode.ANY; return this;
        }

        public Builder<T> breatheMode(BreatheMode mode) {
            this.breatheMode = mode; return this;
        }

        public Builder<T> fallImmune() {
            this.fallImmunity = true; return this;
        }

        public Builder<T> fallImmune(boolean v) {
            this.fallImmunity = v; return this;
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

        public Builder<T> extraHands() {
            this.extraHands = true; return this;
        }

        public Builder<T> extraHands(boolean v) {
            this.extraHands = v; return this;
        }

        public Builder<T> absorbing() {
            return transfurMode(TransfurMode.ABSORPTION);
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

        public LatexVariant<T> build(ResourceLocation formId) {
            return new LatexVariant<>(formId, entityType, type, groundSpeed, swimSpeed, breatheMode, stepSize, canGlide, extraJumpCharges, additionalHealth, extraHands,
                    weakLungs, fallImmunity, scares, transfurMode, fusionOf, mobFusionOf);
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

    public static <M extends GenderedLatexEntity, F extends GenderedLatexEntity> GenderedVariant<M, F> register(GenderedVariant<M, F> variant) {
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

    public static ResourceLocation getLatexFormId(LatexVariant<?> variant) {
        for (var entry : ALL_LATEX_FORMS.entrySet()) {
            if (variant.ctor.equals(entry.getValue().ctor)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static @NotNull LatexVariant<?> getVariantFromEntity(@NotNull LatexEntity entity) {
        for (var entry : ALL_LATEX_FORMS.entrySet()) {
            if (entity.getType().equals(entry.getValue().ctor.get())) {
                return entry.getValue();
            }
        }

        throw new InvalidParameterException("Could not find form for entity \"" + entity + "\"");
    }
}
