package com.aratek.trustfinger;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aratek.trustfinger.adapter.MyPagerAdapter;
import com.aratek.trustfinger.fragment.CaptureFragment;
import com.aratek.trustfinger.fragment.DeviceInfoFragment;
import com.aratek.trustfinger.fragment.EnrollFragment;
import com.aratek.trustfinger.fragment.IdentifyFragment;
import com.aratek.trustfinger.fragment.VerifyFragment;
import com.aratek.trustfinger.interfaces.ViewStatusCallback;
import com.aratek.trustfinger.sdk.DeviceListener;
import com.aratek.trustfinger.sdk.DeviceOpenListener;
import com.aratek.trustfinger.sdk.LedIndex;
import com.aratek.trustfinger.sdk.LedStatus;
import com.aratek.trustfinger.sdk.LfdLevel;
import com.aratek.trustfinger.sdk.TrustFinger;
import com.aratek.trustfinger.sdk.TrustFingerDevice;
import com.aratek.trustfinger.sdk.TrustFingerException;
import com.aratek.trustfinger.utils.DeviceType;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends FragmentActivity implements DeviceOpenListener, ViewStatusCallback {
    private static final String TAG = "MainActivity";
    private static final String ACTION_USB_PERMISSION = "com.aratek.trustfinger.USB_PERMISSION";
    private final int requestPermissionsCode = 100;
    private Spinner mSpinner_deviceType;
    private Spinner mSpinner_usbDevice;
    private Button mButton_openClose;
    private CheckBox mCheckBox_enableLed;
    private TextView mTextView_lfd_level;
    private Spinner mSpinner_antiSpoofing_level;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TextView mTextView_msg;
    private TextView mTextView_version;

    private TrustFinger mTrustFinger;
    protected TrustFingerDevice mTrustFingerDevice;
    private ArrayAdapter<String> adapter_usbDevice;
    private List<String> sp_usbDevice_datas_all = new ArrayList<String>();
    private List<String> sp_usbDevice_datas = new ArrayList<String>();
    private List<Fragment> fragmnts = new ArrayList<Fragment>();
    private CaptureFragment mCaptureFragment;
    private EnrollFragment mEnrollFragment;
    private VerifyFragment mVerifyFragment;
    private IdentifyFragment mIdentifyFragment;
    //    private OneToN_IdentifyFragment mOneToN_IdentifyFragment;
    private DeviceInfoFragment mDeviceInfoFragment;
    private String[] titles;
    private Handler handler = new Handler();
    private int mDeviceId = 0;
    private boolean isDeviceOpened = false;
    private MyApplication mApp;

    private EditText etChangeIndex;
    private TextView tvIndexMsg;
    private LinkedHashMap<Integer, String> mDevIndexList = new LinkedHashMap<>();
    //    private List<int[]> mDevIndexList = new ArrayList<>();
    private int[] currentDevIndex = new int[2];
    private final boolean isNeedPowerOn = false;
    private PackageInfo packageInfo;

    private final int FLD_LEVEL = LfdLevel.OFF;

    //Open mosip App change
    private Button btOpenMosip ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isNeedPowerOn) {
            setUrovoPowerOn("1", CMD_POWER_CONTROL_NODE);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        appVersion();
        findViews();
        mApp = (MyApplication) getApplication();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions();
        } else {
            run();
        }
    }

    private void run() {
        initDatas();
        File file = new File(Config.COMMON_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        mApp.put(Config.FEATURE_PATH, Config.COMMON_PATH + File.separator + "AratekTrustFinger" + File.separator +
                "FingerData");
    }


    private void requestPermissions() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Config.COMMON_PATH = getExternalFilesDir("").getAbsolutePath();
            } else {
                Config.COMMON_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, requestPermissionsCode);
            } else {
                run();
            }
        } else {
            Config.COMMON_PATH = getExternalFilesDir("").getAbsolutePath();
            run();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == requestPermissionsCode) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(MainActivity.this, "Permissions denied!", Toast.LENGTH_SHORT).show();
            }
            run();
        }

    }

    private void appVersion() {
        try {
            PackageManager packageManager = getPackageManager();
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


    private void findViews() {
        mSpinner_deviceType = findViewById(R.id.sp_device_type);
        mSpinner_usbDevice = findViewById(R.id.sp_usb_device);
        mButton_openClose = findViewById(R.id.btn_open_device);
        mCheckBox_enableLed = findViewById(R.id.chk_enabled_led);
        mTextView_lfd_level = findViewById(R.id.tv_lfd_level);
        mSpinner_antiSpoofing_level = findViewById(R.id.sp_spoofing_level);
        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewPager);
        mTextView_msg = findViewById(R.id.tv_msg);
        mTextView_version = findViewById(R.id.tv_version);

        etChangeIndex = findViewById(R.id.etChangeIndex);
        tvIndexMsg = findViewById(R.id.tvIndexMsg);
        //Open mosip App change
        btOpenMosip = findViewById(R.id.btOpenMosip);
        btOpenMosip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReactNativeApp("io.mosip.residentapp");

            }
        });
    }

    private void openReactNativeApp(String packageName) {
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent != null) {
                intent.setAction("io.mosip.residentapp.odk.REQUEST");
                intent.putExtra("biometricAuth", true);
                startActivity(intent);
            } else {
                Log.e(TAG, "Not Found packageName : "+packageName);
                // App not found, handle error or prompt user to install the app
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void initTrustFinger() {
        try {
            mTrustFinger = TrustFinger.getInstance(this.getApplicationContext());
            if (packageInfo != null) {
                mTextView_version.setText("Version: " + packageInfo.versionName + "." + packageInfo.versionCode);
            } else {
                mTextView_version.setText(mTrustFinger.getSdkJarVersion());
            }
            mTrustFinger.initialize();
            mTrustFinger.setDeviceListener(new DeviceListener() {
                @Override
                public void deviceAttached(List<String> deviceList) {
                    sp_usbDevice_datas_all.clear();
                    for (int i = 0; i < deviceList.size(); i++) {
                        sp_usbDevice_datas_all.add(i + "-" + deviceList.get(i));
                    }
                    sp_usbDevice_datas.clear();
                    sp_usbDevice_datas.add(getString(R.string.sp_usb_device_auto));//show default
                    // value
                    sp_usbDevice_datas.addAll(sp_usbDevice_datas_all);
                    mSpinner_deviceType.setSelection(0);
                    mSpinner_usbDevice.setSelection(0);
                    adapter_usbDevice.notifyDataSetChanged();
                    handleMsg("Device atached!", Color.BLACK);
                }

                @Override
                public void deviceDetached(List<String> deviceList) {
                    sp_usbDevice_datas_all.clear();
                    for (int i = 0; i < deviceList.size(); i++) {
                        sp_usbDevice_datas_all.add(i + "-" + deviceList.get(i));
                    }
                    sp_usbDevice_datas.clear();
                    sp_usbDevice_datas.add(getString(R.string.sp_usb_device_auto));//show default
                    // value
                    sp_usbDevice_datas.addAll(sp_usbDevice_datas_all);
                    adapter_usbDevice.notifyDataSetChanged();
                    mSpinner_deviceType.setSelection(0);
                    mSpinner_usbDevice.setSelection(0);
                    isDeviceOpened = false;
                    mButton_openClose.setTextColor(Color.parseColor("#1D9F9A"));
                    mButton_openClose.setText(getString(R.string.btn_open_device));
                    setFragmentDatas(null);
                    mSpinner_deviceType.setEnabled(true);
                    mSpinner_usbDevice.setEnabled(true);
                    //                mCheckBox_enableLed.setEnabled(true);
                    handleMsg("Device detached!", Color.RED);
                }
            });
            if (mTrustFinger.getDeviceCount() <= 0) {
                showAlertDialog(false, "No fingerprint device detected!");
            }
        } catch (TrustFingerException e) {
            handleMsg("TrustFinger getInstance Exception: " + e.getType().toString() + "", Color.RED);
            if (e.getType().toString().equals("DEVICE_NOT_FOUND")) {
                showAlertDialog(true, "No fingerprint device detected!");
            }
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            showAlertDialog(true, "The system does not support simultaneous access to two devices" + ".");
        }

    }

    /**
     * 经测试发现由于setSelection（）函数不会实时回调onItemSelected方法，目前原因未找到，所以部分地方采用自定义模拟的监听器
     */
    private void setDeviceTypeSelection(int position) {
        int currentPosition = mSpinner_deviceType.getSelectedItemPosition();
        mSpinner_deviceType.setSelection(position);
        if (position != currentPosition) {
            onItemChange(position);
        }
    }

    private void onItemChange(int position) {
        List<String> deviceList = mTrustFinger.getDeviceList();
        sp_usbDevice_datas_all.clear();
        for (int i = 0; i < deviceList.size(); i++) {
            sp_usbDevice_datas_all.add(i + "-" + deviceList.get(i));
        }
        DeviceType deviceType = DeviceType.values()[position];
        sp_usbDevice_datas.clear();
        sp_usbDevice_datas.add(getString(R.string.sp_usb_device_auto));//set default value
        if (deviceType.equals(DeviceType.AUTO)) {
            sp_usbDevice_datas.addAll(sp_usbDevice_datas_all);
            mSpinner_usbDevice.setSelection(0);
        } else if (deviceType.equals(DeviceType.A400)) {
            for (String sp_usbDevice_data : sp_usbDevice_datas_all) {
                if (sp_usbDevice_data.contains(getString(R.string.sp_device_type_a400))) {
                    sp_usbDevice_datas.add(sp_usbDevice_data);
                }
            }
            if (sp_usbDevice_datas.size() > 1) {
                mSpinner_usbDevice.setSelection(1);
            }
        } else if (deviceType.equals(DeviceType.A600)) {
            for (String sp_usbDevice_data : sp_usbDevice_datas_all) {
                if (sp_usbDevice_data.contains(getString(R.string.sp_device_type_a600))) {
                    sp_usbDevice_datas.add(sp_usbDevice_data);
                }
            }
            if (sp_usbDevice_datas.size() > 1) {
                mSpinner_usbDevice.setSelection(1);
            }
        } else if (deviceType.equals(DeviceType.EM4010)) {
            for (String sp_usbDevice_data : sp_usbDevice_datas_all) {
                if (sp_usbDevice_data.contains(getString(R.string.sp_device_type_em4010))) {
                    sp_usbDevice_datas.add(sp_usbDevice_data);
                }
            }
            if (sp_usbDevice_datas.size() > 1) {
                mSpinner_usbDevice.setSelection(1);
            }
        } else if (deviceType.equals(DeviceType.EM3011)) {
            for (String sp_usbDevice_data : sp_usbDevice_datas_all) {
                if (sp_usbDevice_data.contains(getString(R.string.sp_device_type_em3011))) {
                    sp_usbDevice_datas.add(sp_usbDevice_data);
                }
            }
            if (sp_usbDevice_datas.size() > 1) {
                mSpinner_usbDevice.setSelection(1);
            }
        } else if (deviceType.equals(DeviceType.EM2010)) {
            for (String sp_usbDevice_data : sp_usbDevice_datas_all) {
                if (sp_usbDevice_data.contains(getString(R.string.sp_device_type_em2010))) {
                    sp_usbDevice_datas.add(sp_usbDevice_data);
                }
            }
            if (sp_usbDevice_datas.size() > 1) {
                mSpinner_usbDevice.setSelection(1);
            }
        } else if (deviceType.equals(DeviceType.FRT610A)) {
            for (String sp_usbDevice_data : sp_usbDevice_datas_all) {

                if (sp_usbDevice_data.contains(getString(R.string.sp_device_type_frt610))) {
                    sp_usbDevice_datas.add(sp_usbDevice_data);
                }
            }
            if (sp_usbDevice_datas.size() > 1) {
                mSpinner_usbDevice.setSelection(1);
            }
        } else if (deviceType.equals(DeviceType.EM03_4010)) {
            for (String sp_usbDevice_data : sp_usbDevice_datas_all) {
                if (sp_usbDevice_data.contains(getString(R.string.sp_device_type_em03_4010))) {
                    sp_usbDevice_datas.add(sp_usbDevice_data);
                }
            }
            if (sp_usbDevice_datas.size() > 1) {
                mSpinner_usbDevice.setSelection(1);
            }
        } else if (deviceType.equals(DeviceType.EM03_3011)) {
            for (String sp_usbDevice_data : sp_usbDevice_datas_all) {
                if (sp_usbDevice_data.contains(getString(R.string.sp_device_type_em03_3011))) {
                    sp_usbDevice_datas.add(sp_usbDevice_data);
                }
            }
            if (sp_usbDevice_datas.size() > 1) {
                mSpinner_usbDevice.setSelection(1);
            }
        } else if (deviceType.equals(DeviceType.EM1920)) {
            for (String sp_usbDevice_data : sp_usbDevice_datas_all) {
                if (sp_usbDevice_data.contains(getString(R.string.sp_device_type_em1920))) {
                    sp_usbDevice_datas.add(sp_usbDevice_data);
                }
            }
            if (sp_usbDevice_datas.size() > 1) {
                mSpinner_usbDevice.setSelection(1);
            }
        } else if (deviceType.equals(DeviceType.A620)) {
            for (String sp_usbDevice_data : sp_usbDevice_datas_all) {
                if (sp_usbDevice_data.contains(getString(R.string.sp_device_type_a620))) {
                    sp_usbDevice_datas.add(sp_usbDevice_data);
                }
            }
            if (sp_usbDevice_datas.size() > 1) {
                mSpinner_usbDevice.setSelection(1);
            }
        }
        adapter_usbDevice.notifyDataSetChanged();
    }

    private void initDatas() {
        ArrayAdapter arrayAdapter_dev_type = ArrayAdapter.createFromResource(this, R.array.device_type, R.layout.spinner_list_item);
        arrayAdapter_dev_type.setDropDownViewResource(R.layout.spinner_list_item);
        mSpinner_deviceType.setAdapter(arrayAdapter_dev_type);
        mSpinner_deviceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onItemChange(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        adapter_usbDevice = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_list_item, sp_usbDevice_datas);
        adapter_usbDevice.setDropDownViewResource(R.layout.spinner_list_item);
        mSpinner_usbDevice.setAdapter(adapter_usbDevice);
        mSpinner_usbDevice.setSelection(0);
        mSpinner_usbDevice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tag = parent.getItemAtPosition(position).toString();
                if (getString(R.string.sp_usb_device_auto).equals(tag)) {
                    mDeviceId = 0;
                    return;
                }
                String[] strs = tag.split("-");
                if (strs != null && strs.length > 0) {
                    mDeviceId = Integer.parseInt(strs[0]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mDeviceId = 0;
            }
        });
        mSpinner_antiSpoofing_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String level = parent.getItemAtPosition(position).toString();
                switch (level) {
                    case "OFF": {
                        if (mTrustFingerDevice != null) {
                            updateLFDLevel(LfdLevel.OFF);
                        }
                        break;
                    }
                    case "EXTRA_LOW": {
                        updateLFDLevel(LfdLevel.EXTRA_LOW);
                        break;
                    }
                    case "LOW": {
                        updateLFDLevel(LfdLevel.LOW);
                        break;
                    }
                    case "MEDIUM": {
                        updateLFDLevel(LfdLevel.MEDIUM);
                        break;
                    }
                    case "HIGH": {
                        updateLFDLevel(LfdLevel.HIGH);
                        break;
                    }
                    case "ULTRA_HIGH": {
                        updateLFDLevel(LfdLevel.ULTRA_HIGH);
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpinner_antiSpoofing_level.setEnabled(false);
        mTextView_lfd_level.setEnabled(false);
        mCheckBox_enableLed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mApp.setLedEnable(true);
                    handler.removeCallbacks(openLedTask);
                    handler.post(openLedTask);
                } else {
                    mApp.setLedEnable(false);
                    handler.removeCallbacks(closeLedTask);
                    handler.post(closeLedTask);
                }
            }
        });
        isDeviceOpened = false;
        mButton_openClose.setText(getString(R.string.btn_open_device));
        mButton_openClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButton_openClose.setEnabled(false);
                if (!isDeviceOpened) {
                    if (mDeviceId == -1) {
                        mButton_openClose.setEnabled(true);
                        return;
                    }
                    Object selectedItem = mSpinner_usbDevice.getSelectedItem();
                    String tag = getString(R.string.sp_usb_device_auto);
                    if (selectedItem != null) {
                        tag = mSpinner_usbDevice.getSelectedItem().toString();
                    }
                    if (getString(R.string.sp_usb_device_auto).equals(tag)) {
                        mDeviceId = 0;
                    } else {
                        String[] strs = tag.split("-");
                        if (strs != null && strs.length > 0) {
                            mDeviceId = Integer.parseInt(strs[0]);
                        }
                    }
                    try {
                        currentDevIndex[0] = mDeviceId;
                        mTrustFinger.openDevice(mDeviceId, MainActivity.this);
                    } catch (TrustFingerException e) {
                        mButton_openClose.setEnabled(true);
                        handleMsg("Device open exception:" + e.getType().toString() + "", Color.RED);
                        e.printStackTrace();
                    }
                } else {
                    int position = mViewPager.getCurrentItem();
                    if (position == 0) {
                        mCaptureFragment.forceStop();
                        mCaptureFragment.resetUI();
                    }
                    if (position == 1) {
                        mEnrollFragment.forceStop();
                        mEnrollFragment.resetUI();
                    }
                    if (position == 2) {
                        mVerifyFragment.forceStop();
                        mVerifyFragment.resetUI();
                    }
                    if (position == 3) {
                        mIdentifyFragment.forceStop();
                        mIdentifyFragment.resetUI();
                    }
//                    if (position == 4) {
//                        mOneToN_IdentifyFragment.forceStop();
//                        mOneToN_IdentifyFragment.resetUI();
//                    }
                    try {
                        mButton_openClose.setTextColor(Color.parseColor("#1D9F9A"));
//                        mTrustFingerDevice.closeAll();
                        handler.removeCallbacks(closeLedTask);
                        handler.post(closeLedTask);
                        mTrustFinger.closeAllDev();
                        mButton_openClose.setText(getString(R.string.btn_open_device));
                        mButton_openClose.setEnabled(true);
                        mSpinner_deviceType.setEnabled(true);
                        mSpinner_usbDevice.setEnabled(true);
                        mCheckBox_enableLed.setEnabled(false);
                        mTextView_lfd_level.setEnabled(false);
                        mSpinner_antiSpoofing_level.setEnabled(false);
                        setFragmentDatas(null);
                        isDeviceOpened = false;
                        mTrustFingerDevice = null;
                        handleMsg("Device closed!", Color.RED);
                    } catch (TrustFingerException e) {
                        mButton_openClose.setTextColor(Color.parseColor("#1D9F9A"));
                        mButton_openClose.setEnabled(true);
                        handleMsg("Device close exception : " + e.getType().toString() + "", Color.RED);
                        e.printStackTrace();
                    }
                }
            }
        });
        mCaptureFragment = new CaptureFragment();
        mCaptureFragment.setLedCallback(this);
        mEnrollFragment = new EnrollFragment();
        mEnrollFragment.setLedCallback(this);
        mVerifyFragment = new VerifyFragment();
        mVerifyFragment.setLedCallback(this);
        mIdentifyFragment = new IdentifyFragment();
        mIdentifyFragment.setLedCallback(this);
