package xyz.phanta.filcabref.client.gui.component;

import io.github.phantamanta44.libnine.client.gui.component.GuiComponent;
import xyz.phanta.filcabref.constant.ResConst;
import xyz.phanta.filcabref.util.DiscreteScrollable;

public class GuiComponentScrollThumb extends GuiComponent {

    private static final int BAR_WIDTH = 160, BAR_HEIGHT = 11, THUMB_WIDTH = 17;
    private static final int SLIDABLE_WIDTH = BAR_WIDTH - THUMB_WIDTH;
    private static final float SLIDABLE_OFFSET = THUMB_WIDTH / 2F;

    private final DiscreteScrollable scrollPane;

    private float position = 0F;
    private boolean focused = false;

    public GuiComponentScrollThumb(DiscreteScrollable scrollPane) {
        super(8, 108, BAR_WIDTH, BAR_HEIGHT);
        this.scrollPane = scrollPane;
    }

    @Override
    public void render(float partialTicks, int mX, int mY, boolean mouseOver) {
        ResConst.TEX_GUI_FILING_CABINET_SCROLL_THUMB.draw(x + position * SLIDABLE_WIDTH, y, 90D);
    }

    @Override
    public boolean onClick(int mX, int mY, int button, boolean mouseOver) {
        if (mouseOver) {
            updatePosition(mX);
            playClickSound();
            focused = true;
            return true;
        }
        focused = false;
        return false;
    }

    @Override
    public boolean onDrag(int mX, int mY, int button, long dragTime, boolean mouseOver) {
        if (focused) {
            updatePosition(mX);
            return true;
        }
        return false;
    }

    private void updatePosition(int mouseX) {
        if (mouseX < x + SLIDABLE_OFFSET) {
            position = 0F;
        } else if (mouseX > x + SLIDABLE_OFFSET + SLIDABLE_WIDTH) {
            position = 1F;
        } else {
            position = (mouseX - x - SLIDABLE_OFFSET) / SLIDABLE_WIDTH;
        }
        scrollPane.setScrollPosition(Math.round(scrollPane.getScrollPositionCount() * position));
    }

}
