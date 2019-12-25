package xyz.phanta.filcabref.item;

import io.github.phantamanta44.libnine.client.model.ParameterizedItemModel;
import io.github.phantamanta44.libnine.item.L9ItemSubs;
import net.minecraft.item.ItemStack;
import xyz.phanta.filcabref.constant.LangConst;

public class ItemMaterial extends L9ItemSubs implements ParameterizedItemModel.IParamaterized {

    public ItemMaterial() {
        super(LangConst.ITEM_MATERIAL, Type.VALUES.length);
    }

    @Override
    public void getModelMutations(ItemStack stack, ParameterizedItemModel.Mutation m) {
        m.mutate("type", Type.getForMeta(stack.getMetadata()).name());
    }

    public enum Type {

        IRON_FRAMING, GOLD_FRAMING;

        public static final Type[] VALUES = values();

        public static Type getForMeta(int meta) {
            return VALUES[meta];
        }

        public int getMeta() {
            return ordinal();
        }

    }

}
