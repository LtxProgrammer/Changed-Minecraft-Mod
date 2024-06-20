package net.ltxprogrammer.changed.network.syncher;

import net.ltxprogrammer.changed.entity.BasicPlayerInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

public class ChangedEntityDataSerializers {
    public static final EntityDataSerializer<BasicPlayerInfo> BASIC_PLAYER_INFO = new EntityDataSerializer<BasicPlayerInfo>() {
        public void write(FriendlyByteBuf buffer, BasicPlayerInfo info) {
            var tag = new CompoundTag();
            info.save(tag);
            buffer.writeNbt(tag);
        }

        public BasicPlayerInfo read(FriendlyByteBuf buffer) {
            BasicPlayerInfo info = new BasicPlayerInfo();
            info.load(buffer.readNbt());
            return info;
        }

        public BasicPlayerInfo copy(BasicPlayerInfo info) {
            BasicPlayerInfo newInfo = new BasicPlayerInfo();
            newInfo.copyFrom(info);
            return newInfo;
        }
    };

    static {
        EntityDataSerializers.registerSerializer(BASIC_PLAYER_INFO);
    }
}
