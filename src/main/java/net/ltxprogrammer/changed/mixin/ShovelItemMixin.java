package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.entity.LatexType;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.COVERED;

@Mixin(ShovelItem.class)
public abstract class ShovelItemMixin {
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    public void useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> callback) {
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        Player player = context.getPlayer();
        if (player != null && blockstate.getProperties().contains(COVERED) && blockstate.getValue(COVERED) != LatexType.NEUTRAL) {
            level.setBlockAndUpdate(blockpos, blockstate.setValue(COVERED, LatexType.NEUTRAL));
            if (player == null || (!player.isCreative() && !player.isSpectator()))
                Block.popResource(level, blockpos, new ItemStack(blockstate.getValue(COVERED).goo.get()));
            level.playSound(context.getPlayer(), blockpos, SoundEvents.SLIME_BLOCK_STEP, SoundSource.BLOCKS, 1.0F, 1.0F);
            callback.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));

            context.getItemInHand().hurtAndBreak(1, player, (p_43122_) -> {
                p_43122_.broadcastBreakEvent(context.getHand());
            });
        }
    }
}
