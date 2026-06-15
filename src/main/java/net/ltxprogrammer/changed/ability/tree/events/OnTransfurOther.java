package net.ltxprogrammer.changed.ability.tree.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class OnTransfurOther extends AbstractPointEvent<OnTransfurOther.Criteria> {
    public static final Codec<OnTransfurOther> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("reward").forGetter(event -> event.reward),
            RegistryElementPredicate.codecElementOrTag(ForgeRegistries.ENTITY_TYPES).fieldOf("entity").orElse(RegistryElementPredicate.forAll(ForgeRegistries.ENTITY_TYPES)).forGetter(event -> event.typePredicate)
    ).apply(instance, OnTransfurOther::new));

    @Override
    public Codec<? extends AbstractPointEvent<?>> getCodec() {
        return CODEC;
    }

    public final RegistryElementPredicate<EntityType<?>> typePredicate;

    public OnTransfurOther(int reward, RegistryElementPredicate<EntityType<?>> typePredicate) {
        super(reward);
        this.typePredicate = typePredicate;
    }

    @Override
    public boolean test(Criteria criteria) {
        return typePredicate.test(criteria.transfurredEntity().getType());
    }

    public record Criteria(LivingEntity transfurredEntity) {}
}
