package net.ltxprogrammer.changed.entity.variant;

import net.minecraft.util.StringRepresentable;
import net.minecraftforge.common.IExtensibleEnum;

/**
 * Describes the shape of the entity, and what armor they can wear
 */
public enum EntityShape implements EntityShapeProvider, IExtensibleEnum, StringRepresentable {
    /**
     * Upright bipedal entity
     */
    ANTHRO("anthro", ClothingShape.Head.ANTHRO, ClothingShape.Torso.ANTHRO, ClothingShape.Legs.BIPEDAL, ClothingShape.Feet.BIPEDAL),
    /**
     * Quadrupedal entity, set on all fours
     */
    FERAL("feral", ClothingShape.Head.NONE, ClothingShape.Torso.NONE, ClothingShape.Legs.NONE, ClothingShape.Feet.NONE),
    /**
     * Half anthro, 2/3 quadrupedal
     */
    TAUR("taur", ClothingShape.Head.ANTHRO, ClothingShape.Torso.ANTHRO, ClothingShape.Legs.QUADRUPEDAL, ClothingShape.Feet.QUADRUPEDAL),
    /**
     * Half anthro, half snake
     */
    NAGA("naga", ClothingShape.Head.ANTHRO, ClothingShape.Torso.ANTHRO, ClothingShape.Legs.TAIL, ClothingShape.Feet.TAIL),
    /**
     * Half anthro, half fish
     */
    MER("mer", ClothingShape.Head.ANTHRO, ClothingShape.Torso.ANTHRO, ClothingShape.Legs.TAIL, ClothingShape.Feet.TAIL);

    private final String serialName;
    public final ClothingShape.Head headShape;
    public final ClothingShape.Torso torsoShape;
    public final ClothingShape.Legs legsShape;
    public final ClothingShape.Feet feetShape;

    EntityShape(String serialName, ClothingShape.Head headShape, ClothingShape.Torso torsoShape, ClothingShape.Legs legsShape, ClothingShape.Feet feetShape) {
        this.serialName = serialName;
        this.headShape = headShape;
        this.torsoShape = torsoShape;
        this.legsShape = legsShape;
        this.feetShape = feetShape;
    }

    @Override
    public String getSerializedName() {
        return serialName;
    }

    @Override
    public ClothingShape.Head getHeadShape() {
        return headShape;
    }

    @Override
    public ClothingShape.Torso getTorsoShape() {
        return torsoShape;
    }

    @Override
    public ClothingShape.Legs getLegsShape() {
        return legsShape;
    }

    @Override
    public ClothingShape.Feet getFeetShape() {
        return feetShape;
    }

    public boolean isLegless() {
        return legsShape == ClothingShape.Legs.TAIL && feetShape == ClothingShape.Feet.TAIL;
    }

    public static EntityShape create(String name, String serialName, ClothingShape.Head headShape, ClothingShape.Torso torsoShape, ClothingShape.Legs legsShape, ClothingShape.Feet feetShape) {
        throw new IllegalStateException("Enum not extended");
    }
}
