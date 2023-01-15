package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.model.RoombaModel;
import net.ltxprogrammer.changed.entity.robot.Roomba;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RoombaRenderer extends MobRenderer<Roomba, RoombaModel> {
    protected RoombaRenderer(EntityRendererProvider.Context context) {
        super(context, new RoombaModel(context.bakeLayer(RoombaModel.LAYER_LOCATION)), 0.4f);
    }

    @Override
    public ResourceLocation getTextureLocation(Roomba p_114482_) {
        return null;
    }
}
