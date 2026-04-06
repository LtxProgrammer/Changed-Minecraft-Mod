package net.ltxprogrammer.changed.entity;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.block.WhiteLatexTransportInterface;
import net.ltxprogrammer.changed.entity.ai.AssimilationBehavior;
import net.ltxprogrammer.changed.entity.ai.LatexAssimilationDecision;
import net.ltxprogrammer.changed.entity.ai.LookAtPlayerButNotHostGoal;
import net.ltxprogrammer.changed.entity.ai.UseAbilityGoal;
import net.ltxprogrammer.changed.entity.beast.*;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.entity.variant.EntityShape;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.network.syncher.ChangedEntityDataSerializers;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Cacheable;
import net.ltxprogrammer.changed.util.Color3;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static net.ltxprogrammer.changed.entity.variant.TransfurVariant.findEntityTransfurVariant;

public abstract class ChangedEntity extends Monster implements EntityShape.Provider {
    @Nullable private Player underlyingPlayer;
    private HairStyle hairStyle;
    private EyeStyle eyeStyle;
    private final Map<AbstractAbility<?>, Pair<Predicate<AbstractAbilityInstance>, AbstractAbilityInstance>> abilities = new HashMap<>();

    public double xCloakO;
    public double yCloakO;
    public double zCloakO;
    public double xCloak;
    public double yCloak;
    public double zCloak;
    public float oBob;
    public float bob;

    float crouchAmount;
    float crouchAmountO;
    public float flyAmount;
    public float flyAmountO;
    float tailDragAmount = 0.0F;
    float tailDragAmountO;

    Vec3 deltaMovementOO = Vec3.ZERO;
    Vec3 deltaMovementO = Vec3.ZERO;

    public Vec3 getDeltaMovement(float partialTicks) {
        return new Vec3(
                Mth.lerp(partialTicks, deltaMovementOO.x, deltaMovementO.x),
                this.onGround() ? 0.0 : Mth.lerp(partialTicks, deltaMovementOO.y, deltaMovementO.y),
                Mth.lerp(partialTicks, deltaMovementOO.z, deltaMovementO.z)
        );
    }

    final Map<SpringType.Direction, EnumMap<SpringType, SpringType.Simulator>> simulatedSprings;
    protected int blinkFatigue = 0;
    protected int blinkDuration = 0;

    public BasicPlayerInfo getBasicPlayerInfo() {
        if (underlyingPlayer instanceof PlayerDataExtension ext) {
            var variant = ext.getTransfurVariant();
            if (variant != null && variant.isTemporaryFromSuit()) {
                if (underlyingPlayer instanceof LivingEntityDataExtension lExt && lExt.getGrabbedBy() instanceof PlayerDataExtension gExt) {
                    return gExt.getBasicPlayerInfo();
                }
            }
            return ext.getBasicPlayerInfo();
        }
        else
            return this.entityData.get(DATA_LOCAL_VARIANT_INFO);
    }

    public float getTailDragAmount(float partialTicks) {
        return Mth.lerp(partialTicks, tailDragAmountO, tailDragAmount);
    }

    public float getSimulatedSpring(SpringType type, SpringType.Direction direction, float partialTicks) {
        var typeMap = simulatedSprings.computeIfAbsent(direction, dir -> new EnumMap<>(SpringType.class));
        return typeMap.computeIfAbsent(type, SpringType.Simulator::new).getSpring(partialTicks);
    }

    private void moveCloak() {
        this.xCloakO = this.xCloak;
        this.yCloakO = this.yCloak;
        this.zCloakO = this.zCloak;
        double d0 = this.getX() - this.xCloak;
        double d1 = this.getY() - this.yCloak;
        double d2 = this.getZ() - this.zCloak;
        double d3 = 10.0D;
        if (d0 > 10.0D) {
            this.xCloak = this.getX();
            this.xCloakO = this.xCloak;
        }

        if (d2 > 10.0D) {
            this.zCloak = this.getZ();
            this.zCloakO = this.zCloak;
        }

        if (d1 > 10.0D) {
            this.yCloak = this.getY();
            this.yCloakO = this.yCloak;
        }

        if (d0 < -10.0D) {
            this.xCloak = this.getX();
            this.xCloakO = this.xCloak;
        }

        if (d2 < -10.0D) {
            this.zCloak = this.getZ();
            this.zCloakO = this.zCloak;
        }

        if (d1 < -10.0D) {
            this.yCloak = this.getY();
            this.yCloakO = this.yCloak;
        }

        this.xCloak += d0 * 0.25D;
        this.zCloak += d2 * 0.25D;
        this.yCloak += d1 * 0.25D;
    }

