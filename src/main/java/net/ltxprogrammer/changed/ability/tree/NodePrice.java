package net.ltxprogrammer.changed.ability.tree;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public record NodePrice(int levels,
                        int groupDiscountLevels,
                        int experience,
                        int groupDiscountExperience,
                        List<ItemEntry> items, @Nullable NodePrice computedDiscountPrice) {
    /// Save computation by computing the discount price once
    private static NodePrice precomputeDiscounted(int levels, int groupDiscountLevels, int experience, int groupDiscountExperience, List<ItemEntry> items) {
        if (groupDiscountLevels == 0 && groupDiscountExperience == 0 && items.stream().noneMatch(ItemEntry::groupDiscounted))
            return null;

        return new NodePrice(
             levels + groupDiscountLevels, 0,
             experience + groupDiscountExperience, 0,
             items.stream().filter(entry -> !entry.groupDiscounted()).toList()
        );
    }

    public NodePrice(int levels, int groupDiscountLevels, int experience, int groupDiscountExperience, List<ItemEntry> items) {
        this(levels, groupDiscountLevels, experience, groupDiscountExperience, items,
                precomputeDiscounted(levels, groupDiscountLevels, experience, groupDiscountExperience, items));
    }

    public boolean isFree() {
        return levels <= 0 && experience <= 0 && items.isEmpty();
    }

    public static boolean hasNonUniqueItem(Inventory inventory, ItemEntry entry) {
        for (var item : inventory.items) {
            if (entry.item.test(item.getItem()))
                return true;
        }

        return false;
    }

    public boolean hasUniqueItems(Inventory inventory) {
        if (items.isEmpty())
            return true;

        var checkItems = new ObjectArrayList<>(this.items);

        for (int slotIndex = 0; slotIndex < inventory.getContainerSize(); ++slotIndex) {
            var itemStack = inventory.getItem(slotIndex);
            int simuCount = itemStack.getCount();
            if (simuCount <= 0)
                continue;

            for (var it = checkItems.iterator(); it.hasNext(); ) {
                var itemEntry = it.next();
                if (!itemEntry.item().test(itemStack.getItem()))
                    continue;

                simuCount--;
                it.remove();

                if (simuCount <= 0)
                    break;
            }

            if (checkItems.isEmpty())
                return true;
        }

        return false;
    }

    public List<ItemStack> takeItems(Inventory inventory) {
        if (items.isEmpty())
            return List.of();

        List<ItemStack> takenItems = new ObjectArrayList<>(this.items.size());
        var checkItems = new ObjectArrayList<>(this.items);

        for (int slotIndex = 0; slotIndex < inventory.getContainerSize(); ++slotIndex) {
            var itemStack = inventory.getItem(slotIndex);
            if (itemStack.isEmpty())
                continue;

            for (var it = checkItems.iterator(); it.hasNext(); ) {
                var itemEntry = it.next();
                if (!itemEntry.item().test(itemStack.getItem()))
                    continue;

                takenItems.add(itemStack.split(1));
                it.remove();

                if (itemStack.isEmpty())
                    break;
            }

            if (checkItems.isEmpty())
                break;
        }

        return takenItems;
    }

    public NodePrice getEffectivePrice(boolean discounted) {
        return (discounted && computedDiscountPrice != null) ? computedDiscountPrice : this;
    }

    public void getLines(Consumer<MutableComponent> lineConsumer, Player player, AbilityTreeInstance.PointStore pointStore, @Nullable ChatFormatting overrideColor) {
        if (isFree()) {
            lineConsumer.accept(Component.translatable("text.changed.ability_tree.price.free").withStyle(overrideColor != null ? overrideColor : ChatFormatting.WHITE));
        } else {
            if (this.levels > 0)
                lineConsumer.accept(Component.translatable("text.changed.ability_tree.price.levels", this.levels)
                        .withStyle(overrideColor != null ? overrideColor : (this.levels <= pointStore.getLevels() ? ChatFormatting.GREEN : ChatFormatting.RED)));
            if (this.experience > 0)
                lineConsumer.accept(Component.translatable("text.changed.ability_tree.price.xplevels", this.experience)
                        .withStyle(overrideColor != null ? overrideColor : (this.experience <= player.experienceLevel ? ChatFormatting.GREEN : ChatFormatting.RED)));
            this.items.forEach(entry -> {
                entry.item.getValues().findFirst().ifPresent(item -> {
                    lineConsumer.accept(Component.translatable(item.getDescriptionId(new ItemStack(item)))
                            .withStyle(overrideColor != null ? overrideColor : (hasNonUniqueItem(player.getInventory(), entry) ? ChatFormatting.GREEN : ChatFormatting.RED)));
                });
            });
        }
    }

    public boolean canAfford(Player player, AbilityTreeInstance.PointStore pointStore) {
        return this.levels() <= pointStore.getLevels() &&
                this.levels() <= player.experienceLevel &&
                this.hasUniqueItems(player.getInventory());
    }

    public record ItemEntry(RegistryElementPredicate<Item> item, boolean groupDiscounted) {}

    public static final Codec<RegistryElementPredicate<Item>> ITEM_PREDICATE_CODEC = RegistryElementPredicate.codecElementOrTag(ForgeRegistries.ITEMS);

    public static final Codec<ItemEntry> ITEM_ENTRY_CODEC = Codec.either(
            ITEM_PREDICATE_CODEC,
            RecordCodecBuilder.<ItemEntry>create(builder -> builder.group(
                    ITEM_PREDICATE_CODEC.fieldOf("item").forGetter(ItemEntry::item),
                    Codec.BOOL.fieldOf("groupDiscounted").orElse(false).forGetter(ItemEntry::groupDiscounted)
            ).apply(builder, ItemEntry::new))
    ).xmap(
            either -> either.map(predicate -> new ItemEntry(predicate, false), Function.identity()),
            Either::right
    );

    public static final Codec<NodePrice> CODEC = Codec.either(
            Codec.INT,
            RecordCodecBuilder.<NodePrice>create(builder -> builder.group(
                Codec.INT.fieldOf("levels").forGetter(NodePrice::levels),
                Codec.INT.fieldOf("groupDiscountLevels").orElse(0).forGetter(NodePrice::groupDiscountLevels),
                Codec.INT.fieldOf("experience").orElse(0).forGetter(NodePrice::experience),
                Codec.INT.fieldOf("groupDiscountExperience").orElse(0).forGetter(NodePrice::groupDiscountExperience),
                Codec.list(ITEM_ENTRY_CODEC).fieldOf("items").orElseGet(List::of).forGetter(NodePrice::items)
            ).apply(builder, NodePrice::new))
    ).xmap(
            either -> either.map(levels -> new NodePrice(levels, 0, 0, 0, List.of()), Function.identity()),
            Either::right
    );
}
