package net.ltxprogrammer.changed.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import net.ltxprogrammer.changed.util.StateHolderHelper;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StateHolder.class)
public class StateHolderMixin<O, S> implements StateHolderHelper<O, S> {
    @Shadow @Final private ImmutableMap<Property<?>, Comparable<?>> values;
    @Shadow private Table<Property<?>, Comparable<?>, S> neighbours;

    @Shadow @Final protected O owner;

    @Override
    public StateHolderHelper<O, S> setValueTypeless(Property<?> property, Object value) {
        Comparable<?> comparable = values.get(property);
        if (comparable == null) {
            throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + owner);
        } else if (comparable == value) {
            return this;
        } else {
            S s = neighbours.get(property, value);
            if (s == null) {
                throw new IllegalArgumentException("Cannot set property " + property + " to " + value + " on " + owner + ", it is not an allowed value");
            } else if (s instanceof StateHolderHelper<?,?> helper) {
                return (StateHolderHelper<O, S>)helper;
            } else {
                throw new IllegalStateException("Mixin failed for StateHolderHelper");
            }
        }
    }

    @Override
    public StateHolder<O, S> getState() {
        return (StateHolder<O, S>)(Object)this;
    }
}
