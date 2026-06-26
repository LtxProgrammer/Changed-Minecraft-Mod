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
import net.ltxprogrammer.changed.util.UniversalDist;
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
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
        public static final ResourceLocation WIDGETS = Changed.modResource("textures/gui/node_frames.png");

        protected final TransfurVariant<?> variant;
        protected final AbilityTreeInstance.AccountedTree accountedTree;
        protected final AbilityTree tree;
        protected final Cacheable<List<Component>> tooltip;
        protected final @Nullable TreeButton parent;

        public final int initX, initY;
        public float waveOffsetX = 0.0f;
        public float waveSpeedX = 0.0f;
        public float waveOffsetY = 0.0f;
        public float waveSpeedY = 0.0f;

        public NodeRenderState renderState = NodeRenderState.DISTANT;

        public enum BackgroundState {
            DARKENED,
            REGULAR,
            HIGHLIGHTED
        }

        public enum NodeRenderState {
            /// Distant node (parent node is not connected to an unlocked node). Darkened, icon and details hidden.
            DISTANT(BackgroundState.DARKENED, true, true, ChatFormatting.RED),
            /// Pre-requisites locked (parent node and conditions). Darkened, icon and details visible.
            PRE_REQ_LOCKED(BackgroundState.DARKENED, false, false, ChatFormatting.RED),
            /// Pre-requisites met, but cannot afford. Regular color, icon and details visible: cost tinted red
            PRE_REQ_MET(BackgroundState.REGULAR, false, false, null),
            /// Pre-requisites met and can afford. Regular color, icon and details visible
            CAN_ACQUIRE(BackgroundState.REGULAR, false, false, ChatFormatting.GREEN),
            /// Node unlocked. Highlighted color, icon and details visible
            UNLOCKED(BackgroundState.HIGHLIGHTED, false, false, ChatFormatting.GRAY);

            public final BackgroundState backgroundState;
            public final boolean hideIcon;
            public final boolean hideTooltipDetails;
            public final @Nullable ChatFormatting costFormatting;

            NodeRenderState(BackgroundState backgroundState, boolean hideIcon, boolean hideTooltipDetails, @Nullable ChatFormatting costFormatting) {
                this.backgroundState = backgroundState;
                this.hideIcon = hideIcon;
                this.hideTooltipDetails = hideTooltipDetails;
                this.costFormatting = costFormatting;
            }
        }

        public TreeButton(TransfurVariant<?> variant,
                          AbilityTreeInstance.AccountedTree accountedTree,
                          int x, int y, int width, int height, Component message,
                          @Nullable TreeButton parent, RandomSource random) {
            super(x, y, width, height, message);
            this.variant = variant;
            this.accountedTree = accountedTree;
            this.tree = accountedTree.getTree();
            this.parent = parent;

            this.initX = x;
            this.initY = y;

            this.tooltip = Cacheable.of(this::createTooltip);

            this.waveSpeedX = (random.nextFloat() + 0.25f) * (random.nextBoolean() ? 0.1f : -0.1f);
            this.waveSpeedY = (random.nextFloat() + 0.25f) * (random.nextBoolean() ? 0.1f : -0.1f);
            this.waveOffsetX = random.nextFloat() * 10.0f;
            this.waveOffsetY = random.nextFloat() * 10.0f;
        }

        public final void setPosition(int xPos, int yPos, float waveParameter) {
            this.setX(xPos + (int)(Mth.sin(waveParameter * waveSpeedX + waveOffsetX) * 2f));
            this.setY(yPos + (int)(Mth.cos(waveParameter * waveSpeedY + waveOffsetY) * 2f));
        }

        public final void checkRenderState() {
            var prev = renderState;
            renderState = determineRenderState();
            if (prev != renderState) {
                this.tooltip.clear();
            }
        }

        protected NodeRenderState determineRenderState() {
            if (accountedTree.hasAllNodes(variant))
                return NodeRenderState.UNLOCKED;
            return NodeRenderState.PRE_REQ_MET;
        }

        protected boolean isUnlocked() {
            return true;
        }

        public List<Component> createTooltip() {
            var tooltipBuilder = ImmutableList.<Component>builder();
            if (this.renderState == NodeRenderState.UNLOCKED) {
                tooltipBuilder.add(tree.getTitle().withStyle(ChatFormatting.LIGHT_PURPLE));
                tree.getFlavorCompletedText().ifPresent(tooltipBuilder::add);
            } else {
                tooltipBuilder.add(tree.getTitle());
                tree.getFlavorText().ifPresent(tooltipBuilder::add);
            }
            return tooltipBuilder.build();
        }

        @Override
        public void onPress() {
            // Passthrough
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput p_259858_) {

        }

        protected int getFrameY() {
            return 0;
        }

        protected int getFrameX() {
            return switch (this.renderState.backgroundState) {
                case HIGHLIGHTED -> 24;
                case DARKENED -> 48;
                default -> 0;
            };
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mx, int my, float partialTicks) {
            this.active = this.renderState == NodeRenderState.CAN_ACQUIRE;

            graphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            graphics.blit(WIDGETS, this.getX(), this.getY(), this.getFrameX(), this.getFrameY(), this.getWidth(), this.getHeight(), 96, 96);
            if (this.isHovered)
                graphics.blit(WIDGETS, this.getX(), this.getY(), 72, this.getFrameY(), this.getWidth(), this.getHeight(), 96, 96);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
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
        public static final ResourceLocation DISTANT_NODE_ICON = Changed.modResource("textures/gui/nodes/distant.png");
        public static final ResourceLocation NUMERALS = Changed.modResource("textures/gui/roman_numerals.png");
        protected static final Component DISTANT_NODE_TEXT = Component.literal("\"")
                .append(Component.translatable("text.changed.ability_tree.distant_node"))
                .append(Component.literal("\""))
                .withStyle(Style.EMPTY
                        .withColor(ChatFormatting.GRAY)
                        .withItalic(true));

        private final AbilityNode node;
        private final ResourceLocation nodeName;

        public NodeButton(TransfurVariant<?> variant,
                          AbilityTreeInstance.AccountedTree accountedTree,
                          AbilityNode node,
                          int x, int y, int width, int height, Component message,
                          @Nullable TreeButton parent, RandomSource random) {
            super(variant, accountedTree, x, y, width, height, message, parent, random);
            this.node = node;
            this.nodeName = node.getNodeLocation();
        }

        @Override
        protected NodeRenderState determineRenderState() {
            if (isUnlocked())
                return NodeRenderState.UNLOCKED;
            if (parent != null && !parent.isUnlocked())
                return NodeRenderState.DISTANT;
            if (!accountedTree.hasPrerequisites(variant, nodeName))
                return NodeRenderState.PRE_REQ_LOCKED;
            if (!accountedTree.canAfford(UniversalDist.getLocalPlayer(), variant, nodeName))
                return NodeRenderState.PRE_REQ_MET;
            return NodeRenderState.CAN_ACQUIRE;
        }

        @Override
        public List<Component> createTooltip() {
            var tooltipBuilder = ImmutableList.<Component>builder();

            if (renderState.hideTooltipDetails) {
                tooltipBuilder.add(DISTANT_NODE_TEXT);
            } else {
                tooltipBuilder.add(node.getTitle().withStyle(node.displayInfo.frameType().titleColor));
                accountedTree.getEffectivePriceText(UniversalDist.getLocalPlayer(), variant, nodeName, tooltipBuilder::add, renderState.costFormatting);
                node.buildDescription(tooltipBuilder::add);
                if (renderState == NodeRenderState.UNLOCKED)
                    node.getFlavorText().ifPresent(tooltipBuilder::add);
            }

            return tooltipBuilder.build();
        }

        protected boolean canPurchase() {
            if (!accountedTree.hasPrerequisites(variant, nodeName))
                return false;
            if (!accountedTree.canAfford(UniversalDist.getLocalPlayer(), variant, nodeName))
                return false;
            return true;
        }

        @Override
        protected boolean isUnlocked() {
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

        @Override
        protected int getFrameY() {
            return node.displayInfo.frameType().yPos;
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mx, int my, float partialTicks) {
            super.renderWidget(graphics, mx, my, partialTicks);

            if (this.renderState.hideIcon) {
                graphics.blit(DISTANT_NODE_ICON, this.getX() + 4, this.getY() + 4, 0, 0, 16, 16, 16, 16);
                return;
            }

            node.displayInfo.icon().ifLeft(iconLocation -> {
                int dWidth = this.getWidth() - node.displayInfo.iconWidth();
                int dHeight = this.getHeight() - node.displayInfo.iconHeight();
                graphics.blit(iconLocation, this.getX() + (dWidth / 2), this.getY() + (dHeight / 2),
                        0, 0,
                        node.displayInfo.iconWidth(), node.displayInfo.iconHeight(),
                        node.displayInfo.iconWidth(), node.displayInfo.iconHeight());
            }).ifRight(itemStack -> {
                graphics.renderFakeItem(itemStack, this.getX() + 4, this.getY() + 4);
            });

            if (node.displayInfo.numeral().xOffset >= 0) {
                graphics.pose().translate(0.0f, 0.0f, 200.0f);
                graphics.setColor(0.25F, 0.25F, 0.25F, 1.0f);
                graphics.blit(NUMERALS, this.getX() + 3, this.getY() + 3,
                        node.displayInfo.numeral().xOffset, node.displayInfo.numeral().yOffset,
                        16, 16,
                        64, 64);
                graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                graphics.blit(NUMERALS, this.getX() + 2, this.getY() + 2,
                        node.displayInfo.numeral().xOffset, node.displayInfo.numeral().yOffset,
                        16, 16,
                        64, 64);
                graphics.pose().translate(0.0f, 0.0f, -200.0f);
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
                                                                             @Nullable AtomicInteger layerCountOut, RandomSource random) {
        List<AbilityTree> orderedTrees = new ArrayList<>();
        Map<AbilityTree, AbilityTreeInstance.AccountedTree> treeToAccountedTree = new HashMap<>();
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
            if (accountedTree.getTree().isHidden())
                return;

            treeToAccountedTree.put(accountedTree.getTree(), accountedTree);
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
            var accountedTree = treeToAccountedTree.get(tree);

            nodeButtons.put(Either.left(tree),
                    new TreeButton(variant,
                            accountedTree,
                            layout.getNodeX(0, 1, 0, orderedTrees.size(), treeIndex),
                            layout.getNodeY(0, 1, 0, orderedTrees.size(), treeIndex),
                            24,
                            24,
                            tree.getTitle(),
                            null,
                            random));
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
                                        nodeButtons.get(Either.right(parent)),
                                random));
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
    private int tickCount = 0;

    public AbilityTreeScreen(AbilityTreeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.graphLayout = GraphLayout.RADIAL;
        AtomicInteger layerCount = new AtomicInteger(0);
        this.nodeGraph = buildNodeGraph(menu.variant.getParent(), graphLayout, layerCount, inventory.player.getRandom());
        this.nodeGraph.values().forEach(this::addRenderableWidget);
        this.layerCount = layerCount.getAcquire();
        this.imageWidth = this.width;
        this.imageHeight = this.height;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        tickCount++;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.nodeGraph.values().forEach(button -> {
            button.setPosition(
                    (int)(button.initX + centerX + panX - (button.getWidth() / 2)),
                    (int)(button.initY + centerY + panY - (button.getHeight() / 2)),
                    tickCount + partialTicks
            );
            button.checkRenderState();
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
