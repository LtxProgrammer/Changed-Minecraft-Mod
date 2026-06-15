package net.ltxprogrammer.changed.ability.tree;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.tree.events.AbstractPointEvent;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Instantiated per player, exists outside TransfurVariantInstance to persist between variants
 */
public class AbilityTreeInstance {
    public record AccountedPurchase(ResourceLocation nodeName, TransfurVariant<?> variant, int levels, int experienceLevels, List<ItemStack> items) {
        public AccountedPurchase(ResourceLocation nodeName, TransfurVariant<?> variant, int levels, int experienceLevels) {
            this(nodeName, variant, levels, experienceLevels, List.of());
        }

        public static AccountedPurchase of(CompoundTag tag) {
            var items = ImmutableList.<ItemStack>builder();
            tag.getList("items", 10).forEach(itemTag -> {
                items.add(ItemStack.of((CompoundTag)itemTag));
            });
            return new AccountedPurchase(
                    TagUtil.getResourceLocation(tag, "node"),
                    ChangedRegistry.TRANSFUR_VARIANT.getValue(TagUtil.getResourceLocation(tag, "variant")),
                    tag.getInt("levels"),
                    tag.getInt("xp"),
                    items.build()
            );
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (obj instanceof AccountedPurchase other)
                return other.nodeName.equals(nodeName) && other.variant.equals(variant)
                        && other.levels == levels
                        && other.experienceLevels == experienceLevels
                        && other.items.equals(items);
            return false;
        }
    }

    public record NodeState(ResourceLocation nodeName, AbilityNode node, boolean unlocked) {}

    public static class PointStore {
        private int points;
        private int levels;

        public PointStore(TransfurVariant<?> ignored) {
            this(0, 0);
        }

        public PointStore() {
            this(0, 0);
        }

        public PointStore(int points, int levels) {
            this.points = points;
            this.levels = levels;
        }

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
        }

        public void addPoints(int points) {
            setPoints(this.points + points);
        }

        public void addPointsAndConvert(int points, int conversionThreshold) {
            addPoints(points);
            convertPointsToLevels(conversionThreshold);
        }

        public int getLevels() {
            return levels;
        }

        public void setLevels(int levels) {
            this.levels = levels;
        }

        public void addLevels(int levels) {
            setLevels(this.levels + levels);
        }

        public void removeLevels(int levels) {
            setLevels(this.levels - levels);
        }

        public boolean canAfford(int levelCost) {
            return this.getLevels() >= levelCost;
        }

        public void convertPointsToLevels(int conversionThreshold) {
            if (points < conversionThreshold)
                return;

            addLevels(points / conversionThreshold);
            setPoints(points % conversionThreshold);
        }

        public @Nullable CompoundTag save() {
            if (this.points <= 0 && this.levels <= 0)
                return null; // Save on memory

            var tag = new CompoundTag();
            if (this.points > 0)
                tag.putInt("p", this.points);
            if (this.levels > 0)
                tag.putInt("l", this.levels);
            return tag;
        }

        public void read(CompoundTag tag) {
            setPoints(tag.getInt("p"));
            setLevels(tag.getInt("l"));
        }

