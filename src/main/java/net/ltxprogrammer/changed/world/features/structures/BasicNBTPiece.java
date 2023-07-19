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
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

public class BasicNBTPiece extends StructurePiece {
    private ResourceLocation nbt;
    @Nullable private final ResourceLocation lootTable;
    private final StructureTemplate template;

    private static StructureTemplate findStructureTemplate(ResourceLocation name) {
        return ServerLifecycleHooks.getCurrentServer().getStructureManager().get(name).orElseThrow();
    }

    private BasicNBTPiece(StructureTemplate template, @Nullable ResourceLocation lootTable, Random random) {
        super(ChangedStructurePieceTypes.NBT.get(), 0, template.getBoundingBox((new StructurePlaceSettings())
                        .setRandom(random)
                        .setRotation(Rotation.getRandom(random))
                        .setMirror(Mirror.values()[random.nextInt(Mirror.values().length)]),
                new BlockPos(0, 0, 0)));
        this.template = template;
        this.lootTable = lootTable;
    }

    public BasicNBTPiece(ResourceLocation structureNBT, @Nullable ResourceLocation lootTable, Random random) {
        this(findStructureTemplate(structureNBT), lootTable, random);
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
        var settings = new StructurePlaceSettings().setMirror(Mirror.values()[random.nextInt(2)])
                .setRotation(Rotation.values()[random.nextInt(3)]).setRandom(random)
                .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
                .setIgnoreEntities(false);
        if (lootTable != null)
            settings.addProcessor(ChestLootTableProcessor.of(lootTable));
        template.placeInWorld(level, blockPos, blockPos, settings, random, 2);
    }
}
