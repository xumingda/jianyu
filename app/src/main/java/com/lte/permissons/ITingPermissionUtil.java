package com.lte.permissons;

import android.Manifest;
import android.app.Activity;

/**
 */

public class ITingPermissionUtil {
    public static final String[] IMPORTANT_PERMISSIONS = new String[] {Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private Activity mActivity;

    private OnITingPerimissionCallback mOnITingPermissionCallback = null;

    public ITingPermissionUtil(Activity activity) {
        mActivity = activity;
    }

//    public void checkImportantPermission(OnITingPerimissionCallback callback) {
//        mOnITingPermissionCallback = callback;
//
//        boolean hasReadPhoneStatePermission = PermissionUtil.hasSelfPermission(mActivity, new String[]{Manifest.permission.READ_PHONE_STATE});
//        boolean hasReadExternalStoragePermission = PermissionUtil.hasSelfPermission(mActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
//
//        if (hasReadExternalStoragePermission && hasReadPhoneStatePermission) {
//            if (mOnITingPermissionCallback != null) {
//                mOnITingPermissionCallback.onGranted();
//            }
//            return;
//        } else {
//            showRequestPermissionDialog(); //缺少权限
//        }
//    }
//
//    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode != PermissionUtil.REQUEST_CODE_ASK_PERMISSIONS || permissions == null || grantResults == null || permissions.length != grantResults.length) {
//            return false;
//        }
//        boolean isReadPhoneGranted = false;
//        boolean isReadExternalStorageGranted = false;
//        for (int i = 0 , length = permissions.length ; i < length ; i++) {
//            if (TextUtils.equals(permissions[i], Manifest.permission.READ_PHONE_STATE)) {
//                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                    isReadPhoneGranted = true;
//                } else {
//                    isReadPhoneGranted = false;
//                }
//            }
//
//            if (TextUtils.equals(permissions[i], Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                    isReadExternalStorageGranted = true;
//                } else {
//                    isReadExternalStorageGranted = false;
//                }
//            }
//        }
//        if (isReadExternalStorageGranted && isReadPhoneGranted) {
//            if (mOnITingPermissionCallback != null) {
//                mOnITingPermissionCallback.onGranted();
//            }
//        } else {
//            boolean isReadPhoneDeniedAndAllwaysForbin = false;
//            boolean isReadExternalStorageDeniedAndAllwaysForbin = false;
//            if (!isReadPhoneGranted) { //未获取到READ_PHONE权限
//                boolean isReadPhoneStateAllwaysForbin = !ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.READ_PHONE_STATE);
//                if (isReadPhoneStateAllwaysForbin) {
//                    isReadPhoneDeniedAndAllwaysForbin = true;
//                }
//            }
//
//            if (!isReadExternalStorageGranted) {
//                boolean isReadExternalStorageAllwaysForbin = !ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE);
//                if (isReadExternalStorageAllwaysForbin) {
//                    isReadExternalStorageDeniedAndAllwaysForbin = true;
//                }
//            }
//
//            if (isReadPhoneDeniedAndAllwaysForbin || isReadExternalStorageDeniedAndAllwaysForbin) {
//                showRequestPermissionReasonDialog(false);
//            } else {
//                showRequestPermissionReasonDialog(true);
//            }
//        }
//        return true;
//    }

//    private void showRequestPermissionDialog() {
//        DialogManager.showPermissionAlertDialog(mActivity, "权限申请", mActivity.getResources().getString(R.string.app_name)+"需要获取<font color='#fd6a6e'>(存储空间)</font>和<font color='#fd6a6e'>(设备信息)</font>权限，以保证歌曲正常播放下载以及您的账号安全。", "确定", new DialogManager.IClickListener() {
//            @Override
//            public boolean click(Dialog dlg, View view) {
//                try {
//                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE}, PermissionUtil.REQUEST_CODE_ASK_PERMISSIONS);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return true;
//            }
//        }, null, new DialogManager.IClickListener() {
//            @Override
//            public boolean click(Dialog dlg, View view) {
//                if (mOnITingPermissionCallback != null) {
//                    mOnITingPermissionCallback.onDenied();
//                }
//                return true;
//            }
//        });
//    }
//
//    private void showRequestPermissionReasonDialog(final boolean canRequestPermission) {
//        String appName = mActivity.getResources().getString(R.string.app_name);
//        DialogManager.showAlertDialog(mActivity, "权限申请", appName + "需要获取<font color='#fd6a6e'>(存储空间)</font>和<font color='#fd6a6e'>(设备信息)</font>权限，以保证歌曲正常播放下载以及您的账号安全。\n请在【设置-应用-" + appName +  "】中开启存储空间权限及设备信息权限，以正常使用" + appName + "功能。", false, "去设置", new DialogManager.IClickListener() {
//            @Override
//            public boolean click(Dialog dlg, View view) {
//                try {
//                    if (canRequestPermission) {
//                        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE}, PermissionUtil.REQUEST_CODE_ASK_PERMISSIONS);
//                    } else {
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        Uri packageUri = Uri.parse("package:" + mActivity.getPackageName());
//                        intent.setData(packageUri);
//                        mActivity.startActivity(intent);
//                        mActivity.finish();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return true;
//            }
//        }, null, new DialogManager.IClickListener() {
//            @Override
//            public boolean click(Dialog dlg, View view) {
//                if (mOnITingPermissionCallback != null) {
//                    mOnITingPermissionCallback.onDenied();
//                }
//                return true;
//            }
//        });
//    }

    public static interface OnITingPerimissionCallback {
        void onGranted();
        void onDenied();
    }
}
