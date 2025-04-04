package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.ExoskeletonModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.robot.Exoskeleton;
import net.ltxprogrammer.changed.util.Cacheable;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ExoskeletonLayer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> implements FirstPersonLayer<T> {
    private final Cacheable<ExoskeletonModel> suitModel;
    private final boolean renderLegs;
    private final Cacheable<ExoskeletonModel.ReplacementLimbs> suitLegsModel;

    public ExoskeletonLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet, ModelLayerLocation model, @Nullable ModelLayerLocation legsModel) {
        super(parent);
        this.suitModel = Cacheable.of(() -> {
            try {
                return new ExoskeletonModel(modelSet.bakeLayer(model));
            } catch (Exception ex) {
                ex.printStackTrace();
                Changed.LOGGER.error("Failed to initialize exoskeleton model. This error is likely from a mod incompatibility.");
                return null;
            }
        });
        this.renderLegs = legsModel != null;
        this.suitLegsModel = Cacheable.of(() -> {
            if (legsModel == null) return null;

            try {
                return new ExoskeletonModel.ReplacementLimbs(modelSet.bakeLayer(legsModel));
            } catch (Exception ex) {
                ex.printStackTrace();
                Changed.LOGGER.error("Failed to initialize exoskeleton model. This error is likely from a mod incompatibility.");
                return null;
            }
        });
    }

    public ExoskeletonLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        this(parent, modelSet, ExoskeletonModel.LAYER_LOCATION_SUIT, null);
    }

    public static <T extends LivingEntity, M extends EntityModel<T> & HeadedModel> ExoskeletonLayer<T, M> forPlayerModel(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        return new ExoskeletonLayer<>(parent, modelSet, ExoskeletonModel.LAYER_LOCATION_SUIT, ExoskeletonModel.LAYER_LOCATION_HUMAN);
    }

    public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T> & HeadedModel> ExoskeletonLayer<T, M> forAdvancedModel(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        return new ExoskeletonLayer<>(parent, modelSet, ExoskeletonModel.LAYER_LOCATION_SUIT, null);
    }

    public void render(PoseStack pose, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!(EntityUtil.maybeGetUnderlying(entity).getFirstPassenger() instanceof Exoskeleton exoskeleton)) return;
        
        var suitModel = this.suitModel.getOrThrow();

        pose.pushPose();
        /*ModelPart modelpart = this.getParentModel().getHead();
        if (this.getParentModel() instanceof AdvancedHumanoidModelInterface<?,?> modelInterface)
            modelInterface.scaleForHead(pose);

        modelpart.translateAndRotate(pose);*/
        suitModel.prepareMobModel(exoskeleton, limbSwing, limbSwingAmount, partialTicks);
        suitModel.matchWearersAnim(this.getParentModel(), exoskeleton);
        suitModel.renderToBuffer(pose, bufferSource.getBuffer(suitModel.renderType(suitModel.getTexture(exoskeleton))), packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0f, 1.0f, 1.0f, 1.0f);

        if (this.renderLegs) {
            var legsModel = this.suitLegsModel.getOrThrow();
            legsModel.prepareMobModel(exoskeleton, limbSwing, limbSwingAmount, partialTicks);
            legsModel.matchWearersAnim(this.getParentModel(), exoskeleton);
            legsModel.renderToBuffer(pose, bufferSource.getBuffer(legsModel.renderType(legsModel.getTexture(exoskeleton))), packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0f, 1.0f, 1.0f, 1.0f);
        }

        pose.popPose();
    }
}
