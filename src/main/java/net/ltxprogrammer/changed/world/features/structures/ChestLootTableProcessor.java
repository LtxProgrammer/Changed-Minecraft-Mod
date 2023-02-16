package net.ltxprogrammer.changed.world.features.structures;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedFeatures;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import javax.annotation.Nullable;

public class ChestLootTableProcessor extends StructureProcessor {
    public static final Codec<ChestLootTableProcessor> CODEC = ResourceLocation.CODEC.fieldOf("blocks").xmap(ChestLootTableProcessor::new, (processor) -> processor.table).codec();

    private final ResourceLocation table;

    ChestLootTableProcessor(ResourceLocation table) {
        this.table = table;
    }

    public static StructureProcessor of(ResourceLocation lootTable) {
        return new ChestLootTableProcessor(lootTable);
    }

    @Nullable
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos blockPos, BlockPos p_74057_, StructureTemplate.StructureBlockInfo p_74058_, StructureTemplate.StructureBlockInfo info, StructurePlaceSettings settings) {
        if (info.state.getBlock() instanceof ChestBlock) {
            TagUtil.putResourceLocation(info.nbt, "LootTable", table);
            info.nbt.putLong("LootTableSeed", settings.getRandom(info.pos).nextLong());
        }
        return info;
    }

    protected StructureProcessorType<?> getType() {
        return ChangedFeatures.CHEST_LOOT_TABLE_PROCESSOR;
    }
}