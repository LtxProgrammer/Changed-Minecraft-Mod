package net.ltxprogrammer.changed.client.gui.computer;

import net.ltxprogrammer.changed.computers.UITheme;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

/// Vertical scroll bar optimized for stepping lines
public class ScrollBarVerticalStepped extends AbstractWidget {
    protected final Supplier<UITheme> themeSupplier;

    protected int canvasSize;
    protected int viewportSize;
    protected int scroll;

    protected boolean scrolling = false;
    protected double scrollMouseStart = 0.0d;
    protected int scrollStart = 0;

    public interface ScrollListener {
        void accept(int lastScroll, int scroll);
    }

    protected ScrollListener scrollListener = null;

    public ScrollBarVerticalStepped(Supplier<UITheme> themeSupplier, int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.themeSupplier = themeSupplier;
    }

    protected boolean isFullCanvasInView() {
        return canvasSize <= viewportSize;
    }

    protected void restrictScroll() {
        if (isFullCanvasInView())
            this.scroll = 0;
        else
            this.scroll = Mth.clamp(this.scroll, 0, getMaxScroll());
    }

    public int getCanvasSize() {
        return canvasSize;
    }

    public ScrollBarVerticalStepped setCanvasSize(int canvasSize) {
        if (this.canvasSize == canvasSize)
            return this;

        this.canvasSize = canvasSize;
        int lastScroll = this.scroll;
        restrictScroll();

        if (this.scrollListener != null)
            this.scrollListener.accept(lastScroll, this.scroll);

        return this;
    }

    public int getViewportSize() {
        return viewportSize;
    }

    public ScrollBarVerticalStepped setViewportSize(int viewportSize) {
        this.viewportSize = viewportSize;
        int lastScroll = this.scroll;
        restrictScroll();

        if (this.scrollListener != null)
            this.scrollListener.accept(lastScroll, this.scroll);

        return this;
    }

    public int getScroll() {
        return scroll;
    }

    public int getMaxScroll() {
        return this.canvasSize - this.viewportSize;
    }

    public void setScroll(int scroll) {
        if (this.scroll == scroll)
            return;

        int lastScroll = this.scroll;
        this.scroll = scroll;
        restrictScroll();

        if (this.scrollListener != null)
            this.scrollListener.accept(lastScroll, this.scroll);
    }

    public boolean isScrolling() {
        return scrolling;
    }

    protected int getBarSize() {
        if (isFullCanvasInView())
            return height - 10;
        return (int)((height - 10) * ((float) viewportSize / (float)canvasSize));
    }

    protected int getBarPosition(int scroll) {
        if (isFullCanvasInView())
            return 5;
        int emptySpace = (height - 10) - getBarSize();
        return 5 + (int)(emptySpace * ((float) scroll / (float)getMaxScroll()));
    }

    public ScrollBarVerticalStepped setScrollListener(ScrollListener scrollListener) {
        this.scrollListener = scrollListener;
        return this;
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double offset) {
        if (isFullCanvasInView())
            return false;

        int lastScroll = this.scroll;
        if (offset < 0)
            this.scroll += 3;
        else
            this.scroll -= 3;
        restrictScroll();

        if (this.scrollListener != null)
            this.scrollListener.accept(lastScroll, this.scroll);

        return true;
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (isFullCanvasInView())
            return false;

        if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            if (!this.scrolling) {
                this.scrolling = true;
                this.scrollMouseStart = my;
                this.scrollStart = this.scroll;
            } else {
                int size = getBarSize();

                int emptySpace = (height - 10) - size;

                double scrollPerPixel = ((double)getMaxScroll() / emptySpace);
                double totalDrag = my - this.scrollMouseStart;

                this.setScroll(this.scrollStart + (int)(scrollPerPixel * totalDrag));
            }
        }

        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            this.scrolling = false;
        }
        return super.mouseReleased(mx, my, button);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mx, int my, float partialTick) {
        // Render up and down steppers

        int x = getX();
        int y = getY();

        var theme = themeSupplier.get();

        if (!isFullCanvasInView()) {
            int barSize = getBarSize();
            int barPosition = getBarPosition(this.scroll);

            graphics.fill(
                    x + 1,
                    y + barPosition,
                    x + width - 1,
                    y + barPosition + barSize, theme.getFGColor());
        } else {
            int barSize = getBarSize();
            int barPosition = getBarPosition(this.scroll);

            graphics.fill(
                    x + 1,
                    y + barPosition,
                    x + width - 1,
                    y + barPosition + barSize, theme.getFGColor(0x7F));
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {

    }
}
