package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.DarkLatexMaskModel;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.util.Cacheable;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class DarkLatexMaskLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    private final Cacheable<DarkLatexMaskModel> maskModel;

    public DarkLatexMaskLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        super(parent);
        this.maskModel = Cacheable.of(() -> {
            try {
                return new DarkLatexMaskModel(modelSet.bakeLayer(DarkLatexMaskModel.LAYER_LOCATION));
            } catch (Exception ex) {
                ex.printStackTrace();
                Changed.LOGGER.error("Failed to initialize dark latex mask model. This error is likely from a mod incompatibility.");
                return null;
            }
        });
    }

    public void render(PoseStack pose, MultiBufferSource bufferSource, int i, T entity, float p_116670_, float p_116671_, float p_116672_, float p_116673_, float p_116674_, float p_116675_) {
        if (!entity.getItemBySlot(EquipmentSlot.HEAD).is(ChangedItems.DARK_LATEX_MASK.get()))
            return;
        
        var mask = maskModel.getOrThrow();

        pose.pushPose();
        ModelPart modelpart = this.getParentModel().getHead();
        modelpart.translateAndRotate(pose);
        pose.translate(0.0D, -25.0D / 16.0D, -4.0D / 16.0D);
        mask.renderToBuffer(pose, bufferSource.getBuffer(mask.renderType(mask.getTexture())), i, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        pose.popPose();
    }
}
