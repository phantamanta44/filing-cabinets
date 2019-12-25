package xyz.phanta.filcabref.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import xyz.phanta.filcabref.inventory.ContainerFilingCabinet;

public class SlotFilingCabinet extends SlotItemHandler {

    private final ContainerFilingCabinet cont;

    public SlotFilingCabinet(ContainerFilingCabinet cont, int index, int posX, int posY) {
        super(cont.getCabinet().getCabinetInventory(), index, posX, posY);
        this.cont = cont;
    }

    @Override
    public void putStack(ItemStack stack) {
        // NO-OP
    }

    @Override
    public boolean isEnabled() {
        return false;
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
        return other instanceof SlotFilingCabinet && cont.equals(((SlotFilingCabinet)other).cont);
    }

    @Override
    public boolean isHere(IInventory inv, int slotIn) {
        return false;
    }

}
