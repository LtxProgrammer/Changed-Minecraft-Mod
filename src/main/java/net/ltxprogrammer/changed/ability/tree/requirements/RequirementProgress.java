package net.ltxprogrammer.changed.ability.tree.requirements;

import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public abstract class RequirementProgress<T extends AbstractRequirement> {
    public final T requirement;

    protected RequirementProgress(T requirement) {
        this.requirement = requirement;
    }

    public abstract boolean requirementMet();

    public abstract void buildDescription(Consumer<Component> componentConsumer);
}
