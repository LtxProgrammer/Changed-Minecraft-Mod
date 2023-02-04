package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface LatexFusingItem extends WearableItem {
    LatexVariant<?> getFusionVariant(LatexVariant<?> currentVariant, LivingEntity livingEntity, ItemStack itemStack);

    void nonLatexWearTick(LivingEntity entity, ItemStack itemStack);
    @Override
    default void wearTick(LivingEntity entity, ItemStack itemStack) {
        if (entity instanceof LatexEntity latex && latex.getSelfVariant() != null) {
            var newVariant = getFusionVariant(latex.getSelfVariant(), latex, itemStack);
            if (newVariant == null)
                return;
            itemStack.shrink(1);
            newVariant.replaceEntity(latex);
        }

        else if (entity instanceof Player player && ProcessTransfur.isPlayerLatex(player)) {
            var newVariant = getFusionVariant(ProcessTransfur.getPlayerLatexVariant(player), player, itemStack);
            if (newVariant == null)
                return;
            itemStack.shrink(1);
            ProcessTransfur.setPlayerLatexVariant(player, newVariant);
            ChangedSounds.broadcastSound(player, newVariant.sound, 1, 1);
        }

        else if (!(entity instanceof LatexEntity))
            nonLatexWearTick(entity, itemStack);
    }
}
