package net.ltxprogrammer.changed.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.network.packet.TugCameraPacket;
import net.minecraft.Util;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class CameraUtil {
    public static class TugData {
        public final Either<Vec3, Integer> lookAt;
        public final double strength;
        public int ticksExpire;
        private LivingEntity cachedEntity = null;

        public TugData(Either<Vec3, Integer> lookAt, double strength, int ticksExpire) {
            this.lookAt = lookAt;
            this.strength = strength;
            this.ticksExpire = ticksExpire;
        }

        public static TugData readFromBuffer(FriendlyByteBuf buffer) {
            Either<Vec3, Integer> either;

            if (buffer.readBoolean()) {
                either = Either.right(buffer.readInt());
            } else {
                either = Either.left(new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
            }

            double strength = buffer.readDouble();
            int ticksExpire = buffer.readInt();
            return new TugData(either, strength, ticksExpire);
        }

        public void writeToBuffer(FriendlyByteBuf buffer) {
            lookAt.ifRight(id -> {
                buffer.writeBoolean(true);
                buffer.writeInt(id);
            }).ifLeft(vec3 -> {
                buffer.writeBoolean(false);
                buffer.writeDouble(vec3.x);
                buffer.writeDouble(vec3.y);
                buffer.writeDouble(vec3.z);
            });

            buffer.writeDouble(strength);
            buffer.writeInt(ticksExpire);
        }

        protected LivingEntity getEntity(Level level) {
            if (cachedEntity == null)
                cachedEntity = (LivingEntity)level.getEntity(lookAt.right().orElseThrow());
            return cachedEntity;
        }

        public Vec3 getDirection(LivingEntity source, float partialTicks) {
            if (lookAt.left().isPresent())
                return lookAt.left().get();
            else {
                return getEntity(source.level()).getEyePosition().subtract(source.getEyePosition(partialTicks)).normalize();
            }
        }

        public boolean shouldExpire(LivingEntity source) {
            if (this.lookAt.right().isPresent() && getEntity(source.level()).isDeadOrDying())
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
            var tug = new TugData(Either.left(direction), strength, 10);
            ext.setTugData(tug);
            if (player instanceof ServerPlayer serverPlayer)
                Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new TugCameraPacket(tug));
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
            var tug = new TugData(Either.right(lookAt.getId()), strength, 10);
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

    private static @NotNull Matrix4f viewSpaceToWorldSpaceMatrix = Util.make(new Matrix4f(), Matrix4f::identity);
    public static void setViewSpaceToWorldSpaceMatrix(@NotNull Matrix4f matrix) {
        viewSpaceToWorldSpaceMatrix = matrix;
    }

    public static Vector4f toWorldSpace(Vector4f localSpace, PoseStack.Pose localToModel) {
        RenderSystem.assertOnRenderThread();

        final Vector4f modelSpace = new Vector4f();
        localSpace.mul(localToModel.pose(), modelSpace);

        final Matrix4f cameraMatrix = RenderSystem.getModelViewMatrix();

        final Vector4f viewSpace = new Vector4f();
        modelSpace.mul(cameraMatrix, viewSpace); // viewSpace is where the vertex resides on the client's screen

        final Vector4f worldSpace = new Vector4f();
        viewSpace.mul(viewSpaceToWorldSpaceMatrix, worldSpace);

        return worldSpace;

        //final Matrix3f normalMatrix = RenderSystem.getInverseViewRotationMatrix();

        /*Vector4f v = new Vector4f(modelSpace.x(), modelSpace.y(), modelSpace.z(), modelSpace.w());
        v.mul(modelSpaceToWorldSpaceMatrix);
        return v;*/
    }

    public static void decomposeZYX(Matrix3fc rotationMatrix, TriConsumer<Float, Float, Float> componentConsumer) {
        // matrix decomposition from https://www.geometrictools.com/Documentation/EulerAngles.pdf
        // JOML matrices are column-major, so switch row-major terms from example
        if (rotationMatrix.m02() > -1.0f && rotationMatrix.m02() < 1.0f) {
            float y = (float) Math.asin(-rotationMatrix.m02());
            float z = (float) Math.atan2(rotationMatrix.m01(), rotationMatrix.m00());
            float x = (float) Math.atan2(rotationMatrix.m12(), rotationMatrix.m22());
            componentConsumer.accept(x, y, z);
        } else {
            int sign = Mth.sign(rotationMatrix.m02());
            float y = sign * -Mth.HALF_PI;
            float z = sign * (float) Math.atan2(-rotationMatrix.m21(), rotationMatrix.m11());
            float x = 0f;
            componentConsumer.accept(x, y, z);
        }
    }

    public static void decomposeZYX(Matrix3fc rotationMatrix, ModelPart part) {
        decomposeZYX(rotationMatrix, (xRot, yRot, zRot) -> {
            part.xRot = xRot;
            part.yRot = yRot;
            part.zRot = zRot;
        });
    }
}
