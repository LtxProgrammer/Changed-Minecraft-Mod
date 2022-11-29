package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.block.DarkLatexBlock;
import net.ltxprogrammer.changed.block.WhiteLatexBlock;
import net.ltxprogrammer.changed.block.WhiteLatexTransportInterface;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.COVERED;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin extends net.minecraftforge.registries.ForgeRegistryEntry<Block> {
    private boolean isLatexed(BlockState blockState) {
        return getLatexed(blockState) != LatexType.NEUTRAL;
    }

    private LatexType getLatexed(BlockState blockState) {
        return blockState.getProperties().contains(COVERED) ? blockState.getValue(COVERED) : LatexType.NEUTRAL;
    }

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos position, @NotNull Random random, CallbackInfo callbackInfo) {
        if (state.getProperties().contains(COVERED) && state.getValue(COVERED) != LatexType.NEUTRAL) {
            callbackInfo.cancel();

            if (!level.isAreaLoaded(position, 3)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
            AbstractLatexBlock.randomTick(state, level, position, random, state.getValue(COVERED));
            return;
        }
    }

    @Inject(method = "getDrops", at = @At("RETURN"), cancellable = true)
    public void getDrops(BlockState state, LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> callbackInfoReturnable) {
        if (isLatexed(state)) {
            var goo = getLatexed(state).goo;
            ArrayList<ItemStack> newList = new ArrayList<>(callbackInfoReturnable.getReturnValue());
            newList.add(goo.get().getDefaultInstance());
            callbackInfoReturnable.setReturnValue(newList);
        }
    }

    @Inject(method = "skipRendering", at = @At("HEAD"), cancellable = true)
    public void skipRendering(BlockState state, BlockState otherState, Direction direction, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        // Guaranteed to be client only

        if (!otherState.isFaceSturdy(UniversalDist.getLevel(), BlockPos.ZERO, direction))
            return;
        if (!state.isFaceSturdy(UniversalDist.getLevel(), BlockPos.ZERO, direction.getOpposite()))
            return;

        if (getLatexed(state) != LatexType.NEUTRAL && getLatexed(state) == getLatexed(otherState))
            callbackInfoReturnable.setReturnValue(true);
        else if (otherState.getBlock() instanceof AbstractLatexBlock abstractLatexBlock) {
            if (abstractLatexBlock instanceof WhiteLatexBlock && getLatexed(state) == LatexType.WHITE_LATEX)
                callbackInfoReturnable.setReturnValue(true);
            if (abstractLatexBlock instanceof DarkLatexBlock && getLatexed(state) == LatexType.DARK_LATEX)
                callbackInfoReturnable.setReturnValue(true);
        }
    }

    @Inject(method = "getVisualShape", at = @At("HEAD"), cancellable = true)
    public void getVisualShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> callbackInfoReturnable) {
        if (getLatexed(state) == LatexType.WHITE_LATEX)
            callbackInfoReturnable.setReturnValue(Shapes.empty());
    }

    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    public void getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> callbackInfoReturnable) {
        if (getLatexed(state) == LatexType.WHITE_LATEX) {
            if (context instanceof EntityCollisionContext ecc) {
                if (ecc.getEntity() instanceof LivingEntity le) {
                    if (WhiteLatexTransportInterface.isEntityInWhiteLatex(le))
                        callbackInfoReturnable.setReturnValue(Shapes.empty());
                }
            }
        }
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> callbackInfoReturnable) {
        var coveredWith = getLatexed(state);

        if (coveredWith != LatexType.NEUTRAL) {
            callbackInfoReturnable.setReturnValue(InteractionResult.PASS);

            if (coveredWith == LatexType.WHITE_LATEX) {
                LatexVariant<?> variant = ProcessTransfur.getPlayerLatexVariant(player);
                if (variant != null && variant.getLatexType() == LatexType.WHITE_LATEX &&
                        /*player.isShiftKeyDown() && */player.getItemInHand(player.getUsedItemHand()).isEmpty() && !WhiteLatexTransportInterface.isEntityInWhiteLatex(player)) { // Empty-handed RMB
                    if (pos.distSqr(new BlockPos(player.getBlockX(), player.getBlockY(), player.getBlockZ())) > 4.0)
                        return;

                    WhiteLatexTransportInterface.entityEnterLatex(player, pos);
                    callbackInfoReturnable.setReturnValue(InteractionResult.CONSUME);
                }
            }
        }
    }
}
