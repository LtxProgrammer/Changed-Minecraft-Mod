package net.ltxprogrammer.changed.entity.beast;


import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class LatexRedDragon extends LatexEntity implements PatronOC {
    public LatexRedDragon(EntityType<? extends LatexRedDragon> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }


    @Override
    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.getColor("#a54f58");
    }

    @Override
    public int getTicksRequiredToFreeze() {  return 140; }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    @Override
    public ChangedParticles.Color3 getHairColor(int layer) {
        return ChangedParticles.Color3.WHITE;
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return HairStyle.Collection.EMPTY;
    }
}