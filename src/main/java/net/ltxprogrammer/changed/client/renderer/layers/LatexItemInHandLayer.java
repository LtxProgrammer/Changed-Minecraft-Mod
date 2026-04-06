package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.UseItemMode;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class LatexItemInHandLayer<T extends ChangedEntity, M extends AdvancedHumanoidModel<T> & ArmedModel & HeadedModel> extends ItemInHandLayer<T, M> {
    private static final float X_ROT_MIN = (-(float)Math.PI / 6F);
    private static final float X_ROT_MAX = ((float)Math.PI / 2F);

    private final ItemInHandRenderer itemInHandRenderer;

    public LatexItemInHandLayer(RenderLayerParent<T, M> parent, ItemInHandRenderer itemInHandRenderer) {
        super(parent, itemInHandRenderer);
        this.itemInHandRenderer = itemInHandRenderer;
    }

    protected void renderArmWithItem(LivingEntity entity, ItemStack heldItem, ItemDisplayContext displayContext, HumanoidArm arm, PoseStack poseStack, MultiBufferSource source, int packedLight) {
        if (heldItem.is(Items.SPYGLASS) && entity.getUseItem() == heldItem && entity.swingTime == 0) {
            this.renderArmWithSpyglass(entity, heldItem, arm, poseStack, source, packedLight);
        } else {
            poseStack.pushPose();
            this.getParentModel().scaleForBody(poseStack);
            super.renderArmWithItem(entity, heldItem, displayContext, arm, poseStack, source, packedLight);
            poseStack.popPose();
        }

    }

    private void renderArmWithSpyglass(LivingEntity entity, ItemStack itemStack, HumanoidArm arm, PoseStack pose, MultiBufferSource source, int packedLight) {
        pose.pushPose();
        ModelPart modelpart = this.getParentModel().getHead();
        this.getParentModel().scaleForHead(pose);
        float f = modelpart.xRot;
        modelpart.xRot = Mth.clamp(modelpart.xRot, X_ROT_MIN, X_ROT_MAX);
        modelpart.translateAndRotate(pose);
        modelpart.xRot = f;
        CustomHeadLayer.translateToHead(pose, false);
        boolean flag = arm == HumanoidArm.LEFT;
        pose.translate(((flag ? -2.5F : 2.5F) / 16.0F), -0.0625D, 0.0D);
        itemInHandRenderer.renderItem(entity, itemStack, ItemDisplayContext.HEAD, false, pose, source, packedLight);
        pose.popPose();
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.getItemUseMode() == UseItemMode.NORMAL)
            super.render(pose, bufferSource, packedLight, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        else if (entity.getItemUseMode() == UseItemMode.MOUTH) {
            boolean flag = entity.isSleeping();
            pose.pushPose();
            var head = this.getParentModel().getHead();
            this.getParentModel().scaleForHead(pose);
            pose.translate(head.x / 16.0F, (head.y) / 16.0F, head.z / 16.0F);
            pose.mulPose(Axis.ZP.rotation(0.0F));
            pose.mulPose(Axis.YP.rotationDegrees(netHeadYaw));
            pose.mulPose(Axis.XP.rotationDegrees(headPitch));
            if (flag) {
                pose.translate(0.46F, 0.26F, 0.22F);
            } else {
                pose.translate(0.06F, 0.27F, -0.5D);
            }

            pose.mulPose(Axis.XP.rotationDegrees(90.0F));
            if (flag) {
                pose.mulPose(Axis.ZP.rotationDegrees(90.0F));
            }
            pose.mulPose(Axis.ZP.rotationDegrees(180.0F));
            pose.translate(1.0 / 16.0F, -2.0 / 16.0F, 1.0 / 16.0F);

            ItemStack itemstack = entity.getItemBySlot(EquipmentSlot.MAINHAND);
            itemInHandRenderer.renderItem(entity, itemstack, ItemDisplayContext.GROUND, false, pose, bufferSource, packedLight);
            pose.popPose();
        }
    }
}
