package ru.liner.sodadrunk.service;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

import ru.liner.sodadrunk.Core;
import ru.liner.sodadrunk.model.BlockedContact;
import ru.liner.sodadrunk.utils.PM;

/**
 * @author : "Line'R"
 * @mailto : serinity320@mail.com
 * @created : 17.04.2022, воскресенье
 **/
public class CallReceiver extends BroadcastReceiver {
    private static final String TAG = "CallReceiver";
    private boolean outgoingCallCatch = false;
    private boolean rejected = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        String phoneNumber;
        switch (intent.getAction()) {
            case Intent.ACTION_NEW_OUTGOING_CALL:
                outgoingCallCatch = true;
                phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                if (!(Boolean) PM.get("control_enabled", false))
                    return;
                for (BlockedContact blockedContact : Core.getBlockedContacts()) {
                    if (!blockedContact.preventCalls)
                        continue;
                    if (trimNumber(blockedContact.number).equals(trimNumber(phoneNumber))) {
                        rejectCall(context);
                        break;
                    }
                }
                break;
            case "android.intent.action.PHONE_STATE":
                String state = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                phoneNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                if (!outgoingCallCatch && state.equals("OFFHOOK") && phoneNumber != null) {
                    if (!(Boolean) PM.get("control_enabled", false))
                        return;
                    for (BlockedContact blockedContact : Core.getBlockedContacts()) {
                        if (!blockedContact.preventCalls)
                            continue;
                        if (trimNumber(blockedContact.number).equals(trimNumber(phoneNumber))) {
                            rejectCall(context);
                            break;
                        }
                    }
                }
                break;
        }
    }

    @SuppressLint("MissingPermission")
    protected void rejectCall(@NonNull Context context) {
        rejected = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
            try {
                telecomManager.endCall();
                rejected = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            try {
                @SuppressLint("DiscouragedPrivateApi") Method m = tm.getClass().getDeclaredMethod("getITelephony");
                m.setAccessible(true);
                ITelephony telephony = (ITelephony) m.invoke(tm);
                if (telephony != null) {
                    telephony.endCall();
                    rejected = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (rejected) {
            new Thread(() -> {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                outgoingCallCatch = false;
                rejected = false;
            }).start();
        }
    }


    private String trimNumber(String number) {
        return number
                .replace(" ", "")
                .replace("(", "")
                .replace(")", "")
                .replace("-", "")
                .replace("+", "");
    }
}
