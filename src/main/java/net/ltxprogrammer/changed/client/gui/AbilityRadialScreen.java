package net.ltxprogrammer.changed.client.gui;

import com.google.common.collect.ImmutableList;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.KeyReference;
import net.ltxprogrammer.changed.client.ChangedClient;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.network.packet.AbilitySelectPacket;
import net.ltxprogrammer.changed.util.SingleRunnable;
import net.ltxprogrammer.changed.world.inventory.AbilityRadialMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AbilityRadialScreen extends VariantRadialScreen<AbilityRadialMenu> {
    public final AbilityRadialMenu menu;
    public final TransfurVariantInstance<?> variant;
    public final List<AbstractAbility<?>> abilities;

    public AbilityRadialScreen(AbilityRadialMenu menu, Inventory inventory, Component text) {
        super(menu, inventory, text, menu.variant);
        this.menu = menu;
        this.variant = menu.variant;
        this.abilities = new ArrayList<>(menu.variant.abilityInstances.keySet());
    }

    @Override
    public int getCount() {
        return abilities.size();
    }

    @Nullable
    @Override
    public List<Component> tooltipsFor(int section) {
        var abilityInstance = menu.variant.getAbilityInstance(abilities.get(section));
        if (abilityInstance == null)
            return List.of();

        var desc = abilityInstance.getAbilityDescription();
        var builder = ImmutableList.<Component>builder().add(abilityInstance.getAbilityName());

        if (!desc.isEmpty()) {
            builder.add(Component.empty());
            builder.addAll(desc);
        }

        if (this.minecraft.options.advancedItemTooltips)
            builder.add((Component.literal(ChangedRegistry.ABILITY.getKey(abilityInstance.ability).toString())).withStyle(ChatFormatting.DARK_GRAY));

        return builder.build();
    }

    @Override
    public void renderSectionForeground(GuiGraphics graphics, int section, double x, double y, float partialTicks, int mouseX, int mouseY, float red, float green, float blue, float alpha) {
        boolean enabled = false;
        boolean selected = false;
        if (menu.variant.abilityInstances.containsKey(abilities.get(section))) {
            var ability = menu.variant.abilityInstances.get(abilities.get(section));
            if (ability != null) {
                enabled = ability.canUse();
            }
        }

        ChangedClient.abilityRenderer.getOrThrow().renderAbility(
                graphics,
                menu.player,
                menu.variant.getAbilityInstance(abilities.get(section)),
                (int) (x - 24 + this.leftPos),
                (int) (y - 24 + this.topPos),
                48,
                (enabled ? 1 : 0.5f),
                enabled,
                0);
    }

    @Override
    public boolean handleClicked(int section, SingleRunnable close) {
        close.run();
        var ability = abilities.get(section);
        variant.setSelectedAbility(KeyReference.ABILITY, ability);
        Changed.PACKET_HANDLER.sendToServer(new AbilitySelectPacket(this.menu.player, KeyReference.ABILITY, ability));
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        var section = getSectionUnderMouse();
        if (section == null)
            return super.keyPressed(keyCode, scanCode, modifiers);

        AtomicBoolean handled = new AtomicBoolean(false);
        variant.abilityHandler.visitSelected((index, totalCount, key, ability, abilityInstance) -> {
            if (key.getKeycode(Minecraft.getInstance().level) != keyCode)
                return;

            if (abilities.size() > section && menu.variant.abilityInstances.containsKey(abilities.get(section))) {
                var hoveredAbility = menu.variant.abilityInstances.get(abilities.get(section));
                if (hoveredAbility != null) {
                    if (handled.getAndSet(true))
                        return;

                    variant.setSelectedAbility(key, abilities.get(section));
                    Changed.PACKET_HANDLER.sendToServer(new AbilitySelectPacket(this.menu.player, key, abilities.get(section)));
                }
            }
        });

        return handled.getAcquire() || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isSelected(int section) {
        if (abilities.size() > section && menu.variant.abilityInstances.containsKey(abilities.get(section))) {
            var ability = menu.variant.abilityInstances.get(abilities.get(section));
            if (ability != null) {
                return menu.variant.isAbilitySelected(ability.ability);
            }
        }

        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
        variant.resetTicksSinceLastAbilityActivity();
    }
}