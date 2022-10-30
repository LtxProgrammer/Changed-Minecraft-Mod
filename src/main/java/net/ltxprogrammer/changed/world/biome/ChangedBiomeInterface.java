package net.ltxprogrammer.changed.world.biome;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.state.BlockState;

public interface ChangedBiomeInterface {
    Biome build();
    BlockState groundBlock();
    BlockState undergroundBlock();
    BlockState underwaterBlock();
    BlockState waterBlock();
    Climate.ParameterPoint climate();
}
