package net.ltxprogrammer.changed.ability.tree.requirements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.ability.tree.AbilityNode;
import net.ltxprogrammer.changed.ability.tree.AbilityTreeInstance;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class IsEntityRequirement extends BinaryRequirement {
    public static final Codec<IsEntityRequirement> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.list(RegistryElementPredicate.codecElementOrTag(ForgeRegistries.ENTITY_TYPES)).fieldOf("entities")
                    .forGetter(tree -> List.copyOf(tree.entities)),
            Codec.STRING.fieldOf("explainId").forGetter(requirement -> requirement.requirementName)
    ).apply(builder, IsEntityRequirement::new));

    protected final Set<RegistryElementPredicate<EntityType<?>>> entities;

    public IsEntityRequirement(List<RegistryElementPredicate<EntityType<?>>> entities, String explainId) {
        super(explainId);
        this.entities = Set.copyOf(entities);
    }

    @Override
    public boolean requirementMet(AbilityTreeInstance.AccountedTree tree, AbilityNode node) {
        TransfurVariantInstance<?> variantInstance = ProcessTransfur.getPlayerTransfurVariant(tree.getPlayer());
        if (variantInstance == null)
            return false;

        var overlaying = EntityUtil.maybeGetOverlaying(tree.getPlayer()).getType();

        return entities.stream().anyMatch(predicate -> predicate.test(overlaying));
    }

    @Override
    public Codec<? extends AbstractRequirement> getCodec() {
        return CODEC;
    }

    @Override
    public boolean skipIfNotMet() {
        return true;
    }

    @Override
    public RequirementProgress<?> deserializeProgress(Tag progressTag) {
        return new Progress<>(this, ((ByteTag)progressTag).getAsInt() > 0);
    }

    protected static class Progress<T extends BinaryRequirement> extends BinaryRequirement.Progress<T> {
        public Progress(T requirement, boolean met) {
            super(requirement, met);
        }

        @Override
        public void buildDescription(Consumer<Component> componentConsumer) {
            if (met)
                return;

            componentConsumer.accept(
                    Component.translatable(requirement.requirementName).withStyle(ChatFormatting.RED)
            );
        }
    }
}
