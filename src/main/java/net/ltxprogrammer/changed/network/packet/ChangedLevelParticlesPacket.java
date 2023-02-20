package net.ltxprogrammer.changed.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.Supplier;

public class ChangedLevelParticlesPacket implements ChangedPacket {
    private static final Logger LOGGER = LogManager.getLogger(ChangedLevelParticlesPacket.class);

    private final double x;
    private final double y;
    private final double z;
    private final float xDist;
    private final float yDist;
    private final float zDist;
    private final float maxSpeed;
    private final int count;
    private final boolean overrideLimiter;
    private final ParticleOptions particle;

    public <T extends ParticleOptions> ChangedLevelParticlesPacket(@NotNull T particle, boolean overrideLimiter, double x, double y, double z, float xDist, float yDist, float zDist, float maxSpeed, int count) {
        this.particle = particle;
        this.overrideLimiter = overrideLimiter;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xDist = xDist;
        this.yDist = yDist;
        this.zDist = zDist;
        this.maxSpeed = maxSpeed;
        this.count = count;
    }

    public ChangedLevelParticlesPacket(FriendlyByteBuf buffer) {
        ParticleType<?> particleType = Registry.PARTICLE_TYPE.get(buffer.readResourceLocation());
        this.overrideLimiter = buffer.readBoolean();
        this.x = buffer.readDouble();
        this.y = buffer.readDouble();
        this.z = buffer.readDouble();
        this.xDist = buffer.readFloat();
        this.yDist = buffer.readFloat();
        this.zDist = buffer.readFloat();
        this.maxSpeed = buffer.readFloat();
        this.count = buffer.readInt();
        this.particle = this.readParticle(buffer, particleType);
    }

    private <T extends ParticleOptions> T readParticle(FriendlyByteBuf buffer, ParticleType<T> type) {
        return type.getDeserializer().fromNetwork(type, buffer);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.particle.getType().getRegistryName());
        buffer.writeBoolean(this.overrideLimiter);
        buffer.writeDouble(this.x);
        buffer.writeDouble(this.y);
        buffer.writeDouble(this.z);
        buffer.writeFloat(this.xDist);
        buffer.writeFloat(this.yDist);
        buffer.writeFloat(this.zDist);
        buffer.writeFloat(this.maxSpeed);
        buffer.writeInt(this.count);
        this.particle.writeToNetwork(buffer);
    }
    
    public static class ClientCode {
        public static boolean handleParticle(ChangedLevelParticlesPacket packet) {
            ClientLevel level = Minecraft.getInstance().level;
            Random random = level.random;

            if (packet.count == 0) {
                double d0 = packet.maxSpeed * packet.xDist;
                double d2 = packet.maxSpeed * packet.yDist;
                double d4 = packet.maxSpeed * packet.zDist;

                try {
                    level.addParticle(packet.particle, packet.overrideLimiter, packet.x, packet.y, packet.z, d0, d2, d4);
                } catch (Throwable throwable1) {
                    LOGGER.warn("Could not spawn particle effect {}", packet.particle);
                    return false;
                }
            } else {
                for(int i = 0; i < packet.count; ++i) {
                    double d1 = random.nextGaussian() * (double)packet.xDist;
                    double d3 = random.nextGaussian() * (double)packet.yDist;
                    double d5 = random.nextGaussian() * (double)packet.zDist;
                    double d6 = random.nextGaussian() * (double)packet.maxSpeed;
                    double d7 = random.nextGaussian() * (double)packet.maxSpeed;
                    double d8 = random.nextGaussian() * (double)packet.maxSpeed;

                    try {
                        level.addParticle(packet.particle, packet.overrideLimiter, packet.x + d1, packet.y + d3, packet.z + d5, d6, d7, d8);
                    } catch (Throwable throwable) {
                        LOGGER.warn("Could not spawn particle effect {}", packet.particle);
                        return false;
                    }
                }
            }

            return true;
        }
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        
        if (context.getDirection().getReceptionSide().isClient()) {
            try {
                if (ClientCode.handleParticle(this))
                    context.setPacketHandled(true);
            } catch (Exception ex) {
                LOGGER.error("Exception while handling particle {}", particle.getType().getRegistryName());
            }
        }
    }
}
