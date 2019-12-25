package xyz.phanta.filcabref.init;

import io.github.phantamanta44.libnine.InitMe;
import net.minecraftforge.fml.common.registry.GameRegistry;
import xyz.phanta.filcabref.FilingCabinetsMod;
import xyz.phanta.filcabref.constant.LangConst;
import xyz.phanta.filcabref.item.ItemMaterial;

@SuppressWarnings("NotNullFieldNotInitialized")
public class FilingCabinetsItems {

    @GameRegistry.ObjectHolder(FilingCabinetsMod.MOD_ID + ":" + LangConst.ITEM_MATERIAL)
    public static ItemMaterial MATERIAL;

    @InitMe(FilingCabinetsMod.MOD_ID)
    public static void init() {
        new ItemMaterial();
    }

}
