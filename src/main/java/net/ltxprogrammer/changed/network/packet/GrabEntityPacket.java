package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.KeyReference;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

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
        SUIT,
        /**
         * Used to update target on reference change
         */
        REPLACE
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
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                var source = level.getEntity(sourceEntity);
                var target = level.getEntity(targetEntity);

                if (!(source instanceof LivingEntity livingSource)) return;
                if (!(target instanceof LivingEntity livingTarget)) return;

                var latexSource = IAbstractChangedEntity.forEither(livingSource);
                if (latexSource == null)
                    return;

                latexSource.getAbilityInstanceSafe(ChangedAbilities.GRAB_ENTITY_ABILITY.get()).ifPresent(ability -> {
                    switch (type) {
                        case REPLACE -> ability.replaceEntityReference(livingTarget);
                        case RELEASE -> ability.releaseEntity(false);
                        case SUIT -> ability.suitEntity(livingTarget);
                        case ARMS -> ability.grabEntity(livingTarget);
                    }
                });
            });
        }

        else {
            context.setPacketHandled(true);
            if (type == GrabType.REPLACE)
                return CompletableFuture.failedFuture(new IllegalArgumentException("GrabType.REPLACE invalid on client-to-server"));

            return levelFuture.thenAccept(level -> {
                var sender = context.getSender();
                var target = level.getEntity(targetEntity);
                if (!(target instanceof LivingEntity livingTarget))
                    return;
                if (!target.getType().is(ChangedTags.EntityTypes.HUMANOIDS) && !(target instanceof Player))
                    return;
                context.setPacketHandled(true);
                if (sender.getId() == sourceEntity) {
                    if (ProcessTransfur.isPlayerNotLatex(sender))
                        return; // Invalid, sender has to be latex
                } else {
                    return; // Invalid, sender cannot dictate other entities grab action
                }

                ProcessTransfur.ifPlayerTransfurred(sender, variant -> {
                    var ability = variant.getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
                    if (ability == null)
                        return;

                    switch (type) {
                        case RELEASE -> {
                            boolean wasSuited = ability.suited;
                            ability.releaseEntity(true);
                            ChangedSounds.broadcastSound(sender, wasSuited ? ChangedSounds.LATEX_UNSUIT_ENTITY : ChangedSounds.LATEX_UNGRAB_ENTITY, 1.0f, 1.0f);
                        }
                        case SUIT -> {
                            if (livingTarget instanceof Player && !Changed.config.server.isGrabEnabled.get())
                                return;

                            ChangedSounds.broadcastSound(sender, ChangedSounds.LATEX_SUIT_ENTITY, 1.0f, 1.0f);
                            ability.suitEntity(livingTarget);
                        }
                        case ARMS -> {
                            if (livingTarget instanceof Player && !Changed.config.server.isGrabEnabled.get())
                                return;

                            boolean wasSuited = ability.suited;
                            ability.grabEntity(livingTarget);
                            ChangedSounds.broadcastSound(sender, wasSuited ? ChangedSounds.LATEX_UNSUIT_ENTITY : ChangedSounds.LATEX_GRAB_ENTITY, 1.0f, 1.0f);
                        }
                    }
                });

                Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY.with(() -> sender), this); // Echo
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

    public static class GrabKeyState implements ChangedPacket {
        private final int id;
        private final boolean attackKey;
        private final boolean useKey;

        public GrabKeyState(Player player, boolean attackKey, boolean useKey) {
            this.id = player.getId();
            this.attackKey = attackKey;
            this.useKey = useKey;
        }

        public GrabKeyState(FriendlyByteBuf buffer) {
            this.id = buffer.readVarInt();
            this.attackKey = buffer.readBoolean();
            this.useKey = buffer.readBoolean();
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeVarInt(this.id);
            buffer.writeBoolean(this.attackKey);
            buffer.writeBoolean(this.useKey);
        }

        @Override
        public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
            if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                context.setPacketHandled(true);
                return levelFuture.thenAccept(level -> {
                    var entity = level.getEntity(this.id);
                    if (!(entity instanceof Player player))
                        throw new IllegalArgumentException("Cannot get player of id " + id);
                    ProcessTransfur.ifPlayerTransfurred(player, variant -> {
                        variant.ifHasAbility(ChangedAbilities.GRAB_ENTITY_ABILITY.get(), instance -> {
                            instance.attackDown = this.attackKey;
                            instance.useDown = this.useKey;
                        });
                    });
                });
            }

            else {
                context.setPacketHandled(true);
                return levelFuture.thenAccept(level -> {
                    var sender = context.getSender();
                    ProcessTransfur.ifPlayerTransfurred(sender, variant -> {
                        variant.ifHasAbility(ChangedAbilities.GRAB_ENTITY_ABILITY.get(), instance -> {
                            instance.attackDown = this.attackKey;
                            instance.useDown = this.useKey;
                            Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY.with(() -> sender), this);
                        });
                    });
                });
            }
        }
    }

    public static class AnnounceEscapeSeed implements ChangedPacket {
        /** Escapee's ID */
        private final int id;
        private final long seed;

        public AnnounceEscapeSeed(Player player, long seed) {
            this.id = player.getId();
            this.seed = seed;
        }

        public AnnounceEscapeSeed(FriendlyByteBuf buffer) {
            this.id = buffer.readVarInt();
            this.seed = buffer.readLong();
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeVarInt(this.id);
            buffer.writeLong(this.seed);
        }

        @Override
        public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
            if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                context.setPacketHandled(true);
                return levelFuture.thenAccept(level -> {
                    var player = level.getEntity(this.id);
                    if (!(player instanceof LivingEntityDataExtension ext) || ext.getGrabbedBy() == null)
                        throw new IllegalStateException("Player is not grabbed");

                    var ability = AbstractAbility.getAbilityInstance(ext.getGrabbedBy(), ChangedAbilities.GRAB_ENTITY_ABILITY.get());
                    if (ability != null)
                        ability.initializeEscape(this.seed);
                    else
                        throw new IllegalStateException("Grabber does not have grab ability");
                });
            }

            return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
        }
    }

    public static class SyncGrabStrength implements ChangedPacket {
        private final int grabberId;
        private final float grabStrength;
        private final float grabStrengthO;
        private final float suitTransition;
        private final float suitTransitionO;

        public SyncGrabStrength(LivingEntity grabber, float grabStrength, float grabStrengthO, float suitTransition, float suitTransitionO) {
            this.grabberId = grabber.getId();
            this.grabStrength = grabStrength;
            this.grabStrengthO = grabStrengthO;
            this.suitTransition = suitTransition;
            this.suitTransitionO = suitTransitionO;
        }

        public SyncGrabStrength(FriendlyByteBuf buffer) {
            this.grabberId = buffer.readVarInt();
            this.grabStrength = buffer.readFloat();
            this.grabStrengthO = buffer.readFloat();
            this.suitTransition = buffer.readFloat();
            this.suitTransitionO = buffer.readFloat();
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeVarInt(this.grabberId);
            buffer.writeFloat(this.grabStrength);
            buffer.writeFloat(this.grabStrengthO);
            buffer.writeFloat(this.suitTransition);
            buffer.writeFloat(this.suitTransitionO);
        }

        @Override
        public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
            if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                context.setPacketHandled(true);
                return levelFuture.thenAccept(level -> {
                    var grabber = IAbstractChangedEntity.forEitherSafe(level.getEntity(this.grabberId));
                    var ability = grabber.flatMap(entity -> entity.getAbilityInstanceSafe(ChangedAbilities.GRAB_ENTITY_ABILITY.get()))
                            .orElseThrow(() -> new IllegalStateException("No grab ability for entity"));

                    ability.grabStrength = this.grabStrength;
                    ability.grabStrengthO = this.grabStrengthO;
                    ability.suitTransition = this.suitTransition;
                    ability.suitTransitionO = this.suitTransitionO;
                });
            }

            return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
        }
    }

    public static class EscapeKeyState implements ChangedPacket {
        /** Escapee's ID */
        private final int id;
        private final boolean keyForward;
        private final boolean keyBackward;
        private final boolean keyLeft;
        private final boolean keyRight;

        public EscapeKeyState(Player player, boolean keyForward, boolean keyBackward, boolean keyLeft, boolean keyRight) {
            this.id = player.getId();
            this.keyForward = keyForward;
            this.keyBackward = keyBackward;
            this.keyLeft = keyLeft;
            this.keyRight = keyRight;
        }

        public EscapeKeyState(FriendlyByteBuf buffer) {
            this.id = buffer.readVarInt();
            this.keyForward = buffer.readBoolean();
            this.keyBackward = buffer.readBoolean();
            this.keyLeft = buffer.readBoolean();
            this.keyRight = buffer.readBoolean();
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeVarInt(this.id);
            buffer.writeBoolean(this.keyForward);
            buffer.writeBoolean(this.keyBackward);
            buffer.writeBoolean(this.keyLeft);
            buffer.writeBoolean(this.keyRight);
        }

        @Override
        public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
            if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                context.setPacketHandled(true);
                return levelFuture.thenAccept(level -> {
                    if (!(level.getEntity(id) instanceof LivingEntityDataExtension ext) || ext.getGrabbedBy() == null)
                        throw new IllegalStateException("Player is not grabbed");

                    var ability = AbstractAbility.getAbilityInstance(ext.getGrabbedBy(), ChangedAbilities.GRAB_ENTITY_ABILITY.get());
                    if (ability != null) {
                        ability.escapeKeys.queueKeyState(KeyReference.MOVE_FORWARD, this.keyForward);
                        ability.escapeKeys.queueKeyState(KeyReference.MOVE_BACKWARD, this.keyBackward);
                        ability.escapeKeys.queueKeyState(KeyReference.MOVE_LEFT, this.keyLeft);
                        ability.escapeKeys.queueKeyState(KeyReference.MOVE_RIGHT, this.keyRight);
                    }

                    else
                        throw new IllegalStateException("Grabber does not have grab ability");
                });
            }

            else {
                context.setPacketHandled(true);
                return levelFuture.thenAccept(level -> {
                    final var entity = context.getSender();
                    if (!(entity instanceof LivingEntityDataExtension ext) || ext.getGrabbedBy() == null)
                        throw new IllegalStateException("Player is not grabbed");

                    var ability = AbstractAbility.getAbilityInstance(ext.getGrabbedBy(), ChangedAbilities.GRAB_ENTITY_ABILITY.get());
                    if (ability != null) {
                        ability.escapeKeys.queueKeyState(KeyReference.MOVE_FORWARD, this.keyForward);
                        ability.escapeKeys.queueKeyState(KeyReference.MOVE_BACKWARD, this.keyBackward);
                        ability.escapeKeys.queueKeyState(KeyReference.MOVE_LEFT, this.keyLeft);
                        ability.escapeKeys.queueKeyState(KeyReference.MOVE_RIGHT, this.keyRight);
                        Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity),
                                new EscapeKeyState(entity, keyForward, keyBackward, keyLeft, keyRight));
                    }

                    else
                        throw new IllegalStateException("Grabber does not have grab ability");
                });
            }
        }
    }

    public static GrabKeyState keyState(Player player, boolean attack, boolean use) {
        return new GrabKeyState(player, attack, use);
    }
}
