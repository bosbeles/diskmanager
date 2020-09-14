package record.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * Enumeration for File units.
 */
public enum FileUnit {
    GB(1024 * 1024 * 1024),
    MB(1024 * 1024),
    KB(1024),
    B(1);

    private long multiplier;

    private FileUnit(long multiplier) {
        this.multiplier = multiplier;
    }

    public long getMultiplier() {
        return multiplier;
    }

    /**
     * Converts to length in bytes.
     *
     * @param n the number
     * @return the length in bytes.
     */
    public long toByte(long n) {
        return n * multiplier;
    }

    /**
     * Converts to length in bytes.
     *
     * @param n the number
     * @return the length in bytes.
     */
    public long toByte(double n) {
        return (long) (n * multiplier);
    }

    public static String humanReadable(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %cB", value / 1024.0, ci.current());
    }
}