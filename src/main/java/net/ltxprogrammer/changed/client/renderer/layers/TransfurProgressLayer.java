package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedLayerDefinitions;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class TransfurProgressLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private final PlayerModel<AbstractClientPlayer> playerModel;

    public TransfurProgressLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent, EntityModelSet modelSet, boolean slim) {
        super(parent);
        this.playerModel = new PlayerModel<>(modelSet.bakeLayer(slim ? ChangedLayerDefinitions.LATEX_COAT_SLIM : ChangedLayerDefinitions.LATEX_COAT), slim);
    }

    private ResourceLocation getProgressTexture(int ticks) {
        int num = ticks / (ProcessTransfur.TRANSFUR_PROGRESSION_TAKEOVER / 10);
        return Changed.modResource("textures/models/latex_coat/" + Math.max(Math.min(num, 10), 1) + ".png");
    }

    private ChangedParticles.Color3 getProgressColor(ResourceLocation type) {
        return ChangedParticles.Color3.fromInt(ChangedEntities.getEntityColorBack(LatexVariant.ALL_LATEX_FORMS.get(type).getEntityType().getRegistryName()));
    }

    public void render(PoseStack pose, MultiBufferSource bufferSource, int i, AbstractClientPlayer player, float p_116670_, float p_116671_, float p_116672_, float p_116673_, float p_116674_, float p_116675_) {
        var progress = ProcessTransfur.getPlayerTransfurProgress(player);
        if (progress != null && progress.ticks() > 0) {
            var color = getProgressColor(progress.type());
            this.getParentModel().renderToBuffer(pose,
                    bufferSource.getBuffer(RenderType.entityCutoutNoCull(getProgressTexture(progress.ticks()))),
                    i,
                    LivingEntityRenderer.getOverlayCoords(player, 0.0F),
                    color.red(),
                    color.green(),
                    color.blue(),
                    1.0F);
        }
    }
}
