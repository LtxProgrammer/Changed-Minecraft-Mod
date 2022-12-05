package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.entity.LatexEntity;
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
        if (p_174526_.is(Items.SPYGLASS) && p_174525_.getUseItem() == p_174526_ && p_174525_.swingTime == 0) {
            this.renderArmWithSpyglass(p_174525_, p_174526_, p_174528_, p_174529_, p_174530_, p_174531_);
        } else {
            super.renderArmWithItem(p_174525_, p_174526_, p_174527_, p_174528_, p_174529_, p_174530_, p_174531_);
        }

    }

    private void renderArmWithSpyglass(LivingEntity p_174518_, ItemStack p_174519_, HumanoidArm p_174520_, PoseStack p_174521_, MultiBufferSource p_174522_, int p_174523_) {
        p_174521_.pushPose();
        ModelPart modelpart = this.getParentModel().getHead();
        float f = modelpart.xRot;
        modelpart.xRot = Mth.clamp(modelpart.xRot, (-(float)Math.PI / 6F), ((float)Math.PI / 2F));
        modelpart.translateAndRotate(p_174521_);
        modelpart.xRot = f;
        CustomHeadLayer.translateToHead(p_174521_, false);
        boolean flag = p_174520_ == HumanoidArm.LEFT;
        p_174521_.translate((double)((flag ? -2.5F : 2.5F) / 16.0F), -0.0625D, 0.0D);
        Minecraft.getInstance().getItemInHandRenderer().renderItem(p_174518_, p_174519_, ItemTransforms.TransformType.HEAD, false, p_174521_, p_174522_, p_174523_);
        p_174521_.popPose();
    }
}
