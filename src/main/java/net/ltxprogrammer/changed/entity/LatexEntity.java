package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.effect.particle.LatexDripParticle;
import net.ltxprogrammer.changed.entity.beast.*;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

import static net.ltxprogrammer.changed.entity.variant.LatexVariant.findLatexEntityVariant;

public abstract class LatexEntity extends Monster {
    @Nullable
    private Player underlyingPlayer;

    @Nullable
    public Player getUnderlyingPlayer() {
        return underlyingPlayer;
    }

    public void setUnderlyingPlayer(@Nullable Player player) {
        this.underlyingPlayer = player;
    }

    public boolean shouldShowName() {
        return underlyingPlayer != null && underlyingPlayer.level.isClientSide ?
                FMLEnvironment.dist == Dist.CLIENT && !(underlyingPlayer instanceof LocalPlayer) : super.isCustomNameVisible();
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


    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions core = this.getType().getDimensions();

        if (this.isVisuallySwimming())
            return EntityDimensions.scalable(core.width, core.width);
        return switch (pose) {
            case STANDING -> core;
            case SLEEPING -> SLEEPING_DIMENSIONS;
            case FALL_FLYING, SWIMMING, SPIN_ATTACK -> EntityDimensions.scalable(core.width, core.width);
            case CROUCHING -> EntityDimensions.scalable(core.width, core.height - 0.2f);
            case DYING -> EntityDimensions.fixed(0.2f, 0.2f);
            default -> core;
        };
    }

    public abstract LatexType getLatexType();
    public abstract TransfurMode getTransfurMode();

    public boolean isPreventingPlayerRest(Player player) {
        return getLatexType().isHostileTo(LatexType.getEntityLatexType(player));
    }

    public float getEyeHeightMul() {
        if (this.isCrouching())
            return 0.82F;
        else
            return 0.93F;
    }

    public static boolean isDarkEnoughToSpawn(ServerLevelAccessor world, BlockPos pos, Random random) {
        if (world.getBrightness(LightLayer.SKY, pos) > random.nextInt(50)) {
            return false;
        } else if (world.getBrightness(LightLayer.BLOCK, pos) > 0) {
            return false;
        } else {
            int i = world.getLevel().isThundering() ? world.getMaxLocalRawBrightness(pos, 10) : world.getMaxLocalRawBrightness(pos);
            return i <= random.nextInt(10);
        }
    }

    public static <T extends LatexEntity> boolean checkLatexSpawnRules(EntityType<T> entityType, ServerLevelAccessor world, MobSpawnType reason, BlockPos pos, Random random) {
        if (world.getDifficulty() == Difficulty.PEACEFUL)
            return false;
        if (!isDarkEnoughToSpawn(world, pos, random))
            return false;
        return Mob.checkMobSpawnRules(entityType, world, reason, pos, random);
    }

    public static <T extends LatexEntity> ChangedEntities.VoidConsumer getInit(RegistryObject<EntityType<T>> registryObject, SpawnPlacements.Type spawnPlacement) {
        return () -> SpawnPlacements.register(registryObject.get(), spawnPlacement, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, LatexEntity::checkLatexSpawnRules);
    }

    public LatexVariant<?> getTransfurVariant() {
        return getSelfVariant();
    }

    public LatexVariant<?> getSelfVariant() {
        return findLatexEntityVariant(this);
    }

    public LatexEntity(EntityType<? extends LatexEntity> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        this.setAttributes(getAttributes());
        this.setHealth((float)this.getAttributes().getInstance(Attributes.MAX_HEALTH).getBaseValue());
        if (!(this instanceof Pudding) && this.getNavigation() instanceof GroundPathNavigation navigation)
            navigation.setCanOpenDoors(true);
    }

    protected void setAttributes(AttributeMap attributes) {
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(getLatexMaxHealth());
        attributes.getInstance(Attributes.FOLLOW_RANGE).setBaseValue(40.0);
        if (this instanceof LatexBenignWolf) attributes.getInstance(Attributes.FOLLOW_RANGE).setBaseValue(4.0);
        if (this instanceof DarkLatexEntity) attributes.getInstance(Attributes.FOLLOW_RANGE).setBaseValue(25.0);
        if (this instanceof WhiteLatexWolf) attributes.getInstance(Attributes.FOLLOW_RANGE).setBaseValue(20.0);
        if (this instanceof LatexRaccoon) attributes.getInstance(Attributes.FOLLOW_RANGE).setBaseValue(8.0);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(getLatexLandSpeed());
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(getLatexSwimSpeed());
        attributes.getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(3.0);
        attributes.getInstance(Attributes.ARMOR).setBaseValue(4.0);
    }

