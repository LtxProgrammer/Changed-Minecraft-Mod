package net.ltxprogrammer.changed.client.gui.computer;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.ComputerScreen;
import net.ltxprogrammer.changed.computers.application.TextEditorApplication;
import net.ltxprogrammer.changed.network.packet.ComputerAppClosePacket;
import net.ltxprogrammer.changed.util.SingleRunnable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.List;

public class TextEditorScreen implements ApplicationScreen {
    public static final ResourceLocation BACKGROUND = Changed.modResource("textures/gui/computer/app_bg/text_editor.png");

    private DisplayCache displayCache = DisplayCache.EMPTY;
    private final TextFieldHelper textEdit;

    protected final TextEditorApplication application;
    protected final ComputerScreen screen;

    protected final SingleRunnable appCloser;

    protected Button enableEditButton;
    protected Button saveButton;
    protected ScrollBarVerticalStepped scrollBar;
    protected StringWidget fileName;

    protected final Font font;

    private boolean editing = false;
    private boolean dirty = false;
    private int frameTick = 0;

    private int desktopLeft;
    private int desktopTop;
    private int desktopWidth;
    private int desktopHeight;

    private int textBoxLeft;
    private int textBoxTop;
    private int textBoxWidth;
    private int textBoxHeight;

    public TextEditorScreen(TextEditorApplication application, ComputerScreen screen) {
        this.application = application;
        this.screen = screen;

        this.appCloser = new SingleRunnable(() -> {
            Changed.PACKET_HANDLER.sendToServer(
                    ComputerAppClosePacket.closeApplication(application.getType()));
        });

        this.font = screen.getMinecraft().font;

        this.textEdit = new TextFieldHelper(
                application::getFileContent,
                nText -> {
                    application.setFileContent(nText);
                    this.dirty = true;
                    this.displayCache = rebuildDisplayCache();
                    this.scrollBar.setCanvasSize(this.displayCache.lines.length + 4);
                },
                TextFieldHelper.createClipboardGetter(screen.getMinecraft()),
                TextFieldHelper.createClipboardSetter(screen.getMinecraft()),
                (nText) -> nText.length() < 1024
        );
        this.textEdit.setCursorToStart();
    }

    @Override
    public void initialize(int desktopLeft, int desktopTop, int desktopWidth, int desktopHeight) {
        screen.clearApplicationWidgets();

        this.desktopLeft = desktopLeft;
        this.desktopTop = desktopTop;
        this.desktopWidth = desktopWidth;
        this.desktopHeight = desktopHeight;

        int x = desktopLeft + 4;
        int y = desktopTop + 4;

        screen.addApplicationWidget(ApplicationScreen.shadowlessString(x, y, desktopWidth - 8, 20,
                        application.getType().getDisplayName(), font)
                .alignCenter().setColor(0x404040));

        this.fileName = screen.addApplicationWidget(ApplicationScreen.shadowlessString(x, desktopTop + 191, desktopWidth, 9,
                        Component.empty(), font)
                .alignLeft().setColor(0x404040));

        screen.addApplicationWidget(Button.builder(COMPONENT_EXIT, (self) -> {
                    appCloser.run();
                }).bounds(x, y, 20, 20)
                .tooltip(Tooltip.create(COMPONENT_EXIT))
                .build(ApplicationScreen.iconButton(screen::getTheme, 200, 0)));

        this.enableEditButton = screen.addApplicationWidget(Button.builder(COMPONENT_EDIT, (self) -> {
                    this.editing = true;
                }).bounds(x + 23, y, 20, 20)
                .tooltip(Tooltip.create(COMPONENT_EDIT))
                .build(ApplicationScreen.iconButton2(screen::getTheme, 20, 0)));

        this.saveButton = screen.addApplicationWidget(Button.builder(COMPONENT_SAVE, (self) -> {
                    saveChanges(true);
                }).bounds(x + 23, y, 20, 20)
                .tooltip(Tooltip.create(COMPONENT_SAVE))
                .build(ApplicationScreen.iconButton2(screen::getTheme, 0, 0)));

        this.scrollBar = screen.addApplicationWidget(ApplicationScreen.verticalScrollBarStepped(screen::getTheme, desktopLeft + 314, desktopTop + 27, 6, 163)
                .setCanvasSize(5).setViewportSize(17).setScrollListener((lastScroll, scroll) -> {
                    if (lastScroll == scroll)
                        return;
                    this.clearDisplayCache();
                }));

        this.textBoxLeft = x;
        this.textBoxTop = y + 26;
        this.textBoxHeight = desktopHeight - 8 - 26 - 10;
        this.textBoxWidth = desktopWidth - 8 - 6;

        this.scrollBar.setCanvasSize(this.getDisplayCache().lines.length + 4);
    }

