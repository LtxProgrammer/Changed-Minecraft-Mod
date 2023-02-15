package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractDarkLatexEntity extends AbstractLatexWolf implements DarkLatexEntity {

    public AbstractDarkLatexEntity(EntityType<? extends AbstractLatexWolf> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public boolean isMaskless() {
        return false;
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.DARK_LATEX;
    }

    @Override
    public @NotNull ChangedParticles.Color3 getHairColor() {
        return ChangedParticles.Color3.DARK;
    }
}