//        mOneToN_IdentifyFragment = new OneToN_IdentifyFragment();
//        mOneToN_IdentifyFragment.setLedCallback(this);
        mDeviceInfoFragment = new DeviceInfoFragment();
        fragmnts.add(mCaptureFragment);
        fragmnts.add(mEnrollFragment);
        fragmnts.add(mVerifyFragment);
        fragmnts.add(mIdentifyFragment);
//        fragmnts.add(mOneToN_IdentifyFragment);
        fragmnts.add(mDeviceInfoFragment);
        titles = getResources().getStringArray(R.array.tabs_name);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), fragmnts, titles));
        mViewPager.setOffscreenPageLimit(5);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(), false);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                View view = getCurrentFocus();
                if (view != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                handleMsg("", Color.BLACK);
                if (position != 0) {
                    mCaptureFragment.forceStop();
                    mCaptureFragment.resetUI();
                }
                if (position != 1) {
                    mEnrollFragment.forceStop();
                    mEnrollFragment.resetUI();
                }
                if (position != 2) {
                    mVerifyFragment.forceStop();
                    mVerifyFragment.resetUI();
                }
                if (position != 3) {
                    mIdentifyFragment.forceStop();
                    mIdentifyFragment.resetUI();
                }
//                if (position != 4) {
//                    mOneToN_IdentifyFragment.forceStop();
//                    mOneToN_IdentifyFragment.resetUI();
//                }
                if (position == 0) {
                    if (isDeviceOpened) {
                        handleMsg("Confirm the settings and select a finger for capture", Color.RED);
                    } else {
                        handleMsg(getString(R.string.msg_click_open_device_button), Color.BLACK);
                    }

                } else if (position == 1) {
                    if (isDeviceOpened) {
                        handleMsg("Fill your info and select a finger for enrollment", Color.RED);
                    } else {
                        handleMsg(getString(R.string.msg_click_open_device_button), Color.BLACK);
                    }
                    mVerifyFragment.loadEnrolledUsers();
                } else if (position == 2) {
                    if (isDeviceOpened) {
                        handleMsg("Select a user for verification", Color.RED);
                    } else {
                        handleMsg(getString(R.string.msg_click_open_device_button), Color.BLACK);
                    }
                    mVerifyFragment.loadEnrolledUsers();
                } else if (position == 3) {
                    if (isDeviceOpened) {
                        handleMsg("Confirm the settings and press Start Identify button", Color.RED);
                    } else {
                        handleMsg(getString(R.string.msg_click_open_device_button), Color.BLACK);
                    }
                } else if (position == 4) {
                    if (isDeviceOpened) {
                        handleMsg("Device opened", Color.BLACK);
                    } else {
                        handleMsg(getString(R.string.msg_click_open_device_button), Color.BLACK);
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void updateLFDLevel(int level) {
        if (mTrustFingerDevice != null) {
            try {
                mTrustFingerDevice.setLfdLevel(level);
            } catch (TrustFingerException e) {
                handleMsg("enableLFD fail", Color.RED);
            }
        }
    }

    private void showAlertDialog(final boolean isError, String msg) throws TrustFingerException {
//        MediaPlayerHelper.payMedia(this, R.raw.no_fingerprint_device_detected);
//        new AlertDialog.Builder(this).setCancelable(false).setTitle("Error")//设置对话框的标题
//                .setMessage(msg)//设置对话框的内容
//                //设置对话框的按钮
//                .setNeutralButton("Redetect", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        if (isError) {
//                            initTrustFinger();
//                        } else {
//                            showAlertDialog(true, "No fingerprint device detected!");
//                        }
//                    }
//                }).setPositiveButton("Exit", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        finish();
//                    }
//                }).create().show();
    }

    private void setFragmentDatas(TrustFingerDevice mTrustFingerDevice) {
        if (mCaptureFragment != null) mCaptureFragment.setDatas(mTrustFingerDevice);
        if (mEnrollFragment != null) mEnrollFragment.setDatas(mTrustFingerDevice);
        if (mVerifyFragment != null) mVerifyFragment.setDatas(mTrustFingerDevice);
        if (mIdentifyFragment != null) mIdentifyFragment.setDatas(mTrustFingerDevice);
//        if (mOneToN_IdentifyFragment != null)
//            mOneToN_IdentifyFragment.setDatas(mTrustFingerDevice);
        if (mDeviceInfoFragment != null) mDeviceInfoFragment.setDatas(mTrustFingerDevice);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initTrustFinger();
        if (mTrustFinger != null) {
            refreshDeviceList();
            if (!isDeviceOpened) {
                handleMsg(getString(R.string.msg_click_open_device_button), Color.BLACK);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTrustFinger != null) {
            mTrustFinger.release();
        }
    }

    private void refreshDeviceList() {
        List<String> deviceList = mTrustFinger.getDeviceList();
        sp_usbDevice_datas_all.clear();
        for (int i = 0; i < deviceList.size(); i++) {
            sp_usbDevice_datas_all.add(i + "-" + deviceList.get(i));
        }
        int selectedItemPosition = mSpinner_usbDevice.getSelectedItemPosition();
        if (sp_usbDevice_datas_all.size() > selectedItemPosition && selectedItemPosition >= 0) {
            onItemChange(selectedItemPosition);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTrustFingerDevice != null) {
            mTrustFingerDevice.closeAll();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler.post(closeLedTask);
            handler = null;
        }
        if (isNeedPowerOn) {
            setUrovoPowerOn("0", CMD_POWER_CONTROL_NODE);
        }
        System.gc();
    }

    public void handleMsg(final String msg, final int color) {
        if (handler == null) {
            handler = new Handler();
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                mTextView_msg.setText(msg);
                mTextView_msg.setTextColor(color);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void openSuccess(TrustFingerDevice trustFingerDevice) {
        mButton_openClose.setTextColor(Color.RED);
        handleMsg("Device open success", Color.BLACK);
        mTrustFingerDevice = trustFingerDevice;
        isDeviceOpened = true;
        String model = mTrustFingerDevice.getDeviceDescription().getProductModel();

        mDevIndexList = mTrustFinger.getDeviceIndexList();
        currentDevIndex[0] = mTrustFinger.getCurrentDevIndex();
        Log.e(TAG, "openSuccess:" + trustFingerDevice.toString() + "");


        if (model.equals(getString(R.string.sp_device_type_a600)) || model.equals(getString(R.string.sp_device_type_a700)) || model.equals(getString(R.string.sp_device_type_a620))) {
            int firmwareVersion = (int) (Float.valueOf(trustFingerDevice.getDeviceDescription().getFwVersion()) * 1000);
            if (firmwareVersion < 4200) {
                new AlertDialog.Builder(this).setCancelable(false).setTitle("Error")//设置对话框的标题
                        .setMessage("The current firmware version is " + firmwareVersion + ",this" + " " + "software only supports version 4.2 or above!")//设置对话框的内容
                        //设置对话框的按钮
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        }).create().show();
                return;
            }
            if (mCheckBox_enableLed.isChecked()) {
                handler.removeCallbacks(openLedTask);
                handler.post(openLedTask);
            }
            mCheckBox_enableLed.setEnabled(true);
        } else {
            mCheckBox_enableLed.setChecked(false);
            mCheckBox_enableLed.setEnabled(false);
            mSpinner_antiSpoofing_level.setEnabled(true);
        }
        mTextView_lfd_level.setEnabled(true);
        mSpinner_antiSpoofing_level.setEnabled(true);
        mSpinner_antiSpoofing_level.setSelection(FLD_LEVEL);
        setDeviceTypeSelection(DeviceType.getDeviceTypePosition(model));
        if (sp_usbDevice_datas.size() > 1) {
            mSpinner_usbDevice.setSelection(mDeviceId + 1);
        }
        adapter_usbDevice.notifyDataSetChanged();
        mButton_openClose.setEnabled(true);
        mButton_openClose.setText(getString(R.string.btn_close_device));
        mSpinner_deviceType.setEnabled(false);
        mSpinner_usbDevice.setEnabled(false);
        setFragmentDatas(mTrustFingerDevice);
    }

    @Override
    public void openFail(String s) {
        handleMsg("Device open fail", Color.RED);
        isDeviceOpened = false;
        mButton_openClose.setEnabled(true);
    }

    @Override
    public void setLedEnable(final boolean isEnable) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mTrustFingerDevice != null) {
                    if (mTrustFingerDevice.getDeviceDescription().getDeviceId() == 600 || mTrustFingerDevice.getDeviceDescription().getDeviceId() == 700 || mTrustFingerDevice.getDeviceDescription().getDeviceId() == 620)
                        mCheckBox_enableLed.setEnabled(isEnable);
                }
            }
        });
    }

    @Override
    public void setLfdEnable(final boolean isEnable) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mTrustFingerDevice != null) {
                    mTextView_lfd_level.setEnabled(isEnable);
                    mSpinner_antiSpoofing_level.setEnabled(isEnable);
                }
            }
        });
    }


    public void onclick_ChangeIndex(View v) {
        int indexNum = 0;
        mDevIndexList = mTrustFinger.getDeviceIndexList();
        try {
            indexNum = Integer.parseInt(etChangeIndex.getEditableText().toString().trim());
        } catch (NumberFormatException e) {
            System.out.println(e);
        }
        if (indexNum < mDevIndexList.size()) {
            currentDevIndex[0] = indexNum;
            mTrustFinger.setDeviceIndex(currentDevIndex[0]);
        } else {
            Log.e(TAG, "ChangeIndex:" + indexNum + ",out of size:" + mDevIndexList.size());
        }


        int devCount;
        try {
            devCount = mTrustFinger.getDeviceCount();
            int indexCount = mTrustFinger.getDeviceIndexCount();
            currentDevIndex[0] = mTrustFinger.getCurrentDevIndex();
            tvIndexMsg.setText("devCount: " + devCount + ",\nIndexCount:" + indexCount + ",\nCurIndex:" + currentDevIndex[0]);
        } catch (TrustFingerException e) {
            tvIndexMsg.setText("get DevCount exception: " + e.getType().toString() + "");
            e.printStackTrace();
        }
    }


    private Runnable openLedTask = () -> openLed();
    private Runnable closeLedTask = () -> closeLed();


    private void openLed() {
        try {
            if (mTrustFingerDevice != null && (mTrustFingerDevice.getDeviceDescription().getDeviceId() == 600
                    || mTrustFingerDevice.getDeviceDescription().getDeviceId() == 700 || mTrustFingerDevice.getDeviceDescription().getDeviceId() == 620)) {
                if (mTrustFingerDevice.getLedStatus(LedIndex.GREEN) == LedStatus.OPEN) {
                    mTrustFingerDevice.setLedStatus(LedIndex.GREEN, LedStatus.CLOSE);
                }
                if (mTrustFingerDevice.getLedStatus(LedIndex.RED) == LedStatus.OPEN) {
                    mTrustFingerDevice.setLedStatus(LedIndex.RED, LedStatus.CLOSE);
                }
                mTrustFingerDevice.setLedStatus(LedIndex.RED, LedStatus.OPEN);
                SystemClock.sleep(300);
                mTrustFingerDevice.setLedStatus(LedIndex.RED, LedStatus.CLOSE);
                mTrustFingerDevice.setLedStatus(LedIndex.GREEN, LedStatus.OPEN);
                SystemClock.sleep(300);
                mTrustFingerDevice.setLedStatus(LedIndex.GREEN, LedStatus.CLOSE);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void closeLed() {
        try {
            if (mTrustFingerDevice != null && (mTrustFingerDevice.getDeviceDescription().getDeviceId() == 600
                    || mTrustFingerDevice.getDeviceDescription().getDeviceId() == 700 || mTrustFingerDevice.getDeviceDescription().getDeviceId() == 620)) {
                if (mTrustFingerDevice.getLedStatus(LedIndex.GREEN) == LedStatus.OPEN) {
                    mTrustFingerDevice.setLedStatus(LedIndex.GREEN, LedStatus.CLOSE);
                }
                if (mTrustFingerDevice.getLedStatus(LedIndex.RED) == LedStatus.OPEN) {
                    mTrustFingerDevice.setLedStatus(LedIndex.RED, LedStatus.CLOSE);
                }
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }


    //device 1
//    private String UROVO_POWER_ON_NODE = "/sys/devices/soc/78d9000.usb/pogo_sw";
//    private String UROVO_OTG_ON_NODE = "/sys/devices/soc/78d9000.usb/otg_enable";

    // device 2
    private static final String CMD_POWER_CONTROL_NODE = "/sys/devices/platform/m536as_gpio_pin/usbhub4_power";


    public boolean setUrovoPowerOn(String string, String node) {
        FileOutputStream node_1 = null;
        try {
            node_1 = new FileOutputStream(node);
            byte[] open_two = string.getBytes();
            node_1.write(open_two);
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        } finally {
            if (node_1 != null) {
                try {
                    node_1.flush();
                    node_1.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.clear();
            outState = null;
        }
    }

}
