package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class TransfurProgressLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public TransfurProgressLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent, EntityModelSet modelSet, boolean slim) {
        super(parent);
    }

    public static ResourceLocation getProgressTexture(float progress) {
        float num = progress / (Changed.config.server.transfurTolerance.get().floatValue() / 10.0f);
        return Changed.modResource("textures/models/latex_coat/" + Math.max(Math.min((int)Math.floor(num), 10), 1) + ".png");
    }

    public static Color3 getProgressColor(LatexVariant<?> variant) {
        return Color3.fromInt(ChangedEntities.getEntityColorBack(variant.getEntityType().getRegistryName()));
    }

    public static Color3 getProgressColor(ResourceLocation type) {
        return Color3.fromInt(ChangedEntities.getEntityColorBack(ChangedRegistry.LATEX_VARIANT.get().getValue(type).getEntityType().getRegistryName()));
    }

    public void render(PoseStack pose, MultiBufferSource bufferSource, int i, AbstractClientPlayer player, float p_116670_, float p_116671_, float p_116672_, float p_116673_, float p_116674_, float p_116675_) {
        var progress = ProcessTransfur.getPlayerTransfurProgress(player);
        if (progress != null && progress.progress() > 0) {
            if (progress.variant().is(ChangedTags.LatexVariants.WOLF_LIKE)) {
                // TODO render partial tail/ears
            }

            var color = getProgressColor(progress.variant());
            this.getParentModel().renderToBuffer(pose,
                    bufferSource.getBuffer(RenderType.entityCutoutNoCull(getProgressTexture(progress.progress()))),
                    i,
                    LivingEntityRenderer.getOverlayCoords(player, 0.0F),
                    color.red(),
                    color.green(),
                    color.blue(),
                    1.0F);
        }
    }
}