        public static PointStore IMMUTABLE_ZERO = new PointStore() {
            @Override
            public void setLevels(int levels) {}

            @Override
            public void setPoints(int points) {}
        };
    }

    public static class AccountedTree {
        private final AbilityTree tree;
        private final List<AccountedPurchase> purchasedNodes = new ArrayList<>();
        private final Map<TransfurVariant<?>, PointStore> pointStores = new HashMap<>();

        public AccountedTree(AbilityTree tree) {
            this.tree = tree;
        }

        public AccountedTree(AbilityTree tree, List<AccountedPurchase> purchasedNodes, Map<TransfurVariant<?>, PointStore> pointStores) {
            this.tree = tree;
            this.purchasedNodes.addAll(purchasedNodes);
            this.pointStores.putAll(pointStores);
        }

        public boolean canAfford(Player player, TransfurVariant<?> variant, ResourceLocation nodeName) {
            var price = this.getEffectivePrice(variant, nodeName);
            return price.canAfford(player, pointStores.getOrDefault(variant, AbilityTreeInstance.PointStore.IMMUTABLE_ZERO));
        }

        public void getEffectivePriceText(Player player, TransfurVariant<?> variant, ResourceLocation nodeName, Consumer<MutableComponent> lineConsumer, @Nullable ChatFormatting overrideColor) {
            this.getEffectivePrice(variant, nodeName).getLines(lineConsumer, player, pointStores.getOrDefault(variant, AbilityTreeInstance.PointStore.IMMUTABLE_ZERO), overrideColor);
        }

        /// Purchases the nodeName for the given price
        public boolean makePurchase(ServerPlayer player, TransfurVariant<?> variant, ResourceLocation nodeName, int levels, int experienceLevels, List<ItemStack> items) {
            if (!tree.hasNode(nodeName))
                return false;

            if (purchasedNodes.stream().anyMatch(purchase -> {
                return purchase.nodeName.equals(nodeName) && purchase.variant == variant;
            })) return false;

            var pointStore = pointStores.computeIfAbsent(variant, PointStore::new);

            pointStore.removeLevels(levels);
            if (experienceLevels > 0)
                player.setExperienceLevels(player.experienceLevel - experienceLevels);
            purchasedNodes.add(new AccountedPurchase(nodeName, variant, levels, experienceLevels, items));
            return true;
        }

        protected void refundPurchase(Player player, AccountedPurchase purchase) {
            purchasedNodes.remove(purchase);
            pointStores.computeIfAbsent(purchase.variant, PointStore::new).addLevels(purchase.levels);
            if (purchase.experienceLevels > 0)
                player.giveExperienceLevels(purchase.experienceLevels);
            purchase.items.forEach(item -> {
                if (!player.addItem(item))
                    player.drop(item, false);
            });
        }

        public int refundNodePurchases(Player player, ResourceLocation nodeName) {
            if (!tree.hasNode(nodeName))
                return 0;

            if (purchasedNodes.stream().noneMatch(purchase -> {
                return purchase.nodeName.equals(nodeName);
            })) return 0;

            var toRemove = purchasedNodes.stream().filter(purchase -> {
                return purchase.nodeName.equals(nodeName);
            }).toList();

            toRemove.forEach(purchase -> {
                refundPurchase(player, purchase);
            });

            return toRemove.size();
        }

        public AbilityTree getTree() {
            return tree;
        }

        public Stream<AccountedPurchase> getPurchasesFor(ResourceLocation nodeName) {
            return purchasedNodes.stream().filter(purchase -> purchase.nodeName.equals(nodeName));
        }

        public boolean hasPrerequisites(TransfurVariant<?> forVariant, ResourceLocation nodeName) {
            final var node = tree.getNamedNode(nodeName);
            if (node == null)
                return false;
            // TODO maybe let node specify criteria as well

            return node.parent.map(
                    parentName -> getNodeStates(forVariant, pair -> {
                        return pair.getFirst().equals(parentName);
                    }).allMatch(NodeState::unlocked),
                    treeReference -> true
            );
        }

        public Stream<NodeState> getNodeStates(TransfurVariant<?> forVariant) {
            return getNodeStates(forVariant, pair -> true);
        }

        private NodeState computeNodeState(TransfurVariant<?> forVariant, Pair<ResourceLocation, AbilityNode> namedNode) {
            if (namedNode.getSecond().price.isFree()) {
                return new NodeState(namedNode.getFirst(), namedNode.getSecond(), true);
            }

            boolean checkedIsFreeDiscounted = false;
            for (var it = getPurchasesFor(namedNode.getFirst()).iterator(); it.hasNext(); ) {
                var purchase = it.next();
                if (purchase.variant == forVariant)
                    return new NodeState(namedNode.getFirst(), namedNode.getSecond(), true);
                if (checkedIsFreeDiscounted)
                    continue;

                checkedIsFreeDiscounted = true;
                if (namedNode.getSecond().price.getEffectivePrice(true).isFree())
                    return new NodeState(namedNode.getFirst(), namedNode.getSecond(), true);
            }

            return new NodeState(namedNode.getFirst(), namedNode.getSecond(), false);
        }

        public Stream<NodeState> getNodeStates(TransfurVariant<?> forVariant, Predicate<Pair<ResourceLocation, AbilityNode>> nodePredicate) {
            return tree.getTreeNodes().filter(nodePredicate).map(namedNode -> this.computeNodeState(forVariant, namedNode));
        }

        public Optional<NodeState> getNodeState(TransfurVariant<?> forVariant, AbilityNode node) {
            return tree.getTreeNodes().filter(pair -> pair.getSecond() == node).findAny()
                    .map(namedNode -> this.computeNodeState(forVariant, namedNode));
        }

        public Optional<NodeState> getNodeState(TransfurVariant<?> forVariant, ResourceLocation nodeName) {
            return tree.getTreeNodes().filter(pair -> pair.getFirst().equals(nodeName)).findAny()
                    .map(namedNode -> this.computeNodeState(forVariant, namedNode));
        }

        public boolean hasAllNodes(TransfurVariant<?> variant) {
            return getNodeStates(variant).allMatch(NodeState::unlocked);
        }

        public NodePrice getEffectivePrice(TransfurVariant<?> forVariant, AbilityNode node) {
            if (!tree.hasNode(node))
                throw new IllegalArgumentException("Node does not exist in tree: " + node.getNodeLocation());

            boolean applyDiscount = getPurchasesFor(node.getNodeLocation())
                    .anyMatch(purchase -> purchase.variant != forVariant && purchase.levels >= node.price.levels());
            return node.price.getEffectivePrice(applyDiscount);
        }

        public NodePrice getEffectivePrice(TransfurVariant<?> forVariant, ResourceLocation nodeName) {
            var node = tree.getNamedNode(nodeName);
            if (node == null)
                throw new IllegalArgumentException("Unknown node by name " + nodeName);

            return getEffectivePrice(forVariant, node);
        }

        public void refundInvalidNodes(Player player) {
            Set<AccountedPurchase> invalid = new HashSet<>();
            purchasedNodes.forEach(purchase -> {
                if (!tree.hasNode(purchase.nodeName))
                    invalid.add(purchase);
            });
            invalid.forEach(purchase -> {
                refundPurchase(player, purchase);
            });
        }

        public void gatherNodeEffects(TransfurVariantInstance<?> variantInstance, Consumer<NodeEffect> sink) {
            Set<ResourceLocation> occludedNodes = new HashSet<>();
            getNodeStates(variantInstance.getParent()).filter(NodeState::unlocked)
                    .forEach(nodeState -> occludedNodes.addAll(nodeState.node.occludes));

            IAbstractChangedEntity entity = IAbstractChangedEntity.forPlayerWithVariant(variantInstance.getHost(), variantInstance);

            getNodeStates(variantInstance.getParent()).forEach(nodeState -> {
                if (nodeState.unlocked) {
                    if (!occludedNodes.contains(nodeState.nodeName)) {
                        nodeState.node.acquiredEffects.forEach(nodeEffect -> {
                            nodeEffect.gatherActiveEffects(entity, sink);
                        });
                    }
                } else {
                    nodeState.node.missingEffects.forEach(nodeEffect -> {
                        nodeEffect.gatherActiveEffects(entity, sink);
                    });
                }
            });
        }

        public <T> void offerPointEvent(TransfurVariantInstance<?> variantInstance, Codec<? extends AbstractPointEvent<T>> pointEventType, T criteria) {
            if (variantInstance.getHost().level().isClientSide())
                return;

            int points = tree.sumPointsForEvent(pointEventType, criteria);
            if (points == 0)
                return;

            pointStores.computeIfAbsent(variantInstance.getParent(), PointStore::new).addPointsAndConvert(points, tree.getPointsPerLevel());
        }

        public boolean appliesTo(TransfurVariant<?> variant) {
            return tree.appliesTo(variant);
        }

        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();

            {
                ListTag purchases = new ListTag();
                purchasedNodes.forEach(purchase -> {
                    CompoundTag purchaseTag = new CompoundTag();
                    TagUtil.putResourceLocation(purchaseTag, "node", purchase.nodeName);
                    TagUtil.putResourceLocation(purchaseTag, "variant", ChangedRegistry.TRANSFUR_VARIANT.getKey(purchase.variant));
                    if (purchase.levels > 0)
                        purchaseTag.putInt("levels", purchase.levels);
                    if (purchase.experienceLevels > 0)
                        purchaseTag.putInt("experienceLevels", purchase.experienceLevels);
                    if (!purchase.items.isEmpty()) {
                        ListTag items = new ListTag();
                        purchase.items.forEach(item -> {
                            CompoundTag itemTag = new CompoundTag();
                            item.save(itemTag);
                            items.add(itemTag);
                        });
                        purchaseTag.put("items", items);
                    }
                    purchases.add(purchaseTag);
                });
                tag.put("purchases", purchases);
            }

            {
                CompoundTag pointStoreTag = new CompoundTag();
                pointStores.forEach((variant, pointStore) -> {
                    var savedPointStore = pointStore.save();
                    if (savedPointStore == null)
                        return;
                    pointStoreTag.put(ChangedRegistry.TRANSFUR_VARIANT.getKey(variant).toString(), savedPointStore);
                });
                tag.put("pointStore", pointStoreTag);
            }

            return tag;
        }

        public void read(CompoundTag tag) {
            purchasedNodes.clear();
            pointStores.clear();

            tag.getList("purchases", 10).forEach(purchaseTag -> {
                var compound = ((CompoundTag)purchaseTag);
                purchasedNodes.add(AccountedPurchase.of(compound));
            });

            var pointStoreTag = tag.getCompound("pointStore");
            pointStoreTag.getAllKeys().forEach(key -> {
                var variant = ChangedRegistry.TRANSFUR_VARIANT.getValue(ResourceLocation.parse(key));
                pointStores.computeIfAbsent(variant, PointStore::new).read(pointStoreTag.getCompound(key));
            });
        }
    }

    private final Set<AccountedTree> trees = new HashSet<>();

    public Set<AccountedTree> getTrees() {
        return ImmutableSet.copyOf(trees);
    }

    public Set<AccountedTree> getTrees(TransfurVariant<?> forVariant) {
        return trees.stream().filter(tree -> tree.appliesTo(forVariant)).collect(Collectors.toSet());
    }

    public void updateTrees(Player player) {
        var treeDefinitions = AbilityTrees.INSTANCE.getTrees();
        if (trees.size() == treeDefinitions.size() && trees.stream().allMatch(accountedTree ->
            treeDefinitions.stream().anyMatch(accountedTree.tree::matchLocation)
        )) {
            return;
        }

        Set<AccountedTree> newAccountedTrees = new HashSet<>();
        trees.forEach(accountedTree -> {
            var newTree = treeDefinitions.stream().filter(accountedTree.tree::matchLocation).findFirst();
            if (newTree.isEmpty())
                return;
            var newAccountedTree = new AccountedTree(
                    newTree.get(),
                    accountedTree.purchasedNodes,
                    accountedTree.pointStores
            );
            newAccountedTree.refundInvalidNodes(player);
            newAccountedTrees.add(newAccountedTree);
        });

        treeDefinitions.forEach(abilityTree -> {
            if (newAccountedTrees.stream().anyMatch(accountedTree -> accountedTree.tree == abilityTree))
                return;

            newAccountedTrees.add(new AccountedTree(abilityTree));
        });

        trees.clear();
        trees.addAll(newAccountedTrees);
    }

    public void gatherNodeEffects(TransfurVariantInstance<?> variantInstance, Consumer<NodeEffect> sink) {
        trees.forEach(tree -> {
            if (tree.appliesTo(variantInstance.getParent()))
                tree.gatherNodeEffects(variantInstance, sink);
        });
    }

    public <T> void offerPointEventToTrees(TransfurVariantInstance<?> variantInstance, Codec<? extends AbstractPointEvent<T>> pointEventType, T criteria) {
        if (variantInstance == null)
            return;

        trees.forEach(tree -> {
            if (tree.appliesTo(variantInstance.getParent()))
                tree.offerPointEvent(variantInstance, pointEventType, criteria);
        });
    }

    public static AbilityTreeInstance getForPlayer(Player player) {
        return ((PlayerDataExtension)player).getAbilityTree();
    }

    public static <T> void offerPointEvent(IAbstractChangedEntity entity, Codec<? extends AbstractPointEvent<T>> pointEventType, T criteria) {
        if (entity.getEntity() instanceof Player player) {
            var tree = getForPlayer(player);
            tree.offerPointEventToTrees(entity.getTransfurVariantInstance(), pointEventType, criteria);
        }
    }

    public static <T> void offerPointEvent(Player player, Codec<? extends AbstractPointEvent<T>> pointEventType, T criteria) {
        var tree = getForPlayer(player);
        ProcessTransfur.ifPlayerTransfurred(player, variantInstance -> {
            tree.offerPointEventToTrees(variantInstance, pointEventType, criteria);
        });
    }

    public static <T> void offerPointEvent(TransfurVariantInstance<?> variantInstance, Codec<? extends AbstractPointEvent<T>> pointEventType, T criteria) {
        var tree = getForPlayer(variantInstance.getHost());
        tree.offerPointEventToTrees(variantInstance, pointEventType, criteria);
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        trees.forEach(accountedTree -> {
            tag.put(accountedTree.getTree().getTreeLocation().toString(),
                    accountedTree.save());
        });
        return tag;
    }

    public CompoundTag saveActive(TransfurVariant<?> variant) {
        CompoundTag tag = new CompoundTag();
        trees.forEach(accountedTree -> {
            if (accountedTree.appliesTo(variant))
                tag.put(accountedTree.getTree().getTreeLocation().toString(),
                        accountedTree.save());
        });
        return tag;
    }

    public CompoundTag saveTree(AbilityTree tree) {
        CompoundTag tag = new CompoundTag();
        trees.forEach(accountedTree -> {
            if (accountedTree.getTree() == tree)
                tag.put(accountedTree.getTree().getTreeLocation().toString(),
                        accountedTree.save());
        });
        return tag;
    }

    public void read(Player player, CompoundTag tag, boolean incomplete) {
        var treeDefinitions = player.level().isClientSide ? AbilityTrees.INSTANCE.getRemoteTrees() : AbilityTrees.INSTANCE.getTrees();

        Set<AccountedTree> newAccountedTrees = new HashSet<>();
        tag.getAllKeys().forEach(key -> {
            ResourceLocation treeLocation = ResourceLocation.parse(key);
            var newTree = treeDefinitions.stream().filter(tree -> tree.getTreeLocation().equals(treeLocation)).findFirst();
            if (newTree.isEmpty())
                return;
            var existingAccountedTree = trees.stream().filter(tree -> tree.tree == newTree.get()).findAny();

            var newAccountedTree = existingAccountedTree.orElseGet(() -> new AccountedTree(
                    newTree.get(),
                    List.of(),
                    Map.of()
            ));

            newAccountedTree.read(tag.getCompound(key));
            newAccountedTree.refundInvalidNodes(player);
            newAccountedTrees.add(newAccountedTree);
        });

        if (!incomplete)
            trees.clear();
        trees.addAll(newAccountedTrees);
    }

    public void restoreFrom(AbilityTreeInstance abilityTree) {
        trees.clear();
        trees.addAll(abilityTree.trees);
    }
}
