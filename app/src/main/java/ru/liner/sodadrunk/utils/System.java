package ru.liner.sodadrunk.utils;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.provider.Settings;
import android.provider.Telephony;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import ru.liner.sodadrunk.Core;
import ru.liner.sodadrunk.R;
import ru.liner.sodadrunk.model.ApplicationModel;

/**
 * @author : "Line'R"
 * @mailto : serinity320@mail.com
 * @created : 15.04.2022, пятница
 **/
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
public class System {
    public static List<ApplicationModel> getInstalledApplications(Context context) {
        List<ApplicationModel> applicationModelList = new ArrayList<>();
        Intent query = new Intent(Intent.ACTION_MAIN);
        query.addCategory(Intent.CATEGORY_DEFAULT);
        query.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(query, 0);
        for (ResolveInfo resolveInfo : resolveInfoList) {
            applicationModelList.add(new ApplicationModel(
                    resolveInfo.activityInfo.packageName,
                    resolveInfo.loadLabel(packageManager).toString(),
                    resolveInfo.activityInfo.loadIcon(packageManager)
            ));
        }
        return applicationModelList;
    }

    public static boolean isAccessibilityServiceRunning(Context context, Class<? extends AccessibilityService> accessibilityService){
            String enabledServices = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            String[] runningServices = enabledServices.split(":");
            for (String runningService : runningServices) {
                if (runningService.contains(accessibilityService.getName()))
                    return true;
            }
            return false;
    }

    public static boolean isAccessibilityServiceEnabled(Context context, Class<? extends AccessibilityService> accessibilityService) {
        ComponentName expectedComponentName = new ComponentName(context, accessibilityService);

        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(),  Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null)
            return false;

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);

        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);

            if (enabledService != null && enabledService.equals(expectedComponentName))
                return true;
        }

        return false;
    }

    public static void requestAccessibilityAccess(Activity activity, int requestCode) {
        new AlertDialog.Builder(activity)
                .setMessage(R.string.enable_service_tutorial)
                .setPositiveButton(R.string.grant_permission_button, (dialogInterface, i) -> activity.startActivityForResult(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), requestCode)).setNegativeButton(R.string.deny_permission_button, null)
                .create()
                .show();
    }

    public static String[] getPermissions() {
        return new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Build.VERSION.SDK_INT >= 26 ? Manifest.permission.READ_PHONE_NUMBERS : Manifest.permission.INTERNET,
                Build.VERSION.SDK_INT >= 26 ? Manifest.permission.ANSWER_PHONE_CALLS : Manifest.permission.INTERNET
        };
    }

    @Nullable
    public static String getDefaultSmsAppPackageName(@NonNull final Context context) {
        return Telephony.Sms.getDefaultSmsPackage(context);
    }

    public static List<String> getDefaultDialerAppPackageName(@NonNull final Context context) {
        List<String> packageNames = new ArrayList<>();
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfoList) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            packageNames.add(activityInfo.applicationInfo.packageName);
        }
        return packageNames;
    }

    public static void endCall(@NonNull final Context context) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        Method endCallMethod = telecomManager.getClass().getDeclaredMethod("endCall");
        endCallMethod.setAccessible(true);
        endCallMethod.invoke(telecomManager);
    }

    public static ActivityInfo getActivityInfo(Context context, ComponentName componentName) {
        try {
            return context.getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @Nullable
    public static ResolveInfo resolveInfoFor(@NonNull String packageName) {
        Intent query = new Intent(Intent.ACTION_MAIN);
        query.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfoList = Core.getContext().getPackageManager().queryIntentActivities(query, 0);
        for (ResolveInfo resolveInfo : resolveInfoList) {
            if (resolveInfo.activityInfo.packageName.equals(packageName))
                return resolveInfo;
        }
        return null;
    }
}
