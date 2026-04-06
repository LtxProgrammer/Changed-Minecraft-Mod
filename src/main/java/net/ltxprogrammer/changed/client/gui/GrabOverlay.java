package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.GrabEntityAbilityInstance;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.util.Color3;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Locale;

@OnlyIn(Dist.CLIENT)
public class GrabOverlay {
    private static final ResourceLocation GRAB_PROGRESS_BAR_PLAYER = Changed.modResource("textures/gui/grab_progress_bar_player.png");
    private static final ResourceLocation GRAB_PROGRESS_BAR_LATEX = Changed.modResource("textures/gui/grab_progress_bar_latex.png");
    private static final ResourceLocation GRAB_ESCAPE_KEYS = Changed.modResource("textures/gui/grab_escape_keys.png");
    private static final int BAR_WIDTH_PLAYER = 200;
    private static final int BAR_HEIGHT_PLAYER = 32;
    private static final int BAR_WIDTH_LATEX = 182;
    private static final int BAR_HEIGHT_LATEX = 10;
    private static final int KEY_SIZE = 16;

    public static void blit(GuiGraphics graphics, ResourceLocation texture, int left, int up, int u0, int v0, int width, int height, int textureWidth, int textureHeight) {
        graphics.blit(texture, left, up, u0, v0, width, height, textureWidth, textureHeight);
    }

    public static void renderBackground(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height, Color3 barColor) {
        graphics.setColor(barColor.red(), barColor.green(), barColor.blue(), 1.0F);
        blit(graphics, texture, x, y, 0, 0, width, height, width, height * 3);
    }

    public static void renderForeground(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height, float progress, Color3 barColor) {
        graphics.setColor(barColor.red(), barColor.green(), barColor.blue(), 1.0F);
        blit(graphics, texture, x, y, 0, height, (int)(progress * width), height, width, height * 3);
    }

    public static void renderSuit(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height, float progress, Color3 barColor) {
        graphics.setColor(barColor.red(), barColor.green(), barColor.blue(), 1.0F);
        float halfWidth = progress * width * 0.5f;

        if (progress >= 1.0f) {
            blit(graphics, texture, x, y, 0, height * 2, width, height, width, height * 3); // Full
        } else {
            int rightOffset = (int)((width * 0.5f) + ((1.0f - progress) * width * 0.5f)) + 1;
            blit(graphics, texture, x, y, 0, height * 2, (int)halfWidth, height, width, height * 3); // Left
            blit(graphics, texture, x + rightOffset, y, rightOffset, height * 2, (int)halfWidth, height, width, height * 3); // Right
        }
    }

    private static Color3 getBrightVariantColor(Pair<Color3, Color3> pair) {
        return new AbstractRadialScreen.ColorScheme(pair.getFirst(), pair.getSecond()).setForegroundToBright().foreground();
    }

    public static void renderProgressBarPlayer(GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
        int x = (screenWidth / 2) - (BAR_WIDTH_PLAYER / 2);
        int y = (screenHeight / 2) + 35;

        if (Minecraft.getInstance().cameraEntity instanceof LivingEntityDataExtension ext && ext.getGrabbedBy() != null) {
            var grabAbility = AbstractAbility.getAbilityInstance(ext.getGrabbedBy(), ChangedAbilities.GRAB_ENTITY_ABILITY.get());
            if (grabAbility == null) return;
            if (grabAbility.grabbedHasControl) return;

            var tfVariant = grabAbility.entity.getSelfVariant();
            Color3 barColor;
            if (tfVariant != null) {
                barColor = getBrightVariantColor(tfVariant.getColors());
            } else
                barColor = Color3.WHITE;

            renderBackground(graphics, GRAB_PROGRESS_BAR_PLAYER, x, y, BAR_WIDTH_PLAYER, BAR_HEIGHT_PLAYER, Color3.WHITE);
            renderForeground(graphics, GRAB_PROGRESS_BAR_PLAYER, x, y, BAR_WIDTH_PLAYER, BAR_HEIGHT_PLAYER, grabAbility.getGrabStrength(partialTicks), barColor);
            renderSuit(graphics, GRAB_PROGRESS_BAR_PLAYER, x, y, BAR_WIDTH_PLAYER, BAR_HEIGHT_PLAYER, grabAbility.getSuitTransitionProgress(partialTicks), barColor);
        }
    }

