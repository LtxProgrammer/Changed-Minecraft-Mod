package net.ltxprogrammer.changed.mixin.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.AbstractRadialScreen;
import net.ltxprogrammer.changed.client.gui.LatexAbilityRadialScreen;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.HumanoidArm;
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
public abstract class GuiMixin extends GuiComponent {
    @Final @Shadow protected Minecraft minecraft;
    @Shadow public int screenWidth;

    @Shadow protected abstract Player getCameraPlayer();

    @Shadow @Final protected static ResourceLocation WIDGETS_LOCATION;
    @Shadow public int screenHeight;

    @Shadow protected abstract void renderSlot(int p_168678_, int p_168679_, float p_168680_, Player p_168681_, ItemStack p_168682_, int p_168683_);

    @Unique private static final ResourceLocation GUI_LATEX_HEARTS = Changed.modResource("textures/gui/latex_hearts.png");
    @Unique private static final ResourceLocation LATEX_INVENTORY_LOCATION = Changed.modResource("textures/gui/latex_inventory.png");

    @Inject(method = "renderHeart", at = @At("HEAD"), cancellable = true)
    private void renderHeart(PoseStack pose, Gui.HeartType type, int x, int y, int texY, boolean blinking, boolean half, CallbackInfo callback) {
        if (!Changed.config.client.useGoopyHearts.get())
            return;
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

    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    protected void renderEffects(PoseStack poseStack, CallbackInfo callback) {
        if (!Changed.config.client.useGoopyInventory.get())
            return;
        ProcessTransfur.ifPlayerLatex(this.minecraft.player, variant -> {
            if (ProcessTransfur.isPlayerOrganic(this.minecraft.player))
                return;

            var colorPair = AbstractRadialScreen.getColors(variant);
            var primary = colorPair.getFirst();
            var secondary = colorPair.getSecond();

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
                RenderSystem.setShaderTexture(0, LATEX_INVENTORY_LOCATION);

                for (MobEffectInstance mobeffectinstance : Ordering.natural().reverse().sortedCopy(collection)) {
                    MobEffect mobeffect = mobeffectinstance.getEffect();
                    net.minecraftforge.client.EffectRenderer renderer = net.minecraftforge.client.RenderProperties.getEffectRenderer(mobeffectinstance);
                    if (!renderer.shouldRenderHUD(mobeffectinstance)) continue;
                    // Rebind in case previous renderHUDEffect changed texture
                    RenderSystem.setShaderTexture(0, LATEX_INVENTORY_LOCATION);
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

                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                        float f = 1.0F;
                        if (mobeffectinstance.isAmbient()) {
                            blit(poseStack, i, j, 165 + 512, 166, 24, 24, 768, 256);
                            RenderSystem.setShaderColor(primary.red(), primary.green(), primary.blue(), 1.0F);
                            blit(poseStack, i, j, 165, 166, 24, 24, 768, 256);
                        } else {
                            blit(poseStack, i, j, 141 + 512, 166, 24, 24, 768, 256);
                            RenderSystem.setShaderColor(primary.red(), primary.green(), primary.blue(), 1.0F);
                            blit(poseStack, i, j, 141, 166, 24, 24, 768, 256);
                            if (mobeffectinstance.getDuration() <= 200) {
                                int k = 10 - mobeffectinstance.getDuration() / 20;
                                f = Mth.clamp((float) mobeffectinstance.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + Mth.cos((float) mobeffectinstance.getDuration() * (float) Math.PI / 5.0F) * Mth.clamp((float) k / 10.0F * 0.25F, 0.0F, 0.25F);
                            }
                        }
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                        TextureAtlasSprite textureatlassprite = mobeffecttexturemanager.get(mobeffect);
                        int l = i;
                        int i1 = j;
                        float f1 = f;
                        list.add(() -> {
                            RenderSystem.setShaderTexture(0, textureatlassprite.atlas().location());
                            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f1);
                            blit(poseStack, l + 3, i1 + 3, this.getBlitOffset(), 18, 18, textureatlassprite);
                        });
                        renderer.renderHUDEffect(mobeffectinstance, this, poseStack, i, j, this.getBlitOffset(), f);
                    }
                }

                list.forEach(Runnable::run);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }

            callback.cancel();
        });
    }

    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    protected void renderHotbar(float partialTicks, PoseStack pose, CallbackInfo callback) {
        ProcessTransfur.ifPlayerLatex(this.minecraft.player, variant -> {
            if (!variant.getParent().itemUseMode.showHotbar) {
                callback.cancel();
                
                Player player = this.getCameraPlayer();
                if (player != null) {
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
                    int i = this.screenWidth / 2;
                    int j = this.getBlitOffset();
                    this.setBlitOffset(-90);
                    this.blit(pose, i - 91, this.screenHeight - 22, 0, 0, 182, 22);

                    this.setBlitOffset(j);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    int i1 = 1;

                    for(int j1 = 0; j1 < 9; ++j1) {
                        int k1 = i - 90 + j1 * 20 + 2;
                        int l1 = this.screenHeight - 16 - 3;
                        this.renderSlot(k1, l1, partialTicks, player, player.getInventory().items.get(j1), i1++);
                    }

                    RenderSystem.disableBlend();
                }
            }
        });
    }

    @Inject(method = "renderSelectedItemName", at = @At("HEAD"), cancellable = true)
    public void renderSelectedItemName(PoseStack pose, CallbackInfo callback) {
        ProcessTransfur.ifPlayerLatex(this.minecraft.player, variant -> {
            if (!variant.getParent().itemUseMode.showHotbar)
                callback.cancel();
        });
    }
}
