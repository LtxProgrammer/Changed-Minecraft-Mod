package net.ltxprogrammer.changed.ability.tree.requirements;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.ltxprogrammer.changed.ability.tree.AbilityNode;
import net.ltxprogrammer.changed.ability.tree.AbilityTreeInstance;
import net.ltxprogrammer.changed.advancements.MatchMode;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.function.Consumer;

public class HasAdvancementsRequirement extends AbstractRequirement {
    protected final List<ResourceLocation> advancementIds;
    protected final MatchMode matchMode;

    public static final Codec<HasAdvancementsRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(ResourceLocation.CODEC).fieldOf("advancements").forGetter(requirement -> requirement.advancementIds),
            MatchMode.CODEC.fieldOf("match").orElse(MatchMode.ALL_OF).forGetter(requirement -> requirement.matchMode)
    ).apply(instance, HasAdvancementsRequirement::new));

    public HasAdvancementsRequirement(List<ResourceLocation> advancementIds, MatchMode matchMode) {
        this.advancementIds = advancementIds;
        this.matchMode = matchMode;

        if (matchMode == MatchMode.NONE_OF)
            throw new UnsupportedOperationException("match cannot be \"none_of\"");
    }

    @Override
    public boolean requirementMet(AbilityTreeInstance.AccountedTree tree, AbilityNode node) {
        if (!(tree.getPlayer() instanceof ServerPlayer serverPlayer))
            return false;

        MinecraftServer server = serverPlayer.getServer();
        if (server == null)
            return false;

        ServerAdvancementManager advancementManager = server.getAdvancements();
        PlayerAdvancements playerAdvancements = serverPlayer.getAdvancements();

        return matchMode.apply(advancementIds.stream().map(advancementManager::getAdvancement), advancement -> {
            return playerAdvancements.getOrStartProgress(advancement).isDone();
        });
    }

    @Override
    public Tag serializeProgress(AbilityTreeInstance.AccountedTree tree, AbilityNode node) {
        CompoundTag tag = new CompoundTag();

        if (!(tree.getPlayer() instanceof ServerPlayer serverPlayer))
            return tag;

        MinecraftServer server = serverPlayer.getServer();
        if (server == null)
            return tag;

        ServerAdvancementManager advancementManager = server.getAdvancements();
        PlayerAdvancements playerAdvancements = serverPlayer.getAdvancements();

        advancementIds.forEach(advancementId -> {
            CompoundTag entryTag = new CompoundTag();

            Advancement advancement = advancementManager.getAdvancement(advancementId);

            entryTag.putBoolean("state", playerAdvancements.getOrStartProgress(advancement).isDone());
            if (advancement.getDisplay() != null)
                entryTag.putString("title", Component.Serializer.toJson(advancement.getDisplay().getTitle()));

            tag.put(advancementId.toString(), entryTag);
        });

        return tag;
    }

    @Override
    public RequirementProgress<?> deserializeProgress(Tag progressTag) {
        var map = new Object2ObjectArrayMap<ResourceLocation, Pair<Component, Boolean>>();
        var tag = (CompoundTag)progressTag;

        tag.getAllKeys().forEach(key -> {
            ResourceLocation id = ResourceLocation.parse(key);

            var entryTag = tag.getCompound(key);

            if (entryTag.contains("title"))
                map.put(id, Pair.of(Component.Serializer.fromJson(entryTag.getString("title")), entryTag.getBoolean("state")));
            else
                map.put(id, Pair.of(Component.literal(key), entryTag.getBoolean("state")));
        });

        return new Progress(this, map);
    }

    @Override
    public Codec<? extends AbstractRequirement> getCodec() {
        return CODEC;
    }

    protected static class Progress extends RequirementProgress<HasAdvancementsRequirement> {
        private final Object2ObjectMap<ResourceLocation, Pair<Component, Boolean>> advancementsMet;

        public Progress(HasAdvancementsRequirement requirement, Object2ObjectMap<ResourceLocation, Pair<Component, Boolean>> advancementsMet) {
            super(requirement);
            this.advancementsMet = advancementsMet;
        }

        @Override
        public boolean requirementMet() {
            return requirement.matchMode.apply(advancementsMet.values().stream(), Pair::getSecond);
        }

        @Override
        public void buildDescription(Consumer<Component> componentConsumer) {
            if (advancementsMet.size() != 1) {
                componentConsumer.accept(requirement.matchMode.getDisplayText().withStyle(
                        requirementMet() ? ChatFormatting.GREEN : ChatFormatting.RED
                ));
            }

            advancementsMet.forEach((id, pair) -> {
                componentConsumer.accept(
                        Component.literal("  ")
                                .append(Component.translatable("text.changed.advancement", pair.getFirst()))
                                .withStyle(pair.getSecond() ? ChatFormatting.GREEN : ChatFormatting.RED)
                );
            });
        }
    }
}
