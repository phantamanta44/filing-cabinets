package xyz.phanta.filcabref.inventory;

import io.github.phantamanta44.libnine.gui.L9Container;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import xyz.phanta.filcabref.block.filing_cabinet.BlockFilingCabinet;
import xyz.phanta.filcabref.block.filing_cabinet.TileFilingCabinet;
import xyz.phanta.filcabref.inventory.slot.SlotFilingCabinet;
import xyz.phanta.filcabref.inventory.slot.SlotFilingCabinetProxy;

public class ContainerFilingCabinet extends L9Container {

    private final TileFilingCabinet tile;
    private final Slot[] cabinetSlots;

    public ContainerFilingCabinet(InventoryPlayer ipl, TileFilingCabinet tile) {
        super(ipl, 217);
        this.tile = tile;
        this.cabinetSlots = new Slot[tile.getCabinetInventory().getSlotCount()];
        for (int i = 0; i < cabinetSlots.length; i++) {
            cabinetSlots[i] = addSlotToContainer(new SlotFilingCabinet(this, i, 0, 0));
        }
    }

    public TileFilingCabinet getCabinet() {
        return tile;
    }

    public BlockFilingCabinet.Type getCabinetType() {
        return tile.getCabinetType();
    }

    public Slot getCabinetSlot(int slotIndex) {
        return cabinetSlots[slotIndex];
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        if (index < 36) {
            Slot slot = inventorySlots.get(index);
            if (slot != null && slot.getHasStack()) {
                ItemStack stack = slot.getStack();
                ItemStack result = tile.getCabinetInventory().insertItem(stack, false);
                if (!ItemStack.areItemStacksEqual(stack, result)) {
                    slot.putStack(result);
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack slotClick(int slotId, int data, ClickType clickType, EntityPlayer player) {
        if (slotId >= 0) {
            Slot invSlot = inventorySlots.get(slotId);
            if (invSlot instanceof SlotFilingCabinet) {
                SlotFilingCabinet slot = (SlotFilingCabinet)invSlot;
                TileFilingCabinet.CabinetInventory inv = tile.getCabinetInventory();
                switch (clickType) {
                    case PICKUP:
                        ItemStack held = player.inventory.getItemStack();
                        if (held.isEmpty()) {
                            ItemStack slotStack = slot.getStack();
                            if (!slotStack.isEmpty()) {
                                int maxExtract = Math.min(slotStack.getCount(), slotStack.getMaxStackSize());
                                player.inventory.setItemStack(inv.extractItem(slot.getSlotIndex(),
                                        data == 0 ? maxExtract : (int)Math.ceil(maxExtract / 2F), false));
                            }
                        } else if (data == 0) { // place entire stack
                            player.inventory.setItemStack(inv.insertItem(held, false));
                        } else { // place one item
                            ItemStack insertStack = ItemHandlerHelper.copyStackWithSize(held, 1);
                            if (inv.insertItem(insertStack, false).isEmpty()) {
                                held.shrink(1);
                            }
                        }
                        break;
                    case QUICK_MOVE:
                        ItemStack mergeStack = inv.extractItem(slot.getSlotIndex(), slot.getStack().getCount(), true);
                        int maxMerge = Math.min(mergeStack.getCount(), mergeStack.getMaxStackSize());
                        mergeStack.setCount(maxMerge);
                        mergeItemStack(mergeStack, 0, 36, false);
                        int extracted = maxMerge - mergeStack.getCount();
                        if (extracted > 0) {
                            inv.extractItem(slot.getSlotIndex(), extracted, false);
                        }
                        break;
                    case CLONE:
                        if (player.capabilities.isCreativeMode) {
                            ItemStack cloneStack = slot.getStack();
                            if (!cloneStack.isEmpty()) {
                                player.inventory.setItemStack(
                                        ItemHandlerHelper.copyStackWithSize(cloneStack, cloneStack.getMaxStackSize()));
                            }
                        }
                        break;
                    case THROW:
                        if (!slot.getStack().isEmpty()) {
                            ItemStack dropStack = inv.extractItem(slot.getSlotIndex(), 1, false);
                            if (!dropStack.isEmpty()) {
                                player.dropItem(dropStack, true);
                            }
                        }
                        break;
                } // ignore all other click types
                return ItemStack.EMPTY;
            } else if (invSlot instanceof SlotFilingCabinetProxy) {
                return ItemStack.EMPTY;
            }
        }
        return super.slotClick(slotId, data, clickType, player);
    }

}
