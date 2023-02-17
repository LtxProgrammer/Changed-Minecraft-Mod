package net.ltxprogrammer.changed.world.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.List;

public interface ChangedBiomeInterface {
    Biome build();
    BlockState groundBlock();
    BlockState undergroundBlock();
    BlockState underwaterBlock();
    BlockState waterBlock();

    List<Climate.ParameterPoint> getPoints();

    public static enum Weirdness {
        MID_SLICE_NORMAL_ASCENDING(Climate.Parameter.span(-1.0F, -0.93333334F)),
        HIGH_SLICE_NORMAL_ASCENDING(Climate.Parameter.span(-0.93333334F, -0.7666667F)),
        PEAK_NORMAL(Climate.Parameter.span(-0.7666667F, -0.56666666F)),
        HIGH_SLICE_NORMAL_DESCENDING(Climate.Parameter.span(-0.56666666F, -0.4F)),
        MID_SLICE_NORMAL_DESCENDING(Climate.Parameter.span(-0.4F, -0.26666668F)),
        LOW_SLICE_NORMAL_DESCENDING(Climate.Parameter.span(-0.26666668F, -0.05F)),
        VALLEY(Climate.Parameter.span(-0.05F, 0.05F)),
        LOW_SLICE_VARIANT_ASCENDING(Climate.Parameter.span(0.05F, 0.26666668F)),
        MID_SLICE_VARIANT_ASCENDING(Climate.Parameter.span(0.26666668F, 0.4F)),
        HIGH_SLICE_VARIANT_ASCENDING(Climate.Parameter.span(0.4F, 0.56666666F)),
        PEAK_VARIANT(Climate.Parameter.span(0.56666666F, 0.7666667F)),
        HIGH_SLICE_VARIANT_DESCENDING(Climate.Parameter.span(0.7666667F, 0.93333334F)),
        MID_SLICE_VARIANT_DESCENDING(Climate.Parameter.span(0.93333334F, 1.0F)),
        FULL_RANGE(Climate.Parameter.span(-1.0F, 1.0F));

        private final Climate.Parameter parameter;

        private Weirdness(Climate.Parameter parameter) {
            this.parameter = parameter;
        }

        public Climate.Parameter parameter() {
            return this.parameter;
        }

        public static Climate.Parameter span(Weirdness min, Weirdness max) {
            return Climate.Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
        }
    }

    public static enum Depth {
        SURFACE(Climate.Parameter.point(0.0F)),
        UNDERGROUND(Climate.Parameter.span(0.2F, 0.9F)),
        FLOOR(Climate.Parameter.point(1.0F)),
        FULL_RANGE(Climate.Parameter.span(-1.0F, 1.0F));

        private final Climate.Parameter parameter;

        private Depth(Climate.Parameter parameter) {
            this.parameter = parameter;
        }

        public Climate.Parameter parameter() {
            return this.parameter;
        }

        public static Climate.Parameter span(Depth min, Depth max) {
            return Climate.Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
        }
    }

    public static enum Erosion {
        EROSION_0(Climate.Parameter.span(-1.0F, -0.78F)),
        EROSION_1(Climate.Parameter.span(-0.78F, -0.375F)),
        EROSION_2(Climate.Parameter.span(-0.375F, -0.2225F)),
        EROSION_3(Climate.Parameter.span(-0.2225F, 0.05F)),
        EROSION_4(Climate.Parameter.span(0.05F, 0.45F)),
        EROSION_5(Climate.Parameter.span(0.45F, 0.55F)),
        EROSION_6(Climate.Parameter.span(0.55F, 1.0F)),
        FULL_RANGE(Climate.Parameter.span(-1.0F, 1.0F));

        private final Climate.Parameter parameter;

        private Erosion(Climate.Parameter parameter) {
            this.parameter = parameter;
        }

        public Climate.Parameter parameter() {
            return this.parameter;
        }

        public static Climate.Parameter span(Erosion min, Erosion max) {
            return Climate.Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
        }
    }

    public static enum Continentalness {
        MUSHROOM_FIELDS(Climate.Parameter.span(-1.2F, -1.05F)),
        DEEP_OCEAN(Climate.Parameter.span(-1.05F, -0.455F)),
        OCEAN(Climate.Parameter.span(-0.455F, -0.19F)),
        COAST(Climate.Parameter.span(-0.19F, -0.11F)),
        NEAR_INLAND(Climate.Parameter.span(-0.11F, 0.03F)),
        MID_INLAND(Climate.Parameter.span(0.03F, 0.3F)),
        FAR_INLAND(Climate.Parameter.span(0.3F, 1.0F)),
        INLAND(Climate.Parameter.span(-0.11F, 0.55F)),
        FULL_RANGE(Climate.Parameter.span(-1.0F, 1.0F));

        private final Climate.Parameter parameter;

        private Continentalness(Climate.Parameter parameter) {
            this.parameter = parameter;
        }

        public Climate.Parameter parameter() {
            return this.parameter;
        }

        public static Climate.Parameter span(Continentalness min, Continentalness max) {
            return Climate.Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
        }
    }

    public static enum Humidity {
        ARID(Climate.Parameter.span(-1.0F, -0.35F)),
        DRY(Climate.Parameter.span(-0.35F, -0.1F)),
        NEUTRAL(Climate.Parameter.span(-0.1F, 0.1F)),
        WET(Climate.Parameter.span(0.1F, 0.3F)),
        HUMID(Climate.Parameter.span(0.3F, 1.0F)),
        FULL_RANGE(Climate.Parameter.span(-1.0F, 1.0F));

        private final Climate.Parameter parameter;

        private Humidity(Climate.Parameter parameter) {
            this.parameter = parameter;
        }

        public Climate.Parameter parameter() {
            return this.parameter;
        }

