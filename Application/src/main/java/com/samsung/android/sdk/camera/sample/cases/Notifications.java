package com.samsung.android.sdk.camera.sample.cases;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.samsung.android.sdk.camera.sample.R;

/**
 * Created by Christa on 11/24/2015.
 */
public class Notifications extends Activity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_page);
        createGUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        createGUI();
    }


    private void createGUI()
    {
        findViewById(R.id.camera2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Notifications.this, Sample_Single.class));
            }
        });

        findViewById(R.id.profile_page2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Notifications.this, ProfilePage.class));
            }
        });
        findViewById(R.id.search2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Notifications.this, SearchPage.class));
            }
        });

        findViewById(R.id.home2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Notifications.this, HomePage.class));
            }
        });


    }
}
