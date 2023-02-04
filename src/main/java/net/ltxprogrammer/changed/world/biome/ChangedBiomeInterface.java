package net.ltxprogrammer.changed.world.biome;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public interface ChangedBiomeInterface {
    public static final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0F, 1.0F);
    public static final Climate.Parameter[] temperatures = new Climate.Parameter[]{Climate.Parameter.span(-1.0F, -0.45F), Climate.Parameter.span(-0.45F, -0.15F), Climate.Parameter.span(-0.15F, 0.2F), Climate.Parameter.span(0.2F, 0.55F), Climate.Parameter.span(0.55F, 1.0F)};
    public static final Climate.Parameter[] humidities = new Climate.Parameter[]{Climate.Parameter.span(-1.0F, -0.35F), Climate.Parameter.span(-0.35F, -0.1F), Climate.Parameter.span(-0.1F, 0.1F), Climate.Parameter.span(0.1F, 0.3F), Climate.Parameter.span(0.3F, 1.0F)};
    public static final Climate.Parameter[] erosions = new Climate.Parameter[]{Climate.Parameter.span(-1.0F, -0.78F), Climate.Parameter.span(-0.78F, -0.375F), Climate.Parameter.span(-0.375F, -0.2225F), Climate.Parameter.span(-0.2225F, 0.05F), Climate.Parameter.span(0.05F, 0.45F), Climate.Parameter.span(0.45F, 0.55F), Climate.Parameter.span(0.55F, 1.0F)};
    public static final Climate.Parameter FROZEN_RANGE = temperatures[0];
    public static final Climate.Parameter UNFROZEN_RANGE = Climate.Parameter.span(temperatures[1], temperatures[4]);
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

    List<Climate.ParameterPoint> getPoints();
}
