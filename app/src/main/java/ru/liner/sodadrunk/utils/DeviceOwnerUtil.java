package ru.liner.sodadrunk.utils;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;

/**
 * @author : "Line'R"
 * @mailto : serinity320@mail.com
 * @created : 16.04.2022, суббота
 **/
public class DeviceOwnerUtil {
    private static final String TAG = "DeviceOwnerUtil";

    public static boolean isDeviceOwnerApp(DevicePolicyManager devicePolicyManager, String packageName) {
        return devicePolicyManager.isDeviceOwnerApp(packageName);
    }

    public static boolean isApplicationHidden(DevicePolicyManager devicePolicyManager, ComponentName admin, String packageName) {
        try {
            return devicePolicyManager.isApplicationHidden(admin, packageName);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isUninstallBlocked(DevicePolicyManager devicePolicyManager, ComponentName admin, String packageName) {
        try {
            return devicePolicyManager.isUninstallBlocked(admin, packageName);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean setApplicationHidden(DevicePolicyManager devicePolicyManager, ComponentName admin, String packageName, boolean hidden) {
        try {
            return devicePolicyManager.setApplicationHidden(admin, packageName, hidden);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return false;
    }


    public static void setUninstallBlocked(DevicePolicyManager devicePolicyManager, ComponentName admin, String packageName, boolean uninstallBlocked) {
        try {
            devicePolicyManager.setUninstallBlocked(admin, packageName, uninstallBlocked);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

}