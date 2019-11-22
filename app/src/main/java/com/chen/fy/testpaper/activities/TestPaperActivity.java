package com.chen.fy.testpaper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.chen.fy.testpaper.R;
import com.jph.takephoto.app.TakePhotoActivity;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;

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
        ImageView ivMore = findViewById(R.id.iv_more);

        boxCamera.setOnClickListener(this);
        boxPhoto.setOnClickListener(this);
        ivMore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.box_photo_camera:
                Intent intent1 = new Intent(this, TestPaperResultActivity.class);
                intent1.putExtra("type", "相机识别");
                startActivity(intent1);
                break;
            case R.id.box_photo:
                Intent intent2 = new Intent(this, TestPaperResultActivity.class);
                intent2.putExtra("type", "图片识别");
                startActivity(intent2);
                break;
            case R.id.iv_more:
                showSelectDialog();
                break;
        }
    }

    /**
     * 显示选择对话框
     */
    private void showSelectDialog() {
        new XPopup.Builder(this)
//                        .maxWidth(600)
                .asCenterList("请选择", new String[]{"更改IP"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (position) {
                                    case 0:         //更改IP地址
                                        showInputDialog();
                                        break;
                                }
                            }
                        }).show();
    }

    /**
     * 显示带有输入的对话框
     * 修改IP
     */
    private void showInputDialog() {
        new XPopup.Builder(this)
                //.dismissOnBackPressed(false)
                .autoOpenSoftInput(true)
//                        .autoFocusEditText(false) //是否让弹窗内的EditText自动获取焦点，默认是true
                .isRequestFocus(false)
                //.moveUpToKeyboard(false)   //是否移动到软键盘上面，默认为true
                .asInputConfirm("更改IP", "请输入您要更改IP地址",
                        new OnInputConfirmListener() {
                            @Override
                            public void onConfirm(String text) {
                                TestPaperResultActivity.TEST_PAPER_SERVER_URL = text;
                            }
                        })
                .show();
    }
}
