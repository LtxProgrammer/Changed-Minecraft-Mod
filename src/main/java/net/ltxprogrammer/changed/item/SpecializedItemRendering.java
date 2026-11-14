package net.ltxprogrammer.changed.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface SpecializedItemRendering {
    static boolean isGUI(ItemDisplayContext type) {
        return type == ItemDisplayContext.GUI || type == ItemDisplayContext.GROUND || type == ItemDisplayContext.FIXED;
    }

    @Nullable ResourceLocation getModelLocation(ItemStack itemStack, ItemDisplayContext type);
    void loadSpecialModels(Consumer<ResourceLocation> loader);

    static void onModelRegistryEvent(ModelEvent.RegisterAdditional event) {
        ForgeRegistries.ITEMS.forEach(item -> {
            if (item instanceof SpecializedItemRendering specialized)
                specialized.loadSpecialModels(event::register);
        });
    }
}