    public static void renderProgressBarLatex(GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
        int x = (screenWidth / 2) - (BAR_WIDTH_LATEX / 2);
        int y = screenHeight - 29;

        if (!(Minecraft.getInstance().cameraEntity instanceof LivingEntity livingCameraEntity)) return;

        var grabAbility = AbstractAbility.getAbilityInstance(livingCameraEntity, ChangedAbilities.GRAB_ENTITY_ABILITY.get());
        if (grabAbility == null) return;
        if (grabAbility.grabbedEntity == null) return;
        if (grabAbility.grabbedHasControl) return;

        Color3 barColor = getBrightVariantColor(ChangedEntities.getEntityColor(grabAbility.entity.getChangedEntity()));

        renderBackground(graphics, GRAB_PROGRESS_BAR_LATEX, x, y, BAR_WIDTH_LATEX, BAR_HEIGHT_LATEX, Color3.WHITE);
        renderForeground(graphics, GRAB_PROGRESS_BAR_LATEX, x, y, BAR_WIDTH_LATEX, BAR_HEIGHT_LATEX, grabAbility.getGrabStrength(partialTicks), barColor);
        renderSuit(graphics, GRAB_PROGRESS_BAR_LATEX, x, y, BAR_WIDTH_LATEX, BAR_HEIGHT_LATEX, grabAbility.getSuitTransitionProgress(partialTicks), barColor);
    }

    public static void renderEscapeKeyAt(Gui gui, GuiGraphics graphics, int x, int y, AbstractAbilityInstance.KeyReference key, float alpha) {
        if (alpha <= 0.05f)
            return;

        graphics.setColor(1.0f, 1.0f, 1.0f, alpha);
        int keyX, keyY;
        switch (key) {
            case MOVE_FORWARD -> { keyX = 0; keyY = 0; }
            case MOVE_RIGHT -> { keyX = 16; keyY = 0; }
            case MOVE_BACKWARD -> { keyX = 16; keyY = 16; }
            case MOVE_LEFT -> { keyX = 0; keyY = 16; }
            default -> { keyX = 0; keyY = 0; }
        }

        RenderSystem.setShaderTexture(0, GRAB_ESCAPE_KEYS);

        graphics.blit(GRAB_ESCAPE_KEYS, x, y, keyX, keyY, 16, 16, 32, 32);

        var keyName = key.getName(Minecraft.getInstance().level).getString().toUpperCase(Locale.ROOT);
        var font = gui.getFont();

        var keyWidth = font.width(keyName);
        int alphaComponent = (int)(alpha * 255) << 24;
        graphics.drawString(font, keyName, (x + (KEY_SIZE / 2)) - (keyWidth / 2), y + 5, 0x00FFFFFF | alphaComponent);
    }

    private static int animateKeySuccess(float ticksUnpressed) { // slice through bar
        float verticalLerp = 1.0f - Mth.clamp(Mth.map(ticksUnpressed, 0.0f, 15.0f, 0.0f, 1.0f), 0.0f, 1.0f);
        float verticalBounce = Mth.map(verticalLerp * verticalLerp * verticalLerp, 1.0f, 0.0f, -25.0f, 25.0f);

        return (int)verticalBounce;
    }

    private static int animateKeyFail(float ticksUnpressed) {
        return 0;
    }

    public static void renderEscapeKeys(Gui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight, GrabEntityAbilityInstance ability) {
        graphics.pose().pushPose();

        var key = ability.currentEscapeKey;
        var lastKey = ability.lastEscapeKey;

        float ticksUnpressed = ability.ticksUnpressed + partialTicks;

        int x = (screenWidth / 2) - (KEY_SIZE / 2);
        int y = (screenHeight / 2) + 20;

        if (lastKey != null)
            renderEscapeKeyAt(gui, graphics, x, y + animateKeySuccess(ticksUnpressed), lastKey,
                    Mth.clamp(Mth.map(ticksUnpressed, 5.0f, 12.0f, 1.0f, 0.0f), 0.0f, 1.0f));
        if (key != null)
            renderEscapeKeyAt(gui, graphics, x, y - 25, key, 1.0f);

        graphics.pose().popPose();
    }

    public static void renderEscapeKeys(Gui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
        if (Minecraft.getInstance().cameraEntity instanceof LivingEntityDataExtension ext && ext.getGrabbedBy() != null) {
            var grabAbility = AbstractAbility.getAbilityInstance(ext.getGrabbedBy(), ChangedAbilities.GRAB_ENTITY_ABILITY.get());
            if (grabAbility == null) return;
            if (grabAbility.grabbedHasControl) return;

            renderEscapeKeys(gui, graphics, partialTicks, screenWidth, screenHeight, grabAbility);
        }
    }

    public static void renderProgressBars(Gui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        final var stack = graphics.pose();

        stack.pushPose();
        stack.translate(0.5, 0.0, 0.0);

        renderProgressBarPlayer(graphics, partialTicks, screenWidth, screenHeight);
        renderEscapeKeys(gui, graphics, partialTicks, screenWidth, screenHeight);

        stack.popPose();

        renderProgressBarLatex(graphics, partialTicks, screenWidth, screenHeight);
    }
}
