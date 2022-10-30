package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.init.ChangedBiomes;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(Aquifer.NoiseBasedAquifer.class)
public abstract class NoiseBasedAquiferMixin implements Aquifer {
    @Inject(method = "computeSubstance", at = @At(value = "RETURN"), cancellable = true)
    public void computeSubstance(DensityFunction.FunctionContext context, double p_208159_, CallbackInfoReturnable<BlockState> cir) {
        BlockState blockState = cir.getReturnValue();
        if (blockState == null || !blockState.is(Blocks.WATER)) return;
        int x = context.blockX();
        int y = context.blockY();
        int z = context.blockZ();

        NoiseBasedAquifer self = (NoiseBasedAquifer)(Object)this;
        NoiseBasedChunkGenerator gen = ChangedBiomes.invertedPickerToGenerator.get(self.globalFluidPicker);
        if (gen == null)
            return;

        Holder<Biome> biomeAtPos = gen.getNoiseBiome(x, y, z);
        AtomicReference<BlockState> waterBlockAtomic = new AtomicReference<>(null);

        ChangedBiomes.DESCRIPTORS.forEach((biome, desc) -> {
            if (biomeAtPos.is(biome.getId()) && waterBlockAtomic.get() == null) {
                waterBlockAtomic.compareAndSet(null, desc.waterBlock());
            }
        });

        final BlockState waterBlock = waterBlockAtomic.getAcquire();

        if (waterBlock != null) {
            int seaLevel = gen.settings.value().seaLevel();
            int prelimSurface = self.noiseChunk.preliminarySurfaceLevel(x, z);
            if (y > prelimSurface) {
                cir.setReturnValue(waterBlock);
                cir.cancel();
            }
        }
    }
}