    @Override
    public void tick(int desktopLeft, int desktopTop, int desktopWidth, int desktopHeight) {
        ++this.frameTick;

        this.editing = canEdit() && this.editing;
        this.saveButton.active = canEdit() && this.dirty;
        this.saveButton.visible = this.editing;
        this.enableEditButton.active = canEdit();
        this.enableEditButton.visible = !this.editing;

        var activeFile = application.getActiveFile();
        if (activeFile != null)
            this.fileName.setMessage(Component.literal(screen.getMenu().getWorkingDir().relativize(application.getActiveFile())
                + ((canEdit() && this.dirty) ? "*" : "")));
        else
            this.fileName.setMessage(Component.empty());
    }

    @Override
    public void render(GuiGraphics graphics, int cursorX, int cursorY, float partialTicks) {
        DisplayCache displayCache = this.getDisplayCache();

        graphics.blit(BACKGROUND, this.desktopLeft, this.desktopTop, 0, 0,
                this.desktopWidth, this.desktopHeight, this.desktopWidth, this.desktopHeight);

        int linesToSkip = this.scrollBar.getScroll();
        int linesDrawn = 0;
        for (LineInfo lineInfo : displayCache.lines) {
            if (linesToSkip > 0) {
                linesToSkip--;
                continue;
            }

            graphics.drawString(this.font, lineInfo.asComponent, lineInfo.x, lineInfo.y, getTextColor(), false);
            linesDrawn++;

            if (linesDrawn >= this.scrollBar.getViewportSize()) {
                break;
            }
        }

        this.renderHighlight(graphics, displayCache.selection);
        this.renderCursor(graphics, displayCache.cursor, displayCache.cursorAtEnd);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            this.appCloser.run();
            return true;
        }

        if (this.isEditing() && this.editKeyPressed(key, scanCode, modifiers)) {
            this.clearDisplayCache();
            this.ensureCursorIsInView();
            return true;
        }

