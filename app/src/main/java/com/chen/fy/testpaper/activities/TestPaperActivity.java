package com.chen.fy.testpaper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.chen.fy.testpaper.R;
import com.jph.takephoto.app.TakePhotoActivity;

public class TestPaperActivity extends TakePhotoActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_paper_layout);

        initView();
    }

    private void initView() {
        LinearLayout boxCamera = findViewById(R.id.box_photo_camera);
        LinearLayout boxPhoto = findViewById(R.id.box_photo);

        boxCamera.setOnClickListener(this);
        boxPhoto.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.box_photo_camera:
                Intent intent1 = new Intent(this,TestPaperResultActivity.class);
                intent1.putExtra("type","相机识别");
                startActivity(intent1);
                break;
            case R.id.box_photo:
                Intent intent2 = new Intent(this,TestPaperResultActivity.class);
                intent2.putExtra("type","图片识别");
                startActivity(intent2);
                break;
        }
    }
}
