package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LatexWallSplotch extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public final LatexType type;
    public final List<LatexVariant<?>> variants;

    private static final VoxelShape AABB = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);

    public LatexWallSplotch(LatexType type, List<LatexVariant<?>> variants) {
        super(BlockBehaviour.Properties.of(Material.CLAY, MaterialColor.COLOR_GRAY).sound(SoundType.SLIME_BLOCK).strength(1.0F, 4.0F).noOcclusion());
        this.type = type;
        this.variants = variants;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (variants.isEmpty()) return;
        if (!level.isClientSide && entity instanceof LivingEntity livingEntity) {
            if (ProcessTransfur.progressTransfur(livingEntity, 6.0f, Util.getRandom(variants, level.random)))
                level.removeBlock(pos, false);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getInteractionShape(state, level, pos);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return AbstractCustomShapeBlock.calculateShapes(state.getValue(FACING), AABB);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder context) {
        return List.of(new ItemStack(type.goo.get()));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        var direction = state.getValue(FACING).getOpposite();
        return super.canSurvive(state, level, pos) && level.getBlockState(pos.relative(direction))
                .isFaceSturdy(level, pos.relative(direction), direction.getOpposite(), SupportType.FULL);
    }
}
