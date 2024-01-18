package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.LockToPlayerMover;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class GrabEntityPacket implements ChangedPacket {
    public enum GrabType {
        /**
         * Target will be release by the latex
         */
        RELEASE,
        /**
         * Target is grabbed by latex by their arms
         */
        ARMS,
        /**
         * Target is fully encased by latex entity
         */
        SUIT
    }

    public final int sourceEntity;
    public final int targetEntity;
    public final GrabType type;

    public GrabEntityPacket(LivingEntity source, LivingEntity target, GrabType type) {
        this.sourceEntity = source.getId();
        this.targetEntity = target.getId();
        this.type = type;
    }

    public GrabEntityPacket(FriendlyByteBuf buffer) {
        this.sourceEntity = buffer.readInt();
        this.targetEntity = buffer.readInt();
        this.type = GrabType.values()[buffer.readInt()];
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(sourceEntity);
        buffer.writeInt(targetEntity);
        buffer.writeInt(type.ordinal());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        var sender = context.getSender();
        if (context.getDirection().getReceptionSide().isServer() && sender != null) {
            var level = sender.getLevel();
            var target = level.getEntity(targetEntity);
            if (!(target instanceof LivingEntity livingTarget))
                return;
            if (!target.getType().is(ChangedTags.EntityTypes.HUMANOIDS) && !(target instanceof Player))
                return;
            context.setPacketHandled(true);
            if (sender.getId() == sourceEntity) {
                if (ProcessTransfur.isPlayerOrganic(sender))
                    return; // Invalid, sender has to be latex
            } else {
                return; // Invalid, sender cannot dictate other entities grab action
            }

            ProcessTransfur.ifPlayerLatex(sender, variant -> {
                var ability = variant.getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
                if (ability == null)
                    return;

                switch (type) {
                    case RELEASE -> {
                        boolean wasSuited = ability.suited;
                        ability.releaseEntity();
                        ChangedSounds.broadcastSound(sender, wasSuited ? ChangedSounds.POISON : ChangedSounds.BLOW1, 1.0f, 1.0f);
                    }
                    case SUIT -> {
                        ability.suitEntity(livingTarget);
                        ChangedSounds.broadcastSound(sender, ChangedSounds.POISON, 1.0f, 1.0f);
                    }
                    case ARMS -> {
                        ability.grabEntity(livingTarget);
                        ChangedSounds.broadcastSound(sender, ChangedSounds.BLOW1, 1.0f, 1.0f);
                    }
                }
            });

            Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY.with(() -> sender), this); // Echo
        }

        else {
            Level localLevel = UniversalDist.getLevel();
            var source = localLevel.getEntity(sourceEntity);
            var target = localLevel.getEntity(targetEntity);

            if (!(source instanceof LivingEntity livingSource)) return;
            if (!(target instanceof LivingEntity livingTarget)) return;

            ProcessTransfur.ifPlayerLatex(EntityUtil.playerOrNull(livingSource), variant -> {
                variant.ifHasAbility(ChangedAbilities.GRAB_ENTITY_ABILITY.get(), ability -> {
                    switch (type) {
                        case RELEASE -> ability.releaseEntity();
                        case SUIT -> ability.suitEntity(livingTarget);
                        case ARMS -> ability.grabEntity(livingTarget);
                    }
                });
            });
        }
    }

    public static GrabEntityPacket release(Player latexPlayer, LivingEntity entity) {
        return new GrabEntityPacket(latexPlayer, entity, GrabType.RELEASE);
    }

    public static GrabEntityPacket initialGrab(Player latexPlayer, LivingEntity entity) {
        return new GrabEntityPacket(latexPlayer, entity, GrabType.ARMS);
    }

    public static GrabEntityPacket suitGrab(Player latexPlayer, LivingEntity entity) {
        return new GrabEntityPacket(latexPlayer, entity, GrabType.SUIT);
    }

    public static class KeyState implements ChangedPacket {
        private final boolean attackKey;
        private final boolean useKey;

        public KeyState(boolean attackKey, boolean useKey) {
            this.attackKey = attackKey;
            this.useKey = useKey;
        }

        public KeyState(FriendlyByteBuf buffer) {
            this.attackKey = buffer.readBoolean();
            this.useKey = buffer.readBoolean();
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeBoolean(this.attackKey);
            buffer.writeBoolean(this.useKey);
        }

        @Override
        public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
            var sender = contextSupplier.get().getSender();
            if (sender != null) {
                ProcessTransfur.ifPlayerLatex(sender, variant -> {
                    variant.ifHasAbility(ChangedAbilities.GRAB_ENTITY_ABILITY.get(), instance -> {
                        instance.attackDown = this.attackKey;
                        instance.useDown = this.useKey;
                        contextSupplier.get().setPacketHandled(true);
                    });
                });
            }
        }
    }

    public static GrabEntityPacket.KeyState keyState(boolean attack, boolean use) {
        return new GrabEntityPacket.KeyState(attack, use);
    }
}
