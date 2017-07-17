package com.samsung.android.sdk.camera.sample.cases;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.samsung.android.sdk.camera.sample.R;

public class PicturePage extends Activity {

    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_identify_page);

        ImageView iv = (ImageView)findViewById(R.id.view_image);

        iv.setImageResource(getIntent().getIntExtra("picture",R.drawable.easy_camera_gallery_no_item));

    }

    @Override
    public void onResume() {
        super.onResume();
        createUI();

    }

    private void createUI() {

        findViewById(R.id.identify_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t = (TextView) findViewById(R.id.identified);
                t.setText(R.string.identify_pic_object);
            }
        });
    }

}
