package ru.liner.sodadrunk.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author : "Line'R"
 * @mailto : serinity320@mail.com
 * @created : 17.04.2022, воскресенье
 **/
public class PhoneReceiver extends BroadcastReceiver {
    private static final String RINGING = "RINGING";
    private static final String OFFHOOK = "OFFHOOK";
    private static final String IDLE = "IDLE";
    private Callback callback;
    private String receivedNumber;
    private boolean outgoingCall;
    @CallingState
    private int callingState = CallingState.IDLE;

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        if (isEmpty(intentAction))
            return;
        if (intentAction.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            outgoingCall = true;
            String outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            if (!isEmpty(outgoingNumber)) {
                receivedNumber = trimNumber(outgoingNumber);
                callingState = CallingState.OUTGOING_START;
                if (callback != null)
                    callback.onStateChanged(callingState, receivedNumber);
            }
        } else if (intentAction.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String stringState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if (!isEmpty(phoneNumber)) {
                receivedNumber = trimNumber(phoneNumber);
                switch (stringState) {
                    case RINGING:
                        callingState = CallingState.INCOMING_START;
                        outgoingCall = false;
                        if (callback != null)
                            callback.onStateChanged(callingState, receivedNumber);
                        break;
                    case OFFHOOK:
                        if (!outgoingCall) {
                            callingState = CallingState.INCOMING;
                            if (callback != null)
                                callback.onStateChanged(callingState, receivedNumber);
                        }
                        break;
                    case IDLE:
                        callingState = outgoingCall ? CallingState.OUTGOING_END : CallingState.INCOMING_END;
                        if (callback != null)
                            callback.onStateChanged(callingState, receivedNumber);
                        break;
                }
            }
        }
    }

    @SuppressLint("InlinedApi")
    @RequiresPermission(allOf = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_NUMBERS
    })
    public void register(Context context, Callback callback) {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
            filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
            filter.setPriority(Integer.MAX_VALUE);
            context.registerReceiver(this, filter);
            this.callback = callback;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void unregister(Context context) {
        try {
            context.unregisterReceiver(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CallingState
    public int getCallingState() {
        return callingState;
    }

    @Nullable
    public String getReceivedNumber() {
        return receivedNumber;
    }

    private boolean isEmpty(@Nullable String string) {
        return string == null || string.length() == 0;
    }

    public static String trimNumber(@NonNull String number) {
        return number
                .replace(" ", "")
                .replace("(", "")
                .replace(")", "")
                .replace("-", "")
                .replace("+", "");
    }

    @IntDef({CallingState.INCOMING_START, CallingState.INCOMING, CallingState.INCOMING_END, CallingState.OUTGOING_START, CallingState.OUTGOING_END, CallingState.IDLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CallingState {
        int INCOMING_START = 0;
        int INCOMING_END = 1;
        int INCOMING = 2;
        int OUTGOING_START = 3;
        int OUTGOING_END = 4;
        int IDLE = 5;
    }

    public interface Callback {
        void onStateChanged(@CallingState int state, @NonNull String phoneNumber);
    }
}