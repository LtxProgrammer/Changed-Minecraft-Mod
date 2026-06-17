package net.ltxprogrammer.changed.client.renderer;

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

public class BuffLatexSharkFemaleRenderer extends AdvancedHumanoidRenderer<BuffLatexSharkFemale, BuffLatexSharkFemaleModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_shark_buff_female.png");

    public BuffLatexSharkFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new BuffLatexSharkFemaleModel(context.bakeLayer(BuffLatexSharkFemaleModel.LAYER_LOCATION)), ArmorLatexFemaleSharkModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withEyelashes(Color3.fromInt(0x1a1a1b)).build());
        this.addLayer(GasMaskLayer.forLargeSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(BuffLatexSharkFemale entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}