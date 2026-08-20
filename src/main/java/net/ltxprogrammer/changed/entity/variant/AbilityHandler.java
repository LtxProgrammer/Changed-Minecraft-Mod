package net.ltxprogrammer.changed.entity.variant;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.KeyReference;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.util.KeyStatesTracker;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class AbilityHandler {
    private final Map<AbstractAbility<?>, AbstractAbilityInstance> abilityInstances;

    private final Map<KeyReference, AbstractAbility<?>> selectedAbilities = new Object2ObjectArrayMap<>();
    private final Object2IntMap<KeyReference> keyToIndex = new Object2IntArrayMap<>();
    private final List<KeyReference> indexToKey = new ObjectArrayList<>();
    private KeyStatesTracker<KeyReference> abilityKeys = new KeyStatesTracker<>(Set.of());

    public AbilityHandler(Map<AbstractAbility<?>, AbstractAbilityInstance> abilityInstances) {
        this.abilityInstances = abilityInstances;

        this.addAbilitySelectSlot(KeyReference.ABILITY);
        this.addAbilitySelectSlot(KeyReference.ABILITY_ALT);
    }

    public boolean addAbilitySelectSlot(KeyReference slot) {
        if (keyToIndex.containsKey(slot))
            return false;

        int index = indexToKey.size();
        keyToIndex.put(slot, index);
        indexToKey.add(slot);
        selectedAbilities.put(slot, null);
        abilityKeys = new KeyStatesTracker<>(keyToIndex.keySet());

        return true;
    }

    public boolean queueKeyState(KeyReference key, boolean state) {
        if (!keyToIndex.containsKey(key))
            return false;

        return abilityKeys.queueKeyState(key, state);
    }

    public int getFlipCount(KeyReference key) {
        if (!keyToIndex.containsKey(key))
            return 0;

        return abilityKeys.getFlipCount(key);
    }

    public boolean isEffectivelyDown(KeyReference key) {
        if (!keyToIndex.containsKey(key))
            return false;

        return abilityKeys.isEffectivelyDown(key);
    }

    public boolean handleKeyFlips(Player host) {
        AtomicBoolean handled = new AtomicBoolean(false);

        abilityKeys.handleStateUpdates((key, isDown, wasDown, unique) -> {
            var ability = selectedAbilities.get(key);
            if (ability == null) return;
            var instance = abilityInstances.get(ability);
            if (instance == null) return;
            var controller = instance.getController();

            boolean oldState = controller.exchangeKeyState(isDown);
            if (isDown || instance.getController().isCoolingDown())
                handled.set(true);
            if (!host.isUsingItem() && !instance.getController().isCoolingDown())
                instance.getUseType().check(isDown, oldState, unique, controller);
        });

        return handled.getAcquire();
    }

    public interface AbilityVisitor {
        void visit(int index, int totalCount, KeyReference key, @Nullable AbstractAbility<?> ability, AbstractAbilityInstance abilityInstance);
    }

    public void visitSelected(AbilityVisitor visitor) {
        for (var entry : keyToIndex.object2IntEntrySet()) {
            int index = entry.getIntValue();
            var ability = selectedAbilities.get(entry.getKey());
            var instance = ability == null ? null : abilityInstances.get(ability);
            visitor.visit(index, keyToIndex.size(), entry.getKey(), ability, instance);
        }
    }

    public boolean isSelected(AbstractAbility<?> ability) {
        return selectedAbilities.containsValue(ability);
    }

    public boolean setSelected(KeyReference slot, AbstractAbility<?> ability) {
        if (!keyToIndex.containsKey(slot))
            return false; // No slot exists
        if (isSelected(ability))
            return false; // Already selected

        selectedAbilities.put(slot, ability);
        return true;
    }

    public AbstractAbility<?> getSelected(KeyReference slot) {
        return selectedAbilities.get(slot);
    }

    public void loadSelected(ListTag list) {
        for (int i = 0; i < list.size() && i < keyToIndex.size(); ++i) {
            var abilityKey = list.getString(i);
            if (abilityKey.isEmpty())
                continue;
            var ability = ChangedRegistry.ABILITY.getValue(ResourceLocation.parse(abilityKey));
            if (!abilityInstances.containsKey(ability))
                continue;

            var key = this.indexToKey.get(i);
            this.setSelected(key, ability);
        }
    }

    public ListTag saveSelected() {
        ListTag list = new ListTag();
        for (KeyReference keyReference : this.indexToKey) {
            var selectedAbility = this.selectedAbilities.get(keyReference);
            if (selectedAbility == null)
                list.add(StringTag.valueOf(""));
            ResourceLocation selectedKey = ChangedRegistry.ABILITY.get().getKey(selectedAbility);
            if (selectedKey == null)
                list.add(StringTag.valueOf(""));
            else
                list.add(StringTag.valueOf(selectedKey.toString()));
        }
        return list;
    }
}
