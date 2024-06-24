package com.aratek.trustfinger.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aratek.trustfinger.Config;
import com.aratek.trustfinger.R;
import com.aratek.trustfinger.bean.LargestFingerData;
import com.aratek.trustfinger.common.CommonUtil;
import com.aratek.trustfinger.interfaces.FileSelectCallBack;
import com.aratek.trustfinger.interfaces.ViewStatusCallback;
import com.aratek.trustfinger.sdk.FingerPosition;
import com.aratek.trustfinger.sdk.ImgCompressAlg;
import com.aratek.trustfinger.sdk.LfdLevel;
import com.aratek.trustfinger.sdk.LfdStatus;
import com.aratek.trustfinger.sdk.TrustFingerDevice;
import com.aratek.trustfinger.sdk.TrustFingerException;
import com.aratek.trustfinger.utils.DBHelper;
import com.aratek.trustfinger.utils.MediaPlayerHelper;
import com.aratek.trustfinger.widget.FingerView;
import com.aratek.trustfinger.widget.OpenCaptureFileDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CaptureFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "CaptureFragment";
    private static final int TYPE_FEATURE = 0;
    private static final int TYPE_IMAGE = 1;
    private static final int MSG_RESET_UI = 0;
    private static final int MSG_UPDATE_IMAGE = 1;
    private static final int MSG_CAPTURE_SUCCESS = 2;
    private static final int MSG_CAPTURE_FAIL = 3;
    private static final int MSG_CAPTURE_WARNING = 4;

    private ScrollView sv;
    private CheckBox mCheckBox_auto_save;
    private TextView mTextView_lable_file_path;
    private TextView mTextView_select_feature_format;
    private PopupWindow popupWindow;
    private TextView mTextView_select_image_format;
    private TextView mTextView_image_quality;
    private EditText mEditText_image_quality_threshold;
    private LinearLayout mLinearLayout_hand;
    private FingerView mFingerView_left_little;
    private FingerView mFingerView_left_ring;
    private FingerView mFingerView_left_middle;
    private FingerView mFingerView_left_index;
    private FingerView mFingerView_left_thumb;

    private FingerView mFingerView_right_thumb;
    private FingerView mFingerView_right_index;
    private FingerView mFingerView_right_middle;
    private FingerView mFingerView_right_ring;
    private FingerView mFingerView_right_little;
    private TextView mTextView_current_position;

    private ImageView mImageView_fingerprint;
    private ProgressBar mProgressBar_image_quality;
    private ImageView mImageView_tips_image;
    private TextView mTextView_tips_msg;

    private String mFilePath;
    private boolean isCaturing = false;
    private int mImageQualityThrethold = 50;
    private String mFeatureFormat = "bione";
    private String mImageFormat = "bmp";
    private String rootPath = Config.COMMON_PATH + "/FingerData/";
    private CaptureTask mCaptureTask;
    private FingerView currentFingerView;
    private FingerPosition mFingerPosition;
    private View root;
    private DBHelper mDbHelper;
    private LargestFingerData largestFingerData = new LargestFingerData();
    private ViewStatusCallback callback;

    public void setLedCallback(ViewStatusCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_capture, container, false);
            sv = (ScrollView) root.findViewById(R.id.sv_content);
            mCheckBox_auto_save = (CheckBox) root.findViewById(R.id.chk_auto_save);
            mTextView_lable_file_path = (TextView) root.findViewById(R.id.tv_lable_file_path);
            mTextView_select_feature_format = (TextView) root.findViewById(R.id.tv_select_feature_format);
            mTextView_image_quality = (TextView) root.findViewById(R.id.tv_image_quality);
            mEditText_image_quality_threshold = (EditText) root.findViewById(R.id.et_image_quality_threshold);
            mTextView_select_image_format = (TextView) root.findViewById(R.id.tv_select_image_format);

            mLinearLayout_hand = (LinearLayout) root.findViewById(R.id.ly_hand);
            mFingerView_left_thumb = (FingerView) root.findViewById(R.id.fv_left_thumb);
            mFingerView_left_thumb.setImageResources(R.drawable.f1_normal,
                    R.drawable.f1_selected,
                    R.drawable.f1_registed, R.drawable.f1_selected_registed);
            mFingerView_left_index = (FingerView) root.findViewById(R.id.fv_left_index);
            mFingerView_left_index.setImageResources(R.drawable.f2_normal,
                    R.drawable.f2_selected,
                    R.drawable.f2_registed, R.drawable.f2_selected_registed);
            mFingerView_left_middle = (FingerView) root.findViewById(R.id.fv_left_middle);
            mFingerView_left_middle.setImageResources(R.drawable.f3_normal,
                    R.drawable.f3_selected,
                    R.drawable.f3_registed, R.drawable.f3_selected_registed);
            mFingerView_left_ring = (FingerView) root.findViewById(R.id.fv_left_ring);
            mFingerView_left_ring.setImageResources(R.drawable.f4_normal,
                    R.drawable.f4_selected,
                    R.drawable.f4_registed, R.drawable.f4_selected_registed);
            mFingerView_left_little = (FingerView) root.findViewById(R.id.fv_left_lilttle);
            mFingerView_left_little.setImageResources(R.drawable.f5_normal,
                    R.drawable.f5_selected,
                    R.drawable.f5_registed, R.drawable.f5_selected_registed);

            mFingerView_right_thumb = (FingerView) root.findViewById(R.id.fv_right_thumb);
            mFingerView_right_thumb.setImageResources(R.drawable.f1_normal,
                    R.drawable.f1_selected,
                    R.drawable.f1_registed, R.drawable.f1_selected_registed);
            mFingerView_right_index = (FingerView) root.findViewById(R.id.fv_right_index);
            mFingerView_right_index.setImageResources(R.drawable.f2_normal,
                    R.drawable.f2_selected,
                    R.drawable.f2_registed, R.drawable.f2_selected_registed);
            mFingerView_right_middle = (FingerView) root.findViewById(R.id.fv_right_middle);
            mFingerView_right_middle.setImageResources(R.drawable.f3_normal,
                    R.drawable.f3_selected,
                    R.drawable.f3_registed, R.drawable.f3_selected_registed);
            mFingerView_right_ring = (FingerView) root.findViewById(R.id.fv_right_ring);
            mFingerView_right_ring.setImageResources(R.drawable.f4_normal,
                    R.drawable.f4_selected,
                    R.drawable.f4_registed, R.drawable.f4_selected_registed);
            mFingerView_right_little = (FingerView) root.findViewById(R.id.fv_right_little);
            mFingerView_right_little.setImageResources(R.drawable.f5_normal,
                    R.drawable.f5_selected,
                    R.drawable.f5_registed, R.drawable.f5_selected_registed);

            mCheckBox_auto_save.setChecked((Boolean) getParameterFromPreferences(Config.AUTO_SAVE, false));
            mTextView_lable_file_path.setSelected(true);
            mTextView_lable_file_path.setText((String) getParameterFromPreferences(Config.FEATURE_PATH, null));
            mTextView_select_feature_format.setText((String) getParameterFromPreferences(Config.FEATURE_FORMAT, null));
            mTextView_select_image_format.setText((String) getParameterFromPreferences(Config.IMAGE_FORMAT, null));
            mEditText_image_quality_threshold.setText((String) getParameterFromPreferences(Config.CAPTURE_IMAGE_QUALITY_THRESHOLD, null));
            rootPath = (String) getParameterFromPreferences(Config.FEATURE_PATH, null);

            mFingerView_left_thumb.setOnClickListener(this);
            mFingerView_left_index.setOnClickListener(this);
            mFingerView_left_middle.setOnClickListener(this);
            mFingerView_left_ring.setOnClickListener(this);
            mFingerView_left_little.setOnClickListener(this);

            mFingerView_right_thumb.setOnClickListener(this);
            mFingerView_right_index.setOnClickListener(this);
            mFingerView_right_middle.setOnClickListener(this);
            mFingerView_right_ring.setOnClickListener(this);
            mFingerView_right_little.setOnClickListener(this);

            mTextView_current_position = (TextView) root.findViewById(R.id.tv_current_position);

            mImageView_fingerprint = (ImageView) root.findViewById(R.id.iv_fingerprint);
            mProgressBar_image_quality = (ProgressBar) root.findViewById(R.id.proBar_image_quality);
            mImageView_tips_image = (ImageView) root.findViewById(R.id.iv_tips_image);
            mTextView_tips_msg = (TextView) root.findViewById(R.id.tv_tips_msg);

            mCheckBox_auto_save.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    saveParameterToPreferences(Config.AUTO_SAVE, isChecked);
                    if (isChecked) {
                        mTextView_select_feature_format.setEnabled(true);
                        mEditText_image_quality_threshold.setEnabled(true);
                        mTextView_select_image_format.setEnabled(true);
                    } else {
                        mTextView_select_feature_format.setEnabled(false);
                        mEditText_image_quality_threshold.setEnabled(false);
                        mTextView_select_image_format.setEnabled(false);

                    }
                }
            });
            //高版本OS 权限被限制
