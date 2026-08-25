package net.ltxprogrammer.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.EntityInDuctModel;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.entity.PlayerMover;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class EntityInDuctRenderer extends EntityRenderer<LivingEntity> {
    public static final ResourceLocation TEXTURE = Changed.modResource("textures/duct_entity.png");
    private final EntityInDuctModel playerModel;

    public EntityInDuctRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.playerModel = new EntityInDuctModel(context.bakeLayer(EntityInDuctModel.LAYER_LOCATION));
    }

    public static boolean wantsToOverride(LivingEntity entity) {
        return entity instanceof PlayerDataExtension ext &&
                ext.isPlayerMover(PlayerMover.DUCT_MOVER.get());
    }

    public Quaternionf getFacingRotation(Direction direction) {
        return switch (direction) {
            case UP -> (new Quaternionf()).rotationX((float)Math.PI / 2F);
            case DOWN -> (new Quaternionf()).rotationX(-(float)Math.PI / 2F);
            case NORTH -> new Quaternionf();
            case EAST -> (new Quaternionf()).rotationY(-(float)Math.PI / 2F);
            case SOUTH -> (new Quaternionf()).rotationY((float)Math.PI);
            case WEST -> (new Quaternionf()).rotationY((float)Math.PI / 2F);
        };
    }

    @Override
    public void render(LivingEntity entity, float yRot, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight) {
        super.render(entity, yRot, partialTick, pose, buffer, packedLight);

        var lookAngle = entity.getLookAngle();
        var look = Direction.getNearest(lookAngle.x, lookAngle.y, lookAngle.z);

        pose.pushPose();
        pose.mulPose(getFacingRotation(look));

        playerModel.renderToBuffer(pose, buffer.getBuffer(RenderType.entityCutout(this.getTextureLocation(entity))), packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0f, 1.0f, 1.0f, 1.0f);

        pose.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(LivingEntity entity) {
        return TEXTURE;
    }
}
