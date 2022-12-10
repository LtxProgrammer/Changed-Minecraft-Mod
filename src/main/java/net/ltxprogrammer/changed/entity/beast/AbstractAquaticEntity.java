package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class AbstractAquaticEntity extends LatexEntity implements AquaticEntity {
    protected final WaterBoundPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;

    public AbstractAquaticEntity(EntityType<? extends AbstractAquaticEntity> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        this.moveControl = new AbstractAquaticEntity.AquaticMoveControl(this);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.waterNavigation = new WaterBoundPathNavigation(this, p_19871_);
        this.groundNavigation = new GroundPathNavigation(this, p_19871_);
        this.groundNavigation.setCanOpenDoors(true);
    }

    @Override
    public int getTicksRequiredToFreeze() { return 100; }

    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.8);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(1.15);
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public TransfurMode getTransfurMode() { return TransfurMode.REPLICATION; }

    @Override
    public MobType getMobType() {
        return MobType.UNDEFINED;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.hurt"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.death"));
    }

    @Override
    public boolean canBreatheUnderwater() { return true; }

    @Override
    public void readAdditionalSaveData(CompoundTag p_20052_) {
        super.readAdditionalSaveData(p_20052_);
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return super.getArmorSlots();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot p_21127_) {
        return super.getItemBySlot(p_21127_);
    }

    @Override
    public void setItemSlot(@NotNull EquipmentSlot p_21036_, @NotNull ItemStack p_21037_) {
        super.setItemSlot(p_21036_, p_21037_);
    }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag p_20139_) {
        super.addAdditionalSaveData(p_20139_);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new RandomSwimmingGoal(this, 0.4D, 10));
    }

    @Override
    public ChangedParticles.Color3 getDripColor() {
        return level.random.nextInt(10) > 3 ? ChangedParticles.Color3.GRAY : ChangedParticles.Color3.WHITE;
    }

    public void travel(@NotNull Vec3 p_32394_) {
        if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
            this.moveRelative(0.01F, p_32394_);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(p_32394_);
        }

    }

    public void updateSwimming() {
        if (!this.level.isClientSide) {
            if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
                this.navigation = this.waterNavigation;
                this.setSwimming(true);
                this.setPose(Pose.SWIMMING);
                this.maxUpStep = 1.0f;
            } else {
                this.navigation = this.groundNavigation;
                this.setSwimming(false);
                this.setPose(Pose.STANDING);
                this.maxUpStep = 0.7f;
            }
        }
    }

    boolean wantsToSwim() {
        LivingEntity livingentity = this.getTarget();
        return livingentity != null && livingentity.isInWater();
    }

    static class AquaticMoveControl extends MoveControl {
        private final AbstractAquaticEntity aquaticEntity;

        public AquaticMoveControl(AbstractAquaticEntity p_32433_) {
            super(p_32433_);
            this.aquaticEntity = p_32433_;
        }

        public void tick() {
            aquaticEntity.updateSwimming();

            LivingEntity livingentity = this.aquaticEntity.getTarget();
            if (this.aquaticEntity.isInWater()) {
                if (livingentity != null && livingentity.getY() > this.aquaticEntity.getY()) {
                    double dx = livingentity.getX() - this.aquaticEntity.getX();
                    double dz = livingentity.getZ() - this.aquaticEntity.getZ();
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    this.aquaticEntity.setDeltaMovement(this.aquaticEntity.getDeltaMovement().add(dx / dist * 0.02D, 0.04D, dz / dist * 0.02D));
                }

                if (this.operation != MoveControl.Operation.MOVE_TO || this.aquaticEntity.getNavigation().isDone()) {
                    this.aquaticEntity.setSpeed(0.0F);
                    return;
                }

                double d0 = this.wantedX - this.aquaticEntity.getX();
                double d1 = this.wantedY - this.aquaticEntity.getY();
                double d2 = this.wantedZ - this.aquaticEntity.getZ();
                double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                d1 /= d3;
                float f = (float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                this.aquaticEntity.setYRot(this.rotlerp(this.aquaticEntity.getYRot(), f, 90.0F));
                this.aquaticEntity.yBodyRot = this.aquaticEntity.getYRot();
                float f1 = (float)(this.speedModifier * this.aquaticEntity.getAttributeValue(ForgeMod.SWIM_SPEED.get()));
                float f2 = Mth.lerp(0.125F, this.aquaticEntity.getSpeed(), f1);
                this.aquaticEntity.setSpeed(f2 * 1.05f);
                this.aquaticEntity.setDeltaMovement(this.aquaticEntity.getDeltaMovement().add((double)f2 * d0 * 0.005D, (double)f2 * d1 * 0.1D, (double)f2 * d2 * 0.005D));
            } else {
                super.tick();
            }
        }
    }
}
