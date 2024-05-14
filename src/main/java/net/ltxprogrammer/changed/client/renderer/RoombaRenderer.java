package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.RoombaModel;
import net.ltxprogrammer.changed.entity.robot.Roomba;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RoombaRenderer extends MobRenderer<Roomba, RoombaModel> {
    private final ResourceLocation TEXTURE = Changed.modResource("textures/roomba.png");

    public RoombaRenderer(EntityRendererProvider.Context context) {
        super(context, new RoombaModel(context.bakeLayer(RoombaModel.LAYER_LOCATION)), 0.4f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Roomba roomba) {
        return TEXTURE;
    }
}
