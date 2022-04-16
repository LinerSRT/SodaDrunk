package ru.liner.sodadrunk.binder;

import android.content.pm.ResolveInfo;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import ru.liner.sodadrunk.R;
import ru.liner.sodadrunk.genericadapter.Binder;
import ru.liner.sodadrunk.model.BlockedApplication;
import ru.liner.sodadrunk.utils.System;

/**
 * @author : "Line'R"
 * @mailto : serinity320@mail.com
 * @created : 15.04.2022, пятница
 **/
public class BlockedApplicationBinder extends Binder<BlockedApplication> {
    private TextView blockedName;
    private ImageView blockedIcon;

    @Override
    public void init() {
        blockedName = find(R.id.blocked_holder_appName);
        blockedIcon = find(R.id.blocked_holder_appIcon);
    }

    @Override
    public void bind(@NonNull BlockedApplication model) {
        blockedName.setText(String.format("%s - %s", model.name, model.packageName));
        new Thread(() -> {
            ResolveInfo resolveInfo = System.resolveInfoFor(model.packageName);
            if (resolveInfo != null)
                blockedIcon.post(() -> blockedIcon.setImageDrawable(resolveInfo.loadIcon(itemView.getContext().getPackageManager())));
        }).start();
    }

    @Override
    public int getDragDirections() {
        return 0;
    }

    @Override
    public int getSwipeDirections() {
        return 0;
    }

    @Override
    public void onCentered(BlockedApplication model) {

    }

    @Override
    public void onNotCentered(BlockedApplication model) {

    }
}
