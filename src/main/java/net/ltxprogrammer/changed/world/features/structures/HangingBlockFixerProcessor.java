package net.ltxprogrammer.changed.world.features.structures;

import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.init.ChangedFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Intended to locally fix MC-102223
 * Placement processors may opt in to this fix with {@link #INSTANCE HangingBlockFixerProcessor.INSTANCE}.
 * Implementation in {@link net.ltxprogrammer.changed.mixin.StructureTemplateMixin}
 */
public class HangingBlockFixerProcessor extends StructureProcessor {
    public static final Codec<HangingBlockFixerProcessor> CODEC = Codec.unit(() -> {
        return HangingBlockFixerProcessor.INSTANCE;
    });
    public static final HangingBlockFixerProcessor INSTANCE = new HangingBlockFixerProcessor();

    private HangingBlockFixerProcessor() {
    }

    protected StructureProcessorType<?> getType() {
        return ChangedFeatures.HANGING_BLOCK_FIXER_PROCESSOR.get();
    }

    @Override
    public StructureTemplate.StructureEntityInfo processEntity(LevelReader world, BlockPos seedPos, StructureTemplate.StructureEntityInfo rawEntityInfo, StructureTemplate.StructureEntityInfo entityInfo, StructurePlaceSettings placementSettings, StructureTemplate template) {
        var info = super.processEntity(world, seedPos, rawEntityInfo, entityInfo, placementSettings, template);

        if (info.nbt.contains("TileX") && info.nbt.contains("TileY") && info.nbt.contains("TileZ")) {
            info.nbt.putInt("TileX", info.blockPos.getX());
            info.nbt.putInt("TileY", info.blockPos.getY());
            info.nbt.putInt("TileZ", info.blockPos.getZ());
        }

        return info;
    }
}
