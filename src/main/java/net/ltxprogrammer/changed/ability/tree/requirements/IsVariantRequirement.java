package net.ltxprogrammer.changed.ability.tree.requirements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.ability.tree.AbilityNode;
import net.ltxprogrammer.changed.ability.tree.AbilityTree;
import net.ltxprogrammer.changed.ability.tree.AbilityTreeInstance;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class IsVariantRequirement extends BinaryRequirement {
    public static final Codec<IsVariantRequirement> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.list(RegistryElementPredicate.codecElementOrTag(ChangedRegistry.TRANSFUR_VARIANT.get())).fieldOf("variants")
                    .forGetter(tree -> List.copyOf(tree.variants)),
            Codec.STRING.fieldOf("explainId").forGetter(requirement -> requirement.requirementName)
    ).apply(builder, IsVariantRequirement::new));

    protected final Set<RegistryElementPredicate<TransfurVariant<?>>> variants;

    public IsVariantRequirement(List<RegistryElementPredicate<TransfurVariant<?>>> variants, String explainId) {
        super(explainId);
        this.variants = Set.copyOf(variants);
    }

    @Override
    public boolean requirementMet(AbilityTreeInstance.AccountedTree tree, AbilityNode node) {
        TransfurVariantInstance<?> variantInstance = ProcessTransfur.getPlayerTransfurVariant(tree.getPlayer());
        if (variantInstance == null)
            return false;

        TransfurVariant<?> variant = variantInstance.getParent();

        return variants.stream().anyMatch(predicate -> predicate.test(variant));
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
