package net.ltxprogrammer.changed.entity.beast.boss;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

public class BehemothHead extends Behemoth {
    public BehemothHead(EntityType<? extends BehemothHead> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    protected void setAttributes(AttributeMap attributes) {
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(100.0);
        attributes.getInstance(Attributes.FOLLOW_RANGE).setBaseValue(24.0);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.0);
        attributes.getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(3.0);
        attributes.getInstance(Attributes.ARMOR).setBaseValue(2.0);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    public void onAddedToWorld() {
        super.onAddedToWorld();
        var leftHand = ChangedEntities.BEHEMOTH_HAND_LEFT.get().create(level).setHead(this);
        leftHand.moveTo(this.position());
        var rightHand = ChangedEntities.BEHEMOTH_HAND_RIGHT.get().create(level).setHead(this);
        rightHand.moveTo(this.position());
        level.addFreshEntity(leftHand);
        level.addFreshEntity(rightHand);
    }
}
