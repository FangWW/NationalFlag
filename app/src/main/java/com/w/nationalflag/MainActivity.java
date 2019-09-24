package com.w.nationalflag;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends Activity {


    private ImageView headView;
    private int index = 0;
    private int[] headBG = new int[]{R.mipmap.ic_launcher, R.mipmap.head2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
    }

    private void initData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 999);
        }
        headView = findViewById(R.id.head);
        findViewById(R.id.changeHG).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
                headView.setImageResource(headBG[(index % headBG.length + 1) - 1]);
            }
        });
        headView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhoto();
            }
        });
        headView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                savePic(headView);
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == Activity.RESULT_OK
                && null != data) {
            try {

                Uri selectedImage = data.getData();
                ContentResolver cr = this.getContentResolver();
                Drawable bitmap = Drawable.createFromStream(cr.openInputStream(selectedImage), null);
                headView.setBackground(bitmap);

                startPhotoZoom(selectedImage);
            } catch (Exception e) {
                //"上传失败");
            }
        }
        if (requestCode == 3 && resultCode == Activity.RESULT_OK
                && null != data) {
            try {
                Uri selectedImage = data.getData();
                ContentResolver cr = this.getContentResolver();
                Drawable bitmap = Drawable.createFromStream(cr.openInputStream(selectedImage), null);
                headView.setBackground(bitmap);
            } catch (Exception e) {
                //"上传失败");
            }
        }
    }

    private void choosePhoto() {
        //这是打开系统默认的相册(就是你系统怎么分类,就怎么显示,展示分类列表)
//        Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
////        startActivityForResult(picture, 2);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");//选择图片
        //intent.setType(“audio/*”); //选择音频
        //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
        //intent.setType(“video/*;image/*”);//同时选择视频和图片
//        intent.setType("*/*");//无类型限制
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 2);
    }


    public void startPhotoZoom(Uri uri) {
        try {
            String filePath = Environment.getExternalStorageDirectory().getCanonicalPath() + "/NationalFlag";
            Uri imageUri = Uri.parse(filePath);
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
            intent.putExtra("crop", "true");
            //该参数可以不设定用来规定裁剪区的宽高比
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            //该参数设定为你的imageView的大小
            intent.putExtra("outputX", 600);
            intent.putExtra("outputY", 600);
            intent.putExtra("scale", true);
            //是否返回bitmap对象
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//输出图片的格式
//        intent.putExtra("noFaceDetection", true); // 头像识别
            startActivityForResult(intent, 3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //----------处理头像---------
    private void savePic(View headerView) {
        try {
            String filePath = Environment.getExternalStorageDirectory().getCanonicalPath() + "/NationalFlag";
            File filePic = new File(filePath + "/head.png");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            getShareingBitmap2(createBitmap2(headerView)).
                    compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(MainActivity.this, "保存路径" + filePic.getPath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "打开文件存储权限！！！", Toast.LENGTH_LONG).show();
        }
    }

    private Bitmap createBitmap2(View v) {
        Bitmap bmp = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        v.draw(c);
        return bmp;
    }

    public static Bitmap getShareingBitmap2(Bitmap imageBitmap) {
        Bitmap.Config config = imageBitmap.getConfig();
        int sourceBitmapHeight = imageBitmap.getHeight();
        int sourceBitmapWidth = imageBitmap.getWidth();
        Paint paint = new Paint();
        Bitmap share_bitmap = Bitmap.createBitmap(sourceBitmapWidth, sourceBitmapHeight, config);
        Canvas canvas = new Canvas(share_bitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(imageBitmap, 0, 0, paint); // 绘制图片
        return share_bitmap;
    }
}
