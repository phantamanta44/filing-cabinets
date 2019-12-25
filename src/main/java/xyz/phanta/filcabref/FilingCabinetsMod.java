package xyz.phanta.filcabref;

import io.github.phantamanta44.libnine.Virtue;
import io.github.phantamanta44.libnine.util.L9CreativeTab;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import xyz.phanta.filcabref.block.filing_cabinet.BlockFilingCabinet;

@Mod(modid = FilingCabinetsMod.MOD_ID, version = FilingCabinetsMod.VERSION, useMetadata = true)
public class FilingCabinetsMod extends Virtue {

    public static final String MOD_ID = "filcabref";
    public static final String VERSION = "1.0.0";

    @SuppressWarnings("NotNullFieldNotInitialized")
    @Mod.Instance(MOD_ID)
    public static FilingCabinetsMod INSTANCE;

    @SuppressWarnings("NotNullFieldNotInitialized")
    @SidedProxy(
            clientSide = "xyz.phanta.filcabref.client.ClientProxy",
            serverSide = "xyz.phanta.filcabref.CommonProxy")
    public static CommonProxy PROXY;

    @SuppressWarnings("NotNullFieldNotInitialized")
    public static Logger LOGGER;

    public FilingCabinetsMod() {
        super(MOD_ID, new L9CreativeTab(MOD_ID, () -> BlockFilingCabinet.Type.BASIC.newStack(1)));
    }

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
        PROXY.onPreInit(event);
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        PROXY.onInit(event);
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        PROXY.onPostInit(event);
    }

}
