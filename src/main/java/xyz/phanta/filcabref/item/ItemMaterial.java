package xyz.phanta.filcabref.item;

import io.github.phantamanta44.libnine.client.model.ParameterizedItemModel;
import io.github.phantamanta44.libnine.item.L9ItemSubs;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import xyz.phanta.filcabref.constant.LangConst;
import xyz.phanta.filcabref.init.FilingCabinetsItems;

import javax.annotation.Nullable;
import java.util.List;

public class ItemMaterial extends L9ItemSubs implements ParameterizedItemModel.IParamaterized {

    public ItemMaterial() {
        super(LangConst.ITEM_MATERIAL, Type.VALUES.length);
    }

    @Override
    public void getModelMutations(ItemStack stack, ParameterizedItemModel.Mutation m) {
        m.mutate("type", Type.getForMeta(stack.getMetadata()).name());
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flags) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (Type.getForMeta(stack.getMetadata())) {
            case CAPACITY_UPGRADE:
                tooltip.add(TextFormatting.GRAY + I18n.format(LangConst.TT_CAPACITY_UPGRADE));
                break;
        }
    }

    public enum Type {

        IRON_FRAMING, GOLD_FRAMING, CAPACITY_UPGRADE;

        public static final Type[] VALUES = values();

        public static Type getForMeta(int meta) {
            return VALUES[meta];
        }

        public int getMeta() {
            return ordinal();
        }

        public ItemStack newStack(int count) {
            return new ItemStack(FilingCabinetsItems.MATERIAL, count, getMeta());
        }

    }

}
