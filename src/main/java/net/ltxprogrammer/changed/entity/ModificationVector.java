package net.ltxprogrammer.changed.entity;

import net.minecraft.core.Registry;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ModificationVector {
    Component getDisplayText();
    @Nullable Component getTooltipText();

    Tag writeAsTag();
    boolean readFromTag(Tag tag);

    /**
     * Rendered as a button that cycles through possible values
     */
    interface EnumVector extends ModificationVector {
        void cycleForward();
        void cycleBackward();
    }

    /**
     * Rendered as a horizontal slider
     */
    interface LinearVector extends ModificationVector {
        /**
         * @return A value between 0 and 1 (inclusive)
         */
        double getValue();

        /**
         * @param value A value between 0 and 1 (inclusive)
         */
        void acceptValue(double value);
    }

    /**
     * Rendered as a checkbox that is either off or on
     */
    interface BooleanVector extends ModificationVector {
        boolean getState();
        void acceptState(boolean state);
    }

    class SimpleEnumVector<T extends StringRepresentable> implements EnumVector {
        private final List<T> values;
        private final Supplier<T> getCurrentValue;
        private final Consumer<T> setValue;
        private final String displayId;
        private final @Nullable String tooltipId;

        public SimpleEnumVector(List<T> values, Supplier<T> getCurrentValue, Consumer<T> setValue, String displayId, @Nullable String tooltipId) {
            this.values = List.copyOf(values);
            this.getCurrentValue = getCurrentValue;
            this.setValue = setValue;
            this.displayId = displayId;
            this.tooltipId = tooltipId;
        }

        public void cycleForward() {
            var current = this.getCurrentValue.get();
            int index = values.indexOf(current);
            this.setValue.accept(values.get((index + 1) % values.size()));
        }

        public void cycleBackward() {
            var current = this.getCurrentValue.get();
            int index = values.indexOf(current);
            this.setValue.accept(values.get((index - 1) % values.size()));
        }

        @Override
        public Component getDisplayText() {
            return Component.translatable(displayId, getCurrentValue.get().getSerializedName());
        }

        @Override
        public @Nullable Component getTooltipText() {
            return tooltipId == null ? null : Component.translatable(tooltipId);
        }

        @Override
        public Tag writeAsTag() {
            return StringTag.valueOf(getCurrentValue.get().getSerializedName());
        }

        @Override
        public boolean readFromTag(Tag tag) {
            var serialized = ((StringTag)tag).getAsString();
            for (var value : values) {
                if (value.getSerializedName().equals(serialized)) {
                    if (getCurrentValue.get() == value)
                        return false;

                    setValue.accept(value);
                    return true;
                }
            }

            return false;
        }
    }

    class SimpleRegistryVector<T> implements EnumVector {
        private final Registry<T> registry;
        private final Supplier<T> getCurrentValue;
        private final Consumer<T> setValue;
        private final String displayId;
        private final @Nullable String tooltipId;

        public SimpleRegistryVector(Registry<T> registry, Supplier<T> getCurrentValue, Consumer<T> setValue, String displayId, @Nullable String tooltipId) {
            this.registry = registry;
            this.getCurrentValue = getCurrentValue;
            this.setValue = setValue;
            this.displayId = displayId;
            this.tooltipId = tooltipId;
        }

        public void cycleForward() {
            var current = this.getCurrentValue.get();

            T firstValue = null;
            boolean forwardNext = false;

            for (Map.Entry<ResourceKey<T>, T> entry : registry.entrySet()) {
                if (firstValue == null)
                    firstValue = entry.getValue();

                if (forwardNext) {
                    this.setValue.accept(entry.getValue());
                    return;
                }

                if (current == entry.getValue())
                    forwardNext = true;
            }

            if (firstValue != null)
                this.setValue.accept(firstValue);
        }

        public void cycleBackward() {
            var current = this.getCurrentValue.get();

            T firstValue = null;
            boolean forwardNext = false;

            var entryList = registry.entrySet().stream().toList();
            var it = entryList.listIterator(entryList.size());

            while (it.hasPrevious()) {
                var entry = it.previous();
                if (firstValue == null)
                    firstValue = entry.getValue();

                if (forwardNext) {
                    this.setValue.accept(entry.getValue());
                    return;
                }

                if (current == entry.getValue())
                    forwardNext = true;
            }

            if (firstValue != null)
                this.setValue.accept(firstValue);
        }

        @Override
        public Component getDisplayText() {
            return Component.translatable(displayId, registry.getKey(getCurrentValue.get()));
        }

        @Override
        public @Nullable Component getTooltipText() {
            return tooltipId == null ? null : Component.translatable(tooltipId);
        }

        @Override
        public Tag writeAsTag() {
            return IntTag.valueOf(registry.getId(getCurrentValue.get()));
        }

        @Override
        public boolean readFromTag(Tag tag) {
            var value = registry.byId(((IntTag)tag).getAsInt());

            if (getCurrentValue.get() == value)
                return false;

            setValue.accept(value);
            return true;
        }
    }

    class SimpleForgeRegistryVector<T> implements EnumVector {
        private final IForgeRegistry<T> registry;
        private final Supplier<T> getCurrentValue;
        private final Consumer<T> setValue;
        private final String displayId;
        private final @Nullable String tooltipId;

        public SimpleForgeRegistryVector(IForgeRegistry<T> registry, Supplier<T> getCurrentValue, Consumer<T> setValue, String displayId, @Nullable String tooltipId) {
            this.registry = registry;
            this.getCurrentValue = getCurrentValue;
            this.setValue = setValue;
            this.displayId = displayId;
            this.tooltipId = tooltipId;
        }

        public void cycleForward() {
            var current = this.getCurrentValue.get();

            T firstValue = null;
            boolean forwardNext = false;

            for (Map.Entry<ResourceKey<T>, T> entry : registry.getEntries()) {
                if (firstValue == null)
                    firstValue = entry.getValue();

                if (forwardNext) {
                    this.setValue.accept(entry.getValue());
                    return;
                }

                if (current == entry.getValue())
                    forwardNext = true;
            }

            if (firstValue != null)
                this.setValue.accept(firstValue);
        }

        public void cycleBackward() {
            var current = this.getCurrentValue.get();

            T firstValue = null;
            boolean forwardNext = false;

            var entryList = registry.getEntries().stream().toList();
            var it = entryList.listIterator(entryList.size());

            while (it.hasPrevious()) {
                var entry = it.previous();
                if (firstValue == null)
                    firstValue = entry.getValue();

                if (forwardNext) {
                    this.setValue.accept(entry.getValue());
                    return;
                }

                if (current == entry.getValue())
                    forwardNext = true;
            }

            if (firstValue != null)
                this.setValue.accept(firstValue);
        }

        @Override
        public Component getDisplayText() {
            return Component.translatable(displayId, registry.getKey(getCurrentValue.get()));
        }

        @Override
        public @Nullable Component getTooltipText() {
            return tooltipId == null ? null : Component.translatable(tooltipId);
        }

        @Override
        public Tag writeAsTag() {
            return StringTag.valueOf(registry.getKey(getCurrentValue.get()).toString());
        }

        @Override
        public boolean readFromTag(Tag tag) {
            var value = registry.getValue(ResourceLocation.parse(((StringTag)tag).getAsString()));

            if (getCurrentValue.get() == value)
                return false;

            setValue.accept(value);
            return true;
        }
    }

    class SimpleLinearVector implements LinearVector {
        private final double minimumValue;
        private final double maximumValue;
        private final Supplier<Double> getCurrentValue;
        private final Consumer<Double> setValue;
        private final String displayId;
        private final @Nullable String tooltipId;

        public SimpleLinearVector(double minimumValue, double maximumValue, Supplier<Double> getCurrentValue, Consumer<Double> setValue, String displayId, @Nullable String tooltipId) {
            this.minimumValue = Math.min(minimumValue, maximumValue);
            this.maximumValue = Math.max(minimumValue, maximumValue);
            this.getCurrentValue = getCurrentValue;
            this.setValue = setValue;
            this.displayId = displayId;
            this.tooltipId = tooltipId;
        }

        @Override
        public double getValue() {
            return Mth.inverseLerp(getCurrentValue.get(), minimumValue, maximumValue);
        }

        @Override
        public void acceptValue(double value) {
            setValue.accept(Mth.lerp(value, minimumValue, maximumValue));
        }

        @Override
        public Component getDisplayText() {
            return Component.translatable(displayId, getCurrentValue.get());
        }

        @Override
        public @Nullable Component getTooltipText() {
            return tooltipId == null ? null : Component.translatable(tooltipId);
        }

        @Override
        public Tag writeAsTag() {
            return DoubleTag.valueOf(getCurrentValue.get());
        }

        @Override
        public boolean readFromTag(Tag tag) {
            setValue.accept(((DoubleTag)tag).getAsDouble());
            return false;
        }
    }

    class SimpleBooleanVector implements BooleanVector {
        private final Supplier<Boolean> getCurrentValue;
        private final Consumer<Boolean> setValue;
        private final String displayId;
        private final @Nullable String tooltipId;

        public SimpleBooleanVector(Supplier<Boolean> getCurrentValue, Consumer<Boolean> setValue, String displayId, @Nullable String tooltipId) {
            this.getCurrentValue = getCurrentValue;
            this.setValue = setValue;
            this.displayId = displayId;
            this.tooltipId = tooltipId;
        }

        @Override
        public boolean getState() {
            return getCurrentValue.get();
        }

        @Override
        public void acceptState(boolean state) {
            setValue.accept(state);
        }

        @Override
        public Component getDisplayText() {
            return Component.translatable(displayId, getCurrentValue.get());
        }

        @Override
        public @Nullable Component getTooltipText() {
            return tooltipId == null ? null : Component.translatable(tooltipId);
        }

        @Override
        public Tag writeAsTag() {
            return ByteTag.valueOf(getCurrentValue.get());
        }

        @Override
        public boolean readFromTag(Tag tag) {
            boolean newValue = ((ByteTag)tag).getAsByte() != 0;
            if (newValue == getCurrentValue.get())
                return false;

            setValue.accept(((ByteTag)tag).getAsByte() != 0);
            return true;
        }
    }

    static <T extends Enum<T> & StringRepresentable> ModificationVector simpleEnum(Class<T> clazz, Supplier<T> getCurrentValue, Consumer<T> setValue, String displayId) {
        return simpleEnum(clazz, getCurrentValue, setValue, displayId, displayId + ".tooltip");
    }

    static <T extends Enum<T> & StringRepresentable> ModificationVector simpleEnum(Class<T> clazz, Supplier<T> getCurrentValue, Consumer<T> setValue, String displayId, @Nullable String tooltipId) {
        return simpleEnum(Arrays.stream(clazz.getEnumConstants()).toList(),
                getCurrentValue, setValue, displayId, tooltipId);
    }

    static <T extends StringRepresentable> ModificationVector simpleEnum(List<T> values, Supplier<T> getCurrentValue, Consumer<T> setValue, String displayId, @Nullable String tooltipId) {
        return new SimpleEnumVector<>(values, getCurrentValue, setValue, displayId, tooltipId);
    }

    static <T> ModificationVector simpleEnum(Registry<T> registry, Supplier<T> getCurrentValue, Consumer<T> setValue, String displayId) {
        return simpleEnum(registry, getCurrentValue, setValue, displayId, displayId + ".tooltip");
    }

    static <T> ModificationVector simpleEnum(Registry<T> registry, Supplier<T> getCurrentValue, Consumer<T> setValue, String displayId, @Nullable String tooltipId) {
        return new SimpleRegistryVector<>(registry, getCurrentValue, setValue, displayId, tooltipId);
    }

    static <T> ModificationVector simpleEnum(IForgeRegistry<T> registry, Supplier<T> getCurrentValue, Consumer<T> setValue, String displayId) {
        return simpleEnum(registry, getCurrentValue, setValue, displayId, displayId + ".tooltip");
    }

    static <T> ModificationVector simpleEnum(IForgeRegistry<T> registry, Supplier<T> getCurrentValue, Consumer<T> setValue, String displayId, @Nullable String tooltipId) {
        return new SimpleForgeRegistryVector<>(registry, getCurrentValue, setValue, displayId, tooltipId);
    }

    static ModificationVector simpleLinear(double minimum, double maximum, Supplier<Double> getCurrentValue, Consumer<Double> setValue, String displayId) {
        return simpleLinear(minimum, maximum, getCurrentValue, setValue, displayId, displayId + ".tooltip");
    }

    static ModificationVector simpleLinear(double minimum, double maximum, Supplier<Double> getCurrentValue, Consumer<Double> setValue, String displayId, @Nullable String tooltipId) {
        return new SimpleLinearVector(minimum, maximum, getCurrentValue, setValue, displayId, tooltipId);
    }

    static ModificationVector simpleBoolean(Supplier<Boolean> getCurrentValue, Consumer<Boolean> setValue, String displayId) {
        return simpleBoolean(getCurrentValue, setValue, displayId, displayId + ".tooltip");
    }

    static ModificationVector simpleBoolean(Supplier<Boolean> getCurrentValue, Consumer<Boolean> setValue, String displayId, @Nullable String tooltipId) {
        return new SimpleBooleanVector(getCurrentValue, setValue, displayId, tooltipId);
    }
}
