package net.ltxprogrammer.changed.world.features.structures;

import net.ltxprogrammer.changed.init.ChangedStructurePieceTypes;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.BiConsumer;

public class SurfaceNBTPiece extends StructurePiece {
    private final ResourceLocation templateName;
    @Nullable private final ResourceLocation lootTable;
    private final StructureTemplate template;
    private final BlockPos generationPosition;

    private SurfaceNBTPiece(StructureTemplate template, ResourceLocation structureNBT, @Nullable ResourceLocation lootTable, Structure.GenerationContext context, int x, int z) {
        super(ChangedStructurePieceTypes.NBT.get(), 0, BoundingBox.infinite());
        this.setOrientation(Direction.Plane.HORIZONTAL.getRandomDirection(context.random()));
        var settings = new StructurePlaceSettings()
                .setMirror(this.getMirror())
                .setRotation(this.getRotation())
                .setIgnoreEntities(false);
        var tmpGenPos = StructureTemplate.calculateRelativePosition(settings, new BlockPos(0, 0, 0)).offset(
                x, 0, z
        );
        this.boundingBox = template.getBoundingBox(settings, tmpGenPos);
        int minXminZ = context.chunkGenerator().getBaseHeight(this.boundingBox.minX(), this.boundingBox.minZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        int minXmaxZ = context.chunkGenerator().getBaseHeight(this.boundingBox.minX(), this.boundingBox.maxZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        int maxXminZ = context.chunkGenerator().getBaseHeight(this.boundingBox.maxX(), this.boundingBox.minZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        int maxXmaxZ = context.chunkGenerator().getBaseHeight(this.boundingBox.maxX(), this.boundingBox.maxZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());

        this.generationPosition = tmpGenPos.offset(0, Math.min(Math.min(minXminZ, minXmaxZ), Math.min(maxXminZ, maxXmaxZ)), 0);
        this.boundingBox = this.boundingBox.moved(0, this.generationPosition.getY(), 0);
        this.templateName = structureNBT;
        this.template = template;
        this.lootTable = lootTable;
    }

    private SurfaceNBTPiece(StructureTemplate template, ResourceLocation structureNBT, @Nullable ResourceLocation lootTable, Structure.GenerationContext context) {
        this(template, structureNBT, lootTable, context,
                context.chunkPos().getMinBlockX() + context.random().nextInt(16),
                context.chunkPos().getMinBlockZ() + context.random().nextInt(16));
    }

    public SurfaceNBTPiece(ResourceLocation structureNBT, @Nullable ResourceLocation lootTable, Structure.GenerationContext context) {
        this(context.structureTemplateManager().getOrCreate(structureNBT), structureNBT, lootTable, context);
    }

    public SurfaceNBTPiece(StructureTemplateManager manager, CompoundTag tag) {
        super(ChangedStructurePieceTypes.NBT.get(), tag);
        this.templateName = TagUtil.getResourceLocation(tag, "nbt");
        this.template = manager.get(templateName).orElseThrow(() -> new IllegalArgumentException("Cannot read template " + templateName));
        this.lootTable = tag.contains("lootTable") ? TagUtil.getResourceLocation(tag, "lootTable") : null;
        if (tag.contains("genPos"))
            this.generationPosition = TagUtil.getBlockPos(tag, "genPos");
        else {
            this.generationPosition = new BlockPos(this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ());
        }
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        TagUtil.putBlockPos(tag, "genPos", generationPosition);
        TagUtil.putResourceLocation(tag, "nbt", templateName);
        if (lootTable != null)
            TagUtil.putResourceLocation(tag, "lootTable", lootTable);
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager manager, ChunkGenerator chunk, RandomSource random, BoundingBox bb, ChunkPos chunkPos, BlockPos blockPos) {
        var settings = new StructurePlaceSettings()
                .setMirror(this.getMirror())
                .setRotation(this.getRotation()).setRandom(random)
                .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
                .addProcessor(HangingBlockFixerProcessor.INSTANCE)
                .setBoundingBox(bb)
                .setKeepLiquids(false)
                .setKnownShape(true)
                .setIgnoreEntities(false);
        if (lootTable != null)
            settings.addProcessor(ChestLootTableProcessor.of(lootTable));

        try {
            template.placeInWorld(level, generationPosition, generationPosition, settings, random, 2);
        } catch (Exception e) {
            CrashReport report = CrashReport.forThrowable(e, "Placing NBT Piece");
            CrashReportCategory category = report.addCategory("Structure piece being placed");
            category.setDetail("Generation Position", generationPosition);
            category.setDetail("Template Name", templateName);
            category.setDetail("Loot Table", lootTable);
            throw new ReportedException(report);
        }

        BoundingBox intersect = new BoundingBox(
                Math.max(this.boundingBox.minX(), bb.minX()),
                Math.max(this.boundingBox.minY(), bb.minY()),
                Math.max(this.boundingBox.minZ(), bb.minZ()),
                Math.min(this.boundingBox.maxX(), bb.maxX()),
                Math.min(this.boundingBox.maxY(), bb.maxY()),
                Math.min(this.boundingBox.maxZ(), bb.maxZ())
        );

        final BiConsumer<BlockPos, Direction> withNormal = (processPos, normal) -> {
            BlockState state = level.getBlockState(processPos);
            BlockPos relativePos = processPos.relative(normal);
            BlockState relativeState = level.getBlockState(relativePos);
            BlockState nState = state.updateShape(normal, relativeState, level, processPos, relativePos);
            BlockState nRelativeState = relativeState.updateShape(normal.getOpposite(), nState, level, relativePos, processPos);
            if (nState != state)
                level.setBlock(processPos, nState, 2);
            if (nRelativeState != relativeState)
                level.setBlock(relativePos, nRelativeState, 2);
        };

        BlockPos.betweenClosedStream(intersect).forEach(processPos -> {
            if (processPos.getX() == boundingBox.minX())
                withNormal.accept(processPos, Direction.WEST);
            if (processPos.getX() == boundingBox.maxX())
                withNormal.accept(processPos, Direction.EAST);
            if (processPos.getY() == boundingBox.minY())
                withNormal.accept(processPos, Direction.DOWN);
            if (processPos.getY() == boundingBox.maxY())
                withNormal.accept(processPos, Direction.UP);
            if (processPos.getZ() == boundingBox.minZ())
                withNormal.accept(processPos, Direction.NORTH);
            if (processPos.getZ() == boundingBox.maxZ())
                withNormal.accept(processPos, Direction.SOUTH);
        });
    }
}
