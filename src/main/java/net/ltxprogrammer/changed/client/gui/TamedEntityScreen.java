package net.ltxprogrammer.changed.client.gui;

import com.google.common.collect.ImmutableList;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.ai.TamedEntityFavor;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.init.ChangedTamedEntityFavors;
import net.ltxprogrammer.changed.util.Color3;
import net.ltxprogrammer.changed.util.SingleRunnable;
import net.ltxprogrammer.changed.world.inventory.TamedEntityMenu;
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

public class TamedEntityScreen extends AbstractRadialScreen<TamedEntityMenu> {
    private static final ResourceLocation PATH_ORGANIC = Changed.modResource("textures/gui/radial/organic.png");
    private static final ResourceLocation PATH_GOO = Changed.modResource("textures/gui/radial/goo.png");
    private static final ResourceLocation PATH_ORGANIC_SELECTED = Changed.modResource("textures/gui/radial/organic_selected.png");
    private static final ResourceLocation PATH_GOO_SELECTED = Changed.modResource("textures/gui/radial/goo_selected.png");
    private static final ResourceLocation PATH_ORGANIC_HOVERED = Changed.modResource("textures/gui/radial/organic_hovered.png");
    private static final ResourceLocation PATH_GOO_HOVERED = Changed.modResource("textures/gui/radial/goo_hovered.png");

    private static final Component ACTIVE = Component.translatable("changed.tamed_entity.active");
    private static final Component INACTIVE = Component.translatable("changed.tamed_entity.inactive");

    public record Interaction(String command, ResourceLocation texture, Supplier<List<Component>> tooltips, Supplier<Boolean> shouldHighlight) {
        public Interaction(String command, Supplier<List<Component>> tooltips, Supplier<Boolean> shouldHighlight) {
            this(command, Changed.modResource("textures/gui/tamed_interactions/" + command + ".png"), tooltips, shouldHighlight);
        }
    }

    protected final ImmutableList<Interaction> availableInteractions;

    public TamedEntityScreen(TamedEntityMenu menu, Inventory inventory, Component text) {
        super(menu, inventory, text, Color3.DARK, Color3.WHITE, menu.tamedEntity);
        var interactionsBuilder = ImmutableList.<Interaction>builder();
        interactionsBuilder.add(new Interaction("view_inventory",
                () -> List.of(Component.translatable("changed.tamed_entity.title.view_inventory")),
                () -> false
        ));
        interactionsBuilder.add(new Interaction("cycle_follow",
                () -> List.of(Component.translatable("changed.tamed_entity.title.cycle_follow"),
                        Component.translatable(menu.tamedEntity.isFollowingOwner() ? "changed.tamed_entity.follow" : "changed.tamed_entity.wander")),
                () -> false
        ));
        interactionsBuilder.add(new Interaction("cycle_target_type",
                () -> List.of(Component.translatable("changed.tamed_entity.title.cycle_target_type"),
                        menu.tamedEntity.getTargetType().getDisplayText()),
                () -> false
        ));
        interactionsBuilder.add(new Interaction("cycle_attack_type",
                () -> List.of(Component.translatable("changed.tamed_entity.title.cycle_attack_type"),
                        menu.tamedEntity.getAttackType().getDisplayText()),
                () -> false
        ));
        interactionsBuilder.add(new Interaction("cycle_attack_condition",
                () -> List.of(Component.translatable("changed.tamed_entity.title.cycle_attack_condition"),
                        menu.tamedEntity.getAttackCondition().getDisplayText()),
                () -> false
        ));
        if (menu.tamedEntity.canDoFavor(ChangedTamedEntityFavors.FISHING.get()))
            interactionsBuilder.add(new Interaction("favor_fishing",
                    () -> List.of(Component.translatable("changed.tamed_entity.title.favor_fishing"),
                            menu.tamedEntity.getCurrentFavor() == ChangedTamedEntityFavors.FISHING.get() ? ACTIVE : INACTIVE),
                    () -> menu.tamedEntity.getCurrentFavor() == ChangedTamedEntityFavors.FISHING.get()
            ));
        if (menu.tamedEntity.canDoFavor(ChangedTamedEntityFavors.CAVING.get()))
            interactionsBuilder.add(new Interaction("favor_caving",
                    () -> List.of(Component.translatable("changed.tamed_entity.title.favor_caving"),
                            menu.tamedEntity.getCurrentFavor() == ChangedTamedEntityFavors.CAVING.get() ? ACTIVE : INACTIVE),
                    () -> menu.tamedEntity.getCurrentFavor() == ChangedTamedEntityFavors.CAVING.get()
            ));
        if (menu.tamedEntity.canDoFavor(ChangedTamedEntityFavors.SUIT_OWNER.get()))
            interactionsBuilder.add(new Interaction("favor_suit_owner",
                    () -> List.of(Component.translatable("changed.tamed_entity.title.favor_suit_owner"),
                            menu.tamedEntity.getCurrentFavor() == ChangedTamedEntityFavors.SUIT_OWNER.get() ? ACTIVE : INACTIVE),
                    () -> menu.tamedEntity.getCurrentFavor() == ChangedTamedEntityFavors.SUIT_OWNER.get()
            ));
        availableInteractions = interactionsBuilder.build();
    }

    protected int getSectionU(int section) {
        return (section % 4) * 80;
    }

    protected int getSectionV(int section) {
        return (section / 4) * 80;
    }

    protected ResourceLocation getTextureForSection(int section, boolean thisHovered, boolean anyHovered) {
        if (menu.tamedEntity.getType().is(ChangedTags.EntityTypes.LATEX))
            return thisHovered ? PATH_GOO_HOVERED : PATH_GOO;
        else
            return thisHovered ? PATH_ORGANIC_HOVERED : PATH_ORGANIC;
    }

    protected ResourceLocation getTextureForSelectedSection() {
        if (menu.tamedEntity.getType().is(ChangedTags.EntityTypes.LATEX))
            return PATH_GOO_SELECTED;
        else
            return PATH_ORGANIC_SELECTED;
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
        boolean thisSelected = getInteractionSafe(section).map(Interaction::shouldHighlight).map(Supplier::get).orElse(false);
        int u = this.getSectionU(section);
        int v = this.getSectionV(section);
        graphics.setColor(red, green, blue, 1);
        graphics.blit(getTextureForSection(section, thisHovered, anyHovered),
                (int)x - 32 + this.leftPos - 8, (int)y - 32 + this.topPos - 8, u, v, 80, 80, 320, 160);
        graphics.setColor(1, 1, 1, 1);

        if (!thisSelected)
            return;

        graphics.blit(getTextureForSelectedSection(),
                (int)x - 32 + this.leftPos - 8, (int)y - 32 + this.topPos - 8, u, v, 80, 80, 320, 160);
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
