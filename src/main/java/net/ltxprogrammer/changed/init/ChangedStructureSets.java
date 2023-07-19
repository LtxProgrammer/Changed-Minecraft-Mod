package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

@Mod.EventBusSubscriber
public class ChangedStructureSets { // TODO finish
    public static final DeferredRegister<StructureSet> REGISTRY = DeferredRegister.create(BuiltinRegistries.STRUCTURE_SETS.key(), Changed.MODID);
    public static final Holder<StructureSet> BEEHIVES = register("beehives", ChangedStructures.BEEHIVE1,
            new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 0xBEE00, Vec3i.ZERO));
    public static final Holder<StructureSet> DECAYED_LABS = register("decayed_labs", () -> new StructureSet(List.of(
            StructureSet.entry(ChangedStructures.AQUATIC1),
            StructureSet.entry(ChangedStructures.AQUATIC2),
            StructureSet.entry(ChangedStructures.HUMAN_RESEARCH_LAB1),
            StructureSet.entry(ChangedStructures.LASER_LAB1),
            StructureSet.entry(ChangedStructures.OFFICE_AREA1),
            StructureSet.entry(ChangedStructures.OFFICE_AREA2),
            StructureSet.entry(ChangedStructures.RESEARCH_TOWER1),
            StructureSet.entry(ChangedStructures.RESEARCH_TOWER2),
            StructureSet.entry(ChangedStructures.RESEARCH_TOWER3),
            StructureSet.entry(ChangedStructures.RESEARCH_TOWER4),
            StructureSet.entry(ChangedStructures.TREATMENT_1),
            StructureSet.entry(ChangedStructures.TREATMENT_2),
            StructureSet.entry(ChangedStructures.TREATMENT_3),
            //StructureSet.entry(ChangedStructures.UNDERGROUND_DL_LAB1),
            StructureSet.entry(ChangedStructures.WHITE_LATEX_LAB1),
            StructureSet.entry(ChangedStructures.WHITE_LATEX_LAB2)
    ), new RandomSpreadStructurePlacement(24, 8, RandomSpreadType.LINEAR, 0xFEED00, Vec3i.ZERO)));

    private static Holder<StructureSet> register(String name, Supplier<StructureSet> set) {
        RegistryObject<StructureSet> registered = REGISTRY.register(name, set);
        return BuiltinRegistries.STRUCTURE_SETS.getOrCreateHolder(Objects.requireNonNull(registered.getKey()));
    }

    private static Holder<StructureSet> register(String name, Holder<ConfiguredStructureFeature<?, ?>> configured, StructurePlacement placement) {
        return register(name, () -> new StructureSet(configured, placement));
    }
}
