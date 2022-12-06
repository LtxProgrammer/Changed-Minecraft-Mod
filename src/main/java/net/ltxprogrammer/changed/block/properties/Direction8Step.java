package net.ltxprogrammer.changed.block.properties;

import net.minecraft.util.StringRepresentable;

public enum Direction8Step implements StringRepresentable {
    NORTH("north"),
    NORTHEAST("northeast"),
    EAST("east"),
    SOUTHEAST("southeast"),
    SOUTH("south"),
    SOUTHWEST("southwest"),
    WEST("west"),
    NORTHWEST("northwest");

    final String name;

    Direction8Step(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return null;
    }
}
