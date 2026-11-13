package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.server.ServerPlayerGameModeExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ServerboundPlayerActionPacketExt implements ChangedPacket {
    private final BlockPos pos;
    private final Direction direction;
    private final Action action;
    private final int sequence;

    public ServerboundPlayerActionPacketExt(Action action, BlockPos pos, Direction direction, int sequence) {
        this.action = action;
        this.pos = pos.immutable();
        this.direction = direction;
        this.sequence = sequence;
    }

    public ServerboundPlayerActionPacketExt(Action action, BlockPos pos, Direction direction) {
        this(action, pos, direction, 0);
    }

    public ServerboundPlayerActionPacketExt(FriendlyByteBuf buffer) {
        this.action = buffer.readEnum(Action.class);
        this.pos = buffer.readBlockPos();
        this.direction = Direction.from3DDataValue(buffer.readUnsignedByte());
        this.sequence = buffer.readVarInt();
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.action);
        buffer.writeBlockPos(this.pos);
        buffer.writeByte(this.direction.get3DDataValue());
        buffer.writeVarInt(this.sequence);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {

            return levelFuture.thenAccept(level -> {
                ServerPlayer player = context.getSender();

                BlockPos blockpos = this.getPos();
                player.resetLastActionTime();
                Action action = this.getAction();
                switch (action) {
                    case START_DESTROY_LATEX_COVER:
                    case ABORT_DESTROY_LATEX_COVER:
                    case STOP_DESTROY_LATEX_COVER:
                        ((ServerPlayerGameModeExtension)player.gameMode).changed$handleLatexCoverBreakAction(blockpos, action, this.getDirection(), player.level().getMaxBuildHeight(), this.getSequence());
                        player.connection.ackBlockChangesUpTo(this.getSequence());
                        return;
                    default:
                        throw new IllegalArgumentException("Invalid player action");
                }
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.SERVER));
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public Action getAction() {
        return this.action;
    }

    public int getSequence() {
        return this.sequence;
    }

    public enum Action {
        START_DESTROY_LATEX_COVER(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK),
        ABORT_DESTROY_LATEX_COVER(ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK),
        STOP_DESTROY_LATEX_COVER(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK);

        private final @Nullable ServerboundPlayerActionPacket.Action parallel;

        Action(@Nullable ServerboundPlayerActionPacket.Action parallel) {
            this.parallel = parallel;
        }

        public @Nullable ServerboundPlayerActionPacket.Action getParallel() {
            return parallel;
        }
    }
}
