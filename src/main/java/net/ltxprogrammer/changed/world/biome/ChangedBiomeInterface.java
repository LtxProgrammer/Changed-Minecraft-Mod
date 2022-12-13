package net.ltxprogrammer.changed.world.biome;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.state.BlockState;

public interface ChangedBiomeInterface {
    public static final Climate.Parameter mushroomFieldsContinentalness = Climate.Parameter.span(-1.2F, -1.05F);
    public static final Climate.Parameter deepOceanContinentalness = Climate.Parameter.span(-1.05F, -0.455F);
    public static final Climate.Parameter oceanContinentalness = Climate.Parameter.span(-0.455F, -0.19F);
    public static final Climate.Parameter coastContinentalness = Climate.Parameter.span(-0.19F, -0.11F);
    public static final Climate.Parameter inlandContinentalness = Climate.Parameter.span(-0.11F, 0.55F);
    public static final Climate.Parameter nearInlandContinentalness = Climate.Parameter.span(-0.11F, 0.03F);
    public static final Climate.Parameter midInlandContinentalness = Climate.Parameter.span(0.03F, 0.3F);
    public static final Climate.Parameter farInlandContinentalness = Climate.Parameter.span(0.3F, 1.0F);

    Biome build();
    BlockState groundBlock();
    BlockState undergroundBlock();
    BlockState underwaterBlock();
    BlockState waterBlock();
    Climate.ParameterPoint climate();
}