    @Override
    public void aiStep() {
        this.oBob = this.bob;

        super.aiStep();

        float f;
        if (this.onGround() && !this.isDeadOrDying() && !this.isSwimming()) {
            f = Math.min(0.1F, (float)this.getDeltaMovement().horizontalDistance());
        } else {
            f = 0.0F;
        }

        this.bob += (f - this.bob) * 0.4F;
    }

    protected static final EntityDataAccessor<OptionalInt> DATA_TARGET_ID = SynchedEntityData.defineId(ChangedEntity.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
    protected static final EntityDataAccessor<BasicPlayerInfo> DATA_LOCAL_VARIANT_INFO = SynchedEntityData.defineId(ChangedEntity.class, ChangedEntityDataSerializers.BASIC_PLAYER_INFO);
    protected static final EntityDataAccessor<Byte> DATA_CHANGED_ENTITY_FLAGS = SynchedEntityData.defineId(ChangedEntity.class, EntityDataSerializers.BYTE);
    public static final int FLAG_IS_FLYING = 0;

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TARGET_ID, OptionalInt.empty());
        this.entityData.define(DATA_LOCAL_VARIANT_INFO, BasicPlayerInfo.random(this.random, this));
        this.entityData.define(DATA_CHANGED_ENTITY_FLAGS, (byte)0);
    }

    @Override
    public void setTarget(@Nullable LivingEntity entity) {
        super.setTarget(entity);
        this.entityData.set(DATA_TARGET_ID, entity != null ? OptionalInt.of(entity.getId()) : OptionalInt.empty());
    }

    @Nullable
    @Override
    public LivingEntity getTarget() {
        if (this.level().isClientSide) {
            var id = this.entityData.get(DATA_TARGET_ID);
            return id.isPresent() && this.level().getEntity(id.getAsInt()) instanceof LivingEntity livingEntity ? livingEntity : null;
        } else
            return super.getTarget();
    }

    protected <A extends AbstractAbilityInstance> A registerAbility(Predicate<A> predicate, A instance) {
        return (A) abilities.computeIfAbsent(instance.ability, type -> new Pair<>(
                inst -> predicate.test((A)inst), instance)).getSecond();
    }

    public float getCrouchAmount(float partialTicks) {
        return Mth.lerp(partialTicks, this.crouchAmountO, this.crouchAmount);
    }

    public float getFlyAmount(float partialTicks) {
        return Mth.lerp(partialTicks, this.flyAmountO, this.flyAmount);
    }

    public boolean isFullyCrouched() {
        return this.crouchAmount == 3.0F;
    }

    @Nullable
    public Player getUnderlyingPlayer() {
        return underlyingPlayer;
    }

    public void setUnderlyingPlayer(@Nullable Player player) {
        this.underlyingPlayer = player;
        if (player != null)
            abilities.clear();
    }

    public <A extends AbstractAbilityInstance> A getAbilityInstance(AbstractAbility<A> ability) {
        try {
            if (this.underlyingPlayer != null) {
                return ProcessTransfur.getPlayerTransfurVariant(this.underlyingPlayer).getAbilityInstance(ability);
            }

            return (A) abilities.get(ability).getSecond();
        } catch (Exception unused) {
            return null;
        }
    }

    public <A extends AbstractAbilityInstance> void ifAbilityInstance(AbstractAbility<A> ability, Consumer<A> consumer) {
        A instance = getAbilityInstance(ability);
        if (instance != null)
            consumer.accept(instance);
    }

    public @NotNull HairStyle getHairStyle() {
        return hairStyle != null ? hairStyle : HairStyle.BALD.get();
    }

    public EyeStyle getEyeStyle() {
        return eyeStyle;
    }

    public void setHairStyle(HairStyle style) {
        this.hairStyle = style != null ? style : HairStyle.BALD.get();
    }

    public void setEyeStyle(EyeStyle style) {
        this.eyeStyle = style != null ? style : EyeStyle.V2;
    }

    @Deprecated
    public Color3 getHairColor(int layer) { return Color3.WHITE; }

    public HairStyle getDefaultHairStyle() {
        if (this.getValidHairStyles() != null) {
            var styles = this.getValidHairStyles();
            return styles.isEmpty() ? HairStyle.BALD.get() : styles.get(this.random.nextInt(styles.size()));
        }

        else
            return HairStyle.BALD.get();
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return HairStyle.Collection.getAll();
    }

    public static @NotNull List<HairStyle> addHairStyle(@Nullable List<HairStyle> list, HairStyle... styles) {
        if (list == null)
            return new ArrayList<>(List.of(styles));
        else
            list.addAll(Arrays.stream(styles).toList());
        return list;
    }

    public boolean shouldShowName() {
        return underlyingPlayer != null && underlyingPlayer.level().isClientSide ?
                !UniversalDist.isLocalPlayer(this.getUnderlyingPlayer()) : super.shouldShowName();
    }

    public ItemStack getUseItem() {
        return underlyingPlayer == null ?
                super.getUseItem() :
                underlyingPlayer.getUseItem();
    }

    public boolean isUsingItem() {
        return underlyingPlayer == null ?
                super.isUsingItem() :
                underlyingPlayer.isUsingItem();
    }

    public InteractionHand getUsedItemHand() {
        return underlyingPlayer == null ?
                super.getUsedItemHand() :
                underlyingPlayer.getUsedItemHand();
    }

    public int getUseItemRemainingTicks() {
        return underlyingPlayer == null ?
                super.getUseItemRemainingTicks() :
                underlyingPlayer.getUseItemRemainingTicks();
    }

    public int getTicksUsingItem() {
        return underlyingPlayer == null ?
                super.getTicksUsingItem() :
                underlyingPlayer.getTicksUsingItem();
    }

    public ItemStack getMainHandItem() {
        return underlyingPlayer == null ?
                super.getMainHandItem() :
                underlyingPlayer.getMainHandItem();
    }

    public ItemStack getOffhandItem() {
        return underlyingPlayer == null ?
                super.getOffhandItem() :
                underlyingPlayer.getOffhandItem();
    }

    public boolean isAutoSpinAttack() {
        return underlyingPlayer == null ?
                super.isAutoSpinAttack() :
                underlyingPlayer.isAutoSpinAttack();
    }

    public int getDepthStriderLevel() {
        return 0;
    }

    @Override
    protected boolean canEnterPose(Pose pose) {
        if (overridePose != null && overridePose != pose)
            return false;
        return super.canEnterPose(pose);
    }

    @Override
    @NotNull
    public AABB getBoundingBoxForPose(@NotNull Pose pose) {
        return super.getBoundingBoxForPose(pose);
    }

    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions core = this.getType().getDimensions();

        if (WhiteLatexTransportInterface.isEntityInWhiteLatex(this.maybeGetUnderlying()))
            return EntityDimensions.scalable(core.width, core.width);

        return (switch (Objects.requireNonNullElse(overridePose, pose)) {
            case STANDING -> core;
            case SLEEPING -> SLEEPING_DIMENSIONS;
            case FALL_FLYING, SWIMMING, SPIN_ATTACK -> EntityDimensions.scalable(core.width, core.width);
            case CROUCHING -> EntityDimensions.scalable(core.width, core.height - 0.3f);
            case DYING -> EntityDimensions.fixed(0.2f, 0.2f);
            default -> core;
        }).scale(getBasicPlayerInfo().getSize(this) * this.getScale());
    }

    public LatexType getLatexType() {
        return ChangedLatexTypes.NONE.get();
    }

    public abstract TransfurMode getTransfurMode();

    public boolean isPreventingPlayerRest(Player player) {
        return getLatexType().isHostileTo(LatexType.getEntityLatexType(player));
    }

    protected float getEyeHeightMul() {
        if (this.isCrouching())
            return 0.83F;
        else
            return 0.93F;
    }

    protected static boolean checkSpawnBlock(ServerLevelAccessor world, MobSpawnType reason, BlockPos pos) {
        if (reason != MobSpawnType.NATURAL)
            return true;
        int index = 3;
        do {
            final BlockPos thisPos = pos;
            BlockState state = world.getBlockState(thisPos);
            pos = thisPos.below();
            if (state.is(ChangedTags.Blocks.LATEX_SPAWNABLE_ON))
                return true;
            if (state.isAir() || !state.isCollisionShapeFullBlock(world, thisPos))
                continue;
            return false;
        } while (index-- > 0);

        return false;
    }

    public static <T extends ChangedEntity> boolean checkEntitySpawnRules(EntityType<T> entityType, ServerLevelAccessor world, MobSpawnType reason, BlockPos pos, RandomSource random) {
        if (pos.getY() < world.getSeaLevel() - 10)
            return false;
        if (!checkSpawnBlock(world, reason, pos))
            return false;
        return Monster.checkMonsterSpawnRules(entityType, world, reason, pos, random);
    }

    @Override
    public float getWalkTargetValue(BlockPos p_33013_, LevelReader p_33014_) {
        return 0.0f;
    }

    public static <T extends ChangedEntity> ChangedEntities.VoidConsumer getInit(RegistryObject<EntityType<T>> registryObject, SpawnPlacements.Type spawnPlacement, SpawnPlacements.SpawnPredicate<T> spawnPredicate) {
        return () -> SpawnPlacements.register(registryObject.get(), spawnPlacement, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawnPredicate);
    }

    @Deprecated
    protected TransfurVariant<?> getTransfurVariant() {
        return getSelfVariant();
    }

    protected TransfurVariant<?> getTransfurVariant(LatexAssimilationDecision.Method method) {
        return this.getTransfurVariant();
    }

    protected float computeTransfurDamage() {
        IAbstractChangedEntity self = IAbstractChangedEntity.forEntity(this);

        float damage = (float)self.getEntity().getAttributeValue(ChangedAttributes.TRANSFUR_DAMAGE.get());
        damage = ProcessTransfur.difficultyAdjustTransfurAmount(self.getLevel().getDifficulty(), damage, self);
        float attackStrengthScale = underlyingPlayer != null ? underlyingPlayer.getAttackStrengthScale(0.5F) : 1.0F;
        damage *= 0.2F + attackStrengthScale * attackStrengthScale * 0.8F;

        return damage;
    }

    /**
     * Returns a decision on how this entity wants to transfur the target entity
     * @param cause tagged transfur cause. Expected to be GRAB_REPLICATE or GRAB_ABSORB for entity sources.
     * @param targetEntity target entity that this entity is deciding how to assimilate
     * @return decision on how this entity wants to transfur the target entity, or null if it doesn't want to.
     */
    public @Nullable LatexAssimilationDecision<?> makeLatexAssimilationDecision(TransfurCause cause, LivingEntity targetEntity) {
        IAbstractChangedEntity self = IAbstractChangedEntity.forEntity(this);

        return switch (cause) {
            case GRAB_REPLICATE -> LatexAssimilationDecision.strongReplication(getTransfurVariant(LatexAssimilationDecision.Method.REPLICATION),
                    self.replicate(),
                    this.computeTransfurDamage(),
                    this::onReplicateOther);
            case GRAB_ABSORB -> LatexAssimilationDecision.strongAbsorption(getTransfurVariant(LatexAssimilationDecision.Method.ABSORPTION),
                    self.absorb(),
                    this.computeTransfurDamage());
            default -> null;
        };
    }

    public TransfurVariant<?> getSelfVariant() {
        return findEntityTransfurVariant(this);
    }

    public ChangedEntity(EntityType<? extends ChangedEntity> type, Level level) {
        super(type, level);
        this.setAttributes(getAttributes());
        this.setHealth((float)this.getAttributes().getInstance(Attributes.MAX_HEALTH).getBaseValue());
        if (!type.is(ChangedTags.EntityTypes.ARMLESS) && this.getNavigation() instanceof GroundPathNavigation navigation)
            navigation.setCanOpenDoors(true);

        hairStyle = this.getDefaultHairStyle();
        if (level.isClientSide) { // Springs are only simulated on the client side
            simulatedSprings = new HashMap<>();
        } else {
            simulatedSprings = Map.of();
        }
    }

    protected static double computeStepHeightOffset(double intendedStepHeight) {
        return intendedStepHeight - 0.6;
    }

    public static AttributeSupplier.Builder createLatexAttributes() {
        return Monster.createMonsterAttributes().add(ChangedAttributes.TRANSFUR_DAMAGE.get(), 3.0D)
                .add(ChangedAttributes.SPRINT_SPEED.get(), 1.0D)
                .add(ChangedAttributes.SNEAK_SPEED.get(), 1.0D)
                .add(ChangedAttributes.AIR_CAPACITY.get(), 15.0)
                .add(ChangedAttributes.JUMP_STRENGTH.get(), 1.0D)
                .add(ChangedAttributes.FALL_RESISTANCE.get(), 1.0D);
    }

    protected void setAttributes(AttributeMap attributes) {
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(24.0);
        attributes.getInstance(Attributes.FOLLOW_RANGE).setBaseValue(40.0);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.0);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(1.0);
        attributes.getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(3.0);
        attributes.getInstance(Attributes.ARMOR).setBaseValue(4.0);
        attributes.getInstance(ForgeMod.STEP_HEIGHT_ADDITION.get()).setBaseValue(computeStepHeightOffset(0.7));
        attributes.getInstance(ChangedAttributes.SPRINT_SPEED.get()).setBaseValue(1.0D);
        attributes.getInstance(ChangedAttributes.SNEAK_SPEED.get()).setBaseValue(1.0D);
        attributes.getInstance(ChangedAttributes.AIR_CAPACITY.get()).setBaseValue(15.0);
        attributes.getInstance(ChangedAttributes.JUMP_STRENGTH.get()).setBaseValue(1.0);
        attributes.getInstance(ChangedAttributes.FALL_RESISTANCE.get()).setBaseValue(1.0);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    protected static int getLevelBrightnessAt(Level level, BlockPos pos) {
        return level.isThundering() ? level.getMaxLocalRawBrightness(pos, 10) : level.getMaxLocalRawBrightness(pos);
    }

    protected int getLevelBrightnessAt(BlockPos pos) {
        return this.level().isThundering() ? this.level().getMaxLocalRawBrightness(pos, 10) : this.level().getMaxLocalRawBrightness(pos);
    }

    protected boolean targetSelectorTest(LivingEntity livingEntity) {
        if (livingEntity instanceof Player player && ChangedCompatibility.isPlayerUsedByOtherMod(player))
            return false;

        if (livingEntity.getVehicle() instanceof SeatEntity seat && seat.shouldSeatedBeInvisible())
            return false;

        if (livingEntity instanceof LivingEntityDataExtension ext && ext.getGrabbedBy() != null) {
            var ability = AbstractAbility.getAbilityInstance(ext.getGrabbedBy(), ChangedAbilities.GRAB_ENTITY_ABILITY.get());
            if (ability != null && ability.suited && ability.grabbedEntity == livingEntity)
                return false;
        }

        LatexAssimilationDecision<?> decision = switch (this.getTransfurMode()) {
            case REPLICATION -> this.makeLatexAssimilationDecision(TransfurCause.GRAB_REPLICATE, livingEntity);
            case ABSORPTION -> this.makeLatexAssimilationDecision(TransfurCause.GRAB_ABSORB, livingEntity);
            default -> null;
        };
        AssimilationBehavior behavior = ProcessTransfur.computeAssimilationBehavior(livingEntity, decision);
        if (behavior != null)
            return true;

        var targetLatexType = LatexType.getEntityLatexType(livingEntity);

        return targetLatexType != null && getLatexType().isHostileTo(targetLatexType);
    }

    @Deprecated
    public TransfurContext getReplicateContext() {
        if (underlyingPlayer == null)
            return TransfurContext.npcLatexHazard(this, TransfurCause.GRAB_REPLICATE);
        else
            return TransfurContext.playerLatexHazard(underlyingPlayer, TransfurCause.GRAB_REPLICATE);
    }

    @Deprecated
    public TransfurContext getAbsorbContext() {
        if (underlyingPlayer == null)
            return TransfurContext.npcLatexHazard(this, TransfurCause.GRAB_ABSORB);
        else
            return TransfurContext.playerLatexHazard(underlyingPlayer, TransfurCause.GRAB_ABSORB);
    }

    public LivingEntity maybeGetUnderlying() {
        return underlyingPlayer != null ? underlyingPlayer : this;
    }

    public boolean tryTransfurTarget(Entity entity) {
        if (!this.getType().is(ChangedTags.EntityTypes.LATEX))
            return false;
        if (!(entity instanceof LivingEntity target))
            return false;

        LatexAssimilationDecision<?> decision = switch (this.getTransfurMode()) {
            case REPLICATION -> this.makeLatexAssimilationDecision(TransfurCause.GRAB_REPLICATE, target);
            case ABSORPTION -> this.makeLatexAssimilationDecision(TransfurCause.GRAB_ABSORB, target);
            default -> null;
        };
        AssimilationBehavior behavior = ProcessTransfur.computeAssimilationBehavior(target, decision);
        if (behavior == null)
            return false;

        behavior.stepAssimilate();
        return true;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if (!tryTransfurTarget(entity))
            return super.doHurtTarget(entity);
        else
            return true;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        final ChangedEntity self = this;
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.4, false));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.3, 120, false));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4f) {
            public boolean canUse() {
                if (self.getTarget() != null && self.getTarget().position().y() > self.position().y)
                    return super.canUse();
                else
                    return false;
            }
        });
        if (!this.getType().is(ChangedTags.EntityTypes.ARMLESS) && GoalUtils.hasGroundPathNavigation(this))
            this.goalSelector.addGoal(4, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(4, new UseAbilityGoal(Cacheable.of(() -> abilities), this));

        if (this instanceof WhiteLatexEntity)
            this.targetSelector.addGoal(1, new HurtByTargetGoal(this, WhiteLatexEntity.class).setAlertOthers());
        else if (this instanceof AbstractDarkLatexEntity)
            this.targetSelector.addGoal(1, new HurtByTargetGoal(this, AbstractDarkLatexEntity.class).setAlertOthers());
        else
            this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, ChangedEntity.class, true, this::targetSelectorTest));
        if (this.getType().is(ChangedTags.EntityTypes.LATEX)) {
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true, this::targetSelectorTest));
            this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, this::targetSelectorTest));
        }
        this.goalSelector.addGoal(6, new LookAtPlayerButNotHostGoal(this, Player.class, 7.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, ChangedEntity.class, 7.0F, 0.2F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Villager.class, 7.0F, 0.2F));
        if (!(this instanceof AquaticEntity))
            this.goalSelector.addGoal(5, new FloatGoal(this));
        if (this instanceof PowderSnowWalkable)
            this.goalSelector.addGoal(5, new ChangedClimbOnTopOfPowderSnowGoal(this, this.level()));
    }

    @Override
    public void tick() {
        super.tick();
        moveCloak();
        variantTick(this.level());

        var player = getUnderlyingPlayer();
        if (player != null) { // ticking whilst hosting a player, mirror players inputs
            mirrorLiving(player);
        }
    }
    
    public void mirrorLiving(LivingEntity player) {
        if (player.level() != this.level())
            this.setLevel(player.level());

        TransfurVariantInstance.syncEntityPosRotWithEntity(this, player);
        
        this.swingingArm = player.swingingArm;
        this.swinging = player.swinging;
        this.swingTime = player.swingTime;

        this.setDeltaMovement(player.getDeltaMovement());
        
        this.horizontalCollision = player.horizontalCollision;
        this.verticalCollision = player.verticalCollision;
        this.setOnGround(player.onGround());
        this.setShiftKeyDown(player.isCrouching());
        if (this.isSprinting() != player.isSprinting())
            this.setSprinting(player.isSprinting());
        this.setSwimming(player.isSwimming());

        this.hurtTime = player.hurtTime;
        this.deathTime = player.deathTime;
        this.walkAnimation.position = player.walkAnimation.position;
        this.walkAnimation.speed = player.walkAnimation.speed;
        this.walkAnimation.speedOld = player.walkAnimation.speedOld;
        this.attackAnim = player.attackAnim;
        this.oAttackAnim = player.oAttackAnim;
        this.flyDist = player.flyDist;
        this.jumping = player.jumping;

        this.wasTouchingWater = player.wasTouchingWater;
        this.swimAmount = player.swimAmount;
        this.swimAmountO = player.swimAmountO;
        this.forgeFluidTypeOnEyes = player.forgeFluidTypeOnEyes;

        this.fallFlyTicks = player.fallFlyTicks;

        this.setSharedFlag(7, player.isFallFlying());

        this.vehicle = player.getVehicle();

        this.useItemRemaining = player.useItemRemaining;

        Pose pose = this.getPose();
        this.setPose(player.getPose());

        if (pose != this.getPose())
            this.refreshDimensions();
        if(player.getSleepingPos().isPresent())
            this.setSleepingPos(player.getSleepingPos().get());
        else
            this.clearSleepingPos();
    }

    @Override
    public HumanoidArm getMainArm() {
        if (this.underlyingPlayer != null)
            return this.underlyingPlayer.getMainArm();
        else
            return super.getMainArm();
    }

    @Override
    public boolean isLeftHanded() {
        if (this.underlyingPlayer != null)
            return this.underlyingPlayer.getMainArm() == HumanoidArm.LEFT;
        else
            return super.isLeftHanded();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (this.underlyingPlayer != null)
            return this.underlyingPlayer.getCapability(cap);
        else
            return super.getCapability(cap);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing) {
        if (this.underlyingPlayer != null)
            return this.underlyingPlayer.getCapability(capability, facing);
        else
            return super.getCapability(capability, facing);
    }

    public Color3 getTransfurColor(TransfurCause cause) { return Color3.WHITE; }

    public float getDripRate(float damage) {
        return Mth.lerp(damage, 0.02f, 0.1f); // 1/50 -> 1/10
    }

    public final float computeHealthRatio() {
        if (this.underlyingPlayer != null)
            return this.underlyingPlayer.getHealth() / this.underlyingPlayer.getMaxHealth();
        else
            return this.getHealth() / this.getMaxHealth();
    }

    protected final Vec3 calculateHorizontalViewVector(float yRot) {
        float f1 = -yRot * ((float)Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        return new Vec3((double)(f3), 0.0, (double)(f2));
    }

    /**
     * Executed by both entity and tf'd player
     * @param level
     */
    public void variantTick(Level level) {
        this.crouchAmountO = this.crouchAmount;
        this.flyAmountO = this.flyAmount;

        if (this.isCrouching()) {
            this.crouchAmount += 0.2F;
            if (this.crouchAmount > 3.0F) {
                this.crouchAmount = 3.0F;
            }
        } else {
            this.crouchAmount = 0.0F;
        }

        if (this.isFlying()) {
            this.flyAmount = Math.min(1.0F, this.flyAmount + 0.15F);
        } else {
            this.flyAmount = Math.max(0.0F, this.flyAmount - 0.15F);
        }

        if (!this.level().isClientSide) return;

        this.tailDragAmountO = this.tailDragAmount;

        this.tailDragAmount *= 0.75F;
        this.tailDragAmount -= (float) (Math.toRadians(Mth.wrapDegrees(this.yBodyRot - this.yBodyRotO)) * 0.35F);
        this.tailDragAmount = Mth.clamp(this.tailDragAmount, -1.1F, 1.1F);

        this.deltaMovementOO = this.deltaMovementO;
        this.deltaMovementO = this.getDeltaMovement();

        simulatedSprings.forEach((direction, map) -> {
            final float deltaVelocity = direction.apply(this);
            map.forEach((springType, simulator) -> {
                simulator.tick(deltaVelocity);
            });
        });

        this.tickBlink();
    }

    private static final int BLINK_MIN = 150;
    private static final int BLINK_MAX = 180;
    protected void tickBlink() {
        if (this.isSleeping()) {
            blinkDuration = 5;
        }

        if (blinkDuration <= 0)
            blinkFatigue++;
        else {
            blinkFatigue = 0;
            blinkDuration--;
        }

        if (blinkFatigue > BLINK_MIN) {
            if (blinkFatigue > BLINK_MAX)
                blink();
            else if (random.nextInt(0, BLINK_MAX - BLINK_MIN) < blinkFatigue - BLINK_MIN)
                blink();
        }
    }

    public void blink() {
        blinkDuration = 3;
    }

    public boolean isBlinking() {
        return blinkDuration > 0;
    }

    public float getVerticalSpringOffset() {
        return 0f;
    }

    public float getHorizontalSpringOffset() {
        return 0f;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (this.tickCount < 30)
            return false; //
        return super.hurt(source, amount);
    }

    public double getPassengersRidingOffset() {
        return this.isCrouching() ? -0.4 : 0.0;
    }

    public double getMyRidingOffset() {
        return -0.475;
    }

    public CompoundTag getLeashInfoTag() {
        return this.leashInfoTag;
    }

    public void setLeashInfoTag(CompoundTag tag) {
        this.leashInfoTag = tag;
    }

    @Override
    public void tickLeash() {
        super.tickLeash();
    }

    @Override
    protected double followLeashSpeed() {
        return 0.4; // Matches melee attack goal speed
    }

    public static class ChangedClimbOnTopOfPowderSnowGoal extends ClimbOnTopOfPowderSnowGoal {
        protected final ChangedEntity latex;
        protected final Level level;

        public ChangedClimbOnTopOfPowderSnowGoal(ChangedEntity p_204055_, Level p_204056_) {
            super(p_204055_, p_204056_);
            latex = p_204055_;
            level = p_204056_;
        }

        @Override
        public boolean canUse() {
            boolean flag = this.latex.wasInPowderSnow || this.latex.isInPowderSnow;
            if (flag && this.latex instanceof PowderSnowWalkable) {
                BlockPos blockpos = this.latex.blockPosition().above();
                BlockState blockstate = this.level.getBlockState(blockpos);
                return blockstate.is(Blocks.POWDER_SNOW) || blockstate.getCollisionShape(this.level, blockpos) == Shapes.empty();
            } else {
                return super.canUse();
            }
        }
    }

    public boolean isMovingSlowly() {
        return this.isCrouching() || this.isVisuallyCrawling();
    }

    public Pose overridePose = null;
    @Override
    public boolean isVisuallySwimming() {
        if (overridePose == Pose.SWIMMING)
            return true;
        return super.isVisuallySwimming();
    }

    public boolean isAllowedToSwim() {
        return true;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("HairStyle"))
            hairStyle = ChangedRegistry.HAIR_STYLE.getValue(tag.getInt("HairStyle"));
        if (tag.contains("LocalVariantInfo")) {
            BasicPlayerInfo info = new BasicPlayerInfo();
            info.load(tag.getCompound("LocalVariantInfo"));
            this.entityData.set(DATA_LOCAL_VARIANT_INFO, info);
        }
        if (tag.contains("ChangedEntityFlags"))
            this.entityData.set(DATA_CHANGED_ENTITY_FLAGS, tag.getByte("ChangedEntityFlags"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("HairStyle", ChangedRegistry.HAIR_STYLE.getID(hairStyle));
        {
            var bpi = new CompoundTag();
            this.entityData.get(DATA_LOCAL_VARIANT_INFO).save(bpi);
            tag.put("LocalVariantInfo", bpi);
        }
        tag.putByte("ChangedEntityFlags", this.entityData.get(DATA_CHANGED_ENTITY_FLAGS));
    }

    public boolean getChangedEntityFlag(int id) {
        return (this.entityData.get(DATA_CHANGED_ENTITY_FLAGS) & (0b1 << id)) >> id == 1;
    }

    public void setChangedEntityFlag(int id, boolean value) {
        byte flags = this.entityData.get(DATA_CHANGED_ENTITY_FLAGS);
        byte givenFlag = (byte)(0b1 << id);

        if (((flags & (0b1 << id)) >> id == 1) == value)
            return;

        this.entityData.set(DATA_CHANGED_ENTITY_FLAGS, (byte)(flags ^ givenFlag));
    }

    public boolean isFlying() {
        return getChangedEntityFlag(FLAG_IS_FLYING);
    }

    public void onDamagedBy(LivingEntity source) {

    }

    public void onReplicateOther(IAbstractChangedEntity other) {

    }

    public void onSuitOther(IAbstractChangedEntity other) {

    }

    public void copyTraitsFrom(IAbstractChangedEntity entity) {
        getBasicPlayerInfo().copyFrom(entity.getChangedEntity().getBasicPlayerInfo());
    }

    public CompoundTag savePlayerVariantData() {
        return new CompoundTag();
    }

    public void readPlayerVariantData(CompoundTag tag) {

    }

    public UseItemMode getItemUseMode() {
        var instance = IAbstractChangedEntity.forEitherSafe(this.maybeGetUnderlying())
                .map(IAbstractChangedEntity::getTransfurVariantInstance).orElse(null);
        if (instance != null)
            return instance.getItemUseMode();
        var grabAbility = getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
        if (grabAbility != null && (grabAbility.shouldAnimateArms() || grabAbility.grabbedHasControl))
            return UseItemMode.NONE;
        var variant = getSelfVariant();
        if (variant != null)
            return getSelfVariant().itemUseMode;
        else
            return UseItemMode.NORMAL;
    }

    @NotNull
    public EntityShape getEntityShape() {
        return EntityShape.ANTHRO;
    }

    public boolean isItemAllowedInSlot(ItemStack stack, EquipmentSlot slot) {
        return true;
    }

    @Override
    public int getMaxAirSupply() {
        // This function is called in the ctor of Entity, so attributes aren't ready yet.
        return this.getAttributes() == null ? 300 : Math.round(20f * (float) this.getAttributes().getValue(ChangedAttributes.AIR_CAPACITY.get()));
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return dimensions.height * this.getEyeHeightMul();
    }

    @Override
    public float getFlyingSpeed() {
        if (underlyingPlayer != null && underlyingPlayer.getAbilities().flying && !underlyingPlayer.isPassenger()) {
            return this.isSprinting() ? underlyingPlayer.getAbilities().getFlyingSpeed() * 2.0F : underlyingPlayer.getAbilities().getFlyingSpeed();
        } else {
            return this.isSprinting() ? 0.025999999F : 0.02F;
        }
    }
}
