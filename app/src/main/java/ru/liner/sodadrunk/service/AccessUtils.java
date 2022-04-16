package ru.liner.sodadrunk.service;

import android.content.ComponentName;
import android.content.Context;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.liner.sodadrunk.utils.System;

/**
 * @author : "Line'R"
 * @mailto : serinity320@mail.com
 * @created : 15.04.2022, пятница
 **/
public class AccessUtils {
    private static AccessibilityNodeInfo getRootNodeInfo(@NonNull AccessibilityEvent event) {
        return event.getSource();
    }

    public static List<AccessibilityNodeInfo> findNodesWhere(@NonNull AccessibilityEvent event, String text, @NonNull String className, @NonNull String contentDescription) {
        AccessibilityNodeInfo nodeInfo = getRootNodeInfo(event);
        if (nodeInfo == null)
            return new ArrayList<>();
        List<AccessibilityNodeInfo> nodeInfoList = nodeInfo.findAccessibilityNodeInfosByText(text);
        List<AccessibilityNodeInfo> resultList = new ArrayList<>();
        for (AccessibilityNodeInfo node : nodeInfoList) {
            if (node.getClassName() == null || node.getContentDescription() == null)
                continue;
            if (node.getClassName().toString().equals(className) && node.getContentDescription().toString().equals(contentDescription))
                resultList.add(node);
        }
        return resultList;
    }

    public static List<AccessibilityNodeInfo> findNodesByText(@NonNull AccessibilityEvent event, @NonNull String text) {
        AccessibilityNodeInfo nodeInfo = getRootNodeInfo(event);
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> nodeInfoList = nodeInfo.findAccessibilityNodeInfosByText(text);
            return nodeInfoList.isEmpty() ? new ArrayList<>() : nodeInfoList;
        }
        return new ArrayList<>();

    }

    public static AccessibilityNodeInfo getParent(AccessibilityNodeInfo accessibilityNodeInfo) {
        return accessibilityNodeInfo.getParent() != null ? getParent(accessibilityNodeInfo.getParent()) : accessibilityNodeInfo;
    }

    @NonNull
    public static String getCurrentActivity(Context context, AccessibilityEvent accessibilityEvent) {
        ComponentName componentName = new ComponentName(accessibilityEvent.getPackageName().toString(), accessibilityEvent.getClassName().toString());
        if (System.getActivityInfo(context, componentName) != null) {
            return componentName.flattenToString();
        }
        return "";
    }

    public static String getItemText(AccessibilityNodeInfo accessibilityNodeInfo, Integer[] numArr) {
        for (Integer num : numArr) {
            if (accessibilityNodeInfo == null || accessibilityNodeInfo.getChildCount() <= num.intValue()) {
                return "null";
            }
            accessibilityNodeInfo = accessibilityNodeInfo.getChild(num.intValue());
        }
        if (accessibilityNodeInfo == null) {
            return "null";
        }
        if (accessibilityNodeInfo.getContentDescription() != null) {
            return String.valueOf(accessibilityNodeInfo.getContentDescription());
        }
        if (accessibilityNodeInfo.getText() != null) {
            return String.valueOf(accessibilityNodeInfo.getText());
        }
        return "null";
    }


}
