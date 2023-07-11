package com.monstertechno.timely;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);

        View AboutUsActivity = findViewById(R.id.AboutUs);
        AboutUsActivity.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, AboutUsActivity.class);
            startActivity(intent);
        });
        AboutUsActivity = findViewById(R.id.SendMessage);


        AboutUsActivity.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, MessageActivity.class);
            startActivity(intent);
        });


    }
}