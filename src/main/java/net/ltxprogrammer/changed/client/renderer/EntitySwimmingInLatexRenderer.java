package net.ltxprogrammer.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.entity.PlayerMover;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class EntitySwimmingInLatexRenderer extends EntityRenderer<LivingEntity> {
    public EntitySwimmingInLatexRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public static boolean wantsToOverride(LivingEntity entity) {
        return entity instanceof PlayerDataExtension ext &&
                ext.isPlayerMover(PlayerMover.LATEX_SWIM.get());
    }

    @Override
    public void render(LivingEntity entity, float yRot, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight) {
        // Intentionally empty, maybe render a blob of latex?
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(LivingEntity entity) {
        return ResourceLocation.withDefaultNamespace("missingno");
    }
}
