package net.ltxprogrammer.changed.mixin.block;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.MapCodec;
import net.ltxprogrammer.changed.block.PartialEntityBlock;
import net.ltxprogrammer.changed.world.LatexCoverGetter;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin extends StateHolder<Block, BlockState> {
    @Shadow public abstract Block getBlock();

    protected BlockStateBaseMixin(Block p_61117_, ImmutableMap<Property<?>, Comparable<?>> p_61118_, MapCodec<BlockState> p_61119_) {
        super(p_61117_, p_61118_, p_61119_);
    }

    @Inject(method = "hasBlockEntity", at = @At("RETURN"), cancellable = true)
    public void hasPartialBlockEntity(CallbackInfoReturnable<Boolean> callback) {
        var base = (BlockBehaviour.BlockStateBase)(Object)this;

        if (callback.getReturnValue() && this.getBlock() instanceof PartialEntityBlock partial && base instanceof BlockState blockState)
            callback.setReturnValue(partial.stateHasBlockEntity(blockState));
    }

    @WrapOperation(method = "updateNeighbourShapes(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;neighborShapeChanged(Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;II)V"))
    public void updateLatexCoverNeighbor(LevelAccessor instance, Direction direction, BlockState neighborState, BlockPos blockPos, BlockPos neighborPos, int flags, int timeToLive, Operation<Void> original) {
        original.call(instance, direction, neighborState, blockPos, neighborPos, flags, timeToLive - 1);
        LatexCoverState.executeShapeUpdate(instance, direction, neighborState, blockPos, neighborPos, flags, timeToLive - 1);
    }

    @WrapMethod(method = "getMapColor")
    public MapColor changed$maybeGetCoverColor(BlockGetter level, BlockPos blockPos, Operation<MapColor> original) {
        BlockPos above = blockPos.above();
        LatexCoverState coverStateAbove = LatexCoverState.getAt(level, above);
        if (coverStateAbove.isAir())
            return original.call(level, blockPos);

        LatexCoverGetter coverGetter = LatexCoverGetter.extendDefault(level);
        MapColor coverColor = coverStateAbove.getMapColor(coverGetter, above);
        if (coverColor == MapColor.NONE)
            return original.call(level, blockPos);
        return coverColor;
    }
}
