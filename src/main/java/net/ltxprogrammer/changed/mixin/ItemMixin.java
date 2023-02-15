package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.init.ChangedAbilities;
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

@Mixin(Item.class)
public abstract class ItemMixin extends net.minecraftforge.registries.ForgeRegistryEntry<Item> implements ItemLike, net.minecraftforge.common.extensions.IForgeItem {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(Level level, Player player, InteractionHand p_41434_, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> callback) {
        if (asItem() instanceof SaddleItem) {
            ProcessTransfur.ifPlayerLatex(player, variant -> {
                var ability = variant.getAbilityInstance(ChangedAbilities.ACCESS_SADDLE);
                if (ability.saddle.isEmpty()) { // Quick equip saddle
                    ItemStack itemstack = player.getItemInHand(p_41434_);
                    ItemStack copy = itemstack.copy();
                    copy.setCount(1);
                    ability.saddle = copy;
                    ability.ability.setDirty(ability);
                    level.playSound((Player)null, player, SoundEvents.HORSE_SADDLE, SoundSource.PLAYERS, 0.5F, 1.0F);
                    itemstack.shrink(1);
                    callback.setReturnValue(InteractionResultHolder.consume(itemstack));
                }
            });
        }
    }
}
