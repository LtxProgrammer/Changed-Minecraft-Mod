package net.ltxprogrammer.changed.ability.tree.events;

public abstract class StatPointEvent extends AbstractPointEvent<StatCriteria> {
    public final int divisor;

    public StatPointEvent(int reward, int divisor) {
        super(reward);
        this.divisor = divisor;
    }

    @Override
    public boolean test(StatCriteria criteria) {
        int mttl = criteria.totalValue() % divisor;
        int mdttl = (criteria.totalValue() + criteria.delta()) % divisor;

        return mdttl < mttl;
    }
}
