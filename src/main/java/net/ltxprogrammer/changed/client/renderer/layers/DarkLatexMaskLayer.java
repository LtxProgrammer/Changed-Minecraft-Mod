package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.DarkLatexMaskModel;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedLayerDefinitions;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;

import static net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModelController.findLargestCube;

public class DarkLatexMaskLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private final DarkLatexMaskModel maskModel;

    public DarkLatexMaskLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent, EntityModelSet modelSet) {
        super(parent);
        this.maskModel = new DarkLatexMaskModel(modelSet.bakeLayer(DarkLatexMaskModel.LAYER_LOCATION));
    }

    public void render(PoseStack pose, MultiBufferSource bufferSource, int i, AbstractClientPlayer player, float p_116670_, float p_116671_, float p_116672_, float p_116673_, float p_116674_, float p_116675_) {
        if (!player.getItemBySlot(EquipmentSlot.HEAD).is(ChangedItems.DARK_LATEX_MASK.get()))
            return;

        pose.pushPose();
        ModelPart modelpart = this.getParentModel().getHead();
        modelpart.translateAndRotate(pose);
        pose.translate(0.0D, -25.0D / 16.0D, -4.0D / 16.0D);
        maskModel.renderToBuffer(pose, bufferSource.getBuffer(maskModel.renderType(maskModel.getTexture())), i, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        pose.popPose();
    }
}
