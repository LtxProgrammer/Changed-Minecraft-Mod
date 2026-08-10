package net.ltxprogrammer.changed.ability.tree.requirements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.ability.tree.AbilityNode;
import net.ltxprogrammer.changed.ability.tree.AbilityTreeInstance;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;

public class IsNotEntityRequirement extends IsEntityRequirement {
    public static final Codec<IsNotEntityRequirement> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.list(RegistryElementPredicate.codecElementOrTag(ForgeRegistries.ENTITY_TYPES)).fieldOf("entities")
                    .forGetter(tree -> List.copyOf(tree.entities)),
            Codec.STRING.fieldOf("explainId").forGetter(requirement -> requirement.requirementName)
    ).apply(builder, IsNotEntityRequirement::new));

    public IsNotEntityRequirement(List<RegistryElementPredicate<EntityType<?>>> entities, String explainId) {
        super(entities, explainId);
    }

    @Override
    public boolean requirementMet(AbilityTreeInstance.AccountedTree tree, AbilityNode node) {
        TransfurVariantInstance<?> variantInstance = ProcessTransfur.getPlayerTransfurVariant(tree.getPlayer());
        if (variantInstance == null)
            return false;

        var overlaying = EntityUtil.maybeGetOverlaying(tree.getPlayer()).getType();

        return entities.stream().noneMatch(predicate -> predicate.test(overlaying));
    }

    @Override
    public Codec<? extends AbstractRequirement> getCodec() {
        return CODEC;
    }
}
