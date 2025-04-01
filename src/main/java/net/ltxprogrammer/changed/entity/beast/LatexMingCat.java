package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;

public class LatexMingCat extends ChangedEntity implements PatronOC {
    public LatexMingCat(EntityType<? extends LatexMingCat> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        AttributePresets.catLike(attributes);
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#d2a87f");
    }
}