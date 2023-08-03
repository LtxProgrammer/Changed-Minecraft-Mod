package net.ltxprogrammer.changed.entity.ai;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractLatex;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.util.Cacheable;
import net.ltxprogrammer.changed.util.CollectionUtil;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.Map;
import java.util.function.Predicate;

public class UseAbilityGoal extends Goal {
    private final Cacheable<Map<AbstractAbility<?>, Pair<Predicate<AbstractAbilityInstance>, AbstractAbilityInstance>>> abilitiesCache;
    private final LatexEntity latex;
    private final IAbstractLatex abstractLatex;
    private AbstractAbility<?> selectedAbility = null;

    public UseAbilityGoal(
            Cacheable<Map<AbstractAbility<?>, Pair<Predicate<AbstractAbilityInstance>, AbstractAbilityInstance>>> abilities, LatexEntity latex) {
        this.abilitiesCache = abilities;
        this.latex = latex;
        this.abstractLatex = IAbstractLatex.forLatex(latex);
    }

    public AbstractAbilityInstance getSelectedAbility() {
        var abilities = abilitiesCache.get();
        if (abilities.containsKey(selectedAbility))
            return abilities.get(selectedAbility).getSecond();
        return null;
    }

    @Override
    public boolean canUse() {
        return !abilitiesCache.get().isEmpty();
    }

    public AbstractAbilityInstance reselectNewAbility() {
        var abilities = abilitiesCache.get();
        var selected = getSelectedAbility();
        if (selected != null && (selected.getController().isCoolingDown() || !abilities.get(selectedAbility).getFirst().test(selected))) {
            selectedAbility = null;
            selected.getUseType().check(false, selected.getController().exchangeKeyState(false), selected.getController());
        }

        if (selectedAbility == null) {
            CollectionUtil.shuffle(abilities.entrySet().stream(), latex.level.random).forEach(entry -> {
                if (selectedAbility == null && entry.getValue().getFirst().test(entry.getValue().getSecond())) {
                    selectedAbility = entry.getKey();
                }
            });


            if (selectedAbility == null) {
                selected = getSelectedAbility();
            }
        }

        return selected;
    }

    @Override
    public void tick() {
        super.tick();
        abilitiesCache.get().forEach((ability, instance) -> instance.getSecond().getController().tickCoolDown());

        var selected = reselectNewAbility();

        if (selected != null) {
            var controller = selected.getController();
            selected.getUseType().check(true, controller.exchangeKeyState(true), controller);
        }
    }
}
