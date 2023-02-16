package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.Gender;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class LatexSnowLeopardMale extends AbstractSnowLeopard {
    public LatexSnowLeopardMale(EntityType<? extends LatexSnowLeopardMale> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.2f);
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.MOHAWK;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    @Override
    public ChangedParticles.Color3 getHairColor() {
        return ChangedParticles.Color3.WHITE;
    }

    @Override
    public Gender getGender() {
        return Gender.MALE;
    }
}
