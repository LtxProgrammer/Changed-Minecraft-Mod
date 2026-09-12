package net.ltxprogrammer.changed.server;

import net.ltxprogrammer.changed.network.packet.ServerboundPlayerActionPacketExt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public interface ServerPlayerGameModeExtension {
    void changed$handleLatexCoverBreakAction(BlockPos blockPos, ServerboundPlayerActionPacketExt.Action action, Direction direction, int maxBuildHeight, int sequence);
}
