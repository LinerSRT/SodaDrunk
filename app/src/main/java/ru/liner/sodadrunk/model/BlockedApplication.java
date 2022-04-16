package ru.liner.sodadrunk.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author : "Line'R"
 * @mailto : serinity320@mail.com
 * @created : 16.04.2022, суббота
 **/
public class BlockedApplication implements Serializable {
    public final String name;
    public final String packageName;

    public BlockedApplication(String name, String packageName) {
        this.name = name;
        this.packageName = packageName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockedApplication that = (BlockedApplication) o;
        return name.equals(that.name) && packageName.equals(that.packageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, packageName);
    }

    @NonNull
    @Override
    public String toString() {
        return "BlockedApplication{" +
                "name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                '}';
    }
}
