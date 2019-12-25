package xyz.phanta.filcabref.util;

public interface SlottedStorage {

    int getStoredQuantity();

    int getCapacity();

    default int getRemainingCapacity() {
        return getCapacity() - getStoredQuantity();
    }

    int getSlotsUsed();

    int getSlotCount();

}
