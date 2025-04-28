package com.example.janet_ahn_myruns5

//globals
//referencing myrunsdatacollector code (easy to have global variables all in one place)

object Globals {
    // Debugging tag
    val TAG = "MyRuns"


    val ACCELEROMETER_BUFFER_CAPACITY = 2048
    val ACCELEROMETER_BLOCK_CAPACITY = 64

    val ACTIVITY_ID_STANDING = 2
    val ACTIVITY_ID_WALKING = 1
    val ACTIVITY_ID_RUNNING = 0
    val ACTIVITY_ID_OTHER = 2

    val SERVICE_TASK_TYPE_COLLECT = 0
    val SERVICE_TASK_TYPE_CLASSIFY = 1

    val ACTION_MOTION_UPDATED = "MYRUNS_MOTION_UPDATED"

    val CLASS_LABEL_KEY = "label"
    val CLASS_LABEL_STANDING = "standing"
    val CLASS_LABEL_WALKING = "walking"
    val CLASS_LABEL_RUNNING = "running"
    val CLASS_LABEL_OTHER = "others"

    val FEAT_FFT_COEF_LABEL = "fft_coef_"
    val FEAT_MAX_LABEL = "max"
    val FEAT_SET_NAME = "accelerometer_features"

    val FEATURE_FILE_NAME = "features.arff"
    val RAW_DATA_NAME = "raw_data.txt"
    val FEATURE_SET_CAPACITY = 10000

    val NOTIFICATION_ID = 1
}