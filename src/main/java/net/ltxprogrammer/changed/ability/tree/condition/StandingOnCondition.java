package net.ltxprogrammer.changed.ability.tree.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class StandingOnCondition extends AbstractCondition {
    public final List<RegistryElementPredicate<Block>> blocks;

    public static final Codec<StandingOnCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(RegistryElementPredicate.codecElementOrTag(ForgeRegistries.BLOCKS)).fieldOf("blocks").forGetter(condition -> condition.blocks)
    ).apply(instance, StandingOnCondition::new));

    public StandingOnCondition(List<RegistryElementPredicate<Block>> blocks) {
        this.blocks = blocks;
    }

    @Override
    public boolean test(IAbstractChangedEntity entity) {
        Block standingOn = entity.getEntity().getBlockStateOn().getBlock();
        return blocks.stream().anyMatch(predicate -> predicate.test(standingOn));
    }

    @Override
    public Codec<? extends AbstractCondition> getCodec() {
        return CODEC;
    }
}
