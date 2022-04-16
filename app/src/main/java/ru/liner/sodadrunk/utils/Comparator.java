package ru.liner.sodadrunk.utils;

/**
 * @author : "Line'R"
 * @mailto : serinity320@mail.com
 * @created : 27.11.2021, суббота
 **/
public abstract class Comparator<T> {
    public final T other;
    public Comparator(T other) {
        this.other = other;
    }
    public abstract boolean compare(T one, T other);
}