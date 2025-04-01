package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

public class LatexBenignWolf extends AbstractLatexWolf {
    public LatexBenignWolf(EntityType<? extends LatexBenignWolf> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.FOLLOW_RANGE).setBaseValue(4.0);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.15);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.2);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#282828");
    }
}