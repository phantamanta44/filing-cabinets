package xyz.phanta.filcabref.client.gui;

import io.github.phantamanta44.libnine.client.gui.L9GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import xyz.phanta.filcabref.block.filing_cabinet.TileFilingCabinet;
import xyz.phanta.filcabref.client.gui.component.GuiComponentScrollThumb;
import xyz.phanta.filcabref.client.gui.component.GuiComponentStorageBar;
import xyz.phanta.filcabref.constant.LangConst;
import xyz.phanta.filcabref.constant.ResConst;
import xyz.phanta.filcabref.inventory.ContainerFilingCabinet;
import xyz.phanta.filcabref.inventory.slot.SlotFilingCabinet;
import xyz.phanta.filcabref.inventory.slot.SlotFilingCabinetProxy;
import xyz.phanta.filcabref.util.CountFormat;
import xyz.phanta.filcabref.util.DiscreteScrollable;

import java.util.Arrays;
import java.util.List;

public class GuiFilingCabinet extends L9GuiContainer implements DiscreteScrollable {

    public static final int SLOTS_PER_COL = 5;
    private static final int SLOTS_PER_ROW = 9;
    private static final int VISIBLE_SLOTS = SLOTS_PER_COL * SLOTS_PER_ROW;

    private final ContainerFilingCabinet cont;
    private final int scrollPositionCount;
    private final SortToken[] sortOrder;

    private int scrollPosition = 0;
    private int cachedSlotCount = -1;

    public GuiFilingCabinet(ContainerFilingCabinet cont) {
        super(cont, ResConst.TEX_GUI_FILING_CABINET.getTexture(), 176, 217);
        this.cont = cont;
        int slotCount = getCabinet().getCabinetInventory().getSlotCount();
        if (slotCount <= VISIBLE_SLOTS) {
            this.scrollPositionCount = 1;
        } else {
            this.scrollPositionCount = 1 + (int)Math.ceil((float)(slotCount - VISIBLE_SLOTS) / SLOTS_PER_COL);
        }
        this.sortOrder = new SortToken[slotCount];
        for (int i = 0; i < sortOrder.length; i++) {
            sortOrder[i] = new SortToken();
        }
        for (int colIndex = 0; colIndex < SLOTS_PER_ROW; colIndex++) {
            for (int rowIndex = 0; rowIndex < SLOTS_PER_COL; rowIndex++) {
                cont.inventorySlots.add(new SlotFilingCabinetProxy(
                        this, colIndex * SLOTS_PER_COL + rowIndex, 8 + colIndex * 18, 18 + rowIndex * 18));
            }
        }
        updateSortOrder();
    }

    @Override
    public void initGui() {
        super.initGui();
        addComponent(new GuiComponentStorageBar.Slot(getCabinet().getCabinetInventory()));
        addComponent(new GuiComponentStorageBar.Item(getCabinet().getCabinetInventory()));
        addComponent(new GuiComponentScrollThumb(this));
    }

    public TileFilingCabinet getCabinet() {
        return cont.getCabinet();
    }

    @Override
    public int getScrollPositionCount() {
        return scrollPositionCount;
    }

    @Override
    public int getScrollPosition() {
        return scrollPosition;
    }

    @Override
    public void setScrollPosition(int position) {
        scrollPosition = position;
    }

    private void updateSortOrder() {
        cachedSlotCount = getCabinet().getCabinetInventory().getSlotsUsed();
        for (int i = 0; i < sortOrder.length; i++) {
            sortOrder[i].slotIndex = i;
        }
        Arrays.sort(sortOrder, 0, cachedSlotCount);
    }

    public int getSortedIndex(int index) {
        return sortOrder[index].slotIndex;
    }

    public boolean isIndexInSortOrder(int index) {
        return index < cachedSlotCount;
    }

    @Override
    public void drawForeground(float partialTicks, int mX, int mY) {
        super.drawForeground(partialTicks, mX, mY);
        drawContainerName(I18n.format(cont.getCabinetType().getGuiNameKey()));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (cachedSlotCount != getCabinet().getCabinetInventory().getSlotsUsed()) {
            updateSortOrder();
        }
    }

    // adapted from vanilla GuiContainer#drawSlot
    @Override
    public void drawSlot(Slot slot) {
        if (slot instanceof SlotFilingCabinetProxy) {
            ItemStack stack = slot.getStack();
            if (!stack.isEmpty()) {
                zLevel = 100.0F;
                itemRender.zLevel = 100.0F;
                GlStateManager.enableDepth();
                itemRender.renderItemAndEffectIntoGUI(mc.player, stack, slot.xPos, slot.yPos);
                int count = stack.getCount();
                CountFormat countFmt = CountFormat.getFormat(count);
                if (countFmt == CountFormat.NORMAL) {
                    itemRender.renderItemOverlayIntoGUI(fontRenderer, stack, slot.xPos, slot.yPos, null);
                } else {
                    itemRender.renderItemOverlayIntoGUI(fontRenderer, stack, slot.xPos, slot.yPos, "");
                    String countStr = countFmt.format(stack.getCount());
                    int countStrWidth = fontRenderer.getStringWidth(countStr);
                    float scaleFactor = 15F / countStrWidth;
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                    GlStateManager.disableBlend();
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(scaleFactor, scaleFactor, 1F);
                    fontRenderer.drawStringWithShadow(countStr,
                            (slot.xPos + 17) / scaleFactor - countStrWidth,
                            (slot.yPos + 17) / scaleFactor - fontRenderer.FONT_HEIGHT,
                            0xFFFFFF);
                    GlStateManager.popMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.enableDepth();
                    GlStateManager.enableLighting();
                }
                itemRender.zLevel = 0.0F;
                zLevel = 0.0F;
            }
        } else if (!(slot instanceof SlotFilingCabinet)) {
            super.drawSlot(slot);
        }
    }

    @Override
    public List<String> getItemToolTip(ItemStack stack) {
        List<String> tooltip = super.getItemToolTip(stack);
        if (getSlotUnderMouse() instanceof SlotFilingCabinetProxy) {
            tooltip.add(TextFormatting.DARK_GRAY + I18n.format(LangConst.TT_ITEM_COUNT, stack.getCount()));
        }
        return tooltip;
    }

    @Override
    protected void handleMouseClick(Slot slot, int slotId, int mouseButton, ClickType type) {
        if (slot instanceof SlotFilingCabinetProxy) {
            slotId = getSortedIndex(((SlotFilingCabinetProxy)slot).getEffectiveIndex());
            super.handleMouseClick(cont.getCabinetSlot(slotId), slotId, mouseButton, type);
        } else {
            super.handleMouseClick(slot, slotId, mouseButton, type);
        }
    }

    private class SortToken implements Comparable<SortToken> {

        int slotIndex = -1;

        @Override
        public int compareTo(SortToken o) {
            TileFilingCabinet.CabinetInventory inv = getCabinet().getCabinetInventory();
            ItemStack stackA = inv.getStackInSlot(slotIndex), stackB = inv.getStackInSlot(o.slotIndex);
            int comp = stackA.getDisplayName().compareToIgnoreCase(stackB.getDisplayName());
            return comp != 0 ? comp : Integer.compare(stackA.getMetadata(), stackB.getMetadata());
        }

    }

}