//            mTextView_lable_file_path.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    exportFilePath();
//                }
//            });
            mTextView_select_image_format.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupWindow(TYPE_IMAGE, mTextView_select_image_format, R.array.image_format);
                    if (popupWindow != null && !popupWindow.isShowing()) {
                        popupWindow.showAsDropDown(mTextView_select_image_format, 0, 10);
                    }
                }
            });

            mTextView_select_feature_format.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupWindow(TYPE_FEATURE, mTextView_select_feature_format, R.array.feature_format);
                    if (popupWindow != null && !popupWindow.isShowing()) {
                        popupWindow.showAsDropDown(mTextView_select_feature_format, 0, 10);
                    }
                }
            });

            mEditText_image_quality_threshold.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    saveParameterToPreferences(Config.CAPTURE_IMAGE_QUALITY_THRESHOLD, s.toString());
                }
            });
        }
        mDbHelper = new DBHelper(getActivity(), Config.SAVE_TO_SDCARD);
        viewCreated = true;
        return root;
    }

    private void exportFilePath() {
        OpenCaptureFileDialog openCaptureFileDialog = new OpenCaptureFileDialog(getContext(), new FileSelectCallBack() {
            @Override
            public void backFilePath(String path) {
                if (!TextUtils.isEmpty(path)) {
                    rootPath = path;
                }
                mTextView_lable_file_path.setText(rootPath);
                saveParameterToPreferences(Config.FEATURE_PATH, rootPath);
            }
        }, Environment.getExternalStorageDirectory().getAbsolutePath());
        Dialog dialog = openCaptureFileDialog.getDialog();
        dialog.show();
    }

    private void showPopupWindow(final int type, final TextView tv, int resId) {
        ListView lv = new ListView(getActivity());
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(getActivity(), resId, R.layout.spinner_list_item);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_list_item);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (type == TYPE_FEATURE) {
                    mFeatureFormat = parent.getItemAtPosition(position).toString();
                    tv.setText(mFeatureFormat);
                    saveParameterToPreferences(Config.FEATURE_FORMAT, mFeatureFormat);
                    popupWindow.dismiss();
                    switch (mFeatureFormat) {
                        case "Aratek Bione":
                            mFeatureFormat = "bione";
                            break;
                        case "ISO 19794-2:2005":
                            mFeatureFormat = "iso-fmr";
                            break;
                        case "ANSI 378-2004":
                            mFeatureFormat = "ansi-fmr";
                            break;
                    }
                } else if (type == TYPE_IMAGE) {
                    mImageFormat = parent.getItemAtPosition(position).toString();
                    saveParameterToPreferences(Config.IMAGE_FORMAT, mImageFormat);
                    tv.setText(mImageFormat);
                    popupWindow.dismiss();
                    switch (mImageFormat) {
                        case "BMP":
                            mImageFormat = "bmp";
                            break;
                        case "WSQ":
                            mImageFormat = "wsq";
                            break;
                        case "RAW":
                            mImageFormat = "raw";
                            break;
                        case "ISO 19794-4:2005":
                            mImageFormat = "iso-fir";
                            break;
                        case "ANSI 381-2004":
                            mImageFormat = "ansi-fir";
                            break;
                    }
                }

            }
        });
        popupWindow = new PopupWindow(lv, tv.getWidth(), ListView.LayoutParams.WRAP_CONTENT, true);
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.bg_corner);
        popupWindow.setBackgroundDrawable(drawable);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupWindow.dismiss();
            }
        });
    }

    private void doCapture() {

        if (!isCaturing) {
            mCaptureTask = new CaptureTask(mTextView_image_quality, mProgressBar_image_quality);
            mCaptureTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            //            isCaturing = true;
            mImageView_fingerprint.setImageBitmap(null);
            mTextView_image_quality.setText("");
            mProgressBar_image_quality.setProgress(0);
            mImageView_tips_image.setImageDrawable(null);
            mTextView_tips_msg.setText("");
            if (mCheckBox_auto_save.isChecked()) {
                enbleSettingsView(false);
            } else {
                mCheckBox_auto_save.setEnabled(false);
            }
            //                    enbleSettingsView(false);
            handleMsg("Capturing", Color.BLACK);
        }
    }

    private boolean checkSettings() {
        String imageQuality = mEditText_image_quality_threshold.getText().toString().trim();
        if ("".equals(imageQuality) || Integer.parseInt(imageQuality) <= 0 || Integer.parseInt(imageQuality) > 100) {
            sv.fullScroll(ScrollView.FOCUS_UP);
            mEditText_image_quality_threshold.setText("");
            mEditText_image_quality_threshold.setHintTextColor(Color.RED);
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
            mEditText_image_quality_threshold.startAnimation(anim);
            mHandler.sendMessage(mHandler.obtainMessage(MSG_CAPTURE_WARNING, "The quality must between 50 and 100"));
            handleMsg("The quality must between 50 and 100", Color.RED);
            return false;
        }
        return true;
    }

    private void enbleSettingsView(boolean enable) {
        mCheckBox_auto_save.setEnabled(enable);
        mTextView_select_feature_format.setEnabled(enable);
        mTextView_select_image_format.setEnabled(enable);
        mTextView_image_quality.setEnabled(enable);
        mEditText_image_quality_threshold.setEnabled(enable);
        mLinearLayout_hand.setEnabled(enable);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mTrustFingerDevice != null) {
            setUI(mTrustFingerDevice);
        } else {
            resetUI();
        }
    }

    public void resetUI() {
        if (viewCreated) {
            mHandler.sendEmptyMessage(MSG_RESET_UI);
        }
    }

    protected void setUI(TrustFingerDevice device) {
        mTextView_current_position.setText(getString(R.string.msg_select_a_finger));
        mTextView_current_position.setTextColor(Color.RED);
        mProgressBar_image_quality.setProgress(0);
        mImageView_fingerprint.setImageBitmap(null);

    }

    public void forceStop() {
        if (isCaturing) {
            if (mCaptureTask != null && mCaptureTask.getStatus() != AsyncTask.Status.FINISHED) {
                mCaptureTask.cancel(false);
                mCaptureTask.waitForDone();
                mCaptureTask = null;
            }
            isCaturing = false;
            mImageView_fingerprint.setImageBitmap(null);
            mTextView_image_quality.setText("");
            if (mCheckBox_auto_save.isChecked()) {
                enbleSettingsView(true);
            } else {
                mCheckBox_auto_save.setEnabled(true);
            }
            if (currentFingerView != null) {
                currentFingerView.setSelected(false);
                currentFingerView = null;
                mFingerPosition = null;
                if (mTrustFingerDevice != null) {
                    mTextView_current_position.setText(getString(R.string.msg_select_a_finger));
                    mTextView_current_position.setTextColor(Color.RED);
                } else {
                    mTextView_current_position.setText("");
                }
            }
            handleMsg("Capture stopped", Color.BLACK);
        }
    }

    @Override
    public void setDatas(TrustFingerDevice device) {
        if (isAdded()) {
            mTrustFingerDevice = device;
            if (device != null) {
                if (viewCreated) {
                    setUI(mTrustFingerDevice);
                }
            } else {
                if (viewCreated) {
                    forceStop();
                    if (mCheckBox_auto_save.isChecked()) {
                        enbleSettingsView(true);
                    } else {
                        mCheckBox_auto_save.setEnabled(true);
                    }
                    resetUI();
                }
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        resetUI();
        forceStop();
    }

    @Override
    public void onClick(View view) {
        if (mTrustFingerDevice == null) {
            //            mHandler.sendMessage(mHandler.obtainMessage(MSG_CAPTURE_WARNING, "Device not opened"));
            handleMsg("Device not opened", Color.RED);
            return;
        }
        boolean isAutoSave = (boolean) getParameterFromPreferences(Config.AUTO_SAVE, false);
        if (!checkSettings()) {
            return;
        }
        FingerPosition currentFingerPosition = null;
        mTextView_current_position.setTextColor(Color.BLACK);
        switch (view.getId()) {
            case R.id.fv_left_lilttle:
                currentFingerPosition = FingerPosition.LeftLittleFinger;
                mTextView_current_position.setText("Finger position: Left Little Finger");
                break;
            case R.id.fv_left_ring:
                currentFingerPosition = FingerPosition.LeftRingFinger;
                mTextView_current_position.setText("Finger position: Left Ring Finger");
                break;
            case R.id.fv_left_middle:
                currentFingerPosition = FingerPosition.LeftMiddleFinger;
                mTextView_current_position.setText("Finger position: Left Middle Finger");
                break;
            case R.id.fv_left_index:
                currentFingerPosition = FingerPosition.LeftIndexFinger;
                mTextView_current_position.setText("Finger position: Left Index Finger");
                break;
            case R.id.fv_left_thumb:
                currentFingerPosition = FingerPosition.LeftThumb;
                mTextView_current_position.setText("Finger position: Left Thumb");
                break;
            case R.id.fv_right_thumb:
                currentFingerPosition = FingerPosition.RightThumb;
                mTextView_current_position.setText("Finger position: Right Thumb");
                break;
            case R.id.fv_right_index:
                currentFingerPosition = FingerPosition.RightIndexFinger;
                mTextView_current_position.setText("Finger position: Right Index Finger");
                break;
            case R.id.fv_right_middle:
                currentFingerPosition = FingerPosition.RightMiddleFinger;
                mTextView_current_position.setText("Finger position: Right Middle Finger");
                break;
            case R.id.fv_right_ring:
                currentFingerPosition = FingerPosition.RightRingFinger;
                mTextView_current_position.setText("Finger position: Right Ring Finger");
                break;
            case R.id.fv_right_little:
                currentFingerPosition = FingerPosition.RightLittleFinger;
                mTextView_current_position.setText("Finger position: Right Little Finger");
                break;
        }

        //        forceStop();
        if (currentFingerView != null) {
            if (currentFingerPosition.getPosition() == mFingerPosition.getPosition()) {
                if (isCaturing) {
                    if (mCaptureTask != null && mCaptureTask.getStatus() != AsyncTask.Status.FINISHED) {
                        mCaptureTask.cancel(false);
                        mCaptureTask.waitForDone();
                        mCaptureTask = null;
                    }
                    isCaturing = false;
                }
                currentFingerView.setSelected(false);
                currentFingerView = null;
                mFingerPosition = null;
                if (mTrustFingerDevice != null) {
                    mTextView_current_position.setText(getString(R.string.msg_select_a_finger));
                    mTextView_current_position.setTextColor(Color.RED);
                } else {
                    mTextView_current_position.setText("");
                }
                if (mCheckBox_auto_save.isChecked()) {
                    enbleSettingsView(true);
                } else {
                    mCheckBox_auto_save.setEnabled(true);
                }
                handleMsg("Capture stopped", Color.BLACK);
                return;
            } else {
                currentFingerView.setSelected(false);
            }
        }
        mFingerPosition = currentFingerPosition;
        currentFingerView = (FingerView) view;
        currentFingerView.setSelected(true);
        doCapture();
    }

    private boolean isAutoSave;


    private class CaptureTask extends AsyncTask<Void, Integer, Void> {
        private boolean mIsDone = false;
        private TextView textViewQulity;
        private ProgressBar progressBar;
        private Bitmap fpImage_bitmap = null;
        private byte[] fpImage_Raw = null;
        private byte[] fpImage_Data = null;
        private byte[] fpImage_bmp = null;
        private byte[] fpFeatureData = null;
        private int imageQuality = 0;
        private long startTime, endTime;

        public CaptureTask(TextView textViewQulity, ProgressBar progressBar) {
            super();
            this.textViewQulity = textViewQulity;
            this.progressBar = progressBar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isCaturing = true;
            sv.fullScroll(ScrollView.FOCUS_DOWN);
            if (mApp.isLedEnable()) {
                ledOnRed();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            boolean isThreshold = false;
            largestFingerData.clear();
            callback.setLedEnable(false);
            callback.setLfdEnable(true);
            startTime = 0;
            endTime = 0;
            int qualityLowTimes = 0;
            do {
                if (isCancelled()) {
                    break;
                }
                if (mTrustFingerDevice == null || !mTrustFingerDevice.isOpened()) {
                    handleMsg("Device not opened", Color.RED);
                    break;
                }
                isAutoSave = (boolean) getParameterFromPreferences(Config.AUTO_SAVE, false);

                if (largestFingerData.isIsrRaise()) {
                    MediaPlayerHelper.payMedia(getContext(), R.raw.please_press_your_finger);
                    mImageQualityThrethold = Integer.parseInt(mEditText_image_quality_threshold.getText().toString().trim());
                    largestFingerData.setIsrRaise(false);
                }
                try {
                    int[] lfdStatus = new int[1];
                    startTime = System.currentTimeMillis();
                    if (mTrustFingerDevice.getLfdLevel() != LfdLevel.OFF) {
                        lfdStatus[0] = mTrustFingerDevice.getLfdLevel();
                        fpImage_Raw = mTrustFingerDevice.captureRawDataLfd(lfdStatus);
                        endTime = System.currentTimeMillis();
                        if (lfdStatus[0] == LfdStatus.FAKE) {
                            handleMsg("fake finger", Color.RED);
                            MediaPlayerHelper.payMedia(getContext(), R.raw.is_fake_finger);
                            mHandler.sendMessage(mHandler.obtainMessage(MSG_CAPTURE_FAIL, "fake finger"));
                            break;
                        }
                    } else {
                        fpImage_Raw = mTrustFingerDevice.captureRawData();
                        endTime = System.currentTimeMillis();
                    }
                    if (fpImage_Raw == null || (mTrustFingerDevice.getLfdLevel() != LfdLevel.OFF && lfdStatus[0] == LfdStatus.UNKNOWN)) {
                        imageQuality = 0;
                        publishProgress(0);
                        updateFingerprintImage(null);
                        handleMsg("unknown finger", Color.RED);
                        continue;
                    }
                    imageQuality = mTrustFingerDevice.rawDataQuality(fpImage_Raw);
                    fpImage_bmp = mTrustFingerDevice.rawToBmp(fpImage_Raw, mTrustFingerDevice.getImageInfo().getWidth(), mTrustFingerDevice.getImageInfo().getHeight(), mTrustFingerDevice
                            .getImageInfo().getResolution());

                    if (fpImage_bmp == null || imageQuality <= Config.minQualityTh) {
//                        handleMsg("bad image", Color.RED);
                        publishProgress(0);
                        updateFingerprintImage(null);
                        continue;
                    }
                    handleMsg(" ", Color.RED);
                    fpImage_bitmap = BitmapFactory.decodeByteArray(fpImage_bmp, 0, fpImage_bmp.length);
                    if (isAutoSave) {
                        Log.e(TAG, "mImageFormat: " + mImageFormat);
                        switch (mImageFormat) {
                            case "bmp":
                                fpImage_Data = fpImage_bmp;
                                break;
                            case "wsq":
                                fpImage_Data = mTrustFingerDevice.rawToWsq(fpImage_Raw, mTrustFingerDevice.getImageInfo().getWidth(), mTrustFingerDevice.getImageInfo().getHeight(),
                                        mTrustFingerDevice.getImageInfo().getResolution());
                                break;
                            case "raw":
                                fpImage_Data = fpImage_Raw;
                                break;
                            case "iso-fir":
                                fpImage_Data = mTrustFingerDevice.rawToISO(fpImage_Raw
                                        , mTrustFingerDevice.getImageInfo().getWidth()
                                        , mTrustFingerDevice.getImageInfo().getHeight()
                                        , mTrustFingerDevice.getImageInfo().getResolution()
                                        , mFingerPosition
                                        , ImgCompressAlg.UNCOMPRESSED_NO_BIT_PACKING);
                                break;
                            case "ansi-fir":
                                fpImage_Data = mTrustFingerDevice.rawToANSI(fpImage_Raw
                                        , mTrustFingerDevice.getImageInfo().getWidth()
                                        , mTrustFingerDevice.getImageInfo().getHeight()
                                        , mTrustFingerDevice.getImageInfo().getResolution()
                                        , mFingerPosition
                                        , ImgCompressAlg.UNCOMPRESSED_NO_BIT_PACKING);
                                break;
                        }
                    }
                    publishProgress(imageQuality);
                    updateFingerprintImage(fpImage_bitmap);
                    if (imageQuality >= mImageQualityThrethold) {
                        try {
                            if (mTrustFingerDevice == null) {
                                handleMsg("Device not opened", Color.RED);
                                break;
                            }
                            if (isAutoSave) {
                                switch (mFeatureFormat) {
                                    case "iso-fmr":// ISO
                                        fpFeatureData = mTrustFingerDevice.extractISOFeature(fpImage_Raw, mFingerPosition);
                                        break;
                                    case "ansi-fmr":// ANSI
                                        fpFeatureData = mTrustFingerDevice.extractANSIFeature(fpImage_Raw, mFingerPosition);
                                        break;
                                    case "bione":// BIONE
                                        fpFeatureData = mTrustFingerDevice.extractFeature(fpImage_Raw, mFingerPosition);
                                        break;
                                }
                            }
                            if (imageQuality > largestFingerData.getImgQuality()) {
                                largestFingerData.update(fpImage_Data, fpFeatureData, imageQuality, fpImage_bitmap);
                            }

                        } catch (TrustFingerException e) {
                            mCaptureTask = null;
                            isCaturing = false;
                            fpFeatureData = null;
                            mHandler.sendMessage(mHandler.obtainMessage(MSG_CAPTURE_FAIL, "Capture exception:" + e.getType().toString()));
                            handleMsg("Capture exception: " + e.getType().toString(), Color.RED);
                            e.printStackTrace();
                            break;
                        }
                    }
//                    if (largestFingerData.getImgQuality() >= Config.maxQualityTh) {
//                        qualityLowTimes = 3;
//                    } else {
//                        if (imageQuality >= largestFingerData.getImgQuality()) {//try again 3 times.
//                            qualityLowTimes = 0;
//                        } else {
//                            qualityLowTimes++;
//                        }
//                    }
                    if (/*qualityLowTimes >= 3 && */!largestFingerData.isIsrRaise() && largestFingerData.getImgQuality() >= mImageQualityThrethold) {
                        if (mApp.isLedEnable()) {
                            ledOnGreen();
                        }
                        MediaPlayerHelper.payMedia(getContext(), R.raw.please_raise_your_finger);
                        saveFingerData(largestFingerData.getFpImageData(), largestFingerData.getFpFeatureData(), largestFingerData.getImgQuality(), (int) Math.abs(endTime - startTime));
                        publishProgress(largestFingerData.getImgQuality());
                        updateFingerprintImage(largestFingerData.getFpImage_bitmap());
                        break;
                    }
                } catch (TrustFingerException e) {
                    mCaptureTask = null;
                    isCaturing = false;
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_CAPTURE_FAIL, "Capture exception:" + e.getType().toString()));
                    handleMsg("Capture exception: " + e.getType().toString(), Color.RED);
                    e.printStackTrace();
                    break;
                }
            } while (true);
            callback.setLedEnable(true);
            callback.setLfdEnable(true);
            if (mApp.isLedEnable()) {
                ledOff();
            }
            mIsDone = true;
            isCaturing = false;
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int value = values[0];
            if (mTrustFingerDevice == null) {
                textViewQulity.setText("");
                progressBar.setProgress(0);
                return;
            }
            textViewQulity.setText("" + value);
            progressBar.setProgress(value);
            super.onProgressUpdate(values);
        }

        public void waitForDone() {
            while (!mIsDone) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void saveFingerData(byte[] fpImageData, byte[] fpFeatureData, final int imgQuality, int takeTime) {
        rootPath = (String) getParameterFromPreferences(Config.FEATURE_PATH, null);
        File root = new File(rootPath);
        if (!root.exists()) {
            root.mkdirs();
        }
        getActivity().runOnUiThread(() -> {
            mTextView_lable_file_path.setText(rootPath);
        });
        String time = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date(System.currentTimeMillis()));
        String imageFileName = mTrustFingerDevice.getDeviceDescription().getProductModel() + "_" + time + "_" + mFingerPosition.name() + "_" + imgQuality + "." + mImageFormat;
        String minitaesFileName = mTrustFingerDevice.getDeviceDescription().getProductModel() + "_" + time + "_" + mFingerPosition.name() + "_" + imgQuality + "." + mFeatureFormat;

        if (isAutoSave) {
            try {
                CommonUtil.saveData(rootPath + "/" + imageFileName, fpImageData);
                CommonUtil.saveData(rootPath + "/" + minitaesFileName, fpFeatureData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mCaptureTask = null;
        isCaturing = false;
        mHandler.sendMessage(mHandler.obtainMessage(MSG_CAPTURE_SUCCESS, imgQuality, takeTime, mFingerPosition.name()));
        handleMsg("Capture success", Color.BLACK);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mCheckBox_auto_save.isChecked()) {
                    enbleSettingsView(true);
                } else {
                    mCheckBox_auto_save.setEnabled(true);
                }
            }
        });
    }


    private void updateFingerprintImage(Bitmap fpImage) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_IMAGE, fpImage));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RESET_UI:
                    if (currentFingerView != null) {
                        currentFingerView.setSelected(false);
                        currentFingerView = null;
                        mFingerPosition = null;
                    }
                    if (isAdded()) {
                        mImageView_fingerprint.setImageBitmap(null);
                        mProgressBar_image_quality.setProgress(0);
                        mImageView_tips_image.setImageDrawable(null);
                        mTextView_tips_msg.setText("");
                        mTextView_image_quality.setText("");
                        if (mTrustFingerDevice != null) {
                            mTextView_current_position.setText(getString(R.string.msg_select_a_finger));
                            mTextView_current_position.setTextColor(Color.RED);
                        } else {
                            mTextView_current_position.setText("");
                        }
                    }

                    break;
                case MSG_UPDATE_IMAGE:
                    if (mTrustFingerDevice == null) {
                        mImageView_fingerprint.setImageBitmap(null);
                        break;
                    }
                    Bitmap bmp_fpImg = (Bitmap) msg.obj;
                    if (bmp_fpImg == null) {
                        mImageView_fingerprint.setImageBitmap(null);
                    } else {
                        mImageView_fingerprint.setImageBitmap(bmp_fpImg);
                    }

                    break;
                case MSG_CAPTURE_SUCCESS: {
                    int quality = msg.arg1;
                    String beforePosition = "Capture succeed!\nFinger position: ";
                    String fingerPosition = (String) msg.obj;
                    String afterPosition = "\nImage Quality: ";
                    String sQuality = "" + quality;
                    String takeTime = "\n Take Time: ";
                    String time = msg.arg2 + " ms";
                    SpannableStringBuilder builder = new SpannableStringBuilder(beforePosition + fingerPosition + afterPosition + sQuality + takeTime + time);
                    // set finger position color
                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1D9F9A")),
                            beforePosition.length(),
                            (beforePosition.length() + fingerPosition.length()),
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                            beforePosition.length(),
                            (beforePosition.length() + fingerPosition.length()),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new AbsoluteSizeSpan(16, true),
                            beforePosition.length(),
                            (beforePosition.length() + fingerPosition.length()),
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    // set quality color
                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1D9F9A")),
                            (beforePosition.length() + fingerPosition.length() + afterPosition.length()),
                            (beforePosition.length() + fingerPosition.length() + afterPosition.length() + sQuality.length()),
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                            (beforePosition.length() + fingerPosition.length() + afterPosition.length()),
                            (beforePosition.length() + fingerPosition.length() + afterPosition.length() + sQuality.length()),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new AbsoluteSizeSpan(16, true),
                            (beforePosition.length() + fingerPosition.length() + afterPosition.length()),
                            (beforePosition.length() + fingerPosition.length() + afterPosition.length() + sQuality.length()),
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    // take time
                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1D9F9A")),
                            (beforePosition.length() + fingerPosition.length() + afterPosition.length() + sQuality.length() + takeTime.length()),
                            (beforePosition.length() + fingerPosition.length() + afterPosition.length() + sQuality.length() + takeTime.length() + time.length()),
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                            (beforePosition.length() + fingerPosition.length() + afterPosition.length() + sQuality.length() + takeTime.length()),
                            (beforePosition.length() + fingerPosition.length() + afterPosition.length() + sQuality.length() + takeTime.length() + time.length()),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new AbsoluteSizeSpan(16, true),
                            (beforePosition.length() + fingerPosition.length() + afterPosition.length() + sQuality.length() + takeTime.length()),
                            (beforePosition.length() + fingerPosition.length() + afterPosition.length() + sQuality.length() + takeTime.length() + time.length()),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    mImageView_tips_image.setImageDrawable(getResources().getDrawable(R.drawable.success));
                    mTextView_tips_msg.setText(builder);
                    if (currentFingerView != null) {
                        currentFingerView.setSelected(false);
                        currentFingerView = null;
                        mFingerPosition = null;
                    }
                    if (mTrustFingerDevice != null) {
                        mTextView_current_position.setText(getString(R.string.msg_select_a_finger));
                        mTextView_current_position.setTextColor(Color.RED);
                    } else {
                        mTextView_current_position.setText("");
                    }
                    sv.fullScroll(ScrollView.FOCUS_DOWN);
                    break;
                }
                case MSG_CAPTURE_FAIL: {
                    isCaturing = false;
                    mCaptureTask = null;

                    String s = (String) msg.obj;
                    mImageView_tips_image.setImageDrawable(getResources().getDrawable(R.drawable.fail));
                    mTextView_tips_msg.setText(s);
                    if (currentFingerView != null) {
                        currentFingerView.setSelected(false);
                        currentFingerView = null;
                        mFingerPosition = null;
                    }
                    if (mTrustFingerDevice != null) {
                        mTextView_current_position.setText(getString(R.string.msg_select_a_finger));
                        mTextView_current_position.setTextColor(Color.RED);
                    } else {
                        mTextView_current_position.setText("");
                    }
                    if (mCheckBox_auto_save.isChecked()) {
                        enbleSettingsView(true);
                    } else {
                        mCheckBox_auto_save.setEnabled(true);
                    }
                    break;
                }
                case MSG_CAPTURE_WARNING: {
                    String s = (String) msg.obj;
                    mImageView_tips_image.setImageDrawable(getResources().getDrawable(R.drawable.warning));
                    mTextView_tips_msg.setText(s);
                    break;
                }
            }
        }
    };
}
