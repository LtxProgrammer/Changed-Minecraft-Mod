package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.client.ChangedClient;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.util.Transition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
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

    public static void renderBackground(int x, int y, GuiGraphics graphics, AbstractRadialScreen.ColorScheme scheme, Player player, TransfurVariantInstance<?> variant, AbstractAbilityInstance selected) {
        RenderSystem.setShaderTexture(0, ABILITY_BACKGROUNDS);
        RenderSystem.enableDepthTest();
        graphics.setColor(scheme.background().red(), scheme.background().green(), scheme.background().blue(), 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        var controller = selected.getController();
        int cool = (int)(controller.coolDownPercent() * 32);
        int active = cool >= 32 ? (int)(controller.getProgressActive() * 32) : 0;

        int gooOrNot = variant.getParent().getEntityType().is(ChangedTags.EntityTypes.LATEX) ? 0 : 32;
        graphics.blit(ABILITY_BACKGROUNDS, x, y, gooOrNot, 0, 32, 32, 64, 128); // back
        if (cool > 0)
            graphics.blit(ABILITY_BACKGROUNDS, x, y + (32 - cool), gooOrNot, 32 + (32 - cool), 32, cool, 64, 128); // ready
        if (active > 0) {
            graphics.setColor(scheme.foreground().red(), scheme.foreground().green(), scheme.foreground().blue(), 1.0F);
            graphics.blit(ABILITY_BACKGROUNDS, x, y + (32 - active), gooOrNot, 64 + (32 - active), 32, active, 64, 128); // active
        }

        if (selected.canUse() && !controller.isCoolingDown()) {
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            graphics.blit(ABILITY_BACKGROUNDS, x, y, gooOrNot, 96, 32, 32, 64, 128); // available
        }
    }

    public static void renderForeground(int x, int y, GuiGraphics graphics, AbstractRadialScreen.ColorScheme scheme, Player player, TransfurVariantInstance<?> variant, AbstractAbilityInstance selected) {
        ChangedClient.abilityRenderer.getOrThrow().renderAbility(
                graphics,
                player,
                selected,
                x,
                y,
                32,
                1.0f,
                true,
                0
        );
        ChangedClient.abilityRenderer.getOrThrow().renderGuiAbilityDecorations(
                graphics,
                Minecraft.getInstance().font,
                selected,
                x,
                y,
                32
        );
    }

    public static void renderSelectedAbility(Gui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        var player = EntityUtil.playerOrNull(Minecraft.getInstance().cameraEntity);
        var variant = ProcessTransfur.getPlayerTransfurVariant(player);
        if (variant == null)
            return;
        if (variant.isTemporaryFromSuit())
            return;
        if (!variant.shouldApplyAbilities())
            return;

        int offset = (int)(Transition.easeInOutSine(Mth.clamp(
                Mth.map(variant.getTicksSinceLastAbilityActivity() + partialTick, 100.0f, 130.0f, 0.0f, 1.0f),
                0.0f, 1.0f)) * 60.0f);
        if (offset >= 59)
            return;
        var color = AbstractRadialScreen.getColors(variant).setForegroundToBright();

        variant.abilityHandler.visitSelected((index, totalCount, key, ability, abilityInstance) -> {
            if (ability == null || abilityInstance == null || abilityInstance.getUseType() == AbstractAbility.UseType.MENU)
                return;

            renderBackground(10 - offset, screenHeight - 42 - 48 * index, graphics, color, player, variant, abilityInstance);
            renderForeground(15 - offset, screenHeight - 47 - (48 * index), graphics, color, player, variant, abilityInstance);
        });
    }
}
