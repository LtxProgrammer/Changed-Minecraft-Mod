package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.block.entity.GasCanisterBlockEntity;
import net.ltxprogrammer.changed.item.GasCanister;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class GasCanisterBlock extends AbstractCustomShapeTallEntityBlock {
    public static final VoxelShape SHAPE_WHOLE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 28.0D, 12.0D);

    public GasCanisterBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).sound(SoundType.METAL).color(MaterialColor.WOOL).strength(1.0F, 5.0F));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);
        if (!(stack.getItem() instanceof GasCanister))
            return;
        if (!(level.getBlockEntity(pos) instanceof GasCanisterBlockEntity blockEntity))
            return;

        blockEntity.setUsage(stack.getDamageValue());
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        var blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        var canisterEntity = blockEntity instanceof GasCanisterBlockEntity ? (GasCanisterBlockEntity)blockEntity : null;
        return super.getDrops(state, builder).stream().peek(itemStack -> {
            if (canisterEntity != null && itemStack.getItem() instanceof GasCanister)
                itemStack.setDamageValue(canisterEntity.getUsage());
        }).collect(Collectors.toList());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER)
            return SHAPE_WHOLE.move(0, -1, 0);
        else
            return SHAPE_WHOLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? new GasCanisterBlockEntity(pos, state) : null;
    }
}
