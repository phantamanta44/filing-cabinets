package xyz.phanta.filcabref;

import net.minecraftforge.common.config.Config;

@Config(modid = FilingCabinetsMod.MOD_ID)
public class FilingCabinetsConfig {

    @Config.Comment("Configuration for basic filing cabinets.")
    public static final CabinetConfig cabinetBasic = new CabinetConfig(270, 1755);

    @Config.Comment("Configuration for advanced filing cabinets.")
    public static final CabinetConfig cabinetAdvanced = new CabinetConfig(540, 3510);

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
        @Config.RangeInt(min = 1, max = 1024)
        public int numSlots;

        @Config.Comment({
                "The capacity of the cabinet; corresponds to the total number of items that can be held across all slots.",
                "WARNING: Reducing this may cause data loss in filing cabinets with more than that many total items!"
        })
        @Config.RangeInt(min = 1)
        public int numItems;

    }

}
