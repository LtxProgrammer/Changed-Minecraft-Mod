package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.GooType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WhiteGooKnight extends AbstractGooWolf {
    public WhiteGooKnight(EntityType<? extends WhiteGooKnight> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public GooType getGooType() {
        return GooType.NEUTRAL;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    @Override
    public Color3 getDripColor() {
        return Color3.WHITE;
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.BALD.get();
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return HairStyle.Collection.MALE.getStyles();
    }

    @Override
    public Color3 getHairColor(int layer) {
        return Color3.WHITE;
    }

    @Override
    public TransfurVariant<?> getTransfurVariant() {
        return TransfurVariant.LIGHT_LATEX_CENTAUR;
    }
}
