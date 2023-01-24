package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LaserBeamBlock extends Block implements NonLatexCoverableBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    private static final VoxelShape SHAPE_X = Shapes.box(0, 7, 7, 16, 9, 9);
    private static final VoxelShape SHAPE_Y = Shapes.box(7, 0, 7, 9, 16, 9);
    private static final VoxelShape SHAPE_Z = Shapes.box(7, 7, 0, 9, 9, 16);

    public LaserBeamBlock() {
        super(BlockBehaviour.Properties.of(Material.STRUCTURAL_AIR).strength(-1.0F, 3600000.0F).noDrops().isValidSpawn((p_61031_, p_61032_, p_61033_, p_61034_) -> false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AXIS);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState blockState, BlockGetter level, BlockPos blockPos) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        return switch (blockState.getValue(AXIS)) {
            case X -> SHAPE_X;
            case Y -> SHAPE_Y;
            case Z -> SHAPE_Z;
        };
    }

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        super.entityInside(blockState, level, blockPos, entity);

        if (!(entity instanceof LivingEntity livingEntity))
            return;

        livingEntity.getArmorSlots().forEach(itemStack -> {
            if (itemStack.is(ChangedItems.BENIGN_PANTS.get()))
                ProcessTransfur.progressTransfur(livingEntity, 11000, LatexVariant.LATEX_BENIGN_WOLF.getFormId());
        });
    }
}
