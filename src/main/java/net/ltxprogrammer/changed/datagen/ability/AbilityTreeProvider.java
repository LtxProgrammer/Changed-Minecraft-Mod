package net.ltxprogrammer.changed.datagen.ability;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.tree.PartialNode.TreeReference;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AbilityTreeProvider extends AbilityTreeDataProvider {

    public static final Multimap<TreeReference, RegistryObject<TransfurVariant<?>>> treeForVariants = ArrayListMultimap.create();

    public static final TreeReference LATEX = new TreeReference(Changed.modResource("latex"));

    public AbilityTreeProvider(PackOutput output) {
        super(output, Changed.MODID);
    }

    @Override
    protected void addTrees() {
        addVariantsToCurrentTrees();
    }

    @SuppressWarnings("unchecked")
    public static <T extends ChangedEntity> void addEntry(TreeReference reference, RegistryObject<TransfurVariant<T>> register) {
        // O cast para (RegistryObject) remove a invariância estrita e permite converter para a assinatura com o wildcard <?>
        AbilityTreeProvider.treeForVariants.put(reference, (RegistryObject<TransfurVariant<?>>) (RegistryObject) register);
    }

    // Call this after all tree registrations or else it will fail.
    private void addVariantsToCurrentTrees() {
        var registry = ChangedRegistry.TRANSFUR_VARIANT.get();
        for (TreeReference treeReference : treeForVariants.keySet()) {
            ResourceLocation treeLoc = treeReference.treeName();
            AbilityTreeBuilder builder = this.treeBuilders.get(treeLoc);

            if (builder != null) {
                Collection<RegistryObject<TransfurVariant<?>>> registeredVariants = treeForVariants.get(treeReference);

                List<RegistryElementPredicate<TransfurVariant<?>>> predicates = registeredVariants.stream()
                        .map(variant -> RegistryElementPredicate.forID(registry, variant.getId()))
                        .collect(Collectors.toList());

                builder.withVariants(predicates);
            } else {
                Changed.LOGGER.warn("Attempted to inject variants into non-existent ability tree: {}", treeLoc);
            }
        }
    }
}