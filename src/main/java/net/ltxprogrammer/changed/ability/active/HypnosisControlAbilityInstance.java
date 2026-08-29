package net.ltxprogrammer.changed.ability.active;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;

public class HypnosisControlAbilityInstance extends AbstractAbilityInstance {
    private final Object2IntMap<LivingEntity> controlledEntities = new Object2IntArrayMap<>();

    protected BlockPos goTo = null;
    protected LivingEntity attackTarget = null;

    public HypnosisControlAbilityInstance(AbstractAbility<?> ability, IAbstractChangedEntity entity) {
        super(ability, entity);
    }

    public boolean addEntity(Player player) {
        if (controlledEntities.containsKey(player))
            return false;
        controlledEntities.put(player, 0);
        return true;
    }

    public boolean addEntity(PathfinderMob mob) {
        if (controlledEntities.containsKey(mob))
            return false;
        controlledEntities.put(mob, 0);
        return true;
    }

    @Override
    public boolean canUse() {
        return !controlledEntities.isEmpty();
    }

    @Override
    public boolean canKeepUsing() {
        return !controlledEntities.isEmpty();
    }

    @Override
    public void startUsing() {

    }

    @Override
    public void tick() {

    }

    @Override
    public void stopUsing() {

    }

    @Override
    public void tickIdle() {
        super.tickIdle();

        var it = controlledEntities.object2IntEntrySet().iterator();
        while (it.hasNext()) {
            var nextEntry = it.next();
            if (nextEntry.getIntValue() >= 15 * 20) {
                it.remove();
                continue;
            }

            nextEntry.setValue(nextEntry.getIntValue() + 1);
        }

        if (controlledEntities.isEmpty()) {
            goTo = null;
            attackTarget = null;
            return;
        }


    }
}
