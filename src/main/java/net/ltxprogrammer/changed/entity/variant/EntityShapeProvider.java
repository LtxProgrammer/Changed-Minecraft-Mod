package net.ltxprogrammer.changed.entity.variant;

public interface EntityShapeProvider {
    ClothingShape.Head getHeadShape();
    ClothingShape.Torso getTorsoShape();
    ClothingShape.Legs getLegsShape();
    ClothingShape.Feet getFeetShape();
}
