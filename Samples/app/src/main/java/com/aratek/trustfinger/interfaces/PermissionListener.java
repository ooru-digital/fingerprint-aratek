package com.aratek.trustfinger.interfaces;

import java.util.List;

/**
 * Created by hecl on 2018/9/29.
 */
public interface PermissionListener {
    void onGranted();

    void onDenied(List<String> deniedPermission);

}
