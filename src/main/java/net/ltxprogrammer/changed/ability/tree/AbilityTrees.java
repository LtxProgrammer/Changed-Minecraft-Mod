package net.ltxprogrammer.changed.ability.tree;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.network.packet.AbilityTreeSyncInstancePacket;
import net.ltxprogrammer.changed.network.packet.ChangedPacket;
import net.ltxprogrammer.changed.util.ResourceUtil;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AbilityTrees extends SimplePreparableReloadListener<Pair<Map<ResourceLocation, AbilityNode>, Set<AbilityTree>>> {
    public static final AbilityTrees INSTANCE = new AbilityTrees();

    private Map<ResourceLocation, AbilityNode> nodes = Map.of();
    private Set<AbilityTree> trees = Set.of();
    private Set<AbilityTree> treesRemote = Set.of();

    private AbilityNode processNodeJSONFile(JsonObject root) {
        return AbilityNode.CODEC.decode(JsonOps.INSTANCE, root)
                .getOrThrow(false, error -> { throw new RuntimeException(error); }).getFirst();
    }

    private AbilityTree processTreeJSONFile(JsonObject root) {
        return AbilityTree.CODEC.decode(JsonOps.INSTANCE, root)
                .getOrThrow(false, error -> { throw new RuntimeException(error); }).getFirst();
    }

    @Override
    @NotNull
    public Pair<Map<ResourceLocation, AbilityNode>, Set<AbilityTree>> prepare(ResourceManager resources, @Nonnull ProfilerFiller profiler) {
        var nodes = ResourceUtil.processJSONResources(new HashMap<ResourceLocation, AbilityNode>(), resources, "ability/nodes", (list, filename, id, json) -> {
            var abilityNode = processNodeJSONFile(json);
            abilityNode.setNodeLocation(id);
            list.put(id, abilityNode);
        }, (exception, filename) -> Changed.LOGGER.error("Failed to load ability node from \"{}\" : {}", filename, exception));

        var trees = ResourceUtil.processJSONResources(new HashSet<AbilityTree>(), resources, "ability/trees", (list, filename, id, json) -> {
            var abilityTree = processTreeJSONFile(json);
            abilityTree.setTreeLocation(id);
            list.add(abilityTree);
        }, (exception, filename) -> Changed.LOGGER.error("Failed to load ability tree from \"{}\" : {}", filename, exception));

        return Pair.of(nodes, trees);
    }

    @Override
    protected void apply(@NotNull Pair<Map<ResourceLocation, AbilityNode>, Set<AbilityTree>> output, @NotNull ResourceManager resources, @NotNull ProfilerFiller profiler) {
        nodes = output.getFirst();
        trees = output.getSecond();

        trees.forEach(tree -> {
            tree.buildTree(nodes);
        });
    }

    public Set<AbilityTree> getTrees() {
        return trees;
    }

    public Set<AbilityTree> getRemoteTrees() {
        return treesRemote;
    }

    public static class SyncPacket implements ChangedPacket {
        private final Map<ResourceLocation, AbilityNode> nodes;
        private final Set<AbilityTree> trees;
        private final @Nullable AbilityTreeSyncInstancePacket receiverAbilityTree;

        protected SyncPacket(Map<ResourceLocation, AbilityNode> nodes, Set<AbilityTree> trees) {
            this.nodes = nodes;
            this.trees = trees;
            this.receiverAbilityTree = null;
        }

        protected SyncPacket(Map<ResourceLocation, AbilityNode> nodes, Set<AbilityTree> trees, @Nullable AbilityTreeSyncInstancePacket receiverAbilityTree) {
            this.nodes = nodes;
            this.trees = trees;
            this.receiverAbilityTree = receiverAbilityTree;
        }

        public SyncPacket(FriendlyByteBuf buffer) {
            {
                var tag = buffer.readAnySizeNbt();
                this.nodes = new HashMap<>();
                tag.getAllKeys().forEach(key -> {
                    ResourceLocation id = ResourceLocation.parse(key);
                    var tree = AbilityNode.CODEC.decode(NbtOps.INSTANCE, tag.get(key)).getOrThrow(false, onError -> {}).getFirst();
                    tree.setNodeLocation(id);
                    this.nodes.put(id, tree);
                });
            }

            {
                var tag = buffer.readAnySizeNbt();
                this.trees = new HashSet<>();
                tag.getAllKeys().forEach(key -> {
                    var tree = AbilityTree.CODEC.decode(NbtOps.INSTANCE, tag.get(key)).getOrThrow(false, onError -> {
                    }).getFirst();
                    tree.setTreeLocation(ResourceLocation.parse(key));
                    this.trees.add(tree);
                });
            }
            this.receiverAbilityTree = buffer.readOptional(AbilityTreeSyncInstancePacket::new).orElse(null);
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            {
                CompoundTag tag = new CompoundTag();
                nodes.forEach((id, tree) -> {
                    var nbt = AbilityNode.CODEC.encodeStart(NbtOps.INSTANCE, tree).getOrThrow(false, onError -> {});
                    tag.put(id.toString(), nbt);
                });
                buffer.writeNbt(tag);
            }

            {
                CompoundTag tag = new CompoundTag();
                trees.forEach(tree -> {
                    var nbt = AbilityTree.CODEC.encodeStart(NbtOps.INSTANCE, tree).getOrThrow(false, onError -> {
                    });
                    tag.put(tree.getTreeLocation().toString(), nbt);
                });
                buffer.writeNbt(tag);
            }
            buffer.writeOptional(Optional.ofNullable(receiverAbilityTree), (optBuffer, value) -> value.write(optBuffer));
        }

        @Override
        public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture< Level > levelFuture, Executor sidedExecutor) {
            if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                this.trees.forEach(tree -> {
                    tree.markRemote();
                    tree.buildTree(this.nodes);
                });

                AbilityTrees.INSTANCE.treesRemote = this.trees;

                context.setPacketHandled(true);

                if (this.receiverAbilityTree != null)
                    return this.receiverAbilityTree.handle(context, levelFuture, sidedExecutor);

                return CompletableFuture.completedFuture(null);
            }

            return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.CLIENT));
        }
    }

    public SyncPacket syncPacket() {
        return new SyncPacket(this.nodes, this.trees);
    }

    public SyncPacket syncPacket(ServerPlayer receiver) {
        return new SyncPacket(
                this.nodes,
                this.trees,
                new AbilityTreeSyncInstancePacket(AbilityTreeInstance.getForPlayer(receiver))
        );
    }
}
