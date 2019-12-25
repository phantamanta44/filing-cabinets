package xyz.phanta.filcabref.util;

public enum CountFormat {

    NORMAL {
        @Override
        public String format(int count) {
            return Integer.toString(count);
        }
    },
    SHORTHAND {
        @Override
        public String format(int count) {
            int magnitude = (int)Math.floor(Math.log10(count) / 3);
            if (magnitude == 0) {
                return Integer.toString(count);
            }
            return String.format("%.1f%s", count / Math.pow(10, magnitude * 3), MAGN_SUFF[magnitude]);
        }
    };

    private static final String[] MAGN_SUFF = { null, "K", "M", "B" };

    public static CountFormat getFormat(int count) {
        return count >= 100 ? SHORTHAND : NORMAL;
    }

    public abstract String format(int count);

}
