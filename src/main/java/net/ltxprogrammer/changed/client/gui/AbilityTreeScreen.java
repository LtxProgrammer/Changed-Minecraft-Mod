package net.ltxprogrammer.changed.client.gui;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.tree.AbilityTree;
import net.ltxprogrammer.changed.ability.tree.AbilityTreeInstance;
import net.ltxprogrammer.changed.ability.tree.AbilityNode;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.network.packet.AbilityTreeMenuPacket;
import net.ltxprogrammer.changed.util.Cacheable;
import net.ltxprogrammer.changed.world.inventory.AbilityTreeMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.event.ContainerScreenEvent;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AbilityTreeScreen extends AbstractContainerScreen<AbilityTreeMenu> {
    protected static void drawLine(BufferBuilder bufferBuilder, Matrix4f matrix4f, float x0, float x1, float y0, float y1, float width, float red, float green, float blue, float alpha) {
        float dx = x0 - x1;
        float dy = y0 - y1;

        if (Mth.abs(dx) < Mth.EPSILON && Mth.abs(dy) < Mth.EPSILON)
            return;

        float mag = (float)Mth.length(dx, dy);
        float dxOffset = dx / mag * (width * 0.5f);
        float dyOffset = dy / mag * (width * 0.5f);

        bufferBuilder.vertex(matrix4f, x0 + dyOffset, y0 - dxOffset, (float)0).color(red, green, blue, alpha).endVertex();
        bufferBuilder.vertex(matrix4f, x1 + dyOffset, y1 - dxOffset, (float)0).color(red, green, blue, alpha).endVertex();
        bufferBuilder.vertex(matrix4f, x1 - dyOffset, y1 + dxOffset, (float)0).color(red, green, blue, alpha).endVertex();
        bufferBuilder.vertex(matrix4f, x0 - dyOffset, y0 + dxOffset, (float)0).color(red, green, blue, alpha).endVertex();
    }

    public static class TreeButton extends AbstractButton {
        protected final TransfurVariant<?> variant;
        protected final AbilityTree tree;
        protected final Cacheable<List<Component>> tooltip;
        protected final @Nullable TreeButton parent;

        public final int initX, initY;

        public TreeButton(TransfurVariant<?> variant,
                          AbilityTree tree,
                          int x, int y, int width, int height, Component message,
                          @Nullable TreeButton parent) {
            super(x, y, width, height, message);
            this.variant = variant;
            this.tree = tree;
            this.parent = parent;

            this.initX = x;
            this.initY = y;

            this.tooltip = Cacheable.of(this::createTooltip);
        }

        public List<Component> createTooltip() {
            var tooltipBuilder = ImmutableList.<Component>builder();
            tooltipBuilder.add(tree.getTitle());
            tree.getFlavorText().ifPresent(tooltipBuilder::add);
            return tooltipBuilder.build();
        }

        @Override
        public void onPress() {
            // Passthrough
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput p_259858_) {

        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mx, int my, float partialTicks) {
            Minecraft minecraft = Minecraft.getInstance();
            super.renderWidget(graphics, mx, my, partialTicks);

            // TODO Make background, and tint to variant color
            // TODO Either make icons, or have tree specify item
            /* TODO Special render conditions for:
            - Distant node (parent node is not connected to an unlocked node)
                - Darkened, icon and details hidden
            - Pre-requisites locked (parent node and conditions)
                - Darkened, icon and details visible
            - Pre-requisites met, but cannot afford
                - Regular color, icon and details visible: cost tinted red
            - Pre-requisites met and can afford
                - Regular color, icon and details visible
            - Node unlocked
                - Highlighted color, icon and details visible
            */

            // Dev idea: animate node elements moving around in place slightly for biological "cell"
        }

        public void renderGraphLine(GuiGraphics graphics, BufferBuilder bufferBuilder, float partialTicks) {
            // TODO render line to parent
            if (parent == null)
                return;
            Matrix4f matrix4f = graphics.pose().last().pose();

            float alpha = 1.0f;
            float red = 0.4f;
            float green = 0.4f;
            float blue = 0.4f;

            int centerXthis = this.getX() + (this.getWidth() / 2);
            int centerYthis = this.getY() + (this.getHeight() / 2);
            int centerXparent = parent.getX() + (parent.getWidth() / 2);
            int centerYparent = parent.getY() + (parent.getHeight() / 2);

            drawLine(bufferBuilder, matrix4f, centerXthis, centerXparent, centerYthis, centerYparent, 1.25f, red, green, blue, alpha);
        }
    }

    public static class NodeButton extends TreeButton {
        protected final AbilityTreeInstance.AccountedTree accountedTree;
        private final AbilityNode node;
        private final ResourceLocation nodeName;

        public NodeButton(TransfurVariant<?> variant,
                          AbilityTreeInstance.AccountedTree accountedTree,
                          AbilityNode node,
                          int x, int y, int width, int height, Component message,
                          @Nullable TreeButton parent) {
            super(variant, accountedTree.getTree(), x, y, width, height, message, parent);
            this.accountedTree = accountedTree;
            this.node = node;
            this.nodeName = node.getNodeLocation();
        }

        @Override
        public List<Component> createTooltip() {
            var tooltipBuilder = ImmutableList.<Component>builder();
            tooltipBuilder.add(node.getTitle());
            tooltipBuilder.add(Component.literal("Generated Cost").withStyle(ChatFormatting.GRAY));
            tooltipBuilder.add(Component.literal("Generated Description").withStyle(ChatFormatting.BLUE));
            node.getFlavorText().ifPresent(tooltipBuilder::add);
            return tooltipBuilder.build();
        }

        protected boolean canPurchase() {
            if (!accountedTree.hasPrerequisites(variant, nodeName))
                return false;
            if (!accountedTree.canAfford(variant, nodeName))
                return false;
            return true;
        }

        protected boolean isOwned() {
            return accountedTree.getNodeState(variant, node).map(AbilityTreeInstance.NodeState::unlocked).orElse(false);
        }

        @Override
        public void onPress() {
            if (canPurchase()) {
                Changed.PACKET_HANDLER.sendToServer(new AbilityTreeMenuPacket(AbilityTreeMenuPacket.Opcode.MAKE_PURCHASE,
                        Optional.of(accountedTree.getTree().getTreeLocation()),
                        Optional.of(nodeName),
                        Optional.empty()));
            }
        }
    }

    private interface GraphLayoutComputer {
        int apply(int layerIndex, int nodesInSection, int nodeIndex, int treesInGraph, int treeIndex);
    }

    private interface GraphLayerRenderer {
        void drawLayerLines(GuiGraphics graphics, BufferBuilder bufferBuilder, int centerX, int centerY, int layerCount);
    }

    private static final double RADIAL_LAYOUT_GAP = 48d;

    public enum GraphLayout implements GraphLayerRenderer {
        RADIAL((layerIndex, nodesInSection, nodeIndex, treesInLayer, treeIndex) -> { // Compute X
            double treeSection = ((treeIndex - 0.5) / (double)treesInLayer) * Math.PI * 2d;

            double radians = ((double)(nodeIndex + 1) / (double)(nodesInSection + 1)) * Math.PI * 2d / (double)treesInLayer;
            return (int)((layerIndex + 1) * RADIAL_LAYOUT_GAP * Math.sin(radians + treeSection));
        }, (layerIndex, nodesInSection, nodeIndex, treesInLayer, treeIndex) -> { // Compute Y
            double treeSection = ((treeIndex - 0.5) / (double)treesInLayer) * Math.PI * 2d;

            double radians = ((double)(nodeIndex + 1) / (double)(nodesInSection + 1)) * Math.PI * 2d / (double)treesInLayer;
            return (int)((layerIndex + 1) * RADIAL_LAYOUT_GAP * -Math.cos(radians + treeSection));
        }, (graphics, bufferBuilder, centerX, centerY, layerCount) -> {
            Matrix4f matrix4f = graphics.pose().last().pose();

            float alpha = 1.0f;
            float red = 0.2f;
            float green = 0.2f;
            float blue = 0.2f;

            for (int layerIndex = 0; layerIndex < layerCount; ++layerIndex) {
                for (int seg = 0; seg < 32; ++seg) {
                    double r0 = (seg / 32d * Math.PI * 2d);
                    double r1 = ((seg + 1) / 32d * Math.PI * 2d);

                    float x0 = (float)Math.sin(r0) * ((layerIndex + 1) * (float)RADIAL_LAYOUT_GAP) + centerX;
                    float y0 = (float)Math.cos(r0) * ((layerIndex + 1) * (float)RADIAL_LAYOUT_GAP) + centerY;
                    float x1 = (float)Math.sin(r1) * ((layerIndex + 1) * (float)RADIAL_LAYOUT_GAP) + centerX;
                    float y1 = (float)Math.cos(r1) * ((layerIndex + 1) * (float)RADIAL_LAYOUT_GAP) + centerY;

                    drawLine(bufferBuilder, matrix4f, x0, x1, y0, y1, 0.75f, red, green, blue, alpha);
                }
            }
        }),
        LATERAL((layerIndex, nodesInSection, nodeIndex, treesInLayer, treeIndex) -> { // Compute X
            return layerIndex * 32;
        }, (layerIndex, nodesInSection, nodeIndex, treesInLayer, treeIndex) -> { // Compute Y
            double treeSection = ((double)treeIndex / (double)treesInLayer);
            double nodePlacement = (((double)nodeIndex / (double)nodesInSection / (double)treesInLayer) * 64d) - 32;

            return (int)(treeSection + nodePlacement);
        }, (graphics, bufferBuilder, centerX, centerY, layerCount) -> {

        });

        private final GraphLayoutComputer nodeXComputer;
        private final GraphLayoutComputer nodeYComputer;
        private final GraphLayerRenderer graphLayerRenderer;

        GraphLayout(GraphLayoutComputer nodeXComputer, GraphLayoutComputer nodeYComputer, GraphLayerRenderer graphLayerRenderer) {
            this.nodeXComputer = nodeXComputer;
            this.nodeYComputer = nodeYComputer;
            this.graphLayerRenderer = graphLayerRenderer;
        }

        public int getNodeX(int layerIndex, int nodesInLayer, int nodeIndex, int treesInGraph, int treeIndex) {
            return nodeXComputer.apply(layerIndex, nodesInLayer, nodeIndex, treesInGraph, treeIndex);
        }

        public int getNodeY(int layerIndex, int nodesInLayer, int nodeIndex, int treesInGraph, int treeIndex) {
            return nodeYComputer.apply(layerIndex, nodesInLayer, nodeIndex, treesInGraph, treeIndex);
        }

        public void drawLayerLines(GuiGraphics graphics, BufferBuilder bufferBuilder, int centerX, int centerY, int layerCount) {
            graphLayerRenderer.drawLayerLines(graphics, bufferBuilder, centerX, centerY, layerCount);
        }
    }

    private Map<Either<AbilityTree, AbilityNode>, TreeButton> buildNodeGraph(TransfurVariant<?> variant, GraphLayout layout,
                                                                             @Nullable AtomicInteger layerCountOut) {
        List<AbilityTree> orderedTrees = new ArrayList<>();
        List<Pair<AbilityTreeInstance.AccountedTree, AbilityNode>> graphRoots = new ArrayList<>();
        List<Multimap<AbilityNode, Pair<AbilityTreeInstance.AccountedTree, AbilityNode>>> graph = new ArrayList<>();
        AtomicReference<AbilityTreeInstance.AccountedTree> currentTree = new AtomicReference<>(null);
        AbilityTree.NodeVisitor graphBuilder = (node, parent, depth) -> {
            if (depth == 0)
                graphRoots.add(Pair.of(currentTree.get(), node));
            else {
                while (graph.size() < depth) {
                    graph.add(HashMultimap.create());
                }

                graph.get(depth - 1).put(parent, Pair.of(currentTree.get(), node));
            }
        };

        var accountedTrees = menu.abilityTree.getTrees(variant);
        accountedTrees.forEach(accountedTree -> {
            orderedTrees.add(accountedTree.getTree());
            currentTree.set(accountedTree);
            accountedTree.getTree().visitNodes(graphBuilder);
        });

        List<List<Pair<Pair<AbilityTreeInstance.AccountedTree, AbilityNode>, AbilityNode>>> orderedGraph = new ArrayList<>();
        if (!graphRoots.isEmpty()) {
            orderedGraph.add(new ArrayList<>());
            graphRoots.forEach(node -> {
                orderedGraph.get(0).add(Pair.of(node, null));
            });
        }

        for (int layerIndex = 0; layerIndex < graph.size(); ++ layerIndex) {
            var layer = graph.get(layerIndex);
            var parentLayer = orderedGraph.get(layerIndex);
            List<Pair<Pair<AbilityTreeInstance.AccountedTree, AbilityNode>, AbilityNode>> orderedLayer = new ArrayList<>();

            for (var parentNode : parentLayer) {
                var parentTreeAndNode = parentNode.getFirst();
                var parentChildren = layer.get(parentTreeAndNode.getSecond());
                parentChildren.forEach(node -> {
                    orderedLayer.add(Pair.of(node, parentTreeAndNode.getSecond()));
                });
            }

            orderedGraph.add(orderedLayer);
        }

        Map<Either<AbilityTree, AbilityNode>, TreeButton> nodeButtons = new HashMap<>();

        for (int treeIndex = 0; treeIndex < orderedTrees.size(); ++treeIndex) {
            var tree = orderedTrees.get(treeIndex);

            nodeButtons.put(Either.left(tree),
                    new TreeButton(variant,
                            tree,
                            layout.getNodeX(0, 1, 0, orderedTrees.size(), treeIndex),
                            layout.getNodeY(0, 1, 0, orderedTrees.size(), treeIndex),
                            24,
                            24,
                            tree.getTitle(),
                            null));
        }

        for (int layerIndex = 0; layerIndex < orderedGraph.size(); ++layerIndex) {
            var orderedLayer = orderedGraph.get(layerIndex);

            for (int nodeIndex = 0; nodeIndex < orderedLayer.size(); ++nodeIndex) {
                var nodeAndParent = orderedLayer.get(nodeIndex);
                var treeAndNode = nodeAndParent.getFirst();
                var tree = treeAndNode.getFirst();
                var node = treeAndNode.getSecond();
                var parent = nodeAndParent.getSecond();
                int treeIndex = orderedTrees.indexOf(tree.getTree());

                int nodeIndexInTree = 0;
                int nodesInTreeAtLayer = 0;

                for (var pair : orderedLayer) {
                    if (pair == nodeAndParent) {
                        nodeIndexInTree = nodesInTreeAtLayer;
                    }

                    if (pair.getFirst().getFirst() == tree) {
                        nodesInTreeAtLayer++;
                    }
                }

                nodeButtons.put(Either.right(node),
                        new NodeButton(variant,
                                tree,
                                node,
                                layout.getNodeX(layerIndex + 1, nodesInTreeAtLayer, nodeIndexInTree, orderedTrees.size(), treeIndex),
                                layout.getNodeY(layerIndex + 1, nodesInTreeAtLayer, nodeIndexInTree, orderedTrees.size(), treeIndex),
                                24,
                                24,
                                node.getTitle(),
                                parent == null ?
                                        nodeButtons.get(Either.left(tree.getTree())) :
                                        nodeButtons.get(Either.right(parent))));
            }
        }

        if (layerCountOut != null)
            layerCountOut.set(orderedGraph.size() + 1);

        return nodeButtons;
    }

    private final GraphLayout graphLayout;
    private final Map<Either<AbilityTree, AbilityNode>, TreeButton> nodeGraph;
    private final int layerCount;
    private double panX = 0d, panY = 0d;
    private float zoom = 1.0f;

    public AbilityTreeScreen(AbilityTreeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.graphLayout = GraphLayout.RADIAL;
        AtomicInteger layerCount = new AtomicInteger(0);
        this.nodeGraph = buildNodeGraph(menu.variant.getParent(), graphLayout, layerCount);
        this.nodeGraph.values().forEach(this::addRenderableWidget);
        this.layerCount = layerCount.getAcquire();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.nodeGraph.values().forEach(button -> {
            button.setX((int)(button.initX + centerX + panX - (button.getWidth() / 2)));
            button.setY((int)(button.initY + centerY + panY - (button.getHeight() / 2)));
        });

        Changed.postModEvent(new ContainerScreenEvent.Render.Background(this, graphics, mouseX, mouseY));
        this.renderBackground(graphics);
        Changed.postModEvent(new ContainerScreenEvent.Render.Foreground(this, graphics, mouseX, mouseY));
        this.renderBg(graphics, partialTicks, mouseX, mouseY);

        for (Renderable renderable : this.renderables) {
            renderable.render(graphics, mouseX, mouseY, partialTicks);
        }

        nodeGraph.values().stream().filter(AbstractWidget::isHovered).findAny().ifPresent(button -> {
            graphics.renderTooltip(minecraft.font, button.tooltip.get(), Optional.empty(), mouseX, mouseY);
        });

        graphics.setColor(1, 1, 1, 1);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mx, int my) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        graphLayout.drawLayerLines(graphics, bufferbuilder,
                (int)(centerX + panX),
                (int)(centerY + panY), layerCount);

        nodeGraph.values().forEach(button -> button.renderGraphLine(graphics, bufferbuilder, partialTicks));

        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_2) {
            panX += dx;
            panY += dy;
            return true;
        }
        return super.mouseDragged(x, y, button, dx, dy);
    }
}
