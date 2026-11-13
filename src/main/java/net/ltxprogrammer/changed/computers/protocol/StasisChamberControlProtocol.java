package net.ltxprogrammer.changed.computers.protocol;

import net.ltxprogrammer.changed.block.entity.StasisChamberBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public final class StasisChamberControlProtocol extends Frame {
    public static final StasisChamberControlProtocol INSTANCE = new StasisChamberControlProtocol();

    private StasisChamberControlProtocol() {}

    @Override
    public String toString() {
        return "StasisControlProtocol";
    }

    public static final class InputProgram extends Frame {
        public final StasisChamberBlockEntity.Program program;
        public final ServerPlayer controller;

        public InputProgram(StasisChamberBlockEntity.Program program, ServerPlayer controller) {
            this.program = program;
            this.controller = controller;
        }

        @Override
        public String toString() {
            return "StasisControlProtocol.InputProgram[" +
                    "program=" + program.getId() + ", " +
                    "controller=" + controller + ']';
        }
    }

    public static final class SetModifications extends Frame {
        public final CompoundTag modifications;

        public SetModifications(CompoundTag modifications) {
            this.modifications = modifications;
        }

        @Override
        public String toString() {
            return "StasisControlProtocol.SetModifications[" +
                    "modifications=" + modifications + ']';
        }
    }

    public static final class SetWaitDuration extends Frame {
        public final int ticks;
        public final ServerPlayer controller;

        public SetWaitDuration(int ticks, ServerPlayer controller) {
            this.ticks = ticks;
            this.controller = controller;
        }

        @Override
        public String toString() {
            return "StasisControlProtocol.SetWaitDuration[" +
                    "ticks=" + ticks + ", " +
                    "controller=" + controller + ']';
        }
    }
}
