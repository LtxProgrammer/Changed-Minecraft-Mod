package net.ltxprogrammer.changed.entity.beast;

import com.google.common.collect.ImmutableMap;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Map;

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
        return LatexVariant.LIGHT_LATEX_WOLF.male();
    }

    @Override
    public LatexVariant<?> getSelfVariant() {
        return null;
    }

    @Override
    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.WHITE;
    }
}
