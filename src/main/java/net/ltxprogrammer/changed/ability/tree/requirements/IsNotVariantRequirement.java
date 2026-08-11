package net.ltxprogrammer.changed.ability.tree.requirements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.ability.tree.AbilityNode;
import net.ltxprogrammer.changed.ability.tree.AbilityTreeInstance;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.process.ProcessTransfur;

import java.util.List;

public class IsNotVariantRequirement extends IsVariantRequirement {
    public static final Codec<IsNotVariantRequirement> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.list(RegistryElementPredicate.codecElementOrTag(ChangedRegistry.TRANSFUR_VARIANT.get())).fieldOf("variants")
                    .forGetter(requirement -> List.copyOf(requirement.variants)),
            Codec.STRING.fieldOf("explainId").forGetter(requirement -> requirement.requirementName)
    ).apply(builder, IsNotVariantRequirement::new));

    public IsNotVariantRequirement(List<RegistryElementPredicate<TransfurVariant<?>>> variants, String explainId) {
        super(variants, explainId);
    }

    @Override
    public boolean requirementMet(AbilityTreeInstance.AccountedTree tree, AbilityNode node) {
        TransfurVariantInstance<?> variantInstance = ProcessTransfur.getPlayerTransfurVariant(tree.getPlayer());
        if (variantInstance == null)
            return false;

        TransfurVariant<?> variant = variantInstance.getParent();

        return variants.stream().noneMatch(predicate -> predicate.test(variant));
    }

    @Override
    public Codec<? extends AbstractRequirement> getCodec() {
        return CODEC;
    }
}
