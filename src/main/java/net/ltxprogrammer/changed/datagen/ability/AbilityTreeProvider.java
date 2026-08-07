package net.ltxprogrammer.changed.datagen.ability;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.tree.PartialNode.TreeReference;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AbilityTreeProvider extends AbilityTreeDataProvider {
    public static final Multimap<TreeReference, RegistryObject<TransfurVariant<?>>> treeForVariants = ArrayListMultimap.create();
    public static final Multimap<DefaultAbilityTree, Supplier<AbilityTreeBuilder>> defaultAbilityTreeForVariants = ArrayListMultimap.create();

    public static final String DEFAULT_ABILITIES = "/default_abilities";

    public record DefaultAbilityTree(Supplier<TransfurVariant<?>> variant, TreeReference treeReference) {}

    public AbilityTreeProvider(PackOutput output) {
        super(output, Changed.MODID);
    }

    @Override
    protected void addTrees() {
        addVariantsDefaultAbilitiesTrees();
        addVariantsToCurrentTrees();
    }

    @SuppressWarnings("unchecked")
    public static <T extends ChangedEntity> void addEntry(TreeReference reference, RegistryObject<TransfurVariant<T>> register) {
        AbilityTreeProvider.treeForVariants.put(reference, (RegistryObject<TransfurVariant<?>>) (RegistryObject) register);
    }

    public static <T extends ChangedEntity> void addDefaultAbilityTreeEntry(Supplier<AbilityTreeBuilder> builder, RegistryObject<TransfurVariant<T>> variantRegistryObject) {
        ResourceLocation variantID = variantRegistryObject.getId();
        ResourceLocation treeLoc = variantID.withPath(variantID.getPath() + DEFAULT_ABILITIES);
        AbilityTreeProvider.defaultAbilityTreeForVariants.put(new DefaultAbilityTree(variantRegistryObject::get, new TreeReference(treeLoc)), builder);
    }

    public static <T extends ChangedEntity> void addDefaultAbilityTreeEntry(Supplier<AbilityTreeBuilder> builder, DefaultAbilityTree defaultAbilityTree) {
        AbilityTreeProvider.defaultAbilityTreeForVariants.put(defaultAbilityTree, builder);
    }

    // Call this after all tree registrations or else it will fail.
    public void addVariantsDefaultAbilitiesTrees() {
        for (DefaultAbilityTree defaultAbilityTree : defaultAbilityTreeForVariants.keySet()) {
            for (Supplier<AbilityTreeBuilder> builder : defaultAbilityTreeForVariants.get(defaultAbilityTree)) {
                ResourceLocation variantId = defaultAbilityTree.variant.get().getFormId();
                ResourceLocation treeLoc = variantId.withPath(variantId.getPath() + DEFAULT_ABILITIES);

                if (builder != null) {
                    addTree(treeLoc, builder.get());
                } else {
                    Changed.LOGGER.warn("Something went wrong when generating an default ability tree: {}", treeLoc);
                }
            }
        }
    }

    // Call this after all tree registrations or else it will fail.
    public void addVariantsToCurrentTrees() {
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
                Changed.LOGGER.warn("Attempted to inject variants into non-existent (in datagen) ability tree: {}", treeLoc);
            }
        }
    }
}
