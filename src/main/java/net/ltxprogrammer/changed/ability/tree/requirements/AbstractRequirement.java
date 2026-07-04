package net.ltxprogrammer.changed.ability.tree.requirements;

import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.ability.tree.AbilityNode;
import net.ltxprogrammer.changed.ability.tree.AbilityTreeInstance;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.nbt.Tag;

import java.util.function.Function;

public abstract class AbstractRequirement {
    public static final Codec<AbstractRequirement> REQUIREMENT_CODEC = ChangedRegistry.PURCHASE_REQUIREMENTS.get().getCodec().dispatch("type",
            AbstractRequirement::getCodec, Function.identity());

    public abstract Codec<? extends AbstractRequirement> getCodec();

    public abstract boolean requirementMet(AbilityTreeInstance.AccountedTree tree, AbilityNode node);

    /// Called on the server to serialize the current requirement progress then send to the client, to be handled by {@link #deserializeProgress deserializeProgress}
    public abstract Tag serializeProgress(AbilityTreeInstance.AccountedTree tree, AbilityNode node);

    public abstract RequirementProgress<?> deserializeProgress(Tag progressTag);

    public boolean hideNodeIfNotMet() {
        return false;
    }
}
