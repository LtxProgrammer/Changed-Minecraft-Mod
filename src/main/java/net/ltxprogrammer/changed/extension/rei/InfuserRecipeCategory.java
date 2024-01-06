package net.ltxprogrammer.changed.extension.rei;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplayMerger;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.InputIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.item.Syringe;
import net.ltxprogrammer.changed.item.VariantHoldingBase;
import net.ltxprogrammer.changed.recipe.InfuserRecipe;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InfuserRecipeCategory implements DisplayCategory<InfuserRecipeDisplay> {
    private final Renderer icon;
    private final TranslatableComponent localizedName;

    public InfuserRecipeCategory() {
        icon = EntryStacks.of(ChangedBlocks.INFUSER.get());
        localizedName = new TranslatableComponent("container.changed.infuser");
    }

    @Override
    public CategoryIdentifier<? extends InfuserRecipeDisplay> getCategoryIdentifier() {
        return ChangedReiPlugin.INFUSER;
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
    public List<Widget> setupDisplay(InfuserRecipeDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 58, bounds.getCenterY() - 27);
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 60, startPoint.y + 18)));
        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 95, startPoint.y + 19)));
        List<InputIngredient<EntryStack<?>>> input = display.getInputIngredients(3, 3);
        List<Slot> slots = Lists.newArrayList();
        for (int y = 0; y < 3; y++)
            for (int x = 0; x < 3; x++)
                slots.add(Widgets.createSlot(new Point(startPoint.x + 1 + x * 18, startPoint.y + 1 + y * 18)).markInput());
        for (InputIngredient<EntryStack<?>> ingredient : input) {
            slots.get(ingredient.getIndex()).entries(ingredient.get());
        }
        widgets.addAll(slots);
        var resultSlot = Widgets.createSlot(new Point(startPoint.x + 95, startPoint.y + 19)).entries(display.getOutputEntries().get(0)).disableBackground().markOutput();
        widgets.add(resultSlot);
        widgets.add(createGenderToggle(new Point(startPoint.x + 93, startPoint.y + 41), resultSlot));
        if (display.isShapeless()) {
            widgets.add(Widgets.createShapelessIcon(bounds));
        }
        return widgets;
    }

    private static class GenderToggle extends WidgetWithBounds {
        private static final ResourceLocation GENDER_SWITCH_LOCATION = Changed.modResource("textures/gui/gender_switch.png");
        private final Slot hostSlot;
        private final Rectangle bounds;

        private GenderToggle(Point location, Slot hostSlot) {
            this.bounds = new Rectangle(location.x, location.y, 20, 10);
            this.hostSlot = hostSlot;
        }

        @Override
        public Rectangle getBounds() {
            return bounds;
        }

        public EntryStack<?> getCurrentEntry() {
            return hostSlot.getCurrentEntry();
        }

        @Override
        public void render(PoseStack pose, int mouseX, int mouseY, float delta) {
            RenderSystem.setShaderTexture(0, GENDER_SWITCH_LOCATION);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            var entry = getCurrentEntry();
            boolean disabled = true;
            boolean toggle = false;
            if (entry.getValue() instanceof ItemStack stack && stack.getItem() instanceof VariantHoldingBase base &&
                    InfuserRecipe.INFUSER_BASE_CONVERSION.containsKey(base.getOriginalItem())) {
                var variant = Syringe.getVariant(stack);
                if (variant != null && variant.isGendered()) {
                    disabled = false;
                    toggle = variant.getFormId().getPath().endsWith("/female");
                }
            }

            int switchX = 0;
            int switchY = disabled ? bounds.height * 2 : (toggle ? bounds.height : 0);
            blit(pose, bounds.x, bounds.y, switchX, switchY, bounds.width, bounds.height, bounds.width * 2, bounds.height * 3);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of();
        }
    }

    private static GenderToggle createGenderToggle(Point where, Slot hostSlot) {
        return new GenderToggle(where, hostSlot);
    }

    @Override
    public @Nullable DisplayMerger<InfuserRecipeDisplay> getDisplayMerger() {
        return DisplayCategory.getContentMerger();
    }
}
