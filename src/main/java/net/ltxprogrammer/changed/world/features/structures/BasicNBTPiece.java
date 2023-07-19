package net.ltxprogrammer.changed.world.features.structures;

import net.ltxprogrammer.changed.init.ChangedStructurePieceTypes;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.Random;

public class BasicNBTPiece extends ScatteredFeaturePiece {
    private ResourceLocation nbt;
    @Nullable private final ResourceLocation lootTable;
    private final StructureTemplate template;

    private static StructureTemplate findStructureTemplate(ResourceLocation name) {
        return ServerLifecycleHooks.getCurrentServer().getStructureManager().get(name).orElseThrow();
    }

    private BasicNBTPiece(StructureTemplate template, @Nullable ResourceLocation lootTable, PieceGenerator.Context<?> context, int x, int z) {
        super(ChangedStructurePieceTypes.NBT.get(),
                x, context.chunkGenerator().getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor()), z,
                template.getSize().getX(), template.getSize().getY(), template.getSize().getZ(),
                getRandomHorizontalDirection(context.random()));
        this.template = template;
        this.lootTable = lootTable;
    }

    private BasicNBTPiece(StructureTemplate template, @Nullable ResourceLocation lootTable, PieceGenerator.Context<?> context) {
        this(template, lootTable, context,
                context.chunkPos().getMinBlockX() + context.random().nextInt(16),
                context.chunkPos().getMinBlockZ() + context.random().nextInt(16));
    }

    public BasicNBTPiece(ResourceLocation structureNBT, @Nullable ResourceLocation lootTable, PieceGenerator.Context<?> context) {
        this(findStructureTemplate(structureNBT), lootTable, context);
        this.nbt = structureNBT;
    }

    public BasicNBTPiece(CompoundTag tag) {
        super(ChangedStructurePieceTypes.NBT.get(), tag);
        this.nbt = TagUtil.getResourceLocation(tag, "nbt");
        this.template = findStructureTemplate(nbt);
        this.lootTable = tag.contains("lootTable") ? TagUtil.getResourceLocation(tag, "lootTable") : null;
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        TagUtil.putResourceLocation(tag, "nbt", nbt);
        if (lootTable != null)
            TagUtil.putResourceLocation(tag, "lootTable", lootTable);
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureFeatureManager manager, ChunkGenerator chunk, Random random, BoundingBox bb, ChunkPos chunkPos, BlockPos blockPos) {
        var settings = new StructurePlaceSettings()
                .setMirror(this.getMirror())
                .setRotation(this.getRotation()).setRandom(random)
                .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
                .setIgnoreEntities(false);
        if (lootTable != null)
            settings.addProcessor(ChestLootTableProcessor.of(lootTable));
        template.placeInWorld(level, blockPos, blockPos, settings, random, 2);
    }
}
