package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.model.MilkPuddingModel;
import net.ltxprogrammer.changed.entity.beast.MilkPudding;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MilkPuddingRenderer extends MobRenderer<MilkPudding, MilkPuddingModel> {
    public MilkPuddingRenderer(EntityRendererProvider.Context context) {
        super(context, new MilkPuddingModel(context.bakeLayer(MilkPuddingModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(MilkPudding p_114482_) {
        return new ResourceLocation("changed:textures/milk_pudding.png");
    }
}
