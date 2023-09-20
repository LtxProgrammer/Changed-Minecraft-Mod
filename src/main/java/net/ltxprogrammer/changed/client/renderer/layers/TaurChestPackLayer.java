package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.LowerTorsoedModel;
import net.ltxprogrammer.changed.client.renderer.model.TaurChestPackModel;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.beast.LatexTaur;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class TaurChestPackLayer<T extends LatexEntity & LatexTaur<T>, M extends LatexHumanoidModel<T> & LowerTorsoedModel> extends RenderLayer<T, M> {
    private final TaurChestPackModel chestPackModel;

    public TaurChestPackLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        super(parent);
        this.chestPackModel = new TaurChestPackModel(modelSet.bakeLayer(TaurChestPackModel.LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource bufferSource, int i, T entity, float p_116670_, float p_116671_, float p_116672_, float p_116673_, float p_116674_, float p_116675_) {
        if (entity.getUnderlyingPlayer() == null)
            return;
        ProcessTransfur.ifPlayerLatex(entity.getUnderlyingPlayer(), variant -> {
            var ability = variant.getAbilityInstance(ChangedAbilities.ACCESS_SADDLE.get());
            if (ability == null || ability.chest == null || ability.chest.isEmpty())
                return;

            pose.pushPose();
            ModelPart modelpart = this.getParentModel().getTorso();
            modelpart.translateAndRotate(pose);
            pose.translate(0.0D, entity.isCrouching() ? -26.97D / 16.0D : -27.0D / 16.0D, 7.0D / 16.0D);
            chestPackModel.renderToBuffer(pose, bufferSource.getBuffer(chestPackModel.renderType(chestPackModel.getTexture())), i, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
            pose.popPose();
        });
    }
}
