package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

@Mod.EventBusSubscriber
public class ChangedStructureSets { // TODO finish
    public static final DeferredRegister<StructureSet> REGISTRY = DeferredRegister.create(BuiltinRegistries.STRUCTURE_SETS.key(), Changed.MODID);
    public static final Holder<StructureSet> BEEHIVES = register("beehives", ChangedStructures.BEEHIVE1, new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 14357619));

    private static Holder<StructureSet> register(String name, Supplier<StructureSet> set) {
        RegistryObject<StructureSet> registered = REGISTRY.register(name, set);
        return BuiltinRegistries.STRUCTURE_SETS.getOrCreateHolder(Objects.requireNonNull(registered.getKey()));
    }

    private static Holder<StructureSet> register(String name, Holder<ConfiguredStructureFeature<?, ?>> configured, StructurePlacement placement) {
        return register(name, () -> new StructureSet(configured, placement));
    }
}
