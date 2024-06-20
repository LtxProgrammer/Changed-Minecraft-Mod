package net.ltxprogrammer.changed.mixin.client;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.block.MicrophoneBlock;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level {
    @Final @Shadow private Minecraft minecraft;

    @Shadow public abstract void playLocalSound(double p_104600_, double p_104601_, double p_104602_, SoundEvent p_104603_, SoundSource p_104604_, float p_104605_, float p_104606_, boolean p_104607_);

    @Shadow public abstract void playLocalSound(BlockPos p_104678_, SoundEvent p_104679_, SoundSource p_104680_, float p_104681_, float p_104682_, boolean p_104683_);

    protected ClientLevelMixin(WritableLevelData data, ResourceKey<Level> key, Holder<DimensionType> dimension, Supplier<ProfilerFiller> profilerFillerSupplier, boolean p_204153_, boolean p_204154_, long p_204155_) {
        super(data, key, dimension, profilerFillerSupplier, p_204153_, p_204154_, p_204155_);
    }

    private Stream<Pair<BlockPos, BlockState>> getBlockStatePairsIfLoaded(AABB aabb) {
        int i = Mth.floor(aabb.minX);
        int j = Mth.floor(aabb.maxX);
        int k = Mth.floor(aabb.minY);
        int l = Mth.floor(aabb.maxY);
        int i1 = Mth.floor(aabb.minZ);
        int j1 = Mth.floor(aabb.maxZ);
        return this.hasChunksAt(i, k, i1, j, l, j1) ? BlockPos.betweenClosedStream(aabb).map(blockPos -> new Pair<>(blockPos, this.getBlockState(blockPos))) : Stream.empty();
    }

    @Inject(method = "playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V", at = @At("RETURN"))
    public void playLocalSound(double x, double y, double z, SoundEvent event, SoundSource source, float volume, float pitch, boolean delay, CallbackInfo callback) {
        switch (source) {
            case RECORDS:
            case HOSTILE:
            case VOICE:
            case NEUTRAL:
            case PLAYERS: break;
            default: return;
        }

        AtomicBoolean singleListener = new AtomicBoolean(false);
        this.getBlockStatePairsIfLoaded(new AABB(new BlockPos(x, y, z)).inflate(3.0)).forEach(pair -> {
            BlockPos blockPos = pair.getFirst();
            BlockState blockState = pair.getSecond();
            if (!blockState.is(ChangedBlocks.MICROPHONE.get()) || !blockState.getValue(MicrophoneBlock.ENABLED))
                return;
            if (!singleListener.compareAndSet(false, true))
                return;

            this.getBlockStatePairsIfLoaded(new AABB(blockPos).inflate(11.0)).forEach(pair2 -> {
                if (!pair2.getSecond().is(ChangedBlocks.SPEAKER.get()))
                    return;
                this.playLocalSound(pair2.getFirst(), event, SoundSource.BLOCKS, volume, pitch, delay);
            });
        });
    }
}
