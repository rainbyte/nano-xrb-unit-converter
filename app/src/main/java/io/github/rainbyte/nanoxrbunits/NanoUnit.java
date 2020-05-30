package io.github.rainbyte.nanoxrbunits;

public enum NanoUnit {
    Nano(30),
    mNano(27),
    uNano(24),
    fNano(15),
    Raw(0);

    public static final int MAX_DIGITS = 39;

    private final int decimals;
    private final int integers;

    NanoUnit(int decimals) {
        this.decimals = decimals;
        this.integers = MAX_DIGITS - decimals;
    }

    public int getDecimals() {
        return decimals;
    }

    public int getIntegers() {
        return integers;
    }

    public String toRaws(String input) {
        String[] parts = input.split("\\.", 2);
        StringBuilder partL = new StringBuilder(parts[0]);
        while (partL.length() > 0 && partL.charAt(0) == '0') {
            partL.deleteCharAt(0);
        }

        StringBuilder sb = new StringBuilder();
        while (sb.length() < integers - partL.length()) {
            sb.append('0');
        }
        sb.append(partL);

        StringBuilder partR = new StringBuilder(parts.length == 2 ? parts[1] : "");
        while (partR.length() < decimals) {
            partR.append('0');
        }
        sb.append(partR);

        return sb.toString();
    }

    public String fromRaws(String raws) {
        int firstDigit = 0;
        while (firstDigit < raws.length() && raws.charAt(firstDigit) == '0') {
            firstDigit++;
        }
        int digits = raws.length() - firstDigit;
        if (digits == 0) return "";

        StringBuilder norm = new StringBuilder();
        while (norm.length() < MAX_DIGITS - digits) {
            norm.append('0');
        }
        norm.append(raws.subSequence(firstDigit, raws.length()));
        assert norm.length() == MAX_DIGITS;

        StringBuilder builder = new StringBuilder(norm.subSequence(0, integers));
        while (builder.length() > 1 && builder.charAt(0) == '0') {
            builder.deleteCharAt(0);
        }

        if (decimals > 0) {
            StringBuilder partR = new StringBuilder(
                    norm.subSequence(norm.length() - decimals, norm.length()));
            while (partR.length() > 0 && partR.charAt(partR.length() - 1) == '0') {
                partR.deleteCharAt(partR.length() - 1);
            }
            if (partR.length() > 0) {
                builder.append('.');
                builder.append(partR);
            }
        }

        // DecimalFormatSymbols.getInstance().decimalSeparator
        return builder.toString();
    }
}