package net.ltxprogrammer.changed.world;

import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;

public class LevelExtension {
    public static final LevelExtension INSTANCE = new LevelExtension();

    public void destroyLatexCoverProgress(LevelAccessor level, int entityId, BlockPos blockPos, int progress) {

    }

    public void setCoversDirty(LevelAccessor level, BlockPos blockPos, LatexCoverState oldState, LatexCoverState recordedState) {

    }

    public void sendCoverUpdated(LevelAccessor level, BlockPos blockPos, LatexCoverState oldState, LatexCoverState newState, int flags) {

    }

    public void coverUpdated(LevelAccessor level, BlockPos blockPos, LatexType latexType) {

    }

    public void onLatexCoverStateChange(LevelAccessor level, BlockPos blockPos, LatexCoverState oldState, LatexCoverState newState) {

    }

    public boolean destroyLatexCover(LevelAccessor level, BlockPos blockPos, boolean doDrops, @Nullable Entity cause) {
        return destroyLatexCover(level, blockPos, doDrops, cause, 512);
    }

    public void customLevelEvent(LevelAccessor level, @Nullable Player source, int id, BlockPos blockPos, int param) {}

    public void customLevelEvent(LevelAccessor level, int id, BlockPos blockPos, int param) {
        customLevelEvent(level, null, id, blockPos, param);
    }

    public boolean destroyLatexCover(LevelAccessor level, BlockPos blockPos, boolean doDrops, @Nullable Entity cause, int timeToLive) {
        LatexCoverState coverState = LatexCoverState.getAt(level, blockPos);
        if (coverState.isAir()) {
            return false;
        } else {
            this.customLevelEvent(level, 2001, blockPos, ChangedLatexTypes.getLatexCoverStateIDMap().getId(coverState));

            if (doDrops) {
                LatexType.dropResources(coverState, (Level)level, blockPos, cause, ItemStack.EMPTY);
            }

            boolean flag = LatexCoverState.setAt(level, blockPos, ChangedLatexTypes.NONE.get().defaultCoverState(), 3, timeToLive);
            if (flag) {
                //this.gameEvent(GameEvent.BLOCK_DESTROY, blockPos, GameEvent.Context.of(cause, coverState));
            }

            return flag;
        }
    }
}
