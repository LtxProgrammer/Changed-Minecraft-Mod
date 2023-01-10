package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.BehemothHandLeftModel;
import net.ltxprogrammer.changed.entity.beast.boss.BehemothHandLeft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BehemothHandLeftRenderer extends MobRenderer<BehemothHandLeft, BehemothHandLeftModel> {
    public BehemothHandLeftRenderer(EntityRendererProvider.Context context) {
        super(context, new BehemothHandLeftModel(context.bakeLayer(BehemothHandLeftModel.LAYER_LOCATION)), 1.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(BehemothHandLeft p_114482_) {
        return Changed.modResource("textures/behemoth_hand.png");
    }
}
