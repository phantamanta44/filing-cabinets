package xyz.phanta.filcabref.block.filing_cabinet;

import io.github.phantamanta44.libnine.capability.provider.CapabilityBroker;
import io.github.phantamanta44.libnine.tile.L9TileEntity;
import io.github.phantamanta44.libnine.tile.RegisterTile;
import io.github.phantamanta44.libnine.util.data.ByteUtils;
import io.github.phantamanta44.libnine.util.data.ISerializable;
import io.github.phantamanta44.libnine.util.data.serialization.AutoSerialize;
import io.github.phantamanta44.libnine.util.data.serialization.IDatum;
import io.github.phantamanta44.libnine.util.helper.ItemUtils;
import io.github.phantamanta44.libnine.util.world.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import xyz.phanta.filcabref.FilingCabinetsConfig;
import xyz.phanta.filcabref.FilingCabinetsMod;
import xyz.phanta.filcabref.constant.LangConst;
import xyz.phanta.filcabref.item.ItemMaterial;
import xyz.phanta.filcabref.util.SlottedStorage;
import xyz.phanta.filcabref.util.TextStyles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public abstract class TileFilingCabinet extends L9TileEntity {

    private final Predicate<ItemStack> upgradeMatcher;

    @AutoSerialize
    private final CabinetInventory inventory;
    @AutoSerialize
    private final IDatum.OfInt upgradeCount = IDatum.ofInt(0);

    TileFilingCabinet(FilingCabinetsConfig.CabinetConfig config, Predicate<ItemStack> upgradeMatcher) {
        this.upgradeMatcher = upgradeMatcher;
        this.inventory = new CabinetInventory(config.numSlots, config.numItems, FilingCabinetsConfig.upgradeCapacity);
        markRequiresSync();
    }

    public abstract BlockFilingCabinet.Type getCabinetType();

    abstract boolean isItemAllowed(ItemStack stack);

    abstract Predicate<ItemStack> extractMatcher(ItemStack stack);

    @Override
    protected ICapabilityProvider initCapabilities() {
        return new CapabilityBroker().with(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, inventory);
    }

    public CabinetInventory getCabinetInventory() {
        return inventory;
    }

    public boolean installCapacityUpgrade(EntityPlayer player, ItemStack stack) {
        if (!stack.isEmpty() && upgradeMatcher.test(stack)) {
            ITextComponent msg;
            if (upgradeCount.getInt() < FilingCabinetsConfig.upgradeCountMax) {
                if (!player.capabilities.isCreativeMode) {
                    stack.shrink(1);
                }
                upgradeCount.preincrement();
                setDirty();
                ((EntityPlayerMP)player).connection.sendPacket(new SPacketSoundEffect(
                        SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER,
                        player.posX, player.posY, player.posZ, 1F, 1F));
                msg = new TextComponentTranslation(LangConst.NOTIF_CAP_UPGRADE_SUCCESS);
                msg.setStyle(TextStyles.SUCCESS);
            } else {
                ((EntityPlayerMP)player).connection.sendPacket(new SPacketSoundEffect(
                        SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER,
                        player.posX, player.posY, player.posZ, 1F, 0.5F));
                msg = new TextComponentTranslation(LangConst.NOTIF_CAP_UPGRADE_MAXED);
                msg.setStyle(TextStyles.FAIL);
            }
            player.sendStatusMessage(msg, true);
            return true;
        }
        return false;
    }

    public void dropInventory(World world, Vec3d pos) {
        for (CabinetInventory.CabinetSlot slot : inventory.slots) {
            ItemStack stack = slot.createFullStack();
            int maxSize = stack.getMaxStackSize();
            while (stack.getCount() > maxSize) {
                WorldUtils.dropItem(world, pos, ItemHandlerHelper.copyStackWithSize(stack, maxSize));
                stack.shrink(maxSize);
            }
            WorldUtils.dropItem(world, pos, stack);
        }
        int upgrades = upgradeCount.getInt();
        if (upgrades > 0) {
            WorldUtils.dropItem(world, pos, ItemMaterial.Type.CAPACITY_UPGRADE.newStack(upgrades));
        }
    }

    public class CabinetInventory implements IItemHandler, SlottedStorage, ISerializable {

        private final CabinetSlot[] slots;
        private final int baseCapacity;
        private final int capacityPerUpgrade;

        private int usedSlotCount = 0;
        private int itemCount = 0;
        @Nullable
        private Predicate<ItemStack> itemMatcher = null;

        CabinetInventory(int numSlots, int baseCapacity, int capacityPerUpgrade) {
            this.slots = new CabinetSlot[numSlots];
            this.baseCapacity = baseCapacity;
            this.capacityPerUpgrade = capacityPerUpgrade;
            for (int i = 0; i < slots.length; i++) {
                slots[i] = new CabinetSlot();
            }
        }

        @Override
        public int getStoredQuantity() {
            return itemCount;
        }

        @Override
        public int getCapacity() {
            return baseCapacity + capacityPerUpgrade * upgradeCount.getInt();
        }

        @Override
        public int getSlotsUsed() {
            return usedSlotCount;
        }

        @Override
        public int getSlotCount() {
            return slots.length;
        }

        private boolean isItemValid(ItemStack stack) {
            return isItemAllowed(stack) && (itemMatcher == null || itemMatcher.test(stack));
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return isItemValid(stack);
        }

        @Override
        public int getSlots() {
            return slots.length;
        }

        @Override
        public int getSlotLimit(int slot) {
            return getRemainingCapacity();
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            return slots[slot].createFullStack();
        }

        public ItemStack insertItem(ItemStack stack, boolean simulate) {
            if (isItemValid(stack)) {
                int slotIndex = 0;
                for (; slotIndex < slots.length; slotIndex++) {
                    if (slots[slotIndex].isEmpty()
                            || ItemHandlerHelper.canItemStacksStack(slots[slotIndex].templateStack, stack)) {
                        break;
                    }
                }
                if (slotIndex != slots.length) {
                    CabinetSlot slot = slots[slotIndex];
                    int toTransfer = Math.min(stack.getCount(), getRemainingCapacity());
                    if (toTransfer > 0) {
                        if (!simulate) {
                            slot.count += toTransfer;
                            itemCount += toTransfer;
                            if (slot.templateStack.isEmpty()) {
                                slot.templateStack = ItemHandlerHelper.copyStackWithSize(stack, 1);
                                ++usedSlotCount;
                                if (itemMatcher == null) {
                                    itemMatcher = extractMatcher(stack);
                                }
                            }
                            setDirty();
                        }
                        return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - toTransfer);
                    }
                }
            }
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int ignored, ItemStack stack, boolean simulate) {
            return insertItem(stack, simulate);
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slotIndex, int amount, boolean simulate) {
            CabinetSlot slot = slots[slotIndex];
            if (!slot.isEmpty()) {
                int toTransfer = Math.min(amount, slot.count);
                ItemStack extracted = slot.createStack(toTransfer);
                if (!simulate) {
                    slot.count -= toTransfer;
                    itemCount -= toTransfer;
                    if (slot.count == 0) {
                        slot.templateStack = ItemStack.EMPTY;
                        --usedSlotCount;
                        if (usedSlotCount == 0) {
                            itemMatcher = null;
                        } else if (usedSlotCount > slotIndex) {
                            System.arraycopy(slots, slotIndex + 1, slots, slotIndex, usedSlotCount - slotIndex);
                            slots[usedSlotCount] = slot;
                        }
                    }
                    setDirty();
                }
                return extracted;
            }
            return ItemStack.EMPTY;
        }

        @Override
        public void serBytes(ByteUtils.Writer data) {
            data.writeInt(usedSlotCount);
            for (int i = 0; i < usedSlotCount; i++) {
                slots[i].serBytes(data);
            }
        }

        @Override
        public void deserBytes(ByteUtils.Reader data) {
            usedSlotCount = data.readInt();
            itemCount = 0;
            for (int i = 0; i < usedSlotCount; i++) {
                slots[i].deserBytes(data);
                itemCount += slots[i].count;
            }
            if (itemCount == 0) {
                itemMatcher = null;
            } else {
                itemMatcher = extractMatcher(slots[0].templateStack);
            }
        }

        @Override
        public void serNBT(NBTTagCompound tag) {
            tag.setInteger("SlotCount", usedSlotCount);
            NBTTagList slotListTag = new NBTTagList();
            for (int i = 0; i < usedSlotCount; i++) {
                NBTTagCompound slotTag = new NBTTagCompound();
                slots[i].serNBT(slotTag);
                slotListTag.appendTag(slotTag);
            }
            tag.setTag("Slots", slotListTag);
        }

        @Override
        public void deserNBT(NBTTagCompound tag) {
            usedSlotCount = tag.getInteger("SlotCount");
            itemCount = 0;
            NBTTagList slotListTag = tag.getTagList("Slots", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < usedSlotCount; i++) {
                slots[i].deserNBT(slotListTag.getCompoundTagAt(i));
                itemCount += slots[i].count;
            }
            if (itemCount == 0) {
                itemMatcher = null;
            } else {
                itemMatcher = extractMatcher(slots[0].templateStack);
            }
        }

        class CabinetSlot implements ISerializable {

            ItemStack templateStack = ItemStack.EMPTY;
            int count = 0;

            boolean isEmpty() {
                return count == 0;
            }

            ItemStack createFullStack() {
                return createStack(count);
            }

            ItemStack createStack(int amount) {
                return ItemHandlerHelper.copyStackWithSize(templateStack, amount);
            }

            @Override
            public void serBytes(ByteUtils.Writer data) {
                data.writeItemStack(templateStack).writeInt(count);
            }

            @Override
            public void deserBytes(ByteUtils.Reader data) {
                templateStack = data.readItemStack();
                count = data.readInt();
            }

            @Override
            public void serNBT(NBTTagCompound tag) {
                tag.setTag("Item", templateStack.serializeNBT());
                tag.setInteger("Count", count);
            }

            @Override
            public void deserNBT(NBTTagCompound tag) {
                templateStack = new ItemStack(tag.getCompoundTag("Item"));
                count = tag.getInteger("Count");
            }

        }

    }

    @RegisterTile(FilingCabinetsMod.MOD_ID)
    public static class Basic extends TileFilingCabinet {

        public Basic() {
            super(FilingCabinetsConfig.cabinetBasic,
                    ItemUtils.matchesWithWildcard(ItemMaterial.Type.CAPACITY_UPGRADE.newStack(1)));
        }

        @Override
        public BlockFilingCabinet.Type getCabinetType() {
            return BlockFilingCabinet.Type.BASIC;
        }

        @Override
        boolean isItemAllowed(ItemStack stack) {
            return true;
        }

        @Override
        Predicate<ItemStack> extractMatcher(ItemStack stack) {
            return is -> stack.getItem().equals(is.getItem());
        }

    }

    @RegisterTile(FilingCabinetsMod.MOD_ID)
    public static class Advanced extends TileFilingCabinet {

        public Advanced() {
            super(FilingCabinetsConfig.cabinetAdvanced,
                    ItemUtils.matchesWithWildcard(ItemMaterial.Type.CAPACITY_UPGRADE.newStack(1)));
        }

        @Override
        public BlockFilingCabinet.Type getCabinetType() {
            return BlockFilingCabinet.Type.ADVANCED;
        }

        @Override
        boolean isItemAllowed(ItemStack stack) {
            return stack.getMaxStackSize() == 1;
        }

        @Override
        Predicate<ItemStack> extractMatcher(ItemStack stack) {
            return is -> true;
        }

    }

}
