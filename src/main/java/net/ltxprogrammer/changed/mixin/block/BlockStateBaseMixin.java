package net.ltxprogrammer.changed.mixin.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.block.PartialEntityBlock;
import net.ltxprogrammer.changed.block.WhiteLatexTransportInterface;
import net.ltxprogrammer.changed.entity.LatexType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.getLatexed;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin extends StateHolder<Block, BlockState> {
    @Shadow protected abstract BlockState asState();

    @Shadow public abstract Block getBlock();

    protected BlockStateBaseMixin(Block p_61117_, ImmutableMap<Property<?>, Comparable<?>> p_61118_, MapCodec<BlockState> p_61119_) {
        super(p_61117_, p_61118_, p_61119_);
    }

    @Inject(method = "canOcclude", at = @At("HEAD"), cancellable = true)
    public void canOcclude(CallbackInfoReturnable<Boolean> cir) {
        if (this.getProperties().contains(AbstractLatexBlock.COVERED) && this.getValue(AbstractLatexBlock.COVERED) == LatexType.WHITE_LATEX)
            cir.setReturnValue(false);
    }

    @Inject(method = "hasBlockEntity", at = @At("RETURN"), cancellable = true)
    public void hasPartialBlockEntity(CallbackInfoReturnable<Boolean> callback) {
        var base = (BlockBehaviour.BlockStateBase)(Object)this;

        if (callback.getReturnValue() && this.getBlock() instanceof PartialEntityBlock partial && base instanceof BlockState blockState)
            callback.setReturnValue(partial.stateHasBlockEntity(blockState));
    }

    @Inject(method = "isSuffocating", at = @At("HEAD"), cancellable = true)
    public void isSuffocating(BlockGetter getter, BlockPos blockPos, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (getLatexed(this.asState()) == LatexType.WHITE_LATEX) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    @Inject(method = "isViewBlocking", at = @At("HEAD"), cancellable = true)
    public void isViewBlocking(BlockGetter getter, BlockPos blockPos, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (getLatexed(this.asState()) == LatexType.WHITE_LATEX) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    @Inject(method = "Lnet/minecraft/world/level/block/state/BlockBehaviour$BlockStateBase;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("HEAD"), cancellable = true)
    public void getCollisionShape(BlockGetter getter, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> callbackInfoReturnable) {
        if (getLatexed(this.asState()) == LatexType.WHITE_LATEX) {
            if (context instanceof EntityCollisionContext ecc) {
                if (ecc.getEntity() instanceof LivingEntity le) {
                    if (WhiteLatexTransportInterface.isEntityInWhiteLatex(le))
                        callbackInfoReturnable.setReturnValue(Shapes.empty());
                }
            }
        }
    }
}
