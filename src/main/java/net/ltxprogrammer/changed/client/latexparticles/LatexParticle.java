package net.ltxprogrammer.changed.client.latexparticles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public abstract class LatexParticle {
    protected static final double MAXIMUM_COLLISION_VELOCITY_SQUARED = Mth.square(100.0D);

    protected final Level level;
    private final int lifespan;

    private int age = 0;
    protected boolean stoppedByCollision;
    protected boolean isOnGround;
    protected boolean isOnWall;

    public LatexParticle(Level level, int lifespan) {
        this.level = level;
        this.lifespan = lifespan;
    }

    public void tick() {
        increaseAge();
    }

    public abstract ParticleRenderType getRenderType();

    public void setupForRender(PoseStack poseStack, float partialTicks) {}
    public abstract void render(VertexConsumer buffer, Camera camera, float partialTicks);

    public int getAge() {
        return age;
    }

    public void increaseAge() {
        age++;
    }

    public boolean shouldExpire() {
        return age >= lifespan;
    }

    public boolean isForEntity(Entity entity) {
        return false;
    }

    public boolean shouldCull() {
        return true;
    }

    public abstract AABB getBoundingBox();
}
