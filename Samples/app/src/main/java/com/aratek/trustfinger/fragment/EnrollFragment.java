package com.aratek.trustfinger.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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


public class EnrollFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "EnrollFragment";
    private static final int MSG_RESET_UI = 0;
    private static final int MSG_UPDATE_IMAGE = 1;
    private static final int MSG_UPDATE_IMAGE_1 = 2;
    private static final int MSG_UPDATE_IMAGE_2 = 3;
    private static final int MSG_UPDATE_IMAGE_3 = 4;
    private static final int MSG_UPDATE_USER_LIST = 5;
    private static final int MSG_ENROLL_SUCCESS = 6;
    private static final int MSG_ENROLL_FAIL = 7;
    private static final int MSG_ENROLL_WARNING = 8;
    private static final int MSG_ENROLL_PROGRESS = 9;

    private ScrollView sv;
    private EditText mEditText_image_quality_threshold;
    private EditText mEditText_user_id;
    private EditText mEditText_user_first_name;
    private EditText mEditText_user_last_name;

    private ImageView mImageView_fingerprint_1;
    private TextView mTextView_image_quality_1;

    private ImageView mImageView_fingerprint_2;
    private TextView mTextView_image_quality_2;
    private ImageView mImageView_fingerprint_3;
    private TextView mTextView_image_quality_3;

    private LinearLayout mLinearLayout_settings;
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
    private TextView mTextView_image_quality;
    private ProgressBar mProgressBar_image_quality;
    private ImageView mImageView_tips_image;
    private TextView mTextView_tips_msg;

    private MyListView mListView_users;
    private TextView mTextView_empty_view;
    private Button mButton_import_user;
    private Button mButton_remove_user;
    private Button mButton_remove_all;

    private FingerPosition mFingerPosition;
    private boolean isEnrolling = false;
    private EnrollTask mEnrollTask;

    private DBHelper mDBHelper;
    private User mUser;
    private FingerData mFingerData;
    private byte[] mImageData_1;
    private byte[] mImageData_2;
    private byte[] mImageData_3;
    private int mQuality_1;
    private int mQuality_2;
    private int mQuality_3;
    private byte[] mTemplate;
    private List<User> mUserList = new ArrayList<User>();
    private MyListAdapter myListAdapter;
    private int mPosition = -1;
    private FingerView currentFingerView;
    private View root;
    private List<LargestFingerData> largestFingerDataList = new ArrayList<>();
    private ViewStatusCallback callback;

    public void setLedCallback(ViewStatusCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_enroll, container, false);

            sv = (ScrollView) root.findViewById(R.id.sv_content);
            mEditText_image_quality_threshold = (EditText) root.findViewById(R.id
                    .et_image_quality_threshold);
            mEditText_user_id = (EditText) root.findViewById(R.id.et_user_id);
            mEditText_user_first_name = (EditText) root.findViewById(R.id.et_user_first_name);
            mEditText_user_last_name = (EditText) root.findViewById(R.id.et_user_last_name);

            mImageView_fingerprint_1 = (ImageView) root.findViewById(R.id.iv_fingerprint_1);
            mTextView_image_quality_1 = (TextView) root.findViewById(R.id.tv_image_quality_1);
            mImageView_fingerprint_2 = (ImageView) root.findViewById(R.id.iv_fingerprint_2);
            mTextView_image_quality_2 = (TextView) root.findViewById(R.id.tv_image_quality_2);
            mImageView_fingerprint_3 = (ImageView) root.findViewById(R.id.iv_fingerprint_3);
            mTextView_image_quality_3 = (TextView) root.findViewById(R.id.tv_image_quality_3);

            mLinearLayout_settings = (LinearLayout) root.findViewById(R.id.ly_settings);
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

            mTextView_current_position = (TextView) root.findViewById(R.id.tv_current_index);

            mImageView_fingerprint = (ImageView) root.findViewById(R.id.iv_fingerprint);
            mTextView_image_quality = (TextView) root.findViewById(R.id.tv_image_quality);

            mProgressBar_image_quality = (ProgressBar) root.findViewById(R.id.proBar_image_quality);
            mImageView_tips_image = (ImageView) root.findViewById(R.id.iv_tips_image);
            mTextView_tips_msg = (TextView) root.findViewById(R.id.tv_tips_msg);

            mListView_users = (MyListView) root.findViewById(R.id.lv_user_list);

            mButton_import_user = (Button) root.findViewById(R.id.btn_import_user);
            mButton_remove_user = (Button) root.findViewById(R.id.btn_remove_user);
            mButton_remove_all = (Button) root.findViewById(R.id.btn_remove_all);
            mTextView_empty_view = (TextView) root.findViewById(R.id.tv_no_datas);
            mListView_users.setEmptyView(mTextView_empty_view);

            mEditText_image_quality_threshold.setText((String) getParameterFromPreferences
                    (Config.ENROLL_IMAGE_QUALITY_THRESHOLD, null));
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
            mEditText_image_quality_threshold.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    saveParameterToPreferences(Config.ENROLL_IMAGE_QUALITY_THRESHOLD, s
                            .toString());
                }
            });
            mListView_users.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mPosition = position;
                    myListAdapter.setSelectItem(position);
                    myListAdapter.notifyDataSetInvalidated();
                    User user = mUserList.get(position);
                    //                showUserFingerData(user);
                }
            });

            mButton_import_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTrustFingerDevice == null) {
                        handleMsg("Device not opened", Color.RED);
                        return;
                    }
                    if (mDBHelper.getUserList().size() >= 100) {
                        handleMsg("Reached the maximum capacity of database", Color.RED);
                        return;
                    }
                    String feature_path = (String) getParameterFromPreferences(Config
                            .FEATURE_PATH, null);
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
                            String[] strs = fileName.split("_");
                            byte[] feature = CommonUtil.getDataFromFile(filePath);
                            if (feature != null) {
                                if (strs != null && strs.length >= 3) {
                                    if (!checkFingerIsExist(feature)) {
                                        showInputUserInfoDialog(strs, feature);
                                    } else {
                                        handleMsg("Import fail, fingerprint already exists!",
                                                Color.RED);
                                    }
                                } else {
                                    handleMsg("Import fail, invalid file!", Color.RED);
                                }
                            } else {
                                handleMsg("Import fail, invalid file!", Color.RED);
                            }
                        }
                    }, ".bione;.iso-fmr;.ansi-fmr;", images, feature_path);
                    dialog.show();

                }
            });
            mButton_remove_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTrustFingerDevice == null) {
                        handleMsg("Device not opened", Color.RED);
                        return;
                    }
                    if (mDBHelper != null) {
                        if (mDBHelper.getUserList().isEmpty()) {
                            mPosition = -1;
                            handleMsg("No enrolled users", Color.RED);
                            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim
                                    .shake);
                            mTextView_empty_view.startAnimation(anim);
                            return;
                        }
                        if (mPosition == -1) {
                            handleMsg("Please select user", Color.RED);
                            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim
                                    .shake);
                            mListView_users.startAnimation(anim);
                            return;
                        }
                    }
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Warning")
                            .setMessage("Remove the selected user?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (mDBHelper != null) {
                                        if (mUserList != null && mUserList.size() > mPosition) {
                                            mDBHelper.removeUser(mUserList.get(mPosition));
                                            loadEnrolledUsers();
                                        }
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();

                }
            });
            mButton_remove_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTrustFingerDevice == null) {
                        handleMsg("Device not opened", Color.RED);
                        return;
                    }
                    if (mDBHelper != null) {
                        if (mDBHelper.getUserList().isEmpty()) {
                            mPosition = -1;
                            handleMsg("No enrolled users", Color.RED);
                            return;
                        }
                    }
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Warning")
                            .setMessage("Remove all?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (mDBHelper != null) {
                                        mDBHelper.removeAll();
                                        loadEnrolledUsers();
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();

                }
            });
        }
        mDBHelper = new DBHelper(getActivity(), Config.SAVE_TO_SDCARD);
        viewCreated = true;
        return root;
    }

    private void showInputUserInfoDialog(final String[] strs, final byte[] feature) {
        View view = View.inflate(getActivity(), R.layout.dialog_set_user, null);
        final EditText mEditText_dialog_user_id = (EditText) view.findViewById(R.id
                .dialog_et_user_id);
        final EditText mEditText_dialog_first_name = (EditText) view.findViewById(R.id
                .dialog_et_user_first_name);
        final EditText mEditText_dialog_last_name = (EditText) view.findViewById(R.id
                .dialog_et_user_last_name);
        final Spinner mSpinner_finger_position = (Spinner) view.findViewById(R.id
                .dialog_sp_finger_position);
        final ArrayAdapter arrayAdapter_finger_position = ArrayAdapter.createFromResource
                (getActivity(), R.array.finger_position, R.layout.spinner_list_item);
        arrayAdapter_finger_position.setDropDownViewResource(R.layout.spinner_list_item);
        mSpinner_finger_position.setAdapter(arrayAdapter_finger_position);
        switch (strs[2]) {
            case "LeftLittleFinger":
                mSpinner_finger_position.setSelection(4);
                break;
            case "LeftRingFinger":
                mSpinner_finger_position.setSelection(3);
                break;
            case "LeftMiddleFinger":
                mSpinner_finger_position.setSelection(2);
                break;
            case "LeftIndexFinger":
                mSpinner_finger_position.setSelection(1);
                break;
            case "LeftThumb":
                mSpinner_finger_position.setSelection(0);
                break;
            case "RightLittleFinger":
                mSpinner_finger_position.setSelection(9);
                break;
            case "RightRingFinger":
                mSpinner_finger_position.setSelection(8);
                break;
            case "RightMiddleFinger":
                mSpinner_finger_position.setSelection(7);
                break;
            case "RightIndexFinger":
                mSpinner_finger_position.setSelection(6);
                break;
            case "RightThumb":
                mSpinner_finger_position.setSelection(5);
                break;
        }
        arrayAdapter_finger_position.notifyDataSetChanged();


        Button mButton_dialog_ok = (Button) view.findViewById(R.id.dialog_btn_ok);
        Button mButton_dialog_cancel = (Button) view.findViewById(R.id.dialog_btn_cancel);

        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        dialog.getWindow().setContentView(view);
        mButton_dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = mEditText_dialog_user_id.getText().toString().trim();
                String userFirstName = mEditText_dialog_first_name.getText().toString().trim();
                String userLastName = mEditText_dialog_last_name.getText().toString().trim();

                if ("".equals(userId)) {
                    mEditText_dialog_user_id.setText("");
                    mEditText_dialog_user_id.setHint("input your id");
                    mEditText_dialog_user_id.setHintTextColor(Color.RED);
                    Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
                    mEditText_dialog_user_id.startAnimation(anim);
                    return;
                }
                if (mDBHelper.checkUserExist(userId)) {
                    Toast.makeText(getActivity(), "This id has been enrolled!", Toast
                            .LENGTH_SHORT).show();
                    Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
                    mEditText_dialog_user_id.startAnimation(anim);
                    return;
                }
                if ("".equals(userFirstName)) {
                    mEditText_dialog_first_name.setText("");
                    mEditText_dialog_first_name.setHint("input your id");
                    mEditText_dialog_first_name.setHintTextColor(Color.RED);
                    Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
                    mEditText_dialog_first_name.startAnimation(anim);
                    return;
                }
                User user = new User();
                user.setId(userId);
                user.setFirstName(userFirstName);
                user.setLastName(userLastName);
                FingerData fingerData = new FingerData();
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
                    user.setFingerData(fingerData);
                    if (mDBHelper.insertUser(user)) {
                        loadEnrolledUsers();
                    } else {
                        handleMsg("Import fail: this user has been imported", Color.RED);
                    }
                } else {
                    handleMsg("Import fail: unsupported file!", Color.RED);
                }
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService
                        (Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                dialog.dismiss();
            }
        });

        mButton_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService
                        (Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                dialog.dismiss();
            }
        });

    }

    private void doEnroll() {
        if (!isEnrolling) {
            mEnrollTask = new EnrollTask(mTextView_image_quality, mProgressBar_image_quality);
            mEnrollTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            isEnrolling = true;
            mImageView_fingerprint.setImageBitmap(null);
            mTextView_image_quality.setText("");
            mImageView_fingerprint_1.setImageDrawable(getResources().getDrawable(R.drawable
                    .nofinger));
            mTextView_image_quality_1.setText("");
            mImageView_fingerprint_2.setImageDrawable(getResources().getDrawable(R.drawable
                    .nofinger));
            mTextView_image_quality_2.setText("");
            mImageView_fingerprint_3.setImageDrawable(getResources().getDrawable(R.drawable
                    .nofinger));
            mTextView_image_quality_3.setText("");
            mProgressBar_image_quality.setProgress(0);
            mImageView_tips_image.setImageDrawable(null);
            mTextView_tips_msg.setText("");
            enbleSettingsView(false);
            handleMsg("Enrolling", Color.BLACK);
        }
    }

    public void forceStop() {
        if (isEnrolling) {
            if (mEnrollTask != null && mEnrollTask.getStatus() != AsyncTask.Status.FINISHED) {
                mEnrollTask.cancel(false);
                mEnrollTask.waitForDone();
                mEnrollTask = null;
            }
            isEnrolling = false;
            enbleSettingsView(true);
            handleMsg("Enroll stopped", Color.BLACK);
        }
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

    public void resetUI() {
        if (viewCreated) {
            mHandler.sendEmptyMessage(MSG_RESET_UI);
        }
    }

    private void showUserFingerData(User user) {
        String id = user.getId();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();

        mEditText_user_id.setText(id);
        mEditText_user_first_name.setText(firstName);
        mEditText_user_last_name.setText(lastName);

        FingerData fingerData = user.getFingerData();
        Set<String> positions = fingerData.getFingerPositions();
        for (String position : positions) {
            switch (position) {
                case "LEFT_LITTLE":
                    mFingerView_left_little.setEnrolled(true);
                    break;
                case "LEFT_RING":
                    mFingerView_left_ring.setEnrolled(true);
                    break;
                case "LEFT_MIDDLE":
                    mFingerView_left_middle.setEnrolled(true);
                    break;
                case "LEFT_INDEX":
                    mFingerView_left_index.setEnrolled(true);
                    break;
                case "LEFT_THUMB":
                    mFingerView_left_thumb.setEnrolled(true);
                    break;
                case "RIGHT_LITTLE":
                    mFingerView_right_little.setEnrolled(true);
                    break;
                case "RIGHT_RING":
                    mFingerView_right_ring.setEnrolled(true);
                    break;
                case "RIGHT_MIDDLE":
                    mFingerView_right_middle.setEnrolled(true);
                    break;
                case "RIGHT_INDEX":
                    mFingerView_right_index.setEnrolled(true);
                    break;
                case "RIGHT_THUMB":
                    mFingerView_right_thumb.setEnrolled(true);
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (mTrustFingerDevice == null) {
            //                    mHandler.sendMessage(mHandler.obtainMessage(MSG_ENROLL_WARNING,
            // "Device not opened"));
            handleMsg("Device not opened", Color.RED);
            return;
        }
        if (!checkSettings() || isEnrolling) {
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
//        if (mEnrollTask != null && mEnrollTask.getStatus() != AsyncTask.Status.FINISHED) {
//            mEnrollTask.cancel(false);
//            mEnrollTask.waitForDone();
//            mEnrollTask = null;
//        }
//        isEnrolling = false;

        if (currentFingerView != null) {
            if (currentFingerPosition.getPosition() == mFingerPosition.getPosition()) {
                if (isEnrolling) {
                    if (mEnrollTask != null && mEnrollTask.getStatus() != AsyncTask.Status
                            .FINISHED) {
                        mEnrollTask.cancel(false);
                        mEnrollTask.waitForDone();
                        mEnrollTask = null;
                    }
                    isEnrolling = false;
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
                mImageView_fingerprint.setImageBitmap(null);
                mTextView_image_quality.setText("");
                enbleSettingsView(true);
                handleMsg("Enroll stopped", Color.BLACK);
                return;
            } else {
                currentFingerView.setSelected(false);
            }
        }
        mFingerPosition = currentFingerPosition;
        currentFingerView = (FingerView) view;
        currentFingerView.setSelected(true);
        //
        int y = mLinearLayout_settings.getHeight() + mLinearLayout_hand.getHeight();
        sv.smoothScrollTo(0, y);
        doEnroll();
    }

    private void loadEnrolledUsers() {
        mHandler.sendEmptyMessage(MSG_UPDATE_USER_LIST);
    }

    private boolean checkSettings() {
        String mUserID = mEditText_user_id.getText().toString().trim();
        String mUserFirstName = mEditText_user_first_name.getText().toString().trim();
        String mImageQualityThreshold = mEditText_image_quality_threshold.getText().toString()
                .trim();
        if ("".equals(mImageQualityThreshold) || Integer.parseInt(mImageQualityThreshold) <= 0 ||
                Integer.parseInt(mImageQualityThreshold) > 100) {
            sv.fullScroll(ScrollView.FOCUS_UP);
            mHandler.sendMessage(mHandler.obtainMessage(MSG_ENROLL_WARNING, "Please check your " +
                    "image quality threshold!"));
            handleMsg("Please check your image quality threshold!", Color.RED);
            mEditText_image_quality_threshold.setText("");
            mEditText_image_quality_threshold.setHintTextColor(Color.RED);
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
            mEditText_image_quality_threshold.startAnimation(anim);
            return false;
        }
        if ("".equals(mUserID)) {
            sv.fullScroll(ScrollView.FOCUS_UP);
            mHandler.sendMessage(mHandler.obtainMessage(MSG_ENROLL_WARNING, "Please check your " +
                    "user id!"));
            handleMsg("Please check your user id!", Color.RED);
            mEditText_user_id.setText("");
            mEditText_user_id.setHint("input your id");
            mEditText_user_id.setHintTextColor(Color.RED);
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
            mEditText_user_id.startAnimation(anim);
            return false;
        }
        if (mDBHelper.checkUserExist(mUserID)) {
            sv.fullScroll(ScrollView.FOCUS_UP);
            mHandler.sendMessage(mHandler.obtainMessage(MSG_ENROLL_WARNING, "This id has been " +
                    "enrolled!"));
            handleMsg("This id has been enrolled!", Color.RED);
            mEditText_user_id.setHintTextColor(Color.RED);
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
            mEditText_user_id.startAnimation(anim);
            return false;
        }
        if ("".equals(mUserFirstName)) {
            sv.fullScroll(ScrollView.FOCUS_UP);
            mHandler.sendMessage(mHandler.obtainMessage(MSG_ENROLL_WARNING, "Please check your " +
                    "first name!"));
            handleMsg("Please check your first name!", Color.RED);
            mEditText_user_first_name.setHintTextColor(Color.RED);
            Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
            mEditText_user_first_name.startAnimation(anim);
            return false;
        }

        return true;
    }

    @Override
    public void setDatas(TrustFingerDevice device) {
        if (isAdded()) {
            mTrustFingerDevice = device;
            if (device != null) {
                if (viewCreated) {
                    mTextView_current_position.setText(getString(R.string.msg_select_a_finger));
                    mTextView_current_position.setTextColor(Color.RED);
                }
            } else {
                forceStop();
                if (viewCreated) {
                    enbleSettingsView(true);
                    resetUI();
                }
            }
        }
    }

    private void enbleSettingsView(boolean enable) {
        mEditText_image_quality_threshold.setEnabled(enable);
        mEditText_user_id.setEnabled(enable);
        mEditText_user_first_name.setEnabled(enable);
        mEditText_user_last_name.setEnabled(enable);
        mLinearLayout_hand.setEnabled(enable);
    }

    private class EnrollTask extends AsyncTask<Void, Integer, Void> {
        private boolean mIsDone = false;
        private TextView textViewQulity;
        private ProgressBar progressBar;
        private Bitmap fpImage_bitmap = null;
        private byte[] fpImage_Raw = null;
        private byte[] fpImage_bmp = null;
        private byte[] templateData = null;
        private byte[] fpFeatureData = null;
        private byte[] fpFeatureData1 = null, fpFeatureData2 = null, fpFeatureData3 = null;
        private int imageQuality = 0;
        private int count = 0;
        private String msg = null;
        private long startTime, endTime;

        public EnrollTask(TextView textViewQulity, ProgressBar progressBar) {
            super();
            this.textViewQulity = textViewQulity;
            this.progressBar = progressBar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isEnrolling = true;
            mEditText_user_id.clearFocus();
            mEditText_user_first_name.clearFocus();
            mEditText_user_last_name.clearFocus();
            if (mApp.isLedEnable()) {
                ledOnRed();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int count = 0;
            LargestFingerData largestFingerData = null;
            largestFingerDataList.clear();
            callback.setLedEnable(false);
            callback.setLfdEnable(true);
            int mImageQualityThrethold = Integer.parseInt(mEditText_image_quality_threshold
                    .getText().toString().trim());
            int qualityLowTimes = 0;
            boolean needHoldup = false;
            do {
                if (isCancelled()) {
                    break;
                }
                if (largestFingerData == null) {
                    largestFingerData = new LargestFingerData();
                }
                if (mTrustFingerDevice == null || !mTrustFingerDevice.isOpened()) {
                    handleMsg("Device not opened", Color.RED);
                    break;
                }
                boolean isFakeFinger = false;
                if (largestFingerData.isIsrRaise()) {
                    if (mApp.isLedEnable()) {
                        ledOnRed();
                    }
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
                            mHandler.sendMessage(mHandler.obtainMessage(MSG_ENROLL_FAIL, "fake finger"));
                            break;
                        }
                    } else {
                        fpImage_Raw = mTrustFingerDevice.captureRawData();
                    }
                    if (fpImage_Raw == null || (mTrustFingerDevice.getLfdLevel() != LfdLevel.OFF && lfdStatus[0] == LfdStatus.UNKNOWN)) {
                        imageQuality = 0;
                        publishProgress(0);
                        updateFingerprintImage(null);
                        if (needHoldup) {
                            needHoldup = false;
                            SystemClock.sleep(300);
                        }/*else {
                            handleMsg("unknown finger", Color.RED);
                        }*/
                        continue;
                    }

                    imageQuality = mTrustFingerDevice.rawDataQuality(fpImage_Raw);

                    fpImage_bmp = mTrustFingerDevice.rawToBmp(fpImage_Raw, mTrustFingerDevice
                            .getImageInfo().getWidth(), mTrustFingerDevice.getImageInfo()
                            .getHeight(), mTrustFingerDevice.getImageInfo().getResolution());

                    if (fpImage_bmp == null || imageQuality <= Config.minQualityTh) {
                        if (!needHoldup) {
                            publishProgress(0);
                            updateFingerprintImage(null);
//                            handleMsg("bad image", Color.RED);
                        } else {
                            handleMsg("Pls hold on ,and capture again!", Color.RED);
                            needHoldup = false;
                            SystemClock.sleep(300);
                        }
                        continue;
                    }
                    if (needHoldup) {
                        handleMsg("Pls hold up your finger and try again!", Color.RED);
                        SystemClock.sleep(300);
                        continue;
                    }
                    fpImage_bitmap = BitmapFactory.decodeByteArray(fpImage_bmp, 0,
                            fpImage_bmp.length);
                    publishProgress(imageQuality);
                    updateFingerprintImage(fpImage_bitmap);
                    if (imageQuality >= mImageQualityThrethold) {
                        try {
                            startTime = System.currentTimeMillis();
                            fpFeatureData = mTrustFingerDevice.extractFeature(fpImage_Raw,
                                    mFingerPosition);
                            endTime = System.currentTimeMillis();
                        } catch (TrustFingerException e) {
                            fpFeatureData = null;
                            e.printStackTrace();
                        }
                        if (fpFeatureData != null) {
                            if (!largestFingerData.isThreshold()) {
                                if (mApp.isLedEnable()) {
                                    ledOnGreen();
                                }
                                largestFingerData.setThreshold(true);
                                MediaPlayerHelper.payMedia(getContext(), R.raw
                                        .please_raise_your_finger);
                            }
                            if (imageQuality > largestFingerData.getImgQuality()) {
                                if (largestFingerDataList.size() > count) {
                                    largestFingerDataList.remove(largestFingerDataList.size()
                                            - 1);
                                }
                                largestFingerData.setFpImageData(fpImage_bmp);
                                largestFingerData.setFpImage_bitmap(fpImage_bitmap);
                                largestFingerData.setImgQuality(imageQuality);
                                largestFingerData.setFpFeatureData(fpFeatureData);
                                largestFingerDataList.add(largestFingerData);
                            }
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

                    if (/*qualityLowTimes >= 3 &&*/ !largestFingerData.isIsrRaise() && largestFingerData.getImgQuality() >= mImageQualityThrethold) {
                        count++;
                        if (count == 1) {
                            fpFeatureData1 = largestFingerDataList.get(0).getFpFeatureData();
                            mQuality_1 = largestFingerDataList.get(0).getImgQuality();
                            mImageData_1 = largestFingerDataList.get(0).getFpImageData();
                            msg = "1st feature extracted success(" + count + "/3)";
                            updateFingerprintImage_1(largestFingerData.getFpImage_bitmap(),
                                    mQuality_1);
                            needHoldup = true;
                        } else if (count == 2) {
                            fpFeatureData2 = largestFingerDataList.get(1).getFpFeatureData();
                            mQuality_2 = largestFingerDataList.get(1).getImgQuality();
                            mImageData_2 = largestFingerDataList.get(1).getFpImageData();
                            msg = "2st feature extracted success(" + count + "/3)";
                            updateFingerprintImage_2(largestFingerData.getFpImage_bitmap(),
                                    mQuality_2);
                            needHoldup = true;
                        } else if (count == 3) {
                            fpFeatureData3 = largestFingerDataList.get(2).getFpFeatureData();
                            mQuality_3 = largestFingerDataList.get(2).getImgQuality();
                            mImageData_3 = largestFingerDataList.get(2).getFpImageData();
                            msg = "3rd feature extracted success(" + count + "/3)";
                            updateFingerprintImage_3(largestFingerData.getFpImage_bitmap(),
                                    mQuality_3);
                        }
                        largestFingerData = null;
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_ENROLL_PROGRESS, count));
                        handleMsg(msg, Color.BLACK);
                        if (largestFingerDataList.size() == 3) {
                            break;
                        }
                    }
                } catch (TrustFingerException e) {
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_ENROLL_FAIL, "Enroll " +
                            "exception:" + e.getType().toString()));
                    handleMsg("Enroll exception:" + e.getType().toString(), Color.RED);
                    e.printStackTrace();
                }
            } while (true);
            callback.setLedEnable(true);
            callback.setLfdEnable(true);
            if (mApp.isLedEnable()) {
                ledOff();
            }
            if (fpFeatureData1 != null && fpFeatureData2 != null && fpFeatureData3 != null) {
                boolean isSameFinger = isSameFinger(fpFeatureData1, fpFeatureData2, fpFeatureData3);
                if (!isSameFinger) {
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_ENROLL_FAIL, "Generalize " +
                            "template failed: not the same finger"));
                    handleMsg("Generalize template failed: not the same finger", Color.RED);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            enbleSettingsView(true);
                        }
                    });
                    mEnrollTask = null;
                    isEnrolling = false;
                    mIsDone = true;
                    return null;
                }
                if (checkFingerIsExist(fpFeatureData1) || checkFingerIsExist(fpFeatureData2) ||
                        checkFingerIsExist(fpFeatureData3)) {
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_ENROLL_FAIL, "Generalize " +
                            "template failed: fingerprint already exists"));
                    handleMsg("Generalize template failed: fingerprint already exists", Color.RED);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            enbleSettingsView(true);
                        }
                    });
                    mEnrollTask = null;
                    isEnrolling = false;
                    mIsDone = true;
                    return null;
                }
                try {
                    templateData = mTrustFingerDevice.generalizeTemplate(fpFeatureData1,
                            fpFeatureData2, fpFeatureData3);
                    if (templateData != null) {
                        final boolean isSuccess = enrollFingerprint(templateData);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                enbleSettingsView(true);
                                if (isSuccess) {
                                    mHandler.sendMessageDelayed(mHandler.obtainMessage
                                            (MSG_ENROLL_SUCCESS, "Enroll succeed!"), 200);
                                    handleMsg("Enroll succeed!", Color.BLACK);
                                } else {
                                    mHandler.sendMessageDelayed(mHandler.obtainMessage
                                            (MSG_ENROLL_FAIL, "Reached the maximum capacity of database!"), 200);
                                    handleMsg("Reached the maximum capacity of database", Color.RED);
                                }
                                if (currentFingerView != null) {
                                    currentFingerView.setSelected(false);
                                    currentFingerView = null;
                                    mFingerPosition = null;
                                }
                                if (mTrustFingerDevice != null) {
                                    mTextView_current_position.setText(getString(R.string
                                            .msg_select_a_finger));
                                    mTextView_current_position.setTextColor(Color.RED);
                                } else {
                                    mTextView_current_position.setText("");
                                }
                                //                                currentFingerView.setEnrolled
                                // (true);
                            }
                        });
                    } else {
                        handleMsg("Generalize template failed!", Color.RED);
                    }
                } catch (TrustFingerException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            enbleSettingsView(true);
                        }
                    });
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_ENROLL_FAIL, "Enroll " +
                            "exception: " + e.getType().toString()));
                    handleMsg("Enroll exception: " + e.getType().toString(), Color.RED);
                    e.printStackTrace();
                    mEnrollTask = null;
                    isEnrolling = false;
                    mIsDone = true;
                    return null;
                }

            }
            mEnrollTask = null;
            isEnrolling = false;
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

    /**
     * 检测手指是否存在
     *
     * @param fpFeatureData
     * @return
     */
    private boolean checkFingerIsExist(byte[] fpFeatureData) {
        List<User> userList = mDBHelper.getUserList();
        byte[] template = null;
        FingerData fingerData;
        for (User user : userList) {
            fingerData = user.getFingerData();
            if (fingerData == null) {
                continue;
            }
            for (String position : fingerData.getFingerPositions()) {
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
                if (template != null) {
                    if (mTrustFingerDevice == null) {
                        return false;
                    }
                    VerifyResult result = mTrustFingerDevice.verify(SecurityLevel.Level4,
                            template, fpFeatureData);
                    if (result.error == 0) {
                        if (result.isMatched) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isSameFinger(byte[] fpFeatureData1, byte[] fpFeatureData2, byte[]
            fpFeatureData3) {
        VerifyResult result = null;
        try {
            result = mTrustFingerDevice.verify(SecurityLevel.Level4, fpFeatureData1,
                    fpFeatureData2);
            if (result.error == 0 && result.isMatched) {
                result = mTrustFingerDevice.verify(SecurityLevel.Level4, fpFeatureData1,
                        fpFeatureData3);
                if (result.error == 0 && result.isMatched) {
                    result = mTrustFingerDevice.verify(SecurityLevel.Level4, fpFeatureData2,
                            fpFeatureData3);
                    if (result.error == 0 && result.isMatched) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
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

    private boolean enrollFingerprint(byte[] templateData) {
        User user = new User();
        String mUserID = mEditText_user_id.getText().toString().trim();
        String mUserFirstName = mEditText_user_first_name.getText().toString().trim();
        String mUserLastName = mEditText_user_last_name.getText().toString().trim();
        FingerData fingerData = new FingerData();
        fingerData.getTemplates().add(templateData);
        switch (mFingerPosition) {
            case LeftLittleFinger:
                fingerData.setLeft_little_image_1(mImageData_1);
                fingerData.setLeft_little_image_2(mImageData_2);
                fingerData.setLeft_little_image_3(mImageData_3);
                fingerData.setLeft_little_quality_1(mQuality_1);
                fingerData.setLeft_little_quality_2(mQuality_2);
                fingerData.setLeft_little_quality_3(mQuality_3);
                fingerData.setLeft_little_template(templateData);
                fingerData.getFingerPositions().add(mFingerPosition.name());
                break;
            case LeftRingFinger:
                fingerData.setLeft_ring_image_1(mImageData_1);
                fingerData.setLeft_ring_image_2(mImageData_2);
                fingerData.setLeft_ring_image_3(mImageData_3);
                fingerData.setLeft_ring_quality_1(mQuality_1);
                fingerData.setLeft_ring_quality_2(mQuality_2);
                fingerData.setLeft_ring_quality_3(mQuality_3);
                fingerData.setLeft_ring_template(templateData);
                fingerData.getFingerPositions().add(mFingerPosition.name());
                break;
            case LeftMiddleFinger:
                fingerData.setLeft_middle_image_1(mImageData_1);
                fingerData.setLeft_middle_image_2(mImageData_2);
                fingerData.setLeft_middle_image_3(mImageData_3);
                fingerData.setLeft_middle_quality_1(mQuality_1);
                fingerData.setLeft_middle_quality_2(mQuality_2);
                fingerData.setLeft_middle_quality_3(mQuality_3);
                fingerData.setLeft_middle_template(templateData);
                fingerData.getFingerPositions().add(mFingerPosition.name());
                break;
            case LeftIndexFinger:
                fingerData.setLeft_index_image_1(mImageData_1);
                fingerData.setLeft_index_image_2(mImageData_2);
                fingerData.setLeft_index_image_3(mImageData_3);
                fingerData.setLeft_index_quality_1(mQuality_1);
                fingerData.setLeft_index_quality_2(mQuality_2);
                fingerData.setLeft_index_quality_3(mQuality_3);
                fingerData.setLeft_index_template(templateData);
                fingerData.getFingerPositions().add(mFingerPosition.name());
                break;
            case LeftThumb:
                fingerData.setLeft_thumb_image_1(mImageData_1);
                fingerData.setLeft_thumb_image_2(mImageData_2);
                fingerData.setLeft_thumb_image_3(mImageData_3);
                fingerData.setLeft_thumb_quality_1(mQuality_1);
                fingerData.setLeft_thumb_quality_2(mQuality_2);
                fingerData.setLeft_thumb_quality_3(mQuality_3);
                fingerData.setLeft_thumb_template(templateData);
                fingerData.getFingerPositions().add(mFingerPosition.name());
                break;

            case RightLittleFinger:
                fingerData.setRight_little_image_1(mImageData_1);
                fingerData.setRight_little_image_2(mImageData_2);
                fingerData.setRight_little_image_3(mImageData_3);
                fingerData.setRight_little_quality_1(mQuality_1);
                fingerData.setRight_little_quality_2(mQuality_2);
                fingerData.setRight_little_quality_3(mQuality_3);
                fingerData.setRight_little_template(templateData);
                fingerData.getFingerPositions().add(mFingerPosition.name());
                break;
            case RightRingFinger:
                fingerData.setRight_ring_image_1(mImageData_1);
                fingerData.setRight_ring_image_2(mImageData_2);
                fingerData.setRight_ring_image_3(mImageData_3);
                fingerData.setRight_ring_quality_1(mQuality_1);
                fingerData.setRight_ring_quality_2(mQuality_2);
                fingerData.setRight_ring_quality_3(mQuality_3);
                fingerData.setRight_ring_template(templateData);
                fingerData.getFingerPositions().add(mFingerPosition.name());
                break;
            case RightMiddleFinger:
                fingerData.setRight_middle_image_1(mImageData_1);
                fingerData.setRight_middle_image_2(mImageData_2);
                fingerData.setRight_middle_image_3(mImageData_3);
                fingerData.setRight_middle_quality_1(mQuality_1);
                fingerData.setRight_middle_quality_2(mQuality_2);
                fingerData.setRight_middle_quality_3(mQuality_3);
                fingerData.setRight_middle_template(templateData);
                fingerData.getFingerPositions().add(mFingerPosition.name());
                break;
            case RightIndexFinger:
                fingerData.setRight_index_image_1(mImageData_1);
                fingerData.setRight_index_image_2(mImageData_2);
                fingerData.setRight_index_image_3(mImageData_3);
                fingerData.setRight_index_quality_1(mQuality_1);
                fingerData.setRight_index_quality_2(mQuality_2);
                fingerData.setRight_index_quality_3(mQuality_3);
                fingerData.setRight_index_template(templateData);
                fingerData.getFingerPositions().add(mFingerPosition.name());
                break;
            case RightThumb:
                fingerData.setRight_thumb_image_1(mImageData_1);
                fingerData.setRight_thumb_image_2(mImageData_2);
                fingerData.setRight_thumb_image_3(mImageData_3);
                fingerData.setRight_thumb_quality_1(mQuality_1);
                fingerData.setRight_thumb_quality_2(mQuality_2);
                fingerData.setRight_thumb_quality_3(mQuality_3);
                fingerData.setRight_thumb_template(templateData);
                fingerData.getFingerPositions().add(mFingerPosition.name());
                break;
        }

        user.setId(mUserID);
        user.setFirstName(mUserFirstName);
        user.setLastName(mUserLastName);
        user.setFingerData(fingerData);
        if (mDBHelper.getUserList().size() >= 100) {
            return false;
        }
        if (mDBHelper.insertUser(user)) {
            //            getActivity().runOnUiThread(new Runnable() {
            //                @Override
            //                public void run() {
            //                    currentFingerView.setEnrolled(true);
            //                }
            //            });
            loadEnrolledUsers();
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

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
                    if (currentFingerView != null) {
                        currentFingerView.setSelected(false);
                        currentFingerView = null;
                        mFingerPosition = null;
                    }
                    mImageView_fingerprint_1.setImageDrawable(getResources().getDrawable(R
                            .drawable.nofinger));
                    mImageView_fingerprint_2.setImageDrawable(getResources().getDrawable(R
                            .drawable.nofinger));
                    mImageView_fingerprint_3.setImageDrawable(getResources().getDrawable(R
                            .drawable.nofinger));
                    mTextView_image_quality_1.setText("");
                    mTextView_image_quality_2.setText("");
                    mTextView_image_quality_3.setText("");
                    mEditText_user_id.setText("");
                    mEditText_user_first_name.setText("");
                    mEditText_user_last_name.setText("");
                    if (mTrustFingerDevice != null) {
                        mTextView_current_position.setText(getString(R.string.msg_select_a_finger));
                        mTextView_current_position.setTextColor(Color.RED);
                    } else {
                        mTextView_current_position.setText("");
                    }
                    mImageView_fingerprint.setImageBitmap(null);
                    mProgressBar_image_quality.setProgress(0);
                    mImageView_tips_image.setImageDrawable(null);
                    mTextView_tips_msg.setText("");
                    mTextView_image_quality.setText("");
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
                    if (mDBHelper != null) {
                        mUserList.clear();
                        mUserList.addAll(mDBHelper.getUserList());
                    }
                    Collections.reverse(mUserList);
                    myListAdapter.notifyDataSetChanged();
                    break;
                case MSG_ENROLL_SUCCESS: {
                    String s = (String) msg.obj;
                    mImageView_tips_image.setImageDrawable(getResources().getDrawable(R.drawable
                            .success));
                    mTextView_tips_msg.setText(s);
                    mEditText_user_first_name.setText("");
                    mEditText_user_last_name.setText("");
                    mEditText_user_id.setText("");

                    break;
                }
                case MSG_ENROLL_FAIL: {
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
                    String s = (String) msg.obj;
                    mImageView_tips_image.setImageDrawable(getResources().getDrawable(R.drawable
                            .fail));
                    mTextView_tips_msg.setText(s);
                    //                    sv.fullScroll(ScrollView.FOCUS_UP);
                    break;
                }
                case MSG_ENROLL_WARNING: {
                    String s = (String) msg.obj;
                    mImageView_tips_image.setImageDrawable(getResources().getDrawable(R.drawable
                            .warning));
                    mTextView_tips_msg.setText(s);
                    break;
                }
                case MSG_ENROLL_PROGRESS: {
                    int progress = (Integer) msg.obj;
                    if (progress == 1) {
                        mImageView_tips_image.setImageDrawable(getResources().getDrawable(R
                                .drawable.progress_1));
                        mTextView_tips_msg.setText("1st feature extracted success(" + progress +
                                "/3)");
                    } else if (progress == 2) {
                        mImageView_tips_image.setImageDrawable(getResources().getDrawable(R
                                .drawable.progress_2));
                        mTextView_tips_msg.setText("2nd feature extracted success(" + progress +
                                "/3)");
                    } else if (progress == 3) {
                        mImageView_tips_image.setImageDrawable(getResources().getDrawable(R
                                .drawable.progress_3));
                        mTextView_tips_msg.setText("3rd feature extracted success(" + progress +
                                "/3)");
                    }
                    break;
                }
            }
        }
    };
}