package net.ltxprogrammer.changed.world.features.structures;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedStructurePieceTypes;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import java.util.*;

public class FacilityPieces {
    /*
    * Entrance [zone]
    * Staircase [zone, length]
    * Corridor [zone, length]
    * T-Corridor [zone]
    * Right Corridor [zone]
    * Room [zone]
     */

    public enum Zone {
        RED_ZONE,
        MAINTENANCE_ZONE,
        BLUE_ZONE;

        public static Zone random(Random r) {
            return Zone.values()[r.nextInt(Zone.values().length)];
        }
    }

    private record GenStep(BlockPos start, Direction direction, List<StructurePieceType> possible) {}
    private record FacilityGenInfo(BlockPos start, Direction direction, Zone zone) {}

    interface FacilityPiece {
        void addSteps(List<GenStep> starts);
        default @Nullable ResourceLocation getLootTable() { return null; }
    }

    abstract static class FacilitySinglePiece extends StructurePiece implements FacilityPiece {
        public final ResourceLocation templateName;
        public final StructureTemplate template;
        public final Zone zone;

        public static BoundingBox makeBoundingBox(Vec3i start, Direction direction, Vec3i size) {
            return StructurePiece.makeBoundingBox(start.getX(), start.getY(), start.getZ(), direction, size.getX(), size.getY(), size.getZ());
        }

        private FacilitySinglePiece(StructurePieceType type, StructureTemplate template, ResourceLocation templateName, int genDepth, FacilityGenInfo info) {
            super(type, genDepth, makeBoundingBox(info.start, info.direction, template.getSize()));
            this.templateName = templateName;
            this.template = template;
            this.zone = info.zone;
            this.setOrientation(info.direction);
        }

        protected FacilitySinglePiece(StructurePieceType type, StructureManager manager, ResourceLocation templateName, int genDepth, FacilityGenInfo info) {
            this(type, manager.getOrCreate(templateName), templateName, genDepth, info);
        }

        public FacilitySinglePiece(StructurePieceType type, StructureManager manager, CompoundTag tag) {
            super(type, tag);
            this.templateName = TagUtil.getResourceLocation(tag, "template");
            this.template = manager.getOrCreate(templateName);
            this.zone = Zone.valueOf(tag.getString("zone"));
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
            TagUtil.putResourceLocation(tag, "template", templateName);
            tag.putString("zone", zone.toString());
        }

        @Override
        public void postProcess(WorldGenLevel level, StructureFeatureManager manager, ChunkGenerator generator, Random random, BoundingBox bb, ChunkPos pos, BlockPos blockPos) {
            var settings = new StructurePlaceSettings()
                    .setMirror(this.getMirror())
                    .setRotation(this.getRotation()).setRandom(random)
                    .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
                    .setIgnoreEntities(false);
            var lootTable = getLootTable();
            if (lootTable != null)
                settings.addProcessor(ChestLootTableProcessor.of(lootTable));
            template.placeInWorld(level, blockPos, blockPos, settings, random, 2);
        }
    }

    abstract static class FacilitySegmentedPiece extends StructurePiece implements FacilityPiece {
        public final ResourceLocation templateNameA;
        public final StructureTemplate templateA;
        public final ResourceLocation templateNameB;
        public final StructureTemplate templateB;
        public final ResourceLocation templateNameC;
        public final StructureTemplate templateC;
        public final Zone zone;
        public final int segments;
        public final Direction segmentDir;

        public static BoundingBox makeBoundingBox(Vec3i start, Direction direction, Vec3i size) {
            return StructurePiece.makeBoundingBox(start.getX(), start.getY(), start.getZ(), direction, size.getX(), size.getY(), size.getZ());
        }

        private static Vec3i getDirectionalSize(BoundingBox box, Direction dir) {
            var norm = dir.getNormal();
            var sz = box.getLength();
            return new Vec3i(norm.getX() * sz.getX(), norm.getY() * sz.getY(), norm.getZ() * sz.getZ());
        }

