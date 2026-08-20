package net.ltxprogrammer.changed.datagen.ability;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.tree.PartialNode.TreeReference;
import net.ltxprogrammer.changed.ability.tree.effects.UnlockActiveAbilityNodeEffect;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.ltxprogrammer.changed.datagen.ability.AbilityTreeProvider.DefaultAbilityTree;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

import static net.ltxprogrammer.changed.datagen.ability.AbilityTreeDataProvider.AbilityTreeBuilder;

public class AbilityNodeProvider extends AbilityNodeDataProvider {

    public static final String DEFAULT_ABILITIES = "/default_abilities";

    public record DefaultAbility(Supplier<AbstractAbility<?>> ability, int defaultLevel) {
    }

    public static final Multimap<RegistryObject<TransfurVariant<?>>, List<DefaultAbility>> defaultAbilitiesForVariants = ArrayListMultimap.create();

    public AbilityNodeProvider(PackOutput output) {
        super(output, Changed.MODID);
    }

    @Override
    protected void addNodes() {
        addDefaultAbilities();
    }

    private void addDefaultAbilities() {
        Multimap<DefaultAbilityTree, Supplier<AbilityTreeBuilder>> defaultAbilityTreeForVariants = AbilityTreeProvider.defaultAbilityTreeForVariants;
        defaultAbilitiesForVariants.forEach((variant, abilities) -> {
            ResourceLocation variantId = variant.getId();
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(variantId.getNamespace(), variantId.getPath() + DEFAULT_ABILITIES);
            for (TreeReference abilityTree : defaultAbilityTreeForVariants.keySet().stream().map(DefaultAbilityTree::treeReference).toList()) {
                AbilityNodeBuilder parent = AbilityNodeBuilder.builder().parent(abilityTree);
                for (DefaultAbility defaultAbility : abilities.stream().toList()) {
                    parent.acquiredEffect(new UnlockActiveAbilityNodeEffect(defaultAbility.ability().get(), defaultAbility.defaultLevel()));
                }
                addNode(id, parent);
            }
        });
    }

    public static <T extends ChangedEntity> void addGeneric(RegistryObject<TransfurVariant<T>> variant, List<Supplier<AbstractAbility<?>>> abilities) {
        List<DefaultAbility> defaultAbilities = abilities.stream().map(abilitySupplier -> new DefaultAbility(abilitySupplier, 0)).toList();
        defaultAbilitiesForVariants.put((RegistryObject<TransfurVariant<?>>) (RegistryObject) variant, defaultAbilities);
        AbilityTreeProvider.addDefaultAbilityTreeEntry(() -> new AbilityTreeBuilder(List.of(forVariant(variant))).hidden(true), variant);
    }

    public static <T extends ChangedEntity> void addEntry(RegistryObject<TransfurVariant<T>> variant, List<DefaultAbility> abilities) {
        defaultAbilitiesForVariants.put((RegistryObject<TransfurVariant<?>>) (RegistryObject) variant, abilities);
        AbilityTreeProvider.addDefaultAbilityTreeEntry(() -> new AbilityTreeBuilder(List.of(forVariant(variant))).hidden(true), variant);
    }

    protected static RegistryElementPredicate<TransfurVariant<?>> forVariant(TransfurVariant<?> variant) {
        return RegistryElementPredicate.forID(ChangedRegistry.TRANSFUR_VARIANT.get(), variant.getFormId());
    }

    protected static <T extends ChangedEntity> RegistryElementPredicate<TransfurVariant<?>> forVariant(RegistryObject<TransfurVariant<T>> variant) {
        return RegistryElementPredicate.forID(ChangedRegistry.TRANSFUR_VARIANT.get(), variant.getId());
    }
}
