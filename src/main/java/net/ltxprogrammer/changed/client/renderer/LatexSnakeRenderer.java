package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexSnakeModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModelPicker;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorSnakeAbdomenModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorUpperBodyModel;
import net.ltxprogrammer.changed.entity.beast.LatexSnake;
import net.ltxprogrammer.changed.item.AbdomenArmor;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSnakeRenderer extends AdvancedHumanoidRenderer<LatexSnake, LatexSnakeModel, ArmorUpperBodyModel<LatexSnake>> {
    public LatexSnakeRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSnakeModel(context.bakeLayer(LatexSnakeModel.LAYER_LOCATION)),
                ArmorModelPicker.legless(context.getModelSet(), ArmorUpperBodyModel.MODEL_SET, ArmorSnakeAbdomenModel.MODEL_SET), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSnake p_114482_) {
        return Changed.modResource("textures/latex_snake.png");
    }
}
