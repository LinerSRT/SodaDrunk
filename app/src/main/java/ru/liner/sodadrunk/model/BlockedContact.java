package ru.liner.sodadrunk.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * @author : "Line'R"
 * @mailto : serinity320@mail.com
 * @created : 16.04.2022, суббота
 **/
public class BlockedContact implements Serializable {
    public final String name;
    public final String number;
    public boolean preventCalls;
    public boolean preventMessaging;

    public BlockedContact(String name, String number) {
        this.name = name;
        this.number = number;
        this.preventCalls = true;
        this.preventMessaging = true;
    }

    @NonNull
    @Override
    public String toString() {
        return "BlockedContact{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", preventCalls=" + preventCalls +
                ", preventMessaging=" + preventMessaging +
                '}';
    }
}
