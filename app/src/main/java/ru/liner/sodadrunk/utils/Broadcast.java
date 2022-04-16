package ru.liner.sodadrunk.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import ru.liner.sodadrunk.Core;


@SuppressWarnings("unused")
public abstract class Broadcast extends BroadcastReceiver {
    private final String[] actions;
    protected final Context context;

    public Broadcast(Context context, String[] actions) {
        this.actions = actions;
        this.context = context;
    }

    public Broadcast(Context context, String action) {
        this(context, new String[]{action});
    }

    public Broadcast(Context context) {
        this(context, new String[]{});
    }

    public Broadcast(String[] actions) {
        this(Core.getContext(), actions);
    }

    public Broadcast(String action) {
        this(new String[]{action});
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        handleChanged(intent);
    }

    public abstract void handleChanged(Intent intent);


    public void setListening(boolean listening) {
        if (listening) {
            IntentFilter intentFilter = new IntentFilter();
            for (String action : actions)
                intentFilter.addAction(action);
            context.registerReceiver(this, intentFilter);
        } else {
            try {
                context.unregisterReceiver(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void send(String action) {
        send(new Intent(action));
    }

    public static void send(Intent intent) {
        send(Core.getContext(), intent);
    }

    public static void send(Context context, Intent intent) {
        context.sendBroadcast(intent);
    }
}
