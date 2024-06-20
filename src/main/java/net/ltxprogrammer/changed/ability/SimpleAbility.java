package net.ltxprogrammer.changed.ability;

// A simple ability ditches the instantiation part of abilities, in order to be simpler to write
public abstract class SimpleAbility extends AbstractAbility<SimpleAbilityInstance> {
    public SimpleAbility() {
        super(SimpleAbilityInstance::new);
    }
}
