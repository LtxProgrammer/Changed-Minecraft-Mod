package net.ltxprogrammer.changed.world.features.structures;

import com.google.common.base.Stopwatch;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedStructureTypes;
import net.ltxprogrammer.changed.world.features.structures.facility.FacilityKeystone;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Facility extends Structure {
    public static final int GENERATION_CHUNK_RADIUS = 8;

    public static final Codec<Facility> CODEC = simpleCodec(Facility::new);

    public Facility(Structure.StructureSettings settings) {
        super(settings);
    }

    public Optional<Structure.GenerationStub> findGenerationPoint(GenerationContext context) {
        Rotation rotation = Rotation.getRandom(context.random());
        BlockPos blockpos = this.getLowestYIn5by5BoxOffset7Blocks(context, rotation);
        return blockpos.getY() < 60 ? Optional.empty() : Optional.of(new Structure.GenerationStub(blockpos, (builder) -> {
            this.tryGeneratePieces(builder, context, blockpos, rotation);
        }));
    }

    private void tryGeneratePieces(StructurePiecesBuilder builder, GenerationContext context, BlockPos blockPos, Rotation rotation) {
        ChunkPos center = context.chunkPos();
        Changed.LOGGER.info("Started facility generation at ChunkPos {}",
                center);

        Stopwatch stopwatch = Stopwatch.createStarted();

        ChunkPos min = new ChunkPos(center.x - GENERATION_CHUNK_RADIUS, center.z - GENERATION_CHUNK_RADIUS);
        ChunkPos max = new ChunkPos(center.x + GENERATION_CHUNK_RADIUS, center.z + GENERATION_CHUNK_RADIUS);
        BlockPos minPos = new BlockPos(min.getMinBlockX(), context.heightAccessor().getMinBuildHeight(), min.getMinBlockZ());
        BlockPos maxPos = new BlockPos(max.getMaxBlockX(), context.heightAccessor().getMaxBuildHeight(), max.getMaxBlockZ());

        BoundingBox generationRegion = BoundingBox.fromCorners(minPos, maxPos);

        final int rerollForSizeCount = Changed.config.server.facilityRollForSizeAttempts.get();
        final int genDepth = Changed.config.server.facilityGenerateDepth.get();

        List<Integer> sizes = new ArrayList<>(rerollForSizeCount);
        List<StructurePiece> largestSet = List.of();
        FacilityKeystone largestKeystone = null;

        for (int reroll = 0; reroll < rerollForSizeCount; reroll++) {
            builder.clear();

            Optional<FacilityKeystone> keystoneOpt = FacilityPieces.generateFacility(builder, context, genDepth, generationRegion);
            if (keystoneOpt.isEmpty()) continue;
            FacilityKeystone keystone = keystoneOpt.get();

            builder.addPiece(keystone);

            int size = ((StructurePiecesBuilderExtender)builder).pieceCount();
            sizes.add(size);
            if (((StructurePiecesBuilderExtender)builder).pieceCount() > largestSet.size()) {
                largestSet = new ArrayList<>(((StructurePiecesBuilderExtender)builder).getPieces());
                largestKeystone = keystone;
            }
        }

        builder.clear();
        if (largestKeystone == null) {
            Changed.LOGGER.info("Failed generating facility at ChunkPos {}",
                    center);
            return;
        }

        largestSet.forEach(builder::addPiece);

        var duration = stopwatch.elapsed().toMillis();

        Changed.LOGGER.info("Generated facility \"{}\" with {} pieces (best of {}), at ChunkPos {} after {} ms",
                largestKeystone,
                largestSet.size(),
                sizes,
                center,
                duration);
    }

    @Override
    public StructureType<?> type() {
        return ChangedStructureTypes.FACILITY.get();
    }
}
