package net.ltxprogrammer.changed.ability.tree.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.function.BiPredicate;

public class InFluidCondition extends AbstractCondition {
    public enum Qualification implements StringRepresentable, BiPredicate<IAbstractChangedEntity, RegistryElementPredicate<FluidType>> {
        TOUCHING("touching", (entity, fluidType) -> {
            return entity.getEntity().isInFluidType((testFluidType, height) -> fluidType.test(testFluidType));
        }),
        SUBMERGED("submerged", (entity, fluidType) -> {
            return fluidType.test(entity.getEntity().getEyeInFluidType());
        });

        public static Codec<Qualification> CODEC = Codec.STRING.comapFlatMap(Qualification::fromSerial, Qualification::getSerializedName);

        public final String serialName;
        public final BiPredicate<IAbstractChangedEntity, RegistryElementPredicate<FluidType>> predicate;

        Qualification(String serialName, BiPredicate<IAbstractChangedEntity, RegistryElementPredicate<FluidType>> predicate) {
            this.serialName = serialName;
            this.predicate = predicate;
        }

        @Override
        public boolean test(IAbstractChangedEntity entity, RegistryElementPredicate<FluidType> fluidType) {
            return predicate.test(entity, fluidType);
        }

        @Override
        public String getSerializedName() {
            return serialName;
        }

        public static DataResult<Qualification> fromSerial(String name) {
            return Arrays.stream(values()).filter(type -> type.serialName.equals(name))
                    .findFirst().map(DataResult::success).orElseGet(() -> DataResult.error(() -> name + " is not a valid Qualification"));
        }
    }

    public final RegistryElementPredicate<FluidType> fluid;
    public final Qualification qualification;

    public static final Codec<InFluidCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryElementPredicate.codecElementOrTag(ForgeRegistries.FLUID_TYPES.get()).fieldOf("fluid").forGetter(condition -> condition.fluid),
            Qualification.CODEC.fieldOf("qualification").forGetter(condition -> condition.qualification)
    ).apply(instance, InFluidCondition::new));

    public InFluidCondition(RegistryElementPredicate<FluidType> fluid, Qualification qualification) {
        this.fluid = fluid;
        this.qualification = qualification;
    }

    @Override
    public boolean test(IAbstractChangedEntity entity) {
        return qualification.test(entity, fluid);
    }

    @Override
    public Codec<? extends AbstractCondition> getCodec() {
        return CODEC;
    }
}
