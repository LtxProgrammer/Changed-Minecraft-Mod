package net.ltxprogrammer.changed.computers.protocol;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public final class DeviceInfoProtocol extends Packet {
    private final Component deviceName;
    private final BlockPos position;
    private final ResourceLocation icon;

    public DeviceInfoProtocol(Component deviceName, BlockPos position, ResourceLocation icon) {
        this.deviceName = deviceName;
        this.position = position;
        this.icon = icon;
    }

    public Component deviceName() {
        return deviceName;
    }

    public BlockPos position() {
        return position;
    }

    public ResourceLocation icon() {
        return icon;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DeviceInfoProtocol) obj;
        return Objects.equals(this.deviceName, that.deviceName) &&
                Objects.equals(this.position, that.position) &&
                Objects.equals(this.icon, that.icon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceName, position, icon);
    }

    @Override
    public String toString() {
        return "DeviceInfoProtocol[" +
                "deviceName=" + deviceName + ", " +
                "position=" + position + ", " +
                "icon=" + icon + ']';
    }

    public static class Query extends Packet {
        public static final Query INSTANCE = new Query();

        private Query() {}
    }
}
