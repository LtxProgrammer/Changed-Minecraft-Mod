package net.ltxprogrammer.changed.mixin.server;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.active.multiarm.AutotoolAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.network.packet.LatexCoverUpdatePacket;
import net.ltxprogrammer.changed.network.packet.ServerboundPlayerActionPacketExt;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.server.ServerPlayerGameModeExtension;
import net.ltxprogrammer.changed.world.LatexCoverGetter;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.ltxprogrammer.changed.world.ServerLevelExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.network.NetworkDirection;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin implements ServerPlayerGameModeExtension {
    @Shadow @Final protected ServerPlayer player;
    @Shadow protected ServerLevel level;

    @Shadow public abstract boolean isCreative();

    @Shadow protected abstract void debugLogging(BlockPos p_215126_, boolean p_215127_, int p_215128_, String p_215129_);

    @Shadow private BlockPos destroyPos;
    @Shadow private boolean isDestroyingBlock;
    @Shadow private int lastSentState;
    @Shadow private int gameTicks;
    @Shadow private int destroyProgressStart;
    @Shadow private boolean hasDelayedDestroy;
    @Shadow private BlockPos delayedDestroyPos;
    @Shadow private int delayedTickStart;
    @Shadow @Final private static Logger LOGGER;
    @Shadow private GameType gameModeForPlayer;
    @Unique protected boolean changed$isDestroyingAlt = false;
    @Unique protected boolean changed$lastBlockHarvested = false;
    @Unique protected Direction changed$lastDirection = Direction.DOWN;
    @Unique protected BlockState changed$lastRemovedBlockState = Blocks.AIR.defaultBlockState();
    @Unique protected ItemStack changed$lastUsedItemToBreak = ItemStack.EMPTY;
    @Unique protected ItemStack changed$preferredDestroyWith = null;

    @Inject(method = "setGameModeForPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameType;updatePlayerAbilities(Lnet/minecraft/world/entity/player/Abilities;)V"))
    public void andApplyFlyAbility(GameType newType, GameType oldType, CallbackInfo callback) {
        ProcessTransfur.ifPlayerTransfurred(this.player, variant -> {
            if (variant.canCreativeFly()) {
                this.player.getAbilities().mayfly = true;
                if (!this.player.onGround() && newType.isSurvival()) {
                    this.player.getAbilities().flying = true;
                }
            }
        });
    }

    @WrapOperation(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"))
    public ItemStack changed$getAutotoolItem(ServerPlayer instance, Operation<ItemStack> original, @Local BlockState blockState) {
        if (!changed$isDestroyingAlt) {
            var autotool = AbstractAbility.getAbilityInstance(instance, ChangedAbilities.AUTOTOOL.get());
            if (autotool == null || !autotool.isActive()) {
                changed$lastUsedItemToBreak = original.call(instance);
                return changed$lastUsedItemToBreak;
            }

            changed$lastUsedItemToBreak = AutotoolAbility.getItemToUse(IAbstractChangedEntity.forPlayer(instance), blockState);
            return changed$lastUsedItemToBreak;
        } else {
            return changed$preferredDestroyWith;
        }
    }

    @WrapOperation(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayerGameMode;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z", remap = false))
    public boolean changed$cacheHarvested(ServerPlayerGameMode instance, BlockPos blockPos, boolean canHarvest, Operation<Boolean> original, @Local BlockState blockState) {
        changed$lastRemovedBlockState = blockState;
        boolean removed = original.call(instance, blockPos, canHarvest);
        changed$lastBlockHarvested = removed && canHarvest;
        return removed;
    }

    @WrapMethod(method = "handleBlockBreakAction")
    public void changed$cacheDirection(BlockPos blockPos, ServerboundPlayerActionPacket.Action action, Direction direction, int maxBuildHeight, int sequence, Operation<Void> original) {
        changed$lastDirection = direction;
        original.call(blockPos, action, direction, maxBuildHeight, sequence);
    }

    @WrapMethod(method = "destroyBlock")
    public boolean changed$andDestroyBlockBelow(BlockPos blockPos, Operation<Boolean> original) {
        boolean broke = original.call(blockPos);
        // Only excavate when the block was harvested and was not instabreak
        if (broke && changed$lastBlockHarvested && changed$lastRemovedBlockState.getDestroySpeed(level, blockPos) > 0.0f) {
            var excavate = AbstractAbility.getAbilityInstance(this.player, ChangedAbilities.EXCAVATE.get());
            if (excavate == null || !excavate.isActive())
                return true;

            if (!changed$lastDirection.getAxis().isHorizontal())
                return true;

            var autotool = AbstractAbility.getAbilityInstance(this.player, ChangedAbilities.AUTOTOOL.get());

            BlockPos below = blockPos.below();
            BlockPos occlusionPos = below.relative(changed$lastDirection);
            BlockState occlusionState = level.getBlockState(occlusionPos);
            if (occlusionState.isFaceSturdy(level, occlusionPos, changed$lastDirection.getOpposite(), SupportType.FULL))
                return true; // Face of the bottom block is occluded, don't break

            BlockState belowState = level.getBlockState(below);

            var correctItem = autotool == null || !autotool.isActive() ?
                    AutotoolAbility.getFirstMainHandCorrectItem(IAbstractChangedEntity.forPlayer(this.player), belowState, changed$lastUsedItemToBreak) :
                    AutotoolAbility.getFirstCorrectItem(IAbstractChangedEntity.forPlayer(this.player), belowState, changed$lastUsedItemToBreak);
            if (correctItem == null)
                return true;

            changed$isDestroyingAlt = true;
            changed$preferredDestroyWith = correctItem;
            original.call(below);
            changed$preferredDestroyWith = null;
            changed$isDestroyingAlt = false;
        }
        return broke;
    }

    @Override
    public void changed$handleLatexCoverBreakAction(BlockPos blockPos, ServerboundPlayerActionPacketExt.Action action, Direction direction, int maxBuildHeight, int sequence) {
        PlayerInteractEvent.LeftClickBlock event = ForgeHooks.onLeftClickBlock(this.player, blockPos, direction, action.getParallel());
        if (!event.isCanceled() && (this.isCreative() || event.getResult() != Event.Result.DENY)) {
            if (!this.player.canReach(blockPos, (double)1.5F)) {
                this.debugLogging(blockPos, false, sequence, "too far");
            } else if (blockPos.getY() >= maxBuildHeight) {
                this.player.connection.send(Changed.PACKET_HANDLER.toVanillaPacket(
                        new LatexCoverUpdatePacket(blockPos, LatexCoverState.getAt(this.level, blockPos)), NetworkDirection.PLAY_TO_CLIENT));
                this.debugLogging(blockPos, false, sequence, "too high");
            } else if (action == ServerboundPlayerActionPacketExt.Action.START_DESTROY_LATEX_COVER) {
                if (!this.level.mayInteract(this.player, blockPos)) {
                    this.player.connection.send(Changed.PACKET_HANDLER.toVanillaPacket(
                            new LatexCoverUpdatePacket(blockPos, LatexCoverState.getAt(this.level, blockPos)), NetworkDirection.PLAY_TO_CLIENT));
                    this.debugLogging(blockPos, false, sequence, "may not interact");
                    return;
                }

                if (this.isCreative()) {
                    this.changed$destroyCoverAndAck(blockPos, sequence, "creative destroy");
                    return;
                }

                if (this.player.blockActionRestricted(this.level, blockPos, this.gameModeForPlayer)) {
                    this.player.connection.send(Changed.PACKET_HANDLER.toVanillaPacket(
                            new LatexCoverUpdatePacket(blockPos, LatexCoverState.getAt(this.level, blockPos)), NetworkDirection.PLAY_TO_CLIENT));
                    this.debugLogging(blockPos, false, sequence, "block action restricted");
                    return;
                }

                this.destroyProgressStart = this.gameTicks;
                float f = 1.0F;
                LatexCoverState coverState = LatexCoverState.getAt(this.level, blockPos);
                if (!coverState.isAir()) {
                    if (event.getUseBlock() != Event.Result.DENY) {
                        coverState.attack(this.level, blockPos, this.player);
                    }

                    f = coverState.getDestroyProgress(this.player, LatexCoverGetter.wrap(this.player.level()), blockPos);
                }

                if (!coverState.isAir() && f >= 1.0F) {
                    this.changed$destroyCoverAndAck(blockPos, sequence, "insta mine");
                } else {
                    if (this.isDestroyingBlock) {
                        this.player.connection.send(Changed.PACKET_HANDLER.toVanillaPacket(
                                new LatexCoverUpdatePacket(this.destroyPos, LatexCoverState.getAt(this.level, this.destroyPos)), NetworkDirection.PLAY_TO_CLIENT));
                        this.debugLogging(blockPos, false, sequence, "abort destroying since another started (client insta mine, server disagreed)");
                    }

                    this.isDestroyingBlock = true;
                    this.destroyPos = blockPos.immutable();
                    int i = (int)(f * 10.0F);
                    ServerLevelExtension.INSTANCE.destroyLatexCoverProgress(this.level, this.player.getId(), blockPos, i);
                    this.debugLogging(blockPos, true, sequence, "actual start of destroying");
                    this.lastSentState = i;
                }
            } else if (action == ServerboundPlayerActionPacketExt.Action.STOP_DESTROY_LATEX_COVER) {
                if (blockPos.equals(this.destroyPos)) {
                    int j = this.gameTicks - this.destroyProgressStart;
                    LatexCoverState coverState = LatexCoverState.getAt(this.level, blockPos);
                    if (!coverState.isAir()) {
                        float f1 = coverState.getDestroyProgress(this.player, LatexCoverGetter.wrap(this.player.level()), blockPos) * (float)(j + 1);
                        if (f1 >= 0.7F) {
                            this.isDestroyingBlock = false;
                            ServerLevelExtension.INSTANCE.destroyLatexCoverProgress(this.level, this.player.getId(), blockPos, -1);
                            this.changed$destroyCoverAndAck(blockPos, sequence, "destroyed");
                            return;
                        }

                        if (!this.hasDelayedDestroy) {
                            this.isDestroyingBlock = false;
                            this.hasDelayedDestroy = true;
                            this.delayedDestroyPos = blockPos;
                            this.delayedTickStart = this.destroyProgressStart;
                        }
                    }
                }

                this.debugLogging(blockPos, true, sequence, "stopped destroying");
            } else if (action == ServerboundPlayerActionPacketExt.Action.ABORT_DESTROY_LATEX_COVER) {
                this.isDestroyingBlock = false;
                if (!Objects.equals(this.destroyPos, blockPos)) {
                    LOGGER.warn("Mismatch in destroy block pos: {} {}", this.destroyPos, blockPos);
                    ServerLevelExtension.INSTANCE.destroyLatexCoverProgress(this.level, this.player.getId(), blockPos, -1);
                    this.debugLogging(blockPos, true, sequence, "aborted mismatched destroying");
                }

                ServerLevelExtension.INSTANCE.destroyLatexCoverProgress(this.level, this.player.getId(), blockPos, -1);
                this.debugLogging(blockPos, true, sequence, "aborted destroying");
            }

        }
    }
    
    @Unique
    public void changed$destroyCoverAndAck(BlockPos blockPos, int sequence, String reason) {
        if (this.changed$destroyLatexCover(blockPos)) {
            this.debugLogging(blockPos, true, sequence, reason);
        } else {
            this.player.connection.send(Changed.PACKET_HANDLER.toVanillaPacket(
                    new LatexCoverUpdatePacket(blockPos, LatexCoverState.getAt(this.level, blockPos)), NetworkDirection.PLAY_TO_CLIENT));
            this.debugLogging(blockPos, false, sequence, reason);
        }
    }
    
    @Unique
    public boolean changed$destroyLatexCover(BlockPos blockPos) {
        LatexCoverState coverState = LatexCoverState.getAt(this.level, blockPos);
        int exp = 0;//ForgeHooks.onBlockBreakEvent(this.level, this.gameModeForPlayer, this.player, blockPos);
        if (exp == -1) {
            return false;
        } else {
            LatexType latexType = coverState.getType();
            if (this.player.getMainHandItem().onBlockStartBreak(blockPos, this.player)) {
                return false;
            } else if (this.player.blockActionRestricted(this.level, blockPos, this.gameModeForPlayer)) {
                return false;
            } else if (this.isCreative()) {
                this.changed$removeLatexCover(blockPos, false);
                return true;
            } else {
                ItemStack itemstack = this.player.getMainHandItem();
                ItemStack itemstack1 = itemstack.copy();
                boolean flag1 = coverState.canHarvestLatexCover(LatexCoverGetter.wrap(this.level), blockPos, this.player);
                BlockState propertyState = coverState.getBlockStateForProperties();
                itemstack.mineBlock(this.level, propertyState, blockPos, this.player);
                if (itemstack.isEmpty() && !itemstack1.isEmpty()) {
                    ForgeEventFactory.onPlayerDestroyItem(this.player, itemstack1, InteractionHand.MAIN_HAND);
                }

                boolean flag = this.changed$removeLatexCover(blockPos, flag1);
                if (flag && flag1) {
                    latexType.playerDestroy(this.level, this.player, blockPos, coverState, itemstack1);
                }

                if (flag && exp > 0) {
                    coverState.getType().popExperience(this.level, blockPos, exp);
                }

                return true;
            }
        }
    }

    @Unique
    private boolean changed$removeLatexCover(BlockPos pos, boolean canHarvest) {
        LatexCoverState state = LatexCoverState.getAt(this.level, pos);
        boolean removed = state.onDestroyedByPlayer(this.level, pos, this.player, canHarvest);
        if (removed) {
            state.getType().destroy(this.level, pos, state);
        }

        return removed;
    }
}
