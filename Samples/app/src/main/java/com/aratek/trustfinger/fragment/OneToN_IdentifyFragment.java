//package com.aratek.trustfinger.fragment;
//
//import android.annotation.SuppressLint;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.graphics.drawable.Drawable;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.content.ContextCompat;
//import android.text.Editable;
//import android.text.Spannable;
//import android.text.SpannableStringBuilder;
//import android.text.Spanned;
//import android.text.TextWatcher;
//import android.text.style.AbsoluteSizeSpan;
//import android.text.style.ForegroundColorSpan;
//import android.text.style.StyleSpan;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.PopupWindow;
//import android.widget.ProgressBar;
//import android.widget.ScrollView;
//import android.widget.TextView;
//
//import com.aratek.trustfinger.Config;
//import com.aratek.trustfinger.R;
//import com.aratek.trustfinger.adapter.MyOneToNListAdapter;
//import com.aratek.trustfinger.bean.LargestFingerData;
//import com.aratek.trustfinger.bean.User;
//import com.aratek.trustfinger.interfaces.ViewStatusCallback;
//import com.aratek.trustfinger.sdk.Bione;
//import com.aratek.trustfinger.sdk.FingerPosition;
//import com.aratek.trustfinger.sdk.LfdLevel;
//import com.aratek.trustfinger.sdk.LfdStatus;
//import com.aratek.trustfinger.sdk.Result;
//import com.aratek.trustfinger.sdk.SecurityLevel;
//import com.aratek.trustfinger.sdk.TrustFingerDevice;
//import com.aratek.trustfinger.sdk.TrustFingerException;
//import com.aratek.trustfinger.sdk.VerifyResult;
//import com.aratek.trustfinger.utils.DBHelper;
//import com.aratek.trustfinger.utils.MediaPlayerHelper;
//import com.aratek.trustfinger.widget.MyListView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class OneToN_IdentifyFragment extends BaseFragment {
//    private static final String TAG = "OneToN_IdentifyFragment";
//    private static final int MSG_RESET_UI = 0;
//    private static final int MSG_UPDATE_IMAGE = 1;
//    private static final int MSG_IDENTIFY_SUCCESS = 2;
//    private static final int MSG_IDENTIFY_FAIL = 3;
//    private static final int MSG_UPDATE_USER_LIST = 4;
//    private static final int MSG_ENROLL_SUCCESS = 5;
//    private static final int MSG_ENROLL_FAIL = 6;
//    private static final int MSG_UPDATA_FINGER_ID_UI = 7;
//    private static final int MSG_VERIFY_SUCCESS = 8;
//    private static final int MSG_VERIFY_FAIL = 9;
//
//
//    private ScrollView sv;
//    private ImageView mImageView_fingerprint;
//    private TextView mTextView_image_quality;
//    private ProgressBar mProgressBar_image_quality;
//    private EditText mEditText_image_quality_threshold, mEditText_finger_id;
//    private TextView mTextView_select_security_level;
//    private PopupWindow popupWindow;
//    private ImageView mImageView_tips_image;
//    private TextView mTextView_tips_msg;
//    private Button mButton_start_stop_identify, mButton_start_stop_enroll, mButton_start_stop_verify;
//    private MyListView mListView_users;
//    private Button mButton_remove_user;
//    private Button mButton_remove_all;
//
//    private List<User> mUserList = new ArrayList<User>();
//    private MyOneToNListAdapter myOneToNListAdapter;
//    private boolean isIdentifing = false;
//    private IdentifyTask mIdentifyTask;
//    private DBHelper mDBHelper;
//    private SecurityLevel mSecurityLevel = SecurityLevel.Level4;
//    private int mIdentifyThreshold = 48;
//    private View root;
//    private long startTime, endTime;
//    private LargestFingerData largestFingerData = new LargestFingerData();
//    private ViewStatusCallback callback;
//    private int mEnrollFingerID = 0;
//    private int mCurrentFingerID = 0;
//    private int mEnrollOrIdentifyFlag = 0; // enroll:1 ; identify:2; verify:3;
//    private final int TASK_ENROLL_FLAG = 1;
//    private final int TASK_IDENTIFY_FLAG = 2;
//    private final int TASK_VERIFY_FLAG = 3;
//
//    public void setLedCallback(ViewStatusCallback callback) {
//        this.callback = callback;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        if (root == null) {
//            root = inflater.inflate(R.layout.fragment_1to_n_identify, container, false);
//            sv = (ScrollView) root.findViewById(R.id.sv_content);
//            mImageView_fingerprint = (ImageView) root.findViewById(R.id.iv_fingerprint);
//            mTextView_image_quality = (TextView) root.findViewById(R.id.tv_image_quality);
//            mProgressBar_image_quality = (ProgressBar) root.findViewById(R.id.proBar_image_quality);
//            mEditText_finger_id = (EditText) root.findViewById(R.id.et_finger_id);
//            mEditText_image_quality_threshold = (EditText) root.findViewById(R.id.et_image_quality_threshold);
//            mTextView_select_security_level = (TextView) root.findViewById(R.id.tv_select_security_level);
//            mImageView_tips_image = (ImageView) root.findViewById(R.id.iv_tips_image);
//            mTextView_tips_msg = (TextView) root.findViewById(R.id.tv_tips_msg);
//            mButton_start_stop_identify = (Button) root.findViewById(R.id.btn_start_stop_identify);
//            mButton_start_stop_enroll = (Button) root.findViewById(R.id.btn_start_stop_enroll);
//            mButton_start_stop_verify = (Button) root.findViewById(R.id.btn_start_stop_verify);
//            mButton_remove_user = (Button) root.findViewById(R.id.btn_remove_user);
//            mButton_remove_all = (Button) root.findViewById(R.id.btn_remove_all);
//
//            mEditText_image_quality_threshold.setText((String) getParameterFromPreferences(Config.IDENTIFY_IMAGE_QUALITY_THRESHOLD, null));
//            String level = (String) getParameterFromPreferences(Config.IDENTIFY_SECURITY_LEVEL, null);
//            mTextView_select_security_level.setText(level);
//            switch (level) {
//                case "Level1":
//                    mSecurityLevel = SecurityLevel.Level1;
//                    break;
//                case "Level2":
//                    mSecurityLevel = SecurityLevel.Level2;
//                    break;
//                case "Level3":
//                    mSecurityLevel = SecurityLevel.Level3;
//                    break;
//                case "Level4":
//                    mSecurityLevel = SecurityLevel.Level4;
//                    break;
//                case "Level5":
//                    mSecurityLevel = SecurityLevel.Level5;
//                    break;
//            }
//            mListView_users = (MyListView) root.findViewById(R.id.lv_user_list);
//            mListView_users.setEmptyView(root.findViewById(R.id.tv_no_datas));
//            mListView_users.setmaxHeight(400);
//            myOneToNListAdapter = new MyOneToNListAdapter(getActivity(), mUserList);
//            mListView_users.setAdapter(myOneToNListAdapter);
//            mListView_users.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (event.getAction() == MotionEvent.ACTION_UP) {
//                        sv.requestDisallowInterceptTouchEvent(false);
//                    } else {
//                        sv.requestDisallowInterceptTouchEvent(true);
//                    }
//                    return false;
//                }
//            });
//            mEditText_finger_id.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
////                    checkEnrollSettings();
//                }
//            });
//            mEditText_image_quality_threshold.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    saveParameterToPreferences(Config.IDENTIFY_IMAGE_QUALITY_THRESHOLD, s.toString());
//                }
//            });
//            mTextView_select_security_level.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showPopupWindow();
//                    if (popupWindow != null && !popupWindow.isShowing()) {
//                        popupWindow.showAsDropDown(mTextView_select_security_level, 0, 10);
//                    }
//                }
//            });
//
//            mButton_start_stop_enroll.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mTrustFingerDevice == null) {
//                        handleMsgOneToN("Device not opened", Color.RED);
//                        return;
//                    }
//                    if (!checkSettings()) {
//                        return;
//                    }
//                    if (!checkEnrollIDSettings()) {
//                        handleMsgOneToN(" Finger ID can not null ! Please enter ID.", Color.RED);
//                        return;
//                    }
//                    if (mEnrollFingerID < 0) {
//                        handleMsgOneToN(" Bione Algorithm has not been initialized! error:" + mEnrollFingerID, Color.RED);
//                        return;
//                    }
//                    //todo 2023-05-09 fanghc
////                    if (!Bione.isFreeID(mEnrollFingerID)) {
////                        handleMsgOneToN(" Finger ID has Enroll! Please Change ID.", Color.RED);
////                        return;
////                    }
//
//                    if (!isIdentifing) {
//                        resetUI();
//                        mEnrollOrIdentifyFlag = TASK_ENROLL_FLAG;
//                        mIdentifyTask = new IdentifyTask(mTextView_image_quality, mProgressBar_image_quality);
//                        mIdentifyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                        mImageView_fingerprint.setImageBitmap(null);
//                        mTextView_image_quality.setText("");
//                        mImageView_tips_image.setImageDrawable(null);
//                        mTextView_tips_msg.setText("");
//                        mProgressBar_image_quality.setProgress(0);
//                        mButton_start_stop_enroll.setText(getString(R.string.btn_stop_enroll));
//                        enbleSettingsView(false);
//
//                    } else {
//                        if (mIdentifyTask != null && mIdentifyTask.getStatus() != AsyncTask.Status.FINISHED) {
//                            mIdentifyTask.cancel(false);
//                            mIdentifyTask.waitForDone();
//                            mIdentifyTask = null;
//                        }
//                        isIdentifing = false;
//                        mImageView_fingerprint.setImageBitmap(null);
//                        mTextView_image_quality.setText("");
//                        mButton_start_stop_enroll.setText(getString(R.string.btn_start_enroll));
//                        enbleSettingsView(true);
//                        handleMsgOneToN(" Stop finish.", Color.BLACK);
//                    }
//                }
//            });
//            mButton_start_stop_verify.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mTrustFingerDevice == null) {
//                        handleMsgOneToN("Device not opened", Color.RED);
//                        return;
//                    }
//                    if (!checkSettings()) {
//                        return;
//                    }
//
//                    if (!checkVerifyIDSettings()) {
//                        handleMsgOneToN(" Finger ID can not null ! Please enter ID.", Color.RED);
//                        return;
//                    }
//
//                    if (mEnrollFingerID < 0) {
//                        handleMsgOneToN(" Bione Algorithm has not been initialized! error:" + mEnrollFingerID, Color.RED);
//                        return;
//                    }
//                    //todo 2023-05-09 fanghc
////                    if (Bione.getEnrolledCount() <= 0) {
////                        handleMsgOneToN("No enrolled users!", Color.RED);
////                        return;
////                    }
//
//                    if (!isIdentifing) {
//                        resetUI();
//                        mEnrollOrIdentifyFlag = TASK_VERIFY_FLAG;
//                        mIdentifyTask = new IdentifyTask(mTextView_image_quality, mProgressBar_image_quality);
//                        mIdentifyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                        mImageView_fingerprint.setImageBitmap(null);
//                        mTextView_image_quality.setText("");
//                        mImageView_tips_image.setImageDrawable(null);
//                        mTextView_tips_msg.setText("");
//                        mProgressBar_image_quality.setProgress(0);
//                        mButton_start_stop_verify.setText(getString(R.string.btn_stop_verify));
//                        enbleSettingsView(false);
//
//                    } else {
//                        if (mIdentifyTask != null && mIdentifyTask.getStatus() != AsyncTask.Status.FINISHED) {
//                            mIdentifyTask.cancel(false);
//                            mIdentifyTask.waitForDone();
//                            mIdentifyTask = null;
//                        }
//                        isIdentifing = false;
//                        mImageView_fingerprint.setImageBitmap(null);
//                        mTextView_image_quality.setText("");
//                        mButton_start_stop_verify.setText(getString(R.string.btn_start_verify));
//                        enbleSettingsView(true);
//                        handleMsgOneToN(" Stop finish.", Color.BLACK);
//                    }
//                }
//            });
//            mButton_start_stop_identify.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mTrustFingerDevice == null) {
//                        handleMsgOneToN("Device not opened", Color.RED);
//                        return;
//                    }
//                    if (!checkSettings()) {
//                        return;
//                    }
//                    if (mEnrollFingerID < 0) {
//                        handleMsgOneToN(" Bione Algorithm has not been initialized! error:" + mEnrollFingerID, Color.RED);
//                        return;
//                    }
//                    // //todo 2023-05-09 fanghc
////                    if (Bione.getEnrolledCount() <= 0) {
////                        handleMsgOneToN("No enrolled users!", Color.RED);
////                        return;
////                    }
//
//                    if (!isIdentifing) {
//                        resetUI();
//                        mEnrollOrIdentifyFlag = TASK_IDENTIFY_FLAG;
//                        mIdentifyTask = new IdentifyTask(mTextView_image_quality, mProgressBar_image_quality);
//                        mIdentifyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                        mImageView_fingerprint.setImageBitmap(null);
//                        mTextView_image_quality.setText("");
//                        mImageView_tips_image.setImageDrawable(null);
//                        mTextView_tips_msg.setText("");
//                        mProgressBar_image_quality.setProgress(0);
//                        mButton_start_stop_identify.setText(getString(R.string.btn_stop_identify));
//                        enbleSettingsView(false);
//
//                    } else {
//                        if (mIdentifyTask != null && mIdentifyTask.getStatus() != AsyncTask.Status.FINISHED) {
//                            mIdentifyTask.cancel(false);
//                            mIdentifyTask.waitForDone();
//                            mIdentifyTask = null;
//                        }
//                        isIdentifing = false;
//                        mImageView_fingerprint.setImageBitmap(null);
//                        mTextView_image_quality.setText("");
//                        mButton_start_stop_identify.setText(getString(R.string.btn_start_identify));
//                        enbleSettingsView(true);
//                        handleMsgOneToN(" Stop finish.", Color.BLACK);
//                    }
//                }
//            });
//            mButton_remove_user.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mTrustFingerDevice == null) {
//                        handleMsgOneToN("Device not opened", Color.RED);
//                        return;
//                    }
//                }
//            });
//            mButton_remove_all.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mTrustFingerDevice == null) {
//                        handleMsgOneToN("Device not opened", Color.RED);
//                        return;
//                    }
//                    if (mEnrollFingerID < 0) {
//                        handleMsgOneToN(" Bione Algorithm has not been initialized! error:" + mEnrollFingerID, Color.RED);
//                        return;
//                    }
//                    //todo 2023-05-09 fanghc
//                    mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATA_FINGER_ID_UI, mEnrollFingerID, 0, 0));
////                    if (Bione.getEnrolledCount() > 0) {
////                        Bione.clear();
////                        handleMsgOneToN("Remove all success!", Color.BLACK);
////                        mEnrollFingerID = 0;
////                        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATA_FINGER_ID_UI, mEnrollFingerID, 0, 0));
////                    } else
////                        handleMsgOneToN("No enrolled users!", Color.RED);
//
//                }
//            });
//        }
//        viewCreated = true;
//        mDBHelper = new DBHelper(getActivity(), Config.SAVE_TO_SDCARD);
//
//        return root;
//    }
//
//    private void showPopupWindow() {
//        ListView lv = new ListView(getActivity());
//        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.security_level, R.layout.spinner_list_item);
//        arrayAdapter.setDropDownViewResource(R.layout.spinner_list_item);
//        lv.setAdapter(arrayAdapter);
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String level = parent.getItemAtPosition(position).toString();
//                mTextView_select_security_level.setText(level);
//                saveParameterToPreferences(Config.IDENTIFY_SECURITY_LEVEL, level);
//                popupWindow.dismiss();
//                switch (level) {
//                    case "Level1":
//                        mSecurityLevel = SecurityLevel.Level1;
//                        break;
//                    case "Level2":
//                        mSecurityLevel = SecurityLevel.Level2;
//                        break;
//                    case "Level3":
//                        mSecurityLevel = SecurityLevel.Level3;
//                        break;
//                    case "Level4":
//                        mSecurityLevel = SecurityLevel.Level4;
//                        break;
//                    case "Level5":
//                        mSecurityLevel = SecurityLevel.Level5;
//                        break;
//                }
//            }
//        });
//        popupWindow = new PopupWindow(lv, mTextView_select_security_level.getWidth(), ListView.LayoutParams.WRAP_CONTENT, true);
//        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.bg_corner);
//        popupWindow.setBackgroundDrawable(drawable);
//        popupWindow.setFocusable(true);
//        popupWindow.setOutsideTouchable(true);
//        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                popupWindow.dismiss();
//            }
//        });
//    }
//
//    private boolean checkSettings() {
//        String mImageQualityThreshold = mEditText_image_quality_threshold.getText().toString().trim();
//        if ("".equals(mImageQualityThreshold) || Integer.parseInt(mImageQualityThreshold) < 50 || Integer.parseInt(mImageQualityThreshold) > 100) {
//            mEditText_image_quality_threshold.setText("");
//            mEditText_image_quality_threshold.setHintTextColor(Color.RED);
//            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
//            mEditText_image_quality_threshold.startAnimation(anim);
//            return false;
//        }
//        return true;
//    }
//
//    private boolean checkEnrollIDSettings() {
//        String mFingerID = mEditText_finger_id.getText().toString().trim();
//        if ("".equals(mFingerID)) {
//            mEditText_finger_id.setText("");
//            mEditText_finger_id.setHintTextColor(Color.RED);
//            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
//            mEditText_finger_id.startAnimation(anim);
//            return false;
//        }
//        int id = Integer.parseInt(mFingerID);
//        if (id != mEnrollFingerID) {
//            mEnrollFingerID = Integer.parseInt(mFingerID);
//        }
//
//        return true;
//    }
//
//    private boolean checkVerifyIDSettings() {
//        String mFingerID = mEditText_finger_id.getText().toString().trim();
//        if ("".equals(mFingerID)) {
//            mEditText_finger_id.setText("");
//            mEditText_finger_id.setHintTextColor(Color.RED);
//            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
//            mEditText_finger_id.startAnimation(anim);
//            return false;
//        }
//        int id = Integer.parseInt(mFingerID);
//        if (id != mEnrollFingerID) {
//            mCurrentFingerID = Integer.parseInt(mFingerID);
//        }
//
//        return true;
//    }
//
//    public void forceStop() {
//        if (isIdentifing) {
//            if (mIdentifyTask != null && mIdentifyTask.getStatus() != AsyncTask.Status.FINISHED) {
//                mIdentifyTask.cancel(false);
//                mIdentifyTask.waitForDone();
//                mIdentifyTask = null;
//            }
//            isIdentifing = false;
//            mButton_start_stop_enroll.setText(getString(R.string.btn_start_enroll));
//            mButton_start_stop_verify.setText(getString(R.string.btn_start_verify));
//            mButton_start_stop_identify.setText(getString(R.string.btn_start_identify));
//            enbleSettingsView(true);
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        forceStop();
//    }
//
//    public void resetUI() {
//        if (viewCreated) {
//            mHandler.sendEmptyMessage(MSG_RESET_UI);
//        }
//    }
//
//    private void enbleSettingsView(boolean enable) {
//        mEditText_image_quality_threshold.setEnabled(enable);
//        mTextView_select_security_level.setEnabled(enable);
//    }
//
//    @Override
//    public void setDatas(TrustFingerDevice device) {
//        if (device == null) {
//            Log.i(TAG, "setDatas entry..., device = null");
//        } else {
//            Log.i(TAG, "setDatas entry..., device != null");
//        }
//        if (isAdded()) {
//            Log.i(TAG, "mTrustFingerDevice = device.");
//            mTrustFingerDevice = device;
//            if (device != null) {
//                // todo 2023-05-09 fanghc
////                FP_DB_PATH = this.getCacheDir().getPath()+"/fp.db"; // "/sdcard/fp.db";//
////                int error = Bione.initialize(mTrustFingerDevice, FP_DB_PATH);//Config.DB_PATH);
////                Log.i("Sanny", "Bione init,ret:" + error + ",path:" + FP_DB_PATH);
////                if (error != Bione.RESULT_OK) {
////                    if (error == Bione.NOT_SUPPORTED) {
////                        handleMsgOneToN("Bione initialize fail! Module not support 1:N. ", Color.RED);
////                    } else {
////                        handleMsgOneToN("Bione initialize fail! error: " + error, Color.RED);
////                    }
////                }
////
////                if (viewCreated) {
////                    mEnrollFingerID = Bione.getFreeID();
////                }
//            } else {
//                if (viewCreated) {
//                    forceStop();
//                    enbleSettingsView(true);
//                    resetUI();
//                }
//                // todo 2023-05-09 fanghc
////                Bione.exit();
//            }
//        }
//    }
//
//    private class IdentifyTask extends AsyncTask<Void, Integer, Void> {
//        private boolean mIsDone = false;
//        private TextView textViewQulity;
//        private ProgressBar progressBar;
//        private Bitmap fpImage_bitmap = null;
//        private byte[] fpImage_Raw = null;
//        private byte[] fpImage_bmp = null;
//        private byte[] fpFeatureData = null;
//        private byte[] featureTemplate = null;
//        private int imageQuality;
//        private String msg = null;
//        private VerifyResult result;
//
//        public IdentifyTask(TextView textViewQulity, ProgressBar progressBar) {
//            super();
//            this.textViewQulity = textViewQulity;
//            this.progressBar = progressBar;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            isIdentifing = true;
//            handleMsgOneToN("Capturing", Color.BLACK);
//            if (mApp.isLedEnable()) {
//                ledOnRed();
//            }
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            largestFingerData.clear();
//            callback.setLedEnable(false);
//            callback.setLfdEnable(true);
//            int mImageQualityThrethold = Integer.parseInt(mEditText_image_quality_threshold.getText().toString().trim());
//
//            do {
//                if (isCancelled()) {
//                    break;
//                }
//                if (mTrustFingerDevice == null) {
//                    handleMsgOneToN("Device not opened", Color.RED);
//                    break;
//                }
//                boolean isFakeFinger = false;
//                if (largestFingerData.isIsrRaise()) {
//                    MediaPlayerHelper.payMedia(getContext(), R.raw.please_press_your_finger);
//                    largestFingerData.setIsrRaise(false);
//                }
//                try {
//                    if (mTrustFingerDevice.getLfdLevel() != LfdLevel.OFF) {
//                        isFakeFinger = false;
//                        int[] lfdStatus = new int[1];
//                        lfdStatus[0] = mTrustFingerDevice.getLfdLevel();
//                        fpImage_Raw = mTrustFingerDevice.captureRawDataLfd(lfdStatus);
//                        if (lfdStatus[0] == LfdStatus.FAKE) {
//                            handleMsgOneToN("fake finger", Color.RED);
//                            isFakeFinger = true;
//                            MediaPlayerHelper.payMedia(getContext(), R.raw.is_fake_finger);
//                        } else if (lfdStatus[0] == LfdStatus.UNKNOWN) {
//                            handleMsgOneToN("unknown finger", Color.RED);
//                        } else {
//                            handleMsgOneToN("", Color.RED);
//                        }
//                    } else {
//                        fpImage_Raw = mTrustFingerDevice.captureRawData();
//                    }
//                    if (fpImage_Raw == null) {
//                        imageQuality = 0;
//                        publishProgress(0);
//                        updateFingerprintImage(null);
//                    } else {
//                        if (mTrustFingerDevice == null) {
//                            handleMsgOneToN("Device not opened", Color.RED);
//                            break;
//                        }
//                        fpImage_bmp = mTrustFingerDevice.rawToBmp(fpImage_Raw, mTrustFingerDevice.getImageInfo().getWidth(), mTrustFingerDevice.getImageInfo().getHeight(), mTrustFingerDevice
//                                .getImageInfo().getResolution());
//                        if (fpImage_bmp == null) {
//                            publishProgress(0);
//                            updateFingerprintImage(null);
//                            continue;
//                        }
//                        fpImage_bitmap = BitmapFactory.decodeByteArray(fpImage_bmp, 0, fpImage_bmp.length);
//                        if (mTrustFingerDevice == null) {
//                            handleMsgOneToN("Device not opened", Color.RED);
//                            break;
//                        }
//                        imageQuality = mTrustFingerDevice.rawDataQuality(fpImage_Raw);
//
//                        if (imageQuality >= mImageQualityThrethold && !isFakeFinger) {
//                            //                        try {
//                            if (mTrustFingerDevice == null) {
//                                handleMsgOneToN("Device not opened", Color.RED);
//                                break;
//                            }
//                            publishProgress(imageQuality);
//                            updateFingerprintImage(fpImage_bitmap);
//                            //todo 2023-05-09 fanghc
////                            Result res = Bione.extractFeature(fpImage_Raw, mTrustFingerDevice.getImageInfo());
////                            if (res.error != Bione.RESULT_OK) {
////                                handleMsgOneToN("get feature fail!", Color.RED);
////                                continue;
////                            }
////                            fpFeatureData = (byte[]) res.data;
//
//                            fpFeatureData = mTrustFingerDevice.extractFeature(fpImage_Raw, FingerPosition.Unknown);
//                            if (fpFeatureData != null) {
//                                if (!largestFingerData.isThreshold()) {
//                                    if (mApp.isLedEnable()) {
//                                        ledOnGreen();
//                                    }
//                                    largestFingerData.setThreshold(true);
//                                    MediaPlayerHelper.payMedia(getContext(), R.raw.please_raise_your_finger);
//                                }
//                                if (imageQuality > largestFingerData.getImgQuality()) {
//                                    largestFingerData.update(fpFeatureData, imageQuality, fpImage_bitmap);
//                                }
//                            } else {
//                                handleMsgOneToN("Extract feature failed! ", Color.RED);
//                            }
//                        }
//                    }
//                    if (((!isFakeFinger && imageQuality < 50) || isFakeFinger) && !largestFingerData.isIsrRaise() && largestFingerData.getImgQuality() >= mImageQualityThrethold) {
//                        fpFeatureData = largestFingerData.getFpFeatureData();
//                        updateFingerprintImage(largestFingerData.getFpImage_bitmap());
//                        publishProgress(largestFingerData.getImgQuality());
//                        break;
//                    }
//                } catch (TrustFingerException e) {
//                    handleMsgOneToN("Capture exception: " + e.getType().toString(), Color.RED);
//                    e.printStackTrace();
//                }
//            } while (true);
//            if (fpFeatureData == null) {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mButton_start_stop_enroll.setText(getString(R.string.btn_start_enroll));
//                        mButton_start_stop_verify.setText(getString(R.string.btn_start_verify));
//                        mButton_start_stop_identify.setText(getString(R.string.btn_start_identify));
//                        enbleSettingsView(true);
//                    }
//                });
//                callback.setLedEnable(true);
//                callback.setLfdEnable(true);
//                if (mApp.isLedEnable()) {
//                    ledOff();
//                }
//                mIdentifyTask = null;
//                isIdentifing = false;
//                mIsDone = true;
//                return null;
//            }
//
//            switch (mSecurityLevel) {
//                case Level1:
//                    mIdentifyThreshold = 24;
//                    break;
//                case Level2:
//                    mIdentifyThreshold = 30;
//                    break;
//                case Level3:
//                    mIdentifyThreshold = 36;
//                    break;
//                case Level4:
//                    mIdentifyThreshold = 48;
//                    break;
//                case Level5:
//                    mIdentifyThreshold = 60;
//                    break;
//            }
////            Bione.setThreshold(1, mIdentifyThreshold);
//
//            if (mEnrollOrIdentifyFlag == TASK_ENROLL_FLAG) {
//                try {
//                    int enrollCount = Bione.getEnrolledCount();
//                    startTime = System.currentTimeMillis();
//                    if (Bione.identify(fpFeatureData) >= 0) {
//                        endTime = System.currentTimeMillis();
//                        handleMsgOneToN("fingerprint already exists! ", Color.RED);
//                        mHandler.sendMessage(mHandler.obtainMessage(MSG_ENROLL_FAIL, enrollCount, (int) (endTime - startTime), "fingerprint already exists!\n"));
//                    } else {
//                        Result res = Bione.makeTemplate(fpFeatureData, fpFeatureData, fpFeatureData);
//                        if (res.error != Bione.RESULT_OK) {
//                            handleMsgOneToN("makeTemplate fail! ret:" + res.error, Color.RED);
//                            mHandler.sendMessage(mHandler.obtainMessage(MSG_ENROLL_FAIL, enrollCount, (int) (endTime - startTime), "makeTemplate fail!"));
//                        } else {
//                            featureTemplate = (byte[]) res.data;
//                            startTime = System.currentTimeMillis();
//                            int ret = Bione.enroll(mEnrollFingerID, featureTemplate);
//                            endTime = System.currentTimeMillis();
//                            if (ret != Bione.RESULT_OK) {
//                                handleMsgOneToN("Enroll fail! ret:" + ret, Color.RED);
//                                mHandler.sendMessage(mHandler.obtainMessage(MSG_ENROLL_FAIL, enrollCount, (int) (endTime - startTime), 0));
//                            } else {
//                                handleMsgOneToN("Enroll success!  The enroll ID is: " + mEnrollFingerID, Color.BLACK);
//                                mHandler.sendMessage(mHandler.obtainMessage(MSG_ENROLL_SUCCESS, enrollCount, (int) (endTime - startTime), mEnrollFingerID));
//                                mCurrentFingerID = mEnrollFingerID;
//                                mEnrollFingerID = Bione.getFreeID();
//                                mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATA_FINGER_ID_UI, mEnrollFingerID, 0, 0));
//                            }
//                        }
//                    }
//                } catch (TrustFingerException e) {
//                    handleMsgOneToN("Enroll exception: " + e.getType().toString(), Color.RED);
//                    e.printStackTrace();
//                    if (mApp.isLedEnable()) {
//                        ledOff();
//                    }
//                }
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mButton_start_stop_enroll.setText(getString(R.string.btn_start_enroll));
//                        enbleSettingsView(true);
//                    }
//                });
//                mEnrollOrIdentifyFlag = 0;
//                mIdentifyTask = null;
//                isIdentifing = false;
//                mIsDone = true;
//                return null;
//            } else if (mEnrollOrIdentifyFlag == TASK_VERIFY_FLAG) {
//                try {
//                    int enrollCount = Bione.getEnrolledCount();
//                    startTime = System.currentTimeMillis();
//                    Result res = Bione.verify(mCurrentFingerID, fpFeatureData, SecurityLevel.Level1);
//                    endTime = System.currentTimeMillis();
////                    Log.i("Sanny","mCurrentFingerID:"+mCurrentFingerID+",res.error:"+res.error);
//
//                    if (res.error != Bione.RESULT_OK) {
//                        handleMsgOneToN("verify fail! error:" + res.error, Color.RED);
//                        mHandler.sendMessage(mHandler.obtainMessage(MSG_VERIFY_FAIL, enrollCount, (int) (endTime - startTime), 0));
//                    } else {
//                        if ((Boolean) res.data) {
//                            handleMsgOneToN("Fingerprint match! The similarity is: " + res.score, Color.BLACK);
//                            mHandler.sendMessage(mHandler.obtainMessage(MSG_VERIFY_SUCCESS, enrollCount, (int) (endTime - startTime), mCurrentFingerID));
//                        } else {
//                            handleMsgOneToN("Fingerprint not match! The similarity is: " + res.score, Color.RED);
//                            mHandler.sendMessage(mHandler.obtainMessage(MSG_VERIFY_FAIL, enrollCount, (int) (endTime - startTime), mCurrentFingerID));
//                        }
//                    }
//                } catch (TrustFingerException e) {
//                    handleMsgOneToN("verify exception: " + e.getType().toString(), Color.RED);
//                    e.printStackTrace();
//                    if (mApp.isLedEnable()) {
//                        ledOff();
//                    }
//                }
//
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mButton_start_stop_verify.setText(getString(R.string.btn_start_verify));
//                        enbleSettingsView(true);
//                    }
//                });
//                mEnrollOrIdentifyFlag = 0;
//                mIdentifyTask = null;
//                isIdentifing = false;
//                mIsDone = true;
//                return null;
//            } else if (mEnrollOrIdentifyFlag == TASK_IDENTIFY_FLAG) {
//                try {
//                    int id = -1;
//                    int enrollCount = Bione.getEnrolledCount();
//                    startTime = System.currentTimeMillis();
//                    id = Bione.identify(fpFeatureData);
//                    endTime = System.currentTimeMillis();
//                    if (id < 0) {
//                        if (id == Bione.NO_RESULT) {
//                            handleMsgOneToN("Identify fail! No finger match.", Color.RED);
//                        } else {
//                            handleMsgOneToN("Identify fail! ret:" + id, Color.RED);
//                        }
//                        mHandler.sendMessage(mHandler.obtainMessage(MSG_IDENTIFY_FAIL, enrollCount, (int) (endTime - startTime), 0));
//                    } else {
//                        handleMsgOneToN("Identify success! The match ID is: " + id, Color.BLACK);
//                        mHandler.sendMessage(mHandler.obtainMessage(MSG_IDENTIFY_SUCCESS, enrollCount, (int) (endTime - startTime), id));
//                    }
//                } catch (TrustFingerException e) {
//                    handleMsgOneToN("Identify exception: " + e.getType().toString(), Color.RED);
//                    e.printStackTrace();
//                    if (mApp.isLedEnable()) {
//                        ledOff();
//                    }
//                }
//
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mButton_start_stop_identify.setText(getString(R.string.btn_start_identify));
//                        enbleSettingsView(true);
//                    }
//                });
//                mEnrollOrIdentifyFlag = 0;
//                mIdentifyTask = null;
//                isIdentifing = false;
//                mIsDone = true;
//                return null;
//            }
//            handleMsgOneToN("Identify completed", Color.BLACK);
//            mEnrollOrIdentifyFlag = 0;
//            mIdentifyTask = null;
//            isIdentifing = false;
//            mIsDone = true;
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            int value = values[0];
//            if (mTrustFingerDevice == null) {
//                textViewQulity.setText("");
//                progressBar.setProgress(0);
//                return;
//            }
//            textViewQulity.setText("" + value);
//            progressBar.setProgress(value);
//            super.onProgressUpdate(values);
//        }
//
//        public void waitForDone() {
//            while (!mIsDone) {
//                try {
//                    Thread.sleep(50);
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                }
//            }
//        }
//    }
//
//    private void updateFingerprintImage(Bitmap fpImage) {
//        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_IMAGE, fpImage));
//    }
//
//    @SuppressLint("HandlerLeak")
//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case MSG_RESET_UI:
//                    mImageView_fingerprint.setImageBitmap(null);
//                    mProgressBar_image_quality.setProgress(0);
//                    mTextView_image_quality.setText("");
//                    mImageView_tips_image.setImageDrawable(null);
//                    mTextView_tips_msg.setText("");
//                    mUserList.clear();
//                    myOneToNListAdapter.notifyDataSetChanged();
//                    break;
//                case MSG_UPDATE_IMAGE:
//                    if (mTrustFingerDevice == null) {
//                        mImageView_fingerprint.setImageBitmap(null);
//                        break;
//                    }
//                    Bitmap bmp_fpImg = (Bitmap) msg.obj;
//                    if (bmp_fpImg == null) {
//                        mImageView_fingerprint.setImageBitmap(null);
//                    } else {
//                        mImageView_fingerprint.setImageBitmap(bmp_fpImg);
//                    }
//                    break;
//                case MSG_UPDATE_USER_LIST:
//                    List<User> users = (List<User>) msg.obj;
//                    mUserList.clear();
//                    mUserList.addAll(users);
//                    Log.e(TAG, mUserList.toString());
//                    myOneToNListAdapter.notifyDataSetChanged();
//                    break;
//                case MSG_IDENTIFY_SUCCESS: {
//                    int totalNum = msg.arg1;
//                    int time = msg.arg2;
//                    int num = (int) msg.obj;
//                    String beforeNum = "Identify succeed!\n";
//                    String sNum = "" + num;
//                    String afterNum = " is recognized finger ID\n";
//                    String sTime = "" + time;
//                    String afterTime = " ms took\n";
//                    String sTotalNum = "" + totalNum;
//                    String afterTotalNum = " user" + (totalNum > 1 ? "s" : "") + " exist in DB";
//                    SpannableStringBuilder builder = new SpannableStringBuilder(beforeNum + sNum + afterNum + sTime + afterTime + sTotalNum + afterTotalNum);
//                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1D9F9A")),
//                            beforeNum.length(),
//                            (beforeNum.length() + sNum.length()),
//                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
//                            beforeNum.length(),
//                            (beforeNum.length() + sNum.length()),
//                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new AbsoluteSizeSpan(16, true),
//                            beforeNum.length(),
//                            (beforeNum.length() + sNum.length()),
//                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1D9F9A")),
//                            (beforeNum.length() + sNum.length() + afterNum.length()),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length()),
//                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
//                            (beforeNum.length() + sNum.length() + afterNum.length()),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length()),
//                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new AbsoluteSizeSpan(16, true),
//                            (beforeNum.length() + sNum.length() + afterNum.length()),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length()),
//                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1D9F9A")),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length() + afterTime.length()),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length() + afterTime.length() + sTotalNum.length()),
//                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length() + afterTime.length()),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length() + afterTime.length() + sTotalNum.length()),
//                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new AbsoluteSizeSpan(16, true),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length() + afterTime.length()),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length() + afterTime.length() + sTotalNum.length()),
//                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//                    mImageView_tips_image.setImageDrawable(getResources().getDrawable(R.drawable.success));
//                    mTextView_tips_msg.setText(builder);
//                    if (mApp.isLedEnable()) {
//                        ledOff();
//                    }
//                    break;
//                }
//                case MSG_IDENTIFY_FAIL: {
//                    int totalNum = msg.arg1;
//                    int time = msg.arg2;
//                    String beforeTime = "Identify failed!\n";
//                    String afterNum = "No finger recognized";
//                    String sTime = "" + time;
//                    String afterTime = " ms took\n";
//                    String sTotalNum = "" + totalNum;
//                    String afterTotalNum = " user" + (totalNum > 1 ? "s" : "") + " exist in DB\n";
//                    SpannableStringBuilder builder = new SpannableStringBuilder(beforeTime + sTime + afterTime + sTotalNum + afterTotalNum + afterNum);
//                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1D9F9A")),
//                            beforeTime.length(),
//                            (beforeTime.length() + sTime.length()),
//                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
//                            beforeTime.length(),
//                            (beforeTime.length() + sTime.length()),
//                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new AbsoluteSizeSpan(16, true),
//                            beforeTime.length(),
//                            (beforeTime.length() + sTime.length()),
//                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1D9F9A")),
//                            (beforeTime.length() + sTime.length() + afterTime.length()),
//                            (beforeTime.length() + sTime.length() + afterTime.length() + sTotalNum.length()),
//                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
//                            (beforeTime.length() + sTime.length() + afterTime.length()),
//                            (beforeTime.length() + sTime.length() + afterTime.length() + sTotalNum.length()),
//                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new AbsoluteSizeSpan(16, true),
//                            (beforeTime.length() + sTime.length() + afterTime.length()),
//                            (beforeTime.length() + sTime.length() + afterTime.length() + sTotalNum.length()),
//                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//                    mImageView_tips_image.setImageDrawable(getResources().getDrawable(R.drawable.fail));
//                    mTextView_tips_msg.setText(builder);
//                    if (mApp.isLedEnable()) {
//                        ledOff();
//                    }
//                    break;
//                }
//                case MSG_ENROLL_SUCCESS: {
//                    int totalNum = msg.arg1;
//                    int time = msg.arg2;
//                    int num = (int) msg.obj;
//                    String beforeNum = "Enroll succeed!\n";
//                    String sNum = "" + num;
//                    String afterNum = " is enroll Finger ID\n";
//                    String sTime = "" + time;
//                    String afterTime = " ms took\n";
//                    String sTotalNum = "" + totalNum;
//                    String afterTotalNum = " user" + (totalNum > 1 ? "s" : "") + " exist in DB";
//                    SpannableStringBuilder builder = new SpannableStringBuilder(beforeNum + sNum + afterNum + sTime + afterTime + sTotalNum + afterTotalNum);
//                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1D9F9A")),
//                            beforeNum.length(),
//                            (beforeNum.length() + sNum.length()),
//                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
//                            beforeNum.length(),
//                            (beforeNum.length() + sNum.length()),
//                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new AbsoluteSizeSpan(16, true),
//                            beforeNum.length(),
//                            (beforeNum.length() + sNum.length()),
//                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1D9F9A")),
//                            (beforeNum.length() + sNum.length() + afterNum.length()),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length()),
//                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
//                            (beforeNum.length() + sNum.length() + afterNum.length()),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length()),
//                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new AbsoluteSizeSpan(16, true),
//                            (beforeNum.length() + sNum.length() + afterNum.length()),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length()),
//                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1D9F9A")),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length() + afterTime.length()),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length() + afterTime.length() + sTotalNum.length()),
//                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length() + afterTime.length()),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length() + afterTime.length() + sTotalNum.length()),
//                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new AbsoluteSizeSpan(16, true),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length() + afterTime.length()),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length() + afterTime.length() + sTotalNum.length()),
//                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//                    mImageView_tips_image.setImageDrawable(getResources().getDrawable(R.drawable.success));
//                    mTextView_tips_msg.setText(builder);
//                    if (mApp.isLedEnable()) {
//                        ledOff();
//                    }
//                    break;
//                }
//                case MSG_ENROLL_FAIL: {
//                    int totalNum = msg.arg1;
//                    int time = msg.arg2;
//                    String s = (String) msg.obj;
//                    String beforeTime = "Enroll failed!\n" + s;
//                    String sTime = "" + time;
//                    String afterTime = " ms took\n";
//                    String sTotalNum = "" + totalNum;
//                    String afterTotalNum = " user" + (totalNum > 1 ? "s" : "") + " exist in DB";
//                    SpannableStringBuilder builder = new SpannableStringBuilder(beforeTime + sTime + afterTime + sTotalNum + afterTotalNum);
//                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1D9F9A")),
//                            beforeTime.length(),
//                            (beforeTime.length() + sTime.length()),
//                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
//                            beforeTime.length(),
//                            (beforeTime.length() + sTime.length()),
//                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new AbsoluteSizeSpan(16, true),
//                            beforeTime.length(),
//                            (beforeTime.length() + sTime.length()),
//                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1D9F9A")),
//                            (beforeTime.length() + sTime.length() + afterTime.length()),
//                            (beforeTime.length() + sTime.length() + afterTime.length() + sTotalNum.length()),
//                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
//                            (beforeTime.length() + sTime.length() + afterTime.length()),
//                            (beforeTime.length() + sTime.length() + afterTime.length() + sTotalNum.length()),
//                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new AbsoluteSizeSpan(16, true),
//                            (beforeTime.length() + sTime.length() + afterTime.length()),
//                            (beforeTime.length() + sTime.length() + afterTime.length() + sTotalNum.length()),
//                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//                    mImageView_tips_image.setImageDrawable(getResources().getDrawable(R.drawable.fail));
//                    mTextView_tips_msg.setText(builder);
//                    if (mApp.isLedEnable()) {
//                        ledOff();
//                    }
//                    break;
//                }
//                case MSG_UPDATA_FINGER_ID_UI: {
//                    String id = String.valueOf(msg.arg1);
//                    mEditText_finger_id.setText(id);
//                    break;
//                }
//                case MSG_VERIFY_SUCCESS: {
//                    int totalNum = msg.arg1;
//                    int time = msg.arg2;
//                    int num = (int) msg.obj;
//                    String beforeNum = "Verify succeed!\n";
//                    String sNum = "" + num;
//                    String afterNum = " is recognized finger ID\n";
//                    String sTime = "" + time;
//                    String afterTime = " ms took\n";
//                    String sTotalNum = "" + totalNum;
//                    String afterTotalNum = " user" + (totalNum > 1 ? "s" : "") + " exist in DB";
//                    SpannableStringBuilder builder = new SpannableStringBuilder(beforeNum + sNum + afterNum + sTime + afterTime + sTotalNum + afterTotalNum);
//                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1D9F9A")),
//                            beforeNum.length(),
//                            (beforeNum.length() + sNum.length()),
//                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
//                            beforeNum.length(),
//                            (beforeNum.length() + sNum.length()),
//                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new AbsoluteSizeSpan(16, true),
//                            beforeNum.length(),
//                            (beforeNum.length() + sNum.length()),
//                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1D9F9A")),
//                            (beforeNum.length() + sNum.length() + afterNum.length()),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length()),
//                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
//                            (beforeNum.length() + sNum.length() + afterNum.length()),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length()),
//                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new AbsoluteSizeSpan(16, true),
//                            (beforeNum.length() + sNum.length() + afterNum.length()),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length()),
//                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1D9F9A")),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length() + afterTime.length()),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length() + afterTime.length() + sTotalNum.length()),
//                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length() + afterTime.length()),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length() + afterTime.length() + sTotalNum.length()),
//                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new AbsoluteSizeSpan(16, true),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length() + afterTime.length()),
//                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length() + afterTime.length() + sTotalNum.length()),
//                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//                    mImageView_tips_image.setImageDrawable(getResources().getDrawable(R.drawable.success));
//                    mTextView_tips_msg.setText(builder);
//                    if (mApp.isLedEnable()) {
//                        ledOff();
//                    }
//                    break;
//                }
//                case MSG_VERIFY_FAIL: {
//                    int totalNum = msg.arg1;
//                    int time = msg.arg2;
//                    String beforeTime = "Verify failed!\n";
//                    String afterNum = "No finger recognized";
//                    String sTime = "" + time;
//                    String afterTime = " ms took\n";
//                    String sTotalNum = "" + totalNum;
//                    String afterTotalNum = " user" + (totalNum > 1 ? "s" : "") + " exist in DB\n";
//                    SpannableStringBuilder builder = new SpannableStringBuilder(beforeTime + sTime + afterTime + sTotalNum + afterTotalNum + afterNum);
//                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1D9F9A")),
//                            beforeTime.length(),
//                            (beforeTime.length() + sTime.length()),
//                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
//                            beforeTime.length(),
//                            (beforeTime.length() + sTime.length()),
//                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new AbsoluteSizeSpan(16, true),
//                            beforeTime.length(),
//                            (beforeTime.length() + sTime.length()),
//                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1D9F9A")),
//                            (beforeTime.length() + sTime.length() + afterTime.length()),
//                            (beforeTime.length() + sTime.length() + afterTime.length() + sTotalNum.length()),
//                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
//                            (beforeTime.length() + sTime.length() + afterTime.length()),
//                            (beforeTime.length() + sTime.length() + afterTime.length() + sTotalNum.length()),
//                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    builder.setSpan(new AbsoluteSizeSpan(16, true),
//                            (beforeTime.length() + sTime.length() + afterTime.length()),
//                            (beforeTime.length() + sTime.length() + afterTime.length() + sTotalNum.length()),
//                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//                    mImageView_tips_image.setImageDrawable(getResources().getDrawable(R.drawable.fail));
//                    mTextView_tips_msg.setText(builder);
//                    if (mApp.isLedEnable()) {
//                        ledOff();
//                    }
//                    break;
//                }
//
//            }
//        }
//    };
//
//}
