package net.ltxprogrammer.changed.mixin.item;

import net.ltxprogrammer.changed.data.AccessorySlotContext;
import net.ltxprogrammer.changed.init.ChangedAccessorySlots;
import net.ltxprogrammer.changed.item.AccessoryItem;
import net.ltxprogrammer.changed.util.ItemUtil;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SaddleItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SaddleItem.class)
public abstract class SaddleItemMixin extends Item implements AccessoryItem {
    public SaddleItemMixin(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (ItemUtil.tryEquipAccessory(player, itemstack, ChangedAccessorySlots.LOWER_BODY.get())) {
            level.playSound((Player)null, player, SoundEvents.HORSE_SADDLE, SoundSource.PLAYERS, 0.5F, 1.0F);
            player.level().gameEvent(player, GameEvent.EQUIP, player.position());

            return InteractionResultHolder.sidedSuccess(itemstack, player.level().isClientSide);
        }

        return super.use(level, player, hand);
    }

    @Override
    public void accessoryRemoved(AccessorySlotContext<?> slotContext) {
        slotContext.wearer().getPassengers().forEach(Entity::stopRiding);
    }
}
