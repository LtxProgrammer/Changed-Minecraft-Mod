package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.GooSquirrelModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorNoTailModel;
import net.ltxprogrammer.changed.entity.beast.GooSquirrel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GooSquirrelRenderer extends AdvancedHumanoidRenderer<GooSquirrel, GooSquirrelModel, ArmorNoTailModel<GooSquirrel>> {
    public GooSquirrelRenderer(EntityRendererProvider.Context context) {
        super(context, new GooSquirrelModel(context.bakeLayer(GooSquirrelModel.LAYER_LOCATION)),
                ArmorNoTailModel::new, ArmorNoTailModel.INNER_ARMOR, ArmorNoTailModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(GooSquirrel p_114482_) {
        return Changed.modResource("textures/latex_squirrel.png");
    }
}