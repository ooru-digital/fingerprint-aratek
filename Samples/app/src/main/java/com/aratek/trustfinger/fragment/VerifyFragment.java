package com.aratek.trustfinger.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.text.TextUtils;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aratek.trustfinger.Config;
import com.aratek.trustfinger.R;
import com.aratek.trustfinger.adapter.MyListAdapter;
import com.aratek.trustfinger.bean.FingerData;
import com.aratek.trustfinger.bean.LargestFingerData;
import com.aratek.trustfinger.bean.User;
import com.aratek.trustfinger.common.CommonUtil;
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
import com.aratek.trustfinger.widget.FingerView;
import com.aratek.trustfinger.widget.MyListView;
import com.aratek.trustfinger.widget.OpenFileDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class VerifyFragment extends BaseFragment /*implements View.OnClickListener*/ {
    private static final String TAG = "VerifyFragment";
    private static final int MSG_RESET_UI = 0;
    private static final int MSG_UPDATE_IMAGE = 1;
    private static final int MSG_UPDATE_IMAGE_1 = 2;
    private static final int MSG_UPDATE_IMAGE_2 = 3;
    private static final int MSG_UPDATE_IMAGE_3 = 4;
    private static final int MSG_UPDATE_USER_LIST = 5;
    private static final int MSG_VERIFY_SUCCESS = 6;
    private static final int MSG_VERIFY_FAIL = 7;
    private static final int MSG_VERIFY_WARNING = 8;

    private ScrollView sv;
    private RadioGroup mRadioGroup_selection;
    private RadioButton mRadioButton_db;
    private RadioButton mRadioButton_file;
    private MyListView mListView_users;
    private TextView mTextView_file_path;
    private TextView mTextView_empty_view;
    private LinearLayout mLinearLayout_user_list;
    private LinearLayout mLinearLayout_file_path;
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

    private ImageView mImageView_fingerprint_1;
    private TextView mTextView_image_quality_1;
    private ImageView mImageView_fingerprint_2;
    private TextView mTextView_image_quality_2;
    private ImageView mImageView_fingerprint_3;
    private TextView mTextView_image_quality_3;
    private ImageView mImageView_fingerprint;
    private TextView mTextView_image_quality;
    private ImageView mImageView_tips_image;
    private TextView mTextView_tips_msg;
    private ProgressBar mProgressBar_image_quality;
    private EditText mEditText_image_quality_threshold;

    private TextView mTextView_select_security_level;
    private PopupWindow popupWindow;

    private List<User> mUserList = new ArrayList<User>();
    private MyListAdapter myListAdapter;
    private int mPosition = -1;
    private User mUser;
    private boolean isVerifing = false;
    private VerifyTask mVerifyTask;
    private DBHelper mDBHelper;
    private SecurityLevel mSecurityLevel = SecurityLevel.Level4;
    private int selection = 0;
    private static final int SELECTION_DB = 0;
    private static final int SELECTION_FILE = 1;
    private FingerView currentFingerView;
    private FingerPosition mFingerPosition;
    private View root;
    private long startTime, endTime;
    private String currentFilePath = "";
    private String currentFileName = "";
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
            root = inflater.inflate(R.layout.fragment_verify, container, false);
            sv = (ScrollView) root.findViewById(R.id.sv_content);
            mLinearLayout_user_list = (LinearLayout) root.findViewById(R.id.ly_user_list);
            mLinearLayout_file_path = (LinearLayout) root.findViewById(R.id.ly_file_path);
            mListView_users = (MyListView) root.findViewById(R.id.lv_user_list);
            mTextView_empty_view = (TextView) root.findViewById(R.id.tv_no_datas);
            mTextView_file_path = (TextView) root.findViewById(R.id.tv_file_path);
            mRadioGroup_selection = (RadioGroup) root.findViewById(R.id.rdoGrp_selection);
            mRadioButton_db = (RadioButton) root.findViewById(R.id.rdoBtn_check_in_local_base);
            mRadioButton_file = (RadioButton) root.findViewById(R.id.rdoBtn_file);
            mImageView_fingerprint_1 = (ImageView) root.findViewById(R.id.iv_fingerprint_1);
            mTextView_image_quality_1 = (TextView) root.findViewById(R.id.tv_image_quality_1);
            mImageView_fingerprint_2 = (ImageView) root.findViewById(R.id.iv_fingerprint_2);
            mTextView_image_quality_2 = (TextView) root.findViewById(R.id.tv_image_quality_2);
            mImageView_fingerprint_3 = (ImageView) root.findViewById(R.id.iv_fingerprint_3);
            mTextView_image_quality_3 = (TextView) root.findViewById(R.id.tv_image_quality_3);

            mImageView_fingerprint = (ImageView) root.findViewById(R.id.iv_fingerprint);
            mTextView_image_quality = (TextView) root.findViewById(R.id.tv_image_quality);
            mProgressBar_image_quality = (ProgressBar) root.findViewById(R.id.proBar_image_quality);
            mEditText_image_quality_threshold = (EditText) root.findViewById(R.id
                    .et_image_quality_threshold);
            mTextView_select_security_level = (TextView) root.findViewById(R.id
                    .tv_select_security_level);
            mImageView_tips_image = (ImageView) root.findViewById(R.id.iv_tips_image);
            mTextView_tips_msg = (TextView) root.findViewById(R.id.tv_tips_msg);

            mEditText_image_quality_threshold.setText((String) getParameterFromPreferences(Config.VERIFY_IMAGE_QUALITY_THRESHOLD, null));
            String level = (String) getParameterFromPreferences(Config.VERIFY_SECURITY_LEVEL, null);
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
            mRadioButton_db.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selection = SELECTION_DB;
                    mLinearLayout_user_list.setVisibility(View.VISIBLE);
                    mLinearLayout_file_path.setVisibility(View.INVISIBLE);
                    resetUI();

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
                    saveParameterToPreferences(Config.VERIFY_IMAGE_QUALITY_THRESHOLD, s.toString());
                }
            });
            mRadioButton_file.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTrustFingerDevice == null) {
                        handleMsg("Device not opened", Color.RED);
                        mRadioGroup_selection.check(R.id.rdoBtn_check_in_local_base);
                        return;
                    }
                    resetUI();
                    mLinearLayout_user_list.setVisibility(View.INVISIBLE);
                    mLinearLayout_file_path.setVisibility(View.VISIBLE);
                    String feature_path = (String) getParameterFromPreferences(Config.FEATURE_PATH, null);
                    Map<String, Integer> images = new HashMap<String, Integer>();
                    images.put(feature_path, R.drawable.filedialog_root);
                    images.put(OpenFileDialog.sParent, R.drawable.filedialog_folder_up);
                    images.put(OpenFileDialog.sFolder, R.drawable.filedialog_folder);
                    images.put("bione", R.drawable.file);
                    images.put("iso-fmr", R.drawable.file);
                    images.put("ansi-fmr", R.drawable.file);
                    images.put(OpenFileDialog.sEmpty, R.drawable.filedialog_root);
                    final Dialog dialog = OpenFileDialog.createDialog(1, getActivity(), getString
                            (R.string.select_file), new OpenFileDialog.CallbackBundle() {
                        @Override
                        public void callback(Bundle bundle) {
                            String fileName = bundle.getString("name");
                            String filePath = bundle.getString("path");
                            if (!TextUtils.isEmpty(fileName)) {
                                currentFileName = fileName;
                            }
                            if (!TextUtils.isEmpty(filePath)) {
                                currentFilePath = filePath;
                            }
                            mTextView_file_path.setText(currentFilePath);
                            String[] strs = currentFileName.split("_");
                            User userFromFile = new User();
                            if (strs != null && strs.length >= 3) {
                                byte[] feature = CommonUtil.getDataFromFile(currentFilePath);
                                FingerData fingerData = new FingerData();
                                if (feature != null) {
                                    String position = null;
                                    switch (strs[2]) {
                                        case "LeftLittleFinger":
                                            fingerData.setLeft_little_template(feature);
                                            position = strs[2];
                                            break;
                                        case "LeftRingFinger":
                                            fingerData.setLeft_ring_template(feature);
                                            position = strs[2];
                                            break;
                                        case "LeftMiddleFinger":
                                            fingerData.setLeft_middle_template(feature);
                                            position = strs[2];
                                            break;
                                        case "LeftIndexFinger":
                                            fingerData.setLeft_index_template(feature);
                                            position = strs[2];
                                            break;
                                        case "LeftThumb":
                                            fingerData.setLeft_thumb_template(feature);
                                            position = strs[2];
                                            break;
                                        case "RightLittleFinger":
                                            fingerData.setRight_little_template(feature);
                                            position = strs[2];
                                            break;
                                        case "RightRingFinger":
                                            fingerData.setRight_ring_template(feature);
                                            position = strs[2];
                                            break;
                                        case "RightMiddleFinger":
                                            fingerData.setRight_middle_template(feature);
                                            position = strs[2];
                                            break;
                                        case "RightIndexFinger":
                                            fingerData.setRight_index_template(feature);
                                            position = strs[2];
                                            break;
                                        case "RightThumb":
                                            fingerData.setRight_thumb_template(feature);
                                            position = strs[2];
                                            break;
                                    }
                                    if (position != null) {
                                        fingerData.getFingerPositions().add(position);
                                        userFromFile.setFingerData(fingerData);
                                    } else {
                                        handleMsg("Import fail: unsupported file!", Color.RED);
                                        return;
                                    }

                                } else {
                                    handleMsg("Import fail, invalid file!", Color.RED);
                                    return;
                                }
                                mUser = userFromFile;
                                sv.fullScroll(ScrollView.FOCUS_DOWN);
                                showUserFingerData(mUser.getFingerData().getFingerPositions());
                                doVerify();
                            } else {
                                handleMsg("Import fail: unsupported file!", Color.RED);
                                return;
                            }

                        }
                    }, ".bione;.iso-fmr;.ansi-fmr;", images, feature_path);
                    //                    dialog.setCancelable(false);
                    dialog.show();

                }
            });
            mRadioGroup_selection.setOnCheckedChangeListener(new RadioGroup
                    .OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    int radioButtonId = group.getCheckedRadioButtonId();
                    switch (radioButtonId) {
                        case R.id.rdoBtn_check_in_local_base:
                            selection = SELECTION_DB;
                            mLinearLayout_user_list.setVisibility(View.VISIBLE);
                            mLinearLayout_file_path.setVisibility(View.INVISIBLE);
                            break;
                        case R.id.rdoBtn_file:

                            break;
                    }

                }
            });

            mListView_users.setEmptyView(mTextView_empty_view);
            myListAdapter = new MyListAdapter(getActivity(), mUserList);
            mListView_users.setAdapter(myListAdapter);
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
            mListView_users.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mTrustFingerDevice == null) {
                        handleMsg("Device not opened", Color.RED);
                        return;
                    }
                    mFingerPosition = null;
                    if (mPosition == position) {
                        myListAdapter.setSelectItem(-1);
                        mPosition = -1;
                        myListAdapter.notifyDataSetInvalidated();
                        resetUI();
                        return;
                    } else {
                        myListAdapter.setSelectItem(position);
                        mPosition = position;
                    }
                    myListAdapter.notifyDataSetInvalidated();
                    mUser = mUserList.get(position);
                    showUserFingerData(mUser.getFingerData().getFingerPositions());
                    handleMsg("", Color.BLACK);
                }
            });

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

