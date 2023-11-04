package net.ltxprogrammer.changed.world.features.structures.facility;

import net.ltxprogrammer.changed.block.GluBlock;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedStructurePieceTypes;
import net.ltxprogrammer.changed.util.TagUtil;
import net.ltxprogrammer.changed.world.features.structures.ChestLootTableProcessor;
import net.ltxprogrammer.changed.world.features.structures.GluReplacementProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public abstract class FacilitySinglePiece extends FacilityPiece {
    public final ResourceLocation templateName;
    public final Optional<ResourceLocation> lootTable;
    private StructureTemplate template = null;

    protected FacilitySinglePiece(PieceType type, Zone zone,
                                  ResourceLocation templateName) {
        super(type, zone);
        this.templateName = templateName;
        this.lootTable = Optional.empty();
    }

    protected FacilitySinglePiece(PieceType type, Zone zone,
                                  ResourceLocation templateName, ResourceLocation lootTable) {
        super(type, zone);
        this.templateName = templateName;
        this.lootTable = Optional.of(lootTable);
    }

    public static class StructureInstance extends FacilityPieceInstance {
        private final ResourceLocation templateName;
        private final ResourceLocation lootTable;
        private final StructureTemplate template;

        public StructureInstance(StructureManager manager, int genDepth, ResourceLocation templateName, Optional<ResourceLocation> lootTable, BoundingBox box) {
            super(ChangedStructurePieceTypes.FACILITY_SINGLE.get(), genDepth, box);
            this.templateName = templateName;
            this.lootTable = lootTable.orElse(null);
            this.template = manager.getOrCreate(templateName);
        }

        public StructureInstance(StructureManager manager, CompoundTag tag) {
            super(ChangedStructurePieceTypes.FACILITY_SINGLE.get(), tag);
            this.templateName = TagUtil.getResourceLocation(tag, "template");
            if (tag.contains("lootTable"))
                this.lootTable = TagUtil.getResourceLocation(tag, "lootTable");
            else
                this.lootTable = null;
            this.template = manager.getOrCreate(templateName);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
            super.addAdditionalSaveData(context, tag);
            TagUtil.putResourceLocation(tag, "template", templateName);
            if (lootTable != null)
                TagUtil.putResourceLocation(tag, "lootTable", lootTable);
        }

        private BlockPos getGenerationPosition() {
            return new BlockPos(this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ());
        }

        @Override
        public void postProcess(WorldGenLevel level, StructureFeatureManager manager, ChunkGenerator generator, Random random, BoundingBox bb, ChunkPos pos, BlockPos blockPos) {
            var settings = new StructurePlaceSettings()
                    .setMirror(this.getMirror())
                    .setRotation(this.getRotation()).setRandom(random)
                    .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
                    .addProcessor(JigsawReplacementProcessor.INSTANCE)
                    .addProcessor(GluReplacementProcessor.INSTANCE)
                    .setIgnoreEntities(false);
            if (lootTable != null)
                settings.addProcessor(ChestLootTableProcessor.of(lootTable));
            template.placeInWorld(level, getGenerationPosition(), blockPos, settings, random, 2);
        }

        @Override
        public void addSteps(FacilityPiece parent, List<GenStep> steps) {
            var settings = new StructurePlaceSettings()
                    .setMirror(this.getMirror())
                    .setRotation(this.getRotation())
                    .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
                    .setIgnoreEntities(true);
            template.filterBlocks(getGenerationPosition(), settings, ChangedBlocks.GLU_BLOCK.get()).forEach(blockInfo -> {
                steps.add(new GenStep(blockInfo, parent.getValidNeighbors()));
            });
        }

        private static BlockPos gluNeighbor(BlockPos gluPos, BlockState gluState) {
            return gluPos.relative(gluState.getValue(GluBlock.ORIENTATION).front());
        }

        @Override
        public boolean setupBoundingBox(StructurePiecesBuilder builder, StructureTemplate.StructureBlockInfo exitGlu) {
            var settings = new StructurePlaceSettings()
                    .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
                    .setIgnoreEntities(true);

            var gluBlockPos = gluNeighbor(exitGlu.pos, exitGlu.state);
            for (Direction dir : Direction.Plane.HORIZONTAL) { // TODO randomly sort
                this.setRotation(dir);
                settings.setRotation(this.getRotation());
                for (var blockInfo : template.filterBlocks(BlockPos.ZERO, settings, ChangedBlocks.GLU_BLOCK.get())) {
                    if (!GluBlock.canConnect(exitGlu.state, blockInfo.state))
                        continue;
                    this.setupBoundingBox(gluBlockPos.subtract(blockInfo.pos));

                    if (builder.findCollisionPiece(this.boundingBox) == null)
                        return true;
                }
            }

            return false;
        }

        @Override
        public void setupBoundingBox(BlockPos minimum) {
            this.boundingBox = template.getBoundingBox(BlockPos.ZERO, this.getRotation(), BlockPos.ZERO, this.getMirror());
            this.boundingBox.move( // Move bounding box so minimum is always BlockPos.ZERO
                    -Math.min(this.boundingBox.minX(), 0),
                    -Math.min(this.boundingBox.minY(), 0),
                    -Math.min(this.boundingBox.minZ(), 0));
            this.boundingBox.move(minimum);
        }
    }

    @Override
    public FacilityPieceInstance createStructurePiece(StructureManager structures, int genDepth) {
        return new StructureInstance(structures, genDepth, templateName, lootTable, null);
    }
}
