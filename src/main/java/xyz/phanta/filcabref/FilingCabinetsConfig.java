package xyz.phanta.filcabref;

import net.minecraftforge.common.config.Config;

@Config(modid = FilingCabinetsMod.MOD_ID)
public class FilingCabinetsConfig {

    @Config.Comment("Configuration for basic filing cabinets.")
    public static final CabinetConfig cabinetBasic = new CabinetConfig(256, 8192);

    @Config.Comment("Configuration for advanced filing cabinets.")
    public static final CabinetConfig cabinetAdvanced = new CabinetConfig(512, 8192);

    @Config.Comment({
            "Amount of additional capacity added per capacity upgrade.",
            "WARNING: Reducing this may cause data loss in filled filing cabinets!"
    })
    @Config.RangeInt(min = 1)
    public static int upgradeCapacity = 4096;

    @Config.Comment({
            "Maximum number of capacity upgrades that can be installed in a single filing cabinet.",
            "If this is reduced, existing cabinets will retain all of their upgrades beyond the limit."
    })
    @Config.RangeInt(min = 0, max = 64)
    public static int upgradeCountMax = 14;

    public static class CabinetConfig {

        CabinetConfig(int numSlots, int numItems) {
            this.numSlots = numSlots;
            this.numItems = numItems;
        }

        @Config.Comment({
                "The number of slots in the cabinet; corresponds to the number of distinct item types that can be held.",
                "WARNING: Reducing this may cause data loss in filing cabinets with more than that many item types!",
                "WARNING: Making this too large may make the cabinet GUI difficult to scroll through!"
        })
        @Config.RangeInt(min = 1)
        public int numSlots;

        @Config.Comment({
                "The capacity of the cabinet; corresponds to the total number of items that can be held across all slots.",
                "WARNING: Reducing this may cause data loss in filing cabinets with more than that many total items!"
        })
        @Config.RangeInt(min = 1)
        public int numItems;

    }

}
