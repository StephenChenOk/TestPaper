package com.chen.fy.testpaper.activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.chen.fy.testpaper.R;
import com.chen.fy.testpaper.adapters.TestPaperAdapter;
import com.chen.fy.testpaper.beans.Images;
import com.chen.fy.testpaper.beans.TestPaperInfo;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.PermissionManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TestPaperResultActivity extends TakePhotoActivity {

    private static final String TEST_PAPER_SERVER_URL = "http://192.168.43.4:8787/all_infer";
    /**
     * 拍照控件
     */
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;

    /**
     * 图片剪切以及图片地址
     */
    private CropOptions cropOptions;
    private Uri uri;

    /**
     * 图片地址
     */
    private String imagePath;
    /**
     * 图片文件大小
     */
    private long fileLength;
    /**
     * 图片文件名
     */
    private final String imageName = "test_paper.jpg";

    private List<TestPaperInfo> list = new ArrayList<>();

    private ImageView imageView;

    private TestPaperAdapter adapter;

    private RecyclerView recyclerView;

    private AlertDialog mCircleDialog;

    private TextView tvSelectPaper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_paper_result_layout);

        initView();

        initTakePhoto();

        if (getIntent() != null) {
            if (getIntent().getStringExtra("type").equals("相机识别")) {
                //相机拍照照片并剪裁
                takePhoto.onPickFromCaptureWithCrop(uri, cropOptions);
            }
            if (getIntent().getStringExtra("type").equals("图片识别")) {
                //相册获取照片并剪裁
                takePhoto.onPickFromGalleryWithCrop(uri, cropOptions);
            }
        }

        //initData();

    }

    private void initView() {
        imageView = findViewById(R.id.iv_test_paper);
        recyclerView = findViewById(R.id.rv_test_paper);
        tvSelectPaper = findViewById(R.id.select_test_paper);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar_test_paper);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initData() {
        if (list == null) {
            list = new ArrayList<>();
        }

        Bitmap bitmap = null;
        for (Images images : resultImages) {
            bitmap = images.getBitmap();
            imageView.setImageBitmap(images.getBitmap());
        }

        if (worryImages == null || worryImages.isEmpty()) {

            TestPaperInfo info1 = new TestPaperInfo();
            info1.setResult("合格");
            info1.setImage(bitmap);
            list.add(info1);
        } else {
            for (Images images : worryImages) {
                TestPaperInfo info1 = new TestPaperInfo();
                info1.setResult("不合格");
                info1.setImage(images.getBitmap());
                list.add(info1);
            }
        }

        adapter = new TestPaperAdapter(list);

        //2 RecyclerView设置
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);//1 表示列数
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        tvSelectPaper.setText("扣出试纸部分");

        //进度条消失
        mCircleDialog.dismiss();
        mCircleDialog = null;

    }

    /**
     * 初始化TakePhoto开源库,实现拍照以及从相册中选择图片
     */
    private void initTakePhoto() {
        //获得对象
        takePhoto = getTakePhoto();

        //获取外部存储位置的uri
        File file = new File(getExternalFilesDir(null), imageName);
        uri = Uri.fromFile(file);
        imagePath = uri.getPath();
        fileLength = file.length();

        //进行图片剪切
        int size = Math.min(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
        cropOptions = new CropOptions.Builder().setOutputX(size).setOutputX(size).setWithOwnCrop(false).create();  //true表示使用TakePhoto自带的裁剪工具

        //进行图片压缩
        CompressConfig compressConfig = new CompressConfig.Builder().
                //大小            像素
                        setMaxSize(512).setMaxPixel(200).create();
        /*
         * 启用图片压缩
         * @param config 压缩图片配置
         * @param showCompressDialog 压缩时是否显示进度对话框
         * @return
         */
        takePhoto.onEnableCompress(compressConfig, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //以下代码为处理Android6.0、7.0动态权限所需(TakePhoto所需)
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    /**
     * 获取照片成功后成功回调
     */
    @Override
    public void takeSuccess(TResult result) {
        //显示进度条
        getProcessCircle();
        mCircleDialog.show();
        //上传图片到服务器
        uploadImage();
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
        finish();
    }

    /*
     *  初始化圆形进度条
     */
    private void getProcessCircle() {
        if (mCircleDialog == null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            //反射一个自定义的全新的对话框布局
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.process_circle_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(view);
            mCircleDialog = builder.create();
            //设置不可以点击弹窗之后进行取消
            mCircleDialog.setCanceledOnTouchOutside(false);
            //设置旋转
            Animation animation = new RotateAnimation(0.0f, 720.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setFillAfter(true);
            animation.setInterpolator(new LinearInterpolator());
            animation.setDuration(1200);
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.RESTART);
            ImageView iv_process_circle = view.findViewById(R.id.iv_process_circle);
            iv_process_circle.setAnimation(animation);
        }
    }

    /***
     * 上传图片到服务器进行处理
     */
    private void uploadImage() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.execute(imagePath);
    }

    private String doPost(String imagePath) {
        OkHttpClient mOkHttpClient = new OkHttpClient();

        //setType(MultipartBody.FORM)
        String result = "error";
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)                    //以文件形式上传
                .addFormDataPart("img", imagePath,                         //图片
                        RequestBody.create(MediaType.parse("image/jpg"), new File(imagePath)))
                .build();
        Request.Builder reqBuilder = new Request.Builder();
        Request request = reqBuilder
                .url(TEST_PAPER_SERVER_URL)
                .post(requestBody)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            long startTime = System.currentTimeMillis();
            while (!response.isSuccessful()) {   //如果还没有返回数据
                long tempTime = System.currentTimeMillis();
                //超时
                if ((tempTime - startTime) >= 16000) {
                    //跳到UI线程进行UI操作
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //进度条消失
                            mCircleDialog.dismiss();
                            mCircleDialog = null;
                        }
                    });
                    return "timeout";
                }
            }

            assert response.body() != null;
            //将服务器返回的流转换成压缩包
            File imagesFile = writeToLocal(response.body().byteStream());
            //获取压缩包中的图片
            readImages(imagesFile.getAbsolutePath());

            //跳到UI线程进行UI操作
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initData();
                }
            });
            return "success";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public File writeToLocal(InputStream input)
            throws IOException {
        File imagsFile = new File(getExternalFilesDir(null), "images.zip");
        int index;
        byte[] bytes = new byte[1024];
        FileOutputStream downloadFile = new FileOutputStream(imagsFile);
        while ((index = input.read(bytes)) != -1) {
            downloadFile.write(bytes, 0, index);
            downloadFile.flush();
        }
        input.close();
        downloadFile.close();
        return imagsFile;
    }

    List<Images> cropImages = null;
    List<Images> divideImages = null;
    List<Images> resultImages = null;
    List<Images> worryImages = null;

    public void readImages(String file) throws Exception {
        ZipFile zf = new ZipFile(file);
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        ZipInputStream zin = new ZipInputStream(in);
        ZipEntry ze;
        int i = 0;
        while ((ze = zin.getNextEntry()) != null) {  //会自动进入目录
            if (!ze.isDirectory()) {    //不是目录
                String fileName = ze.getName().substring(0, ze.getName().length() - 6);
                switch (fileName) {  //判断是哪个文件夹下的图片
                    case "crop/crop_img":
                        cropImages = addImages(zf, ze, cropImages);      //要检测的图片
                        break;
                    case "divide/divide_img":
                        divideImages = addImages(zf, ze, divideImages);  //把识别的地方分割出来
                        break;
                    case "result/result_img":
                        resultImages = addImages(zf, ze, resultImages);  //把要识别试纸圈出来
                        Log.d("chensheng", "resultImages");
                        break;
                    case "worry/worry_img":                              //不合格的地方
                        worryImages = addImages(zf, ze, worryImages);
                        break;
                }
            }
        }
        zin.closeEntry();
    }

    private List<Images> addImages(ZipFile zf, ZipEntry ze, List<Images> images) throws IOException {
        if (images == null) {
            images = new ArrayList<>();
        }
        InputStream is = zf.getInputStream(ze);
        Bitmap bitmap = BitmapFactory.decodeStream(is);

        Images images1 = new Images();
        images1.setBitmap(bitmap);
        images.add(images1);

        return images;
    }

    /**
     * 访问网络AsyncTask,访问网络在子线程进行并返回主线程通知访问的结果
     */
    class NetworkTask extends AsyncTask<String, Integer, String> {

        /**
         * 后台任务开始之前调用，通常用来初始化界面操作
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * 执行后台耗时操作，已在子线程中执行
         *
         * @return 对结果进行返回
         */
        @Override
        protected String doInBackground(String... params) {
            return doPost(params[0]);
        }

        /**
         * 当后台任务执行完毕时调用
         *
         * @param result 后台执行任务的返回值
         */
        @Override
        protected void onPostExecute(String result) {
        }
    }
}
