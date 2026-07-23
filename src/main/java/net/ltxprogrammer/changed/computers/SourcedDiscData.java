package net.ltxprogrammer.changed.computers;

import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.function.Consumer;

public interface SourcedDiscData {
    DiscData getDiscData();
    void writeBack();
    boolean matchesOrigin(Object origin);

    default String getName() {
        return getDiscData().getName();
    }

    static SourcedDiscData fromItem(ItemStack itemStack) {
        return new ItemStackSource(itemStack);
    }

    static SourcedDiscData wrap(DiscData data) {
        return wrap(data, $ -> {}, true);
    }

    static SourcedDiscData wrap(DiscData data, Consumer<DiscData> writer, boolean onlyWriteWhenModified) {
        return new WrappedDiscDataSource(data, writer, onlyWriteWhenModified);
    }

    class WrappedDiscDataSource implements SourcedDiscData {
        final DiscData discData;
        final Consumer<DiscData> writer;
        final boolean onlyWriteWhenModified;

        public WrappedDiscDataSource(DiscData discData, Consumer<DiscData> writer, boolean onlyWriteWhenModified) {
            this.discData = discData;
            this.writer = writer;
            this.onlyWriteWhenModified = onlyWriteWhenModified;
        }

        @Override
        public DiscData getDiscData() {
            return discData;
        }

        @Override
        public void writeBack() {
            if (!onlyWriteWhenModified || discData.isModified()) {
                writer.accept(discData);
                discData.clearModified();
            }
        }

        @Override
        public boolean matchesOrigin(Object origin) {
            return discData.equals(origin);
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof WrappedDiscDataSource that)) return false;
            return Objects.equals(discData, that.discData);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(discData);
        }

        @Override
        public String toString() {
            return "WrappedDiscDataSource{" +
                    "discData=" + discData +
                    '}';
        }
    }

    class ItemStackSource implements SourcedDiscData {
        DiscData cachedDiscData = null;
        final ItemStack originItem;

        public ItemStackSource(ItemStack originItem) {
            this.originItem = originItem;
        }

        @Override
        public DiscData getDiscData() {
            if (cachedDiscData == null) {
                var tag = originItem.getOrCreateTag();
                if (tag.contains("fs"))
                    cachedDiscData = new DiscData(originItem.getOrCreateTag().getCompound("fs"));
                else
                    cachedDiscData = new DiscData();
            }

            return cachedDiscData;
        }

        @Override
        public void writeBack() {
            var data = getDiscData();
            if (data.isModified()) {
                originItem.getOrCreateTag().put("fs", data.serialize());
                data.clearModified();
            }
        }

        @Override
        public boolean matchesOrigin(Object origin) {
            return originItem.equals(origin);
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof ItemStackSource that)) return false;
            return Objects.equals(originItem, that.originItem);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(originItem);
        }

        @Override
        public String toString() {
            return "ItemStackSource{" +
                    "originItem=" + originItem +
                    '}';
        }
    }
}
