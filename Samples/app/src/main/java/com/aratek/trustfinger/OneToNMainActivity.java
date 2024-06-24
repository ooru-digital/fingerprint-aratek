//package com.aratek.trustfinger;
//
//import android.Manifest;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Color;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.provider.Settings;
//import android.support.annotation.NonNull;
//import android.support.design.widget.TabLayout;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.view.ViewPager;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.EditText;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.aratek.trustfinger.adapter.MyPagerAdapter;
//import com.aratek.trustfinger.fragment.DeviceInfoFragment;
//import com.aratek.trustfinger.fragment.OneToN_IdentifyFragment;
//import com.aratek.trustfinger.interfaces.ViewStatusCallback;
//import com.aratek.trustfinger.sdk.DeviceListener;
//import com.aratek.trustfinger.sdk.DeviceOpenListener;
//import com.aratek.trustfinger.sdk.LedIndex;
//import com.aratek.trustfinger.sdk.LedStatus;
//import com.aratek.trustfinger.sdk.LfdLevel;
//import com.aratek.trustfinger.sdk.TrustFinger;
//import com.aratek.trustfinger.sdk.TrustFingerDevice;
//import com.aratek.trustfinger.sdk.TrustFingerException;
//import com.aratek.trustfinger.utils.DeviceType;
//import com.aratek.trustfinger.utils.MediaPlayerHelper;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//public class OneToNMainActivity extends FragmentActivity implements DeviceOpenListener,
//        ViewStatusCallback {
//    private static final String TAG = "OneToNMainActivity";
//    private static final String ACTION_USB_PERMISSION = "com.aratek.trustfinger.USB_PERMISSION";
//    private static final String[] PERMISSION_LIST = new String[]{
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//    };
//    private static final int REQUEST_PERMISSIONS_CODE = 0;
//    public static String FP_DB_PATH = "/sdcard/fp.db";
//    private Spinner mSpinner_deviceType;
//    private Spinner mSpinner_usbDevice;
//    private Button mButton_openClose;
//    private CheckBox mCheckBox_enableLed;
//    private TextView mTextView_lfd_level;
//    private Spinner mSpinner_antiSpoofing_level;
//    private ViewPager mViewPager;
//    private TabLayout mTabLayout;
//    private TextView mTextView_msg;
//    private TextView mTextView_version;
//
//    private TrustFinger mTrustFinger;
//    protected TrustFingerDevice mTrustFingerDevice;
//    private ArrayAdapter<String> adapter_usbDevice;
//    private List<String> sp_usbDevice_datas_all = new ArrayList<String>();
//    private List<String> sp_usbDevice_datas = new ArrayList<String>();
//    private List<Fragment> fragmnts = new ArrayList<Fragment>();
//
////    private CaptureFragment mCaptureFragment;
//    private OneToN_IdentifyFragment mOneToN_IdentifyFragment;
//    private DeviceInfoFragment mDeviceInfoFragment;
//    private String[] titles;
//    private Handler handler = new Handler();
//    private int mDeviceId = 0;
//    private boolean isDeviceOpened = false;
//    private MyApplication mApp;
//
//    private EditText etChangeIndex;
//    private TextView tvIndexMsg;
//    private List<int[]> mDevIndexList = new ArrayList<int[]>();
//    private int[] currentDevIndex = new int[2];
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_oneton_main);
//        findViews();
//        initView();
////        setUrovoPowerOn("1",UROVO_OTG_ON_NODE);
////        setUrovoPowerOn("1",UROVO_POWER_ON_NODE);//add power on at Urovo devices.
////        Log.i("Sanny","setUrovoPowerOn 1 otg power.");
//
////        try {
////            Thread.sleep(1000);
////        }
////        catch (InterruptedException e) {
////            Thread.currentThread().interrupt();
////        }
//
//        FP_DB_PATH = this.getCacheDir().getPath()+"/fp.db";
//        initTrustFinger();
//        mApp = (MyApplication) getApplication();
//
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////            requestPermissions();
////        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            ActivityCompat.requestPermissions(this, PERMISSION_LIST, REQUEST_PERMISSIONS_CODE);
//        }
//
//
//
//    }
//
//    private void initView() {
//        initDatas();
//        File file = new File(Config.COMMON_PATH);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//    }
//
//
//    private void requestPermissions() {
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                new AlertDialog.Builder(OneToNMainActivity.this)
//                        .setMessage("This app need to open the permissions")
//                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Intent intent = new Intent();
//                                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//                                intent.setData(Uri.fromParts("package", getPackageName(), null));
//                                startActivityForResult(intent, 100);
//                            }
//                        })
//                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Toast.makeText(OneToNMainActivity.this, "Permissions denied!", Toast
//                                        .LENGTH_SHORT).show();
//                                finish();
//                            }
//                        })
//                        .setCancelable(false)
//                        .create()
//                        .show();
//            }
//            else {
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//            }
//        }
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        boolean hasAllGranted = true;
//
//        for (int i = 0; i < grantResults.length; ++i) {
//            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
//                hasAllGranted = false;
//                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
//                    new AlertDialog.Builder(this)
//                            .setMessage("This app need to open the permissions")
//                            .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Intent intent = new Intent(Settings
//                                            .ACTION_APPLICATION_DETAILS_SETTINGS);
//                                    Uri uri = Uri.fromParts("package", getApplicationContext()
//                                            .getPackageName(), null);
//                                    intent.setData(uri);
//                                    startActivityForResult(intent, 100);
//                                }
//                            })
//                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Toast.makeText(OneToNMainActivity.this, "Permissions denied!",
//                                            Toast.LENGTH_SHORT).show();
//                                    finish();
//                                }
//                            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
//                        @Override
//                        public void onCancel(DialogInterface dialog) {
//                            Toast.makeText(OneToNMainActivity.this, "Permissions denied!", Toast
//                                    .LENGTH_SHORT).show();
//                            finish();
//                        }
//                    }).show();
//
//                }
//                else {
//                    Toast.makeText(OneToNMainActivity.this, "Permissions denied!", Toast.LENGTH_SHORT)
//                            .show();
//                    finish();
//                }
//                break;
//            }
//        }
//
//    }
//
////    @Override
////    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
////        if (requestCode == 100) {
////            if (ContextCompat.checkSelfPermission(this,
////                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
////                    == PackageManager.PERMISSION_GRANTED) {
////                run();
////            }
////            else {
////                Toast.makeText(OneToNMainActivity.this, "Permissions denied!", Toast.LENGTH_SHORT).show();
////                finish();
////            }
////        }
////    }
//
//    private void findViews() {
//        mSpinner_deviceType = findViewById(R.id.sp_device_type);
//        mSpinner_usbDevice = findViewById(R.id.sp_usb_device);
//        mButton_openClose = findViewById(R.id.btn_open_device);
//        mCheckBox_enableLed = findViewById(R.id.chk_enabled_led);
//        mTextView_lfd_level = findViewById(R.id.tv_lfd_level);
//        mSpinner_antiSpoofing_level = findViewById(R.id.sp_spoofing_level);
//        mTabLayout = findViewById(R.id.tabLayout);
//        mViewPager = findViewById(R.id.viewPager);
//        mTextView_msg = findViewById(R.id.tv_msg);
//        mTextView_version = findViewById(R.id.tv_version);
//
//        etChangeIndex = findViewById(R.id.etChangeIndex);
//        tvIndexMsg = findViewById(R.id.tvIndexMsg);
//    }
//
//    private void initTrustFinger() {
//        try {
//            mTrustFinger = TrustFinger.getInstance(this.getApplicationContext());
//            //            mTextView_version.setText("v" + mTrustFinger.getSdkVersion());
//            mTrustFinger.setUsbSwitchToLibusb(true);
//            mTrustFinger.initialize();
//            mTrustFinger.setDeviceListener(new DeviceListener() {
//                @Override
//                public void deviceAttached(List<String> deviceList) {
//                    sp_usbDevice_datas_all.clear();
//                    for (int i = 0; i < deviceList.size(); i++) {
//                        sp_usbDevice_datas_all.add(i + "-" + deviceList.get(i));
//                    }
//                    sp_usbDevice_datas.clear();
//                    sp_usbDevice_datas.add(getString(R.string.sp_usb_device_auto));//show default
//                    // value
//                    sp_usbDevice_datas.addAll(sp_usbDevice_datas_all);
//                    mSpinner_deviceType.setSelection(0);
//                    mSpinner_usbDevice.setSelection(0);
//                    adapter_usbDevice.notifyDataSetChanged();
//                    handleMsg("Device atached!", Color.BLACK);
//                }
//
//                @Override
//                public void deviceDetached(List<String> deviceList) {
//                    sp_usbDevice_datas_all.clear();
//                    for (int i = 0; i < deviceList.size(); i++) {
//                        sp_usbDevice_datas_all.add(i + "-" + deviceList.get(i));
//                    }
//                    sp_usbDevice_datas.clear();
//                    sp_usbDevice_datas.add(getString(R.string.sp_usb_device_auto));//show default
//                    // value
//                    sp_usbDevice_datas.addAll(sp_usbDevice_datas_all);
//                    adapter_usbDevice.notifyDataSetChanged();
//                    mSpinner_deviceType.setSelection(0);
//                    mSpinner_usbDevice.setSelection(0);
//                    isDeviceOpened = false;
//                    mButton_openClose.setTextColor(Color.parseColor("#1D9F9A"));
//                    mButton_openClose.setText(getString(R.string.btn_open_device));
//                    setFragmentDatas(null);
//                    mSpinner_deviceType.setEnabled(true);
//                    mSpinner_usbDevice.setEnabled(true);
//                    //                mCheckBox_enableLed.setEnabled(true);
//                    handleMsg("Device detached!", Color.RED);
//                }
//            });
//            if (mTrustFinger.getDeviceCount() <= 0) {
//                handleMsg("No fingerprint device detected!", Color.RED);
//            }
//        }
//        catch (TrustFingerException e) {
//            handleMsg("TrustFinger getInstance Exception: " + e.getType().toString() + "", Color.RED);
//            if (e.getType().toString().equals("DEVICE_NOT_FOUND")) {
//                handleMsg("No fingerprint device detected!", Color.RED);
//            }
//            e.printStackTrace();
//        }
//        catch (ArrayIndexOutOfBoundsException e) {
//            handleMsg("The system does not support simultaneous access to two devices", Color.RED);
//        }
//
//    }
//
//    /**
//     * 经测试发现由于setSelection（）函数不会实时回调onItemSelected方法，目前原因未找到，所以部分地方采用自定义模拟的监听器
//     */
//    private void setDeviceTypeSelection(int position) {
//        int currentPosition = mSpinner_deviceType.getSelectedItemPosition();
//        mSpinner_deviceType.setSelection(position);
//        if (position != currentPosition) {
//            onItemChange(position);
//        }
//    }
//
//    private void onItemChange(int position) {
//        List<String> deviceList = mTrustFinger.getDeviceList();
//        sp_usbDevice_datas_all.clear();
//        for (int i = 0; i < deviceList.size(); i++) {
//            sp_usbDevice_datas_all.add(i + "-" +
//                    deviceList.get(i));
//        }
//        DeviceType deviceType = DeviceType.values()[position];
//        sp_usbDevice_datas.clear();
//        sp_usbDevice_datas.add(getString(R.string.sp_usb_device_auto));//set default value
//        if (deviceType.equals(DeviceType.AUTO)) {
//            sp_usbDevice_datas.addAll(sp_usbDevice_datas_all);
//            mSpinner_usbDevice.setSelection(0);
//        }
//        else if (deviceType.equals(DeviceType.A400)) {
//            for (String sp_usbDevice_data : sp_usbDevice_datas_all) {
//                if (sp_usbDevice_data.contains(getString(R.string
//                        .sp_device_type_a400))) {
//                    sp_usbDevice_datas.add(sp_usbDevice_data);
//                }
//            }
//            if (sp_usbDevice_datas.size() > 1) {
//                mSpinner_usbDevice.setSelection(1);
//            }
//        }
//        else if (deviceType.equals(DeviceType.A600)) {
//            for (String sp_usbDevice_data : sp_usbDevice_datas_all) {
//                if (sp_usbDevice_data.contains(getString(R.string
//                        .sp_device_type_a600))) {
//                    sp_usbDevice_datas.add(sp_usbDevice_data);
//                }
//            }
//            if (sp_usbDevice_datas.size() > 1) {
//                mSpinner_usbDevice.setSelection(1);
//            }
//        }
//        else if (deviceType.equals(DeviceType.EM4010)) {
//            for (String sp_usbDevice_data : sp_usbDevice_datas_all) {
//                if (sp_usbDevice_data.contains(getString(R.string
//                        .sp_device_type_em4010))) {
//                    sp_usbDevice_datas.add(sp_usbDevice_data);
//                }
//            }
//            if (sp_usbDevice_datas.size() > 1) {
//                mSpinner_usbDevice.setSelection(1);
//            }
//        }
//        else if (deviceType.equals(DeviceType.EM3011)) {
//            for (String sp_usbDevice_data : sp_usbDevice_datas_all) {
//                if (sp_usbDevice_data.contains(getString(R.string
//                        .sp_device_type_em3011))) {
//                    sp_usbDevice_datas.add(sp_usbDevice_data);
//                }
//            }
//            if (sp_usbDevice_datas.size() > 1) {
//                mSpinner_usbDevice.setSelection(1);
//            }
//        }
//        else if (deviceType.equals(DeviceType.EM2010)) {
//            for (String sp_usbDevice_data : sp_usbDevice_datas_all) {
//                if (sp_usbDevice_data.contains(getString(R.string
//                        .sp_device_type_em2010))) {
//                    sp_usbDevice_datas.add(sp_usbDevice_data);
//                }
//            }
//            if (sp_usbDevice_datas.size() > 1) {
//                mSpinner_usbDevice.setSelection(1);
//            }
//        }
//        else if (deviceType.equals(DeviceType.FRT610A)) {
//            for (String sp_usbDevice_data : sp_usbDevice_datas_all) {
//
//                if (sp_usbDevice_data.contains(getString(R.string
//                        .sp_device_type_frt610))) {
//                    sp_usbDevice_datas.add(sp_usbDevice_data);
//                }
//            }
//            if (sp_usbDevice_datas.size() > 1) {
//                mSpinner_usbDevice.setSelection(1);
//            }
//        }
//        else if (deviceType.equals(DeviceType.EM03_4010)) {
//            for (String sp_usbDevice_data : sp_usbDevice_datas_all) {
//                if (sp_usbDevice_data.contains(getString(R.string
//                        .sp_device_type_em03_4010))) {
//                    sp_usbDevice_datas.add(sp_usbDevice_data);
//                }
//            }
//            if (sp_usbDevice_datas.size() > 1) {
//                mSpinner_usbDevice.setSelection(1);
//            }
//        }
//        else if (deviceType.equals(DeviceType.EM03_3011)) {
//            for (String sp_usbDevice_data : sp_usbDevice_datas_all) {
//                if (sp_usbDevice_data.contains(getString(R.string
//                        .sp_device_type_em03_3011))) {
//                    sp_usbDevice_datas.add(sp_usbDevice_data);
//                }
//            }
//            if (sp_usbDevice_datas.size() > 1) {
//                mSpinner_usbDevice.setSelection(1);
//            }
//        }
//        else if (deviceType.equals(DeviceType.EM1920)) {
//            for (String sp_usbDevice_data : sp_usbDevice_datas_all) {
//                if (sp_usbDevice_data.contains(getString(R.string
//                        .sp_device_type_em1920))) {
//                    sp_usbDevice_datas.add(sp_usbDevice_data);
//                }
//            }
//            if (sp_usbDevice_datas.size() > 1) {
//                mSpinner_usbDevice.setSelection(1);
//            }
//        }
//        if(adapter_usbDevice != null)
//            adapter_usbDevice.notifyDataSetChanged();
//    }
//
//    private void initDatas() {
//        ArrayAdapter arrayAdapter_dev_type = ArrayAdapter.createFromResource(this, R.array
//                .device_type, R.layout.spinner_list_item);
//        arrayAdapter_dev_type.setDropDownViewResource(R.layout.spinner_list_item);
//        mSpinner_deviceType.setAdapter(arrayAdapter_dev_type);
//        mSpinner_deviceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                onItemChange(position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        adapter_usbDevice = new ArrayAdapter<String>(OneToNMainActivity.this, R.layout
//                .spinner_list_item, sp_usbDevice_datas);
//        adapter_usbDevice.setDropDownViewResource(R.layout.spinner_list_item);
//        mSpinner_usbDevice.setAdapter(adapter_usbDevice);
//        mSpinner_usbDevice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view,
//                                       int position, long id) {
//                String tag = parent.getItemAtPosition(position).toString();
//                if (getString(R.string.sp_usb_device_auto).equals(tag)) {
//                    mDeviceId = 0;
//                    return;
//                }
//                String[] strs = tag.split("-");
//                if (strs != null && strs.length > 0) {
//                    mDeviceId = Integer.parseInt(strs[0]);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                mDeviceId = -1;
//            }
//        });
//        mSpinner_antiSpoofing_level.setOnItemSelectedListener(new AdapterView
//                .OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String level = parent.getItemAtPosition(position).toString();
//                switch (level) {
//                    case "OFF": {
//                        if (mTrustFingerDevice != null) {
//                            updateLFDLevel(LfdLevel.OFF);
//                        }
//                        break;
//                    }
//                    case "EXTRA_LOW": {
//                        updateLFDLevel(LfdLevel.EXTRA_LOW);
//                        break;
//                    }
//                    case "LOW": {
//                        updateLFDLevel(LfdLevel.LOW);
//                        break;
//                    }
//                    case "MEDIUM": {
//                        updateLFDLevel(LfdLevel.MEDIUM);
//                        break;
//                    }
//                    case "HIGH": {
//                        updateLFDLevel(LfdLevel.HIGH);
//                        break;
//                    }
//                    case "ULTRA_HIGH": {
//                        updateLFDLevel(LfdLevel.ULTRA_HIGH);
//                        break;
//                    }
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        mSpinner_antiSpoofing_level.setEnabled(true);
//        mTextView_lfd_level.setEnabled(true);
//        mCheckBox_enableLed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener
//                () {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    mApp.setLedEnable(true);
//                }
//                else {
//                    mApp.setLedEnable(false);
//                }
//                if (isDeviceOpened) {
//                    if (mTrustFingerDevice != null) {
//                        if (mTrustFingerDevice.getDeviceDescription().getDeviceId() == 600
//                        || mTrustFingerDevice.getDeviceDescription().getDeviceId() == 700) {
//                            if (!isChecked) {
//                                if (mTrustFingerDevice.getLedStatus(LedIndex.RED) != LedStatus
//                                        .CLOSE) {
//                                    mTrustFingerDevice.setLedStatus(LedIndex.RED, LedStatus.CLOSE);
//                                }
//                                if (mTrustFingerDevice.getLedStatus(LedIndex.GREEN) != LedStatus
//                                        .CLOSE) {
//                                    mTrustFingerDevice.setLedStatus(LedIndex.GREEN, LedStatus
//                                            .CLOSE);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        });
//        isDeviceOpened = false;
//        mButton_openClose.setText(getString(R.string.btn_open_device));
//        mButton_openClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mButton_openClose.setEnabled(false);
//                if (!isDeviceOpened) {
//                    if (mDeviceId == -1) {
//
//                        mButton_openClose.setEnabled(true);
//                        return;
//                    }
//                    //                    if (sp_usbDevice_datas.size() == 1) {
//                    //                        handleMsg("Device not attached", Color.RED);
//                    //                        mButton_openClose.setEnabled(true);
//                    //                        return;
//                    //                    }
//                    String tag = mSpinner_usbDevice.getSelectedItem().toString();
//                    if (getString(R.string.sp_usb_device_auto).equals(tag)) {
//                        mDeviceId = 0;
//                    }
//                    else {
//                        String[] strs = tag.split("-");
//                        if (strs != null && strs.length > 0) {
//                            mDeviceId = Integer.parseInt(strs[0]);
//                        }
//                    }
//                    try {
////                        mTrustFinger.openDevice(mDeviceId, MainActivity.this);
//                        mTrustFinger.openDevice(currentDevIndex, OneToNMainActivity.this);
//
//                    }
//                    catch (TrustFingerException e) {
//                        mButton_openClose.setEnabled(true);
//                        handleMsg("Device open exception:" + e.getType().toString() + "", Color
//                                .RED);
//                        e.printStackTrace();
//                    }
//                }
//                else {
//                    int position = mViewPager.getCurrentItem();
////                    if (position == 0) {
////                        mCaptureFragment.forceStop();
////                        mCaptureFragment.resetUI();
////                    }
//                    if (position == 0) {
//                        mOneToN_IdentifyFragment.forceStop();
//                        mOneToN_IdentifyFragment.resetUI();
//                    }
//                    try {
//                        if (mTrustFingerDevice.getDeviceDescription().getDeviceId() == 600) {
//                            if (mTrustFingerDevice.getLedStatus(LedIndex.RED) != LedStatus.CLOSE) {
//                                mTrustFingerDevice.setLedStatus(LedIndex.RED, LedStatus.CLOSE);
//                            }
//                            if (mTrustFingerDevice.getLedStatus(LedIndex.GREEN) != LedStatus
//                                    .CLOSE) {
//                                mTrustFingerDevice.setLedStatus(LedIndex.GREEN, LedStatus.CLOSE);
//                            }
//                        }
//                        mButton_openClose.setTextColor(Color.parseColor("#1D9F9A"));
////                        mTrustFingerDevice.closeAll();
//                        mTrustFinger.closeAllDev();
////                        Bione.exit();
//                        mTrustFingerDevice = null;
//                        isDeviceOpened = false;
//                        mButton_openClose.setText(getString(R.string.btn_open_device));
//                        mButton_openClose.setEnabled(true);
//                        mSpinner_deviceType.setEnabled(true);
//                        mSpinner_usbDevice.setEnabled(true);
//                        mCheckBox_enableLed.setEnabled(false);
//                        mTextView_lfd_level.setEnabled(true);
//                        mSpinner_antiSpoofing_level.setEnabled(true);
//                        setFragmentDatas(null);
//                        handleMsg("Device closed!", Color.RED);
//                    }
//                    catch (TrustFingerException e) {
//                        mButton_openClose.setTextColor(Color.parseColor("#1D9F9A"));
//                        mButton_openClose.setEnabled(true);
//                        handleMsg("Device close exception : " + e.getType().toString() + "",
//                                Color.RED);
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
////        mCaptureFragment = new CaptureFragment();
////        mCaptureFragment.setLedCallback(this);
//        mOneToN_IdentifyFragment = new OneToN_IdentifyFragment();
//        mOneToN_IdentifyFragment.setLedCallback(this);
//        mDeviceInfoFragment = new DeviceInfoFragment();
////        fragmnts.add(mCaptureFragment);
//        fragmnts.add(mOneToN_IdentifyFragment);
//        fragmnts.add(mDeviceInfoFragment);
//        titles = getResources().getStringArray(R.array.oneton_tabs_name);
//        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), fragmnts, titles));
//        mViewPager.setOffscreenPageLimit(2);
//        mTabLayout.setupWithViewPager(mViewPager);
//        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                mViewPager.setCurrentItem(tab.getPosition(), false);
//
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {}
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {}
//        });
//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int
//                    positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context
//                        .INPUT_METHOD_SERVICE);
//                View view = getCurrentFocus();
//                if (view != null) {
//                    imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager
//                            .HIDE_NOT_ALWAYS);
//                }
//                handleMsg("", Color.BLACK);
////                if (position != 0) {
////                    mCaptureFragment.forceStop();
////                    mCaptureFragment.resetUI();
////                }
//                if (position != 0) {
//                    mOneToN_IdentifyFragment.forceStop();
//                    mOneToN_IdentifyFragment.resetUI();
//                }
////                if (position == 0) {
////                    if (isDeviceOpened) {
////                        handleMsg("Confirm the settings and select a finger for capture",
////                                Color.RED);
////                    }else {
////                        handleMsg(getString(R.string.msg_click_open_device_button), Color.BLACK);
////                    }
////
////                }else
//                    if (position == 0) {
//                    if (isDeviceOpened) {
//                        handleMsg("Device opened", Color.BLACK);
//                    }else {
//                        handleMsg(getString(R.string.msg_click_open_device_button), Color.BLACK);
//                    }
//                }
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//        });
//    }
//
//    private void updateLFDLevel(int level) {
//        if (mTrustFingerDevice != null) {
//            try {
//                mTrustFingerDevice.setLfdLevel(level);
//
//            }
//            catch (TrustFingerException e) {
//                handleMsg("enableLFD fail", Color.RED);
//            }
//        }
//    }
//
//    private void showAlertDialog(final boolean isError, String msg) throws TrustFingerException {
//        MediaPlayerHelper.payMedia(this, R.raw.no_fingerprint_device_detected);
//        new AlertDialog.Builder(this)
//                .setCancelable(false)
//                .setTitle("Error")//设置对话框的标题
//                .setMessage(msg)//设置对话框的内容
//                //设置对话框的按钮
//                .setNeutralButton("Redetect", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        if (isError) {
//                            initTrustFinger();
//                        }
//                        else {
//                            showAlertDialog(true, "No fingerprint device detected!");
//                        }
//                    }
//                })
//                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        finish();
//                    }
//                }).create().show();
//    }
//
//    private void setFragmentDatas(TrustFingerDevice mTrustFingerDevice) {
////        if (mCaptureFragment != null)
////            mCaptureFragment.setDatas(mTrustFingerDevice);
//        if (mOneToN_IdentifyFragment != null)
//            mOneToN_IdentifyFragment.setDatas(mTrustFingerDevice);
//        if (mDeviceInfoFragment != null)
//            mDeviceInfoFragment.setDatas(mTrustFingerDevice);
//    }
//
//
//    private void refreshDeviceList() {
//        List<String> deviceList = mTrustFinger.getDeviceList();
//        sp_usbDevice_datas_all.clear();
//        for (int i = 0; i < deviceList.size(); i++) {
//            sp_usbDevice_datas_all.add(i + "-" + deviceList.get(i));
//        }
//
//    }
//    private int openDevice(){
//        try {
//            mTrustFinger.openDevice(currentDevIndex, OneToNMainActivity.this);
//        }
//        catch (TrustFingerException e) {
//            mButton_openClose.setEnabled(true);
//            handleMsg("Device open exception:" + e.getType().toString() + "", Color
//                    .RED);
//            e.printStackTrace();
//        }
//        return 0;
//    }
//    private int closeDevice(){
//        mButton_openClose.setTextColor(Color.parseColor("#1D9F9A"));
//
//        mTrustFinger.closeAllDev();
//
//        mTrustFingerDevice = null;
//        isDeviceOpened = false;
//        mButton_openClose.setText(getString(R.string.btn_open_device));
//        mButton_openClose.setEnabled(true);
//        return 0;
//    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        if (mTrustFinger != null) {
//            openDevice();
//            refreshDeviceList();
//            if (!isDeviceOpened) {
//                handleMsg(getString(R.string.msg_click_open_device_button), Color.BLACK);
//            }
//        }
//
//    }
//
//    @Override
//    protected void onPause() {
//        closeDevice();
//
//        super.onPause();
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mTrustFingerDevice != null) {
//            mTrustFingerDevice.closeAll();
//            //todo 2023-05-09 fanghc
////            Bione.exit();
//        }
//        if (mTrustFinger != null) {
//            mTrustFinger.release();
//        }
//
////        setUrovoPowerOn("0",UROVO_POWER_ON_NODE);//add power on at Urovo devices.
////        setUrovoPowerOn("0",UROVO_OTG_ON_NODE);
////        Log.i("Sanny","setUrovoPowerOn 0. power otg");
//    }
//
//    public void handleMsg(final String msg, final int color) {
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                mTextView_msg.setText(msg);
//                mTextView_msg.setTextColor(color);
//            }
//        });
//    }
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            View v = getCurrentFocus();
//            if (isShouldHideInput(v, ev)) {
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context
//                        .INPUT_METHOD_SERVICE);
//                if (imm != null) {
//                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                }
//            }
//            return super.dispatchTouchEvent(ev);
//        }
//        if (getWindow().superDispatchTouchEvent(ev)) {
//            return true;
//        }
//        return onTouchEvent(ev);
//    }
//
//    public boolean isShouldHideInput(View v, MotionEvent event) {
//        if (v != null && (v instanceof EditText)) {
//            int[] leftTop = {0, 0};
//            v.getLocationInWindow(leftTop);
//            int left = leftTop[0];
//            int top = leftTop[1];
//            int bottom = top + v.getHeight();
//            int right = left + v.getWidth();
//            if (event.getX() > left && event.getX() < right
//                    && event.getY() > top && event.getY() < bottom) {
//                return false;
//            }
//            else {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public void openSuccess(TrustFingerDevice trustFingerDevice) {
//        mButton_openClose.setTextColor(Color.RED);
//        handleMsg("Device open success", Color.BLACK);
//        mTrustFingerDevice = trustFingerDevice;
//        String model = mTrustFingerDevice.getDeviceDescription().getProductModel();
//
//
//        mDevIndexList = mTrustFinger.getDeviceIndexList();
//        currentDevIndex[0] = mTrustFinger.getCurrentDevIndex();
//        Log.i(TAG,"openSuccess:" + trustFingerDevice.toString() + "");
//
//
//        if (model.equals(getString(R.string.sp_device_type_a600))
//                || model.equals(getString(R.string.sp_device_type_a700))) {
//            int firmwareVersion = (int) (Float.valueOf(trustFingerDevice.getDeviceDescription()
//                                .getFwVersion()) * 1000);
//            if (firmwareVersion < 4200) {
//                new AlertDialog.Builder(this)
//                        .setCancelable(false)
//                        .setTitle("Error")//设置对话框的标题
//                        .setMessage("The current firmware version is " + firmwareVersion + ",this" +
//                                " " +
//                                "software only supports version 4.2 or above!")//设置对话框的内容
//                        //设置对话框的按钮
//                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                finish();
//                            }
//                        }).create().show();
//                return;
//            }
//            mCheckBox_enableLed.setChecked(true);
//            mCheckBox_enableLed.setEnabled(true);
//            mSpinner_antiSpoofing_level.setEnabled(true);
//        }
//        else {
//            mCheckBox_enableLed.setChecked(false);
//            mCheckBox_enableLed.setEnabled(false);
//            mSpinner_antiSpoofing_level.setEnabled(true);
//        }
//        updateLFDLevel(LfdLevel.OFF);
//        mTextView_lfd_level.setEnabled(true);
//        mSpinner_antiSpoofing_level.setSelection(0);
//        setDeviceTypeSelection(DeviceType.getDeviceTypePosition(model));
//        if (sp_usbDevice_datas.size() > 1) {
//            mSpinner_usbDevice.setSelection(mDeviceId + 1);
//        }
//        if(adapter_usbDevice !=null)
//            adapter_usbDevice.notifyDataSetChanged();
//        isDeviceOpened = true;
//        mButton_openClose.setEnabled(true);
//        mButton_openClose.setText(getString(R.string.btn_close_device));
//        mSpinner_deviceType.setEnabled(false);
//        mSpinner_usbDevice.setEnabled(false);
//        //                                mCheckBox_enableLed.setEnabled
//        // (false);
//
//        setFragmentDatas(mTrustFingerDevice);
//
//    }
//
//    @Override
//    public void openFail(String s) {
//        handleMsg("Device open fail", Color.RED);
//        isDeviceOpened = false;
//        mButton_openClose.setEnabled(true);
//    }
//
//    @Override
//    public void setLedEnable(final boolean isEnable) {
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                if (mTrustFingerDevice != null) {
//                    if (mTrustFingerDevice.getDeviceDescription().getDeviceId() == 600)
//                        mCheckBox_enableLed.setEnabled(isEnable);
//                }
//            }
//        });
//    }
//
//    @Override
//    public void setLfdEnable(final boolean isEnable) {
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                if (mTrustFingerDevice != null) {
//                    mTextView_lfd_level.setEnabled(isEnable);
//                    mSpinner_antiSpoofing_level.setEnabled(isEnable);
//                }
//            }
//        });
//    }
//
//
//
//    public void onclick_ChangeIndex(View v) {
//        int indexNum = 0;
//        mDevIndexList = mTrustFinger.getDeviceIndexList();
//        try {
//            indexNum = Integer.parseInt(etChangeIndex.getEditableText().toString().trim());
//        } catch (NumberFormatException e) {
//            System.out.println(e);
//        }
//        if(indexNum < mDevIndexList.size() ) {
//            currentDevIndex[0] = indexNum;
//            mTrustFinger.setDeviceIndex(currentDevIndex[0]);
//        }else {
//            Log.e(TAG,"ChangeIndex:" + indexNum + ",out of size:" + mDevIndexList.size());
//        }
//
//
//        int devCount;
//        try {
//            devCount = mTrustFinger.getDeviceCount();
//            int indexCount = mTrustFinger.getDeviceIndexCount();
//            currentDevIndex[0] = mTrustFinger.getCurrentDevIndex();
//            tvIndexMsg.setText("devCount: " + devCount + ",\nIndexCount:" + indexCount + ",\nCurIndex:" + currentDevIndex[0]);
//        } catch (TrustFingerException e) {
//            tvIndexMsg.setText("get DevCount exception: " + e.getType().toString() + "");
//            e.printStackTrace();
//        }
//
//    }
//
//    private  String UROVO_POWER_ON_NODE = "/sys/devices/soc/78d9000.usb/pogo_sw";
//    private  String UROVO_OTG_ON_NODE = "/sys/devices/soc/78d9000.usb/otg_enable";
//    public boolean  setUrovoPowerOn(String string, String node){
//        FileOutputStream node_1 = null;
//        try {
//            node_1 = new FileOutputStream(node);
//            byte[] open_two = string.getBytes();
//            node_1.write(open_two);
//            return true;
//        }catch (Exception e){
//            e.printStackTrace();
//            return false;
//        }finally {
//            if(node_1!=null){
//                try {
//                    node_1.close();
//                }catch (Exception e){
//
//                }
//            }
//        }
//    }
//
//
//}
