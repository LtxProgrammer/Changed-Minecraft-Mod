package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class LatexFennecFox extends AbstractLatexWolf {
    public LatexFennecFox(EntityType<? extends LatexFennecFox> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    @Override
    public Color3 getDripColor() {
        return Color3.getColor("#ffe195");
    }

    @Override
    public Color3 getHairColor(int layer) {
        return Color3.fromInt(0xffe195);
    }
}
