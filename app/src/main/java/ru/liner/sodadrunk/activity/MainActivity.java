package ru.liner.sodadrunk.activity;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

import ru.liner.sodadrunk.Core;
import ru.liner.sodadrunk.R;
import ru.liner.sodadrunk.math.Generator;
import ru.liner.sodadrunk.service.ControlService;
import ru.liner.sodadrunk.service.DevicePolicyReceiver;
import ru.liner.sodadrunk.utils.Broadcast;
import ru.liner.sodadrunk.utils.DeviceOwnerUtil;
import ru.liner.sodadrunk.utils.PM;
import ru.liner.sodadrunk.utils.System;

public class MainActivity extends AppCompatActivity {

    private ImageView infoScreenButton;
    private FloatingActionButton controlButton;
    private FloatingActionButton addFiltersButton;
    private TextView controlStatus;
    private Generator generator;
    private boolean localEnabled;
    private Broadcast serviceReceiver;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 250 && resultCode == RESULT_OK) {
            PM.put("admin_enabled", true);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serviceReceiver = new Broadcast(new String[]{
                Core.ACTION_SERVICE_STARTED,
                Core.ACTION_SERVICE_STOPPED
        }) {
            @Override
            public void handleChanged(Intent intent) {
                localEnabled = intent.getAction().equals(Core.ACTION_SERVICE_STARTED);
                refreshUI();
            }
        };
        serviceReceiver.setListening(true);

        localEnabled = false;
        findViews();
        requestDeviceAdmin();
        refreshUI();
        Permissions.check(this, System.getPermissions(), null, null, new PermissionHandler() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                finish();
            }
        });

        generator = new Generator();
        controlButton.setOnClickListener(view -> {
            if (!ControlService.isStart()) {
                startActivityForResult(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), 300);
            } else if ((Boolean) PM.get("control_enabled", false)) {
                CharSequence[] suggestedAnswer = generator.getSuggestionsChar();
                new AlertDialog.Builder(this)
                        .setTitle("Solve to continue: " + generator.getOne() + "x" + generator.getOther() + "=?")
                        .setSingleChoiceItems(suggestedAnswer, 0, (dialogInterface, i) -> {
                            if (generator.validateResult(Integer.parseInt(String.valueOf(suggestedAnswer[i])))) {
                                PM.put("control_enabled", false);
                                localEnabled = false;
                                refreshUI();
                            } else {
                                Toast.makeText(MainActivity.this, "Wrong answer! Try again.", Toast.LENGTH_SHORT).show();
                                generator.generate();
                            }
                            dialogInterface.dismiss();
                        }).create().show();
            } else {
                PM.put("control_enabled", true);
                localEnabled = true;
                refreshUI();
            }
        });
        infoScreenButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, InfoActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)));
        addFiltersButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, SetupActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        generator.generate();
        serviceReceiver.setListening(true);
        refreshUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        refreshUI();
        serviceReceiver.setListening(false);
    }

    private void findViews() {
        infoScreenButton = findViewById(R.id.main_infoScreenButton);
        controlButton = findViewById(R.id.main_controlProtectionButton);
        addFiltersButton = findViewById(R.id.main_addFiltersButton);
        controlStatus = findViewById(R.id.main_controlProtectionStatus);
    }

    private void refreshUI() {
        controlStatus.setText(localEnabled ? getText(R.string.disable_protection) : getString(R.string.enable_protection));
        controlButton.setImageResource(localEnabled ? R.drawable.ic_baseline_stop_24 : R.drawable.ic_baseline_play_arrow_24);
    }


    private void requestDeviceAdmin() {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (!DeviceOwnerUtil.isDeviceOwnerApp(devicePolicyManager, getPackageName())) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, new ComponentName(this, DevicePolicyReceiver.class));
            intent.putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    getString(R.string.control_service_description)
            );
            startActivityForResult(intent, 250);
        }
    }
}