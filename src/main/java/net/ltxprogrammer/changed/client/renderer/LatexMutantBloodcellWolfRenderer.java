package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexMutantBloodcellWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexFemaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.LatexMutantBloodcellWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexMutantBloodcellWolfRenderer extends AdvancedHumanoidRenderer<LatexMutantBloodcellWolf, LatexMutantBloodcellWolfModel, ArmorLatexFemaleWolfModel<LatexMutantBloodcellWolf>> {
    public LatexMutantBloodcellWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexMutantBloodcellWolfModel(context.bakeLayer(LatexMutantBloodcellWolfModel.LAYER_LOCATION)),
                ArmorLatexFemaleWolfModel::new, ArmorLatexFemaleWolfModel.INNER_ARMOR, ArmorLatexFemaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexMutantBloodcellWolf p_114482_) {
        return Changed.modResource("textures/latex_mutant_bloodcell_wolf.png");
    }
}