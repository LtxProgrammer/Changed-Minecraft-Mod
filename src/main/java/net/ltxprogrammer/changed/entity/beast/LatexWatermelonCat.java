package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;

public class LatexWatermelonCat extends ChangedEntity implements GenderedEntity, PatronOC {
    public LatexWatermelonCat(EntityType<? extends LatexWatermelonCat> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        AttributePresets.catLike(attributes);
    }

    @Override
    public int getTicksRequiredToFreeze() { return 160; }

    @Override
    public Gender getGender() {
        return Gender.FEMALE;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#545254");
    }
}
