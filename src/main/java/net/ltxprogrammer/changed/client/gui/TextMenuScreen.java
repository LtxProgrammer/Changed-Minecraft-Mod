package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.world.inventory.TextMenu;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.List;

public abstract class TextMenuScreen extends AbstractContainerScreen<TextMenu> {
    private DisplayCache cache = DisplayCache.EMPTY;
    private final TextFieldHelper textEdit = new TextFieldHelper(
            () -> menu.textCopy,
            nText -> {
                menu.textCopy = nText;
                rebuildCache();
            },
            this::getClipboard,
            this::setClipboard,
            (nText) -> nText.length() < 1024 && this.font.wordWrapHeight(nText, getTextAreaWidth()) <= getTextAreaHeight()
    );
    private final Player player;
    public TextMenuScreen(TextMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = getBackgroundWidth();
        this.imageHeight = getBackgroundHeight();
        this.player = inventory.player;

        menu.setDirty(new CompoundTag()); // Query text
    }

    public static class DisplayCache {
        public final List<FormattedText> lines;

        public static final DisplayCache EMPTY = new DisplayCache(List.of());

        public DisplayCache(List<FormattedText> lines) {
            this.lines = lines;
        }
    }

    public abstract int getBackgroundWidth();
    public abstract int getBackgroundHeight();
    public abstract int getTextAreaWidth();
    public abstract int getTextAreaHeight();
    public abstract int getTextAreaX();
    public abstract int getTextAreaY();
    public int getTextColor() { return 0; }
    public abstract ResourceLocation getBackground();
    public abstract @Nullable Component getNoteTitle();

    private void setClipboard(String p_98148_) {
        if (this.minecraft != null) {
            TextFieldHelper.setClipboardContents(this.minecraft, p_98148_);
        }

    }

    private String getClipboard() {
        return this.minecraft != null ? TextFieldHelper.getClipboardContents(this.minecraft) : "";
    }

    protected void rebuildCache() {
        if (menu.textCopy.isEmpty()) {
            cache = DisplayCache.EMPTY;
            return;
        }

        var splitter = this.font.getSplitter();
        cache = new DisplayCache(splitter.splitLines(menu.textCopy, getTextAreaWidth(), Style.EMPTY));
    }

    @Override
    protected void renderBg(PoseStack pose, float partialTicks, int cursorX, int cursorY) {
        if (cache == DisplayCache.EMPTY)
            rebuildCache(); // Keep trying to rebuild until text is present

        this.renderBackground(pose);
        RenderSystem.setShaderTexture(0, getBackground());
        RenderSystem.setShaderColor(1, 1, 1, 1);
        blit(pose, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        int offset = getTextAreaY();
        int finalX = this.leftPos + getTextAreaX();
        int finalY = this.topPos + offset;
        for (var line : cache.lines) {
            finalX = this.font.draw(pose, line.getString(), this.leftPos + getTextAreaX(), this.topPos + offset, getTextColor());
            finalY = this.topPos + offset;
            offset += 9;
        }

        if (menu.textCopyLastReceived.isEmpty()) {
            this.font.draw(pose, "_", finalX, finalY, getTextColor());
        }
    }

    @Override
    protected void renderLabels(PoseStack pose, int cursorX, int cursorY) {
        var noteTitle = this.getNoteTitle();
        if (noteTitle != null)
            this.font.draw(pose, noteTitle, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
    }

    public boolean charTyped(char c, int i) {
        if (super.charTyped(c, i)) {
            return true;
        } else if (menu.textCopyLastReceived.isEmpty() && SharedConstants.isAllowedChatCharacter(c)) {
            this.textEdit.insertText(Character.toString(c));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean keyPressed(int key, int scancode, int mods) {
        if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
            if (!Screen.hasShiftDown()) {
                menu.setText(menu.textCopy);
                player.closeContainer();
            } else {
                textEdit.insertText("\n");
            }
            return true;
        } else if (key == GLFW.GLFW_KEY_BACKSPACE) {
            textEdit.removeCharsFromCursor(-1);
            return true;
        } else if (key == GLFW.GLFW_KEY_ESCAPE) {
            player.closeContainer();
            return true;
        }

        return false;
    }
}
