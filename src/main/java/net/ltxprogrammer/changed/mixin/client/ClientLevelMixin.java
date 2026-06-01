package net.ltxprogrammer.changed.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.block.LatexCoveringSource;
import net.ltxprogrammer.changed.block.MicrophoneBlock;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
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

    protected ClientLevelMixin(WritableLevelData p_270739_, ResourceKey<Level> p_270683_, RegistryAccess p_270200_, Holder<DimensionType> p_270240_, Supplier<ProfilerFiller> p_270692_, boolean p_270904_, boolean p_270470_, long p_270248_, int p_270466_) {
        super(p_270739_, p_270683_, p_270200_, p_270240_, p_270692_, p_270904_, p_270470_, p_270248_, p_270466_);
    }

    @Shadow public abstract void playLocalSound(double p_104600_, double p_104601_, double p_104602_, SoundEvent p_104603_, SoundSource p_104604_, float p_104605_, float p_104606_, boolean p_104607_);

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
        this.getBlockStatePairsIfLoaded(new AABB(EntityUtil.getBlock(x, y, z)).inflate(3.0)).forEach(pair -> {
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

    @WrapMethod(method = "tickNonPassenger")
    private void ensureSetNoCulling(Entity entity, Operation<Void> original) {
        boolean originalNoCullState = entity.noCulling;

        if (entity instanceof LivingEntity livingEntity && AbstractAbility.getAbilityInstanceSafe(livingEntity, ChangedAbilities.GRAB_ENTITY_ABILITY.get())
                .map(ability -> ability.grabbedEntity == this.minecraft.getCameraEntity()).orElse(false)) {
            entity.noCulling = true;
        }

        else if (this.minecraft.getCameraEntity() instanceof LivingEntity livingEntity && AbstractAbility.getAbilityInstanceSafe(livingEntity, ChangedAbilities.GRAB_ENTITY_ABILITY.get())
                .map(ability -> ability.grabbedEntity == entity).orElse(false)) {
            entity.noCulling = true;
        }

        original.call(entity);

        entity.noCulling = originalNoCullState;
    }

    @WrapMethod(method = "doAnimateTick")
    private void changed$doLatexCoverAnimateTick(int xCenter, int yCenter, int zCenter, int radius, RandomSource random, @Nullable Block markerBlock, BlockPos.MutableBlockPos pos, Operation<Void> original) {
        original.call(xCenter, yCenter, zCenter, radius, random, markerBlock, pos);

        LatexCoverState coverState = LatexCoverState.getAt(this, pos);
        if (!coverState.isAir())
            coverState.animateTick(this, pos, random);
    }
}
