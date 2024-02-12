package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.GenderedEntity;
import net.ltxprogrammer.changed.entity.GooType;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class AbstractBlackGooWolf extends AbstractBlackGooEntity implements GenderedEntity {
    public AbstractBlackGooWolf(EntityType<? extends AbstractBlackGooWolf> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public int getTicksRequiredToFreeze() { return 240; }

    @Override
    public GooType getGooType() {
        return GooType.BLACK_GOO;
    }

    @Override
    public Color3 getDripColor() {
        return Color3.DARK;
    }

    @Override
    public boolean isMaskless() {
        return false;
    }
}
