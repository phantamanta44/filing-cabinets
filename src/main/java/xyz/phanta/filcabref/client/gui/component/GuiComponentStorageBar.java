package xyz.phanta.filcabref.client.gui.component;

import io.github.phantamanta44.libnine.client.gui.component.GuiComponent;
import io.github.phantamanta44.libnine.util.format.FormatUtils;
import io.github.phantamanta44.libnine.util.render.TextureRegion;
import net.minecraft.client.resources.I18n;
import xyz.phanta.filcabref.constant.LangConst;
import xyz.phanta.filcabref.constant.ResConst;
import xyz.phanta.filcabref.util.SlottedStorage;

public abstract class GuiComponentStorageBar extends GuiComponent {

    public GuiComponentStorageBar(int y) {
        super(133, y, 36, 4);
    }

    @Override
    public void render(float partialTicks, int mX, int mY, boolean mouseOver) {
        float frac = getQuantity() / (float)getCapacity();
        if (frac > 0) {
            getBarTexture().drawPartial(x + 1, y + 1, 90D, 0F, 0F, frac, 1F);
        }
    }

    @Override
    public void renderTooltip(float partialTicks, int mX, int mY) {
        int quantity = getQuantity(), capacity = getCapacity();
        drawTooltip(
                I18n.format(getTooltipKey(), quantity, capacity, FormatUtils.formatPercentage(quantity / (float)capacity)),
                mX, mY);
    }

    abstract String getTooltipKey();

    abstract int getQuantity();

    abstract int getCapacity();

    abstract TextureRegion getBarTexture();

    public static class Slot extends GuiComponentStorageBar {

        private final SlottedStorage inventory;

        public Slot(SlottedStorage inventory) {
            super(6);
            this.inventory = inventory;
        }

        @Override
        String getTooltipKey() {
            return LangConst.TT_CABINET_INFO_SLOT_USE;
        }

        @Override
        int getQuantity() {
            return inventory.getSlotsUsed();
        }

        @Override
        int getCapacity() {
            return inventory.getSlotCount();
        }

        @Override
        TextureRegion getBarTexture() {
            return ResConst.TEX_GUI_FILING_CABINET_BAR_SLOTS;
        }

    }

    public static class Item extends GuiComponentStorageBar {

        private final SlottedStorage inventory;

        public Item(SlottedStorage inventory) {
            super(10);
            this.inventory = inventory;
        }

        @Override
        String getTooltipKey() {
            return LangConst.TT_CABINET_INFO_ITEM_USE;
        }

        @Override
        int getQuantity() {
            return inventory.getStoredQuantity();
        }

        @Override
        int getCapacity() {
            return inventory.getCapacity();
        }

        @Override
        TextureRegion getBarTexture() {
            return ResConst.TEX_GUI_FILING_CABINET_BAR_ITEMS;
        }

    }

}
