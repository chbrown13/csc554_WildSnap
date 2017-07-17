package com.samsung.android.sdk.camera.sample.cases;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import com.samsung.android.sdk.camera.sample.R;


public class ProfilePage extends Activity {

    public String currentPicId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        createUI();
    }


    @Override
    public void onResume() {
        super.onResume();
        createUI();
    }


    private void createUI() {

        findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfilePage.this,PicturePage.class);
                i.putExtra("picture", R.drawable.squirell);
                startActivity(i);
            }
        });

        findViewById(R.id.imageView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent i = new Intent(ProfilePage.this,PicturePage.class);
                    i.putExtra("picture", R.drawable.squirell2);
                    startActivity(i);
            }
        });

        findViewById(R.id.imageView3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfilePage.this,PicturePage.class);
                i.putExtra("picture", R.drawable.squirell3);
                startActivity(i);
            }
        });

        findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfilePage.this, Sample_Single.class));
            }
        });

        findViewById(R.id.notifications).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfilePage.this, Notifications.class));
            }
        });

        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfilePage.this, SearchPage.class));
            }
        });

        findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfilePage.this, HomePage.class));
            }
        });

    }

}
