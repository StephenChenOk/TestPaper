package com.chen.fy.testpaper.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chen.fy.testpaper.R;
import com.chen.fy.testpaper.beans.User;
import com.chen.fy.testpaper.utils.LoginRegisterUtils;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etUsername;
    private EditText etPwd;
    private EditText etPwd2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        initView();

        //禁止输入空格
        LoginRegisterUtils.setEditTextInhibitInputSpace(etUsername);
        LoginRegisterUtils.setEditTextInhibitInputSpace(etPwd);
        LoginRegisterUtils.setEditTextInhibitInputSpace(etPwd2);
    }

    private void initView() {
        etUsername = findViewById(R.id.et_username);
        etPwd = findViewById(R.id.et_pwd);
        etPwd2 = findViewById(R.id.et_pwd_again);
        Button btnRegister = findViewById(R.id.btn_register);
        ImageView ivReturn = findViewById(R.id.iv_return);
        TextView tvReturn = findViewById(R.id.tv_return);

        btnRegister.setOnClickListener(this);
        ivReturn.setOnClickListener(this);
        tvReturn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_register:
                goRegester();
                break;
            case R.id.iv_return:
                finish();
                break;
            case R.id.tv_return:
                finish();
                break;
        }
    }

    private void goRegester() {
        String username = etUsername.getText().toString();
        //用户名已填入且没有被注册过时
        if(LoginRegisterUtils.userAvailable(username)){
            String password1 = etPwd.getText().toString();
            String password2 = etPwd2.getText().toString();
            //判断两次密码是否相等
            if(LoginRegisterUtils.passwordSame(password1,password2)){
                //获取系统随机生成的值(盐值),用于加密
                String pwSalt = UUID.randomUUID().toString().substring(0, 5);
                //用输入的密码+盐值进行MD5加密
                String pwHash = LoginRegisterUtils.getMD5(password1 + pwSalt);
                //存入数据库
                User visitor = new User();
                visitor.setUsername(username);
                visitor.setPwSalt(pwSalt);
                visitor.setPwHash(pwHash);
                visitor.save();
                //注册成功后跳到登入界面
                Toast.makeText(RegisterActivity.this,"注册成功,可以进行登入操作",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
