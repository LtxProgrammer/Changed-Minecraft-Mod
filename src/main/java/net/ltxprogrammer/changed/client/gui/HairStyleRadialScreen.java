package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.client.renderer.LatexHumanoidRenderer;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.util.SingleRunnable;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.ltxprogrammer.changed.world.inventory.HairStyleRadialMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class HairStyleRadialScreen extends LatexAbilityRadialScreen<HairStyleRadialMenu> {
    public final HairStyleRadialMenu menu;
    public final LatexVariantInstance<?> variant;
    public final List<HairStyle> styles;
    private int tickCount = 0;

    public HairStyleRadialScreen(HairStyleRadialMenu menu, Inventory inventory, Component text) {
        super(menu, inventory, text, menu.variant);
        this.imageWidth = 0;
        this.imageHeight = 0;
        this.menu = menu;
        this.variant = menu.variant;
        this.styles = variant.getLatexEntity().getValidHairStyles();
    }

    public static void renderEntityHeadWithHair(int x, int y, int scale, float lookX, float lookY, LatexEntity entity, float alpha) {
        float f = (float)Math.atan((double)(lookX / 40.0F));
        float f1 = (float)Math.atan((double)(lookY / 40.0F));
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.translate((double)x, (double)y, 1050.0D);
        posestack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack posestack1 = new PoseStack();
        posestack1.translate(0.0D, 0.0D, 1000.0D);
        posestack1.scale((float)scale, (float)scale, (float)scale);
        Quaternion quaternion = Vector3f.YP.rotationDegrees(f * 20.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(-f1 * 20.0F);
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
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
            var renderer = dispatcher.getRenderer(entity);
            if (renderer instanceof LatexHumanoidRenderer latexRenderer) {
                latexRenderer.getModel(entity).getHead().render(posestack1,
                        bufferSource.getBuffer(latexRenderer.getModel().renderType(latexRenderer.getTextureLocation(entity))),
                        LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1, 1, 1, alpha
                );
                latexRenderer.getHairLayer().render(posestack1,
                        bufferSource, LightTexture.FULL_BRIGHT, entity, 0, 0, 0, 0, 0, alpha);
            }
        });
        bufferSource.endBatch();
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

    @Override
    public int getCount() {
        return styles.size();
    }

    @Nullable
    @Override
    public List<Component> tooltipsFor(int section) {
        return null;
    }

    @Override
    public void renderSectionForeground(PoseStack pose, int section, double x, double y, float partialTicks, int mouseX, int mouseY, float red, float green, float blue, float alpha) {
        x = x * 0.9;
        y = (y * 0.9) - 16;

        var oldStyle = variant.getLatexEntity().getHairStyle();
        variant.getLatexEntity().setHairStyle(styles.get(section));
        renderEntityHeadWithHair((int)x + this.leftPos, (int)y + 32 + this.topPos, 40,
                (float)(this.leftPos) - mouseX + (int)x,
                (float)(this.topPos) - mouseY + (int)y,
                variant.getLatexEntity(), alpha);
        variant.getLatexEntity().setHairStyle(oldStyle);
    }

    @Override
    public boolean handleClicked(int section, SingleRunnable close) {
        this.variant.getLatexEntity().setHairStyle(styles.get(section));
        ChangedAbilities.getAbility(ChangedAbilities.SELECT_HAIRSTYLE.getId()).setDirty(this.menu.player, this.variant);
        return true;
    }
}