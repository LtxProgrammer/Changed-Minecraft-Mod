package net.ltxprogrammer.changed.ability.tree.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;

public class TimeInFluid extends AbstractPointEvent<TimeInFluid.Criteria> {
    public static final Codec<TimeInFluid> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("reward").forGetter(event -> event.reward),
            Codec.INT.fieldOf("ticks").forGetter(event -> event.divisor),
            RegistryElementPredicate.codecElementOrTag(ForgeRegistries.FLUID_TYPES.get()).fieldOf("fluid")
                    .orElse(RegistryElementPredicate.forID(ForgeRegistries.FLUID_TYPES.get(), ForgeMod.WATER_TYPE.getId())).forGetter(event -> event.fluidTypePredicate),
            Codec.BOOL.fieldOf("submerged").forGetter(event -> event.submerged)
    ).apply(instance, TimeInFluid::new));

    @Override
    public Codec<? extends AbstractPointEvent<?>> getCodec() {
        return CODEC;
    }

    public final int divisor;
    public final RegistryElementPredicate<FluidType> fluidTypePredicate;
    public final boolean submerged;

    public TimeInFluid(int reward, int divisor, RegistryElementPredicate<FluidType> fluidTypePredicate, boolean submerged) {
        super(reward);
        this.divisor = divisor;
        this.fluidTypePredicate = fluidTypePredicate;
        this.submerged = submerged;
    }

    @Override
    public boolean test(Criteria criteria) {
        if (submerged != criteria.submerged)
            return false;
        if (!fluidTypePredicate.test(criteria.fluidType))
            return false;

        int mttl = criteria.totalValue() % divisor;
        int mdttl = (criteria.totalValue() + criteria.delta()) % divisor;

        return mdttl < mttl;
    }

    public record Criteria(int totalValue, int delta, FluidType fluidType, boolean submerged) {}
}
