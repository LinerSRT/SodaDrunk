package ru.liner.sodadrunk.activity;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import ru.liner.sodadrunk.utils.Timer;

public class MainActivity extends AppCompatActivity {
    private ImageView infoScreenButton;
    private FloatingActionButton controlButton;
    private FloatingActionButton addFiltersButton;
    private FloatingActionButton setTimer;
    private TextView controlStatus;
    private TextView timerProgressText;
    private Generator generator;
    private boolean localEnabled;
    private Broadcast serviceReceiver;
    private ProgressBar timerProgress;
    private Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        timer = new Timer(MainActivity.this);
        timerProgress.setIndeterminate(false);
        timerProgress.setProgress(0);
        serviceReceiver = new Broadcast(new String[]{
                Core.ACTION_SERVICE_STARTED,
                Core.ACTION_SERVICE_STOPPED
        }) {
            @Override
            public void handleChanged(Intent intent) {
                localEnabled = intent.getAction().equals(Core.ACTION_SERVICE_STARTED);
                PM.put("control_enabled", localEnabled);
                refreshUI();
            }
        };
        serviceReceiver.setListening(true);
        localEnabled = false;
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
        setTimer.setOnClickListener(view -> {
            if (!(Boolean) PM.get("timer_active", false)) {
                timer.start(new Timer.Callback() {
                    @Override
                    public void onStart() {
                        controlButton.performClick();
                        controlButton.setEnabled(false);
                        setTimer.post(new Runnable() {
                            @Override
                            public void run() {
                                setTimer.animate()
                                        .scaleX(0)
                                        .scaleY(0)
                                        .setDuration(300)
                                        .setInterpolator(new AccelerateInterpolator())
                                        .start();
                                controlButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.accentColorDisabled)));
                                controlButton.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.textColorDisabled)));
                                controlStatus.setText("Control disabled while timer is running");
                            }
                        });
                    }

                    @Override
                    public void onTimeChanged(long timeLeft) {
                        PM.put("timer_active", true);
                        PM.put("timer_startTime", timer.getStartTime());
                        PM.put("timer_endTime", timer.getEndTime());
                        timerProgress.post(new Runnable() {
                            @Override
                            public void run() {
                                timerProgress.setProgress(Math.round(timer.getPercent()));
                                timerProgressText.setText("Time left - " + Timer.convertSecondsToHMmSs(timeLeft));
                                if (timerProgress.getVisibility() != View.VISIBLE)
                                    timerProgress.setVisibility(View.VISIBLE);
                                if (timerProgressText.getVisibility() != View.VISIBLE)
                                    timerProgressText.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onTimeLeft() {
                        PM.put("timer_active", false);
                        localEnabled = PM.get("control_enabled", false);
                        setTimer.post(new Runnable() {
                            @Override
                            public void run() {
                                setTimer.animate()
                                        .scaleX(1)
                                        .scaleY(1)
                                        .setDuration(300)
                                        .setInterpolator(new AccelerateInterpolator())
                                        .start();
                                controlButton.setEnabled(true);
                                controlButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.white)));
                                controlButton.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.accentColor)));
                            }
                        });
                        controlStatus.setText(localEnabled ? getText(R.string.disable_protection) : getString(R.string.enable_protection));

                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        generator.generate();
        serviceReceiver.setListening(true);
        if (!localEnabled)
            localEnabled = PM.get("control_enabled", false);
        refreshUI();
        if ((Boolean) PM.get("timer_active", false)) {
            timer.restore(PM.get("timer_startTime", timer.getStartTime()), PM.get("timer_endTime", timer.getEndTime()), new Timer.Callback() {
                @Override
                public void onStart() {
                    controlButton.setEnabled(false);
                    setTimer.post(new Runnable() {
                        @Override
                        public void run() {
                            setTimer.animate()
                                    .scaleX(0)
                                    .scaleY(0)
                                    .setDuration(300)
                                    .setInterpolator(new AccelerateInterpolator())
                                    .start();
                            controlButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.accentColorDisabled)));
                            controlButton.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.textColorDisabled)));
                            controlStatus.setText("Control disabled while timer is running");
                        }
                    });
                }

                @Override
                public void onTimeChanged(long timeLeft) {
                    PM.put("timer_active", true);
                    PM.put("timer_leftTime", timeLeft);
                    timerProgress.post(new Runnable() {
                        @Override
                        public void run() {
                            timerProgress.setProgress(Math.round(timer.getPercent()));
                            timerProgressText.setText("Time left - " + Timer.convertSecondsToHMmSs(timeLeft));
                            if (timerProgress.getVisibility() != View.VISIBLE)
                                timerProgress.setVisibility(View.VISIBLE);
                            if (timerProgressText.getVisibility() != View.VISIBLE)
                                timerProgressText.setVisibility(View.VISIBLE);
                        }
                    });
                }

                @Override
                public void onTimeLeft() {
                    PM.put("timer_active", false);
                    localEnabled = PM.get("control_enabled", false);
                    setTimer.post(new Runnable() {
                        @Override
                        public void run() {
                            setTimer.animate()
                                    .scaleX(1)
                                    .scaleY(1)
                                    .setDuration(300)
                                    .setInterpolator(new AccelerateInterpolator())
                                    .start();
                            controlButton.setEnabled(true);
                            controlButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.white)));
                            controlButton.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.accentColor)));
                         }
                    });
                    controlStatus.setText(localEnabled ? getText(R.string.disable_protection) : getString(R.string.enable_protection));

                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        refreshUI();
        serviceReceiver.setListening(false);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 250 && resultCode == RESULT_OK) {
            PM.put("admin_enabled", true);
        }
    }


    private void findViews() {
        infoScreenButton = findViewById(R.id.main_infoScreenButton);
        controlButton = findViewById(R.id.main_controlProtectionButton);
        addFiltersButton = findViewById(R.id.main_addFiltersButton);
        controlStatus = findViewById(R.id.main_controlProtectionStatus);
        setTimer = findViewById(R.id.main_setTimer);
        timerProgress = findViewById(R.id.main_timerProgress);
        timerProgressText = findViewById(R.id.main_timerProgressText);
    }

    private void refreshUI() {
        timerProgress.setVisibility(PM.get("timer_active", false) ? View.VISIBLE : View.GONE);
        timerProgressText.setVisibility(PM.get("timer_active", false) ? View.VISIBLE : View.GONE);
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