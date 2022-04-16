package ru.liner.sodadrunk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ru.liner.sodadrunk.Core;
import ru.liner.sodadrunk.R;
import ru.liner.sodadrunk.model.BlockedContact;
import ru.liner.sodadrunk.utils.Comparator;
import ru.liner.sodadrunk.utils.Lists;

public class BlockingContactConfigureActivity extends AppCompatActivity {
    private SwitchCompat callsSwitch;
    private SwitchCompat smsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_block_setup);
        ImageView backButton = findViewById(R.id.setup_contact_backButton);
        TextView contactInfo = findViewById(R.id.setup_contact_contactInfo);
        callsSwitch = findViewById(R.id.setup_contact_callsSwitch);
        smsSwitch = findViewById(R.id.setup_contact_smsSwitch);
        FloatingActionButton doneButton = findViewById(R.id.setup_contact_saveButton);
        FloatingActionButton deleteButton = findViewById(R.id.setup_contact_deleteButton);
        Intent data = getIntent();
        if (data != null) {
            BlockedContact blockedContact = (BlockedContact) data.getSerializableExtra("blockedContact");
            if(Lists.contains(Core.getBlockedContacts(), new Comparator<BlockedContact>(blockedContact) {
                @Override
                public boolean compare(BlockedContact one, BlockedContact other) {
                    return one.number.equals(other.number) || one.name.equals(other.name);
                }
            }) && !data.getBooleanExtra("edit_mode", false)){
                finish();
                return;
            }
            contactInfo.setText(String.format("%s, %s", blockedContact.name, blockedContact.number));
            callsSwitch.setOnClickListener(view -> blockedContact.preventCalls = callsSwitch.isChecked());
            callsSwitch.setChecked(blockedContact.preventCalls);
            smsSwitch.setOnClickListener(view -> blockedContact.preventMessaging = smsSwitch.isChecked());
            smsSwitch.setChecked(blockedContact.preventMessaging);
            backButton.setOnClickListener(view -> finish());
            doneButton.setOnClickListener(view -> {
                System.out.println(blockedContact.toString());
                if(data.getBooleanExtra("edit_mode", false)){
                    Core.setContact(blockedContact);
                } else {
                    Core.blockContact(blockedContact);
                }
                finish();
            });
            deleteButton.setOnClickListener(view -> {
                Core.unblockContact(blockedContact);
                finish();
            });
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}