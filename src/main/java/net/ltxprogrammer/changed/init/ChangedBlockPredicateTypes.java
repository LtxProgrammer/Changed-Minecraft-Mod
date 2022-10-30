package net.ltxprogrammer.changed.init;

import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.util.Dynamic;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.blockpredicates.WouldSurvivePredicate;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedBlockPredicateTypes {
    public static BlockPredicateType<ChangedFeatures.DeferredWouldSurvivePredicate> DEFERRED_WOULD_SURVIVE = register("changed:would_survive", ChangedFeatures.DeferredWouldSurvivePredicate.CODEC);

    private static <P extends BlockPredicate> BlockPredicateType<P> register(String p_190450_, Codec<P> p_190451_) {
        return (BlockPredicateType<P>) Dynamic.lateRegister(Registry.BLOCK_PREDICATE_TYPES, new ResourceLocation(p_190450_), () -> () -> (Codec<BlockPredicate>) p_190451_);
    }
}
