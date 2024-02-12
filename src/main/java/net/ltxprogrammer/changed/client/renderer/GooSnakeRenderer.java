package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.GooSnakeModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorSnakeAbdomenModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorUpperBodyModel;
import net.ltxprogrammer.changed.entity.beast.GooSnake;
import net.ltxprogrammer.changed.item.AbdomenArmor;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GooSnakeRenderer extends AdvancedHumanoidRenderer<GooSnake, GooSnakeModel, ArmorUpperBodyModel<GooSnake>> {
    public GooSnakeRenderer(EntityRendererProvider.Context context) {
        super(context, new GooSnakeModel(context.bakeLayer(GooSnakeModel.LAYER_LOCATION)),
                ArmorUpperBodyModel::new, ArmorUpperBodyModel.INNER_ARMOR, ArmorUpperBodyModel.OUTER_ARMOR,
                ArmorSnakeAbdomenModel::new, ArmorSnakeAbdomenModel.INNER_ARMOR, ArmorSnakeAbdomenModel.OUTER_ARMOR,
                AbdomenArmor::useAbdomenModel, AbdomenArmor::useInnerAbdomenModel, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(GooSnake p_114482_) {
        return Changed.modResource("textures/latex_snake.png");
    }
}
