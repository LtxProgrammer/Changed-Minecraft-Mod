package net.ltxprogrammer.changed.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.ltxprogrammer.changed.world.inventory.TextMenu;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public abstract class TextMenuScreen<T extends TextMenu> extends Screen implements MenuAccess<T> {
    private DisplayCache displayCache = DisplayCache.EMPTY;
    private Button doneButton;
    private Button doneReadingButton;
    private Button cancelButton;

    protected final T menu;
    protected int imageWidth, imageHeight;
    protected int leftPos, topPos;
    protected int titleLabelX;
    protected int titleLabelY;

    private final TextFieldHelper textEdit;

    private final Player player;
    public TextMenuScreen(T menu, Player player, Component title) {
        super(title);
        this.menu = menu;
        this.imageWidth = getBackgroundWidth();
        this.imageHeight = getBackgroundHeight();
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.player = player;

        textEdit = new TextFieldHelper(
                () -> menu.textCopy,
                nText -> {
                    menu.textCopy = nText;
                    rebuildDisplayCache();
                },
                this::getClipboard,
                this::setClipboard,
                (nText) -> nText.length() < 1024 && this.font.wordWrapHeight(nText, getTextAreaWidth()) <= getTextAreaHeight()
        );
    }

    @Override
    public T getMenu() {
        return menu;
    }

    @Override
    protected void init() {
        super.init();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        this.doneButton = this.addRenderableWidget(new Button(this.width / 2 - 100, this.topPos + getBackgroundHeight() + 10, 98, 20, CommonComponents.GUI_DONE, (button) -> {
            this.saveChanges(true);
            this.onClose();
        }));
        this.cancelButton = this.addRenderableWidget(new Button(this.width / 2 + 2, this.topPos + getBackgroundHeight() + 10, 98, 20, CommonComponents.GUI_CANCEL, (button) -> {
            this.saveChanges(false);
            this.onClose();
        }));


        this.doneReadingButton = this.addRenderableWidget(new Button(this.width / 2 - 49, this.topPos + getBackgroundHeight() + 10, 98, 20, CommonComponents.GUI_DONE, (button) -> {
            this.saveChanges(false);
            this.onClose();
        }));

        if (canEdit()) {
            doneButton.visible = true;
            cancelButton.visible = true;

            doneReadingButton.visible = false;
        } else {
            doneButton.visible = false;
            cancelButton.visible = false;

            doneReadingButton.visible = true;
        }
    }

    public abstract int getBackgroundWidth();
    public abstract int getBackgroundHeight();
    public abstract int getTextAreaWidth();
    public abstract int getTextAreaHeight();
    public abstract int getTextAreaX();
    public abstract int getTextAreaY();
    public int getTextColor() { return -16777216; }
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

    private TextMenuScreen.DisplayCache getDisplayCache() {
        if (this.displayCache == null || this.displayCache == DisplayCache.EMPTY) {
            this.displayCache = this.rebuildDisplayCache();
        }

        return this.displayCache;
    }

    private void clearDisplayCache() {
        this.displayCache = null;
    }

    static int findLineFromPos(int[] lines, int index) {
        int i = Arrays.binarySearch(lines, index);
        return i < 0 ? -(i + 2) : i;
    }

    private TextMenuScreen.DisplayCache rebuildDisplayCache() {
        String s = menu.textCopy;
        if (s.isEmpty()) {
            return TextMenuScreen.DisplayCache.EMPTY;
        } else {
            int i = this.textEdit.getCursorPos();
            int j = this.textEdit.getSelectionPos();
            IntList intlist = new IntArrayList();
            List<TextMenuScreen.LineInfo> list = Lists.newArrayList();
            MutableInt mutableint = new MutableInt();
            MutableBoolean mutableboolean = new MutableBoolean();
            StringSplitter stringsplitter = this.font.getSplitter();
            stringsplitter.splitLines(s, getTextAreaWidth(), Style.EMPTY, true, (p_98132_, p_98133_, p_98134_) -> {
                int k3 = mutableint.getAndIncrement();
                String s2 = s.substring(p_98133_, p_98134_);
                mutableboolean.setValue(s2.endsWith("\n"));
                String s3 = StringUtils.stripEnd(s2, " \n");
                int l3 = k3 * 9;
                TextMenuScreen.Pos2i pos2i1 = this.convertLocalToScreen(new TextMenuScreen.Pos2i(0, l3));
                intlist.add(p_98133_);
                list.add(new TextMenuScreen.LineInfo(p_98132_, s3, pos2i1.x, pos2i1.y));
            });
            int[] aint = intlist.toIntArray();
            boolean flag = i == s.length();
            TextMenuScreen.Pos2i pos2i;
            if (flag && mutableboolean.isTrue()) {
                pos2i = new TextMenuScreen.Pos2i(0, list.size() * 9);
            } else {
                int k = findLineFromPos(aint, i);
                int l = this.font.width(s.substring(aint[k], i));
                pos2i = new TextMenuScreen.Pos2i(l, k * 9);
            }

            List<Rect2i> list1 = Lists.newArrayList();
            if (i != j) {
                int l2 = Math.min(i, j);
                int i1 = Math.max(i, j);
                int j1 = findLineFromPos(aint, l2);
                int k1 = findLineFromPos(aint, i1);
                if (j1 == k1) {
                    int l1 = j1 * 9;
                    int i2 = aint[j1];
                    list1.add(this.createPartialLineSelection(s, stringsplitter, l2, i1, l1, i2));
                } else {
                    int i3 = j1 + 1 > aint.length ? s.length() : aint[j1 + 1];
                    list1.add(this.createPartialLineSelection(s, stringsplitter, l2, i3, j1 * 9, aint[j1]));

                    for(int j3 = j1 + 1; j3 < k1; ++j3) {
                        int j2 = j3 * 9;
                        String s1 = s.substring(aint[j3], aint[j3 + 1]);
                        int k2 = (int)stringsplitter.stringWidth(s1);
                        list1.add(this.createSelection(new TextMenuScreen.Pos2i(0, j2), new TextMenuScreen.Pos2i(k2, j2 + 9)));
                    }

                    list1.add(this.createPartialLineSelection(s, stringsplitter, aint[k1], i1, k1 * 9, aint[k1]));
                }
            }

            return new TextMenuScreen.DisplayCache(s, pos2i, flag, aint, list.toArray(new TextMenuScreen.LineInfo[0]), list1.toArray(new Rect2i[0]));
        }
    }

    private TextMenuScreen.Pos2i convertScreenToLocal(TextMenuScreen.Pos2i pos) {
        return new TextMenuScreen.Pos2i(pos.x - getTextAreaX() - this.leftPos, pos.y - getTextAreaY() - this.topPos);
    }

    private TextMenuScreen.Pos2i convertLocalToScreen(TextMenuScreen.Pos2i pos) {
        return new TextMenuScreen.Pos2i(pos.x + getTextAreaX() + this.leftPos, pos.y + getTextAreaY() + this.topPos);
    }

    private Rect2i createPartialLineSelection(String p_98120_, StringSplitter p_98121_, int p_98122_, int p_98123_, int p_98124_, int p_98125_) {
        String s = p_98120_.substring(p_98125_, p_98122_);
        String s1 = p_98120_.substring(p_98125_, p_98123_);
        TextMenuScreen.Pos2i bookeditscreen$pos2i = new TextMenuScreen.Pos2i((int)p_98121_.stringWidth(s), p_98124_);
        TextMenuScreen.Pos2i bookeditscreen$pos2i1 = new TextMenuScreen.Pos2i((int)p_98121_.stringWidth(s1), p_98124_ + 9);
        return this.createSelection(bookeditscreen$pos2i, bookeditscreen$pos2i1);
    }

    private Rect2i createSelection(TextMenuScreen.Pos2i p_98117_, TextMenuScreen.Pos2i p_98118_) {
        TextMenuScreen.Pos2i bookeditscreen$pos2i = this.convertLocalToScreen(p_98117_);
        TextMenuScreen.Pos2i bookeditscreen$pos2i1 = this.convertLocalToScreen(p_98118_);
        int i = Math.min(bookeditscreen$pos2i.x, bookeditscreen$pos2i1.x);
        int j = Math.max(bookeditscreen$pos2i.x, bookeditscreen$pos2i1.x);
        int k = Math.min(bookeditscreen$pos2i.y, bookeditscreen$pos2i1.y);
        int l = Math.max(bookeditscreen$pos2i.y, bookeditscreen$pos2i1.y);
        return new Rect2i(i, k, j - i, l - k);
    }

    @Override
    public void render(PoseStack pose, int cursorX, int cursorY, float partialTicks) {
        this.renderBackground(pose);
        RenderSystem.setShaderTexture(0, getBackground());
        RenderSystem.setShaderColor(1, 1, 1, 1);
        blit(pose, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        TextMenuScreen.DisplayCache displayCache = this.getDisplayCache();

        for(TextMenuScreen.LineInfo lineInfo : displayCache.lines) {
            this.font.draw(pose, lineInfo.asComponent, (float)lineInfo.x, (float)lineInfo.y, getTextColor());
        }

        this.renderHighlight(displayCache.selection);
        this.renderCursor(pose, displayCache.cursor, displayCache.cursorAtEnd);

        this.renderLabels(pose, cursorX, cursorY);

        super.render(pose, cursorX, cursorY, partialTicks);
    }

    public boolean canEdit() {
        return menu.canEditExisting() || menu.textCopyLastReceived.isEmpty();
    }

    private int frameTick = 0;
    public void tick() {
        super.tick();
        ++this.frameTick;

        if (canEdit()) {
            doneButton.visible = true;
            cancelButton.visible = true;

            doneReadingButton.visible = false;
        } else {
            doneButton.visible = false;
            cancelButton.visible = false;

            doneReadingButton.visible = true;
        }
    }

    private void renderCursor(PoseStack poseStack, TextMenuScreen.Pos2i pos, boolean atEnd) {
        if (canEdit() && this.frameTick / 6 % 2 == 0) {
            pos = this.convertLocalToScreen(pos);
            if (!atEnd) {
                GuiComponent.fill(poseStack, pos.x, pos.y - 1, pos.x + 1, pos.y + 9, getTextColor());
            } else {
                this.font.draw(poseStack, "_", (float)pos.x, (float)pos.y, getTextColor());
            }
        }

    }

    private void renderHighlight(Rect2i[] rects) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(0.0F, 0.0F, 255.0F, 255.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

        for(Rect2i rect2i : rects) {
            int i = rect2i.getX();
            int j = rect2i.getY();
            int k = i + rect2i.getWidth();
            int l = j + rect2i.getHeight();
            bufferbuilder.vertex((double)i, (double)l, 0.0D).endVertex();
            bufferbuilder.vertex((double)k, (double)l, 0.0D).endVertex();
            bufferbuilder.vertex((double)k, (double)j, 0.0D).endVertex();
            bufferbuilder.vertex((double)i, (double)j, 0.0D).endVertex();
        }

        tesselator.end();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }

    protected void renderLabels(PoseStack pose, int cursorX, int cursorY) {
        var noteTitle = this.getNoteTitle();
        if (noteTitle != null)
            this.font.draw(pose, noteTitle, this.leftPos + this.titleLabelX, this.topPos + this.titleLabelY, 4210752);
    }

    public boolean charTyped(char c, int i) {
        if (super.charTyped(c, i)) {
            return true;
        } else if (canEdit() && SharedConstants.isAllowedChatCharacter(c)) {
            this.textEdit.insertText(Character.toString(c));
            this.clearDisplayCache();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean keyPressed(int key, int scancode, int mods) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            saveChanges(false);
            this.onClose();
        }

        if (canEdit() && this.editKeyPressed(key, scancode, mods)) {
            this.clearDisplayCache();
            return true;
        }

        return false;
    }

    @Override
    public void onClose() {
        player.closeContainer();
        super.onClose();
    }

    public boolean editKeyPressed(int key, int scancode, int mods) {
        if (Screen.isSelectAll(key)) {
            this.textEdit.selectAll();
            return true;
        } else if (Screen.isCopy(key)) {
            this.textEdit.copy();
            return true;
        } else if (Screen.isPaste(key)) {
            this.textEdit.paste();
            return true;
        } else if (Screen.isCut(key)) {
            this.textEdit.cut();
            return true;
        } else {
            switch(key) {
                case 257:
                case 335:
                    this.textEdit.insertText("\n");
                    return true;
                case 259:
                    this.textEdit.removeCharsFromCursor(-1);
                    return true;
                case 261:
                    this.textEdit.removeCharsFromCursor(1);
                    return true;
                case 262:
                    this.textEdit.moveByChars(1, Screen.hasShiftDown());
                    return true;
                case 263:
                    this.textEdit.moveByChars(-1, Screen.hasShiftDown());
                    return true;
                case 264:
                    this.keyDown();
                    return true;
                case 265:
                    this.keyUp();
                    return true;
                case 268:
                    this.keyHome();
                    return true;
                case 269:
                    this.keyEnd();
                    return true;
                default:
                    return false;
            }
        }
    }

    private void selectWord(int p_98142_) {
        String s = menu.textCopy;
        this.textEdit.setSelectionRange(StringSplitter.getWordPosition(s, -1, p_98142_, false), StringSplitter.getWordPosition(s, 1, p_98142_, false));
    }

    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        if (super.mouseDragged(x, y, button, dx, dy)) {
            return true;
        } else {
            if (button == 0) {
                TextMenuScreen.DisplayCache displayCache = this.getDisplayCache();
                int i = displayCache.getIndexAtPosition(this.font, this.convertScreenToLocal(new TextMenuScreen.Pos2i((int)x, (int)y)));
                this.textEdit.setCursorPos(i, true);
                this.clearDisplayCache();
            }

            return true;
        }
    }

    private long lastClickTime;
    private int lastIndex = -1;
    public boolean mouseClicked(double x, double y, int button) {
        if (super.mouseClicked(x, y, button)) {
            return true;
        } else {
            if (button == 0) {
                long i = Util.getMillis();
                TextMenuScreen.DisplayCache displayCache = this.getDisplayCache();
                int j = displayCache.getIndexAtPosition(this.font, this.convertScreenToLocal(new TextMenuScreen.Pos2i((int)x, (int)y)));
                if (j >= 0) {
                    if (j == this.lastIndex && i - this.lastClickTime < 250L) {
                        if (!this.textEdit.isSelecting()) {
                            this.selectWord(j);
                        } else {
                            this.textEdit.selectAll();
                        }
                    } else {
                        this.textEdit.setCursorPos(j, Screen.hasShiftDown());
                    }

                    this.clearDisplayCache();
                }

                this.lastIndex = j;
                this.lastClickTime = i;
            }

            return true;
        }
    }
    
    private void keyUp() {
        this.changeLine(-1);
    }

    private void keyDown() {
        this.changeLine(1);
    }

    private void changeLine(int offset) {
        int i = this.textEdit.getCursorPos();
        int j = this.getDisplayCache().changeLine(i, offset);
        this.textEdit.setCursorPos(j, Screen.hasShiftDown());
    }

    private void keyHome() {
        int i = this.textEdit.getCursorPos();
        int j = this.getDisplayCache().findLineStart(i);
        this.textEdit.setCursorPos(j, Screen.hasShiftDown());
    }

    private void keyEnd() {
        TextMenuScreen.DisplayCache bookeditscreen$displaycache = this.getDisplayCache();
        int i = this.textEdit.getCursorPos();
        int j = bookeditscreen$displaycache.findLineEnd(i);
        this.textEdit.setCursorPos(j, Screen.hasShiftDown());
    }
    
    private void saveChanges(boolean save) {
        if (canEdit() && save)
            menu.setText(menu.textCopy);
    }

    @OnlyIn(Dist.CLIENT)
    static class DisplayCache {
        static final TextMenuScreen.DisplayCache EMPTY = new TextMenuScreen.DisplayCache("", new TextMenuScreen.Pos2i(0, 0), true, new int[]{0}, new TextMenuScreen.LineInfo[]{new TextMenuScreen.LineInfo(Style.EMPTY, "", 0, 0)}, new Rect2i[0]);
        private final String fullText;
        final TextMenuScreen.Pos2i cursor;
        final boolean cursorAtEnd;
        private final int[] lineStarts;
        final TextMenuScreen.LineInfo[] lines;
        final Rect2i[] selection;

        public DisplayCache(String text, TextMenuScreen.Pos2i cursor, boolean atEnd, int[] starts, TextMenuScreen.LineInfo[] lines, Rect2i[] selection) {
            this.fullText = text;
            this.cursor = cursor;
            this.cursorAtEnd = atEnd;
            this.lineStarts = starts;
            this.lines = lines;
            this.selection = selection;
        }

        public int getIndexAtPosition(Font font, TextMenuScreen.Pos2i pos) {
            int i = pos.y / 9;
            if (i < 0) {
                return 0;
            } else if (i >= this.lines.length) {
                return this.fullText.length();
            } else {
                TextMenuScreen.LineInfo lineInfo = this.lines[i];
                return this.lineStarts[i] + font.getSplitter().plainIndexAtWidth(lineInfo.contents, pos.x, lineInfo.style);
            }
        }

        public int changeLine(int index, int direction) {
            int i = TextMenuScreen.findLineFromPos(this.lineStarts, index);
            int j = i + direction;
            int k;
            if (0 <= j && j < this.lineStarts.length) {
                int l = index - this.lineStarts[i];
                int i1 = this.lines[j].contents.length();
                k = this.lineStarts[j] + Math.min(l, i1);
            } else {
                k = index;
            }

            return k;
        }

        public int findLineStart(int index) {
            int i = TextMenuScreen.findLineFromPos(this.lineStarts, index);
            return this.lineStarts[i];
        }

        public int findLineEnd(int p_9index219_) {
            int i = TextMenuScreen.findLineFromPos(this.lineStarts, p_9index219_);
            return this.lineStarts[i] + this.lines[i].contents.length();
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class LineInfo {
        final Style style;
        final String contents;
        final Component asComponent;
        final int x;
        final int y;

        public LineInfo(Style p_98232_, String p_98233_, int p_98234_, int p_98235_) {
            this.style = p_98232_;
            this.contents = p_98233_;
            this.x = p_98234_;
            this.y = p_98235_;
            this.asComponent = (new TextComponent(p_98233_)).setStyle(p_98232_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class Pos2i {
        public final int x;
        public final int y;

        Pos2i(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
