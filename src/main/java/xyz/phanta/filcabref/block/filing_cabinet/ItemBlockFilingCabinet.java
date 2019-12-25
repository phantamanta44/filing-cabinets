package xyz.phanta.filcabref.block.filing_cabinet;

import io.github.phantamanta44.libnine.client.model.ParameterizedItemModel;
import io.github.phantamanta44.libnine.item.L9ItemBlockStated;
import net.minecraft.item.ItemStack;

public class ItemBlockFilingCabinet extends L9ItemBlockStated implements ParameterizedItemModel.IParamaterized {

    public ItemBlockFilingCabinet(BlockFilingCabinet block) {
        super(block);
    }

    @Override
    public void getModelMutations(ItemStack stack, ParameterizedItemModel.Mutation m) {
        m.mutate("type", BlockFilingCabinet.Type.getForItemMeta(stack.getMetadata()).name());
    }

}
