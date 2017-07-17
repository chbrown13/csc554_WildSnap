package com.samsung.android.sdk.camera.sample.cases;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.samsung.android.sdk.camera.sample.R;

/**
 * Created by Christa on 11/24/2015.
 */
public class HomePage extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        createGUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        createGUI();
    }


    private void createGUI() {
        findViewById(R.id.camera4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this, Sample_Single.class));
            }
        });

        findViewById(R.id.profile_page4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this, ProfilePage.class));
            }
        });
        findViewById(R.id.notifications4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this, Notifications.class));
            }
        });
        findViewById(R.id.search4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this, SearchPage.class));
            }
        });

    }
}
