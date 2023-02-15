package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.model.CentaurChestPackModel;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.TorsoSupplier;
import net.ltxprogrammer.changed.entity.beast.LightLatexCentaur;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class CentaurChestPackLayer<T extends LightLatexCentaur, M extends LatexHumanoidModel<T> & TorsoSupplier> extends RenderLayer<T, M> {
    private final CentaurChestPackModel chestPackModel;

    public CentaurChestPackLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        super(parent);
        this.chestPackModel = new CentaurChestPackModel(modelSet.bakeLayer(CentaurChestPackModel.LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource bufferSource, int i, T entity, float p_116670_, float p_116671_, float p_116672_, float p_116673_, float p_116674_, float p_116675_) {
        if (entity.getUnderlyingPlayer() == null)
            return;
        ProcessTransfur.ifPlayerLatex(entity.getUnderlyingPlayer(), variant -> {
            var ability = variant.getAbilityInstance(ChangedAbilities.ACCESS_SADDLE);
            if (ability == null || ability.chest == null || ability.chest.isEmpty())
                return;

            pose.pushPose();
            ModelPart modelpart = this.getParentModel().getTorso();
            modelpart.translateAndRotate(pose);
            pose.translate(0.0D, entity.isCrouching() ? -23.0D / 16.0D : -26.0D / 16.0D, 7.0D / 16.0D);
            chestPackModel.renderToBuffer(pose, bufferSource.getBuffer(chestPackModel.renderType(chestPackModel.getTexture())), i, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
            pose.popPose();
        });
    }
}
