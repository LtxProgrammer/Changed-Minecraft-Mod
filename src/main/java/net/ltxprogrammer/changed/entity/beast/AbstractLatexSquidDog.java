package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.GenderedEntity;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

public abstract class AbstractLatexSquidDog extends AbstractAquaticEntity implements GenderedEntity {
    public AbstractLatexSquidDog(EntityType<? extends AbstractLatexSquidDog> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public Color3 getTransfurColor(TransfurCause cause) {
        if (cause == TransfurCause.SQUID_DOG_INKBALL)
            return Color3.fromInt(0x101010);
        else
            return Color3.WHITE;
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.925);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(1.3);
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(30.0);
    }
}
