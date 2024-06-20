package net.ltxprogrammer.changed.client.renderer.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.projectile.GasParticle;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class GasParticleRenderer extends EntityRenderer<GasParticle> {
    public GasParticleRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(GasParticle particle, float p_114486_, float partialTick, PoseStack pose, MultiBufferSource buffers, int packedLight) {
        if (partialTick < 0.6f)
            return;

        float dispersion = (((float)particle.tickCount / (float)GasParticle.DISSIPATE_TIME) * 3.0f) + 0.001f;

        EntityDimensions dimensions = particle.getDimensions(particle.getPose());
        double dh = particle.level.random.nextDouble(dimensions.height * dispersion);
        double dx = (particle.level.random.nextDouble(dimensions.width * dispersion) - (0.5 * dimensions.width * dispersion));
        double dz = (particle.level.random.nextDouble(dimensions.width * dispersion) - (0.5 * dimensions.width * dispersion));
        Vec3 delta = particle.getDeltaMovement();
        particle.level.addParticle(ChangedParticles.gas(particle.getColor()),
                particle.xo + dx * 1.2, particle.yo + dh, particle.zo + dz * 1.2,
                delta.x * 0.75f, delta.y * 0.75f, delta.z * 0.75f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(GasParticle particle) {
        return Changed.modResource("textures/block/blank.png");
    }
}