        public static BoundingBox makeSegmentedBoundingBox(StructureTemplate templateA, StructureTemplate templateB, StructureTemplate templateC,
                                                           FacilityGenInfo info, Direction segmentDir, int segments) {
            BoundingBox boxA = makeBoundingBox(info.start, info.direction, templateA.getSize());
            BoundingBox boxB = makeBoundingBox(info.start, info.direction, templateB.getSize());
            BoundingBox boxC = makeBoundingBox(info.start, info.direction, templateC.getSize());

            return boxA.encapsulate(boxC.move(
                    getDirectionalSize(boxB, segmentDir).multiply(segments)
                            .offset(getDirectionalSize(boxC, segmentDir))));
        }

        private FacilitySegmentedPiece(StructurePieceType type, StructureTemplate templateA, ResourceLocation templateNameA,
                                       StructureTemplate templateB, ResourceLocation templateNameB,
                                       StructureTemplate templateC, ResourceLocation templateNameC, int genDepth, FacilityGenInfo info, Direction segmentDir, int segments) {
            super(type, genDepth, makeSegmentedBoundingBox(templateA, templateB, templateC, info, segmentDir, segments));
            this.templateNameA = templateNameA;
            this.templateA = templateA;
            this.templateNameB = templateNameB;
            this.templateB = templateB;
            this.templateNameC = templateNameC;
            this.templateC = templateC;
            this.zone = info.zone;
            this.segments = segments;
            this.segmentDir = segmentDir;
            this.setOrientation(info.direction);
        }

        protected FacilitySegmentedPiece(StructurePieceType type, StructureManager manager,
                                         ResourceLocation templateNameA,
                                         ResourceLocation templateNameB,
                                         ResourceLocation templateNameC, int genDepth, FacilityGenInfo info, Direction segmentDir, int segments) {
            this(type, manager.getOrCreate(templateNameA), templateNameA,
                    manager.getOrCreate(templateNameB), templateNameB,
                    manager.getOrCreate(templateNameC), templateNameC, genDepth, info, segmentDir, segments);
        }

        public FacilitySegmentedPiece(StructurePieceType type, StructureManager manager, CompoundTag tag) {
            super(type, tag);
            this.templateNameA = TagUtil.getResourceLocation(tag, "templateA");
            this.templateA = manager.getOrCreate(templateNameA);
            this.templateNameB = TagUtil.getResourceLocation(tag, "templateB");
            this.templateB = manager.getOrCreate(templateNameB);
            this.templateNameC = TagUtil.getResourceLocation(tag, "templateC");
            this.templateC = manager.getOrCreate(templateNameC);
            this.zone = Zone.valueOf(tag.getString("zone"));
            this.segments = tag.getInt("segments");
            this.segmentDir = Direction.valueOf(tag.getString("segmentDir"));
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
            TagUtil.putResourceLocation(tag, "templateA", templateNameA);
            TagUtil.putResourceLocation(tag, "templateB", templateNameB);
            TagUtil.putResourceLocation(tag, "templateC", templateNameC);
            tag.putString("zone", zone.toString());
            tag.putInt("segments", segments);
            tag.putString("segmentDir", segmentDir.toString());
        }

        @Override
        public void postProcess(WorldGenLevel level, StructureFeatureManager manager, ChunkGenerator generator, Random random, BoundingBox bb, ChunkPos pos, BlockPos blockPos) {
            var settings = new StructurePlaceSettings()
                    .setMirror(this.getMirror())
                    .setRotation(this.getRotation()).setRandom(random)
                    .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
                    .setIgnoreEntities(false);
            var lootTable = getLootTable();
            if (lootTable != null)
                settings.addProcessor(ChestLootTableProcessor.of(lootTable));
            templateA.placeInWorld(level, blockPos, blockPos, settings, random, 2);
        }
    }

