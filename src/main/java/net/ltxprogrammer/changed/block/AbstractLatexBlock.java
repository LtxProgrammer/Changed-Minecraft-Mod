package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.LatexTypeOld;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.entity.latex.SpreadingLatexType;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Cacheable;
import net.ltxprogrammer.changed.world.LatexCoverGetter;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractLatexBlock extends Block implements LatexCoveringSource {
    private final Supplier<? extends SpreadingLatexType> latexType;
    private final Supplier<? extends Item> goo;
    private final Cacheable<LatexCoverState> simulatedCoverState;

    public static LatexType getSurfaceType(LevelReader level, BlockPos blockPos, Direction face, SupportType supportType) {
        final LatexCoverState coverState = LatexCoverState.getAt(level, blockPos);
        final BlockState otherBlockState = level.getBlockState(blockPos.relative(face));
        if (!otherBlockState.isFaceSturdy(level, blockPos.relative(face), face.getOpposite(), supportType))
            return ChangedLatexTypes.NONE.get();
        if (!coverState.isAir())
            return coverState.getType();
        if (otherBlockState.getBlock() instanceof AbstractLatexBlock block)
            return block.latexType.get();
        return ChangedLatexTypes.NONE.get();
    }

    public static LatexType getSurfaceType(LevelReader level, BlockPos blockPos, Direction face) {
        return getSurfaceType(level, blockPos, face, SupportType.FULL);
    }

    public static LatexType getSurfaceType(LatexCoverGetter level, BlockPos blockPos, Direction face, SupportType supportType) {
        final LatexCoverState coverState = level.getLatexCover(blockPos);
        final BlockState otherBlockState = level.getBlockState(blockPos.relative(face));
        if (!otherBlockState.isFaceSturdy(level, blockPos.relative(face), face.getOpposite(), supportType))
            return ChangedLatexTypes.NONE.get();
        if (!coverState.isAir())
            return coverState.getType();
        if (otherBlockState.getBlock() instanceof AbstractLatexBlock block)
            return block.latexType.get();
        return ChangedLatexTypes.NONE.get();
    }

    public static LatexType getSurfaceType(LatexCoverGetter level, BlockPos blockPos, Direction face) {
        return getSurfaceType(level, blockPos, face, SupportType.FULL);
    }

    public static boolean isSurfaceOfType(LevelReader level, BlockPos blockPos, Direction face, LatexType type) {
        return getSurfaceType(level, blockPos, face, SupportType.FULL) == type;
    }

    public static boolean isSurfaceOfType(LevelReader level, BlockPos blockPos, Direction face, SupportType supportType, LatexType type) {
        return getSurfaceType(level, blockPos, face, supportType) == type;
    }

    public static boolean isSurfaceOfType(LatexCoverGetter level, BlockPos blockPos, Direction face, LatexType type) {
        return getSurfaceType(level, blockPos, face, SupportType.FULL) == type;
    }

    public static boolean isSurfaceOfType(LatexCoverGetter level, BlockPos blockPos, Direction face, SupportType supportType, LatexType type) {
        return getSurfaceType(level, blockPos, face, supportType) == type;
    }

    public AbstractLatexBlock(Properties properties, Supplier<? extends SpreadingLatexType> latexType, Supplier<? extends Item> goo) {
        super(properties);
        this.latexType = latexType;
        this.goo = goo;
        this.simulatedCoverState = Cacheable.of(() -> latexType.get().sourceCoverState().setValue(SpreadingLatexType.DOWN, true));
    }

    public static boolean tryCover(Level level, BlockPos relative, LatexType type) {
        if (!(type instanceof SpreadingLatexType spreadingLatexType))
            return false;
        LatexCoverState originalCover = LatexCoverState.getAt(level, relative);
        if (!originalCover.isAir())
            return false;
        /*if (spreadingLatexType.shouldDecay(type.defaultCoverState(), level, relative))
            return false;*/
        BlockState old = level.getBlockState(relative);
        if (old.is(ChangedTags.Blocks.DENY_LATEX_COVER) || old.isCollisionShapeFullBlock(level, relative))
            return false;

        var event = new SpreadingLatexType.CoveringBlockEvent(spreadingLatexType, old, old,
                spreadingLatexType.spreadState(level, relative, spreadingLatexType.sourceCoverState()), relative, level);
        spreadingLatexType.defaultCoverBehavior(event);
        if (Changed.postModEvent(event))
            return false;
        if (event.originalState == event.getPlannedState() && event.plannedCoverState == originalCover)
            return false;
        /*if (!Changed.config.server.canBlockBeCovered(event.plannedState.getBlock()))
            return InteractionResult.FAIL;*/

        level.setBlockAndUpdate(event.blockPos, event.getPlannedState());
        LatexCoverState.setAtAndUpdate(level, event.blockPos, event.plannedCoverState);

        event.getPostProcess().accept(level, event.blockPos);
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable) {
        return false;
    }

    @Override
    public void fallOn(Level level, BlockState blockState, BlockPos blockPos, Entity entity, float p_152430_) {
        if (latexType.get().stepOn(level, blockPos.above(), simulatedCoverState.getOrThrow(), blockPos, blockState, entity))
            return;

        super.stepOn(level, blockPos, blockState, entity);
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
        if (latexType.get().updateEntityAfterFallOn(LatexCoverGetter.extendDefault(level), this, simulatedCoverState.getOrThrow(), entity))
            return;

        super.updateEntityAfterFallOn(level, entity);
    }

    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        if (latexType.get().stepOn(level, blockPos.above(), simulatedCoverState.getOrThrow(), blockPos, blockState, entity))
            return;

        super.stepOn(level, blockPos, blockState, entity);
    }

    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, builder.getParameter(LootContextParams.TOOL)) > 0)
            return List.of(new ItemStack(this));
        return List.of(goo.get().getDefaultInstance(), goo.get().getDefaultInstance(), goo.get().getDefaultInstance());
    }
}
