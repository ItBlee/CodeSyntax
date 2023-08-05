package Util;

import java.util.ResourceBundle;

public final class ApiPropertyUtil {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("api");

    public ApiPropertyUtil() {
        throw new AssertionError();
    }

    public static String getString(String key) {
        return BUNDLE.getString(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(BUNDLE.getString(key));
    }

    public static Object getObject(String key) {
        return BUNDLE.getObject(key);
    }

}