        public static Climate.Parameter span(Humidity min, Humidity max) {
            return Climate.Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
        }
    }

    public static enum Temperature {
        ICY(Climate.Parameter.span(-1.0F, -0.45F)),
        COOL(Climate.Parameter.span(-0.45F, -0.15F)),
        NEUTRAL(Climate.Parameter.span(-0.15F, 0.2F)),
        WARM(Climate.Parameter.span(0.2F, 0.55F)),
        HOT(Climate.Parameter.span(0.55F, 1.0F)),
        FROZEN(Climate.Parameter.span(-1.0F, -0.45F)),
        UNFROZEN(Climate.Parameter.span(-0.45F, 1.0F)),
        FULL_RANGE(Climate.Parameter.span(-1.0F, 1.0F));

        private final Climate.Parameter parameter;

        private Temperature(Climate.Parameter parameter) {
            this.parameter = parameter;
        }

        public Climate.Parameter parameter() {
            return this.parameter;
        }

        public static Climate.Parameter span(Temperature min, Temperature max) {
            return Climate.Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
        }
    }
    
    public static class ParameterPointsBuilder {
        private List<Climate.Parameter> temperatures = Lists.newArrayList();
        private List<Climate.Parameter> humidities = Lists.newArrayList();
        private List<Climate.Parameter> continentalnesses = Lists.newArrayList();
        private List<Climate.Parameter> erosions = Lists.newArrayList();
        private List<Climate.Parameter> depths = Lists.newArrayList();
        private List<Climate.Parameter> weirdnesses = Lists.newArrayList();
        private List<Long> offsets = Lists.newArrayList();

        public ParameterPointsBuilder() {
        }

        public ParameterPointsBuilder temperature(Climate.Parameter... values) {
            this.temperatures.addAll(Arrays.asList(values));
            return this;
        }

        public ParameterPointsBuilder temperature(Temperature... values) {
            this.temperatures.addAll(Arrays.asList(values).stream().map(Temperature::parameter).toList());
            return this;
        }

        public ParameterPointsBuilder humidity(Climate.Parameter... values) {
            this.humidities.addAll(Arrays.asList(values));
            return this;
        }

        public ParameterPointsBuilder humidity(Humidity... values) {
            this.humidities.addAll(Arrays.asList(values).stream().map(Humidity::parameter).toList());
            return this;
        }

        public ParameterPointsBuilder continentalness(Climate.Parameter... values) {
            this.continentalnesses.addAll(Arrays.asList(values));
            return this;
        }

        public ParameterPointsBuilder continentalness(Continentalness... values) {
            this.continentalnesses.addAll(Arrays.asList(values).stream().map(Continentalness::parameter).toList());
            return this;
        }

        public ParameterPointsBuilder erosion(Climate.Parameter... values) {
            this.erosions.addAll(Arrays.asList(values));
            return this;
        }

        public ParameterPointsBuilder erosion(Erosion... values) {
            this.erosions.addAll(Arrays.asList(values).stream().map(Erosion::parameter).toList());
            return this;
        }

        public ParameterPointsBuilder depth(Climate.Parameter... values) {
            this.depths.addAll(Arrays.asList(values));
            return this;
        }

        public ParameterPointsBuilder depth(Depth... values) {
            this.depths.addAll(Arrays.asList(values).stream().map(Depth::parameter).toList());
            return this;
        }

        public ParameterPointsBuilder weirdness(Climate.Parameter... values) {
            this.weirdnesses.addAll(Arrays.asList(values));
            return this;
        }

        public ParameterPointsBuilder weirdness(Weirdness... values) {
            this.weirdnesses.addAll(Arrays.asList(values).stream().map(Weirdness::parameter).toList());
            return this;
        }

        public ParameterPointsBuilder offset(Float... values) {
            this.offsets.addAll(Arrays.asList(values).stream().map(Climate::quantizeCoord).toList());
            return this;
        }

        public ParameterPointsBuilder offset(Long... values) {
            this.offsets.addAll(Arrays.asList(values));
            return this;
        }

        public List<Climate.ParameterPoint> build() {
            this.populateIfEmpty();
            ImmutableList.Builder<Climate.ParameterPoint> builder = new ImmutableList.Builder();
            this.temperatures.forEach((temperature) -> {
                this.humidities.forEach((humidity) -> {
                    this.continentalnesses.forEach((continentalness) -> {
                        this.erosions.forEach((erosion) -> {
                            this.depths.forEach((depth) -> {
                                this.weirdnesses.forEach((weirdness) -> {
                                    this.offsets.forEach((offset) -> {
                                        builder.add(new Climate.ParameterPoint(temperature, humidity, continentalness, erosion, depth, weirdness, offset));
                                    });
                                });
                            });
                        });
                    });
                });
            });
            return builder.build();
        }

        private void populateIfEmpty() {
            if (this.temperatures.isEmpty()) {
                this.temperatures.add(Temperature.FULL_RANGE.parameter());
            }

            if (this.humidities.isEmpty()) {
                this.humidities.add(Humidity.FULL_RANGE.parameter());
            }

            if (this.continentalnesses.isEmpty()) {
                this.continentalnesses.add(Continentalness.FULL_RANGE.parameter());
            }

            if (this.erosions.isEmpty()) {
                this.erosions.add(Erosion.FULL_RANGE.parameter());
            }

            if (this.depths.isEmpty()) {
                this.depths.add(Depth.FULL_RANGE.parameter());
            }

            if (this.weirdnesses.isEmpty()) {
                this.weirdnesses.add(Weirdness.FULL_RANGE.parameter());
            }

            if (this.offsets.isEmpty()) {
                this.offsets.add(Climate.quantizeCoord(0.0F));
            }

        }
    }
}