        return ApplicationScreen.super.keyPressed(key, scanCode, modifiers);
    }

    /// Do not use with DisplayCache.lineStarts()
    protected int getCursorLineIndex() {
        return (getDisplayCache().cursor.y / 9);
    }

    protected void ensureCursorIsInView() {
        int lineIndex = getCursorLineIndex();
        int scroll = this.scrollBar.getScroll();

        int relativeLineIndex = lineIndex - scroll;

        if (relativeLineIndex >= 0 && relativeLineIndex < this.scrollBar.getViewportSize())
            return;

        if (relativeLineIndex < 0)
            scrollBar.setScroll(lineIndex);
        else
            scrollBar.setScroll(lineIndex - this.scrollBar.getViewportSize() + 1);
    }

    protected boolean canEdit() {
        return true;
    }

    protected boolean isEditing() {
        return editing && canEdit();
    }

    protected int getTextColor() {
        return screen.getTheme().getFGColor();
    }

    public int getTextAreaWidth() {
        return textBoxWidth;
    }

    public int getTextAreaHeight() {
        return textBoxHeight;
    }

    public int getTextAreaX() {
        return 4;
    }

    public int getTextAreaY() {
        return 4 + 26;
    }

    protected boolean isMouseInTextArea(double x, double y) {
        return x > textBoxLeft && x < (textBoxLeft + textBoxWidth) && y > textBoxTop && y < (textBoxTop + textBoxHeight);
    }

    private Pos2i convertScreenToLocal(Pos2i pos) {
        int linePositionOffset = this.scrollBar.getScroll() * -9;
        return new Pos2i(
                pos.x - getTextAreaX() - this.desktopLeft,
                pos.y - getTextAreaY() - this.desktopTop - linePositionOffset);
    }

    private Pos2i convertLocalToScreen(Pos2i pos) {
        int linePositionOffset = this.scrollBar.getScroll() * -9;
        return new Pos2i(
                pos.x + getTextAreaX() + this.desktopLeft,
                pos.y + getTextAreaY() + this.desktopTop + linePositionOffset);
    }

    private Rect2i createPartialLineSelection(String text, StringSplitter splitter, int p_98122_, int p_98123_, int p_98124_, int p_98125_) {
        String s = text.substring(p_98125_, p_98122_);
        String s1 = text.substring(p_98125_, p_98123_);
        Pos2i bookeditscreen$pos2i = new Pos2i((int)splitter.stringWidth(s), p_98124_);
        Pos2i bookeditscreen$pos2i1 = new Pos2i((int)splitter.stringWidth(s1), p_98124_ + 9);
        return this.createSelection(bookeditscreen$pos2i, bookeditscreen$pos2i1);
    }

    private Rect2i createSelection(Pos2i start, Pos2i end) {
        Pos2i screenStart = this.convertLocalToScreen(start);
        Pos2i screenEnd = this.convertLocalToScreen(end);
        int i = Math.min(screenStart.x, screenEnd.x);
        int j = Math.max(screenStart.x, screenEnd.x);
        int k = Math.min(screenStart.y, screenEnd.y);
        int l = Math.max(screenStart.y, screenEnd.y);
        return new Rect2i(i, k, j - i, l - k);
    }

    private void renderCursor(GuiGraphics graphics, Pos2i pos, boolean atEnd) {
        if (isEditing() && this.frameTick / 6 % 2 == 0) {
            pos = this.convertLocalToScreen(pos);

            if (pos.y < textBoxTop)
                return;
            if (pos.y >= textBoxTop + scrollBar.getViewportSize() * 9)
                return;

            if (!atEnd) {
                graphics.fill(RenderType.guiOverlay(), pos.x,
                        pos.y - 1,
                        pos.x + 1,
                        pos.y + 9, getTextColor());
            } else {
                graphics.drawString(this.font, "_", pos.x, pos.y, getTextColor(), false);
            }
        }

    }

    private void renderHighlight(GuiGraphics graphics, Rect2i[] rects) {
        for(Rect2i rect2i : rects) {
            int x = rect2i.getX();
            int y = rect2i.getY();
            int x2 = x + rect2i.getWidth();
            int y2 = y + rect2i.getHeight();

            if (y < textBoxTop)
                continue;
            if (y2 > textBoxTop + scrollBar.getViewportSize() * 9)
                continue;

            graphics.fill(RenderType.guiTextHighlight(), x, y, x2, y2, -16776961);
        }
    }

    private DisplayCache getDisplayCache() {
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

    private DisplayCache rebuildDisplayCache() {
        String content = application.getFileContent();
        if (content.isEmpty()) {
            return DisplayCache.EMPTY;
        } else {
            int cursorPos = this.textEdit.getCursorPos();
            int selectionPos = this.textEdit.getSelectionPos();
            IntList intlist = new IntArrayList();
            List<LineInfo> list = Lists.newArrayList();
            MutableInt mutableint = new MutableInt();
            MutableBoolean mutableboolean = new MutableBoolean();
            StringSplitter stringsplitter = this.font.getSplitter();
            stringsplitter.splitLines(content, getTextAreaWidth(), Style.EMPTY, true, (lineStyle, beginIndex, endIndex) -> {
                int k3 = mutableint.getAndIncrement();
                String s2 = content.substring(beginIndex, endIndex);
                mutableboolean.setValue(s2.endsWith("\n"));
                String s3 = StringUtils.stripEnd(s2, " \n");
                int l3 = k3 * 9;
                Pos2i pos2i1 = this.convertLocalToScreen(new Pos2i(0, l3));
                intlist.add(beginIndex);
                list.add(new LineInfo(lineStyle, s3, pos2i1.x, pos2i1.y));
            });
            int[] aint = intlist.toIntArray();
            boolean cursorAtEnd = cursorPos == content.length();
            Pos2i cursor;
            if (cursorAtEnd && mutableboolean.isTrue()) {
                cursor = new Pos2i(0, list.size() * 9);
            } else {
                int k = findLineFromPos(aint, cursorPos);
                int l = this.font.width(content.substring(aint[k], cursorPos));
                cursor = new Pos2i(l, k * 9);
            }

            List<Rect2i> list1 = Lists.newArrayList();
            if (cursorPos != selectionPos) {
                int selectionStartPos = Math.min(cursorPos, selectionPos);
                int selectionEndPos = Math.max(cursorPos, selectionPos);
                int selectionStartLine = findLineFromPos(aint, selectionStartPos);
                int selectionEndLine = findLineFromPos(aint, selectionEndPos);
                if (selectionStartLine == selectionEndLine) {
                    int l1 = selectionStartLine * 9;
                    int i2 = aint[selectionStartLine];
                    list1.add(this.createPartialLineSelection(content, stringsplitter, selectionStartPos, selectionEndPos, l1, i2));
                } else {
                    int i3 = selectionStartLine + 1 > aint.length ? content.length() : aint[selectionStartLine + 1];
                    list1.add(this.createPartialLineSelection(content, stringsplitter, selectionStartPos, i3, selectionStartLine * 9, aint[selectionStartLine]));

                    for(int j3 = selectionStartLine + 1; j3 < selectionEndLine; ++j3) {
                        int j2 = j3 * 9;
                        String s1 = content.substring(aint[j3], aint[j3 + 1]);
                        int k2 = (int)stringsplitter.stringWidth(s1);
                        list1.add(this.createSelection(new Pos2i(0, j2), new Pos2i(k2, j2 + 9)));
                    }

                    list1.add(this.createPartialLineSelection(content, stringsplitter, aint[selectionEndLine], selectionEndPos, selectionEndLine * 9, aint[selectionEndLine]));
                }
            }

            return new DisplayCache(content, cursor, cursorAtEnd, aint, list.toArray(new LineInfo[0]), list1.toArray(new Rect2i[0]));
        }
    }

    @Override
    public boolean charTyped(char c, int i) {
        if (ApplicationScreen.super.charTyped(c, i)) {
            return true;
        } else if (isEditing() && SharedConstants.isAllowedChatCharacter(c)) {
            this.textEdit.insertText(Character.toString(c));
            this.clearDisplayCache();
            this.ensureCursorIsInView();
            return true;
        } else {
            return false;
        }
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
                case GLFW.GLFW_KEY_ENTER:
                case GLFW.GLFW_KEY_KP_ENTER:
                    this.textEdit.insertText("\n");
                    return true;
                case GLFW.GLFW_KEY_BACKSPACE:
                    this.textEdit.removeCharsFromCursor(-1);
                    return true;
                case GLFW.GLFW_KEY_DELETE:
                    this.textEdit.removeCharsFromCursor(1);
                    return true;
                case GLFW.GLFW_KEY_RIGHT:
                    this.textEdit.moveByChars(1, Screen.hasShiftDown());
                    return true;
                case GLFW.GLFW_KEY_LEFT:
                    this.textEdit.moveByChars(-1, Screen.hasShiftDown());
                    return true;
                case GLFW.GLFW_KEY_DOWN:
                    this.keyDown();
                    return true;
                case GLFW.GLFW_KEY_UP:
                    this.keyUp();
                    return true;
                case GLFW.GLFW_KEY_HOME:
                    this.keyHome();
                    return true;
                case GLFW.GLFW_KEY_END:
                    this.keyEnd();
                    return true;
                case GLFW.GLFW_KEY_TAB:
                    int column = textEdit.getCursorPos() - getDisplayCache().lineStarts()[findLineFromPos(getDisplayCache().lineStarts(), textEdit.getCursorPos())];
                    this.textEdit.insertText(" ".repeat(4 - (column % 4)));
                    return true;
                default:
                    return false;
            }
        }
    }

    private void selectWord(int index) {
        String s = application.getFileContent();
        this.textEdit.setSelectionRange(
                StringSplitter.getWordPosition(s, -1, index, false),
                StringSplitter.getWordPosition(s, 1, index, false));
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        if (isMouseInTextArea(x, y) && !scrollBar.isScrolling()) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                DisplayCache displayCache = this.getDisplayCache();
                int i = displayCache.getIndexAtPosition(this.font, this.convertScreenToLocal(new Pos2i((int)x, (int)y)));
                this.textEdit.setCursorPos(i, true);
                this.clearDisplayCache();
            }

            return true;
        }

        return ApplicationScreen.super.mouseDragged(x, y, button, dx, dy);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double yOffset) {
        if (isMouseInTextArea(x, y) && this.scrollBar.mouseScrolled(x, y, yOffset))
            return true;

        return ApplicationScreen.super.mouseScrolled(x, y, yOffset);
    }

    private long lastClickTime;
    private int lastIndex = -1;
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (isMouseInTextArea(x, y)) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                long i = Util.getMillis();
                DisplayCache displayCache = this.getDisplayCache();
                int index = displayCache.getIndexAtPosition(this.font, this.convertScreenToLocal(new Pos2i((int)x, (int)y)));
                if (index >= 0) {
                    if (index == this.lastIndex && i - this.lastClickTime < 250L) {
                        if (!this.textEdit.isSelecting()) {
                            this.selectWord(index);
                        } else {
                            this.textEdit.selectAll();
                        }
                    } else {
                        this.textEdit.setCursorPos(index, Screen.hasShiftDown());
                    }

                    this.clearDisplayCache();
                }

                this.lastIndex = index;
                this.lastClickTime = i;
            }

            return true;
        }

        return ApplicationScreen.super.mouseClicked(x, y, button);
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
        this.ensureCursorIsInView();
    }

    private void keyHome() {
        int i = this.textEdit.getCursorPos();
        int j = this.getDisplayCache().findLineStart(i);
        this.textEdit.setCursorPos(j, Screen.hasShiftDown());
        this.ensureCursorIsInView();
    }

    private void keyEnd() {
        DisplayCache bookeditscreen$displaycache = this.getDisplayCache();
        int i = this.textEdit.getCursorPos();
        int j = bookeditscreen$displaycache.findLineEnd(i);
        this.textEdit.setCursorPos(j, Screen.hasShiftDown());
        this.ensureCursorIsInView();
    }

    private void saveChanges(boolean save) {
        if (canEdit() && save) {
            this.dirty = false;
            application.save();
        }
    }

    record DisplayCache(String fullText, Pos2i cursor, boolean cursorAtEnd, int[] lineStarts, LineInfo[] lines, Rect2i[] selection) {
        static final DisplayCache EMPTY = new DisplayCache("", new Pos2i(0, 0), true, new int[]{0}, new LineInfo[]{new LineInfo(Style.EMPTY, "", 0, 0)}, new Rect2i[0]);
        
        public int getIndexAtPosition(Font font, Pos2i pos) {
            int i = pos.y / 9;
            if (i < 0) {
                return 0;
            } else if (i >= this.lines.length) {
                return this.fullText.length();
            } else {
                LineInfo lineInfo = this.lines[i];
                return this.lineStarts[i] + font.getSplitter().plainIndexAtWidth(lineInfo.contents, pos.x, lineInfo.style);
            }
        }

        public int changeLine(int index, int direction) {
            int i = findLineFromPos(this.lineStarts, index);
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
            int i = findLineFromPos(this.lineStarts, index);
            return this.lineStarts[i];
        }

        public int findLineEnd(int p_9index219_) {
            int i = findLineFromPos(this.lineStarts, p_9index219_);
            return this.lineStarts[i] + this.lines[i].contents.length();
        }
    }

    record LineInfo(Style style, String contents, Component asComponent, int x, int y) {
        public LineInfo(Style style, String contents, int x, int y) {
            this(style, contents, (Component.literal(contents)).setStyle(style), x, y);
        }
    }

    record Pos2i(int x, int y) {}
}
