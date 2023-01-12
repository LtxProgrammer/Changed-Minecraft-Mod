package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public abstract class AbstractLatexBlock extends Block implements NonLatexCoverableBlock {
    public static final EnumProperty<LatexType> COVERED = EnumProperty.create("covered_with", LatexType.class, LatexType.values());

    private final LatexType latexType;
    private final Supplier<Item> goo;

    public AbstractLatexBlock(Properties p_49795_, LatexType latexType, Supplier<Item> goo) {
        super(p_49795_.randomTicks().dynamicShape());
        this.latexType = latexType;
        this.goo = goo;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    private static final float FACTION_BENEFIT = 1.1F;
    private static final float FACTION_HINDER = 0.5F;
    private static final float NEUTRAL_HINDER = 0.75F;

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable) {
        if (latexType != LatexType.DARK_LATEX) return false;

        BlockState plant = plantable.getPlant(world, pos.relative(facing));
        if (plant.getBlock() instanceof AbstractLatexCrystal)
            return true;
        else
            return false;
    }

    public static void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity, LatexType latexType) {
        LatexEntity latexEntity = null;

        if (entity instanceof LatexEntity) latexEntity = (LatexEntity)entity;

        if (entity instanceof Player player) {
            LatexVariant<?> variant = ProcessTransfur.getPlayerLatexVariant(player);
            if (variant != null)
                latexEntity = variant.getLatexEntity();
        }

        if (latexEntity != null) {
            LatexType type = latexEntity.getLatexType();
            if (type == latexType) {
                if (!entity.isInWater())
                    multiplyMotion(entity, FACTION_BENEFIT);
            }

            else if (type != LatexType.NEUTRAL) {
                multiplyMotion(entity, FACTION_HINDER);
            }

            else {
                multiplyMotion(entity, NEUTRAL_HINDER);
            }
        }

        else if (entity instanceof LivingEntity) {
            multiplyMotion(entity, NEUTRAL_HINDER);
        }
    }

    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        stepOn(level, blockPos, blockState, entity, latexType);
    }

    private static void multiplyMotion(Entity entity, float mul) {
        entity.setDeltaMovement(entity.getDeltaMovement().multiply(mul, mul, mul));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return List.of(goo.get().getDefaultInstance(), goo.get().getDefaultInstance(), goo.get().getDefaultInstance());
    }

    private static boolean checkBlock(BlockGetter level, BlockPos pos) {
        return !level.getBlockState(pos).isRedstoneConductor(level, pos);
    }

    // Note: see BlockMixin.java and BlockBehaviourMixin.java for context
    public static void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos position, @NotNull Random random, LatexType latexType) {
        if (!level.isAreaLoaded(position, 3)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
        if (!level.getGameRules().getBoolean(ChangedGameRules.RULE_LATEX_SPREAD)) return; // TODO adjust to spread like it covers (not underneath blocks)
        if (random.nextInt(10) < 6) return;

        boolean spread = checkBlock(level, position.above());
        if (!spread && checkBlock(level, position.above().east()))
            spread = true;
        if (!spread && checkBlock(level, position.above().west()))
            spread = true;
        if (!spread && checkBlock(level, position.above().north()))
            spread = true;
        if (!spread && checkBlock(level, position.above().south()))
            spread = true;
        if (!spread && checkBlock(level, position.below()))
            spread = true;
        if (!spread && checkBlock(level, position.below().east()))
            spread = true;
        if (!spread && checkBlock(level, position.below().west()))
            spread = true;
        if (!spread && checkBlock(level, position.below().north()))
            spread = true;
        if (!spread && checkBlock(level, position.below().south()))
            spread = true;
        if (!spread && checkBlock(level, position.east()))
            spread = true;
        if (!spread && checkBlock(level, position.west()))
            spread = true;
        if (!spread && checkBlock(level, position.east().north()))
            spread = true;
        if (!spread && checkBlock(level, position.west().north()))
            spread = true;
        if (!spread && checkBlock(level, position.east().south()))
            spread = true;
        if (!spread && checkBlock(level, position.west().south()))
            spread = true;
        if (!spread && checkBlock(level, position.north()))
            spread = true;
        if (!spread && checkBlock(level, position.south()))
            spread = true;

        if (!spread) return;

        Direction checkDir = Direction.getRandom(random);
        BlockPos checkPos = position.relative(checkDir);
        BlockState checkState = level.getBlockState(checkPos);
        if (!checkState.is(ChangedTags.Blocks.LATEX_NON_REPLACEABLE) && checkState.getProperties().contains(COVERED) &&
                checkState.getValue(COVERED) == LatexType.NEUTRAL) {
            if (checkDir == Direction.UP && random.nextInt(3) > 0) // Reduced chance of spreading up
                return;

            for (var tag : checkState.getTags().toList()) {
                if (tag.location().equals(new ResourceLocation("minecraft:flowers"))) {
                    level.setBlockAndUpdate(checkPos, Blocks.DEAD_BUSH.defaultBlockState().setValue(COVERED, latexType));
                    return;
                }
            }


            if (checkState.is(Blocks.GRASS)) {
                level.setBlockAndUpdate(checkPos, Blocks.DEAD_BUSH.defaultBlockState().setValue(COVERED, latexType));
                return;
            }

            else if (checkState.is(Blocks.GRASS_BLOCK)) {
                level.setBlockAndUpdate(checkPos, Blocks.DIRT.defaultBlockState().setValue(COVERED, latexType));
                return;
            }

            else {
                level.setBlockAndUpdate(checkPos, checkState.setValue(COVERED, latexType));
                return;
            }
        }
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos position, @NotNull Random random) {
        super.randomTick(state, level, position, random);

        randomTick(state, level, position, random, latexType);
    }
}
