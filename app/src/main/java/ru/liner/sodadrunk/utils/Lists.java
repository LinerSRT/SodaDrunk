package ru.liner.sodadrunk.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : "Line'R"
 * @mailto : serinity320@mail.com
 * @created : 16.04.2022, суббота
 **/
@SuppressWarnings("unused")
public class Lists {
    public static <T> List<T> filter(List<T> objectList, Comparator<T> comparator) {
        List<T> filtered = new ArrayList<>();
        if (!objectList.isEmpty())
            for (int i = 0; i < objectList.size(); i++) {
                if (comparator.compare(objectList.get(i), comparator.other)) {
                    filtered.add(objectList.get(i));
                }
            }
        return filtered;
    }

    public static <T> int indexOf(List<T> objectList, Comparator<T> comparator){
        for (int i = 0; i < objectList.size(); i++) {
            if(comparator.compare(objectList.get(i), comparator.other))
                return i;
        }
        return -1;
    }

    public static <T> boolean contains(List<T> objectList, Comparator<T> comparator){
        for (int i = 0; i < objectList.size(); i++) {
            if(comparator.compare(objectList.get(i), comparator.other))
                return true;
        }
        return false;
    }
}
