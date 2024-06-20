package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GreenLizard extends ChangedEntity {
    public GreenLizard(EntityType<? extends GreenLizard> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    @Override
    public Color3 getDripColor() {
        return Color3.getColor("#b3e53a");
    }

    @Override
    public Color3 getHairColor(int layer) {
        return Color3.BROWN;
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return HairStyle.Collection.EMPTY;
    }
}
