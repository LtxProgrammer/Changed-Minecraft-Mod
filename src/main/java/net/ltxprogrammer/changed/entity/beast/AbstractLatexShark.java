package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.AttributePresets;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;

public abstract class AbstractLatexShark extends AbstractAquaticEntity {
    public AbstractLatexShark(EntityType<? extends AbstractLatexShark> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        AttributePresets.sharkLike(attributes);
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.GRAY;
    }
}
