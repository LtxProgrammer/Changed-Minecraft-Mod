package net.ltxprogrammer.changed.block.entity;

import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.Optional;

public class GluBlockEntity extends BlockEntity {
    public static final String SIZE = "size";
    public static final String HAS_DOOR = "door";
    public static final String JOINT = "joint";
    public static final String FINAL_STATE = "final_state";

    private int size = 3;
    private boolean hasDoor = false;
    private JointType jointType = JointType.ENTRANCE;
    private String finalState = "minecraft:air";

    public GluBlockEntity(BlockPos pos, BlockState state) {
        super(ChangedBlockEntities.GLU.get(), pos, state);
    }

    public int getSize() {
        return this.size;
    }

    public boolean getHasDoor() {
        return this.hasDoor;
    }

    public JointType getJointType() {
        return this.jointType;
    }

    public String getFinalState() {
        return this.finalState;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setHasDoor(boolean hasDoor) {
        this.hasDoor = hasDoor;
    }

    public void setJointType(JointType type) {
        this.jointType = type;
    }

    public void setFinalState(String state) {
        this.finalState = state;
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(SIZE, this.size);
        tag.putBoolean(HAS_DOOR, this.hasDoor);
        tag.putString(JOINT, this.jointType.getSerializedName());
        tag.putString(FINAL_STATE, this.finalState);
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        this.size = tag.getInt(SIZE);
        this.hasDoor = tag.getBoolean(HAS_DOOR);
        this.jointType = JointType.byName(tag.getString(JOINT)).orElseThrow();
        this.finalState = tag.getString(FINAL_STATE);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public static enum JointType implements StringRepresentable {
        ENTRANCE("entrance"),
        EXIT("exit");

        private final String name;

        private JointType(String p_59455_) {
            this.name = p_59455_;
        }

        public String getSerializedName() {
            return this.name;
        }

        public static Optional<JointType> byName(String p_59458_) {
            return Arrays.stream(values()).filter((p_59461_) -> {
                return p_59461_.getSerializedName().equals(p_59458_);
            }).findFirst();
        }

        public Component getTranslatedName() {
            return new TranslatableComponent("glu_block.joint." + this.name);
        }

        public JointType next() {
            int nextOrdinal = this.ordinal() + 1;
            if (nextOrdinal >= values().length)
                nextOrdinal = 0;
            return values()[nextOrdinal];
        }

        public boolean canConnectTo(JointType other) {
            return this != other;
        }
    }
}
