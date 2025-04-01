package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.LowerTorsoedModel;
import net.ltxprogrammer.changed.client.renderer.model.TaurChestPackModel;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.beast.LatexTaur;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedAccessorySlots;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.Items;

public class TaurChestPackLayer<T extends ChangedEntity & LatexTaur<T>, M extends AdvancedHumanoidModel<T> & LowerTorsoedModel> extends RenderLayer<T, M> {
    private final TaurChestPackModel chestPackModel;

    public TaurChestPackLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        super(parent);
        this.chestPackModel = new TaurChestPackModel(modelSet.bakeLayer(TaurChestPackModel.LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource bufferSource, int i, T entity, float p_116670_, float p_116671_, float p_116672_, float p_116673_, float p_116674_, float p_116675_) {
        if (entity.getUnderlyingPlayer() == null)
            return;
        if (!AccessorySlots.getForEntity(entity)
                .flatMap(slots -> slots.getItem(ChangedAccessorySlots.LOWER_BODY_SIDE.get()))
                .map(stack -> !stack.isEmpty()).orElse(false))
            return;

        this.getParentModel().swapResetPoseStack(pose);

        pose.pushPose();
        ModelPart modelpart = this.getParentModel().getLowerTorso();
        modelpart.translateAndRotate(pose);
        pose.translate(0.0D, -1.51D - (2.0D / 16.0D), 7.0D / 16.0D);
        chestPackModel.renderToBuffer(pose, bufferSource.getBuffer(chestPackModel.renderType(chestPackModel.getTexture())), i, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        pose.popPose();

        this.getParentModel().swapResetPoseStack(pose);
    }
}
