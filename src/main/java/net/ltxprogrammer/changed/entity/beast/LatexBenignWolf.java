package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.robot.Exoskeleton;
import net.ltxprogrammer.changed.init.ChangedAttributes;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

public class LatexBenignWolf extends AbstractLatexWolf {
    private boolean hasExoLast = false;

    public LatexBenignWolf(EntityType<? extends LatexBenignWolf> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.FOLLOW_RANGE).setBaseValue(4.0);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.15);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.2);
        attributes.getInstance(ChangedAttributes.JUMP_STRENGTH.get()).setBaseValue(0.5);
        attributes.getInstance(ChangedAttributes.MINING_SPEED.get()).setBaseValue(0.5);
    }

    @Override
    public void variantTick(Level level) {
        super.variantTick(level);

        boolean hasExo = Exoskeleton.getEntityExoskeleton(this.maybeGetUnderlying()).isPresent();
        if (hasExoLast != hasExo) {
            var attributes = this.getAttributes();

            if (hasExo) {
                attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.075);
                attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.95);
                attributes.getInstance(ChangedAttributes.JUMP_STRENGTH.get()).setBaseValue(1.0);
                attributes.getInstance(ChangedAttributes.MINING_SPEED.get()).setBaseValue(1.0);
            }

            else {
                attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.15);
                attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.2);
                attributes.getInstance(ChangedAttributes.JUMP_STRENGTH.get()).setBaseValue(0.5);
                attributes.getInstance(ChangedAttributes.MINING_SPEED.get()).setBaseValue(0.5);
            }

            hasExoLast = hasExo;

            var instance = IAbstractChangedEntity.forEitherSafe(this.maybeGetUnderlying()).map(IAbstractChangedEntity::getTransfurVariantInstance).orElse(null);
            if (instance != null) {
                instance.visionType = hasExo ? VisionType.NORMAL : VisionType.BLIND;
                instance.itemUseMode = hasExo ? UseItemMode.NORMAL : UseItemMode.NONE;

                instance.refreshAttributes();
            }
        }
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#282828");
    }
}