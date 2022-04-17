package ru.liner.sodadrunk.compat;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ru.liner.sodadrunk.R;

/**
 * @author : "Line'R"
 * @mailto : serinity320@mail.com
 * @created : 17.04.2022, воскресенье
 **/

public class Permissions {
    private static final String TAG = Permissions.class.getName();
    private static final int REQUEST_CODE = (Permissions.class.hashCode() & 0xffff);
    private static final Map<String, Boolean> permissionsResults = new ConcurrentHashMap<>();
    public static final String RECEIVE_SMS = "android.permission.RECEIVE_SMS";
    public static final String WRITE_SMS = "android.permission.WRITE_SMS";
    public static final String CALL_PHONE = "android.permission.CALL_PHONE";
    public static final String READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";
    public static final String READ_PRIVILEGED_PHONE_STATE = "android.permission.READ_PRIVILEGED_PHONE_STATE";
    public static final String READ_CONTACTS = "android.permission.READ_CONTACTS";
    public static final String VIBRATE = "android.permission.VIBRATE";
    public static final String ANSWER_PHONE_CALLS = "android.permission.ANSWER_PHONE_CALLS";
    public static final String PROCESS_OUTGOING_CALLS = "android.permission.PROCESS_OUTGOING_CALLS";
    public static final String READ_CALL_LOG = "android.permission.READ_CALL_LOG";

    private static List<String> PERMISSIONS = new ArrayList<>(Arrays.asList(
            RECEIVE_SMS,
            WRITE_SMS,
            CALL_PHONE,
            READ_PHONE_STATE,
            READ_PRIVILEGED_PHONE_STATE,
            READ_CONTACTS,
            PROCESS_OUTGOING_CALLS,
            READ_CALL_LOG,
            VIBRATE
    ));

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Log.d(TAG, "Requesting extra permissions for PIE and higher.");
            PERMISSIONS.add(ANSWER_PHONE_CALLS);
        }
    }

    public static boolean isGranted(@NonNull Context context, @NonNull String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        Boolean result = permissionsResults.get(permission);
        if (result == null) {
            int permissionCheck = ContextCompat.checkSelfPermission(context, permission);
            result = (permissionCheck == PackageManager.PERMISSION_GRANTED);
            permissionsResults.put(permission, result);
        }
        return result;
    }

    /**
     * Checks for permissions and notifies the user if they aren't granted
     **/
    public static void notifyIfNotGranted(@NonNull Context context) {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (String permission : PERMISSIONS) {
            if (!isGranted(context, permission)) {
                if (count > 0) {
                    sb.append("\n");
                }
                String info = getPermissionInfo(context, permission);
                sb.append(info);
                sb.append(";");
                count++;
            }
        }

        if (count > 0) {
            int duration;
            String message = context.getString(R.string.app_name) + " ";
            if (count == 1) {
                duration = Toast.LENGTH_SHORT;
                message += "seed permission" + ":\n" + sb.toString();
            } else {
                duration = Toast.LENGTH_LONG;
                message += "need permissions" + ":\n" + sb.toString();
            }
            Toast.makeText(context, message, duration).show();
        }
    }

    /**
     * Checks for permission and notifies if it isn't granted
     **/
    public static boolean notifyIfNotGranted(@NonNull Context context, @NonNull String permission) {
        if (!isGranted(context, permission)) {
            notify(context, permission);
            return true;
        }
        return false;
    }

    /**
     * Returns information string about permission
     **/
    @Nullable
    private static String getPermissionInfo(@NonNull Context context, @NonNull String permission) {
        context = context.getApplicationContext();
        PackageManager pm = context.getPackageManager();
        PermissionInfo info = null;
        try {
            info = pm.getPermissionInfo(permission, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException ex) {
            Log.w(TAG, ex);
        }

        if (info != null) {
            CharSequence label = info.loadLabel(pm);
            if (label == null) {
                label = info.nonLocalizedLabel;
            }
            return label.toString();
        }

        return null;
    }

    /**
     * Notifies the user if permission isn't granted
     **/
    private static void notify(@NonNull Context context, @NonNull String permission) {
        String info = getPermissionInfo(context, permission);
        if (info != null) {
            String message = context.getString(R.string.app_name) + " need permission:\n" + info + ";";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Checks for permissions and shows a dialog for permission granting
     **/
    public static void checkAndRequest(@NonNull Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new LinkedList<>();
            for (String permission : PERMISSIONS) {
                if (!isGranted(context, permission)) {
                    permissions.add(permission);
                }
            }
            if (permissions.size() > 0) {
                ActivityCompat.requestPermissions(context, permissions.toArray(new String[0]), REQUEST_CODE);
            }
        }
    }

    /**
     * Resets permissions results cache
     **/
    public static void invalidateCache() {
        permissionsResults.clear();
    }

    /**
     * Saves the results of permission granting request
     **/
    public static void onRequestPermissionsResult(int requestCode,
                                                  @NonNull String[] permissions,
                                                  @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE &&
                permissions.length == grantResults.length) {
            for (int i = 0; i < permissions.length; i++) {
                boolean result = (grantResults[i] == PackageManager.PERMISSION_GRANTED);
                permissionsResults.put(permissions[i], result);
            }
        }
    }
}