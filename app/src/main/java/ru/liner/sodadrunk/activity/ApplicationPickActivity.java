package ru.liner.sodadrunk.activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.liner.sodadrunk.R;
import ru.liner.sodadrunk.binder.ApplicationBinder;
import ru.liner.sodadrunk.genericadapter.GenericAdapter;
import ru.liner.sodadrunk.model.ApplicationModel;
import ru.liner.sodadrunk.utils.System;

public class ApplicationPickActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_pick);
        ImageView backButton = findViewById(R.id.pick_app_backButton);
        RecyclerView appRecycler = findViewById(R.id.pick_app_appRecycler);
        GenericAdapter adapter = new GenericAdapter();
        appRecycler.setLayoutManager(new LinearLayoutManager(this));
        appRecycler.setAdapter(adapter);
        adapter.register(R.layout.application_holder, ApplicationModel.class, ApplicationBinder.class);
        adapter.add(System.getInstalledApplications(this));
        backButton.setOnClickListener(view -> finish());
    }
}