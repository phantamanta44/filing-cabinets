package xyz.phanta.filcabref.init;

import io.github.phantamanta44.libnine.InitMe;
import net.minecraftforge.fml.common.registry.GameRegistry;
import xyz.phanta.filcabref.FilingCabinetsMod;
import xyz.phanta.filcabref.block.filing_cabinet.BlockFilingCabinet;
import xyz.phanta.filcabref.constant.LangConst;

@SuppressWarnings("NotNullFieldNotInitialized")
public class FilingCabinetsBlocks {

    @GameRegistry.ObjectHolder(FilingCabinetsMod.MOD_ID + ":" + LangConst.BLOCK_FILING_CABINET)
    public static BlockFilingCabinet FILING_CABINET;

    @InitMe(FilingCabinetsMod.MOD_ID)
    public static void init() {
        new BlockFilingCabinet();
    }

}
