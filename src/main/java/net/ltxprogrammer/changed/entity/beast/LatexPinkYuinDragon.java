package net.ltxprogrammer.changed.entity.beast;


import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.PowderSnowWalkable;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;


public class LatexPinkYuinDragon extends LatexPinkWyvern implements PowderSnowWalkable {
    public LatexPinkYuinDragon(EntityType<? extends LatexPinkYuinDragon> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public Color3 getDripColor() {
        return Color3.getColor(this.random.nextInt(4) < 3 ? "#f2aaba" : "#d1626d");
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
    public Color3 getTransfurColor(TransfurCause cause) {
        if (cause == TransfurCause.PINK_PANTS)
            return Color3.WHITE;
        else
            return Color3.fromInt(0xf7aebe);
    }
}