package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.BehemothHeadModel;
import net.ltxprogrammer.changed.entity.beast.boss.BehemothHead;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BehemothHeadRenderer extends MobRenderer<BehemothHead, BehemothHeadModel> {
    public BehemothHeadRenderer(EntityRendererProvider.Context context) {
        super(context, new BehemothHeadModel(context.bakeLayer(BehemothHeadModel.LAYER_LOCATION)), 3.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(BehemothHead p_114482_) {
        return Changed.modResource("textures/behemoth_head.png");
    }
}
