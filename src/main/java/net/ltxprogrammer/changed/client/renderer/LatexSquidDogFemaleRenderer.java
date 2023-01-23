package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexSquidDogFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.LatexSquidDogMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexSquidDogModel;
import net.ltxprogrammer.changed.entity.beast.LatexSquidDogFemale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSquidDogFemaleRenderer extends LatexHumanoidRenderer<LatexSquidDogFemale, LatexSquidDogFemaleModel, ArmorLatexSquidDogModel<LatexSquidDogFemale>> {
    public LatexSquidDogFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSquidDogFemaleModel(context.bakeLayer(LatexSquidDogFemaleModel.LAYER_LOCATION)),
                ArmorLatexSquidDogModel::new, ArmorLatexSquidDogModel.INNER_ARMOR, ArmorLatexSquidDogModel.OUTER_ARMOR, 0.65f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSquidDogFemale p_114482_) {
        return Changed.modResource("textures/latex_squid_dog.png");
    }
}