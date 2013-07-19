package contacts.app.android.util;

/**
 * Utilities for strings.
 */
public final class StringUtils {

    /**
     * Determines that string is <code>null</code> or empty.
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    private StringUtils() {
    }

}
