package com.aratek.trustfinger.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aratek.trustfinger.R;
import com.aratek.trustfinger.sdk.DeviceDescription;
import com.aratek.trustfinger.sdk.TrustFingerDevice;

import java.io.UnsupportedEncodingException;


public class DeviceInfoFragment extends BaseFragment {
    private TextView mTextView_manufacturer;
    private TextView mTextView_device_id;
    private TextView mTextView_model;
    private TextView mTextView_firmwareVersion;
    private TextView mTextView_serialNumber;
    private TextView mTextView_imageWidth;
    private TextView mTextView_imageHeight;
    private TextView mTextView_imageDpi;
    private View root;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_device_info, container, false);
            mTextView_manufacturer = (TextView) root.findViewById(R.id.tv_manufacturer);
            mTextView_device_id = (TextView) root.findViewById(R.id.tv_device_id);
            mTextView_model = (TextView) root.findViewById(R.id.tv_model);
            mTextView_firmwareVersion = (TextView) root.findViewById(R.id.tv_fw_version);
            mTextView_serialNumber = (TextView) root.findViewById(R.id.tv_serial_number);
            mTextView_imageWidth = (TextView) root.findViewById(R.id.tv_image_width);
            mTextView_imageHeight = (TextView) root.findViewById(R.id.tv_image_height);
            mTextView_imageDpi = (TextView) root.findViewById(R.id.tv_image_dpi);
        }
        viewCreated = true;
        return root;
    }


    public void onResume() {
        super.onResume();
        if (mTrustFingerDevice != null) {
            setUI(mTrustFingerDevice.getDeviceDescription());
        } else {
            resetUI();
        }
    }

    protected void resetUI() {
        mTextView_manufacturer.setText("");
        mTextView_device_id.setText("");
        mTextView_model.setText("");
        mTextView_firmwareVersion.setText("");
        mTextView_serialNumber.setText("");
        mTextView_imageWidth.setText("");
        mTextView_imageHeight.setText("");
        mTextView_imageDpi.setText("");
    }

    protected void setUI(DeviceDescription mDeviceDescription) {
        mTextView_manufacturer.setText(mDeviceDescription.getManufaturer() + "");
        mTextView_device_id.setText(mDeviceDescription.getDeviceId() + "");
        mTextView_model.setText(mDeviceDescription.getProductModel() + "");
        mTextView_firmwareVersion.setText(mDeviceDescription.getFwVersion() + "");
        mTextView_serialNumber.setText(mDeviceDescription.getSerialNumber() + "");
        mTextView_imageWidth.setText(mDeviceDescription.getImageWidth() + "");
        mTextView_imageHeight.setText(mDeviceDescription.getImageHeight() + "");
        mTextView_imageDpi.setText(mDeviceDescription.getResolution() + "");
    }

    @Override
    public void setDatas(TrustFingerDevice device) {
        mTrustFingerDevice = device;
        if (device != null) {
            if (viewCreated) {
                setUI(device.getDeviceDescription());
            }
        } else {
            if (viewCreated) {
                resetUI();
            }
        }
    }
}
