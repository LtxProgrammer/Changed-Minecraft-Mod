package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.PatronOC;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LatexBeifeng extends AbstractLatexWolf implements DarkLatexEntity, PatronOC {
    public LatexBeifeng(EntityType<? extends LatexBeifeng> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public Color3 getDripColor() {
        return level.random.nextInt(10) > 3 ? Color3.BLUE : Color3.WHITE;
    }

    @Override
    public boolean isMaskless() {
        return true;
    }

    @Override
    public Color3 getHairColor(int layer) {
        return Color3.fromInt(0xffe852);
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return HairStyle.Collection.getAll();
    }
}
