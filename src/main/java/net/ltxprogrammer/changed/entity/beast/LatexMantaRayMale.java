package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;

public class LatexMantaRayMale extends AbstractLatexMantaRay {
    public LatexMantaRayMale(EntityType<? extends LatexMantaRayMale> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public Gender getGender() {
        return Gender.MALE;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#6f7696");
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        AttributePresets.sharkLike(attributes);
    }
}
