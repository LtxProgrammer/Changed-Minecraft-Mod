package net.ltxprogrammer.changed.mixin.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.LatexAbilityRadialScreen;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin extends GuiComponent {
    @Unique
    private static final ResourceLocation GUI_LATEX_HEARTS = Changed.modResource("textures/gui/latex_hearts.png");

    @Inject(method = "renderHeart", at = @At("HEAD"), cancellable = true)
    private void renderHeart(PoseStack pose, Gui.HeartType type, int x, int y, int texY, boolean blinking, boolean half, CallbackInfo callback) {
        if (type != Gui.HeartType.CONTAINER && type != Gui.HeartType.NORMAL)
            return;

        if (Minecraft.getInstance().getCameraEntity() instanceof Player player) {
            if (ProcessTransfur.isPlayerOrganic(player))
                return;
            ProcessTransfur.ifPlayerLatex(player, variant -> {
                var colors = LatexAbilityRadialScreen.getColors(variant);
                var color = type == Gui.HeartType.NORMAL ? colors.getFirst() : colors.getSecond();
                RenderSystem.setShaderTexture(0, GUI_LATEX_HEARTS);
                RenderSystem.setShaderColor(color.red(), color.green(), color.blue(), 1);
                this.blit(pose, x, y, type.getX(half, blinking), texY, 9, 9);
                RenderSystem.setShaderColor(1, 1, 1, 1);
                this.blit(pose, x, y, type.getX(half, blinking), texY + 9, 9, 9);
                RenderSystem.setShaderTexture(0, Gui.GUI_ICONS_LOCATION);
                callback.cancel();
            });
        }
    }
}
