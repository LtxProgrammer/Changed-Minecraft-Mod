package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractLatex;
import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
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

    public static void renderBackground(int x, int y, PoseStack stack, AbstractRadialScreen.ColorScheme scheme, Player player, LatexVariantInstance<?> variant, AbstractAbilityInstance selected) {
        RenderSystem.setShaderTexture(0, ABILITY_BACKGROUNDS);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(scheme.background().red(), scheme.background().green(), scheme.background().blue(), 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        var controller = selected.getController();
        int cool = selected.canUse() ? (int)(controller.coolDownPercent() * 32) : 0;
        int active = cool >= 32 ? (int)(controller.getProgressActive() * 32) : 0;

        int gooOrNot = variant.getParent().getEntityType().is(ChangedTags.EntityTypes.ORGANIC_LATEX) ? 32 : 0;
        blit(stack, x, y, gooOrNot, 0, 32, 32, 64, 96); // back
        if (cool > 0)
            blit(stack, x, y + (32 - cool), gooOrNot, 32 + (32 - cool), 32, cool, 64, 96); // ready
        if (active > 0) {
            RenderSystem.setShaderColor(scheme.foreground().red(), scheme.foreground().green(), scheme.foreground().blue(), 1.0F);
            blit(stack, x, y + (32 - active), gooOrNot, 64 + (32 - active), 32, active, 64, 96); // active
        }
    }

    public static void renderForeground(int x, int y, PoseStack stack, AbstractRadialScreen.ColorScheme scheme, Player player, LatexVariantInstance<?> variant, AbstractAbilityInstance selected) {
        RenderSystem.setShaderTexture(0, selected.ability.getTexture(IAbstractLatex.forPlayer(player)));
        RenderSystem.setShaderColor(0, 0, 0, 0.5f); // Render ability shadow
        blit(stack, x, y + 4, 0, 0, 32, 32, 32, 32);
        RenderSystem.setShaderColor(scheme.foreground().red(), scheme.foreground().green(), scheme.foreground().blue(), 1.0F);
        blit(stack, x, y, 0, 0, 32, 32, 32, 32);
    }

    public static void renderSelectedAbility(Gui gui, PoseStack stack, int screenWidth, int screenHeight) {
        ProcessTransfur.ifPlayerLatex(EntityUtil.playerOrNull(Minecraft.getInstance().cameraEntity), (player, variant) -> {
            var ability = variant.getSelectedAbility();
            if (ability == null || ability.getUseType() == AbstractAbility.UseType.MENU)
                return;
            var color = AbstractRadialScreen.getColors(variant).setForegroundToBright();

            renderBackground(10, screenHeight - 42, stack, color, player, variant, ability);
            renderForeground(15, screenHeight - 47, stack, color, player, variant, ability);
        });
    }
}
