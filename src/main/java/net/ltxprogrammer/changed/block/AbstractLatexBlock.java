package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.item.AbstractLatexGoo;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public abstract class AbstractLatexBlock extends Block implements NonLatexCoverableBlock {
    public static final EnumProperty<LatexType> COVERED = EnumProperty.create("covered_with", LatexType.class, LatexType.values());

    private final LatexType latexType;
    private final Supplier<? extends Item> goo;

    public static boolean isLatexed(BlockState blockState) {
        return getLatexed(blockState) != LatexType.NEUTRAL;
    }

    public static LatexType getLatexed(BlockState blockState) {
        if (blockState.getProperties().contains(COVERED))
            return blockState.getValue(COVERED);
        if (blockState.getBlock() instanceof AbstractLatexBlock block)
            return block.latexType;
        return LatexType.NEUTRAL;
    }


    public AbstractLatexBlock(Properties p_49795_, LatexType latexType, Supplier<? extends Item> goo) {
        super(p_49795_.randomTicks().dynamicShape());
        this.latexType = latexType;
        this.goo = goo;
    }

    public static boolean tryCover(Level level, BlockPos relative, LatexType type) {
        BlockState old = level.getBlockState(relative);
        if (!old.getProperties().contains(COVERED))
            return false;
        level.setBlockAndUpdate(relative, old.setValue(COVERED, type));
        return true;
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
            LatexVariantInstance<?> variant = ProcessTransfur.getPlayerLatexVariant(player);
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
    public List<ItemStack> getDrops(BlockState blockState, LootContext.Builder builder) {
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, builder.getParameter(LootContextParams.TOOL)) > 0)
            return List.of(new ItemStack(ChangedItems.getBlockItem(this)));
        return List.of(goo.get().getDefaultInstance(), goo.get().getDefaultInstance(), goo.get().getDefaultInstance());
    }

    private static boolean isPassThroughBlock(Level level, BlockState state, BlockPos pos) {
        return state.isAir() || !state.isCollisionShapeFullBlock(level, pos);
    }

    private static boolean isValidSurface(Level level, BlockPos toCover, BlockPos neighbor, Direction coverNormal) {
        BlockState state = level.getBlockState(neighbor);
        return isPassThroughBlock(level, state, neighbor);
    }

    // Note: see BlockMixin.java and BlockBehaviourMixin.java for context
    public static void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos position, @NotNull Random random, LatexType latexType) {
        if (level.getGameRules().getInt(ChangedGameRules.RULE_LATEX_GROWTH_RATE) == 0) return;
        if (!level.isAreaLoaded(position, 3)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
        if (random.nextInt(10 * level.getGameRules().getInt(ChangedGameRules.RULE_LATEX_GROWTH_RATE)) < 600) return;

        Direction checkDir = Direction.getRandom(random);
        BlockPos.MutableBlockPos checkPos = position.relative(checkDir).mutable();

        if (checkDir.getAxis() == Direction.Axis.Y && level.getBlockState(checkPos).isAir()) {
            checkDir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            checkPos.set(checkPos.relative(checkDir));
        }

        BlockState checkState = level.getBlockState(checkPos);

        if (!checkState.is(ChangedTags.Blocks.LATEX_NON_REPLACEABLE) && checkState.getProperties().contains(COVERED) &&
                checkState.getValue(COVERED) != latexType) {
            if (checkPos.subtract(position).getY() > 0 && random.nextInt(3) > 0) // Reduced chance of spreading up
                return;

            if (Arrays.stream(Direction.values()).noneMatch(direction -> isValidSurface(level, checkPos, checkPos.relative(direction), direction)))
                return;

            var event = new AbstractLatexGoo.CoveringBlockEvent(latexType, checkState, checkPos, level);
            if (MinecraftForge.EVENT_BUS.post(event))
                return;
            if (event.originalState == event.plannedState)
                return;
            level.setBlockAndUpdate(event.blockPos, event.plannedState);
        }
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos position, @NotNull Random random) {
        super.randomTick(state, level, position, random);

        randomTick(state, level, position, random, latexType);
        latexTick(state, level, position, random);
    }

    public void latexTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos position, @NotNull Random random) {}
}
