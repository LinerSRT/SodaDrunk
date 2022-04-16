package ru.liner.sodadrunk.model;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

/**
 * @author : "Line'R"
 * @mailto : serinity320@mail.com
 * @created : 16.04.2022, суббота
 **/
public class ApplicationModel {
    public final String packageName;
    public final String applicationName;
    public final transient Drawable icon;

    public ApplicationModel(String packageName, String applicationName, Drawable icon) {
        this.packageName = packageName;
        this.applicationName = applicationName;
        this.icon = icon;
    }

    @NonNull
    @Override
    public String toString() {
        return "ApplicationModel{" +
                "packageName='" + packageName + '\'' +
                ", applicationName='" + applicationName + '\'' +
                '}';
    }
}
