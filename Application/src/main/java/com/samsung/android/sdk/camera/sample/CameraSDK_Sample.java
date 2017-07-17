package com.samsung.android.sdk.camera.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.samsung.android.sdk.camera.sample.cases.Sample_Single;

public class CameraSDK_Sample extends Activity {

    private final String SAMPLE_SINGLE = "Single";

    private final String SAMPLE_NAMES_LIST[] = {
        SAMPLE_SINGLE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_single);

        createUI();
    }

    private void createUI() {

        Intent intent = new Intent(CameraSDK_Sample.this, Sample_Single.class);
        startActivity(intent);

    }
}
