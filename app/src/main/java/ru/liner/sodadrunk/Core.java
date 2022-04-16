package ru.liner.sodadrunk;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.Arrays;
import java.util.List;

import ru.liner.sodadrunk.math.Generator;
import ru.liner.sodadrunk.model.BlockedApplication;
import ru.liner.sodadrunk.model.BlockedContact;
import ru.liner.sodadrunk.utils.Broadcast;
import ru.liner.sodadrunk.utils.Comparator;
import ru.liner.sodadrunk.utils.Lists;
import ru.liner.sodadrunk.utils.PM;

/**
 * @author : "Line'R"
 * @mailto : serinity320@mail.com
 * @created : 15.04.2022, пятница
 **/
@SuppressLint("StaticFieldLeak")
public class Core extends Application {
    public static final String BLOCKED_CONTACT_LIST_KEY = "blocked_contact_list";
    public static final String ACTION_BLOCKED_CONTACT_LIST_CHANGED = "ru.liner.sodadrunk.ACTION_BLOCKED_CONTACT_LIST_CHANGED";
    public static final String BLOCKED_APP_LIST_KEY = "blocked_app_list";
    public static final String ACTION_BLOCKED_APP_LIST_CHANGED = "ru.liner.sodadrunk.ACTION_BLOCKED_APP_LIST_CHANGED";
    public static final String ACTION_SERVICE_STARTED = "ru.liner.sodadrunk.ACTION_SERVICE_STARTED";
    public static final String ACTION_SERVICE_STOPPED = "ru.liner.sodadrunk.ACTION_SERVICE_STOPPED";

    private static Context context;
    private static Handler handler;
    private static Core core;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        core = this;
        PM.init(this, "soda_prefs");
        Generator generator = new Generator();
        System.out.println(generator.toString());
        System.out.println(Arrays.toString(generator.getSuggestions()));
    }

    public static Context getContext() {
        return context;
    }

    public static Core getCore() {
        return core;
    }

    public static Handler getHandle() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        return handler;
    }

    public static List<BlockedApplication> getBlockedApps() {
        return PM.getList(BLOCKED_APP_LIST_KEY, BlockedApplication.class);
    }

    public static List<BlockedContact> getBlockedContacts() {
        return PM.getList(BLOCKED_CONTACT_LIST_KEY, BlockedContact.class);
    }

    public static void blockApplication(BlockedApplication blockedApplication) {
        List<BlockedApplication> currentBlockList = getBlockedApps();
        if(!Lists.contains(currentBlockList, new Comparator<BlockedApplication>(blockedApplication) {
            @Override
            public boolean compare(BlockedApplication one, BlockedApplication other) {
                return one.name.equals(other.name) || one.packageName.equals(other.packageName);
            }
        })){
            currentBlockList.add(blockedApplication);
            PM.putList(BLOCKED_APP_LIST_KEY, currentBlockList);
            Broadcast.send(ACTION_BLOCKED_CONTACT_LIST_CHANGED);
        }
    }

    public static void unblockApplication(BlockedApplication blockedApplication) {
        List<BlockedApplication> currentBlockList = getBlockedApps();
        if(Lists.contains(currentBlockList, new Comparator<BlockedApplication>(blockedApplication) {
            @Override
            public boolean compare(BlockedApplication one, BlockedApplication other) {
                return one.name.equals(other.name) || one.packageName.equals(other.packageName);
            }
        })) {
            currentBlockList.remove(blockedApplication);
            PM.putList(BLOCKED_APP_LIST_KEY, currentBlockList);
            Broadcast.send(ACTION_BLOCKED_CONTACT_LIST_CHANGED);
        }
    }


    public static void blockContact(BlockedContact blockedContact) {
        List<BlockedContact> currentBlockList = getBlockedContacts();
        if(!Lists.contains(currentBlockList, new Comparator<BlockedContact>(blockedContact) {
            @Override
            public boolean compare(BlockedContact one, BlockedContact other) {
                return one.number.equals(other.number) || one.name.equals(other.name);
            }
        })){
            currentBlockList.add(blockedContact);
            PM.putList(BLOCKED_CONTACT_LIST_KEY, currentBlockList);
            Broadcast.send(ACTION_BLOCKED_CONTACT_LIST_CHANGED);
        }
    }

    public static void unblockContact(BlockedContact blockedContact) {
        List<BlockedContact> currentBlockList = getBlockedContacts();
        int index = Lists.indexOf(currentBlockList, new Comparator<BlockedContact>(blockedContact) {
            @Override
            public boolean compare(BlockedContact one, BlockedContact other) {
                return one.number.equals(other.number) || one.name.equals(other.name);
            }
        });
        if(index != -1){
            currentBlockList.remove(index);
            PM.putList(BLOCKED_CONTACT_LIST_KEY, currentBlockList);
            Broadcast.send(ACTION_BLOCKED_CONTACT_LIST_CHANGED);
        }
    }

    public static void setContact(BlockedContact blockedContact) {
        List<BlockedContact> currentBlockList = getBlockedContacts();
        int index = Lists.indexOf(currentBlockList, new Comparator<BlockedContact>(blockedContact) {
            @Override
            public boolean compare(BlockedContact one, BlockedContact other) {
                return one.number.equals(other.number) || one.name.equals(other.name);
            }
        });
        if(index != -1){
            currentBlockList.set(index, blockedContact);
            PM.putList(BLOCKED_CONTACT_LIST_KEY, currentBlockList);
            Broadcast.send(ACTION_BLOCKED_CONTACT_LIST_CHANGED);
        }
    }

}
