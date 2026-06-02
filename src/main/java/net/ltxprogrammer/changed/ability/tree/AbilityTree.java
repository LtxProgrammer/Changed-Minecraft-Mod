package net.ltxprogrammer.changed.ability.tree;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.tree.events.AbstractPointEvent;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AbilityTree {
    protected static final Codec<AbstractPointEvent<?>> POINT_EVENT_CODEC = ChangedRegistry.POINT_EVENTS.get().getCodec().dispatch("type", AbstractPointEvent::getCodec, Function.identity());

    public static final Codec<AbilityTree> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.list(RegistryElementPredicate.codec(ChangedRegistry.TRANSFUR_VARIANT.get())).fieldOf("variants")
                    .forGetter(tree -> List.copyOf(tree.variants)),
            Codec.list(POINT_EVENT_CODEC).fieldOf("pointEvents").orElse(List.of()).forGetter(node -> List.copyOf(node.pointEvents)),
            Codec.STRING.fieldOf("titleId").forGetter(tree -> tree.titleId),
            Codec.STRING.fieldOf("flavorId").orElse("").forGetter(tree -> tree.flavorId)
    ).apply(builder, AbilityTree::new));

    public static final ResourceLocation ROOT_NAME = Changed.modResource("root");

    private final Set<RegistryElementPredicate<TransfurVariant<?>>> variants;
    private final Set<AbstractPointEvent<?>> pointEvents;
    private final String titleId;
    private final String flavorId;

    private ResourceLocation treeLocation;
    private Set<TreeView> treeRoots;
    private Map<ResourceLocation, AbilityNode> treeNodes;
    private boolean isRemote = false;

    public AbilityTree(List<RegistryElementPredicate<TransfurVariant<?>>> variants, List<AbstractPointEvent<?>> pointEvents, String titleId, String flavorId) {
        this.variants = Set.copyOf(variants);
        this.pointEvents = Set.copyOf(pointEvents);
        this.titleId = titleId;
        this.flavorId = flavorId;
    }

    public <T> int sumPointsForEvent(Codec<? extends AbstractPointEvent<T>> pointEventType, T criteria) {
        return pointEvents.stream().filter(event -> event.getCodec() == pointEventType)
                .filter(event -> ((AbstractPointEvent)event).test(criteria))
                .mapToInt(AbstractPointEvent::getPoints)
                .sum();
    }

    public void markRemote() {
        isRemote = true;
    }

    public boolean isRemote() {
        return this.isRemote;
    }

    public void setTreeLocation(ResourceLocation treeLocation) {
        this.treeLocation = treeLocation;
    }

    public void buildTree(Map<ResourceLocation, AbilityNode> pool) {
        treeRoots = pool.entrySet().stream()
                .filter(entry -> entry.getValue().parent.right().map(reference -> reference.treeName().equals(this.treeLocation)).orElse(false))
                .map(entry -> new TreeView(entry.getKey(), entry.getValue(), pool))
                .collect(Collectors.toSet());
        treeNodes = new HashMap<>();

        visitNodes((currentNode, parentNode, depth) -> {
            treeNodes.put(currentNode.getNodeLocation(), currentNode);
        });
    }

    public ResourceLocation getTreeLocation() {
        return treeLocation;
    }

    public boolean matchLocation(AbilityTree other) {
        return this.treeLocation.equals(other.treeLocation);
    }

    public boolean appliesTo(TransfurVariant<?> variant) {
        return variants.stream().anyMatch(predicate -> predicate.test(variant));
    }

    public boolean hasNode(AbilityNode node) {
        if (node == null)
            return false;

        return treeNodes.containsValue(node);
    }

    public boolean hasNode(ResourceLocation nodeName) {
        return hasNode(treeNodes.get(nodeName));
    }

    public AbilityNode getNamedNode(ResourceLocation nodeName) {
        return treeNodes.get(nodeName);
    }

    public Stream<Pair<ResourceLocation, AbilityNode>> getTreeNodes() {
        return treeNodes.entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue()));
    }

    public Component getTitle() {
        return Component.translatable(titleId)
                .withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));
    }

    public Optional<Component> getFlavorText() {
        if (flavorId.isEmpty())
            return Optional.empty();

        return Optional.of(Component.literal("\"")
                .append(Component.translatable(flavorId))
                .append(Component.literal("\""))
                .withStyle(Style.EMPTY
                        .withColor(ChatFormatting.GRAY)
                        .withItalic(true)));
    }

    public static abstract class NodeEffectInstance {
        public abstract void saveTo(CompoundTag tag);
        public abstract void readFrom(CompoundTag tag);
    }

    public static class TreeView {
        private final AbilityNode node;
        private final Set<TreeView> children;

        public TreeView(ResourceLocation thisName, AbilityNode node, Map<ResourceLocation, AbilityNode> pool) {
            this.node = node;
            this.children = pool.entrySet().stream()
                    .filter(entry -> entry.getValue().parent.left().map(parentName -> parentName.equals(thisName)).orElse(false))
                    .map(entry -> new TreeView(entry.getKey(), entry.getValue(), pool))
                    .collect(Collectors.toSet());
        }

        public AbilityNode getNode() {
            return node;
        }

        public Set<TreeView> getChildren() {
            return children;
        }
    }

    public Set<TreeView> getTreeRoots() {
        return treeRoots;
    }

    public interface NodeVisitor {
        void accept(AbilityNode currentNode, @Nullable AbilityNode parentNode, int depth);
    }

    private static void visitTreeView(NodeVisitor visitor, Set<TreeView> views, AbilityNode parent, int depth) {
        views.forEach(view -> {
            visitor.accept(view.node, parent, depth);
            visitTreeView(visitor, view.children, view.node, depth + 1);
        });
    }

    public void visitNodes(NodeVisitor visitor) {
        visitTreeView(visitor, treeRoots, null, 0);
    }
}
