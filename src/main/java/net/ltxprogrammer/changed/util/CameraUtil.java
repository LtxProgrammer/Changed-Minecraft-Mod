package net.ltxprogrammer.changed.util;

import com.mojang.datafixers.util.Either;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Field;

public class CameraUtil {
    private static final Field wantToLookAtField;

    static {
        Field wantToLookAtField1;
        try {
            wantToLookAtField1 = Player.class.getDeclaredField("wantToLookAt");
            wantToLookAtField1.setAccessible(true);
        } catch (NoSuchFieldException e) {
            wantToLookAtField1 = null;
            e.printStackTrace();
        }
        wantToLookAtField = wantToLookAtField1;
    }

    public static record TugData(Either<Vec3, LivingEntity> lookAt, double strength, long tickExpire) {
        public Vec3 getDirection(LivingEntity source) {
            if (lookAt.left().isPresent())
                return lookAt.left().get();
            else
                return lookAt.right().get().getEyePosition().subtract(source.getEyePosition()).normalize();
        }

        public float getWantRotX(LivingEntity source) {
            Vec3 dir = getDirection(source);

            double d0 = dir.x;
            double d1 = dir.y;
            double d2 = dir.z;
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            return Mth.wrapDegrees((float)(-(Mth.atan2(d1, d3) * (double)(180F / (float)Math.PI))));
        }

        public float getWantRotY(LivingEntity source) {
            Vec3 dir = getDirection(source);

            double d0 = dir.x;
            double d2 = dir.z;
            return Mth.wrapDegrees((float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F);
        }
    }

    public static TugData getTugData(Player player) {
        try {
            return (TugData) wantToLookAtField.get(player);
        } catch (IllegalAccessException ignored) {}

        return null;
    }

    public static void resetTugData(Player player) {
        try {
            wantToLookAtField.set(player, null);
        } catch (IllegalAccessException ignored) {}
    }

    public static void tugEntityLookDirection(LivingEntity livingEntity, Vec3 direction, double strength) {
        if (livingEntity instanceof Player player) {
            try {
                wantToLookAtField.set(player, new TugData(Either.left(direction), strength * 0.333, livingEntity.tickCount + 10));
                if (DistUtil.isLocalPlayer(player)) // Let local player handle adjusting view
                    return;
            } catch (IllegalAccessException ignored) {}
        }

        float xRotO = livingEntity.xRotO;
        float yRotO = livingEntity.yRotO;
        direction = livingEntity.getLookAngle().lerp(direction, strength);
        livingEntity.lookAt(EntityAnchorArgument.Anchor.EYES, livingEntity.getEyePosition().add(direction));
        livingEntity.xRotO = xRotO;
        livingEntity.yRotO = yRotO;
    }

    public static void tugEntityLookDirection(LivingEntity livingEntity, LivingEntity lookAt, double strength) {
        if (livingEntity instanceof Player player) {
            try {
                wantToLookAtField.set(player, new TugData(Either.right(lookAt), strength * 0.333, livingEntity.tickCount + 10));
                if (DistUtil.isLocalPlayer(player)) // Let local player handle adjusting view
                    return;
            } catch (IllegalAccessException ignored) {}
        }

        Vec3 direction = lookAt.getEyePosition().subtract(livingEntity.getEyePosition()).normalize();
        float xRotO = livingEntity.xRotO;
        float yRotO = livingEntity.yRotO;
        direction = livingEntity.getLookAngle().lerp(direction, strength);
        livingEntity.lookAt(EntityAnchorArgument.Anchor.EYES, livingEntity.getEyePosition().add(direction));
        livingEntity.xRotO = xRotO;
        livingEntity.yRotO = yRotO;
    }
}
