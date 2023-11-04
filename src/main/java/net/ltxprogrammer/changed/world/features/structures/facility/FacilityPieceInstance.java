package net.ltxprogrammer.changed.world.features.structures.facility;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import java.util.List;

public abstract class FacilityPieceInstance extends StructurePiece {
    private Rotation rotation;

    protected FacilityPieceInstance(StructurePieceType type, int genDepth, BoundingBox boundingBox) {
        super(type, genDepth, boundingBox);
        this.setOrientation(Direction.NORTH);
        this.setRotation(Direction.NORTH);
    }

    public FacilityPieceInstance(StructurePieceType type, CompoundTag tag) {
        super(type, tag);
        this.setRotation(Rotation.values()[tag.getInt("R")]);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putInt("R", rotation.ordinal());
    }

    public abstract void addSteps(FacilityPiece parent, List<GenStep> steps);

    public abstract boolean setupBoundingBox(StructurePiecesBuilder builder, StructureTemplate.StructureBlockInfo exitGlu);
    public abstract void setupBoundingBox(BlockPos minimum);

    public void setupBoundingBoxOnBottomCenter(BlockPos center) {
        this.setupBoundingBox(BlockPos.ZERO);
        var offset = new Vec3i(
                (this.boundingBox.maxX() / 2) + center.getX(),
                center.getY(),
                (this.boundingBox.maxZ() / 2) + center.getZ());
        this.boundingBox.move(offset);
    }

    @Override
    public Rotation getRotation() {
        return rotation;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    public void setRotation(@Nullable Direction direction) {
        if (direction == null) {
            this.rotation = Rotation.NONE;
        } else {
            switch (direction) {
                case SOUTH -> this.rotation = Rotation.CLOCKWISE_180;
                case WEST -> this.rotation = Rotation.COUNTERCLOCKWISE_90;
                case EAST -> this.rotation = Rotation.CLOCKWISE_90;
                default -> this.rotation = Rotation.NONE;
            }
        }
    }
}
