package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.GenderedLatexEntity;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber
public abstract class AbstractLatexShark extends AbstractAquaticGenderedEntity {
    @SubscribeEvent
    public static void canEntitySpawn(LivingSpawnEvent.CheckSpawn event) {
        if (event.getEntityLiving() instanceof AquaticEntity && event.getSpawnReason() == MobSpawnType.NATURAL)
            event.setResult(Event.Result.ALLOW);
    }

    public AbstractLatexShark(EntityType<? extends AbstractLatexShark> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    private static boolean isDeepEnoughToSpawn(LevelAccessor p_32367_, BlockPos p_32368_) {
        return p_32368_.getY() < p_32367_.getSeaLevel() - 5;
    }

    public static boolean checkAquaticLatexSpawnRules(EntityType<? extends LatexEntity> p_32350_, ServerLevelAccessor p_32351_, MobSpawnType p_32352_, BlockPos p_32353_, Random p_32354_) {
        if (!p_32351_.getFluidState(p_32353_.below()).is(FluidTags.WATER)) {
            return false;
        } else {
            Holder<Biome> holder = p_32351_.getBiome(p_32353_);
            boolean flag = p_32351_.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(p_32351_, p_32353_, p_32354_) && (p_32352_ == MobSpawnType.SPAWNER || p_32351_.getFluidState(p_32353_).is(FluidTags.WATER));
            if (!holder.is(Biomes.RIVER) && !holder.is(Biomes.FROZEN_RIVER)) {
                return p_32354_.nextInt(40) == 0 && isDeepEnoughToSpawn(p_32351_, p_32353_) && flag;
            } else {
                return p_32354_.nextInt(15) == 0 && flag;
            }
        }
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public ChangedParticles.Color3 getDripColor() {
        return level.random.nextInt(10) > 3 ? ChangedParticles.Color3.GRAY : ChangedParticles.Color3.WHITE;
    }
}
