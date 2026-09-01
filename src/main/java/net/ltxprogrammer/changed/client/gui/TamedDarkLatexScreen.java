package net.ltxprogrammer.changed.client.gui;

import com.google.common.collect.ImmutableList;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.ai.TamedEntityFavor;
import net.ltxprogrammer.changed.util.Color3;
import net.ltxprogrammer.changed.util.SingleRunnable;
import net.ltxprogrammer.changed.world.inventory.TamedDarkLatexMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class TamedDarkLatexScreen extends AbstractRadialScreen<TamedDarkLatexMenu> {
    private static final String PATH_GOO = "textures/gui/radial/goo/";
    private static final String PATH_GOO_SELECTED = "textures/gui/radial/goo_selected/";

    private static final Component ACTIVE = Component.translatable("changed.tamed_dark_latex.active");
    private static final Component INACTIVE = Component.translatable("changed.tamed_dark_latex.inactive");

    public record Interaction(String command, ResourceLocation texture, Supplier<List<Component>> tooltips, Supplier<Boolean> shouldHighlight) {
        public Interaction(String command, Supplier<List<Component>> tooltips, Supplier<Boolean> shouldHighlight) {
            this(command, Changed.modResource("textures/gui/tamed_dl_interactions/" + command + ".png"), tooltips, shouldHighlight);
        }
    }

    protected final ImmutableList<Interaction> availableInteractions;

    public TamedDarkLatexScreen(TamedDarkLatexMenu menu, Inventory inventory, Component text) {
        super(menu, inventory, text, Color3.DARK, Color3.WHITE, menu.tamedDarkLatex);
        var interactionsBuilder = ImmutableList.<Interaction>builder();
        interactionsBuilder.add(new Interaction("view_inventory",
                () -> List.of(Component.translatable("changed.tamed_dark_latex.title.view_inventory")),
                () -> false
        ));
        interactionsBuilder.add(new Interaction("cycle_follow",
                () -> List.of(Component.translatable("changed.tamed_dark_latex.title.cycle_follow"),
                        Component.translatable(menu.tamedDarkLatex.isFollowingOwner() ? "changed.tamed_dark_latex.follow" : "changed.tamed_dark_latex.wander")),
                () -> false
        ));
        interactionsBuilder.add(new Interaction("cycle_target_type",
                () -> List.of(Component.translatable("changed.tamed_dark_latex.title.cycle_target_type"),
                        menu.tamedDarkLatex.getTargetType().getDisplayText()),
                () -> false
        ));
        interactionsBuilder.add(new Interaction("cycle_attack_type",
                () -> List.of(Component.translatable("changed.tamed_dark_latex.title.cycle_attack_type"),
                        menu.tamedDarkLatex.getAttackType().getDisplayText()),
                () -> false
        ));
        interactionsBuilder.add(new Interaction("cycle_attack_condition",
                () -> List.of(Component.translatable("changed.tamed_dark_latex.title.cycle_attack_condition"),
                        menu.tamedDarkLatex.getAttackCondition().getDisplayText()),
                () -> false
        ));
        if (menu.tamedDarkLatex.canDoFavor(TamedEntityFavor.FISHING))
            interactionsBuilder.add(new Interaction("favor_fishing",
                    () -> List.of(Component.translatable("changed.tamed_dark_latex.title.favor_fishing"),
                            menu.tamedDarkLatex.getCurrentFavor() == TamedEntityFavor.FISHING ? ACTIVE : INACTIVE),
                    () -> menu.tamedDarkLatex.getCurrentFavor() == TamedEntityFavor.FISHING
            ));
        if (menu.tamedDarkLatex.canDoFavor(TamedEntityFavor.CAVING))
            interactionsBuilder.add(new Interaction("favor_caving",
                    () -> List.of(Component.translatable("changed.tamed_dark_latex.title.favor_caving"),
                            menu.tamedDarkLatex.getCurrentFavor() == TamedEntityFavor.CAVING ? ACTIVE : INACTIVE),
                    () -> menu.tamedDarkLatex.getCurrentFavor() == TamedEntityFavor.CAVING
            ));
        if (menu.tamedDarkLatex.canDoFavor(TamedEntityFavor.SUIT_OWNER))
            interactionsBuilder.add(new Interaction("favor_suit_owner",
                    () -> List.of(Component.translatable("changed.tamed_dark_latex.title.favor_suit_owner"),
                            menu.tamedDarkLatex.getCurrentFavor() == TamedEntityFavor.SUIT_OWNER ? ACTIVE : INACTIVE),
                    () -> menu.tamedDarkLatex.getCurrentFavor() == TamedEntityFavor.SUIT_OWNER
            ));
        availableInteractions = interactionsBuilder.build();
    }

    protected ResourceLocation getTextureForSection(int section, boolean thisHovered, boolean anyHovered, boolean active) {
        return Changed.modResource(((thisHovered || (!anyHovered && active)) ? PATH_GOO_SELECTED : PATH_GOO) + section + ".png");
    }

    @Override
    public int getCount() {
        return availableInteractions.size();
    }

    @Override
    public @Nullable List<Component> tooltipsFor(int section) {
        return availableInteractions.get(section).tooltips.get();
    }

    protected Optional<Interaction> getInteractionSafe(int interactionIndex) {
        if (interactionIndex < availableInteractions.size())
            return Optional.ofNullable(availableInteractions.get(interactionIndex));
        return Optional.empty();
    }

    @Override
    public void renderSectionBackground(GuiGraphics graphics, int section, double x, double y, float partialTicks, int mouseX, int mouseY, float red, float green, float blue) {
        var hovered = getSectionAt(mouseX, mouseY);
        boolean anyHovered = hovered.isPresent();
        boolean thisHovered = anyHovered && hovered.get() == section;
        graphics.setColor(red, green, blue, 1);
        graphics.blit(getTextureForSection(section, thisHovered, anyHovered, getInteractionSafe(section).map(Interaction::shouldHighlight).map(Supplier::get).orElse(false)),
                (int)x - 32 + this.leftPos, (int)y - 32 + this.topPos, 0, 0, 64, 64, 64, 64);
    }

    @Override
    public void renderSectionForeground(GuiGraphics graphics, int section, double x, double y, float partialTicks, int mouseX, int mouseY, float red, float green, float blue, float alpha) {
        graphics.setColor(0, 0, 0, 0.5f);
        graphics.blit(availableInteractions.get(section).texture,
                (int)x - 24 + this.leftPos + 3, (int)y - 24 + this.topPos + 3, 0, 0, 48, 48, 48, 48);
        graphics.setColor(red, green, blue, 1);
        graphics.blit(availableInteractions.get(section).texture,
                (int)x - 24 + this.leftPos, (int)y - 24 + this.topPos, 0, 0, 48, 48, 48, 48);
    }

    @Override
    public boolean handleClicked(int section, SingleRunnable close) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));

        getInteractionSafe(section).ifPresent(interaction -> {
            var tag = new CompoundTag();
            tag.putString("command", interaction.command);
            menu.setDirty(tag);
        });

        return false;
    }
}
