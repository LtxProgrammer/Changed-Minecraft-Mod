package net.ltxprogrammer.changed.client;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.client.gui.AbstractRadialScreen;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedRegistry;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

public class AbilityColors {
    public static final int DEFAULT = -1;
    // FORGE: Use RegistryDelegates as non-Vanilla item ids are not constant
    private final java.util.Map<net.minecraftforge.registries.IRegistryDelegate<AbstractAbility<?>>, AbilityColor> abilityColors = new java.util.HashMap<>();

    public static AbstractRadialScreen.ColorScheme getAbilityColors(AbstractAbilityInstance abilityInstance) {
        return AbstractRadialScreen.getColors(abilityInstance.entity.getTransfurVariantInstance()).setForegroundToBright();
    }

    public static AbilityColors createDefault() {
        AbilityColors colors = new AbilityColors();

        colors.register((abilityInstance, layer) -> {
            var scheme = getAbilityColors(abilityInstance);

            return Optional.of(switch (layer) {
                case 0 -> scheme.foreground().toInt();
                default -> DEFAULT;
            });
        }, ChangedRegistry.ABILITY.get().getValues());

        colors.register((abilityInstance, layer) -> {
            var scheme = getAbilityColors(abilityInstance);

            TransfurMode mode = abilityInstance.entity.getTransfurMode();
            if (layer == 0 && mode == TransfurMode.REPLICATION)
                return Optional.of(scheme.foreground().toInt());
            else if (layer == 1 && mode == TransfurMode.ABSORPTION)
                return Optional.of(scheme.foreground().toInt());

            return Optional.empty();
        }, ChangedAbilities.SWITCH_TRANSFUR_MODE.get());

        return colors;
    }

    public Optional<Integer> getColor(AbstractAbilityInstance abilityInstance, int layer) {
        AbilityColor color = this.abilityColors.get(abilityInstance.getAbility().delegate);
        return color == null ? Optional.of(DEFAULT) : color.getColor(abilityInstance, layer);
    }

    public void register(AbilityColor abilityColor, AbstractAbility<?>... abilities) {
        for (AbstractAbility<?> ability : abilities) {
            this.abilityColors.put(ability.delegate, abilityColor);
        }
    }

    public void register(AbilityColor abilityColor, Collection<AbstractAbility<?>> abilities) {
        for (AbstractAbility<?> ability : abilities) {
            this.abilityColors.put(ability.delegate, abilityColor);
        }
    }
}
