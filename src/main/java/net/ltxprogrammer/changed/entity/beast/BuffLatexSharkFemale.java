package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.Gender;
import net.ltxprogrammer.changed.entity.GenderedEntity;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

public class BuffLatexSharkFemale extends AbstractLatexShark implements GenderedEntity {
    public BuffLatexSharkFemale(EntityType<? extends BuffLatexSharkFemale> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.9);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(1.5);
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(28);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    @Override
    public Gender getGender() {
        return Gender.FEMALE;
    }
}
