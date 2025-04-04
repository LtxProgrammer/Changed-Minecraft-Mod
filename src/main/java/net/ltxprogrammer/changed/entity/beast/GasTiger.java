package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

public class GasTiger extends ChangedEntity implements PowderSnowWalkable {
    public GasTiger(EntityType<? extends GasTiger> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.15);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(1.1);
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(22.0);
    }

    @Override
    public int getTicksRequiredToFreeze() { return 360; }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.NONE;
    }
}
