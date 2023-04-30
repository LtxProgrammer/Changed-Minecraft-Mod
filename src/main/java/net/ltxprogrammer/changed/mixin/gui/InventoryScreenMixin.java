package net.ltxprogrammer.changed.mixin.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.AbstractRadialScreen;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> implements RecipeUpdateListener {
    @Shadow private float xMouse;
    @Shadow private float yMouse;
    @Unique private static final ResourceLocation LATEX_INVENTORY_LOCATION = Changed.modResource("textures/gui/latex_inventory.png");

    public InventoryScreenMixin(InventoryMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Inject(method = "renderLabels", at = @At("HEAD"), cancellable = true)
    protected void renderLabels(PoseStack p_98889_, int p_98890_, int p_98891_, CallbackInfo callback) {
        if (this.minecraft == null)
            return;

        ProcessTransfur.ifPlayerLatex(this.minecraft.player, variant -> {
            if (ProcessTransfur.isPlayerOrganic(this.minecraft.player))
                return;

            var colorPair = AbstractRadialScreen.getColors(variant);
            var primary = colorPair.getFirst();
            var secondary = colorPair.getSecond();
            int textColor = primary.brightness() > 0.5f ? 0x0 : 0xffffff;
            if (Mth.abs(secondary.brightness() - primary.brightness()) > 0.1f)
                textColor = secondary.toInt();

            this.font.draw(p_98889_, this.title, (float) this.titleLabelX, (float) this.titleLabelY, textColor);
            callback.cancel();
        });
    }

    @Inject(method = "renderBg", at = @At("HEAD"), cancellable = true)
    protected void renderBg(PoseStack pose, float p_98871_, int p_98872_, int p_98873_, CallbackInfo callback) {
        if (this.minecraft == null)
            return;

        ProcessTransfur.ifPlayerLatex(this.minecraft.player, variant -> {
            if (ProcessTransfur.isPlayerOrganic(this.minecraft.player))
                return;

            int i = this.leftPos;
            int j = this.topPos;

            var colorPair = AbstractRadialScreen.getColors(variant);
            var primary = colorPair.getFirst();
            var secondary = colorPair.getSecond();

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, LATEX_INVENTORY_LOCATION);

            RenderSystem.setShaderColor(secondary.red(), secondary.green(), secondary.blue(), 1.0F);
            blit(pose, i, j, 256, 0, this.imageWidth, this.imageHeight, 768, 256);
            RenderSystem.setShaderColor(primary.red(), primary.green(), primary.blue(), 1.0F);
            blit(pose, i, j, 0, 0, this.imageWidth, this.imageHeight, 768, 256);

            InventoryScreen.renderEntityInInventory(i + 51, j + 75, 30, (float)(i + 51) - this.xMouse, (float)(j + 75 - 50) - this.yMouse, this.minecraft.player);

            callback.cancel();
        });
    }
}