    @Override
    public @NotNull Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        final Predicate<LivingEntity> ENEMY_FACTION_OR_NOT_LATEXED_OR_CAN_FUSE = livingEntity -> {
            for (var checkVariant : LatexVariant.MOB_FUSION_LATEX_FORMS.values()) {
                if (checkVariant.isFusionOf(getSelfVariant(), livingEntity.getClass()))
                    return true;
            }
            if (!livingEntity.getType().is(ChangedTags.EntityTypes.HUMANOIDS) && !(livingEntity instanceof LatexEntity))
                return false;
            if (getLatexType().isHostileTo(LatexType.getEntityLatexType(livingEntity)))
                return true;
            LatexVariant<?> playerVariant = LatexVariant.getEntityVariant(livingEntity);
            if (livingEntity instanceof Player && !livingEntity.level.getGameRules().getBoolean(ChangedGameRules.RULE_NPC_WANT_FUSE_PLAYER))
                return false;
            for (var checkVariant : LatexVariant.FUSION_LATEX_FORMS.values()) {
                if (checkVariant.isFusionOf(getSelfVariant(), playerVariant))
                    return true;
            }

            return false;
        };

        final LatexEntity self = this;
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.36, false));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.3));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4f) {
            public boolean canUse() {
                if (self.getTarget() != null && self.getTarget().position().y() > self.position().y)
                    return super.canUse();
                else
                    return false;
            }
        });
        if (!(this instanceof Pudding) && GoalUtils.hasGroundPathNavigation(this))
            this.goalSelector.addGoal(4, new OpenDoorGoal(this, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LatexEntity.class, true, ENEMY_FACTION_OR_NOT_LATEXED_OR_CAN_FUSE));
        if (!(this.getType().is(ChangedTags.EntityTypes.ORGANIC_LATEX))) {
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true, ENEMY_FACTION_OR_NOT_LATEXED_OR_CAN_FUSE));
            this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, ENEMY_FACTION_OR_NOT_LATEXED_OR_CAN_FUSE));
        }
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        if (!(this instanceof AquaticEntity))
            this.goalSelector.addGoal(5, new FloatGoal(this));
        if (this instanceof PowderSnowWalkable)
            this.goalSelector.addGoal(5, new LatexClimbOnTopOfPowderSnowGoal(this, this.level));
    }

    @Override
    public void tick() {
        super.tick();
        visualTick(this.level);
        if (this instanceof UniqueEffect unique)
            unique.effectTick(this.level, this);

        var variant = getSelfVariant();
        if (variant == null) return;

        if (this.vehicle != null && (variant.rideable() || !variant.hasLegs))
            this.stopRiding();
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    public abstract ChangedParticles.Color3 getDripColor();

    public void visualTick(Level level) {
        if (this.getType().is(ChangedTags.EntityTypes.ORGANIC_LATEX))
            return;

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            if (level instanceof ClientLevel clientLevel) {
                if (level.random.nextInt(25) == 0) {
                    ChangedParticles.Color3 color = getDripColor();
                    if (color != null) {
                        EntityDimensions dimensions = getDimensions(getPose());
                        double dh = level.random.nextDouble(dimensions.height);
                        double dx = (level.random.nextDouble(dimensions.width) - (0.5 * dimensions.width));
                        double dz = (level.random.nextDouble(dimensions.width) - (0.5 * dimensions.width));
                        LatexDripParticle.setNextColor(color);
                        clientLevel.addParticle(ChangedParticles.DRIPPING_LATEX, xo + dx * 1.2, yo + dh, zo + dz * 1.2, 0.0, 0.0, 0.0);
                    }
                }
            }
        });
    }

    public double getPassengersRidingOffset() {
        return this.isCrouching() ? -0.4 : 0.0;
    }

    public double getMyRidingOffset() {
        return -0.4;
    }

    private <T> T callIfNotNull(LatexVariant<?> variant, Function<LatexVariant<?>, T> func, T def) {
        return variant == null ? def : func.apply(variant);
    }

    public double getLatexMaxHealth() { return callIfNotNull(getSelfVariant(), variant -> variant.additionalHealth + 20.0, 20.0); }

    public double getLatexLandSpeed() { return callIfNotNull(getSelfVariant(), variant -> (double)variant.groundSpeed, 1.0); }

    public double getLatexSwimSpeed() { return callIfNotNull(getSelfVariant(), variant -> (double)variant.swimSpeed, 1.0); }

    public static class LatexClimbOnTopOfPowderSnowGoal extends ClimbOnTopOfPowderSnowGoal {
        protected final LatexEntity latex;
        protected final Level level;

        public LatexClimbOnTopOfPowderSnowGoal(LatexEntity p_204055_, Level p_204056_) {
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

    public int overrideVisuallySwimming = 0;
    @Override
    public boolean isVisuallySwimming() {
        if (overrideVisuallySwimming > 0)
            return true;
        return super.isVisuallySwimming();
    }
}
