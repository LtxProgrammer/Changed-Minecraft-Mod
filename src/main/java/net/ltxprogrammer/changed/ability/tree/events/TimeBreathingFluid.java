package net.ltxprogrammer.changed.ability.tree.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;

public class TimeBreathingFluid extends AbstractPointEvent<TimeBreathingFluid.Criteria> {
    public static final Codec<TimeBreathingFluid> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("reward").forGetter(event -> event.reward),
            Codec.INT.fieldOf("ticks").forGetter(event -> event.divisor),
            RegistryElementPredicate.codec(ForgeRegistries.FLUID_TYPES.get()).fieldOf("fluid")
                    .orElse(RegistryElementPredicate.forID(ForgeRegistries.FLUID_TYPES.get(), ForgeMod.WATER_TYPE.getId())).forGetter(event -> event.fluidTypePredicate)
    ).apply(instance, TimeBreathingFluid::new));

    @Override
    public Codec<? extends AbstractPointEvent<?>> getCodec() {
        return CODEC;
    }

    public final int divisor;
    public final RegistryElementPredicate<FluidType> fluidTypePredicate;

    public TimeBreathingFluid(int reward, int divisor, RegistryElementPredicate<FluidType> fluidTypePredicate) {
        super(reward);
        this.divisor = divisor;
        this.fluidTypePredicate = fluidTypePredicate;
    }

    @Override
    public boolean test(Criteria criteria) {
        if (!fluidTypePredicate.test(criteria.fluidType))
            return false;

        int mttl = criteria.totalValue() % divisor;
        int mdttl = (criteria.totalValue() + criteria.delta()) % divisor;

        return mdttl < mttl;
    }

    public record Criteria(int totalValue, int delta, FluidType fluidType) {}
}
