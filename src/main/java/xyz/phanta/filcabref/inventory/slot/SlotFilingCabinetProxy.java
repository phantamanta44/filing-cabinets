package xyz.phanta.filcabref.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import xyz.phanta.filcabref.client.gui.GuiFilingCabinet;

public class SlotFilingCabinetProxy extends Slot {

    private static final IInventory DUMMY_INV = new InventoryBasic("[Null]", true, 0);

    private final GuiFilingCabinet cabGui;

    public SlotFilingCabinetProxy(GuiFilingCabinet cabGui, int index, int posX, int posY) {
        super(DUMMY_INV, index, posX, posY);
        this.cabGui = cabGui;
    }

    public int getEffectiveIndex() {
        return getSlotIndex() + cabGui.getScrollPosition() * GuiFilingCabinet.SLOTS_PER_COL;
    }

    @Override
    public ItemStack getStack() {
        int effectiveIndex = getEffectiveIndex();
        if (cabGui.isIndexInSortOrder(effectiveIndex)) {
            return cabGui.getCabinet().getCabinetInventory().getStackInSlot(cabGui.getSortedIndex(effectiveIndex));
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void putStack(ItemStack stack) {
        // NO-OP
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Override
    public int getSlotStackLimit() {
        return 0;
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return false;
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSameInventory(Slot other) {
        return other instanceof SlotFilingCabinetProxy && cabGui.equals(((SlotFilingCabinetProxy)other).cabGui);
    }

    @Override
    public boolean isHere(IInventory inv, int slotIn) {
        return false;
    }

}
