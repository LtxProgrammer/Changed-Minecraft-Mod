package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;

public class DarkDragon extends ChangedEntity implements DarkLatexEntity, PatronOC, WingedEntity {
    public DarkDragon(EntityType<? extends ChangedEntity> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public boolean isMaskless() {
        return true;
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        AttributePresets.dragonLike(attributes);
    }

    @Override
    public LatexType getLatexType() {
        return ChangedLatexTypes.DARK_LATEX.get();
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    @Override
    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.DARK;
    }

    @Override
    public WingDesign getWingDesign() {
        return WingDesign.WEBBED_DARK;
    }
}
