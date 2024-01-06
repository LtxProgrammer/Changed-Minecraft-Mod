package net.ltxprogrammer.changed.extension.rei;

import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplayMerger;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.InputIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PurifierRecipeCategory implements DisplayCategory<PurifierRecipeDisplay> {
    private final Renderer icon;
    private final TranslatableComponent localizedName;

    public PurifierRecipeCategory() {
        icon = EntryStacks.of(ChangedBlocks.PURIFIER.get());
        localizedName = new TranslatableComponent("container.changed.purifier");
    }

    @Override
    public CategoryIdentifier<? extends PurifierRecipeDisplay> getCategoryIdentifier() {
        return ChangedReiPlugin.PURIFIER;
    }

    @Override
    public Component getTitle() {
        return localizedName;
    }

    @Override
    public Renderer getIcon() {
        return icon;
    }

    @Override
    public List<Widget> setupDisplay(PurifierRecipeDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 58, bounds.getCenterY() - 27);
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 42, startPoint.y + 18)));
        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 82, startPoint.y + 19)));
        List<InputIngredient<EntryStack<?>>> input = display.getInputIngredients(1, 1);
        List<Slot> slots = Lists.newArrayList();
        slots.add(Widgets.createSlot(new Point(startPoint.x + 10, startPoint.y + 19)).markInput());
        for (InputIngredient<EntryStack<?>> ingredient : input) {
            slots.get(ingredient.getIndex()).entries(ingredient.get());
        }
        widgets.addAll(slots);
        var resultSlot = Widgets.createSlot(new Point(startPoint.x + 82, startPoint.y + 19)).entries(display.getOutputEntries().get(0)).disableBackground().markOutput();
        widgets.add(resultSlot);
        if (display.isShapeless()) {
            widgets.add(Widgets.createShapelessIcon(bounds));
        }
        return widgets;
    }

    @Override
    public @Nullable DisplayMerger<PurifierRecipeDisplay> getDisplayMerger() {
        return DisplayCategory.getContentMerger();
    }
}
