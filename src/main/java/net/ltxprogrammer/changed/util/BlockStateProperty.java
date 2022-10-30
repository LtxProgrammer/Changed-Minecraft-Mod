package net.ltxprogrammer.changed.util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BlockStateProperty extends Property<BlockStateProperty.BlockStateHolder> {
    private ImmutableMap<String, BlockStateHolder> possibleValues = null;
    private final Optional<Predicate<Block>> keepBlock;
    private final Optional<Predicate<Property<?>>> keepProperty;

    @Override
    public Collection<BlockStateHolder> getPossibleValues() {
        if (possibleValues != null)
            return possibleValues.values();

        ImmutableMap.Builder<String, BlockStateHolder> builder = ImmutableMap.builder();
        Registry.BLOCK.forEach(block -> {
            var defaultState = block.defaultBlockState();
            if (keepBlock.isPresent() && !keepBlock.get().test(block))
                return;

            var properties = block.getStateDefinition().getProperties();
            block.getStateDefinition().getPossibleStates().forEach(blockState -> {
                AtomicBoolean keep = new AtomicBoolean(true);
                keepProperty.ifPresent(propertyPredicate -> properties.forEach(property -> {
                    if (!propertyPredicate.test(property)) {
                        if (defaultState.getValue(property) != blockState.getValue(property))
                            keep.set(false);
                    }
                }));
                if (keep.getAcquire()) {
                    var state = new BlockStateHolder(blockState);
                    builder.put(getName(state), state);
                }
            });
        });
        possibleValues = builder.build();
        return possibleValues.values();
    }

    private static final Function<Map.Entry<Property<?>, Comparable<?>>, String> PROPERTY_ENTRY_TO_STRING_FUNCTION = new Function<>() {
        public String apply(@Nullable Map.Entry<Property<?>, Comparable<?>> p_61155_) {
            if (p_61155_ == null) {
                return "<NULL>";
            } else {
                Property<?> property = p_61155_.getKey();
                return property.getName() + "0" + this.getName(property, p_61155_.getValue());
            }
        }

        private <T extends Comparable<T>> String getName(Property<T> p_61152_, Comparable<?> p_61153_) {
            return p_61152_.getName((T)p_61153_);
        }
    };

    protected String getName(BlockState blockState) {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(blockState.getBlock().getRegistryName().toString().replace(':', '0'));
        stringbuilder.append('0');
        stringbuilder.append(blockState.getValues().entrySet().stream().filter(entry ->
                        keepProperty.map(propertyPredicate -> propertyPredicate.test(entry.getKey()))
                                .orElse(true))
                .map(PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining("__")));
        return stringbuilder.toString();
    }

    @Override
    public String getName(BlockStateHolder p_61696_) {
        return getName(p_61696_.blockState);
    }

    @Override
    public Optional<BlockStateHolder> getValue(String input) {
        if (possibleValues == null)
            throw new IllegalStateException("Not initialized");
        return possibleValues.containsKey(input) ? Optional.of(possibleValues.get(input)) : Optional.empty();
    }

    public int generateHashCode() {
        return 31 * this.getClass().hashCode() ^ this.getName().hashCode();
    }

    public BlockStateHolder of(BlockState state) {
        if (possibleValues == null)
            throw new IllegalStateException("Not initialized");
        return possibleValues.get(getName(state));
    }

    public static class BlockStateHolder implements Comparable<BlockStateHolder>, Supplier<BlockState> {
        private final BlockState blockState;

        protected BlockStateHolder(BlockState blockState) {
            this.blockState = blockState;
        }

        @Override
        public int compareTo(@NotNull BlockStateProperty.BlockStateHolder o) {
            return Objects.requireNonNull(blockState.getBlock().getRegistryName()).compareTo(
                   Objects.requireNonNull(o.blockState.getBlock().getRegistryName()));
        }

        public Block getBlock() {
            return blockState.getBlock();
        }

        public BlockState get() {
            return blockState;
        }

        public String toString() {
            StringBuilder stringbuilder = new StringBuilder();
            stringbuilder.append(blockState.getBlock().getRegistryName().toString().replace(':', '0'));
            stringbuilder.append('0');
            stringbuilder.append(blockState.getValues().entrySet().stream()
                    .map(PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining("__")));
            return stringbuilder.toString();
        }

        public int hashCode() {
            return blockState.toString().hashCode();
        }
    }

    protected BlockStateProperty(String p_61692_, Optional<Predicate<Block>> keepBlock, Optional<Predicate<Property<?>>> keepProperty) {
        super(p_61692_, BlockStateHolder.class);
        this.keepBlock = keepBlock;
        this.keepProperty = keepProperty;
    }

    public static BlockStateProperty create(String name) {
        return new BlockStateProperty(name, Optional.empty(), Optional.empty());
    }

    public static BlockStateProperty create(String name, Predicate<Block> keepBlock) {
        return new BlockStateProperty(name, Optional.of(keepBlock), Optional.empty());
    }

    public static BlockStateProperty create(String name, Predicate<Block> keepBlock, Predicate<Property<?>> keepProperty) {
        return new BlockStateProperty(name, Optional.of(keepBlock), Optional.of(keepProperty));
    }

    public String toString() {
        return "BlockStateProperty{name=" + this.getName() + ", clazz=" + this.getClass().toString() + "}";
    }
}
