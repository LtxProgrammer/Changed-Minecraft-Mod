package net.ltxprogrammer.changed.world.features.structures.facility;

import net.ltxprogrammer.changed.Changed;
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

import java.util.*;

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
        private BlockPos generationPosition;

        public StructureInstance(StructureManager manager, int genDepth, ResourceLocation templateName, Optional<ResourceLocation> lootTable, BoundingBox box) {
            super(ChangedStructurePieceTypes.FACILITY_SINGLE.get(), genDepth, box);
            this.templateName = templateName;
            this.lootTable = lootTable.orElse(null);
            this.template = manager.get(templateName).orElseThrow();
        }

        public StructureInstance(StructureManager manager, CompoundTag tag) {
            super(ChangedStructurePieceTypes.FACILITY_SINGLE.get(), tag);
            this.generationPosition = TagUtil.getBlockPos(tag, "genPos");
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
            TagUtil.putBlockPos(tag, "genPos", generationPosition);
            TagUtil.putResourceLocation(tag, "template", templateName);
            if (lootTable != null)
                TagUtil.putResourceLocation(tag, "lootTable", lootTable);
        }

        @Override
        public void postProcess(WorldGenLevel level, StructureFeatureManager manager, ChunkGenerator generator, Random random, BoundingBox bb, ChunkPos pos, BlockPos blockPos) {
            var settings = new StructurePlaceSettings()
                    .setMirror(this.getMirror())
                    .setRotation(this.getRotation()).setRandom(random)
                    .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
                    .addProcessor(JigsawReplacementProcessor.INSTANCE)
                    .addProcessor(GluReplacementProcessor.INSTANCE)
                    .setKeepLiquids(false)
                    .setIgnoreEntities(false);
            if (lootTable != null)
                settings.addProcessor(ChestLootTableProcessor.of(lootTable));
            template.placeInWorld(level, generationPosition, blockPos, settings, random, 2);
        }

        @Override
        public void addSteps(FacilityGenerationStack stack, List<GenStep> steps) {
            var settings = new StructurePlaceSettings()
                    .setMirror(this.getMirror())
                    .setRotation(this.getRotation())
                    .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
                    .setIgnoreEntities(true);
            template.filterBlocks(generationPosition, settings, ChangedBlocks.GLU_BLOCK.get()).forEach(blockInfo -> {
                steps.add(new GenStep(blockInfo, stack.getParent().getValidNeighbors(stack)));
            });
        }

        private static BlockPos gluNeighbor(BlockPos gluPos, BlockState gluState) {
            return gluPos.relative(gluState.getValue(GluBlock.ORIENTATION).front());
        }

        @Override
        public boolean setupBoundingBox(StructurePiecesBuilder builder, StructureTemplate.StructureBlockInfo exitGlu, Random random) {
            var settings = new StructurePlaceSettings()
                    .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
                    .setIgnoreEntities(true);

            var gluBlockPos = gluNeighbor(exitGlu.pos, exitGlu.state);

            var directions = new ArrayList<>(Direction.Plane.HORIZONTAL.stream().toList());
            Collections.shuffle(directions, random);
            for (Direction dir : directions) {
                this.setRotation(dir);
                settings.setRotation(this.getRotation());

                var blockInfoList = template.filterBlocks(BlockPos.ZERO, settings, ChangedBlocks.GLU_BLOCK.get());
                Collections.shuffle(blockInfoList, random);
                for (var blockInfo : blockInfoList) {
                    if (!GluBlock.canConnect(exitGlu.state, exitGlu.nbt, blockInfo.state, blockInfo.nbt))
                        continue;

                    var structureOrigin = gluBlockPos.subtract(blockInfo.pos);
                    this.setupBoundingBox(structureOrigin);
                    this.generationPosition = structureOrigin;
                    if (!sanityCheckGluBlock(gluBlockPos))
                        Changed.LOGGER.error("Sanity check failed for facility generation glu block");

                    if (builder.findCollisionPiece(this.boundingBox) == null)
                        return true;
                }
            }

            return false;
        }

        protected boolean sanityCheckGluBlock(BlockPos pos) {
            return pos.getX() == this.boundingBox.minX() || pos.getX() == this.boundingBox.maxX() ||
                    pos.getY() == this.boundingBox.minY() || pos.getY() == this.boundingBox.maxY() ||
                    pos.getZ() == this.boundingBox.minZ() || pos.getZ() == this.boundingBox.maxZ();
        }

        @Override
        public void setupBoundingBox(BlockPos offset) {
            this.boundingBox = template.getBoundingBox(BlockPos.ZERO, this.getRotation(), BlockPos.ZERO, this.getMirror());
            this.boundingBox.move(offset);
            this.generationPosition = new BlockPos(this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ());
        }
    }

    @Override
    public FacilityPieceInstance createStructurePiece(StructureManager structures, int genDepth) {
        return new StructureInstance(structures, genDepth, templateName, lootTable, null);
    }
}
