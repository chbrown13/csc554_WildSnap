package com.samsung.android.sdk.camera.sample.cases;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.samsung.android.sdk.camera.sample.R;

/**
 * Created by Christa on 11/24/2015.
 */
public class SearchPage extends Activity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        createGUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        createGUI();
    }


    private void createGUI()
    {
        findViewById(R.id.camera3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchPage.this, Sample_Single.class));
            }
        });

        findViewById(R.id.profile_page3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchPage.this, ProfilePage.class));
            }
        });
        findViewById(R.id.notifications3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchPage.this, Notifications.class));
            }
        });
        findViewById(R.id.home3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchPage.this, HomePage.class));
            }
        });



    }





}
