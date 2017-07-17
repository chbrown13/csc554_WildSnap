package com.samsung.android.sdk.camera.sample.cases;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.samsung.android.sdk.camera.sample.R;

public class PicturePage_NotIdentify extends Activity {

    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_select_page);

        ImageView iv = (ImageView)findViewById(R.id.view_image2);

        iv.setImageResource(getIntent().getIntExtra("picture",R.drawable.squirell));

    }


    private void createUI() {

        //might need listener to go back to save picture page
    }

}

