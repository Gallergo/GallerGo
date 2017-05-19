#include <jni.h>
#include <opencv2/opencv.hpp>
#include <android/asset_manager_jni.h>
#include <android/log.h>

using namespace cv;
using namespace std;

extern "C" {

JNIEXPORT void JNICALL
Java_com_example_yu_opencvhist_MainActivity_loadImage1(
        JNIEnv *env,
        jobject,
        jstring imageFileName,
        jlong addrImage) {

    Mat &img1 = *(Mat *) addrImage;

    const char *nativeFileNameString = env->GetStringUTFChars(imageFileName, JNI_FALSE);

    string baseDir("/storage/emulated/0/");
    baseDir.append(nativeFileNameString);
    const char *pathDir = baseDir.c_str();

    img1 = imread(pathDir, IMREAD_COLOR);

}

JNIEXPORT void JNICALL
Java_com_example_yu_opencvhist_MainActivity_loadImage2(
        JNIEnv *env,
        jobject,
        jstring imageFileName,
        jlong addrImage) {

    Mat &img2 = *(Mat *) addrImage;

    const char *nativeFileNameString = env->GetStringUTFChars(imageFileName, JNI_FALSE);

    string baseDir("/storage/emulated/0/");
    baseDir.append(nativeFileNameString);
    const char *pathDir = baseDir.c_str();

    img2 = imread(pathDir, IMREAD_COLOR);

}

JNIEXPORT jdouble JNICALL
Java_com_example_yu_opencvhist_MainActivity_compare(
        JNIEnv *env,
        jobject,
        jlong addrInputImage,
        jlong addrOutputImage) {

    int retVal = 0;
    Mat &img1 = *(Mat *) addrInputImage;
    Mat &img2 = *(Mat *) addrOutputImage;
    Mat hsvImg1;
    Mat hsvImg2;

    cvtColor( img1, hsvImg1, CV_BGR2HSV);
    cvtColor( img2, hsvImg2, CV_BGR2HSV);

    /// Using 50 bins for hue and 60 for saturation
    int h_bins = 60; int s_bins = 60;
    int histSize[] = { h_bins, s_bins };

    // hue varies from 0 to 179, saturation from 0 to 255
    float h_ranges[] = { 0, 180 };
    float s_ranges[] = { 0, 256 };

    const float* ranges[] = { h_ranges, s_ranges };

    // Use the o-th and 1-st channels
    int channels[] = { 0, 1 };

    /// Histograms
    MatND hist_img1;
    MatND hist_img2;

    /// Calculate the histograms for the HSV images
    calcHist( &hsvImg1, 1, channels, Mat(), hist_img1, 2, histSize, ranges, true, false );
    normalize( hist_img1, hist_img1, 0, 1, NORM_MINMAX, -1, Mat() );

    calcHist( &hsvImg2, 1, channels, Mat(), hist_img2, 2, histSize, ranges, true, false );
    normalize( hist_img2, hist_img2, 0, 1, NORM_MINMAX, -1, Mat() );

    /// Apply the histogram comparison methods

    double result0 = compareHist(hist_img1, hist_img2, 0);
    double result1 = compareHist(hist_img1, hist_img2, 1);
    double result2 = compareHist(hist_img1, hist_img2, 2);
    double result3 = compareHist(hist_img1, hist_img2, 3);

    int count = 0;
    if (result0 > 0.9) count++;
    if (result1 < 0.1) count++;
    if (result2 > 1.5) count++;
    if (result3 < 0.3) count++;

    if (count >= 3) retVal = 1;

    return result1;
}
}
