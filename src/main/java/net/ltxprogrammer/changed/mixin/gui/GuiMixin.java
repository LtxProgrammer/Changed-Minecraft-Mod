package net.ltxprogrammer.changed.mixin.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.AbstractRadialScreen;
import net.ltxprogrammer.changed.client.gui.VariantRadialScreen;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Final @Shadow protected Minecraft minecraft;
    @Shadow public int screenWidth;

    @Shadow protected abstract Player getCameraPlayer();

    @Shadow @Final protected static ResourceLocation WIDGETS_LOCATION;
    @Shadow public int screenHeight;

    @Shadow protected abstract void renderSlot(GuiGraphics graphics, int p_168678_, int p_168679_, float p_168680_, Player p_168681_, ItemStack p_168682_, int p_168683_);

    @Unique private static final ResourceLocation GUI_LATEX_HEARTS = Changed.modResource("textures/gui/latex_hearts.png");
    @Unique private static final ResourceLocation LATEX_INVENTORY_LOCATION = Changed.modResource("textures/gui/latex_inventory.png");

    @Inject(method = "renderHeart", at = @At("HEAD"), cancellable = true)
    private void renderHeart(GuiGraphics graphics, Gui.HeartType type, int x, int y, int texY, boolean blinking, boolean half, CallbackInfo callback) {
        if (!Changed.config.client.useGoopyHearts.get())
            return;
        if (type != Gui.HeartType.CONTAINER && type != Gui.HeartType.NORMAL)
            return;

        if (Minecraft.getInstance().getCameraEntity() instanceof Player player) {
            if (ProcessTransfur.isNonGoo(player))
                return;
            ProcessTransfur.ifPlayerTransfurred(player, variant -> {
                var colors = VariantRadialScreen.getColors(variant);
                var color = type == Gui.HeartType.NORMAL ? colors.background() : colors.foreground();
                graphics.setColor(color.red(), color.green(), color.blue(), 1);
                graphics.blit(GUI_LATEX_HEARTS, x, y, type.getX(half, blinking), texY, 9, 9);
                graphics.setColor(1, 1, 1, 1);
                graphics.blit(GUI_LATEX_HEARTS, x, y, type.getX(half, blinking), texY + 9, 9, 9);
                callback.cancel();
            });
        }
    }

    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    protected void renderEffects(GuiGraphics graphics, CallbackInfo callback) {
        if (!Changed.config.client.useGoopyInventory.get())
            return;
        ProcessTransfur.ifPlayerTransfurred(this.minecraft.player, variant -> {
            if (ProcessTransfur.isNonGoo(this.minecraft.player))
                return;

            var colorPair = AbstractRadialScreen.getColors(variant);

            Collection<MobEffectInstance> collection = this.minecraft.player.getActiveEffects();
            if (!collection.isEmpty()) {
                Screen $$4 = this.minecraft.screen;
                if ($$4 instanceof EffectRenderingInventoryScreen) {
                    EffectRenderingInventoryScreen effectrenderinginventoryscreen = (EffectRenderingInventoryScreen) $$4;
                    if (effectrenderinginventoryscreen.canSeeEffects()) {
                        return;
                    }
                }

                RenderSystem.enableBlend();
                int j1 = 0;
                int k1 = 0;
                MobEffectTextureManager mobeffecttexturemanager = this.minecraft.getMobEffectTextures();
                List<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());

                for (MobEffectInstance mobeffectinstance : Ordering.natural().reverse().sortedCopy(collection)) {
                    MobEffect mobeffect = mobeffectinstance.getEffect();
                    var renderer = net.minecraftforge.client.extensions.common.IClientMobEffectExtensions.of(mobeffectinstance);
                    if (!renderer.isVisibleInGui(mobeffectinstance)) continue;
                    // Rebind in case previous renderHUDEffect changed texture
                    if (mobeffectinstance.showIcon()) {
                        int i = this.screenWidth;
                        int j = 1;
                        if (this.minecraft.isDemo()) {
                            j += 15;
                        }

                        if (mobeffect.isBeneficial()) {
                            ++j1;
                            i -= 25 * j1;
                        } else {
                            ++k1;
                            i -= 25 * k1;
                            j += 26;
                        }

                        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                        float f = 1.0F;
                        if (mobeffectinstance.isAmbient()) {
                            graphics.blit(LATEX_INVENTORY_LOCATION, i, j, 165 + 512, 166, 24, 24, 768, 256);
                            graphics.setColor(colorPair.background().red(), colorPair.background().green(), colorPair.background().blue(), 1.0F);
                            graphics.blit(LATEX_INVENTORY_LOCATION, i, j, 165, 166, 24, 24, 768, 256);
                        } else {
                            graphics.blit(LATEX_INVENTORY_LOCATION, i, j, 141 + 512, 166, 24, 24, 768, 256);
                            graphics.setColor(colorPair.background().red(), colorPair.background().green(), colorPair.background().blue(), 1.0F);
                            graphics.blit(LATEX_INVENTORY_LOCATION, i, j, 141, 166, 24, 24, 768, 256);
                            if (mobeffectinstance.getDuration() <= 200) {
                                int k = 10 - mobeffectinstance.getDuration() / 20;
                                f = Mth.clamp((float) mobeffectinstance.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + Mth.cos((float) mobeffectinstance.getDuration() * (float) Math.PI / 5.0F) * Mth.clamp((float) k / 10.0F * 0.25F, 0.0F, 0.25F);
                            }
                        }
                        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

                        TextureAtlasSprite textureatlassprite = mobeffecttexturemanager.get(mobeffect);
                        int l = i;
                        int i1 = j;
                        float f1 = f;
                        list.add(() -> {
                            graphics.setColor(1.0F, 1.0F, 1.0F, f1);
                            graphics.blit(l + 3, i1 + 3, 0, 18, 18, textureatlassprite);
                        });
                        renderer.renderGuiIcon(mobeffectinstance, (Gui)(Object)this, graphics, i, j, 0, f);
                    }
                }

                list.forEach(Runnable::run);
                graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            }

            callback.cancel();
        });
    }

    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    protected void renderHotbar(float partialTicks, GuiGraphics graphics, CallbackInfo callback) {
        ProcessTransfur.ifPlayerTransfurred(this.minecraft.player, variant -> {
            if (!variant.getItemUseMode().showHotbar) {
                callback.cancel();
                
                Player player = this.getCameraPlayer();
                if (player != null) {
                    graphics.setColor(1.0F, 1.0F, 1.0F, 0.25F);
                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    graphics.blit(WIDGETS_LOCATION, (this.screenWidth / 2) - 91, this.screenHeight - 22, 0, 0, 182, 22);
                }
            }
        });
    }

    @Inject(method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V", at = @At("HEAD"), cancellable = true, remap = false)
    public void renderSelectedItemName(GuiGraphics graphics, int yShift, CallbackInfo callback) {
        ProcessTransfur.ifPlayerTransfurred(this.minecraft.player, variant -> {
            if (!variant.getItemUseMode().showHotbar)
                callback.cancel();
        });
    }
}
