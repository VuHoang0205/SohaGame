
package com.s.sdk.utils;

public final class Validate {
    //private static final String TAG = Validate.class.getName();
    public static void notNull(Object arg, String name) {
        if (arg == null) {
            throw new NullPointerException("Argument '" + name + "' cannot be null");
        }
    }
}
