package net.ltxprogrammer.changed.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.*;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.fluid.TransfurGas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
public class ChangedOverlays {
    protected static final ResourceLocation VIGNETTE_LOCATION = ResourceLocation.parse("textures/misc/vignette.png");

    public static final ResourceLocation DANGER_OVERLAY = Changed.modResource("danger");
    public static final ResourceLocation ABILITY_OVERLAY = Changed.modResource("ability");
    public static final ResourceLocation GRABBED_OVERLAY = Changed.modResource("grabbed");
    public static final ResourceLocation GAS_VFX_OVERLAY = Changed.modResource("gas_vfx");
    public static final ResourceLocation VARIANT_BLINDNESS_OVERLAY = Changed.modResource("variant_blindness");
    public static final ResourceLocation FLIGHT_STAMINA_OVERLAY = Changed.modResource("flight_stamina");

    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll(DANGER_OVERLAY.getPath(), (gui, graphics, partialTick, screenWidth, screenHeight) -> {
            gui.setupOverlayRenderState(true, false);
            TransfurProgressOverlay.renderDangerOverlay(gui, graphics, partialTick, screenWidth, screenHeight);
        });
        event.registerAbove(DANGER_OVERLAY, ABILITY_OVERLAY.getPath(), (gui, graphics, partialTick, screenWidth, screenHeight) -> {
            gui.setupOverlayRenderState(true, false);
            AbilityOverlay.renderSelectedAbility(gui, graphics, partialTick, screenWidth, screenHeight);
        });
        event.registerAbove(VanillaGuiOverlay.EXPERIENCE_BAR.id(), GRABBED_OVERLAY.getPath(), GrabOverlay::renderProgressBars);
        event.registerAbove(VanillaGuiOverlay.VIGNETTE.id(), GAS_VFX_OVERLAY.getPath(), (gui, graphics, partialTick, screenWidth, screenHeight) -> {
            var cameraEntity = Minecraft.getInstance().cameraEntity;

            if (cameraEntity instanceof LivingEntityDataExtension ext) {
                ext.isEyeInGas(TransfurGas.class).map(TransfurGas::getColor).ifPresent(color -> {
                    RenderSystem.disableDepthTest();
                    RenderSystem.depthMask(false);
                    RenderSystem.enableBlend();
                    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                    graphics.setColor(color.red(), color.green(), color.blue(), 1.0F);

                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    RenderSystem.setShaderTexture(0, VIGNETTE_LOCATION);
                    Tesselator tesselator = Tesselator.getInstance();
                    BufferBuilder bufferbuilder = tesselator.getBuilder();
                    bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                    bufferbuilder.vertex(0.0D, screenHeight, -90.0D).uv(0.0F, 1.0F).endVertex();
                    bufferbuilder.vertex(screenWidth, screenHeight, -90.0D).uv(1.0F, 1.0F).endVertex();
                    bufferbuilder.vertex(screenWidth, 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
                    bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();
                    tesselator.end();
                    RenderSystem.depthMask(true);
                    RenderSystem.enableDepthTest();
                    graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                    RenderSystem.disableBlend();
                    RenderSystem.defaultBlendFunc();
                });
            }
        });
        event.registerBelowAll(VARIANT_BLINDNESS_OVERLAY.getPath(), VariantBlindnessOverlay::render);
        event.registerAbove(VanillaGuiOverlay.EXPERIENCE_BAR.id(), FLIGHT_STAMINA_OVERLAY.getPath(), FlightStaminaOverlay::render);
    }
}
