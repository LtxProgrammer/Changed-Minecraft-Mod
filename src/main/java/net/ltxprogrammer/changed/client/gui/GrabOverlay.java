package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GrabOverlay {
    private static final ResourceLocation GRAB_PROGRESS_BAR_PLAYER = Changed.modResource("textures/gui/grab_progress_bar_player.png");
    private static final ResourceLocation GRAB_PROGRESS_BAR_LATEX = Changed.modResource("textures/gui/grab_progress_bar_latex.png");
    private static final int BAR_WIDTH_PLAYER = 200;
    private static final int BAR_HEIGHT_PLAYER = 32;
    private static final int BAR_WIDTH_LATEX = 182;
    private static final int BAR_HEIGHT_LATEX = 10;

    public static void blit(PoseStack stack, int left, int up, int u0, int v0, int width, int height, int textureWidth, int textureHeight) {
        Gui.blit(stack, left, up, u0, v0, width, height, textureWidth, textureHeight);
    }

    public static void renderBackground(int x, int y, int width, int height, PoseStack stack) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0F);
        blit(stack, x, y, 0, 0, width, height, width, height * 3);
    }

    public static void renderForeground(int x, int y, int width, int height, PoseStack stack, float progress) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0F);
        blit(stack, x, y, 0, height, (int)(progress * width), height, width, height * 3);
    }

    public static void renderSuit(int x, int y, int width, int height, PoseStack stack, float progress) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0F);
        float halfWidth = progress * width * 0.5f;

        if (progress >= 1.0f) {
            blit(stack, x, y, 0, height * 2, width, height, width, height * 3); // Full
        } else {
            int rightOffset = (int)((width * 0.5f) + ((1.0f - progress) * width * 0.5f));
            blit(stack, x, y, 0, height * 2, (int)halfWidth, height, width, height * 3); // Left
            blit(stack, x + rightOffset, y, rightOffset, height * 2, (int)halfWidth, height, width, height * 3); // Right
        }
    }

    public static void renderProgressBarPlayer(PoseStack stack, int screenWidth, int screenHeight) {
        RenderSystem.setShaderTexture(0, GRAB_PROGRESS_BAR_PLAYER);
        int x = (screenWidth / 2) - (BAR_WIDTH_PLAYER / 2);
        int y = (screenHeight / 2) + 20;

        if (Minecraft.getInstance().cameraEntity instanceof LivingEntityDataExtension ext && ext.getGrabbedBy() != null) {
            var grabAbility = AbstractAbility.getAbilityInstance(ext.getGrabbedBy(), ChangedAbilities.GRAB_ENTITY_ABILITY.get());
            if (grabAbility == null) return;
            if (grabAbility.relinquishControl) return;

            renderBackground(x, y, BAR_WIDTH_PLAYER, BAR_HEIGHT_PLAYER, stack);
            renderForeground(x, y, BAR_WIDTH_PLAYER, BAR_HEIGHT_PLAYER, stack, grabAbility.getGrabStrength());
            renderSuit(x, y, BAR_WIDTH_PLAYER, BAR_HEIGHT_PLAYER, stack, grabAbility.suited ? 1.0f : grabAbility.getSuitTransitionProgress());
        }
    }

    public static void renderProgressBarLatex(PoseStack stack, int screenWidth, int screenHeight) {
        RenderSystem.setShaderTexture(0, GRAB_PROGRESS_BAR_LATEX);
        int x = (screenWidth / 2) - (BAR_WIDTH_LATEX / 2);
        int y = screenHeight - 29;

        if (!(Minecraft.getInstance().cameraEntity instanceof LivingEntity livingCameraEntity)) return;

        var grabAbility = AbstractAbility.getAbilityInstance(livingCameraEntity, ChangedAbilities.GRAB_ENTITY_ABILITY.get());
        if (grabAbility == null) return;
        if (grabAbility.relinquishControl) return;

        renderBackground(x, y, BAR_WIDTH_LATEX, BAR_HEIGHT_LATEX, stack);
        renderForeground(x, y, BAR_WIDTH_LATEX, BAR_HEIGHT_LATEX, stack, grabAbility.getGrabStrength());
        renderSuit(x, y, BAR_WIDTH_LATEX, BAR_HEIGHT_LATEX, stack, grabAbility.suited ? 1.0f : grabAbility.getSuitTransitionProgress());
    }

    public static void renderProgressBars(Gui gui, PoseStack stack, int screenWidth, int screenHeight) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        stack.pushPose();
        stack.translate(0.5, 0.0, 0.0);

        renderProgressBarPlayer(stack, screenWidth, screenHeight);

        stack.popPose();

        renderProgressBarLatex(stack, screenWidth, screenHeight);
    }
}
