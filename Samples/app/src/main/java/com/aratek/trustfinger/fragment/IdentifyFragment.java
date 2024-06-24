package com.aratek.trustfinger.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aratek.trustfinger.Config;
import com.aratek.trustfinger.R;
import com.aratek.trustfinger.adapter.MyRankListAdapter;
import com.aratek.trustfinger.bean.FingerData;
import com.aratek.trustfinger.bean.LargestFingerData;
import com.aratek.trustfinger.bean.User;
import com.aratek.trustfinger.interfaces.ViewStatusCallback;
import com.aratek.trustfinger.sdk.FingerPosition;
import com.aratek.trustfinger.sdk.LfdLevel;
import com.aratek.trustfinger.sdk.LfdStatus;
import com.aratek.trustfinger.sdk.SecurityLevel;
import com.aratek.trustfinger.sdk.TrustFingerDevice;
import com.aratek.trustfinger.sdk.TrustFingerException;
import com.aratek.trustfinger.sdk.VerifyResult;
import com.aratek.trustfinger.utils.DBHelper;
import com.aratek.trustfinger.utils.MediaPlayerHelper;
import com.aratek.trustfinger.widget.MyListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class IdentifyFragment extends BaseFragment {
    private static final String TAG = "IdentifyFragment";
    private static final int MSG_RESET_UI = 0;
    private static final int MSG_UPDATE_IMAGE = 1;
    private static final int MSG_IDENTIFY_SUCCESS = 2;
    private static final int MSG_IDENTIFY_FAIL = 3;
    private static final int MSG_UPDATE_USER_LIST = 4;

    private ScrollView sv;
    private ImageView mImageView_fingerprint;
    private TextView mTextView_image_quality;
    private ProgressBar mProgressBar_image_quality;
    private EditText mEditText_image_quality_threshold;
    private TextView mTextView_select_security_level;
    private PopupWindow popupWindow;
    private ImageView mImageView_tips_image;
    private TextView mTextView_tips_msg;
    private Button mButton_start_stop_identify;
    private MyListView mListView_users;

    private List<User> mUserList = new ArrayList<User>();
    private MyRankListAdapter myRankListAdapter;
    private boolean isIdentifing = false;
    private IdentifyTask mIdentifyTask;
    private DBHelper mDBHelper;
    private SecurityLevel mSecurityLevel = SecurityLevel.Level4;
    private int mIdentifyThreshold = 48;
    private View root;
    private long startTime, endTime;
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
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_identify, container, false);
            sv = (ScrollView) root.findViewById(R.id.sv_content);
            mImageView_fingerprint = (ImageView) root.findViewById(R.id.iv_fingerprint);
            mTextView_image_quality = (TextView) root.findViewById(R.id.tv_image_quality);
            mProgressBar_image_quality = (ProgressBar) root.findViewById(R.id.proBar_image_quality);
            mEditText_image_quality_threshold = (EditText) root.findViewById(R.id.et_image_quality_threshold);
            mTextView_select_security_level = (TextView) root.findViewById(R.id.tv_select_security_level);
            mImageView_tips_image = (ImageView) root.findViewById(R.id.iv_tips_image);
            mTextView_tips_msg = (TextView) root.findViewById(R.id.tv_tips_msg);
            mButton_start_stop_identify = (Button) root.findViewById(R.id.btn_start_stop_identify);

            mEditText_image_quality_threshold.setText((String) getParameterFromPreferences(Config.IDENTIFY_IMAGE_QUALITY_THRESHOLD, null));
            String level = (String) getParameterFromPreferences(Config.IDENTIFY_SECURITY_LEVEL, null);
            mTextView_select_security_level.setText(level);
            switch (level) {
                case "Level1":
                    mSecurityLevel = SecurityLevel.Level1;
                    break;
                case "Level2":
                    mSecurityLevel = SecurityLevel.Level2;
                    break;
                case "Level3":
                    mSecurityLevel = SecurityLevel.Level3;
                    break;
                case "Level4":
                    mSecurityLevel = SecurityLevel.Level4;
                    break;
                case "Level5":
                    mSecurityLevel = SecurityLevel.Level5;
                    break;
            }
            mListView_users = (MyListView) root.findViewById(R.id.lv_user_list);
            mListView_users.setEmptyView(root.findViewById(R.id.tv_no_datas));
            mListView_users.setmaxHeight(400);
            myRankListAdapter = new MyRankListAdapter(getActivity(), mUserList);
            mListView_users.setAdapter(myRankListAdapter);
            mListView_users.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        sv.requestDisallowInterceptTouchEvent(false);
                    } else {
                        sv.requestDisallowInterceptTouchEvent(true);
                    }
                    return false;
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
                    saveParameterToPreferences(Config.IDENTIFY_IMAGE_QUALITY_THRESHOLD, s.toString());
                }
            });
            mTextView_select_security_level.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupWindow();
                    if (popupWindow != null && !popupWindow.isShowing()) {
                        popupWindow.showAsDropDown(mTextView_select_security_level, 0, 10);
                    }
                }
            });

            mButton_start_stop_identify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTrustFingerDevice == null) {
                        handleMsg("Device not opened", Color.RED);
                        return;
                    }
                    if (!checkSettings()) {
                        return;
                    }
                    if (mDBHelper.getUserList().isEmpty()) {
                        handleMsg("No enrolled users!", Color.RED);
                        return;
                    }

                    if (!isIdentifing) {
                        //                        isIdentifing = true;
                        resetUI();
                        mIdentifyTask = new IdentifyTask(mTextView_image_quality, mProgressBar_image_quality);
                        mIdentifyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        mImageView_fingerprint.setImageBitmap(null);
                        mTextView_image_quality.setText("");
                        mImageView_tips_image.setImageDrawable(null);
                        mTextView_tips_msg.setText("");
                        mProgressBar_image_quality.setProgress(0);
                        mButton_start_stop_identify.setText(getString(R.string.btn_stop_identify));
                        enbleSettingsView(false);

                    } else {
                        if (mIdentifyTask != null && mIdentifyTask.getStatus() != AsyncTask.Status.FINISHED) {
                            mIdentifyTask.cancel(false);
                            mIdentifyTask.waitForDone();
                            mIdentifyTask = null;
                        }
                        isIdentifing = false;
                        mImageView_fingerprint.setImageBitmap(null);
                        mTextView_image_quality.setText("");
                        mButton_start_stop_identify.setText(getString(R.string.btn_start_identify));
                        enbleSettingsView(true);
                    }
                }
            });
        }
        viewCreated = true;
        mDBHelper = new DBHelper(getActivity(), Config.SAVE_TO_SDCARD);
        return root;
    }

    private void showPopupWindow() {
        ListView lv = new ListView(getActivity());
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.security_level, R.layout.spinner_list_item);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_list_item);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String level = parent.getItemAtPosition(position).toString();
                mTextView_select_security_level.setText(level);
                saveParameterToPreferences(Config.IDENTIFY_SECURITY_LEVEL, level);
                popupWindow.dismiss();
                switch (level) {
                    case "Level1":
                        mSecurityLevel = SecurityLevel.Level1;
                        break;
                    case "Level2":
                        mSecurityLevel = SecurityLevel.Level2;
                        break;
                    case "Level3":
                        mSecurityLevel = SecurityLevel.Level3;
                        break;
                    case "Level4":
                        mSecurityLevel = SecurityLevel.Level4;
                        break;
                    case "Level5":
                        mSecurityLevel = SecurityLevel.Level5;
                        break;
                }
            }
        });
        popupWindow = new PopupWindow(lv, mTextView_select_security_level.getWidth(), ListView.LayoutParams.WRAP_CONTENT, true);
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

    private boolean checkSettings() {
        String mImageQualityThreshold = mEditText_image_quality_threshold.getText().toString().trim();
        if ("".equals(mImageQualityThreshold) || Integer.parseInt(mImageQualityThreshold) <= 0 || Integer.parseInt(mImageQualityThreshold) > 100) {
            mEditText_image_quality_threshold.setText("");
            mEditText_image_quality_threshold.setHintTextColor(Color.RED);
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
            mEditText_image_quality_threshold.startAnimation(anim);
            return false;
        }
        return true;
    }

    public void forceStop() {
        if (isIdentifing) {
            if (mIdentifyTask != null && mIdentifyTask.getStatus() != AsyncTask.Status.FINISHED) {
                mIdentifyTask.cancel(false);
                mIdentifyTask.waitForDone();
                mIdentifyTask = null;
            }
            isIdentifing = false;
            mButton_start_stop_identify.setText(getString(R.string.btn_start_identify));
            enbleSettingsView(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        forceStop();
    }

    public void resetUI() {
        if (viewCreated) {
            mHandler.sendEmptyMessage(MSG_RESET_UI);
        }
    }

    private void enbleSettingsView(boolean enable) {
        mEditText_image_quality_threshold.setEnabled(enable);
        mTextView_select_security_level.setEnabled(enable);
    }

    private void loadIdentifyUsers(List<User> users) {
        if (mHandler != null) {
            mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_USER_LIST, users));
        }
    }

    @Override
    public void setDatas(TrustFingerDevice device) {
        if (isAdded()) {
            mTrustFingerDevice = device;
            if (device != null) {
                if (viewCreated) {

                }
            } else {
                if (viewCreated) {
                    forceStop();
                    enbleSettingsView(true);
                    resetUI();
                }
            }
        }
    }

    private class IdentifyTask extends AsyncTask<Void, Integer, Void> {
        private boolean mIsDone = false;
        private TextView textViewQulity;
        private ProgressBar progressBar;
        private Bitmap fpImage_bitmap = null;
        private byte[] fpImage_Raw = null;
        private byte[] fpImage_bmp = null;
        private byte[] fpFeatureData = null;
        private int imageQuality;
        private String msg = null;
        private VerifyResult result;

        public IdentifyTask(TextView textViewQulity, ProgressBar progressBar) {
            super();
            this.textViewQulity = textViewQulity;
            this.progressBar = progressBar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isIdentifing = true;
            handleMsg("Capturing", Color.BLACK);
            if (mApp.isLedEnable()) {
                ledOnRed();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            largestFingerData.clear();
            callback.setLedEnable(false);
            callback.setLfdEnable(true);
            int mImageQualityThrethold = Integer.parseInt(mEditText_image_quality_threshold.getText().toString().trim());
            do {
                handleMsg("", Color.RED);
                if (isCancelled()) {
                    break;
                }
                if (mTrustFingerDevice == null || !mTrustFingerDevice.isOpened()) {
                    handleMsg("Device not opened", Color.RED);
                    break;
                }
                boolean isFakeFinger = false;
                if (largestFingerData.isIsrRaise()) {
                    MediaPlayerHelper.payMedia(getContext(), R.raw.please_press_your_finger);
                    largestFingerData.setIsrRaise(false);
                }
                try {
                    int[] lfdStatus = new int[1];
                    if (mTrustFingerDevice.getLfdLevel() != LfdLevel.OFF) {
                        lfdStatus[0] = mTrustFingerDevice.getLfdLevel();
                        fpImage_Raw = mTrustFingerDevice.captureRawDataLfd(lfdStatus);
                        if (lfdStatus[0] == LfdStatus.FAKE) {
                            handleMsg("fake finger", Color.RED);
                            isFakeFinger = true;
                            MediaPlayerHelper.payMedia(getContext(), R.raw.is_fake_finger);
                            break;
                        }
                    } else {
                        fpImage_Raw = mTrustFingerDevice.captureRawData();
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
                        publishProgress(0);
                        updateFingerprintImage(null);
//                        handleMsg("bad image", Color.RED);
                        continue;
                    }
                    fpImage_bitmap = BitmapFactory.decodeByteArray(fpImage_bmp, 0, fpImage_bmp.length);
                    publishProgress(imageQuality);
                    updateFingerprintImage(fpImage_bitmap);
                    if (imageQuality >= mImageQualityThrethold) {
                        if (mTrustFingerDevice == null) {
                            handleMsg("Device not opened", Color.RED);
                            break;
                        }
                        fpFeatureData = mTrustFingerDevice.extractFeature(fpImage_Raw, FingerPosition.Unknown);
                        if (fpFeatureData != null) {
                            if (!largestFingerData.isThreshold()) {
                                if (mApp.isLedEnable()) {
                                    ledOnGreen();
                                }
                                largestFingerData.setThreshold(true);
                                MediaPlayerHelper.payMedia(getContext(), R.raw.please_raise_your_finger);
                            }
                            if (imageQuality > largestFingerData.getImgQuality()) {
                                largestFingerData.update(fpFeatureData, imageQuality, fpImage_bitmap);
                            }
                        } else {
                            handleMsg("Extract feature failed! ", Color.RED);
                            continue;
                        }
                    }

                    if (!largestFingerData.isIsrRaise() && largestFingerData.getImgQuality() >= mImageQualityThrethold) {
                        fpFeatureData = largestFingerData.getFpFeatureData();
                        updateFingerprintImage(largestFingerData.getFpImage_bitmap());
                        publishProgress(largestFingerData.getImgQuality());
                        break;
                    }
                } catch (TrustFingerException e) {
                    handleMsg("Capture exception: " + e.getType().toString(), Color.RED);
                    e.printStackTrace();
                }
            } while (true);
            if (fpFeatureData == null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mButton_start_stop_identify.setText(getString(R.string.btn_start_identify));
                        enbleSettingsView(true);
                    }
                });
                callback.setLedEnable(true);
                callback.setLfdEnable(true);
                if (mApp.isLedEnable()) {
                    ledOff();
                }
                mIdentifyTask = null;
                isIdentifing = false;
                mIsDone = true;
                return null;
            }
            try {
                List<User> userList = mDBHelper.getUserList();
                if (mTrustFingerDevice == null) {
                    handleMsg("No enrolled users", Color.RED);
                    if (mApp.isLedEnable()) {
                        ledOff();
                    }
                    mIdentifyTask = null;
                    isIdentifing = false;
                    mIsDone = true;
                    return null;
                }
                byte[] template = null;
                FingerData fingerData;
                String fingerPosition = null;
                handleMsg("Identifing", Color.BLACK);
                startTime = System.currentTimeMillis();
                for (User user : userList) {
                    fingerData = user.getFingerData();
                    if (fingerData == null) {
                        handleMsg("identify fail， no enrolled templates!", Color.RED);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mButton_start_stop_identify.setText(getString(R.string.btn_start_identify));
                                enbleSettingsView(true);
                            }
                        });
                        if (mApp.isLedEnable()) {
                            ledOff();
                        }
                        mIdentifyTask = null;
                        isIdentifing = false;
                        mIsDone = true;
                        return null;
                    }
                    for (String position : fingerData.getFingerPositions()) {
                        fingerPosition = position;
                        switch (position) {
                            case "LeftLittleFinger":
                                template = fingerData.getLeft_little_template();
                                break;
                            case "LeftRingFinger":
                                template = fingerData.getLeft_ring_template();
                                break;
                            case "LeftMiddleFinger":
                                template = fingerData.getLeft_middle_template();
                                break;
                            case "LeftIndexFinger":
                                template = fingerData.getLeft_index_template();
                                break;
                            case "LeftThumb":
                                template = fingerData.getLeft_thumb_template();
                                break;
                            case "RightLittleFinger":
                                template = fingerData.getRight_little_template();
                                break;
                            case "RightRingFinger":
                                template = fingerData.getRight_ring_template();
                                break;
                            case "RightMiddleFinger":
                                template = fingerData.getRight_middle_template();
                                break;
                            case "RightIndexFinger":
                                template = fingerData.getRight_index_template();
                                break;
                            case "RightThumb":
                                template = fingerData.getRight_thumb_template();
                                break;

                        }
                        if (template != null && fingerPosition != null) {
                            if (mTrustFingerDevice == null) {
                                handleMsg("Device not opened", Color.RED);
                                mIdentifyTask = null;
                                isIdentifing = false;
                                mIsDone = true;
                                return null;
                            }
                            result = mTrustFingerDevice.verify(mSecurityLevel, template, fpFeatureData);
                            if (result.error == 0) {
                                user.setSimilarity(result.similarity);
                            } else {
                                user.setSimilarity(-1);
                            }
                        } else {
                            user.setSimilarity(-1);
                            Log.e(TAG, "verify fail, no enrolled templates");
                        }
                    }
                }
                List<User> mUserList = new ArrayList<>();
                switch (mSecurityLevel) {
                    case Level1:
                        mIdentifyThreshold = 24;
                        break;
                    case Level2:
                        mIdentifyThreshold = 30;
                        break;
                    case Level3:
                        mIdentifyThreshold = 36;
                        break;
                    case Level4:
                        mIdentifyThreshold = 48;
                        break;
                    case Level5:
                        mIdentifyThreshold = 60;
                        break;
                }
                endTime = System.currentTimeMillis();
                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).getSimilarity() >= mIdentifyThreshold) {
                        mUserList.add(userList.get(i));
                    }
                }
                if (mUserList.isEmpty()) {
                    handleMsg("Identify failed", Color.BLACK);
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_IDENTIFY_FAIL, userList.size(), (int) (endTime - startTime)));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mButton_start_stop_identify.setText(getString(R.string.btn_start_identify));
                            enbleSettingsView(true);
                        }
                    });
                    mIdentifyTask = null;
                    isIdentifing = false;
                    mIsDone = true;
                    return null;

                }
                Collections.sort(mUserList);
                for (int i = 0; i < mUserList.size(); i++) {
                    mUserList.get(i).setRank(i + 1);
                }
                mHandler.sendMessage(mHandler.obtainMessage(MSG_IDENTIFY_SUCCESS, userList.size(), (int) (endTime - startTime), mUserList.size()));
                loadIdentifyUsers(mUserList);
                mIdentifyTask = null;
                isIdentifing = false;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mButton_start_stop_identify.setText(getString(R.string.btn_start_identify));
                        enbleSettingsView(true);
                    }
                });
            } catch (TrustFingerException e) {
                handleMsg("Identify exception: " + e.getType().toString(), Color.RED);
                e.printStackTrace();
                if (mApp.isLedEnable()) {
                    ledOff();
                }
            }
            handleMsg("Identify completed", Color.BLACK);
            mIdentifyTask = null;
            isIdentifing = false;
            mIsDone = true;
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

    private void updateFingerprintImage(Bitmap fpImage) {
        if (mHandler != null) {
            mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_IMAGE, fpImage));
        }
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
                    mImageView_fingerprint.setImageBitmap(null);
                    mProgressBar_image_quality.setProgress(0);
                    mTextView_image_quality.setText("");
                    mImageView_tips_image.setImageDrawable(null);
                    mTextView_tips_msg.setText("");
                    mUserList.clear();
                    myRankListAdapter.notifyDataSetChanged();
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
                case MSG_UPDATE_USER_LIST:
                    List<User> users = (List<User>) msg.obj;
                    mUserList.clear();
                    mUserList.addAll(users);
                    Log.e(TAG, mUserList.toString());
                    myRankListAdapter.notifyDataSetChanged();
                    break;
                case MSG_IDENTIFY_SUCCESS: {
                    int totalNum = msg.arg1;
                    int time = msg.arg2;
                    int num = (int) msg.obj;
                    String beforeNum = "Identify succeed!\n";
                    String sNum = "" + num;
                    String afterNum = " user" + (num > 1 ? "s" : "") + " recognized\n";
                    String sTime = "" + time;
                    String afterTime = " ms taked\n";
                    String sTotalNum = "" + totalNum;
                    String afterTotalNum = " user" + (totalNum > 1 ? "s" : "") + " exist in DB";
                    SpannableStringBuilder builder = new SpannableStringBuilder(beforeNum + sNum + afterNum + sTime + afterTime + sTotalNum + afterTotalNum);
                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1D9F9A")),
                            beforeNum.length(),
                            (beforeNum.length() + sNum.length()),
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                            beforeNum.length(),
                            (beforeNum.length() + sNum.length()),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new AbsoluteSizeSpan(16, true),
                            beforeNum.length(),
                            (beforeNum.length() + sNum.length()),
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1D9F9A")),
                            (beforeNum.length() + sNum.length() + afterNum.length()),
                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length()),
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                            (beforeNum.length() + sNum.length() + afterNum.length()),
                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length()),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new AbsoluteSizeSpan(16, true),
                            (beforeNum.length() + sNum.length() + afterNum.length()),
                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length()),
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1D9F9A")),
                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length() + afterTime.length()),
                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length() + afterTime.length() + sTotalNum.length()),
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length() + afterTime.length()),
                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length() + afterTime.length() + sTotalNum.length()),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new AbsoluteSizeSpan(16, true),
                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length() + afterTime.length()),
                            (beforeNum.length() + sNum.length() + afterNum.length() + sTime.length() + afterTime.length() + sTotalNum.length()),
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    mImageView_tips_image.setImageDrawable(getResources().getDrawable(R.drawable.success));
                    mTextView_tips_msg.setText(builder);
                    if (mApp.isLedEnable()) {
                        ledOff();
                    }
                    break;
                }
                case MSG_IDENTIFY_FAIL: {
                    int totalNum = msg.arg1;
                    int time = msg.arg2;
                    String beforeTime = "Identify failed!\n";
                    String sTime = "" + time;
                    String afterTime = " ms taked\n";
                    String sTotalNum = "" + totalNum;
                    String afterTotalNum = " user" + (totalNum > 1 ? "s" : "") + " exist in DB";
                    SpannableStringBuilder builder = new SpannableStringBuilder(beforeTime + sTime + afterTime + sTotalNum + afterTotalNum);
                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1D9F9A")),
                            beforeTime.length(),
                            (beforeTime.length() + sTime.length()),
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                            beforeTime.length(),
                            (beforeTime.length() + sTime.length()),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new AbsoluteSizeSpan(16, true),
                            beforeTime.length(),
                            (beforeTime.length() + sTime.length()),
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1D9F9A")),
                            (beforeTime.length() + sTime.length() + afterTime.length()),
                            (beforeTime.length() + sTime.length() + afterTime.length() + sTotalNum.length()),
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                            (beforeTime.length() + sTime.length() + afterTime.length()),
                            (beforeTime.length() + sTime.length() + afterTime.length() + sTotalNum.length()),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new AbsoluteSizeSpan(16, true),
                            (beforeTime.length() + sTime.length() + afterTime.length()),
                            (beforeTime.length() + sTime.length() + afterTime.length() + sTotalNum.length()),
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    mImageView_tips_image.setImageDrawable(getResources().getDrawable(R.drawable.fail));
                    mTextView_tips_msg.setText(builder);
                    if (mApp.isLedEnable()) {
                        ledOff();
                    }
                    break;
                }
            }
        }
    };
}
