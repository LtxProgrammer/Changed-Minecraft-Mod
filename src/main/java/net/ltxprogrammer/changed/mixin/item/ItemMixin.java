package net.ltxprogrammer.changed.mixin.item;

import net.ltxprogrammer.changed.init.ChangedAccessorySlots;
import net.ltxprogrammer.changed.util.ItemUtil;
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

@Mixin(Item.class)
public abstract class ItemMixin extends net.minecraftforge.registries.ForgeRegistryEntry<Item> implements ItemLike, net.minecraftforge.common.extensions.IForgeItem {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> callback) {
        if (asItem() instanceof SaddleItem) {
            ItemStack itemstack = player.getItemInHand(hand);

            if (ItemUtil.tryEquipAccessory(player, itemstack, ChangedAccessorySlots.LOWER_BODY.get())) {
                level.playSound((Player)null, player, SoundEvents.HORSE_SADDLE, SoundSource.PLAYERS, 0.5F, 1.0F);
                itemstack.shrink(1);
                callback.setReturnValue(InteractionResultHolder.consume(itemstack));
            }
        }
    }
}
