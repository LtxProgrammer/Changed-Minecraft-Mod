package net.ltxprogrammer.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.BuffLatexSharkFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexFemaleSharkModel;
import net.ltxprogrammer.changed.entity.beast.BuffLatexSharkFemale;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BuffLatexSharkFemaleRenderer extends AdvancedHumanoidRenderer<BuffLatexSharkFemale, BuffLatexSharkFemaleModel, ArmorLatexFemaleSharkModel<BuffLatexSharkFemale>> {
    public BuffLatexSharkFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new BuffLatexSharkFemaleModel(context.bakeLayer(BuffLatexSharkFemaleModel.LAYER_LOCATION)),
                ArmorLatexFemaleSharkModel::new, ArmorLatexFemaleSharkModel.INNER_ARMOR, ArmorLatexFemaleSharkModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withEyelashes(Color3.fromInt(0x1a1a1b)).build());
        this.addLayer(GasMaskLayer.forLargeSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(BuffLatexSharkFemale p_114482_) {
        return Changed.modResource("textures/latex_shark_buff_female.png");
    }

    @Override
    protected void scale(BuffLatexSharkFemale entity, PoseStack pose, float partialTick) {
        float modelScale = 1.025F;
        pose.scale(modelScale, modelScale, modelScale);
    }
}