package net.ltxprogrammer.changed.datagen.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.tree.PartialNode;
import net.ltxprogrammer.changed.ability.tree.PartialNode.TreeReference;
import net.ltxprogrammer.changed.ability.tree.condition.TrueCondition;
import net.ltxprogrammer.changed.ability.tree.effects.EnableFeatureNodeEffect;
import net.ltxprogrammer.changed.init.ChangedTransfurVariantFeatures;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import static net.ltxprogrammer.changed.init.ChangedAbilityTreeCodecs.TRUE_CONDITION;

public class AbilityNodesProvider extends AbilityNodeDataProvider {

    public static final TreeReference LATEX = new TreeReference(Changed.modResource("latex"));
    public static final ResourceLocation UNIVERSAL_NODE = Changed.modResource("universal_node");

    public AbilityNodesProvider(PackOutput output) {
        super(output, Changed.MODID);
    }

    @Override
    protected void addNodes() {
        addNode(UNIVERSAL_NODE).parent(LATEX).acquiredEffect(new EnableFeatureNodeEffect(TrueCondition.INSTANCE, ChangedTransfurVariantFeatures.ABSORPTION.get(), 1));
    }
}
