package net.ltxprogrammer.changed.client;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.AbilityOverlay;
import net.ltxprogrammer.changed.client.gui.GrabOverlay;
import net.ltxprogrammer.changed.client.gui.TransfurProgressOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ChangedOverlays {
    public static final IIngameOverlay GOO_ELEMENT = OverlayRegistry.registerOverlayTop(Changed.modResourceStr("goo"), (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        gui.setupOverlayRenderState(true, false);
        TransfurProgressOverlay.renderGooOverlay(gui, screenWidth, screenHeight);
    });
    public static final IIngameOverlay ABILITY_ELEMENT = OverlayRegistry.registerOverlayTop(Changed.modResourceStr("ability"), (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        gui.setupOverlayRenderState(true, false);
        AbilityOverlay.renderSelectedAbility(gui, poseStack, screenWidth, screenHeight);
    });
    public static final IIngameOverlay GRABBED_ELEMENT = OverlayRegistry.registerOverlayAbove(ForgeIngameGui.EXPERIENCE_BAR_ELEMENT, Changed.modResourceStr("grabbed"), GrabOverlay::renderProgressBars);
}
