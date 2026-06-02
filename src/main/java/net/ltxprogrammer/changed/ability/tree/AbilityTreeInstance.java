package net.ltxprogrammer.changed.ability.tree;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.tree.events.AbstractPointEvent;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Instantiated per player, exists outside TransfurVariantInstance to persist between variants
 */
public class AbilityTreeInstance {
    public static final int POINTS_PER_LEVEL = 10;

    public record AccountedPurchase(ResourceLocation nodeName, TransfurVariant<?> variant, int price) {
        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (obj instanceof AccountedPurchase other)
                return other.nodeName.equals(nodeName) && other.variant.equals(variant) && other.price == price;
            return false;
        }
    }

    public record NodeState(ResourceLocation nodeName, AbilityNode node, boolean unlocked) {}

    public static class AccountedTree {
        private final AbilityTree tree;
        private final List<AccountedPurchase> purchasedNodes = new ArrayList<>();
        private final Map<TransfurVariant<?>, Integer> pointStores = new HashMap<>();

        public AccountedTree(AbilityTree tree) {
            this.tree = tree;
        }

        public AccountedTree(AbilityTree tree, List<AccountedPurchase> purchasedNodes, Map<TransfurVariant<?>, Integer> pointStores) {
            this.tree = tree;
            this.purchasedNodes.addAll(purchasedNodes);
            this.pointStores.putAll(pointStores);
        }

        public boolean canAfford(TransfurVariant<?> variant, ResourceLocation nodeName) {
            int price = this.getEffectivePrice(variant, nodeName);
            return price <= pointStores.getOrDefault(variant, 0);
        }

        public MutableComponent getEffectivePriceText(TransfurVariant<?> variant, ResourceLocation nodeName) {
            return Component.translatable("text.changed.ability_tree.price", this.getEffectivePrice(variant, nodeName));
        }

        public boolean makePurchase(TransfurVariant<?> variant, ResourceLocation nodeName, int price) {
            if (!tree.hasNode(nodeName))
                return false;

            if (purchasedNodes.stream().anyMatch(purchase -> {
                return purchase.nodeName.equals(nodeName) && purchase.variant == variant;
            })) return false;

            pointStores.compute(variant, ($_, points) -> {
                if (points == null)
                    return 0;
                else
                    return points - price;
            });

            purchasedNodes.add(new AccountedPurchase(nodeName, variant, price));
            return true;
        }

        public int refundNodePurchases(ResourceLocation nodeName) {
            if (!tree.hasNode(nodeName))
                return 0;

            if (purchasedNodes.stream().noneMatch(purchase -> {
                return purchase.nodeName.equals(nodeName);
            })) return 0;

            var toRemove = purchasedNodes.stream().filter(purchase -> {
                return purchase.nodeName.equals(nodeName);
            }).toList();

            toRemove.forEach(purchase -> {
                purchasedNodes.remove(purchase);
                pointStores.compute(purchase.variant, (variant, points) -> {
                    if (points == null)
                        return purchase.price;
                    else
                        return points + purchase.price;
                });
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
            boolean unlocked = getPurchasesFor(namedNode.getFirst()).anyMatch(purchase -> {
                if (purchase.variant == forVariant)
                    return true; // This variant paid for the node
                if (namedNode.getSecond().price + namedNode.getSecond().groupDiscount <= 0)
                    return true; // Another variant paid for the node, and the discount makes it free
                return false;
            });
            return new NodeState(namedNode.getFirst(), namedNode.getSecond(), unlocked);
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

        public int getEffectivePrice(TransfurVariant<?> forVariant, AbilityNode node) {
            if (!tree.hasNode(node))
                throw new IllegalArgumentException("Node does not exist in tree: " + node.getNodeLocation());

            boolean applyDiscount = getPurchasesFor(node.getNodeLocation())
                    .anyMatch(purchase -> purchase.variant != forVariant && purchase.price >= node.price);
            return (applyDiscount ? node.price + node.groupDiscount : node.price);
        }

        public int getEffectivePrice(TransfurVariant<?> forVariant, ResourceLocation nodeName) {
            var node = tree.getNamedNode(nodeName);
            if (node == null)
                throw new IllegalArgumentException("Unknown node by name " + nodeName);

            return getEffectivePrice(forVariant, node);
        }

        public void refundInvalidNodes() {
            Set<AccountedPurchase> invalid = new HashSet<>();
            purchasedNodes.forEach(purchase -> {
                if (!tree.hasNode(purchase.nodeName))
                    invalid.add(purchase);
            });
            invalid.forEach(purchase -> {
                purchasedNodes.remove(purchase);
                pointStores.compute(purchase.variant, (variant, points) -> {
                    if (points == null)
                        return purchase.price;
                    else
                        return points + purchase.price;
                });
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

            pointStores.compute(variantInstance.getParent(), ($_, prevPoints) -> {
                if (prevPoints == null)
                    return points;
                else
                    return prevPoints + points;
            });
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
                    purchaseTag.putInt("price", purchase.price);
                    purchases.add(purchaseTag);
                });
                tag.put("purchases", purchases);
            }

            {
                CompoundTag pointStore = new CompoundTag();
                pointStores.forEach((variant, points) -> {
                    pointStore.putInt(ChangedRegistry.TRANSFUR_VARIANT.getKey(variant).toString(), points);
                });
                tag.put("pointStore", pointStore);
            }

            return tag;
        }

        public void read(CompoundTag tag) {
            purchasedNodes.clear();
            pointStores.clear();

            tag.getList("purchases", 10).forEach(purchaseTag -> {
                var compound = ((CompoundTag)purchaseTag);
                purchasedNodes.add(new AccountedPurchase(
                        TagUtil.getResourceLocation(compound, "node"),
                        ChangedRegistry.TRANSFUR_VARIANT.getValue(TagUtil.getResourceLocation(compound, "variant")),
                        compound.getInt("price")
                ));
            });

            var pointStore = tag.getCompound("pointStore");
            pointStore.getAllKeys().forEach(key -> {
                var variant = ChangedRegistry.TRANSFUR_VARIANT.getValue(ResourceLocation.parse(key));
                pointStores.put(variant, pointStore.getInt(key));
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

    public void updateTrees() {
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
            newAccountedTree.refundInvalidNodes();
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

    public void read(Level level, CompoundTag tag, boolean incomplete) {
        var treeDefinitions = level.isClientSide ? AbilityTrees.INSTANCE.getRemoteTrees() : AbilityTrees.INSTANCE.getTrees();

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
            newAccountedTree.refundInvalidNodes();
            newAccountedTrees.add(newAccountedTree);
        });

        if (!incomplete)
            trees.clear();
        trees.addAll(newAccountedTrees);
    }
}
