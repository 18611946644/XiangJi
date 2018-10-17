package com.bwie.rk06_touxiang_paizhao_caijian_ercicaiyang;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ImageView imgTouxiang;
    private File imgRoot;
    private Intent intent;
    private Uri uri;

    private static final int FLAG_CAMERA_REQUEST = 100;//裁剪   跳转回传码值
    private static final int FLAG_ALUMB_REQUEST = 101;//相册裁剪   跳转回传码值
    private static final int FLAG_CROP_REQUEST =102;//拍照后直接返回  裁剪

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取控件
        imgTouxiang = findViewById(R.id.Img_touxiang);

        //设置一个存储文件
        //如果SDka已经挂载
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //获取根目录
            File rootSD = Environment.getExternalStorageDirectory();
            //创建文件存储文件包(File.separator  代表反斜杠)
            imgRoot = new File(rootSD.getAbsolutePath()+File.separator+"imgs");
            //判断该文件是否存在
            if(!imgRoot.exists()){
                //如果不存在就创建
                imgRoot.mkdirs();
            }
        }

        //监听
       // imgTouxiang.setOnClickListener(this);

        imgTouxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("温馨提示：");
                builder.setMessage("请选择:");
                builder.setPositiveButton("相机", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //点击选择从相册选择
                        Log.d(TAG, "onClick: 您选择了从相册中选择头像上传!");

                        //1 参数  dirPath路径, name 名字---创建一个时间  用来当做图片名字(防止覆盖)
                        File f = new File(imgRoot,new Date().getTime()+".jpg");
                        //2 获取uri
                        uri = Uri.fromFile(f);
                        //3 使用系统相机的隐式跳转
                        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //4 设置拍照输出路径
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                        //5 start直接开启   还需要给他设置一个拍照后的保存路径
                        startActivityForResult(intent, FLAG_CAMERA_REQUEST);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("相册", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //点击从相机拍照
                        Log.d(TAG, "onClick: 您选择了从相机拍照上传头像!");
                        // 相册跳转
                        intent = new Intent(Intent.ACTION_PICK);
                        // 通配符类型
                        intent.setType("image/*");
                        // 开启跳转
                        startActivityForResult(intent,FLAG_ALUMB_REQUEST);
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

    }

   /* @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Img_touxiang://点击头像跳转
                //提示进行选择



                break;
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //1  如果返回的是定义的100FLAG_CAMERA_REQUEST
        if(requestCode==FLAG_CAMERA_REQUEST){//拍照裁剪上传
            //2 就得到照片后进行裁剪
            intent = crop(uri);
            //3 跳转  到裁剪
            startActivityForResult(intent, FLAG_CROP_REQUEST);
        }else if(requestCode == FLAG_ALUMB_REQUEST){//相册中裁剪上传
            uri = data.getData();//从相册中返回值
            //裁剪  方法
            intent = crop(uri);
            //3 跳转  到裁剪
            startActivityForResult(intent, FLAG_CROP_REQUEST);
        }else if(requestCode == FLAG_CROP_REQUEST){
            // 从返回值中直接获取bitmap
            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            imgTouxiang.setImageBitmap(bmp);
        }

    }


    //裁剪方法
    private Intent crop(Uri uri) {

        // 隐式Intent，调用系统的裁剪
        Intent intent = new Intent("com.android.camera.action.CROP");
        // 设置裁剪的数据源和数据类型
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");// 可裁剪
        // 裁剪的宽高比例
        intent.putExtra("aspectX", 1); // 裁剪的宽比例
        intent.putExtra("aspectY", 1); // 裁剪的高比例

        // 裁剪的宽度和高度
        intent.putExtra("outputX", 300); // 裁剪的宽度
        intent.putExtra("outputY", 300); // 裁剪的高度
        // 可省略
        intent.putExtra("scale", true); // 支持缩放
        // 裁剪之后保存的路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(uri.getPath() + ".bak")); // 将裁剪的结果输出到指定的Uri
        // 必须加，否则返回值中找不到返回的值
        intent.putExtra("return-data", true); // 若为true则表示返回数据
        // 可以省略
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());// 裁剪成的图片的格式
        // intent.putExtra("noFaceDetection", true); //启用人脸识别
        return intent;

    }
}
