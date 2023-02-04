package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.network.packet.SyncSwitchPacket;
import net.ltxprogrammer.changed.world.inventory.InfuserMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.PacketDistributor;

public class InfuserScreen extends AbstractContainerScreen<InfuserMenu> implements RecipeUpdateListener {
    public static class Switch extends AbstractButton {
        private static final int TEXT_COLOR = 14737632;
        private boolean toggle;
        private final boolean showLabel;
        public final AbstractContainerScreen<?> containerScreen;
        private final ResourceLocation name;

        public void onPress() {
            this.toggle = !this.toggle;

            Changed.PACKET_HANDLER.send(PacketDistributor.SERVER.noArg(), SyncSwitchPacket.of(this));
        }

        public boolean selected() {
            return this.toggle;
        }

        private final ResourceLocation texture0;
        private final ResourceLocation texture1;

        public Switch(AbstractContainerScreen<?> container, ResourceLocation name,
                int p_93826_, int p_93827_, int p_93828_, int p_93829_, Component p_93830_, boolean p_93831_, ResourceLocation texture0, ResourceLocation texture1) {
            super(p_93826_, p_93827_, p_93828_, p_93829_, p_93830_);
            this.name = name;
            this.containerScreen = container;
            this.texture0 = texture0;
            this.texture1 = texture1;
            this.toggle = p_93831_;
            this.showLabel = true;
        }

        public void renderButton(PoseStack p_93843_, int p_93844_, int p_93845_, float p_93846_) {
            Minecraft minecraft = Minecraft.getInstance();
            RenderSystem.setShaderTexture(0, toggle ? texture1 : texture0);
            RenderSystem.enableDepthTest();
            Font font = minecraft.font;
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            blit(p_93843_, this.x, this.y, this.isFocused() ? 20.0F : 0.0F, this.toggle ? 20.0F : 0.0F, this.width, this.height, this.width, this.height);
            this.renderBg(p_93843_, minecraft, p_93844_, p_93845_);
            if (this.showLabel) {
                drawString(p_93843_, font, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, 14737632 | Mth.ceil(this.alpha * 255.0F) << 24);
            }
        }

        @Override
        public void updateNarration(NarrationElementOutput p_169152_) {
            p_169152_.add(NarratedElementType.TITLE, this.createNarrationMessage());
            if (this.active) {
                if (this.isFocused()) {
                    p_169152_.add(NarratedElementType.USAGE, new TranslatableComponent("narration.checkbox.usage.focused"));
                } else {
                    p_169152_.add(NarratedElementType.USAGE, new TranslatableComponent("narration.checkbox.usage.hovered"));
                }
            }
        }

        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public ResourceLocation getName() {
            return name;
        }
    }

    private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
    private final RecipeBookComponent recipeBookComponent = new RecipeBookComponent();
    private boolean widthTooNarrow;
    private Switch maleFemaleSwitch;

    public InfuserScreen(InfuserMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    protected void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
        this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
        this.addRenderableWidget(new ImageButton(this.leftPos + 23, this.height / 2 - 25, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (p_98484_) -> {
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
            ((ImageButton)p_98484_).setPosition(this.leftPos + 23, this.height / 2 - 25);
            maleFemaleSwitch.setPosition(this.leftPos + 131, this.topPos + 61);
        }));
        this.addWidget(this.recipeBookComponent);
        this.setInitialFocus(this.recipeBookComponent);
        this.titleLabelX = 29;

        maleFemaleSwitch = new Switch(this, Changed.modResource("male_female_switch"), this.leftPos + 131, this.topPos + 61, 20, 10, TextComponent.EMPTY, false,
                Changed.modResource("textures/gui/gender_switch_male.png"), Changed.modResource("textures/gui/gender_switch_female.png"));
        this.addRenderableWidget(maleFemaleSwitch);
    }

    private static final ResourceLocation texture = Changed.modResource("textures/gui/infuser.png");

    @Override
    public void render(PoseStack p_98479_, int p_98480_, int p_98481_, float p_98482_) {
        this.renderBackground(p_98479_);
        if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
            this.renderBg(p_98479_, p_98482_, p_98480_, p_98481_);
            this.recipeBookComponent.render(p_98479_, p_98480_, p_98481_, p_98482_);
        } else {
            this.recipeBookComponent.render(p_98479_, p_98480_, p_98481_, p_98482_);
            super.render(p_98479_, p_98480_, p_98481_, p_98482_);
            this.recipeBookComponent.renderGhostRecipe(p_98479_, this.leftPos, this.topPos, true, p_98482_);
        }

        this.renderTooltip(p_98479_, p_98480_, p_98481_);
        this.recipeBookComponent.renderTooltip(p_98479_, this.leftPos, this.topPos, p_98480_, p_98481_);
    }

    @Override
    protected void renderBg(PoseStack ms, float partialTicks, int gx, int gy) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShaderTexture(0, texture);
        this.blit(ms, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        RenderSystem.disableBlend();
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) {
            this.minecraft.player.closeContainer();
            return true;
        }

        return super.keyPressed(key, b, c);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.recipeBookComponent.tick();
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
    }

    @Override
    public void onClose() {
        super.onClose();
        Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(false);
    }
    @Override
    public void recipesUpdated() {
        this.recipeBookComponent.recipesUpdated();
    }

    @Override
    public RecipeBookComponent getRecipeBookComponent() {
        return this.recipeBookComponent;
    }
}
