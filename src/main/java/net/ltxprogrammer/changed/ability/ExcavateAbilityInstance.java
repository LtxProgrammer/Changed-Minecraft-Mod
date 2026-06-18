package net.ltxprogrammer.changed.ability;

public class ExcavateAbilityInstance extends AbstractAbilityInstance {
    private boolean isActive = false;

    public ExcavateAbilityInstance(AbstractAbility<?> ability, IAbstractChangedEntity entity) {
        super(ability, entity);
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean canKeepUsing() {
        return true;
    }

    @Override
    public void startUsing() {
        this.isActive = !this.isActive;
        entity.displayClientMessage(isActive ? ExcavateAbility.ENABLE : ExcavateAbility.DISABLE, true);
    }

    @Override
    public void tick() {

    }

    @Override
    public void stopUsing() {

    }
}
