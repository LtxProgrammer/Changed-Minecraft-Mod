package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModelInterface;
import net.ltxprogrammer.changed.client.renderer.model.GasMaskModel;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.util.Cacheable;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class GasMaskLayer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> implements FirstPersonLayer<T> {
    private final Cacheable<GasMaskModel> maskModel;

    public GasMaskLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet, ModelLayerLocation model) {
        super(parent);
        this.maskModel = Cacheable.of(() -> {
            try {
                return new GasMaskModel(modelSet.bakeLayer(model));
            } catch (Exception ex) {
                ex.printStackTrace();
                Changed.LOGGER.error("Failed to initialize gas mask model. This error is likely from a mod incompatibility.");
                return null;
            }
        });
    }

    public GasMaskLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        this(parent, modelSet, GasMaskModel.LAYER_LOCATION);
    }

    public static <T extends LivingEntity, M extends EntityModel<T> & HeadedModel> GasMaskLayer<T, M> forNormal(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        return new GasMaskLayer<>(parent, modelSet, GasMaskModel.LAYER_LOCATION);
    }

    public static <T extends LivingEntity, M extends EntityModel<T> & HeadedModel> GasMaskLayer<T, M> forSnouted(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        return new GasMaskLayer<>(parent, modelSet, GasMaskModel.LAYER_LOCATION_SNOUTED);
    }

    public static <T extends LivingEntity, M extends EntityModel<T> & HeadedModel> GasMaskLayer<T, M> forLargeSnouted(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        return new GasMaskLayer<>(parent, modelSet, GasMaskModel.LAYER_LOCATION_LARGE_SNOUTED);
    }

    public void render(PoseStack pose, MultiBufferSource bufferSource, int packedLight, T entity, float p_116670_, float p_116671_, float p_116672_, float p_116673_, float p_116674_, float p_116675_) {
        if (!entity.getItemBySlot(EquipmentSlot.HEAD).is(ChangedItems.GAS_MASK.get()))
            return;
        
        var mask = maskModel.getOrThrow();

        pose.pushPose();
        ModelPart modelpart = this.getParentModel().getHead();
        if (this.getParentModel() instanceof AdvancedHumanoidModelInterface<?,?> modelInterface)
            modelInterface.scaleForHead(pose);

        modelpart.translateAndRotate(pose);
        mask.renderToBuffer(pose, bufferSource.getBuffer(mask.renderType(mask.getTexture())), packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0f, 1.0f, 1.0f, 1.0f);
        pose.popPose();
    }
}
