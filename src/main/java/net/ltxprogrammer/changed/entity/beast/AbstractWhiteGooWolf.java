package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.GenderedEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.GooType;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class AbstractWhiteGooWolf extends ChangedEntity implements GenderedEntity {
    public AbstractWhiteGooWolf(EntityType<? extends AbstractWhiteGooWolf> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public int getTicksRequiredToFreeze() { return 240; }

    @Override
    public GooType getGooType() {
        return GooType.NEUTRAL;
    }

    @Override
    public Color3 getDripColor() {
        return Color3.WHITE;
    }

    @Override
    public Color3 getHairColor(int layer) {
        return Color3.WHITE;
    }
}
