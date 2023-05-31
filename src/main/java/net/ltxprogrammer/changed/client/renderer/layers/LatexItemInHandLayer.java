package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.UseItemMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class LatexItemInHandLayer<T extends LatexEntity, M extends LatexHumanoidModel<T> & ArmedModel & HeadedModel> extends ItemInHandLayer<T, M> {
    private static final float X_ROT_MIN = (-(float)Math.PI / 6F);
    private static final float X_ROT_MAX = ((float)Math.PI / 2F);

    public LatexItemInHandLayer(RenderLayerParent<T, M> p_174516_) {
        super(p_174516_);
    }

    protected void renderArmWithItem(LivingEntity p_174525_, ItemStack p_174526_, ItemTransforms.TransformType p_174527_, HumanoidArm p_174528_, PoseStack p_174529_, MultiBufferSource p_174530_, int p_174531_) {
        if (p_174525_ instanceof LatexEntity latexEntity && latexEntity.getUnderlyingPlayer() != null)
            p_174525_ = latexEntity.getUnderlyingPlayer();

        if (p_174526_.is(Items.SPYGLASS) && p_174525_.getUseItem() == p_174526_ && p_174525_.swingTime == 0) {
            this.renderArmWithSpyglass(p_174525_, p_174526_, p_174528_, p_174529_, p_174530_, p_174531_);
        } else {
            super.renderArmWithItem(p_174525_, p_174526_, p_174527_, p_174528_, p_174529_, p_174530_, p_174531_);
        }

    }

    private void renderArmWithSpyglass(LivingEntity entity, ItemStack itemStack, HumanoidArm arm, PoseStack pose, MultiBufferSource source, int color) {
        pose.pushPose();
        ModelPart modelpart = this.getParentModel().getHead();
        float f = modelpart.xRot;
        modelpart.xRot = Mth.clamp(modelpart.xRot, (-(float)Math.PI / 6F), ((float)Math.PI / 2F));
        modelpart.translateAndRotate(pose);
        modelpart.xRot = f;
        CustomHeadLayer.translateToHead(pose, false);
        boolean flag = arm == HumanoidArm.LEFT;
        var list = LatexHumanoidModel.findLargestCube(modelpart);
        if (list.isEmpty()) {
            pose.popPose();
            return;
        }
        var headCube = list.get(0);
        float dH = 0.5f - headCube.maxY;
        pose.translate(((flag ? -2.5F : 2.5F) / 16.0F), -0.0625D + (dH / 16.0f), 0.0D);
        Minecraft.getInstance().getItemInHandRenderer().renderItem(entity, itemStack, ItemTransforms.TransformType.HEAD, false, pose, source, color);
        pose.popPose();
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource bufferSource, int packedLight, T entity, float p_116670_, float p_116671_, float red, float green, float blue, float alpha) {
        var self = entity.getSelfVariant();
        if (self == null || self.itemUseMode == UseItemMode.NORMAL)
            super.render(pose, bufferSource, packedLight, entity, p_116670_, p_116671_, red, green, blue, alpha);
        else if (self.itemUseMode == UseItemMode.MOUTH) {
            boolean flag = entity.isSleeping();
            pose.pushPose();
            var head = this.getParentModel().getHead();
            pose.translate(head.x / 16.0F, head.y / 16.0F, head.z / 16.0F);
            pose.mulPose(Vector3f.ZP.rotation(0.0F));
            pose.mulPose(Vector3f.YP.rotationDegrees(blue));
            pose.mulPose(Vector3f.XP.rotationDegrees(alpha));
            if (entity.isBaby()) {
                if (flag) {
                    pose.translate(0.4F, 0.26F, 0.15F);
                } else {
                    pose.translate(0.06F, 0.26F, -0.5D);
                }
            } else if (flag) {
                pose.translate(0.46F, 0.26F, 0.22F);
            } else {
                pose.translate(0.06F, 0.27F, -0.5D);
            }

            pose.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            if (flag) {
                pose.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
            }

            ItemStack itemstack = entity.getItemBySlot(EquipmentSlot.MAINHAND);
            Minecraft.getInstance().getItemInHandRenderer().renderItem(entity, itemstack, ItemTransforms.TransformType.GROUND, false, pose, bufferSource, packedLight);
            pose.popPose();
        }
    }
}
