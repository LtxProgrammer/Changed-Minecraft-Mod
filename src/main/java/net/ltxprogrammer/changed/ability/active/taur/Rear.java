package net.ltxprogrammer.changed.ability.active.taur;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.active.SimpleAbility;
import net.ltxprogrammer.changed.init.ChangedPoseExtensions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class Rear extends SimpleAbility {
    protected static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("db522621-37f3-4c9e-bc0c-d6e4c5458c30");
    protected static final UUID BLOCK_REACH_UUID = UUID.fromString("db522621-37f3-4c9e-bc0c-d6e4c5458c31");
    protected static final AttributeModifier MOVEMENT_SPEED = new AttributeModifier(MOVEMENT_SPEED_UUID, "Movement modifier", -0.8, AttributeModifier.Operation.MULTIPLY_TOTAL);
    protected static final AttributeModifier BLOCK_REACH = new AttributeModifier(BLOCK_REACH_UUID, "Reach modifier", 1.5, AttributeModifier.Operation.ADDITION);

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.HOLD;
    }

    @Override
    public int getChargeTime(IAbstractChangedEntity entity) {
        return 5;
    }

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return entity.getEntity().onGround() && entity.getChangedEntity().canEnterPose(ChangedPoseExtensions.REARING.get());
    }

    private static final Collection<Component> DESCRIPTION = Collections.singleton(Component.translatable("ability.changed.taur.rear.desc"));

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        super.startUsing(entity);

        entity.getChangedEntity().setPoseExtension(ChangedPoseExtensions.REARING.get());

        if (entity.getLevel().isClientSide())
            return;

        entity.getEntity().getAttributes().getInstance(Attributes.MOVEMENT_SPEED).addTransientModifier(MOVEMENT_SPEED);
        entity.getEntity().getAttributes().getInstance(ForgeMod.BLOCK_REACH.get()).addTransientModifier(BLOCK_REACH);
    }

    @Override
    public void stopUsing(IAbstractChangedEntity entity) {
        super.stopUsing(entity);

        entity.getChangedEntity().clearPoseExtension(ChangedPoseExtensions.REARING.get());

        if (entity.getLevel().isClientSide())
            return;

        entity.getEntity().getAttributes().getInstance(Attributes.MOVEMENT_SPEED).removeModifier(MOVEMENT_SPEED);
        entity.getEntity().getAttributes().getInstance(ForgeMod.BLOCK_REACH.get()).removeModifier(BLOCK_REACH);
    }
}