    public static class FacilityStaircase extends FacilitySegmentedPiece {
        private static ResourceLocation getTemplateNameA(Zone zone) {
            return switch (zone) {
                case RED_ZONE -> Changed.modResource("facility/staircase_red_top");
                case BLUE_ZONE -> Changed.modResource("facility/staircase_blue_top");
                case MAINTENANCE_ZONE -> Changed.modResource("facility/staircase_maintenance_top");
            };
        }

        private static ResourceLocation getTemplateNameB(Zone zone) {
            return switch (zone) {
                case RED_ZONE -> Changed.modResource("facility/staircase_red_middle");
                case BLUE_ZONE -> Changed.modResource("facility/staircase_blue_middle");
                case MAINTENANCE_ZONE -> Changed.modResource("facility/staircase_maintenance_middle");
            };
        }

        private static ResourceLocation getTemplateNameC(Zone zone) {
            return switch (zone) {
                case RED_ZONE -> Changed.modResource("facility/staircase_red_bottom");
                case BLUE_ZONE -> Changed.modResource("facility/staircase_blue_bottom");
                case MAINTENANCE_ZONE -> Changed.modResource("facility/staircase_maintenance_bottom");
            };
        }

        protected FacilityStaircase(StructureManager manager, int genDepth, PieceGenerator.Context<NoneFeatureConfiguration> context, FacilityGenInfo info) {
            super(ChangedStructurePieceTypes.FACILITY_STAIRCASE.get(), manager,
                    getTemplateNameA(info.zone), getTemplateNameB(info.zone), getTemplateNameC(info.zone), genDepth, info, Direction.DOWN,
                    context.random().nextInt(2, 5));
        }

        public FacilityStaircase(StructureManager manager, CompoundTag tag) {
            super(ChangedStructurePieceTypes.FACILITY_STAIRCASE.get(), manager, tag);
        }

        @Override
        public void postProcess(WorldGenLevel level, StructureFeatureManager manager, ChunkGenerator generator, Random random, BoundingBox bb, ChunkPos pos, BlockPos blockPos) {

        }

        @Override
        public void addSteps(List<GenStep> starts) {

        }
    }

    public static class FacilityEntrance extends FacilitySinglePiece {
        private static ResourceLocation getTemplateName(Zone zone) {
            return switch (zone) {
                case RED_ZONE -> Changed.modResource("facility/entrance_red");
                case BLUE_ZONE -> Changed.modResource("facility/entrance_blue");
                case MAINTENANCE_ZONE -> Changed.modResource("facility/entrance_maintenance");
            };
        }

        protected FacilityEntrance(StructureManager manager, int genDepth, FacilityGenInfo info) {
            super(ChangedStructurePieceTypes.FACILITY_ENTRANCE.get(), manager, getTemplateName(info.zone), genDepth, info);
        }

        public FacilityEntrance(StructureManager manager, CompoundTag tag) {
            super(ChangedStructurePieceTypes.FACILITY_ENTRANCE.get(), manager, tag);
        }

        @Nullable
        @Override
        public ResourceLocation getLootTable() {
            return LootTables.LOW_TIER_LAB;
        }

        @Override
        public void addSteps(List<GenStep> starts) {
            //starts.add(new GenStep())
        }
    }

    public static void generateFacility(StructurePiecesBuilder builder, PieceGenerator.Context<NoneFeatureConfiguration> context, int genDepth, int span) {
        BlockPos blockPos = new BlockPos(
                context.chunkPos().getBlockX(context.random().nextInt(16)), 0,
                context.chunkPos().getBlockZ(context.random().nextInt(16)));
        blockPos = blockPos.atY(context.chunkGenerator().getBaseHeight(blockPos.getX() + 6, blockPos.getZ() + 6,
                Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor()));
        var entrance = new FacilityEntrance(context.structureManager(), genDepth, new FacilityGenInfo(blockPos, Direction.getRandom(context.random()), Zone.random(context.random())));
        builder.addPiece(entrance);

        BoundingBox used = entrance.getBoundingBox();
        List<GenStep> starts = new ArrayList<>();
        entrance.addSteps(starts);

        while (span > 0) {
            starts.forEach(step -> {

            });

            span--;
        }
    }
}
