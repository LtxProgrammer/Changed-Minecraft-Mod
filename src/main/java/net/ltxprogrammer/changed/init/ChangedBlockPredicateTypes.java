package net.ltxprogrammer.changed.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedBlockPredicateTypes {
    public static BlockPredicateType<ChangedFeatures.DeferredWouldSurvivePredicate> DEFERRED_WOULD_SURVIVE =
            register("changed:would_survive", ChangedFeatures.DeferredWouldSurvivePredicate.CODEC);

    private static <P extends BlockPredicate> BlockPredicateType<P> register(String name, Codec<P> codec) {
        BlockPredicateType<P> ret = null;
        if (Registry.BLOCK_PREDICATE_TYPES instanceof MappedRegistry<BlockPredicateType<?>> reg) {
            reg.unfreeze();
            ret = Registry.register(reg, name, () -> codec);
            reg.freeze();
        }
        return ret;
    }
}
