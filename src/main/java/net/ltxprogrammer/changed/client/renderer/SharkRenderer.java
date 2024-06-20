package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.SharkModel;
import net.ltxprogrammer.changed.entity.beast.Shark;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SharkRenderer extends MobRenderer<Shark, SharkModel<Shark>> {
    private static final ResourceLocation SHARK_LOCATION = Changed.modResource("textures/shark.png");

    public SharkRenderer(EntityRendererProvider.Context context) {
        super(context, new SharkModel<>(context.bakeLayer(SharkModel.LAYER_LOCATION)), 0.7F);
    }

    public ResourceLocation getTextureLocation(Shark shark) {
        return SHARK_LOCATION;
    }
}