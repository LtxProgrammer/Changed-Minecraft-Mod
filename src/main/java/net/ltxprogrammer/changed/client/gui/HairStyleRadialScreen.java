package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.FormRenderHandler;
import net.ltxprogrammer.changed.client.renderer.LatexHumanoidRenderer;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.network.VariantAbilityActivate;
import net.ltxprogrammer.changed.world.inventory.AbilityRadialMenu;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.ltxprogrammer.changed.world.inventory.HairStyleRadialMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class HairStyleRadialScreen extends AbstractContainerScreen<HairStyleRadialMenu> {
    private final static HashMap<String, Object> guistate = ComputerMenu.guistate;

    public final HairStyleRadialMenu menu;
    public final LatexVariant<?> variant;
    public final List<HairStyle> styles;
    private int tickCount = 0;
    private final ChangedParticles.Color3 primaryColor;
    private final ChangedParticles.Color3 secondaryColor;

    public HairStyleRadialScreen(HairStyleRadialMenu menu, Inventory inventory, Component text) {
        super(menu, inventory, text);
        this.imageWidth = 0;
        this.imageHeight = 0;
        this.menu = menu;
        this.variant = menu.variant;
        this.styles = variant.getLatexEntity().getValidHairStyles();

        var colors = getColors(variant);
        this.primaryColor = colors.getA();
        this.secondaryColor = colors.getB();
    }

    public static void renderEntityHeadWithHair(int x, int y, int scale, float xRot, float yRot, LatexEntity entity) {
        float f = (float)Math.atan((double)(xRot / 40.0F));
        float f1 = (float)Math.atan((double)(yRot / 40.0F));
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.translate((double)x, (double)y, 1050.0D);
        posestack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack posestack1 = new PoseStack();
        posestack1.translate(0.0D, 0.0D, 1000.0D);
        posestack1.scale((float)scale, (float)scale, (float)scale);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
        quaternion.mul(quaternion1);
        posestack1.mulPose(quaternion);
        float f2 = entity.yBodyRot;
        float f3 = entity.getYRot();
        float f4 = entity.getXRot();
        float f5 = entity.yHeadRotO;
        float f6 = entity.yHeadRot;
        entity.yBodyRot = 180.0F + f * 20.0F;
        entity.setYRot(180.0F + f * 40.0F);
        entity.setXRot(-f1 * 20.0F);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion1.conj();
        dispatcher.overrideCameraOrientation(quaternion1);
        dispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
            var renderer = dispatcher.getRenderer(entity);
            if (renderer instanceof LatexHumanoidRenderer latexRenderer) {
                FormRenderHandler.renderModelPartWithTexture(
                        latexRenderer.getModel(entity).getHead(),
                        new PoseStack(),
                        posestack1, multibuffersource$buffersource.getBuffer(latexRenderer.getModel().renderType(latexRenderer.getTextureLocation(entity))),
                        15728880, 1.0F);
                latexRenderer.getHairLayer().render(posestack1,
                        multibuffersource$buffersource, 0, entity, 0, 0, 0, 0, 0, 0);
            }
        });
        multibuffersource$buffersource.endBatch();
        dispatcher.setRenderShadow(true);
        entity.yBodyRot = f2;
        entity.setYRot(f3);
        entity.setXRot(f4);
        entity.yHeadRotO = f5;
        entity.yHeadRot = f6;
        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }

    public static Pair<ChangedParticles.Color3, ChangedParticles.Color3> getColors(LatexVariant<?> variant) {
        var ints = ChangedEntities.getEntityColor(variant.getEntityType().getRegistryName());
        return new Pair<>(
                ChangedParticles.Color3.fromInt(ints.getA()),
                ChangedParticles.Color3.fromInt(ints.getB()));
    }

    private static final double RADIAL_DISTANCE = 90.0;

    private static double calcOffset(int section) {
        return section % 2 == 0 ? 0.04 : -0.04;
    }

    @Nullable
    public HairStyle getHairStyleAt(int mouseX, int mouseY) {
        for (int sect = 0; sect < 8 && sect < styles.size() && sect <= tickCount; sect++) {
            double dbl = (sect + 0.5 + calcOffset(sect)) / 8.0;
            int x = (int)(Math.sin(dbl * Math.PI * 2.0) * RADIAL_DISTANCE);
            int y = -(int)(Math.cos(dbl * Math.PI * 2.0) * RADIAL_DISTANCE);

            int minX = x - 24 + this.leftPos;
            int maxX = minX + 48;
            int minY = y - 24 + this.topPos;
            int maxY = minY + 48;
            if (mouseX >= minX && mouseX <= maxX &&
                mouseY >= minY && mouseY <= maxY)
                return styles.get(sect);
        }

        return null;
    }

    @Override
    public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        this.renderBg(ms, partialTicks, mouseX, mouseY);

        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    @Override
    protected void renderBg(PoseStack ms, float partialTicks, int gx, int gy) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Render radial bg
        for (int sect = 0; sect < 8 && sect <= tickCount; sect++) {
            double dbl = (sect + 0.5 + calcOffset(sect)) / 8.0;
            int x = (int)(Math.sin(dbl * Math.PI * 2.0) * RADIAL_DISTANCE);
            int y = -(int)(Math.cos(dbl * Math.PI * 2.0) * RADIAL_DISTANCE);

            boolean enabled = false;
            if (sect < styles.size() && menu.variant.abilityInstances.containsKey(styles.get(sect))) {
                enabled = menu.variant.abilityInstances.get(styles.get(sect)).canUse();
            }

            RenderSystem.setShaderColor(primaryColor.red(), primaryColor.green(), primaryColor.blue(), enabled ? 1 : 0.5f);
            RenderSystem.setShaderTexture(0, Changed.modResource("textures/gui/radial/" + sect + ".png"));
            this.blit(ms, x - 32 + this.leftPos, y - 32 + this.topPos, 0, 0, 64, 64, 64, 64);
        }

        for (int sect = 0; sect < 8 && sect < styles.size() && sect <= tickCount; sect++) {
            double dbl = (sect + 0.5 + calcOffset(sect)) / 8.0;
            int x = (int)(Math.sin(dbl * Math.PI * 2.0) * RADIAL_DISTANCE);
            int y = -(int)(Math.cos(dbl * Math.PI * 2.0) * RADIAL_DISTANCE);

            boolean enabled = false;
            if (menu.variant.abilityInstances.containsKey(styles.get(sect))) {
                enabled = menu.variant.abilityInstances.get(styles.get(sect)).canUse();
            }

            renderEntityHeadWithHair(x - 24 + this.leftPos, y - 24 + this.topPos, 40, 40, 0, variant.getLatexEntity());
        }

        RenderSystem.setShaderColor(1, 1, 1, 1);
        InventoryScreen.renderEntityInInventory(this.leftPos, this.topPos + 50, 38, (float)(this.leftPos) - gx, (float)(this.topPos) - gy, variant.getLatexEntity());

        RenderSystem.disableBlend();
    }

    /*@Override
    public boolean mouseClicked(double p_97748_, double p_97749_, int p_97750_) {
        var ability = getHairStyleAt((int)p_97748_, (int)p_97749_);
        if (ability != null && menu.variant.abilityInstances.get(ability).canUse()) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.minecraft.player.closeContainer();
            Changed.PACKET_HANDLER.sendToServer(new VariantAbilityActivate(ability));
            return true;
        }

        return false;
    }*/

    /*@Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.player.closeContainer();
            return true;
        }

        if (key == GLFW.GLFW_KEY_0) { // Due to keyboard positioning, 0 will be treated as 10
            if (10 < styles.size()) {
                this.minecraft.player.closeContainer();
                Changed.PACKET_HANDLER.sendToServer(new VariantAbilityActivate(styles.get(10)));
                return true;
            }
        }

        if (key >= GLFW.GLFW_KEY_1 && key <= GLFW.GLFW_KEY_9) {
            int idx = key - GLFW.GLFW_KEY_1;
            if (idx < styles.size()) {
                this.minecraft.player.closeContainer();
                Changed.PACKET_HANDLER.sendToServer(new VariantAbilityActivate(styles.get(idx)));
                return true;
            }
        }

        return super.keyPressed(key, b, c);
    }*/

    @Override
    public void containerTick() {
        super.containerTick();
        tickCount++;
    }

    @Override
    public void onClose() {
        super.onClose();
        Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public void init() {
        super.init();

        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
    }
}