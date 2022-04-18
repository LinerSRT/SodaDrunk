package ru.liner.sodadrunk.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.telecom.TelecomManager;

import androidx.annotation.NonNull;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author : "Line'R"
 * @mailto : serinity320@mail.com
 * @created : 18.04.2022, понедельник
 **/
public class Telecom {

    @SuppressLint("MissingPermission")
    public static boolean rejectCall(@NonNull Context context) {
        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            telecomManager.endCall();
            return true;
        } else {
            try {
                Method endCallMethod = telecomManager.getClass().getDeclaredMethod("endCall");
                endCallMethod.setAccessible(true);
                endCallMethod.invoke(telecomManager);
                return true;
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                try {
                    Method getITelephony = telecomManager.getClass().getDeclaredMethod("getITelephony");
                    getITelephony.setAccessible(true);
                    ITelephony telephony = (ITelephony) getITelephony.invoke(telecomManager);
                    if (telephony != null) {
                        telephony.endCall();
                        return true;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
        return false;
    }

    public static String trimNumber(@NonNull String number) {
        return number
                .replace(" ", "")
                .replace("(", "")
                .replace(")", "")
                .replace("-", "")
                .replace("+", "");
    }
}
