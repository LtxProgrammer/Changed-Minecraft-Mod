package net.ltxprogrammer.changed.world.features.structures.facility;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/**
 * The position of a glu block
 */
public record GenStep(StructureTemplate.StructureBlockInfo blockInfo, WeightedRandomList<WeightedEntry.Wrapper<PieceType>> validTypes) {
}
