package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Beifeng extends AbstractCaveEntity implements DarkLatexEntity, PatronOC {
    public Beifeng(EntityType<? extends Beifeng> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.05);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(1.0);
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public TransfurMode getTransfurMode() { return TransfurMode.NONE; }

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

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#51659d");
    }
}
