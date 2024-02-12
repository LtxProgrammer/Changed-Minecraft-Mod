package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.model.BeifengModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleDragonModel;
import net.ltxprogrammer.changed.entity.beast.Beifeng;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BeifengRenderer extends AdvancedHumanoidRenderer<Beifeng, BeifengModel, ArmorLatexMaleDragonModel<Beifeng>> {
    public BeifengRenderer(EntityRendererProvider.Context context) {
        super(context, new BeifengModel(context.bakeLayer(BeifengModel.LAYER_LOCATION)),
                ArmorLatexMaleDragonModel::new, ArmorLatexMaleDragonModel.INNER_ARMOR, ArmorLatexMaleDragonModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(), CustomEyesLayer.fixedColor(Color3.WHITE),CustomEyesLayer.fixedColor(Color3.parseHex("#ffe852"))));
    }

    @Override
    public ResourceLocation getTextureLocation(Beifeng p_114482_) {
        return Changed.modResource("textures/latex_beifeng.png");
    }
}