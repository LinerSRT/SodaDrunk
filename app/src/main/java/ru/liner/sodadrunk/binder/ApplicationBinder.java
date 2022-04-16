package ru.liner.sodadrunk.binder;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import ru.liner.sodadrunk.Core;
import ru.liner.sodadrunk.R;
import ru.liner.sodadrunk.genericadapter.Binder;
import ru.liner.sodadrunk.model.ApplicationModel;
import ru.liner.sodadrunk.model.BlockedApplication;

/**
 * @author : "Line'R"
 * @mailto : serinity320@mail.com
 * @created : 16.04.2022, суббота
 **/
public class ApplicationBinder extends Binder<ApplicationModel> {
    private ImageView applicationIcon;
    private TextView applicationName;
    private TextView applicationPackageName;
    private SwitchCompat applicationBlockSwitch;

    @Override
    public void init() {
        applicationIcon = find(R.id.applicationIcon);
        applicationName = find(R.id.applicationName);
        applicationPackageName = find(R.id.applicationPackageName);
        applicationBlockSwitch = find(R.id.applicationBlockSwitch);
    }

    @Override
    public void bind(@NonNull ApplicationModel model) {
        applicationBlockSwitch.setChecked(false);
        for (BlockedApplication blockedData : Core.getBlockedApps()) {
            if (
                    blockedData.name.equals(model.applicationName) ||
                            blockedData.packageName.equals(model.packageName)
            ) {
                applicationBlockSwitch.setChecked(true);
            }
        }
        applicationIcon.setImageDrawable(model.icon);
        applicationName.setText(model.applicationName);
        applicationPackageName.setText(model.packageName);
        applicationBlockSwitch.setOnClickListener(view -> {
            if(applicationBlockSwitch.isChecked()){
                Core.blockApplication(new BlockedApplication(model.applicationName, model.packageName));
            } else {
                Core.unblockApplication(new BlockedApplication(model.applicationName, model.packageName));
            }
        });
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
    public void onCentered(ApplicationModel model) {

    }

    @Override
    public void onNotCentered(ApplicationModel model) {

    }

}
