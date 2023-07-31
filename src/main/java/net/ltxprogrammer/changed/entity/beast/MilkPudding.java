package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

public class MilkPudding extends LatexEntity {
    public MilkPudding(EntityType<? extends MilkPudding> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public static void init() {
        SpawnPlacements.register(ChangedEntities.MILK_PUDDING.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, world, reason, pos, random) -> (world.getDifficulty() != Difficulty.PEACEFUL
                        && Monster.isDarkEnoughToSpawn(world, pos, random) && Mob.checkMobSpawnRules(entityType, world, reason, pos, random)));
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    @Override
    public LatexVariant<?> getTransfurVariant() {
        return LatexVariant.LIGHT_LATEX_WOLF.randomGender(random);
    }

    @Override
    public LatexVariant<?> getSelfVariant() {
        return null;
    }

    @Override
    public Color3 getDripColor() {
        return Color3.WHITE;
    }

    @Override
    public Color3 getHairColor(int layer) {
        return Color3.WHITE;
    }

    @Override
    public double getLatexMaxHealth() {
        return callIfNotNull(getSelfVariant(), variant -> variant.additionalHealth + 20.0, 8.0);
    }
}
