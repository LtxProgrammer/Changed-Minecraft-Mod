package net.ltxprogrammer.changed.computers;

import net.ltxprogrammer.changed.computers.protocol.FileSystemShareProtocol;
import net.ltxprogrammer.changed.computers.protocol.LogicalNetworkInterface;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public interface SourcedDiscData {
    DiscData getDiscData();
    void writeBack();
    boolean matchesOrigin(Object origin);

    boolean canEject();
    void eject();

    default String getName() {
        return getDiscData().getName();
    }

    static SourcedDiscData fromItem(ItemStack itemStack, Runnable modifiedListener, Consumer<ItemStack> onEject) {
        return new ItemStackSource(itemStack, modifiedListener, onEject);
    }

    static SourcedDiscData wrap(DiscData data, boolean ejectable) {
        return wrap(data, ejectable, $ -> {}, true);
    }

    static SourcedDiscData wrap(DiscData data, boolean ejectable, Consumer<DiscData> writer, boolean onlyWriteWhenModified) {
        return new WrappedDiscDataSource(data, ejectable, writer, onlyWriteWhenModified);
    }

    static SourcedDiscData fromRemote(int logicalAddress, String name, @Nullable FileSystemShareProtocol share) {
        return new RemoteSource(logicalAddress, name, share);
    }

    class WrappedDiscDataSource implements SourcedDiscData {
        final DiscData discData;
        final boolean ejectable;
        final Consumer<DiscData> writer;
        final boolean onlyWriteWhenModified;

        public WrappedDiscDataSource(DiscData discData, boolean ejectable, Consumer<DiscData> writer, boolean onlyWriteWhenModified) {
            this.discData = discData;
            this.ejectable = ejectable;
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
        public boolean canEject() {
            return ejectable;
        }

        @Override
        public void eject() {

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
        final Runnable modifiedListener;
        final Consumer<ItemStack> onEject;

        public ItemStackSource(ItemStack originItem, Runnable modifiedListener, Consumer<ItemStack> onEject) {
            this.originItem = originItem;
            this.modifiedListener = modifiedListener;
            this.onEject = onEject;
        }

        @Override
        public DiscData getDiscData() {
            if (cachedDiscData == null) {
                var tag = originItem.getOrCreateTag();
                if (tag.contains("fs"))
                    cachedDiscData = new DiscData(originItem.getOrCreateTag().getCompound("fs"), modifiedListener);
                else
                    cachedDiscData = new DiscData(modifiedListener);
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
        public boolean canEject() {
            return true;
        }

        @Override
        public void eject() {
            writeBack();
            onEject.accept(originItem);
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

    class RemoteSource implements SourcedDiscData {
        final int logicalAddress;
        DiscData data;

        public RemoteSource(int logicalAddress, String name, @Nullable FileSystemShareProtocol share) {
            this.logicalAddress = logicalAddress;
            if (share != null) {
                data = DiscData.copyOf(share.getRootFolder(), this::markModified);
                data.setPermissions(share.getPermissions());
            } else
                data = new DiscData(this::markModified);
            data.setName(name);
        }

        @Override
        public DiscData getDiscData() {
            return data;
        }

        @Override
        public void writeBack() {
            if (data.getPermissions().canWrite()) {
                // TODO
            }
        }

        protected void markModified() {

        }

        @Override
        public boolean matchesOrigin(Object origin) {
            return origin.equals(logicalAddress);
        }

        @Override
        public boolean canEject() {
            return false;
        }

        @Override
        public void eject() {

        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof RemoteSource that)) return false;
            return Objects.equals(logicalAddress, that.logicalAddress);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(logicalAddress);
        }

        @Override
        public String toString() {
            return "RemoteSource{" +
                    "address=" + LogicalNetworkInterface.logicalAddressAsString(logicalAddress) +
                    '}';
        }
    }
}
