/*
* Copyright 2015 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.lte.permissons;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 动态权限工具类  用于Android M系统
 */
public abstract class PermissionUtil {
	public static final int REQUEST_CODE_ASK_SMS_PERMISSION = 122;
	public static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public static final int REQUEST_CODE_ASK_READ_PHONE_STATE = 124;
    public static final int REQUEST_CODE_ASK_READ_EXTERNAL_STORAGE = 125;
    /**
     * Check that all given permissions have been granted by verifying that each entry in the
     * given array is of the value {@link PackageManager#PERMISSION_GRANTED}.
     *
     * @see Activity#onRequestPermissionsResult(int, String[], int[])
     */
    public static boolean verifyPermissions(int[] grantResults) {
        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the Activity has access to all given permissions.
     * Always returns true on platforms below M.
     *
     * @see Activity#checkSelfPermission(String)
     */
    public static boolean hasSelfPermission(Context activity, String[] permissions) {
        // Below Android M all permissions are granted at install time and are already available.
        if (!isMNC()) {
            return true;
        }
        // Verify that all required permissions have been granted
        for (String permission : permissions) {
        	Log.d("Contacts", ContextCompat.checkSelfPermission(activity,permission)+"");
            if (ContextCompat.checkSelfPermission(activity,permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    /**
     * Returns true if the Activity has access to a given permission.
     * Always returns true on platforms below M.
     *
     * @see Activity#checkSelfPermission(String)
     */
    public static boolean hasSelfPermission(Context activity, String permission) {
        // Below Android M all permissions are granted at install time and are already available.
        if (!isMNC()) {
            return true;
        }
        try {
        	Log.d("Contacts", ContextCompat.checkSelfPermission(activity,permission)+"");
        	return ContextCompat.checkSelfPermission(activity,permission) == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
			e.printStackTrace();
		}
        return true;
    }

    public static boolean isMNC() {
        /*
         TODO: In the Android M Preview release, checking if the platform is M is done through
         the codename, not the version code. Once the API has been finalised, the following check
         should be used: */
//         return Build.VERSION.SDK_INT == Build.VERSION_CODES.M;
    	Log.d("Build.VERSION.SDK_INT"," Build.VERSION.SDK_INT: " + Build.VERSION.SDK_INT);
    	return Build.VERSION.SDK_INT >= 23;
//      return "MNC".equals(Build.VERSION.CODENAME);
    }
    
    public static boolean canWriteSettings(Context context) {
    	if (isMNC()) {
    		try {
    			Class<?> cla = Class.forName("android.provider.Settings$System");
    			Method canWriteMethod = cla.getDeclaredMethod("canWrite", Context.class);
    			Object object = canWriteMethod.invoke(null, context);
    			if (object instanceof Boolean) {
    				return (Boolean)object;
    			}
    		} catch (ClassNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (NoSuchMethodException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IllegalAccessException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IllegalArgumentException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (InvocationTargetException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	} else {
    		return true;
    	}
    	return false;
    }

    public static boolean canDrawOverlays(Context context) {
        if (isMNC()) {
            try {
                Class<?> cla = Class.forName("android.provider.Settings");
                Method canWriteMethod = cla.getDeclaredMethod("canDrawOverlays", Context.class);
                Object object = canWriteMethod.invoke(null, context);
                if (object instanceof Boolean) {
                    return (Boolean)object;
                }
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return true;
        }
        return false;
    }
    
    public static String getActionManageWriteSettingsString() {
		try {
			Class<?> cla = Class.forName("android.provider.Settings");
			Field field = cla.getDeclaredField("ACTION_MANAGE_WRITE_SETTINGS");
			Object object = field.get(null);
			if (object instanceof String) {
				return (String)object;
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
    }

    public static String getActionManageOverlayPermissoinString() {
        try {
            Class<?> cla = Class.forName("android.provider.Settings");
            Field field = cla.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");
            Object object = field.get(null);
            if (object instanceof String) {
                return (String)object;
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

//    public static void requestReadContactPermission(Context context) {
//        if (context instanceof  Activity) {
//            final Activity activity = (Activity) context;
//            DialogManager.showAlertDialog(context, "提示", "请设置授予“爱听4G”读取联系人的权限", "前往设置", new DialogManager.IClickListener() {
//
//                @Override
//                public boolean click(Dialog dlg, View view) {
//                    try {
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        Uri packageUri = Uri.parse("package:" + activity.getPackageName());
//                        intent.setData(packageUri);
//                        activity.startActivity(intent);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    return true;
//                }
//            }, "取消", new DialogManager.IClickListener() {
//
//                @Override
//                public boolean click(Dialog dlg, View view) {
//                    // TODO Auto-generated method stub
//                    dlg.dismiss();
//                    return true;
//                }
//            });
//        }
//    }
}
