package net.ltxprogrammer.changed.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IExtensibleEnum;

import java.util.function.Function;

public enum SpringType implements IExtensibleEnum {
    LIGHT_WEAK(0.8f, 0.6f),
    LIGHT_NORMAL(0.8f, 0.8f),
    LIGHT_STRONG(0.8f, 0.95f),
    MODERATE_WEAK(0.5f, 0.6f),
    MODERATE_NORMAL(0.5f, 0.8f),
    MODERATE_STRONG(0.5f, 0.95f),
    HEAVY_WEAK(0.2f, 0.6f),
    HEAVY_NORMAL(0.2f, 0.8f),
    HEAVY_STRONG(0.2f, 0.95f);

    // Unit-less scalar parameter, defined as how much velocity affects the spring
    private final float weight;
    private final float strength;
    private final float dampening;
    private static final float VELOCITY_DAMPENING = 0.95f;
    private static final float SPRING_DAMPENING = 0.85f;

    SpringType(float weight, float strength) {
        this(weight, strength, SPRING_DAMPENING);
    }

    SpringType(float weight, float strength, float dampening) {
        this.weight = weight;
        this.strength = strength;
        this.dampening = dampening;
    }

    public static SpringType create(String name, float weight, float strength) {
        throw new IllegalStateException("enum not extended");
    }

    public static SpringType create(String name, float weight, float strength, float dampening) {
        throw new IllegalStateException("enum not extended");
    }

    public float simulateVelocity(float velocity, float deltaVelocity, float spring) {
        velocity *= VELOCITY_DAMPENING;
        velocity += deltaVelocity;
        velocity = Mth.clamp(velocity, -1.0F, 1.0F);
        velocity -= spring * strength; // This should simulate a spring
        return velocity;
    }

    public float simulateSpring(float velocity, float spring) {
        spring += velocity * weight; // This adds weight to the spring
        spring *= dampening; // This adds an organic dampening
        return spring;
    }

    public enum Direction implements Function<LivingEntity, Float>, IExtensibleEnum {
        VERTICAL(entity -> {
            float vertVelocity = entity.onGround() ? 0.0f : (float) entity.getDeltaMovement().y;
            if (entity instanceof ChangedEntity changedEntity)
                vertVelocity += changedEntity.getVerticalSpringOffset();
            return vertVelocity;
        }),
        FORWARDS(entity -> {
            float f1 = -entity.yBodyRot * ((float)Math.PI / 180F);
            var facingForwards = new Vec3(Mth.sin(f1), 0.0, Mth.cos(f1));
            float horiVelocity = (float)facingForwards.dot(entity.getDeltaMovement());
            if (entity instanceof ChangedEntity changedEntity)
                horiVelocity += changedEntity.getHorizontalSpringOffset();
            return horiVelocity;
        });

        private final Function<LivingEntity, Float> fn;

        Direction(Function<LivingEntity, Float> fn) {
            this.fn = fn;
        }

        public static Direction create(String name, Function<LivingEntity, Float> velocityGetter) {
            throw new IllegalStateException("enum not extended");
        }

        @Override
        public Float apply(LivingEntity livingEntity) {
            return fn.apply(livingEntity);
        }
    }

    public static class Simulator {
        private float velocity = 0.0f;
        private float springO = 0.0f, spring = 0.0f;
        private final SpringType springType;

        public Simulator(SpringType springType) {
            this.springType = springType;
        }

        public float getSpring(float partialTicks) {
            return Mth.lerp(partialTicks, springO, spring);
        }

        public void tick(float deltaVelocity) {
            springO = spring;
            velocity = springType.simulateVelocity(velocity, deltaVelocity, spring);
            spring = springType.simulateSpring(velocity, spring);
        }
    }
}
