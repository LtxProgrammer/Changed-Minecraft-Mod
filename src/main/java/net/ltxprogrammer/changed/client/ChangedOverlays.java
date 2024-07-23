package net.ltxprogrammer.changed.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.AbilityOverlay;
import net.ltxprogrammer.changed.client.gui.GrabOverlay;
import net.ltxprogrammer.changed.client.gui.TransfurProgressOverlay;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.fluid.TransfurGas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ChangedOverlays {
    protected static final ResourceLocation VIGNETTE_LOCATION = new ResourceLocation("textures/misc/vignette.png");

    public static final IIngameOverlay DANGER_ELEMENT = OverlayRegistry.registerOverlayTop(Changed.modResourceStr("danger"), (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        gui.setupOverlayRenderState(true, false);
        TransfurProgressOverlay.renderDangerOverlay(gui, poseStack, partialTick, screenWidth, screenHeight);
    });
    public static final IIngameOverlay ABILITY_ELEMENT = OverlayRegistry.registerOverlayTop(Changed.modResourceStr("ability"), (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        gui.setupOverlayRenderState(true, false);
        AbilityOverlay.renderSelectedAbility(gui, poseStack, partialTick, screenWidth, screenHeight);
    });
    public static final IIngameOverlay GRABBED_ELEMENT = OverlayRegistry.registerOverlayAbove(ForgeIngameGui.EXPERIENCE_BAR_ELEMENT, Changed.modResourceStr("grabbed"), GrabOverlay::renderProgressBars);
    public static final IIngameOverlay GAS_VFX_ELEMENT = OverlayRegistry.registerOverlayAbove(ForgeIngameGui.VIGNETTE_ELEMENT, Changed.modResourceStr("gas_vfx"), ((gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        var cameraEntity = Minecraft.getInstance().cameraEntity;

        if (cameraEntity instanceof LivingEntityDataExtension ext) {
            ext.isEyeInGas(TransfurGas.class).map(TransfurGas::getColor).ifPresent(color -> {
                RenderSystem.disableDepthTest();
                RenderSystem.depthMask(false);
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                RenderSystem.setShaderColor(color.red(), color.green(), color.blue(), 1.0F);

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
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.defaultBlendFunc();
            });
        }
    }));
}
