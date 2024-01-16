package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class LatexHeldEntityLayer<T extends LatexEntity, M extends LatexHumanoidModel<T>> extends RenderLayer<T, M> {
    private final ModelPart torso;

    public LatexHeldEntityLayer(RenderLayerParent<T, M> parent) {
        super(parent);
        torso = parent.getModel().getTorso();
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        var ability = entity.getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
        if (ability == null) return;
        if (ability.grabbedEntity == null) return;
        if (ability.suited) return;

        LatexVariantInstance.syncEntityPosRotWithEntity(ability.grabbedEntity, entity);
        pose.pushPose();
        torso.translateAndRotate(pose);

        pose.translate(0.0625, 0.0, -4.5 / 16.0);
        pose.mulPose(Vector3f.ZP.rotationDegrees(11.0f));

        var entityRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(ability.grabbedEntity);

        if (!(entityRenderer instanceof LivingEntityRenderer livingEntityRenderer)) return;

        boolean bodyVisible = livingEntityRenderer.isBodyVisible(ability.grabbedEntity);
        boolean shouldBeVisible = !bodyVisible && !ability.grabbedEntity.isInvisibleTo(Minecraft.getInstance().player);
        boolean shouldGlow = Minecraft.getInstance().shouldEntityAppearGlowing(ability.grabbedEntity);
        var renderType = livingEntityRenderer.getRenderType(ability.grabbedEntity, bodyVisible, shouldBeVisible, shouldGlow);
        int overlay = LivingEntityRenderer.getOverlayCoords(ability.grabbedEntity, livingEntityRenderer.getWhiteOverlayProgress(ability.grabbedEntity, partialTicks));
        livingEntityRenderer.getModel().renderToBuffer(pose, bufferSource.getBuffer(renderType), packedLight, overlay, 1.0f, 1.0f, 1.0f, 1.0f);

        for (Object layer : livingEntityRenderer.layers) {
            if (!(layer instanceof RenderLayer renderLayer)) return;
            renderLayer.render(pose, bufferSource, packedLight, ability.grabbedEntity, 0.0f, 0.0f, partialTicks, 0.0f, 0.0f, 0.0f);
        }

        pose.popPose();
    }
}
