package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;

public class LatexMedusaCat extends ChangedEntity implements GenderedEntity {
    public LatexMedusaCat(EntityType<? extends LatexMedusaCat> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        AttributePresets.catLike(attributes);
    }

    @Override
    public Gender getGender() {
        return Gender.FEMALE;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }
}