package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SaddleItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.ltxprogrammer.changed.world.inventory.CentaurSaddleMenu.SADDLE_LOCATION;

@Mixin(Item.class)
public abstract class ItemMixin extends net.minecraftforge.registries.ForgeRegistryEntry<Item> implements ItemLike, net.minecraftforge.common.extensions.IForgeItem {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(Level level, Player player, InteractionHand p_41434_, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> callback) {
        if ((Item)(Object)this instanceof SaddleItem) {
            if (ProcessTransfur.isPlayerLatex(player) && ProcessTransfur.getPlayerLatexVariant(player).rideable()) {
                if (!player.getPersistentData().contains(SADDLE_LOCATION)) { // Quick equip saddle
                    ItemStack itemstack = player.getItemInHand(p_41434_);
                    ItemStack copy = itemstack.copy();
                    copy.setCount(1);
                    player.getPersistentData().put(SADDLE_LOCATION, copy.serializeNBT());
                    level.playSound((Player)null, player, SoundEvents.HORSE_SADDLE, SoundSource.PLAYERS, 0.5F, 1.0F);
                    itemstack.shrink(1);
                    callback.setReturnValue(InteractionResultHolder.consume(itemstack));
                }
            }
        }
    }
}
