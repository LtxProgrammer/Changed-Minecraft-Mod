package net.ltxprogrammer.changed.world.features.structures;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedFeatures;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;

public class GluReplacementProcessor extends StructureProcessor {
    public static final Codec<GluReplacementProcessor> CODEC = Codec.unit(() -> {
        return GluReplacementProcessor.INSTANCE;
    });
    public static final GluReplacementProcessor INSTANCE = new GluReplacementProcessor();

    private GluReplacementProcessor() {
    }

    @Nullable
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader p_74127_, BlockPos p_74128_, BlockPos p_74129_, StructureTemplate.StructureBlockInfo p_74130_, StructureTemplate.StructureBlockInfo blockInfo, StructurePlaceSettings p_74132_) {
        BlockState blockstate = blockInfo.state;
        if (blockstate.is(ChangedBlocks.GLU_BLOCK.get())) {
            // TODO
            /*String s = blockInfo.nbt.getString("final_state");
            BlockStateParser blockstateparser = new BlockStateParser(new StringReader(s), false);

            try {
                blockstateparser.parse(true);
            } catch (CommandSyntaxException commandsyntaxexception) {
                throw new RuntimeException(commandsyntaxexception);
            }

            return blockstateparser.getState().is(Blocks.STRUCTURE_VOID) ? null : new StructureTemplate.StructureBlockInfo(blockInfo.pos, blockstateparser.getState(), (CompoundTag)null);*/
            return null;
        } else {
            return blockInfo;
        }
    }

    protected StructureProcessorType<?> getType() {
        return ChangedFeatures.GLU_REPLACEMENT_PROCESSOR;
    }
}
