package net.ltxprogrammer.changed.entity.robot;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.ltxprogrammer.changed.entity.AccessoryEntities;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.util.Cacheable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public class Exoskeleton extends AbstractRobot implements WearableExoskeleton {
    protected static final UUID MECH_ATTACK_DAMAGE_UUID = UUID.fromString("bfed474a-d281-4102-9b5f-cd785026b1d5");
    protected static final UUID MECH_ATTACK_SPEED_UUID = UUID.fromString("8c461d33-f151-4c32-a2d7-e76593ce5a35");
    protected static final UUID MECH_MOVEMENT_SPEED_UUID = UUID.fromString("97790787-d3fe-47bd-90eb-86c63164f131");
    protected static final UUID MECH_ARMOR_UUID = UUID.fromString("40845805-4dde-4c45-8eb7-defe001f9035");
    protected static final UUID MECH_KNOCKBACK_UUID = UUID.fromString("494836c5-32c2-4b38-9ae3-261d295389e3");

    private static final Cacheable<Multimap<Attribute, AttributeModifier>> DEFAULT_MODIFIERS = Cacheable.of(() -> {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(MECH_ATTACK_DAMAGE_UUID, "Weapon modifier", 2, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(MECH_ATTACK_SPEED_UUID, "Weapon modifier", -1, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(MECH_MOVEMENT_SPEED_UUID, "Movement modifier", 0.2, AttributeModifier.Operation.MULTIPLY_TOTAL));
        builder.put(Attributes.ARMOR, new AttributeModifier(MECH_ARMOR_UUID, "Armor modifier", 20, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(MECH_KNOCKBACK_UUID, "Armor modifier", 2, AttributeModifier.Operation.MULTIPLY_BASE));
        return builder.build();
    });

    public Exoskeleton(EntityType<? extends Exoskeleton> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    public ItemLike getDropItem() {
        return ChangedItems.EXOSKELETON.get();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers() {
        return DEFAULT_MODIFIERS.get();
    }

    @Override
    public float getJumpStrengthMultiplier() {
        return 1.25f;
    }

    @Override
    public float getFallDamageMultiplier() {
        return 0.4f;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0f)
                .add(Attributes.ARMOR, 20.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.15D);
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        // Omitted
    }

    @Override
    public boolean isAffectedByWater() {
        return true;
    }

    @Override
    public SoundEvent getRunningSound() {
        return null;
    }

    @Override
    public int getRunningSoundDuration() {
        return 100;
    }

    @Override
    public float getMaxDamage() {
        return 40f;
    }

    @Override
    public ChargerType getChargerType() {
        return ChargerType.EXOSKELETON;
    }

    public Entity getWearerEntity() {
        return this.vehicle;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.getWearerEntity() == null && player.getFirstPassenger() == null && isWearerValid(player)) {
            this.startRiding(player);
            //player.startRiding(this);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        if (this.getWearerEntity() == null)
            return super.getDimensions(pose);
        else
            return SLEEPING_DIMENSIONS;
    }

    @Override
    public boolean startRiding(Entity entity, boolean flag) {
        if (super.startRiding(entity, flag)) {
            this.refreshDimensions();
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.getAttributes().addTransientAttributeModifiers(getAttributeModifiers());
            }

            return true;
        }

        return false;
    }

    @Override
    public void stopRiding() {
        this.refreshDimensions();
        if (this.getWearerEntity() instanceof LivingEntity livingEntity) {
            livingEntity.getAttributes().removeAttributeModifiers(getAttributeModifiers());
        }
        super.stopRiding();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWearerEntity() != null && !this.isWearerValid(this.getWearerEntity())) {
            this.stopRiding();
        }
    }

    public boolean isWearerValid(Entity wearer) {
        if (wearer instanceof LivingEntity livingEntity)
            return AccessoryEntities.getApparentEntityType(livingEntity).is(ChangedTags.EntityTypes.CAN_WEAR_EXOSKELETON);
        return false;
    }
}
