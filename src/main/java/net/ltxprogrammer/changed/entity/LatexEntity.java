package net.ltxprogrammer.changed.entity;

import com.google.common.collect.ImmutableMap;
import net.ltxprogrammer.changed.entity.beast.AquaticEntity;
import net.ltxprogrammer.changed.entity.beast.Pudding;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
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
        return 0.95F;
    }

    public static <T extends LatexEntity> ChangedEntities.VoidConsumer getInit(RegistryObject<EntityType<T>> registryObject, SpawnPlacements.Type spawnPlacement) {
        return () -> SpawnPlacements.register(registryObject.get(), spawnPlacement, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, world, reason, pos, random) ->
                        (world.getDifficulty() != Difficulty.PEACEFUL &&
                        Mob.checkMobSpawnRules(entityType, world, reason, pos, random)));
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
        this.setHealth((float) getLatexMaxHealth());
        if (!(this instanceof Pudding) && this.getNavigation() instanceof GroundPathNavigation navigation)
            navigation.setCanOpenDoors(true);
    }

    protected void setAttributes(AttributeMap attributes) {
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(getLatexMaxHealth());
        attributes.getInstance(Attributes.FOLLOW_RANGE).setBaseValue(24.0);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(getLatexLandSpeed());
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(getLatexSwimSpeed());
        attributes.getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(3.0);
        attributes.getInstance(Attributes.ARMOR).setBaseValue(2.0);
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
        if (!(this instanceof OrganicLatex)) {
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true, ENEMY_FACTION_OR_NOT_LATEXED_OR_CAN_FUSE));
            this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, ENEMY_FACTION_OR_NOT_LATEXED_OR_CAN_FUSE));
        }
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        if (!(this instanceof AquaticEntity))
            this.goalSelector.addGoal(5, new FloatGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        visualTick(this.level);
        effectTick(this.level, this);
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    public abstract ChangedParticles.Color3 getDripColor();

    public void visualTick(Level level) {
        if (this instanceof OrganicLatex)
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
                        ChangedParticles.LatexDripParticle.setNextColor(color);
                        clientLevel.addParticle(ChangedParticles.DRIPPING_LATEX, xo + dx * 1.2, yo + dh, zo + dz * 1.2, 0.0, 0.0, 0.0);
                    }
                }
            }
        });
    }

    public void effectTick(Level level, LivingEntity self) {}

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
}
