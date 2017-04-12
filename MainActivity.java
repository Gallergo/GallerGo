package com.example.yu.a;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String filename1 = (String) "C:/images/iphone1.jpeg";
        String filename2 = (String)"C:/images/iphone2.jpeg";
        TextView tv = (TextView) findViewById(R.id.text1);

        int ret;
        ret = compareHistogram(filename1, filename2);
        if (ret > 0) {
            tv.setText("두 이미지는 일치합니다.");
        }
        else {
            tv.setText("두 이미지는 다릅니다.");
        }
    }

    public static int compareHistogram(String filename1, String filename2) {
        int retVal = 0;
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat img1 = Imgcodecs.imread(filename1, Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Mat img2 = Imgcodecs.imread(filename2, Imgcodecs.CV_LOAD_IMAGE_COLOR);

        Mat hsvImg1 = new Mat();
        Mat hsvImg2 = new Mat();

        Imgproc.cvtColor(img1, hsvImg1, Imgproc.COLOR_BGR2HSV);
        Imgproc.cvtColor(img2, hsvImg2, Imgproc.COLOR_BGR2HSV);

        List<Mat> listImg1 = new ArrayList<Mat>();
        List<Mat> listImg2 = new ArrayList<Mat>();

        listImg1.add(hsvImg1);
        listImg2.add(hsvImg2);

        MatOfFloat ranges = new MatOfFloat(0, 255);
        MatOfInt histSize = new MatOfInt(50);
        MatOfInt channels = new MatOfInt(0);

        Mat histImg1 = new Mat();
        Mat histImg2 = new Mat();

        Imgproc.calcHist(listImg1, channels, new Mat(), histImg1, histSize, ranges);
        Imgproc.calcHist(listImg2, channels, new Mat(), histImg2, histSize, ranges);

        Core.normalize(histImg1, histImg1, 0, 1, Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(histImg2, histImg2, 0, 1, Core.NORM_MINMAX, -1, new Mat());


        double result0 = Imgproc.compareHist(histImg1, histImg2, Imgproc.CV_COMP_CORREL);
        double result1 = Imgproc.compareHist(histImg1, histImg2, Imgproc.CV_COMP_CHISQR);
        double result2 = Imgproc.compareHist(histImg1, histImg2, Imgproc.CV_COMP_INTERSECT);
        double result3 = Imgproc.compareHist(histImg1, histImg2, Imgproc.CV_COMP_BHATTACHARYYA);

        int count = 0;
        if (result0 > 0.9) count++;
        if (result1 < 0.1) count++;
        if (result2 > 1.5) count++;
        if (result3 < 0.3) count++;

        if (count >= 3) retVal = 1;


        return retVal;
    }
}