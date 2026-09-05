package net.ltxprogrammer.changed.client.gui;

import net.ltxprogrammer.changed.entity.beast.WingedEntity;
import net.ltxprogrammer.changed.init.ChangedAttributes;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Transition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;

public class FlightStaminaOverlay {
    private static double LAST_RENDERED_STAMINA = 0.0d;
    private static float TICKS_SINCE_CHANGE = 0.0f;

    public static void render(Gui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
        Player player = Minecraft.getInstance().player;
        if (!ProcessTransfur.isPlayerTransfurred(player))
            return;
        if (player.isCreative() || player.isSpectator())
            return;
        var variant = ProcessTransfur.getPlayerTransfurVariant(player);
        if (variant == null)
            return;

        double maxFlightStamina = player.getAttributeValue(ChangedAttributes.MAX_FLIGHT_STAMINA.get());
        if (maxFlightStamina <= 0.0)
            return;

        double stamina = variant.getFlightStamina();

        if (stamina != LAST_RENDERED_STAMINA) {
            LAST_RENDERED_STAMINA = stamina;
            TICKS_SINCE_CHANGE = 0.0f;
        } else {
            TICKS_SINCE_CHANGE += Minecraft.getInstance().getDeltaFrameTime();
        }

        float alpha = Transition.easeInOutSine(
                Mth.clamp(Mth.map(TICKS_SINCE_CHANGE, 100.0f, 130.0f, 1.0f, 0.0f), 0.0f, 1.0f));

        WingedEntity.WingDesign design = WingedEntity.WingDesign.WEBBED_DARK;
        if (variant.getChangedEntity() instanceof WingedEntity wingedEntity)
            design = wingedEntity.getWingDesign();

        int x = (screenWidth / 2) - 32;
        int y = screenHeight - 60 - 14;
        if (gui instanceof ForgeGui forgeGui)
            y = screenHeight - (Math.max(forgeGui.leftHeight, forgeGui.rightHeight) + 5);

        int pixelsOfStamina = Mth.clamp((int) Math.round((stamina / maxFlightStamina) * 14.0), 0, 14);

        if (alpha <= 0.0f)
            return;

        graphics.setColor(1.0f, 1.0f, 1.0f, alpha);
        if (pixelsOfStamina < 14)
            graphics.blit(design.background, x, y, 0, 0, 64, 14 - pixelsOfStamina, 64, 14);
        if (pixelsOfStamina > 0)
            graphics.blit(design.foreground, x, y + (14 - pixelsOfStamina), 0, (14 - pixelsOfStamina), 64, pixelsOfStamina, 64, 14);
    }
}
