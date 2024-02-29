package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexCrystalWolfHornedModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.LatexCrystalWolfHorned;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexCrystalWolfHornedRenderer extends LatexHumanoidRenderer<LatexCrystalWolfHorned, LatexCrystalWolfHornedModel, ArmorLatexMaleWolfModel<LatexCrystalWolfHorned>> {
    public LatexCrystalWolfHornedRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexCrystalWolfHornedModel(context.bakeLayer(LatexCrystalWolfHornedModel.LAYER_LOCATION)),
                ArmorLatexMaleWolfModel::new, ArmorLatexMaleWolfModel.INNER_ARMOR, ArmorLatexMaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(),
                CustomEyesLayer.fixedColor(Color3.parseHex("#5a5a5a")),
                CustomEyesLayer::irisColorLeft,
                CustomEyesLayer::irisColorRight));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexCrystalWolfHorned p_114482_) {
        return Changed.modResource("textures/latex_crystal_wolf_horned.png");
    }
}