package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexSnakeModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModelPicker;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorSnakeAbdomenModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorSnakeUpperBodyModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorUpperBodyModel;
import net.ltxprogrammer.changed.entity.beast.LatexSnake;
import net.ltxprogrammer.changed.item.AbdomenArmor;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSnakeRenderer extends AdvancedHumanoidRenderer<LatexSnake, LatexSnakeModel, ArmorSnakeUpperBodyModel<LatexSnake>> {
    public LatexSnakeRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSnakeModel(context.bakeLayer(LatexSnakeModel.LAYER_LOCATION)),
                ArmorModelPicker.legless(context.getModelSet(), ArmorSnakeUpperBodyModel.MODEL_SET, ArmorSnakeAbdomenModel.MODEL_SET), 0.5f);
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.fromInt(0x6e6e6e)).build());
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSnake p_114482_) {
        return Changed.modResource("textures/latex_snake.png");
    }
}
