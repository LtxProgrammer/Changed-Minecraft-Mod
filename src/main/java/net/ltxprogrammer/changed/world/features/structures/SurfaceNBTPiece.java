package net.ltxprogrammer.changed.world.features.structures;

import net.ltxprogrammer.changed.init.ChangedStructurePieceTypes;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import java.util.Random;

public class SurfaceNBTPiece extends StructurePiece {
    private final ResourceLocation templateName;
    @Nullable private final ResourceLocation lootTable;
    private final StructureTemplate template;
    private final BlockPos generationPosition;

    private SurfaceNBTPiece(StructureTemplate template, ResourceLocation structureNBT, @Nullable ResourceLocation lootTable, PieceGenerator.Context<?> context, int x, int z) {
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
        int minXminZ = context.chunkGenerator().getBaseHeight(this.boundingBox.minX(), this.boundingBox.minZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor());
        int minXmaxZ = context.chunkGenerator().getBaseHeight(this.boundingBox.minX(), this.boundingBox.maxZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor());
        int maxXminZ = context.chunkGenerator().getBaseHeight(this.boundingBox.maxX(), this.boundingBox.minZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor());
        int maxXmaxZ = context.chunkGenerator().getBaseHeight(this.boundingBox.maxX(), this.boundingBox.maxZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor());

        this.generationPosition = tmpGenPos.offset(0, Math.min(Math.min(minXminZ, minXmaxZ), Math.min(maxXminZ, maxXmaxZ)), 0);
        this.boundingBox = this.boundingBox.moved(0, this.generationPosition.getY(), 0);
        this.templateName = structureNBT;
        this.template = template;
        this.lootTable = lootTable;
    }

    private SurfaceNBTPiece(StructureTemplate template, ResourceLocation structureNBT, @Nullable ResourceLocation lootTable, PieceGenerator.Context<?> context) {
        this(template, structureNBT, lootTable, context,
                context.chunkPos().getMinBlockX() + context.random().nextInt(16),
                context.chunkPos().getMinBlockZ() + context.random().nextInt(16));
    }

    public SurfaceNBTPiece(ResourceLocation structureNBT, @Nullable ResourceLocation lootTable, PieceGenerator.Context<?> context) {
        this(context.structureManager().getOrCreate(structureNBT), structureNBT, lootTable, context);
    }

    public SurfaceNBTPiece(StructureManager manager, CompoundTag tag) {
        super(ChangedStructurePieceTypes.NBT.get(), tag);
        this.templateName = TagUtil.getResourceLocation(tag, "nbt");
        this.template = manager.get(templateName).orElseThrow();
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
    public void postProcess(WorldGenLevel level, StructureFeatureManager manager, ChunkGenerator chunk, Random random, BoundingBox bb, ChunkPos chunkPos, BlockPos blockPos) {
        var settings = new StructurePlaceSettings()
                .setMirror(this.getMirror())
                .setRotation(this.getRotation()).setRandom(random)
                .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
                .setBoundingBox(bb)
                .setKeepLiquids(false)
                .setKnownShape(true)
                .setIgnoreEntities(false);
        if (lootTable != null)
            settings.addProcessor(ChestLootTableProcessor.of(lootTable));
        template.placeInWorld(level, generationPosition, generationPosition, settings, random, 2);
    }
}
