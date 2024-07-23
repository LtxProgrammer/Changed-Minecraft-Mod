package net.ltxprogrammer.changed.entity.beast;


import net.ltxprogrammer.changed.entity.AttributePresets;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;


public class LatexPinkDeer extends LatexPinkWyvern {
    public LatexPinkDeer(EntityType<? extends LatexPinkDeer> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.075);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.95);
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
            return Color3.fromInt(0xd8bc99);
        else
            return Color3.fromInt(0xf7aebe);
    }
}