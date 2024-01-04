package net.ltxprogrammer.changed.util;

import com.mojang.datafixers.util.Either;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.network.packet.TugCameraPacket;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

public class CameraUtil {
    public static class TugData {
        public final Either<Vec3, LivingEntity> lookAt;
        public final double strength;
        public int ticksExpire;

        public TugData(Either<Vec3, LivingEntity> lookAt, double strength, int ticksExpire) {
            this.lookAt = lookAt;
            this.strength = strength;
            this.ticksExpire = ticksExpire;
        }

        public static TugData readFromBuffer(FriendlyByteBuf buffer) {
            Either<Vec3, LivingEntity> either;

            if (buffer.readBoolean()) {
                either = Either.right((LivingEntity) UniversalDist.getLevel().getEntity(buffer.readInt()));
            } else {
                either = Either.left(new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
            }

            double strength = buffer.readDouble();
            int ticksExpire = buffer.readInt();
            return new TugData(either, strength, ticksExpire);
        }

        public void writeToBuffer(FriendlyByteBuf buffer) {
            lookAt.ifRight(livingEntity -> {
                buffer.writeBoolean(true);
                buffer.writeInt(livingEntity.getId());
            }).ifLeft(vec3 -> {
                buffer.writeBoolean(false);
                buffer.writeDouble(vec3.x);
                buffer.writeDouble(vec3.y);
                buffer.writeDouble(vec3.z);
            });

            buffer.writeDouble(strength);
            buffer.writeInt(ticksExpire);
        }

        public Vec3 getDirection(LivingEntity source, float partialTicks) {
            if (lookAt.left().isPresent())
                return lookAt.left().get();
            else
                return lookAt.right().get().getEyePosition().subtract(source.getEyePosition(partialTicks)).normalize();
        }

        public boolean shouldExpire(LivingEntity source) {
            if (this.lookAt.right().isPresent() && this.lookAt.right().get().isDeadOrDying())
                return true;
            if (source instanceof Player player && (player.isCreative() || player.isSpectator()))
                return true;
            return this.ticksExpire <= 0;
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
            ext.setTugData(new TugData(Either.left(direction), strength * 0.333, 10));
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
            var tug = new TugData(Either.right(lookAt), strength, 10);
            ext.setTugData(tug);
            if (player instanceof ServerPlayer serverPlayer)
                Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new TugCameraPacket(tug));
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
