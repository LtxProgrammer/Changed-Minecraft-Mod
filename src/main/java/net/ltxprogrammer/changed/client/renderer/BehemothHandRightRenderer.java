package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.BehemothHandRightModel;
import net.ltxprogrammer.changed.entity.beast.boss.BehemothHandRight;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BehemothHandRightRenderer extends MobRenderer<BehemothHandRight, BehemothHandRightModel> {
    public BehemothHandRightRenderer(EntityRendererProvider.Context context) {
        super(context, new BehemothHandRightModel(context.bakeLayer(BehemothHandRightModel.LAYER_LOCATION)), 0.8f);
    }

    @Override
    public ResourceLocation getTextureLocation(BehemothHandRight p_114482_) {
        return Changed.modResource("textures/behemoth_hand.png");
    }
}
