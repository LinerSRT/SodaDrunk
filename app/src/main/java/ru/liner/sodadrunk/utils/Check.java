package ru.liner.sodadrunk.utils;

import java.util.Collection;
import java.util.Map;

/**
 * @author : "Line'R"
 * @mailto : serinity320@mail.com
 * @created : 17.04.2022, воскресенье
 **/
public class Check {
    public static boolean isEmpty(CharSequence str) {
        return isNull(str) || str.length() == 0;
    }

    public static boolean isEmpty(Object[] os) {
        return isNull(os) || os.length == 0;
    }

    public static boolean isEmpty(Collection<?> l) {
        return isNull(l) || l.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> m) {
        return isNull(m) || m.isEmpty();
    }

    public static boolean isNull(Object o) {
        return o == null;
    }
}
