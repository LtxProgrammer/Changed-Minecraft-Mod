package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AbilityOverlay {
    /*
     ----Goo--------------Organic------
     |   back         |               |
     |   ready        |               |
     |   release      |               |
     ----------------------------------
     */
    private static final ResourceLocation ABILITY_BACKGROUNDS = Changed.modResource("textures/gui/ability_backgrounds.png");

    public static void blit(PoseStack stack, int left, int up, int u0, int v0, int width, int height, int textureWidth, int textureHeight) {
        Gui.blit(stack, left, up, u0, v0, width, height, textureWidth, textureHeight);
    }

    public static void renderBackground(int x, int y, PoseStack stack, ChangedParticles.Color3 primary, ChangedParticles.Color3 secondary, Player player, LatexVariantInstance<?> variant, AbstractAbilityInstance selected) {
        RenderSystem.setShaderTexture(0, ABILITY_BACKGROUNDS);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(primary.red(), primary.green(), primary.blue(), 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        var controller = selected.getController();
        int ready = (int)(controller.getProgressReady() * 32);
        int active = ready >= 32 ? (int)(controller.getProgressActive() * 32) : 0;

        int gooOrNot = variant.getParent().getEntityType().is(ChangedTags.EntityTypes.ORGANIC_LATEX) ? 32 : 0;
        blit(stack, x, y, gooOrNot, 0, 32, 32, 64, 96); // back
        if (ready > 0)
            blit(stack, x, y + (ready - 32), gooOrNot, 32, 32, ready, 64, 96); // ready
        if (active > 0) {
            RenderSystem.setShaderColor(secondary.red(), secondary.green(), secondary.blue(), 1.0F);
            blit(stack, x, y + (active - 32), gooOrNot, 64, 32, active, 64, 96); // active
        }
    }

    public static void renderForeground(int x, int y, PoseStack stack, ChangedParticles.Color3 back, ChangedParticles.Color3 fore, Player player, LatexVariantInstance<?> variant, AbstractAbilityInstance selected) {
        RenderSystem.setShaderTexture(0, selected.ability.getTexture(player, variant));
        RenderSystem.setShaderColor(0, 0, 0, 0.5f); // Render ability shadow
        blit(stack, x, y + 4, 0, 0, 32, 32, 32, 32);
        float minRed = Math.max(fore.red(), 0.125f);
        float minGreen = Math.max(fore.green(), 0.125f);
        float minBlue = Math.max(fore.blue(), 0.125f);
        RenderSystem.setShaderColor(minRed, minGreen, minBlue, 1.0F);
        blit(stack, x, y, 0, 0, 32, 32, 32, 32);
    }

    public static void renderSelectedAbility(Gui gui, PoseStack stack, int screenWidth, int screenHeight) {
        ProcessTransfur.ifPlayerLatex(Util.playerOrNull(Minecraft.getInstance().cameraEntity), (player, variant) -> {
            var ability = variant.getSelectedAbility();
            if (ability == null)
                return;
            var color = AbstractRadialScreen.getColors(variant);
            var backgroundColor = color.getFirst();
            var foregroundColor = color.getSecond();

            if (backgroundColor.brightness() > foregroundColor.brightness()) {
                backgroundColor = color.getSecond();
                foregroundColor = color.getFirst();
            }

            if (foregroundColor.brightness() - backgroundColor.brightness() < 0.125f) {
                foregroundColor = foregroundColor.add(0.125f);
            }

            renderBackground(10, screenHeight - 42, stack, backgroundColor, foregroundColor, player, variant, ability);
            renderForeground(15, screenHeight - 47, stack, backgroundColor, foregroundColor, player, variant, ability);
        });
    }
}
