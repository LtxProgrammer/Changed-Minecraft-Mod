package net.ltxprogrammer.changed.client;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.TransfurProgressOverlay;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChangedOverlays {
    public static final IIngameOverlay GOO_ELEMENT = OverlayRegistry.registerOverlayTop(Changed.modResourceStr("goo"), (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        gui.setupOverlayRenderState(true, false);
        TransfurProgressOverlay.renderGooOverlay(gui);
    });
}
