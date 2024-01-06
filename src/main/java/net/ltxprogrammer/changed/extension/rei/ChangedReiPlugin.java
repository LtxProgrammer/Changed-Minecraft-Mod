package net.ltxprogrammer.changed.extension.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.client.registry.transfer.simple.SimpleTransferHandler;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.comparison.EntryComparator;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.impl.Internals;
import net.ltxprogrammer.changed.client.gui.InfuserScreen;
import net.ltxprogrammer.changed.client.gui.PurifierScreen;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedRecipeTypes;
import net.ltxprogrammer.changed.recipe.InfuserRecipe;
import net.ltxprogrammer.changed.recipe.PurifierRecipe;
import net.ltxprogrammer.changed.world.inventory.InfuserMenu;
import net.ltxprogrammer.changed.world.inventory.PurifierMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

@REIPluginClient
public class ChangedReiPlugin implements REIClientPlugin {
    public static final CategoryIdentifier<InfuserRecipeDisplay> INFUSER = CategoryIdentifier.of("changed", "plugins/infuser");
    public static final CategoryIdentifier<PurifierRecipeDisplay> PURIFIER = CategoryIdentifier.of("changed", "plugins/purifier");

    @Override
    public void registerItemComparators(ItemComparatorRegistry registry) {
        registry.register(latexVariantNbt(), ChangedItems.LATEX_SYRINGE.get(), ChangedItems.LATEX_TIPPED_ARROW.get());
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerContainerClickArea(new Rectangle(103, 33, 22, 15), InfuserScreen.class, INFUSER);
        registry.registerContainerClickArea(new Rectangle(103, 33, 22, 15), PurifierScreen.class, PURIFIER);
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new InfuserRecipeCategory());
        registry.add(new PurifierRecipeCategory());
        registry.addWorkstations(INFUSER, EntryStacks.of(ChangedBlocks.INFUSER.get()));
        registry.addWorkstations(PURIFIER, EntryStacks.of(ChangedBlocks.PURIFIER.get()));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(InfuserRecipe.class, ChangedRecipeTypes.INFUSER_RECIPE, InfuserRecipeDisplay::new);
        registry.registerRecipeFiller(PurifierRecipe.class, ChangedRecipeTypes.PURIFIER_RECIPE, PurifierRecipeDisplay::new);
    }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        registry.register(SimpleTransferHandler.create(InfuserMenu.class, INFUSER,
                new SimpleTransferHandler.IntRange(1, 10)));
        registry.register(SimpleTransferHandler.create(PurifierMenu.class, PURIFIER,
                new SimpleTransferHandler.IntRange(1, 2)));
    }

    private static EntryComparator<ItemStack> latexVariantNbt() {
        EntryComparator<Tag> nbtHasher = nbt("Count", "safe", "owner");
        return (context, stack) -> {
            CompoundTag tag = stack.getTag();
            return tag == null ? 0L : nbtHasher.hash(context, tag);
        };
    }

    private static EntryComparator<Tag> nbt(String... ignoredKeys) {
        return Internals.getNbtHasher(ignoredKeys);
    }
}
