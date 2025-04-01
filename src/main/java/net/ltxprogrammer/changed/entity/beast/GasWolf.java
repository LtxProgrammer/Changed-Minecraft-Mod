package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.AttributePresets;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;

public class GasWolf extends AbstractLatexWolf {
    public GasWolf(EntityType<? extends GasWolf> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        AttributePresets.wolfLike(attributes);
    }
}
