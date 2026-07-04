package net.ltxprogrammer.changed.ability.tree.requirements;

import net.ltxprogrammer.changed.ability.tree.AbilityNode;
import net.ltxprogrammer.changed.ability.tree.AbilityTreeInstance;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public abstract class BinaryRequirement extends AbstractRequirement {
    protected final String requirementName;

    protected BinaryRequirement(String requirementName) {
        this.requirementName = requirementName;
    }

    @Override
    public Tag serializeProgress(AbilityTreeInstance.AccountedTree tree, AbilityNode node) {
        return ByteTag.valueOf(requirementMet(tree, node));
    }

    @Override
    public RequirementProgress<?> deserializeProgress(Tag progressTag) {
        return new Progress(this, ((ByteTag)progressTag).getAsInt() > 0);
    }

    private static class Progress extends RequirementProgress<BinaryRequirement> {
        protected final boolean met;

        public Progress(BinaryRequirement requirement, boolean met) {
            super(requirement);
            this.met = met;
        }

        @Override
        public boolean requirementMet() {
            return met;
        }

        @Override
        public void buildDescription(Consumer<Component> componentConsumer) {
            componentConsumer.accept(
                    Component.translatable(requirement.requirementName).withStyle(met ? ChatFormatting.GREEN : ChatFormatting.RED)
            );
        }
    }
}
