package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

public class LatexOrca extends AbstractAquaticEntity {
    public LatexOrca(EntityType<? extends LatexOrca> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.875);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(1.48);
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#393939");
    }
}
