package com.example.d_alz.ex1;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.opencv.core.Mat;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }

    private Mat img1;
    private Mat img2;

    private static final String TAG = "opencv";
    static final int PERMISSION_REQUEST_CODE = 1;
    String[] PERMISSIONS  = {"android.permission.WRITE_EXTERNAL_STORAGE"};

    private boolean hasPermissions(String[] permissions) {
        int ret = 0;
        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions){
            ret = checkCallingOrSelfPermission(perms);
            if (!(ret == PackageManager.PERMISSION_GRANTED)){
                //퍼미션 허가 안된 경우
                return false;
            }
        }
        //모든 퍼미션이 허가된 경우
        return true;
    }

    private void requestNecessaryPermissions(String[] permissions) {
        //마시멜로( API 23 )이상에서 런타임 퍼미션(Runtime Permission) 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = (TextView) findViewById(R.id.sample_text);

        int ret;
        if (!hasPermissions(PERMISSIONS)) { //퍼미션 허가를 했었는지 여부를 확인
            requestNecessaryPermissions(PERMISSIONS);//퍼미션 허가안되어 있다면 사용자에게 요청
        }
        read_image_file();
        ret = compare(img1.getNativeObjAddr(), img2.getNativeObjAddr());
        if (ret == 1) {
            tv.setText("일치");
        } else
            tv.setText("불");
    }
     
    private void read_image_file() { //전체반복
        img1 = new Mat();
        img2 = new Mat();
        TextView tv1 = (TextView) findViewById(R.id.sample_text1);
        TextView tv2 = (TextView) findViewById(R.id.sample_text2);

        String path="/storage/emulated/0/DCIM/Camera/20150502_023557.jpg";
        String filename=new File(path).getName();
        tv1.setText(filename);

        loadImage1(filename, img1.getNativeObjAddr()); // 선택한사진


        loadImage2("20150505_061051.jpg", img2.getNativeObjAddr()); // 반복 비교할사진
    }

    public native void loadImage1(String imageFileName, long img);
    public native void loadImage2(String imageFileName, long img);
    public native int compare(long Image1, long Image2);
}
