package net.ltxprogrammer.changed.mixin.gui;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.AbstractRadialScreen;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(EffectRenderingInventoryScreen.class)
public abstract class EffectRenderingInventoryScreenMixin<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    @Unique private static final ResourceLocation LATEX_INVENTORY_LOCATION = Changed.modResource("textures/gui/latex_inventory.png");

    public EffectRenderingInventoryScreenMixin(T menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Inject(method = "renderBackgrounds", at = @At("HEAD"), cancellable = true)
    private void renderBackgrounds(PoseStack poseStack, int x, int height, Iterable<MobEffectInstance> effects, boolean wide, CallbackInfo callback) {
        if (!Changed.config.client.useGoopyInventory.get())
            return;
        ProcessTransfur.ifPlayerLatex(this.minecraft.player, variant -> {
            if (ProcessTransfur.isPlayerOrganic(this.minecraft.player))
                return;

            var colorPair = AbstractRadialScreen.getColors(variant);
            var primary = colorPair.getFirst();
            var secondary = colorPair.getSecond();

            RenderSystem.setShaderTexture(0, LATEX_INVENTORY_LOCATION);
            int i = this.topPos;

            for(MobEffectInstance effect : effects) {
                // Background
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                if (wide)
                    blit(poseStack, x, i, 512, 166, 120, 32, 768, 256);
                else
                    blit(poseStack, x, i, 512, 198, 32, 32, 768, 256);

                // Foreground
                RenderSystem.setShaderColor(primary.red(), primary.green(), primary.blue(), 1.0F);
                if (wide)
                    blit(poseStack, x, i, 0, 166, 120, 32, 768, 256);
                else
                    blit(poseStack, x, i, 0, 198, 32, 32, 768, 256);

                i += height;
            }

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            callback.cancel();
        });
    }
}
