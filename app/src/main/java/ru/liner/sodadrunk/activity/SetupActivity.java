package ru.liner.sodadrunk.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ru.liner.sodadrunk.Core;
import ru.liner.sodadrunk.R;
import ru.liner.sodadrunk.binder.BlockedApplicationBinder;
import ru.liner.sodadrunk.binder.BlockedContactBinder;
import ru.liner.sodadrunk.genericadapter.GenericAdapter;
import ru.liner.sodadrunk.model.BlockedApplication;
import ru.liner.sodadrunk.model.BlockedContact;
import ru.liner.sodadrunk.utils.Broadcast;

/**
 * @author : "Line'R"
 * @mailto : serinity320@mail.com
 * @created : 15.04.2022, пятница
 **/
public class SetupActivity extends AppCompatActivity {
    private GenericAdapter contactAdapter;
    private GenericAdapter appsAdapter;
    private TextView noBlockingAppsText;
    private TextView noBlockingContactsText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        RecyclerView contactRecyclerView = findViewById(R.id.contactRecycler);
        noBlockingAppsText = findViewById(R.id.setup_noBlockingAppsText);
        noBlockingContactsText = findViewById(R.id.setup_noBlockingContactsText);
        FloatingActionButton addApplication = findViewById(R.id.setup_addApp);
        ImageView backButton = findViewById(R.id.setup_backButton);
        FloatingActionButton addContact = findViewById(R.id.addContact);
        RecyclerView appsRecyclerView = findViewById(R.id.appsRecycler);
        contactRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        appsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactAdapter = new GenericAdapter();
        contactAdapter.register(R.layout.blocked_holder, BlockedContact.class, BlockedContactBinder.class);
        contactRecyclerView.setAdapter(contactAdapter);
        appsAdapter = new GenericAdapter();
        appsAdapter.register(R.layout.blocked_holder, BlockedApplication.class, BlockedApplicationBinder.class);
        appsRecyclerView.setAdapter(appsAdapter);
        refreshData();
        addApplication.setOnClickListener(view -> startActivity(new Intent(SetupActivity.this, ApplicationPickActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)));
        addContact.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("vnd.android.cursor.dir/phone_v2");
            startActivityForResult(intent, 200);
        });
        backButton.setOnClickListener(view -> finish());
        Broadcast blockedListChangedListener = new Broadcast(new String[]{
                Core.ACTION_BLOCKED_APP_LIST_CHANGED,
                Core.ACTION_BLOCKED_CONTACT_LIST_CHANGED
        }) {
            @Override
            public void handleChanged(Intent intent) {
                refreshData();
            }
        };
        blockedListChangedListener.setListening(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        contactAdapter.set(Core.getBlockedContacts());
        appsAdapter.set(Core.getBlockedApps());
        noBlockingAppsText.setVisibility(appsAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        noBlockingContactsText.setVisibility(contactAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
            cursor.moveToFirst();
            BlockedContact blockedContact = new BlockedContact(
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            );
            Intent intent = new Intent(this, BlockingContactConfigureActivity.class);
            intent.putExtra("blockedContact", blockedContact);
            intent.putExtra("edit_mode", false);
            startActivity(intent);
            cursor.close();
        }
    }

}
