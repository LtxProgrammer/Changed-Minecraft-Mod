package net.ltxprogrammer.changed.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.ClientLevelExtension;
import net.ltxprogrammer.changed.client.MultiPlayerGameModeExtension;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.network.packet.ServerboundPlayerActionPacketExt;
import net.ltxprogrammer.changed.world.LatexCoverGetter;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.network.NetworkDirection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Function;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin implements MultiPlayerGameModeExtension {
    @Shadow @Final private Minecraft minecraft;

    @Shadow private GameType localPlayerMode;

    @Shadow private boolean isDestroying;

    @Shadow @Final private ClientPacketListener connection;

    @Shadow private BlockPos destroyBlockPos;

    @Shadow private float destroyProgress;

    @Shadow protected abstract void ensureHasSentCarriedItem();

    @Shadow private int destroyDelay;

    @Unique
    protected void changed$startGeneralPrediction(ClientLevel level, Int2ObjectFunction<Object> action) {
        try (BlockStatePredictionHandler blockstatepredictionhandler = level.getBlockStatePredictionHandler().startPredicting()) {
            int i = blockstatepredictionhandler.currentSequence();
            Packet<?> packet = Changed.PACKET_HANDLER.toVanillaPacket(action.apply(i), NetworkDirection.PLAY_TO_SERVER);
            this.connection.send(packet);
        }
    }

    @Shadow protected abstract boolean sameDestroyTarget(BlockPos p_105282_);

    @Shadow private float destroyTicks;

    @Shadow public abstract int getDestroyStage();

    @Shadow private ItemStack destroyingItem;

    @Shadow public abstract void stopDestroyBlock();

    @Unique private boolean changed$isDestroyingCover = false;
    @Unique private boolean changed$isExpectingDestroyCover = false;

    @Unique
    private boolean changed$sameDestroyTargetLatexCover(BlockPos pos) {
        changed$isExpectingDestroyCover = true;
        boolean result = this.sameDestroyTarget(pos);
        changed$isExpectingDestroyCover = false;
        return result;
    }

    @WrapMethod(method = "sameDestroyTarget")
    private boolean changed$includeDestroyCover(BlockPos blockPos, Operation<Boolean> original) {
        return original.call(blockPos) &&
                changed$isDestroyingCover == changed$isExpectingDestroyCover;
    }

    @Override
    public boolean changed$destroyLatexCover(BlockPos pos) {
        if (this.minecraft.player.getMainHandItem().onBlockStartBreak(pos, this.minecraft.player)) {
            return false;
        } else if (this.minecraft.player.blockActionRestricted(this.minecraft.level, pos, this.localPlayerMode)) {
            return false;
        } else {
            Level level = this.minecraft.level;
            LatexCoverState coverState = LatexCoverState.getAt(level, pos);
            Block coverBlock = coverState.getType().getBlock();
            BlockState blockState = coverBlock != null ? coverBlock.defaultBlockState() : Blocks.AIR.defaultBlockState();
            if (!this.minecraft.player.getMainHandItem().getItem().canAttackBlock(blockState, level, pos, this.minecraft.player)) {
                return false;
            } else {
                LatexType latexType = coverState.getType();
                if (coverState.isAir()) {
                    return false;
                } else {
                    boolean flag = coverState.onDestroyedByPlayer(level, pos, this.minecraft.player, false);
                    if (flag) {
                        latexType.destroy(level, pos, coverState);
                    }

                    return flag;
                }
            }
        }
    }

    @Override
    public boolean changed$startDestroyLatexCover(BlockPos pos, Direction direction) {
        if (this.minecraft.player.blockActionRestricted(this.minecraft.level, pos, this.localPlayerMode)) {
            return false;
        } else if (!this.minecraft.level.getWorldBorder().isWithinBounds(pos)) {
            return false;
        } else {
            if (this.localPlayerMode.isCreative()) {
                /*LatexCoverState coverState = LatexCoverState.getAt(this.minecraft.level, pos);
                this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, pos, coverState, 1.0F);*/
                this.changed$startGeneralPrediction(this.minecraft.level, (sequence) -> {
                    if (!ForgeHooks.onLeftClickBlock(this.minecraft.player, pos, direction, ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK).isCanceled()) {
                        this.changed$destroyLatexCover(pos);
                    }

                    return new ServerboundPlayerActionPacketExt(ServerboundPlayerActionPacketExt.Action.START_DESTROY_LATEX_COVER, pos, direction, sequence);
                });
                this.destroyDelay = 5;
            } else if (!this.isDestroying || !this.changed$sameDestroyTargetLatexCover(pos)) {
                if (this.isDestroying && this.changed$isDestroyingCover) {
                    this.connection.send(Changed.PACKET_HANDLER.toVanillaPacket(
                            new ServerboundPlayerActionPacketExt(ServerboundPlayerActionPacketExt.Action.ABORT_DESTROY_LATEX_COVER, this.destroyBlockPos, direction), NetworkDirection.PLAY_TO_SERVER));
                } else if (this.isDestroying) {
                    this.stopDestroyBlock();
                }

                PlayerInteractEvent.LeftClickBlock event = ForgeHooks.onLeftClickBlock(this.minecraft.player, pos, direction, ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK);
                LatexCoverState coverState = LatexCoverState.getAt(this.minecraft.level, pos);
                //this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, pos, coverState, 0.0F);
                this.changed$startGeneralPrediction(this.minecraft.level, (sequence) -> {
                    boolean flag = !coverState.isAir();
                    if (flag && this.destroyProgress == 0.0F && event.getUseBlock() != Event.Result.DENY) {
                        coverState.attack(this.minecraft.level, pos, this.minecraft.player);
                    }

                    ServerboundPlayerActionPacketExt packet = new ServerboundPlayerActionPacketExt(ServerboundPlayerActionPacketExt.Action.START_DESTROY_LATEX_COVER, pos, direction, sequence);
                    if (event.getUseItem() == Event.Result.DENY) {
                        return packet;
                    } else {
                        if (flag && coverState.getDestroyProgress(this.minecraft.player, LatexCoverGetter.wrap(this.minecraft.player.level()), pos) >= 1.0F) {
                            this.changed$destroyLatexCover(pos);
                        } else {
                            this.isDestroying = true;
                            this.changed$isDestroyingCover = true;
                            this.destroyBlockPos = pos;
                            this.destroyingItem = this.minecraft.player.getMainHandItem();
                            this.destroyProgress = 0.0F;
                            this.destroyTicks = 0.0F;
                            ClientLevelExtension.INSTANCE.destroyLatexCoverProgress(this.minecraft.level, this.minecraft.player.getId(), this.destroyBlockPos, this.getDestroyStage());
                        }

                        return packet;
                    }
                });
            }

            return true;
        }
    }

    @Override
    public void changed$stopDestroyLatexCover() {
        if (this.isDestroying && this.changed$isDestroyingCover) {
            /*LatexCoverState coverState = LatexCoverState.getAt(this.minecraft.level.getBlockState, this.destroyBlockPos);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, this.destroyBlockPos, coverState, -1.0F);*/
            this.connection.send(Changed.PACKET_HANDLER.toVanillaPacket(
                    new ServerboundPlayerActionPacketExt(ServerboundPlayerActionPacketExt.Action.ABORT_DESTROY_LATEX_COVER, this.destroyBlockPos, Direction.DOWN), NetworkDirection.PLAY_TO_SERVER));
            this.isDestroying = false;
            this.changed$isDestroyingCover = false;
            this.destroyProgress = 0.0F;
            ClientLevelExtension.INSTANCE.destroyLatexCoverProgress(this.minecraft.level, this.minecraft.player.getId(), this.destroyBlockPos, -1);
            this.minecraft.player.resetAttackStrengthTicker();
        }
    }

    @Override
    public boolean changed$continueDestroyLatexCover(BlockPos pos, Direction direction) {
        this.ensureHasSentCarriedItem();
        if (this.destroyDelay > 0) {
            --this.destroyDelay;
            return true;
        } else if (this.localPlayerMode.isCreative() && this.minecraft.level.getWorldBorder().isWithinBounds(pos)) {
            this.destroyDelay = 5;
            /*LatexCoverState coverState = LatexCoverState.getAt(this.minecraft.level, pos);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, pos, coverState, 1.0F);*/
            this.changed$startGeneralPrediction(this.minecraft.level, (sequence) -> {
                if (!ForgeHooks.onLeftClickBlock(this.minecraft.player, pos, direction, ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK).isCanceled()) {
                    this.changed$destroyLatexCover(pos);
                }

                return new ServerboundPlayerActionPacketExt(ServerboundPlayerActionPacketExt.Action.START_DESTROY_LATEX_COVER, pos, direction, sequence);
            });
            return true;
        } else if (this.changed$sameDestroyTargetLatexCover(pos)) {
            LatexCoverState coverState = LatexCoverState.getAt(this.minecraft.level, pos);
            if (coverState.isAir()) {
                this.isDestroying = false;
                this.changed$isDestroyingCover = false;
                return false;
            } else {
                this.destroyProgress += coverState.getDestroyProgress(this.minecraft.player, LatexCoverGetter.wrap(this.minecraft.player.level()), pos);
                if (this.destroyTicks % 4.0F == 0.0F) {
                    SoundType soundtype = coverState.getSoundType(this.minecraft.level, pos, this.minecraft.player);
                    this.minecraft.getSoundManager().play(new SimpleSoundInstance(soundtype.getHitSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 8.0F, soundtype.getPitch() * 0.5F, SoundInstance.createUnseededRandom(), pos));
                }

                ++this.destroyTicks;
                //this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, pos, coverState, Mth.clamp(this.destroyProgress, 0.0F, 1.0F));
                if (ForgeHooks.onClientMineHold(this.minecraft.player, pos, direction).getUseItem() == Event.Result.DENY) {
                    return true;
                } else {
                    if (this.destroyProgress >= 1.0F) {
                        this.isDestroying = false;
                        this.changed$isDestroyingCover = false;
                        this.changed$startGeneralPrediction(this.minecraft.level, (sequence) -> {
                            this.changed$destroyLatexCover(pos);
                            return new ServerboundPlayerActionPacketExt(ServerboundPlayerActionPacketExt.Action.STOP_DESTROY_LATEX_COVER, pos, direction, sequence);
                        });
                        this.destroyProgress = 0.0F;
                        this.destroyTicks = 0.0F;
                        this.destroyDelay = 5;
                    }

                    ClientLevelExtension.INSTANCE.destroyLatexCoverProgress(this.minecraft.level, this.minecraft.player.getId(), this.destroyBlockPos, this.getDestroyStage());
                    return true;
                }
            }
        } else {
            return this.changed$startDestroyLatexCover(pos, direction);
        }
    }

    @Override
    public boolean changed$isDestroyingLatexCover() {
        return changed$isDestroyingCover;
    }
}
