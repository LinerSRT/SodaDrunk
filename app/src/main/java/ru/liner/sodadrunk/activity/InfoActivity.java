package ru.liner.sodadrunk.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ru.liner.sodadrunk.R;

public class InfoActivity extends AppCompatActivity {
    private TextView emailCopy;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        emailCopy =findViewById(R.id.emailCopy);
        backButton = findViewById(R.id.info_backButton);
        backButton.setOnClickListener(view -> finish());
        emailCopy.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Email","fazafarlex@gmail.com");
            clipboard.setPrimaryClip(clip);
            Toast.makeText(InfoActivity.this, "E-mail copied to clipboard!", Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public void onBackPressed() {

    }


}