//            mFingerView_left_thumb.setOnClickListener(this);
//            mFingerView_left_index.setOnClickListener(this);
//            mFingerView_left_middle.setOnClickListener(this);
//            mFingerView_left_ring.setOnClickListener(this);
//            mFingerView_left_little.setOnClickListener(this);
//
//            mFingerView_right_thumb.setOnClickListener(this);
//            mFingerView_right_index.setOnClickListener(this);
//            mFingerView_right_middle.setOnClickListener(this);
//            mFingerView_right_ring.setOnClickListener(this);
//            mFingerView_right_little.setOnClickListener(this);

            mTextView_select_security_level.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupWindow();
                    if (popupWindow != null && !popupWindow.isShowing()) {
                        popupWindow.showAsDropDown(mTextView_select_security_level, 0, 10);
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
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array
                .security_level, R.layout.spinner_list_item);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_list_item);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String level = parent.getItemAtPosition(position).toString();
                mTextView_select_security_level.setText(level);
                saveParameterToPreferences(Config.VERIFY_SECURITY_LEVEL, level);
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
        popupWindow = new PopupWindow(lv, mTextView_select_security_level.getWidth(), ListView
                .LayoutParams.WRAP_CONTENT, true);
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

    private void resetFingerViews() {
        mFingerView_left_little.setEnrolled(false);
        mFingerView_left_little.setSelected(false);
        mFingerView_left_ring.setEnrolled(false);
        mFingerView_left_ring.setSelected(false);
        mFingerView_left_middle.setEnrolled(false);
        mFingerView_left_middle.setSelected(false);
        mFingerView_left_index.setEnrolled(false);
        mFingerView_left_index.setSelected(false);
        mFingerView_left_thumb.setEnrolled(false);
        mFingerView_left_thumb.setSelected(false);
        mFingerView_right_little.setEnrolled(false);
        mFingerView_right_little.setSelected(false);
        mFingerView_right_ring.setEnrolled(false);
        mFingerView_right_ring.setSelected(false);
        mFingerView_right_middle.setEnrolled(false);
        mFingerView_right_middle.setSelected(false);
        mFingerView_right_index.setEnrolled(false);
        mFingerView_right_index.setSelected(false);
        mFingerView_right_thumb.setEnrolled(false);
        mFingerView_right_thumb.setSelected(false);
    }

    private void showUserFingerData(Set<String> fingerPositions) {
        resetFingerViews();
        for (String position : fingerPositions) {
            switch (position) {
                case "LeftLittleFinger":
                    mFingerView_left_little.setEnrolled(true);
//                    mFingerView_left_little.performClick();
                    onClick(mFingerView_left_little);
                    break;
                case "LeftRingFinger":
                    mFingerView_left_ring.setEnrolled(true);
//                    mFingerView_left_ring.performClick();
                    onClick(mFingerView_left_ring);
                    break;
                case "LeftMiddleFinger":
                    mFingerView_left_middle.setEnrolled(true);
//                    mFingerView_left_middle.performClick();
                    onClick(mFingerView_left_middle);
                    break;
                case "LeftIndexFinger":
                    mFingerView_left_index.setEnrolled(true);
//                    mFingerView_left_index.performClick();
                    onClick(mFingerView_left_index);
                    break;
                case "LeftThumb":
                    mFingerView_left_thumb.setEnrolled(true);
//                    mFingerView_left_thumb.performClick();
                    onClick(mFingerView_left_thumb);
                    break;
                case "RightLittleFinger":
                    mFingerView_right_little.setEnrolled(true);
//                    mFingerView_right_little.performClick();
                    onClick(mFingerView_right_little);
                    break;
                case "RightRingFinger":
                    mFingerView_right_ring.setEnrolled(true);
//                    mFingerView_right_ring.performClick();
                    onClick(mFingerView_right_ring);
                    break;
                case "RightMiddleFinger":
                    mFingerView_right_middle.setEnrolled(true);
                    onClick(mFingerView_right_middle);
//                    mFingerView_right_middle.performClick();
                    break;
                case "RightIndexFinger":
                    mFingerView_right_index.setEnrolled(true);
//                    mFingerView_right_index.performClick();
                    onClick(mFingerView_right_index);
                    break;
                case "RightThumb":
                    mFingerView_right_thumb.setEnrolled(true);
//                    mFingerView_right_thumb.performClick();
                    onClick(mFingerView_right_thumb);
                    break;
            }
        }
    }

    private void doVerify() {
        if (!isVerifing) {
            mVerifyTask = new VerifyTask(mTextView_image_quality, mProgressBar_image_quality);
            mVerifyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            //            isVerifing = true;
            mImageView_fingerprint.setImageBitmap(null);
            mTextView_image_quality.setText("");
            mProgressBar_image_quality.setProgress(0);
            mImageView_tips_image.setImageDrawable(null);
            mTextView_tips_msg.setText("");
            enbleSettingsView(false);
            handleMsg("Verifing", Color.BLACK);
        }
    }

    private boolean checkSettings() {
        String mImageQualityThreshold = mEditText_image_quality_threshold.getText().toString()
                .trim();
        if ("".equals(mImageQualityThreshold) || Integer.parseInt(mImageQualityThreshold) <= 0 ||
                Integer.parseInt(mImageQualityThreshold) > 100) {
            mEditText_image_quality_threshold.setText("");
            mEditText_image_quality_threshold.setHintTextColor(Color.RED);
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
            mEditText_image_quality_threshold.startAnimation(anim);
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadEnrolledUsers();
    }

    @Override
    public void onPause() {
        super.onPause();
        resetUI();
        forceStop();
    }

    public void onClick(View view) {
        if (mTrustFingerDevice == null) {
            //            mHandler.sendMessage(mHandler.obtainMessage(MSG_VERIFY_WARNING, "Device
            // not opened"));
            handleMsg("Device not opened", Color.RED);
            return;
        }
        if (TextUtils.isEmpty(mTextView_file_path.getText().toString()) && mDBHelper.getUserList().isEmpty()) {
            handleMsg("No enrolled users!", Color.RED);
            return;
        }
        if (selection == SELECTION_FILE && mUser == null) {
            handleMsg("Select a file for verification!", Color.RED);
            return;
        }
        if (mUser == null) {
            handleMsg("Select a user for verification!", Color.RED);
            return;
        }
        if (!checkSettings()) {
            return;
        }
        FingerPosition currentFingerPosition = null;
        switch (view.getId()) {
            case R.id.fv_left_lilttle:
                currentFingerPosition = FingerPosition.LeftLittleFinger;
                break;
            case R.id.fv_left_ring:
                currentFingerPosition = FingerPosition.LeftRingFinger;
                break;
            case R.id.fv_left_middle:
                currentFingerPosition = FingerPosition.LeftMiddleFinger;
                break;
            case R.id.fv_left_index:
                currentFingerPosition = FingerPosition.LeftIndexFinger;
                break;
            case R.id.fv_left_thumb:
                currentFingerPosition = FingerPosition.LeftThumb;
                break;
            case R.id.fv_right_thumb:
                currentFingerPosition = FingerPosition.RightThumb;
                break;
            case R.id.fv_right_index:
                currentFingerPosition = FingerPosition.RightIndexFinger;
                break;
            case R.id.fv_right_middle:
                currentFingerPosition = FingerPosition.RightMiddleFinger;
                break;
            case R.id.fv_right_ring:
                currentFingerPosition = FingerPosition.RightRingFinger;
                break;
            case R.id.fv_right_little:
                currentFingerPosition = FingerPosition.RightLittleFinger;
                break;
        }

        currentFingerView = (FingerView) view;
        if (!currentFingerView.isEnrolled()) {
            handleMsg("This finger has not been enrolled!", Color.RED);
            return;
        }
        if (mFingerPosition != null) {
            if (currentFingerPosition.getPosition() == mFingerPosition.getPosition()) {
                if (isVerifing) {
                    if (mVerifyTask != null && mVerifyTask.getStatus() != AsyncTask.Status
                            .FINISHED) {
                        mVerifyTask.cancel(false);
                        mVerifyTask.waitForDone();
                        mVerifyTask = null;
                    }
                    isVerifing = false;
                    mImageView_fingerprint.setImageDrawable(getResources().getDrawable(R.drawable
                            .nofinger));
                    mTextView_image_quality.setText("");
                    enbleSettingsView(true);
                }
                currentFingerView.setSelected(false);
                currentFingerView = null;
                mFingerPosition = null;
                mImageView_fingerprint.setImageDrawable(getResources().getDrawable(R.drawable
                        .nofinger));
                mTextView_image_quality.setText("");
                enbleSettingsView(true);
                handleMsg("Verify stopped", Color.BLACK);
                return;
            } else {
                currentFingerView.setSelected(false);
            }
        }
        mFingerPosition = currentFingerPosition;
        currentFingerView.setSelected(true);
        sv.fullScroll(ScrollView.FOCUS_DOWN);
        doVerify();
    }

    public void resetUI() {
        if (viewCreated) {
            mHandler.sendEmptyMessage(MSG_RESET_UI);
        }
    }

    public void forceStop() {
        if (isVerifing) {
            if (mVerifyTask != null && mVerifyTask.getStatus() != AsyncTask.Status.FINISHED) {
                mVerifyTask.cancel(false);
                mVerifyTask.waitForDone();
                mVerifyTask = null;
            }
            isVerifing = false;
            enbleSettingsView(true);
        }
    }

    private void enbleSettingsView(boolean enable) {
        mEditText_image_quality_threshold.setEnabled(enable);
        mTextView_select_security_level.setEnabled(enable);
        //        mListView_users.setEnabled(enable);
        for (int i = 0; i < mRadioGroup_selection.getChildCount(); i++) {
            mRadioGroup_selection.getChildAt(i).setEnabled(enable);
        }
    }

    public void loadEnrolledUsers() {
        mHandler.sendEmptyMessage(MSG_UPDATE_USER_LIST);
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

    private class VerifyTask extends AsyncTask<Void, Integer, Void> {
        private boolean mIsDone = false;
        private TextView textViewQulity;
        private ProgressBar progressBar;
        private Bitmap fpImage_bitmap = null;
        private byte[] fpImage_Raw = null;
        private byte[] fpImage_bmp = null;
        private byte[] fpFeatureData = null;
        private int imageQuality = 0;
        private String msg = null;
        private VerifyResult result;

        public VerifyTask(TextView textViewQulity, ProgressBar progressBar) {
            super();
            this.textViewQulity = textViewQulity;
            this.progressBar = progressBar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isVerifing = true;
            if (mApp.isLedEnable()) {
                ledOnRed();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            largestFingerData.clear();
            callback.setLedEnable(false);
            callback.setLfdEnable(true);
            int mImageQualityThrethold = Integer.parseInt
                    (mEditText_image_quality_threshold.getText().toString().trim());
            int qualityLowTimes = 0;
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
                            mHandler.sendMessage(mHandler.obtainMessage(MSG_VERIFY_WARNING, "fake finger"));
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
                    fpImage_bmp = mTrustFingerDevice.rawToBmp(fpImage_Raw, mTrustFingerDevice
                            .getImageInfo().getWidth(), mTrustFingerDevice.getImageInfo()
                            .getHeight(), mTrustFingerDevice.getImageInfo().getResolution());
                    if (fpImage_bmp == null || imageQuality <= Config.minQualityTh) {
                        publishProgress(0);
                        updateFingerprintImage(null);
//                        handleMsg("bad image", Color.RED);
                        continue;
                    }
                    fpImage_bitmap = BitmapFactory.decodeByteArray(fpImage_bmp, 0, fpImage_bmp
                            .length);
                    publishProgress(imageQuality);
                    updateFingerprintImage(fpImage_bitmap);
                    if (imageQuality >= mImageQualityThrethold) {
                        if (mTrustFingerDevice == null) {
                            handleMsg("Device not opened", Color.RED);
                            break;
                        }
                        fpFeatureData = mTrustFingerDevice.extractFeature(fpImage_Raw,
                                FingerPosition.Unknown);
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
//                    if (largestFingerData.getImgQuality() >= Config.maxQualityTh) {
//                        qualityLowTimes = 3;
//                    } else {
//                        if (imageQuality > largestFingerData.getImgQuality()) {//try again 3 times.
//                            qualityLowTimes = 0;
//                        } else {
//                            qualityLowTimes++;
//                        }
//                    }
                    if (/*qualityLowTimes >= 3 &&*/ !largestFingerData.isIsrRaise() && largestFingerData.getImgQuality() >= mImageQualityThrethold) {
                        fpFeatureData = largestFingerData.getFpFeatureData();
                        updateFingerprintImage(largestFingerData.getFpImage_bitmap());
                        publishProgress(largestFingerData.getImgQuality());
                        break;
                    }
                } catch (TrustFingerException e) {
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_VERIFY_WARNING, "Verify " +
                            "exception:" + e.getType().toString()));
                    handleMsg("Verify exception: " + e.getType().toString(), Color.RED);
                    e.printStackTrace();
                }
            } while (true);
            if (fpFeatureData == null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        enbleSettingsView(true);
                    }
                });
                if (mApp.isLedEnable()) {
                    ledOff();
                }
                callback.setLedEnable(true);
                callback.setLfdEnable(true);
                mVerifyTask = null;
                isVerifing = false;
                mIsDone = true;
                return null;
            }
            try {
                FingerData fingerData = null;
                if (mUser != null) {
                    fingerData = mUser.getFingerData();
                }

                if (fingerData == null) {
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_VERIFY_WARNING, "No enrolled " +
                            "templates"));
                    handleMsg("Verify fail， no enrolled templates", Color.RED);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            enbleSettingsView(true);
                        }
                    });
                    if (mApp.isLedEnable()) {
                        ledOff();
                    }
                    mVerifyTask = null;
                    isVerifing = false;
                    mIsDone = true;
                    return null;
                }
                byte[] template = null;
                String fingerPosition = null;
                boolean matched = false;
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
                            if (mApp.isLedEnable()) {
                                ledOff();
                            }
                            mVerifyTask = null;
                            isVerifing = false;
                            mIsDone = true;
                            return null;
                        }
                        result = mTrustFingerDevice.verify(mSecurityLevel, template, fpFeatureData);
                        if (result.error == 0) {
                            if (result.isMatched) {
                                matched = true;
                                break;
                            } else {
                                Log.e(TAG, "" + result.similarity);
                            }
                        }
                    } else {
                        Log.e(TAG, "verify fail, no enrolled template");
                    }
                }
                if (matched) {
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_VERIFY_SUCCESS, result
                            .similarity, 0, fingerPosition));
                } else {
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_VERIFY_FAIL, "Verify fail!"));
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        enbleSettingsView(true);
                    }
                });
            } catch (TrustFingerException e) {
                if (mApp.isLedEnable()) {
                    ledOff();
                }
                mHandler.sendMessage(mHandler.obtainMessage(MSG_VERIFY_WARNING, "Verify " +
                        "exception:" + e.getType().toString()));
                handleMsg("Verify exception: " + e.getType().toString(), Color.RED);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        enbleSettingsView(true);
                    }
                });
                e.printStackTrace();
                mVerifyTask = null;
                isVerifing = false;
                mIsDone = true;
                return null;
            }
            mVerifyTask = null;
            isVerifing = false;
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
        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_IMAGE, fpImage));
    }

    private void updateFingerprintImage_1(Bitmap fpImage, int imageQuality) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_IMAGE_1, imageQuality, 0, fpImage));
    }

    private void updateFingerprintImage_2(Bitmap fpImage, int imageQuality) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_IMAGE_2, imageQuality, 0, fpImage));
    }

    private void updateFingerprintImage_3(Bitmap fpImage, int imageQuality) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_IMAGE_3, imageQuality, 0, fpImage));
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
                    mPosition = -1;
                    mUser = null;
                    myListAdapter.setSelectItem(-1);
                    myListAdapter.notifyDataSetChanged();
                    resetFingerViews();
                    mFingerPosition = null;
                    currentFingerView = null;
                    mImageView_fingerprint.setImageDrawable(getResources().getDrawable(R.drawable
                            .nofinger));
                    mProgressBar_image_quality.setProgress(0);
                    mTextView_image_quality.setText("");
                    mImageView_tips_image.setImageDrawable(null);
                    mTextView_tips_msg.setText("");
                    mTextView_file_path.setText("");
                    break;
                case MSG_UPDATE_IMAGE:
                    if (mTrustFingerDevice == null) {
                        mImageView_fingerprint.setImageDrawable(getResources().getDrawable(R
                                .drawable.nofinger));
                        break;
                    }
                    Bitmap bmp_fpImg = (Bitmap) msg.obj;
                    if (bmp_fpImg == null) {
                        mImageView_fingerprint.setImageDrawable(getResources().getDrawable(R
                                .drawable.nofinger));
                    } else {
                        mImageView_fingerprint.setImageBitmap(bmp_fpImg);
                    }

                    break;
                case MSG_UPDATE_IMAGE_1:
                    if (mTrustFingerDevice == null) {
                        break;
                    }
                    Bitmap bmp_fpImg_1 = (Bitmap) msg.obj;
                    if (bmp_fpImg_1 == null) {
                        mImageView_fingerprint_1.setImageDrawable(getResources().getDrawable(R
                                .drawable.nofinger));
                    } else {
                        mImageView_fingerprint_1.setImageBitmap(bmp_fpImg_1);
                    }
                    int imageQuality_1 = msg.arg1;
                    mTextView_image_quality_1.setText("" + imageQuality_1);
                    break;
                case MSG_UPDATE_IMAGE_2:
                    if (mTrustFingerDevice == null) {
                        break;
                    }
                    Bitmap bmp_fpImg_2 = (Bitmap) msg.obj;
                    if (bmp_fpImg_2 == null) {
                        mImageView_fingerprint_2.setImageDrawable(getResources().getDrawable(R
                                .drawable.nofinger));
                    } else {
                        mImageView_fingerprint_2.setImageBitmap(bmp_fpImg_2);
                    }
                    int imageQuality_2 = msg.arg1;
                    mTextView_image_quality_2.setText("" + imageQuality_2);
                    break;
                case MSG_UPDATE_IMAGE_3:
                    if (mTrustFingerDevice == null) {
                        break;
                    }
                    Bitmap bmp_fpImg_3 = (Bitmap) msg.obj;
                    if (bmp_fpImg_3 == null) {
                        mImageView_fingerprint_3.setImageDrawable(getResources().getDrawable(R
                                .drawable.nofinger));
                    } else {
                        mImageView_fingerprint_3.setImageBitmap(bmp_fpImg_3);
                    }
                    int imageQuality_3 = msg.arg1;
                    mTextView_image_quality_3.setText("" + imageQuality_3);
                    break;
                case MSG_UPDATE_USER_LIST:
                    mPosition = -1;
                    mUser = null;
                    myListAdapter.setSelectItem(-1);
                    if (currentFingerView != null) {
                        currentFingerView.setSelected(false);
                        currentFingerView.setEnrolled(false);
                    }
                    if (mDBHelper != null) {
                        mUserList.clear();
                        mUserList.addAll(mDBHelper.getUserList());
                    }
                    Collections.reverse(mUserList);
                    myListAdapter.notifyDataSetChanged();
                    break;
                case MSG_VERIFY_SUCCESS: {
                    int score = msg.arg1;
                    String beforePosition = "Verify succeed!\nFinger position: ";
                    String fingerPosition = (String) msg.obj;
                    String afterPsition = "\nSimilarity: ";
                    String sScore = "" + score;
                    SpannableStringBuilder builder = new SpannableStringBuilder(beforePosition +
                            fingerPosition + afterPsition + sScore);
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
                    // set similarity color
                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1D9F9A")),
                            (beforePosition.length() + fingerPosition.length() + afterPsition
                                    .length()),
                            (beforePosition.length() + fingerPosition.length() + afterPsition
                                    .length() + sScore.length()),
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                            (beforePosition.length() + fingerPosition.length() + afterPsition
                                    .length()),
                            (beforePosition.length() + fingerPosition.length() + afterPsition
                                    .length() + sScore.length()),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new AbsoluteSizeSpan(16, true),
                            (beforePosition.length() + fingerPosition.length() + afterPsition
                                    .length()),
                            (beforePosition.length() + fingerPosition.length() + afterPsition
                                    .length() + sScore.length()),
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    mImageView_tips_image.setImageDrawable(getResources().getDrawable(R.drawable
                            .success));
                    mTextView_tips_msg.setText(builder);
                    //                    mPosition = -1;
                    //                    mUser = null;
                    if (currentFingerView != null) {
                        currentFingerView.setSelected(false);
                        //                        currentFingerView.setEnrolled(false);
                        //                        currentFingerView = null;
                        mFingerPosition = null;
                    }
                    //                    myListAdapter.setSelectItem(-1);
                    //                    myListAdapter.notifyDataSetChanged();
                    if (mApp.isLedEnable()) {
                        ledOff();
                    }
                    break;
                }
                case MSG_VERIFY_FAIL: {
                    String s = (String) msg.obj;
                    mImageView_tips_image.setImageDrawable(getResources().getDrawable(R.drawable
                            .fail));
                    mTextView_tips_msg.setText(s);
                    //                    mPosition = -1;
                    //                    mUser = null;
                    if (currentFingerView != null) {
                        currentFingerView.setSelected(false);
                        //                        currentFingerView.setEnrolled(false);
                        //                        currentFingerView = null;
                        mFingerPosition = null;
                    }
                    //                    myListAdapter.setSelectItem(-1);
                    //                    myListAdapter.notifyDataSetChanged();
                    if (mApp.isLedEnable()) {
                        ledOff();
                    }
                    break;
                }
                case MSG_VERIFY_WARNING: {
                    String s = (String) msg.obj;
                    mImageView_tips_image.setImageDrawable(getResources().getDrawable(R.drawable
                            .warning));
                    mTextView_tips_msg.setText(s);
                    mPosition = -1;
                    //                    mUser = null;
                    if (currentFingerView != null) {
                        currentFingerView.setSelected(false);
                        //                        currentFingerView.setEnrolled(false);
                        currentFingerView = null;
                        mFingerPosition = null;
                    }
                    myListAdapter.setSelectItem(-1);
                    myListAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    };
}
