package net.ltxprogrammer.changed.util;

import com.mojang.datafixers.util.Either;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class CameraUtil {
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
        if (player instanceof PlayerDataExtension ext)
            return ext.getTugData();
        return null;
    }

    public static void resetTugData(Player player) {
        if (player instanceof PlayerDataExtension ext)
            ext.setTugData(null);
    }

    public static void tugEntityLookDirection(LivingEntity livingEntity, Vec3 direction, double strength) {
        if (livingEntity instanceof Player player && player instanceof PlayerDataExtension ext) {
            ext.setTugData(new TugData(Either.left(direction), strength * 0.333, livingEntity.tickCount + 10));
            if (UniversalDist.isLocalPlayer(player)) // Let local player handle adjusting view
                return;
        }

        float xRotO = livingEntity.xRotO;
        float yRotO = livingEntity.yRotO;
        direction = livingEntity.getLookAngle().lerp(direction, strength);
        livingEntity.lookAt(EntityAnchorArgument.Anchor.EYES, livingEntity.getEyePosition().add(direction));
        livingEntity.xRotO = xRotO;
        livingEntity.yRotO = yRotO;
    }

    public static void tugEntityLookDirection(LivingEntity livingEntity, LivingEntity lookAt, double strength) {
        if (livingEntity instanceof Player player && player instanceof PlayerDataExtension ext) {
            ext.setTugData(new TugData(Either.right(lookAt), strength * 0.333, livingEntity.tickCount + 10));
            if (UniversalDist.isLocalPlayer(player)) // Let local player handle adjusting view
                return;
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
