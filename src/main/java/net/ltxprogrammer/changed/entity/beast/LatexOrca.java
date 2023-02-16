package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class LatexOrca extends AbstractAquaticEntity {
    public LatexOrca(EntityType<? extends LatexOrca> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public ChangedParticles.Color3 getDripColor() {
        return level.random.nextInt(10) > 3 ? ChangedParticles.Color3.DARK : ChangedParticles.Color3.WHITE;
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.MALE_SHORT_FRONT;
    }

    @Override
    public ChangedParticles.Color3 getHairColor() {
        return ChangedParticles.Color3.DARK;
    }
}
