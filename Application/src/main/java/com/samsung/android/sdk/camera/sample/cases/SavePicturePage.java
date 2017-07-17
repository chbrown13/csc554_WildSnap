package com.samsung.android.sdk.camera.sample.cases;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;


import com.samsung.android.sdk.camera.SCameraCharacteristics;
import com.samsung.android.sdk.camera.SCaptureRequest;
import com.samsung.android.sdk.camera.sample.R;
import com.samsung.android.sdk.camera.sample.cases.util.SettingDialog;

/**
 * Created by Christa on 11/11/2015.
 */
@SuppressWarnings("deprecation")
public class SavePicturePage extends Activity {


    //the images to display
            Integer[] imageIDs = {
            R.drawable.squirell,
            R.drawable.squirell2,
            R.drawable.squirell3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savepicture);

        createUI();
    }

    @Override
   public void onResume() {
        super.onResume();
        createUI();
   }

    private void createUI() {

        findViewById(R.id.imageView4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SavePicturePage.this,PicturePage_NotIdentify.class);
                i.putExtra("picture", R.drawable.squirell);
                startActivity(i);
            }
        });

        findViewById(R.id.imageView5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SavePicturePage.this,PicturePage_NotIdentify.class);
                i.putExtra("picture", R.drawable.squirell2);
                startActivity(i);
            }
        });

        findViewById(R.id.imageView6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SavePicturePage.this,PicturePage_NotIdentify.class);
                i.putExtra("picture", R.drawable.squirell3);
                startActivity(i);
            }
        });

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavePicturePage.this, ProfilePage.class);
                startActivity(intent);
            }
        });
    }
}
