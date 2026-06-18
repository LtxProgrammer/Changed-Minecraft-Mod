package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IExtensibleEnum;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;

public class AutotoolAbility extends AbstractAbility<AutotoolAbilityInstance> {
    public AutotoolAbility() {
        super(AutotoolAbilityInstance::new);
    }

    public static final Collection<Component> DESCRIPTION = Collections.singleton(Component.translatable("ability.changed.autotool.desc"));
    public static final Component ENABLE = Component.translatable("ability.changed.autotool.enable");
    public static final Component DISABLE = Component.translatable("ability.changed.autotool.disable");

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
    }

    public static @NotNull ItemStack getItemToUse(IAbstractChangedEntity entity, BlockState blockState) {
        var correct = getFirstCorrectItem(entity, blockState, null);
        if (correct == null)
            return entity.getEntity().getMainHandItem();
        return correct;
    }

    public static @Nullable ItemStack getFirstMainHandCorrectItem(IAbstractChangedEntity entity, BlockState blockState, @Nullable ItemStack skipItem) {
        var switchHands = entity.getAbilityInstance(ChangedAbilities.SWITCH_HANDS.get());

        Predicate<ItemStack> predicate = itemStack -> {
            return itemStack != skipItem && itemStack.isCorrectToolForDrops(blockState);
        };

        if (switchHands == null) {
            if (predicate.test(entity.getEntity().getMainHandItem()))
                return entity.getEntity().getMainHandItem();
        } else {
            var mainItem = switchHands.getMainHandItems().filter(predicate).findFirst();
            if (mainItem.isPresent())
                return mainItem.get();
        }

        return null;
    }

    public static @Nullable ItemStack getFirstCorrectItem(IAbstractChangedEntity entity, BlockState blockState, @Nullable ItemStack skipItem) {
        var switchHands = entity.getAbilityInstance(ChangedAbilities.SWITCH_HANDS.get());

        Predicate<ItemStack> predicate = itemStack -> {
            return itemStack != skipItem && itemStack.isCorrectToolForDrops(blockState);
        };

        if (switchHands == null) {
            if (predicate.test(entity.getEntity().getMainHandItem()))
                return entity.getEntity().getMainHandItem();
            if (predicate.test(entity.getEntity().getOffhandItem()))
                return entity.getEntity().getOffhandItem();
        } else {
            var mainItem = switchHands.getMainHandItems().filter(predicate).findFirst();
            if (mainItem.isPresent())
                return mainItem.get();
            var offItem = switchHands.getOffHandItems().filter(predicate).findFirst();
            if (offItem.isPresent())
                return offItem.get();
        }

        return null;
    }
}
