#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>

using namespace cv;
extern "C"

JNIEXPORT jint JNICALL
Java_com_example_yu_opencvhist_MainActivity_CompareHistogram(JNIEnv *env, jobject instance,
                                                             jstring filename1_,
                                                             jstring filename2_) {
    const char *filename1 = env->GetStringUTFChars(filename1_, 0);
    const char *filename2 = env->GetStringUTFChars(filename2_, 0);

    // TODO
    // Load images to compare
    int retVal=0;
    Mat img1 = imread(filename1, IMREAD_COLOR);
    Mat img2 = imread(filename2, IMREAD_COLOR);
    Mat hsvImg1;
    Mat hsvImg2;


    // Convert to HSV
    cvtColor(img1, hsvImg1, COLOR_BGR2HSV);
    cvtColor(img2, hsvImg2, COLOR_BGR2HSV);


    // Set configuration for calchist()
    int h_bins = 50; int s_bins = 60;
    int histSize[] = { h_bins, s_bins };

    // hue varies from 0 to 179, saturation from 0 to 255
    float h_ranges[] = { 0, 180 };
    float s_ranges[] = { 0, 256 };

    const float* ranges[] = { h_ranges, s_ranges };

    // Use the o-th and 1-st channels
    int channels[] = { 0, 1 };

    // Histograms
    MatND histImg1;
    MatND histImg2;

    // Calculate the histogram for the HSV imgaes
    calcHist( &hsvImg1, 1, channels, Mat(), histImg1, 2, histSize, ranges, true, false );
    normalize( histImg1, histImg1, 0, 1, NORM_MINMAX, -1, Mat() );

    calcHist( &hsvImg2, 1, channels, Mat(), histImg2, 2, histSize, ranges, true, false );
    normalize( histImg2, histImg2, 0, 1, NORM_MINMAX, -1, Mat() );

    double result0 = compareHist(histImg1, histImg2, 0);
    double result1 = compareHist(histImg1, histImg2, 1);
    double result2 = compareHist(histImg1, histImg2, 2);
    double result3 = compareHist(histImg1, histImg2, 3);

    int count=0;
    if (result0 > 0.9) count++;
    if (result1 < 0.1) count++;
    if (result2 > 1.5) count++;
    if (result3 < 0.3) count++;

    if (count >= 3) retVal = 1;

    env->ReleaseStringUTFChars(filename1_, filename1);
    env->ReleaseStringUTFChars(filename2_, filename2);
}
