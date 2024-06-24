package com.aratek.trustfinger;

/**
 * Created by hecl on 2018/9/26.
 */
public class Config {
    public static final int maxQualityTh = 80;
    public static final int minQualityTh = 30;


    public static String COMMON_PATH = "/sdcard/AratekTrustFinger";
    public static final String DB_PATH = "/sdcard/AratekTrustFinger/fp.db";
    public static final boolean SAVE_TO_SDCARD = false;

    public static final String SHAREDPREFERENCE_NAME = "sharedpreference_trustfinger";

    public static final String AUTO_SAVE = "auto_save";
    public static final String FEATURE_PATH = "feature_path";
    public static final String FEATURE_FORMAT = "feature_format";
    public static final String IMAGE_FORMAT = "image_format";
    public static final String CAPTURE_IMAGE_QUALITY_THRESHOLD = "capture_image_quality_threshold";
    public static final String ENROLL_IMAGE_QUALITY_THRESHOLD = "enroll_image_quality_threshold";
    public static final String VERIFY_IMAGE_QUALITY_THRESHOLD = "verify_image_quality_threshold";
    public static final String VERIFY_SECURITY_LEVEL = "verify_security_level";
    public static final String IDENTIFY_IMAGE_QUALITY_THRESHOLD = "identify_image_quality_threshold";
    public static final String IDENTIFY_SECURITY_LEVEL = "identify_security_level";
}
