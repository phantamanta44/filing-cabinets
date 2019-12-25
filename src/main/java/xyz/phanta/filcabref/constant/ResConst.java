package xyz.phanta.filcabref.constant;

import io.github.phantamanta44.libnine.util.render.TextureRegion;
import io.github.phantamanta44.libnine.util.render.TextureResource;
import net.minecraft.util.ResourceLocation;
import xyz.phanta.filcabref.FilingCabinetsMod;

public class ResConst {

    private static final String TEXTURES_BASE = "textures/";

    private static final String TEX_GUI_KEY = TEXTURES_BASE + "gui/";
    public static final TextureResource TEX_GUI_FILING_CABINET
            = FilingCabinetsMod.INSTANCE.newTextureResource(TEX_GUI_KEY + "filing_cabinet.png", 256, 256);
    public static final TextureRegion TEX_GUI_FILING_CABINET_BAR_SLOTS = TEX_GUI_FILING_CABINET.getRegion(176, 0, 34, 2);
    public static final TextureRegion TEX_GUI_FILING_CABINET_BAR_ITEMS = TEX_GUI_FILING_CABINET.getRegion(176, 2, 34, 2);
    public static final TextureRegion TEX_GUI_FILING_CABINET_SCROLL_THUMB = TEX_GUI_FILING_CABINET.getRegion(176, 4, 17, 11);

}
