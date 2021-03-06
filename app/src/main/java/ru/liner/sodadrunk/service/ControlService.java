package ru.liner.sodadrunk.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;
import java.util.List;

import ru.liner.sodadrunk.Core;
import ru.liner.sodadrunk.model.BlockedApplication;
import ru.liner.sodadrunk.model.BlockedContact;
import ru.liner.sodadrunk.utils.Broadcast;
import ru.liner.sodadrunk.utils.PM;
import ru.liner.sodadrunk.utils.System;
import ru.liner.sodadrunk.utils.Telecom;

/**
 * @author : "Line'R"
 * @mailto : serinity320@mail.com
 * @created : 15.04.2022, пятница
 **/
@SuppressLint("WrongConstant | MissingPermission")
public class ControlService extends AccessibilityService {
    private static final String TAG = ControlService.class.getSimpleName();
    private PhoneReceiver phoneReceiver;
    private static final boolean enableLogging = false;
    private static ControlService service;
    private AccessibilityNodeInfo lastNode;
    private AccessibilityEvent lastEvent;
    private boolean firstLaunch = true;


    @Override
    public void onCreate() {
        super.onCreate();
        PM.init(getBaseContext(), "soda_prefs");
        phoneReceiver = new PhoneReceiver();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        service = this;
        WatchingApps.DiallerQuery.PACKAGE_LIST.addAll(System.getDefaultDialerAppPackageName(this));
        WatchingApps.MessagingQuery.SUPPORTED_LIST.add(System.getDefaultSmsAppPackageName(this));
        Broadcast.send(Core.ACTION_SERVICE_STARTED);
        phoneReceiver.register(this, (state, phoneNumber) -> {
            switch (state) {
                case PhoneReceiver.CallingState.OUTGOING_START:
                    if (!(Boolean) PM.get("control_enabled", false))
                        break;
                    for (BlockedContact blockedContact : Core.getBlockedContacts()) {
                        if (!blockedContact.preventCalls)
                            continue;
                        if (Telecom.trimNumber(blockedContact.number).equals(Telecom.trimNumber(phoneNumber))) {
                            Telecom.rejectCall(this);
                            break;
                        }
                    }
                    break;
                case PhoneReceiver.CallingState.OUTGOING_END:
                case PhoneReceiver.CallingState.IDLE:
                case PhoneReceiver.CallingState.INCOMING:
                case PhoneReceiver.CallingState.INCOMING_END:
                case PhoneReceiver.CallingState.INCOMING_START:
                    break;
            }
        });
    }

    private boolean validateEvent(AccessibilityEvent accessibilityEvent) {
        if (!(Boolean) PM.get("control_enabled", false))
            return false;
        if (accessibilityEvent == null)
            return false;
        AccessibilityNodeInfo source = accessibilityEvent.getSource();
        if (source == null)
            return false;
        return AccessUtils.getParent(source) != null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (!validateEvent(accessibilityEvent))
            return;
        AccessibilityNodeInfo source = accessibilityEvent.getSource();
        if (source == null)
            return;
        source.refresh();
        lastNode = AccessUtils.getParent(source);
        lastEvent = accessibilityEvent;
        onWindowContentChanged();

    }

    @SuppressLint({"MissingPermission", "NewApi"})
    public void onWindowContentChanged() {
        String packageName = String.valueOf(lastNode.getPackageName());
        String activityName = AccessUtils.getCurrentActivity(getBaseContext(), lastEvent);
        if (firstLaunch) {
            firstLaunch = false;
            new Thread(() -> {
                performGlobalAction(GLOBAL_ACTION_BACK);
                if (!packageName.equals(getPackageName()))
                    performGlobalAction(GLOBAL_ACTION_BACK);
                if (!packageName.equals(getPackageName()))
                    performGlobalAction(GLOBAL_ACTION_BACK);
                if (!packageName.equals(getPackageName()))
                    performGlobalAction(GLOBAL_ACTION_BACK);
                if (!packageName.equals(getPackageName()))
                    performGlobalAction(GLOBAL_ACTION_BACK);
            }).start();
            return;
        }
        if (enableLogging)
            Log.d(TAG, "Current application: " + packageName + " | " + activityName);
        if (WatchingApps.AdminControl.SUPPORTED_LIST.contains(activityName) && (Boolean) PM.get("admin_enabled", false)) {
            if (enableLogging)
                Log.d(TAG, "Remove device admin detected, blocking...");
            performGlobalAction(GLOBAL_ACTION_HOME);
        }
        //TODO Temporary disabled
        /*if (WatchingApps.DiallerQuery.PACKAGE_LIST.contains(packageName) || WatchingApps.DiallerQuery.ACTIVITY_LIST.contains(activityName) || telecomManager.isInCall()) {
            if (enableLogging)
                Log.d(TAG, "Detected dialer action...");
            try {
                for (BlockedContact blockedContact : Core.getBlockedContacts()) {
                    if (!blockedContact.preventCalls)
                        continue;
                    List<AccessibilityNodeInfo> visibleContactInfo = AccessUtils.findNodesByText(lastEvent, blockedContact.name);
                    List<AccessibilityNodeInfo> visiblePhoneInfo = AccessUtils.findNodesByText(lastEvent, blockedContact.number);
                    if (!visibleContactInfo.isEmpty() || !visiblePhoneInfo.isEmpty()) {
                        if (enableLogging)
                            Log.d(TAG, "Found blocked contact in dialer: " + blockedContact.name + " | " + blockedContact.number + ". Ending call!");
                        System.endCall(getBaseContext());
                    }
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
                if (enableLogging)
                    Log.d(TAG, "An error occur while ending call");
            }
        } else */
        if (WatchingApps.MessagingQuery.SUPPORTED_LIST.contains(packageName) || WatchingApps.MessagingQuery.SUPPORTED_LIST.contains(activityName.replace("/", ""))) {
            if (enableLogging)
                Log.d(TAG, "Detected messenger action...");
            for (BlockedContact blockedContact : Core.getBlockedContacts()) {
                List<AccessibilityNodeInfo> nodeInfoList = AccessUtils.findNodesWhere(lastEvent, blockedContact.name, "android.widget.TextView", blockedContact.name);
                if (!nodeInfoList.isEmpty() && blockedContact.preventMessaging) {
                    if (enableLogging)
                        Log.d(TAG, "Found blocked contact in messenger: " + blockedContact.name + " | " + blockedContact.number + ". Going back!");
                    performGlobalAction(GLOBAL_ACTION_BACK);
                    return;
                }
            }
        } else {
            if (enableLogging)
                Log.d(TAG, "Detected application action...");
            for (BlockedApplication blockedApplication : Core.getBlockedApps()) {
                if (blockedApplication.packageName.equals(packageName)) {
                    if (enableLogging)
                        Log.d(TAG, "Found blocked application: " + blockedApplication.name + " | " + blockedApplication.packageName + ". Going home!");
                    performGlobalAction(GLOBAL_ACTION_HOME);
                }
            }
        }
    }

    @Override
    public void onInterrupt() {
        service = null;
        PM.put("control_enabled", false);
        Broadcast.send(Core.ACTION_SERVICE_STOPPED);
        phoneReceiver.unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        service = null;
        PM.put("control_enabled", false);
        Broadcast.send(Core.ACTION_SERVICE_STOPPED);
        phoneReceiver.unregister(this);
    }

    public static boolean isStart() {
        return service != null;
    }
}
