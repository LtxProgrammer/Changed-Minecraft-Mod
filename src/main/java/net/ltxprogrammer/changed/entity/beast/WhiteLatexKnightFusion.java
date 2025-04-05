package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

public class WhiteLatexKnightFusion extends WhiteLatexKnight {
    public WhiteLatexKnightFusion(EntityType<? extends WhiteLatexKnightFusion> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(28.0);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.075f);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.95);
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    @Override
    public TransfurVariant<?> getTransfurVariant() {
        return ChangedTransfurVariants.WHITE_LATEX_KNIGHT_FUSION.get();
    }
}