package net.ltxprogrammer.changed.ability.tree.requirements;

import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.ability.tree.AbilityNode;
import net.ltxprogrammer.changed.ability.tree.AbilityTreeInstance;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;

public class HasAllOtherNodesInTreeRequirement extends BinaryRequirement {
    public static final HasAllOtherNodesInTreeRequirement INSTANCE = new HasAllOtherNodesInTreeRequirement();
    public static final Codec<HasAllOtherNodesInTreeRequirement> CODEC = Codec.unit(() -> INSTANCE);

    public HasAllOtherNodesInTreeRequirement() {
        super("text.changed.ability_tree.requirement.all_other_nodes");
    }

    @Override
    public boolean requirementMet(AbilityTreeInstance.AccountedTree tree, AbilityNode node) {
        TransfurVariantInstance<?> variant = ProcessTransfur.getPlayerTransfurVariant(tree.getPlayer());
        if (variant == null)
            return false;

        return tree.getNodeStates(variant.getParent(), pair -> {
            if (pair.getSecond() == node)
                return false;
            return !pair.getSecond().requirements.contains(INSTANCE);
        }).allMatch(AbilityTreeInstance.NodeState::unlocked);
    }

    @Override
    public Codec<? extends AbstractRequirement> getCodec() {
        return CODEC;
    }
}
