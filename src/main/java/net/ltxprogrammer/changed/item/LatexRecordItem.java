package net.ltxprogrammer.changed.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class LatexRecordItem extends LoopedRecordItem {
    private static final Component NAME = new TranslatableComponent("item.changed.latex_music_disc");

    public LatexRecordItem(int comparatorValue, Supplier<SoundEvent> soundSupplier, Properties builder) {
        super(comparatorValue, soundSupplier, builder);
    }

    @Override
    public Component getName(ItemStack stack) {
        return NAME;
    }
}
