package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.GenderedLatexEntity;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber
public abstract class AbstractLatexShark extends GenderedLatexEntity implements AquaticEntity {
    protected final WaterBoundPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;

    @SubscribeEvent
    public static void canEntitySpawn(LivingSpawnEvent.CheckSpawn event) {
        if (event.getEntityLiving() instanceof AquaticEntity && event.getSpawnReason() == MobSpawnType.NATURAL)
            event.setResult(Event.Result.ALLOW);
    }

    public AbstractLatexShark(EntityType<? extends AbstractLatexShark> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        this.moveControl = new SharkMoveControl(this);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.waterNavigation = new WaterBoundPathNavigation(this, p_19871_);
        this.groundNavigation = new GroundPathNavigation(this, p_19871_);
        this.groundNavigation.setCanOpenDoors(true);
    }

    private static boolean isDeepEnoughToSpawn(LevelAccessor p_32367_, BlockPos p_32368_) {
        return p_32368_.getY() < p_32367_.getSeaLevel() - 5;
    }

    public static boolean checkAquaticLatexSpawnRules(EntityType<? extends LatexEntity> p_32350_, ServerLevelAccessor p_32351_, MobSpawnType p_32352_, BlockPos p_32353_, Random p_32354_) {
        if (!p_32351_.getFluidState(p_32353_.below()).is(FluidTags.WATER)) {
            return false;
        } else {
            Holder<Biome> holder = p_32351_.getBiome(p_32353_);
            boolean flag = p_32351_.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(p_32351_, p_32353_, p_32354_) && (p_32352_ == MobSpawnType.SPAWNER || p_32351_.getFluidState(p_32353_).is(FluidTags.WATER));
            if (!holder.is(Biomes.RIVER) && !holder.is(Biomes.FROZEN_RIVER)) {
                return p_32354_.nextInt(40) == 0 && isDeepEnoughToSpawn(p_32351_, p_32353_) && flag;
            } else {
                return p_32354_.nextInt(15) == 0 && flag;
            }
        }
    }

    @Override
    public int getTicksRequiredToFreeze() { return 100; }

    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.8);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(1.2);
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

    protected static final AttributeModifier SPEED_BOOST = new AttributeModifier(UUID.fromString("5c40eef3-ef3e-4d8d-9437-0da1925473d7"), "changed:trait_swim_speed", 1.2F, AttributeModifier.Operation.MULTIPLY_BASE);

    public void updateSwimming() {
        if (!this.level.isClientSide) {
            if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
                this.navigation = this.waterNavigation;
                this.setSwimming(true);
                this.maxUpStep = 1.0f;

                if (!this.getAttribute(ForgeMod.SWIM_SPEED.get()).hasModifier(SPEED_BOOST))
                    this.getAttribute(ForgeMod.SWIM_SPEED.get()).addPermanentModifier(SPEED_BOOST);
            } else {
                this.navigation = this.groundNavigation;
                this.setSwimming(false);
                this.maxUpStep = 0.7f;

                if (this.getAttribute(ForgeMod.SWIM_SPEED.get()).hasModifier(SPEED_BOOST))
                    this.getAttribute(ForgeMod.SWIM_SPEED.get()).removePermanentModifier(SPEED_BOOST.getId());
            }
        }
    }

    boolean wantsToSwim() {
        LivingEntity livingentity = this.getTarget();
        return livingentity != null && livingentity.isInWater();
    }

    static class SharkMoveControl extends MoveControl {
        private final AbstractLatexShark shark;

        public SharkMoveControl(AbstractLatexShark p_32433_) {
            super(p_32433_);
            this.shark = p_32433_;
        }

        public void tick() {
            shark.updateSwimming();

            LivingEntity livingentity = this.shark.getTarget();
            if (this.shark.isInWater()) {
                if (livingentity != null && livingentity.getY() > this.shark.getY()) {
                    double dx = livingentity.getX() - this.shark.getX();
                    double dz = livingentity.getZ() - this.shark.getZ();
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    this.shark.setDeltaMovement(this.shark.getDeltaMovement().add(dx / dist * 0.02D, 0.04D, dz / dist * 0.02D));
                }

                if (this.operation != MoveControl.Operation.MOVE_TO || this.shark.getNavigation().isDone()) {
                    this.shark.setSpeed(0.0F);
                    return;
                }

                double d0 = this.wantedX - this.shark.getX();
                double d1 = this.wantedY - this.shark.getY();
                double d2 = this.wantedZ - this.shark.getZ();
                double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                d1 /= d3;
                float f = (float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                this.shark.setYRot(this.rotlerp(this.shark.getYRot(), f, 90.0F));
                this.shark.yBodyRot = this.shark.getYRot();
                float f1 = (float)(this.speedModifier * this.shark.getAttributeValue(ForgeMod.SWIM_SPEED.get()));
                float f2 = Mth.lerp(0.125F, this.shark.getSpeed(), f1);
                this.shark.setSpeed(f2 * 1.25f);
                this.shark.setDeltaMovement(this.shark.getDeltaMovement().add((double)f2 * d0 * 0.005D, (double)f2 * d1 * 0.1D, (double)f2 * d2 * 0.005D));
            } else {
                /*this.shark.setSpeed((float)this.shark.getAttributeValue(Attributes.MOVEMENT_SPEED));
                if (!this.shark.onGround) {
                    this.shark.setDeltaMovement(this.shark.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
                }*/
                if (this.strafeForwards > 0.5f)
                    super.tick();
                else
                    super.tick();
            }
        }
    }
}
