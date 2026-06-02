package net.ltxprogrammer.changed.ability.tree.events;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class OnJump extends AbstractPointEvent<NullCriteria> {
    public static final Codec<OnJump> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("reward").forGetter(event -> event.reward)
    ).apply(instance, OnJump::new));

    @Override
    public Codec<? extends AbstractPointEvent<?>> getCodec() {
        return CODEC;
    }


    public OnJump(int reward) {
        super(reward);
    }

    @Override
    public boolean test(NullCriteria criteria) {
        return true;
    }
}
