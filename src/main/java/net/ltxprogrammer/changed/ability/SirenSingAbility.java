package net.ltxprogrammer.changed.ability;

public class SirenSingAbility extends AbstractAbility<SirenSingAbilityInstance> {
    public SirenSingAbility() {
        super(SirenSingAbilityInstance::new);
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.HOLD;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 90 * 20; // 90 Seconds
    }
}
