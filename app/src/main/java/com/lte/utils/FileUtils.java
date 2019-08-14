package com.lte.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.util.Log;


import com.lte.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileUtils {
    public static final String SD_PATH = "lte";

    public static byte[] readFileToBytes(File file) {
        if (file == null || !file.exists()) {
            return null;
        }

        BufferedInputStream in = null;
        byte[] content = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);

            byte[] temp = new byte[2024];
            int size = 0;
            while ((size = in.read(temp)) != -1) {
                out.write(temp, 0, size);
            }
            in.close();
            content = out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return content;
    }

    public static boolean isExistsBySync(final String path) {
        final byte[] isExist = new byte[]{0};
        Thread procThread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (isExist) {
                    File f = new File(path);
                    if (f.exists()) {
                        isExist[0] = 1;
                    }
                }
            }
        });
        procThread.start();
        try {
            Thread.sleep(20);
            if (procThread.isAlive()) {
                procThread.interrupt();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return isExist[0] == 1;
    }



    public static File createFile(String fileName){

        File file = null;
        try {
            file = new File(fileName);
            File path = new File(file.getParent());
            if (!path.exists()) {
                createDir(path.toString());
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    public static File createDir(String dirName) {

        File dir = null;
        try {
            dir = new File(dirName);
            if(!dir.exists()){
            	dir.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dir;
    }


    public static int deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            try {
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
                return 2;
            }
        } else {
            return 1;
        }
        return 0;
    }


      public static void delFolder(String folderPath) {
	       try {
	          delAllFile(folderPath);
	          String filePath = folderPath;
	          filePath = filePath.toString();
	          File myFilePath = new File(filePath);
	          myFilePath.delete();
	       } catch (Exception e) {
	         e.printStackTrace();
	       }
      }

    /**
     * 删除目录（文件夹）以及目录下的文件
     * @param   sPath 被删除目录的文件路径
     */
    public static void  deleteDirectory(String sPath) {

        try {
            //如果sPath不以文件分隔符结尾，自动添加文件分隔符
            if (sPath == null) {
                return;
            }
            if (!sPath.endsWith(File.separator)) {
                sPath = sPath + File.separator;
            }
            File dirFile = new File(sPath);
            //如果dir对应的文件不存在，或者不是一个目录，则退出
            if (dirFile == null || !dirFile.exists() || !dirFile.isDirectory()) {
                return ;
            }
            //删除文件夹下的所有文件(包括子目录)
            File[] files = dirFile.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    //删除子文件
                    if (files[i].isFile()) {
                        deleteFile(files[i].getAbsolutePath().toString());
                    } //删除子目录
                    else {
                        deleteDirectory(files[i].getAbsolutePath());
                    }
                }
            }

            //删除当前目录
            dirFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
          return flag;
        }
        if (!file.isDirectory()) {
          return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
           if (path.endsWith(File.separator)) {
              temp = new File(path + tempList[i]);
           } else {
               temp = new File(path + File.separator + tempList[i]);
           }
           if (temp.isFile()) {
              temp.delete();
           }





        }
        return flag;
      }

    public static boolean isSDFileExist(String dir,String fileName) {
        try {
            String sdPath = Environment.getExternalStorageDirectory() + "/"+dir;
            File file = new File(sdPath + fileName);
            return file.exists();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    public static File writeFromInput(String path, String fileName, InputStream input) {
        File file = null;
        OutputStream output = null;
        try {
            if (!path.endsWith("/")) {
                path += "/";
            }
            createDir(path);
            file = createFile(path + fileName);
            output = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int c;
            while ((c = input.read(buffer)) != -1) {
                output.write(buffer, 0, c);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }


    public static int CopyFile(String fromFile, String toFile) {
        File fFromFile = new File(fromFile);

        if (!fFromFile.exists()) {
            return 1;
        }

        File fToFile = new File(toFile);

        if (fToFile.exists()) {
            fToFile.delete();
        }

        try {
            InputStream fosfrom = new FileInputStream(fFromFile);
            OutputStream fosto = new FileOutputStream(fToFile);
            byte bt[] = new byte[524288];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return 0;
        } catch (Exception ex) {
            return 2;
        }
    }


    public static boolean isExternalStorageExit(Context context) {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


	public static String getFileSizeString(float size) {
		if(size<=0){
			return "0KB";
		}
		String result[] = fileSize(size);
		return result[0]+result[1];
	}
    public static String getFileSizeString(String path) {
        File file = new File(path);
        try {
            return FormetFileSize(getFileSize(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static  long  getFilesSize(File f) {
        long  size =  0 ;
        File flist[] = f.listFiles();
        if(flist==null){
        	    return 0;
        }
        for (File aFlist : flist) {
            if (aFlist.isDirectory()) {
                size = size + getFilesSize(aFlist);
            } else {
                size = size + aFlist.length();
            }
        }
        Log.d("DataManager","size :" +size);
        return  size;
    }
    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        }
        Log.d("DataManager","getFileSize :" +size);
        return size;
    }
    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }
    public static String getExternalStoragePath(Context context){
    	String storagePath = SharedPreferencesUtil.getStringConfig(context, "imusic", "storagePath", null);
    	if(storagePath==null){
    		storagePath = getStoragePathList(context).get(0);
    	}else{//防止热拔外置sd卡时获取到的外置sd卡路径修改为内置的sd卡路径  @update 2015年3月9日16:24:17  @zhengxh
    		List<String> storagePathList = getStoragePathList(context);
    		if(!storagePathList.contains(storagePath)){
    			storagePath = getStoragePathList(context).get(0);
    			SharedPreferencesUtil.setConfig(context, "imusic", "storagePath", storagePath);
    		}
    	}

    	return storagePath;
//    	   return SharedPreferencesUtil.getStringConfig(context, "imusic", "storagePath", getStoragePathList(context).get(0));
    }


	/**
	 * 获取头像路径
	 * @param context
	 * @return
	 */
	public static String getPhotoImagePath(Context context) {
		String path = null;
		if(context==null){
			path = "sdcard/iting/headImg";
			Log.w("LoadImage", "context is NULL");
		}else{
			path = context.getResources().getString(R.string.image_head_path);
		}
		path = path.replaceAll("sdcard",  getExternalStoragePath(context));
		File tempDir = FileUtils.createDir(path);
		return tempDir.getAbsolutePath();
	}

	public static String getConfigPath(Context context) {
		String path = context.getResources().getString(R.string.config_path);
		path = path.replaceAll("sdcard",   getExternalStoragePath(context));
		File tempDir = FileUtils.createDir(path);
		return tempDir.getAbsolutePath();
	}
    public static String getConfigLogPath(Context context) {
        String path = context.getResources().getString(R.string.config_log_path);
        path = path.replaceAll("sdcard",   getExternalStoragePath(context));
        File tempDir = FileUtils.createDir(path);
        return tempDir.getAbsolutePath();
    }

    public static String getImageCachePath(Context context){
	    	String cachePath = context.getString(R.string.image_cache_path);
	    	if ("ZTE N880G".endsWith(Build.MODEL) && !Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
	    		cachePath = cachePath.replaceAll("sdcard", "/mnt/sdcard2");
	    	}else {
	    		cachePath = cachePath.replaceAll("sdcard", getExternalStoragePath(context));
	    	}
	    	return cachePath;
    }
    public static String getDataDownloadPath(Context context){
        String cachePath = context.getString(R.string.data_download_path);
        if ("ZTE N880G".endsWith(Build.MODEL) && !Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cachePath = cachePath.replaceAll("sdcard", "/mnt/sdcard2");
        }else {
            cachePath = cachePath.replaceAll("sdcard", getExternalStoragePath(context));
        }
        return cachePath;
    }
	public static boolean copyFileFromAssets(Context context, String apkName,
                                             String path) {
		boolean flag = false;
		int BUFFER = 10240;
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		AssetFileDescriptor fileDescriptor = null;
		byte b[] = null;
		try {
			fileDescriptor = context.getAssets().openFd(apkName);
			File file = new File(path);
			if (file.exists()) {
				if (fileDescriptor != null
						&& fileDescriptor.getLength() == file.length()) {
					flag = true;
				} else
					file.delete();
			}
			if (!flag) {
				in = new BufferedInputStream(
						fileDescriptor.createInputStream(), BUFFER);
				boolean isOK = file.createNewFile();
				if (in != null && isOK) {
					out = new BufferedOutputStream(new FileOutputStream(file),
							BUFFER);
					b = new byte[BUFFER];
					int read = 0;
					while ((read = in.read(b)) > 0) {
						out.write(b, 0, read);
					}
					flag = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fileDescriptor != null) {
				try {
					fileDescriptor.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

    public static List<String> getStoragePathList(Context context ){
	      List<String > mountedPaths=new ArrayList<String>();
	      String mountedState= Environment.getExternalStorageState();
	      if(!Environment.MEDIA_MOUNTED.equals(mountedState)){
	    	  AppUtils.showToast(context, "SD卡还没准备好");
	    	  String path= Environment.getExternalStorageDirectory().getAbsolutePath();
	   		  mountedPaths.add(path);
	   		  SharedPreferencesUtil.setConfig(context, "imusic",  "storagePath", path);
	    	  return mountedPaths;
	      }

	      try {
	    	  File pub=context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
	 	      if(!pub.exists()){
	 	    	  pub.mkdirs();
	 	      }
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
	      if(Build.VERSION.SDK_INT>=9){
		    	  try {
		    		  StorageManager storageManager=(StorageManager)context.getSystemService(Context.STORAGE_SERVICE);
		    		 Method[] methods=storageManager.getClass().getMethods();
		    		 boolean hasGetVolumeListMethod=false;
		    		 boolean hasGetVolumeStateMethod=false;
		    		 for (Method method : methods) {
		    			    if("getVolumeList".equals(method.getName())){
		    			    	    hasGetVolumeListMethod=true;
		    			    }
		    			    if("getVolumeState".equals(method.getName())){
		    			    	  hasGetVolumeStateMethod=true;
		    			    }
				  }
		    		 if(hasGetVolumeListMethod&&hasGetVolumeStateMethod){
		    			 Method getVolumeListMethod=storageManager.getClass().getMethod("getVolumeList");
		    			 Method getVolumeStateMethod=storageManager.getClass().getMethod("getVolumeState",String.class);
		    			 Object[] objects= (Object[]) getVolumeListMethod.invoke(storageManager);
		    			 for (Object object : objects) {
		    				 Method method=object.getClass().getMethod("getPath");
		    				 String path=(String) method.invoke(object);
		    				 String state=(String)getVolumeStateMethod.invoke(storageManager, path);
		    				 if(Environment.MEDIA_MOUNTED.equals(state)){
		    					 Method isEmulatedmethod=object.getClass().getMethod("isEmulated");
		    					 Method isRemovablemethod=object.getClass().getMethod("isRemovable");
		    					 boolean isRemovable=(Boolean)isRemovablemethod.invoke(object);
			    				 boolean isEmulated= (Boolean) isEmulatedmethod.invoke(object);
                                 if(!isEmulated&&isRemovable&& Build.VERSION.SDK_INT>=19){
                                     if (Build.MODEL != null && Build.MODEL.toLowerCase().equals("tcl p316l")) { //TCL P316L机型适配：存储到SD卡时可存储到非包名路径下
                                         try {
                                             String downloadPath = context.getString(R.string.data_download_path);
                                             downloadPath = downloadPath.replaceAll("sdcard", path);
                                             File file = new File(downloadPath);
                                             if (!file.exists()) {
                                                 file.mkdirs();
                                             }
                                             if (!file.exists()) {
                                                 path+="/Android/data/"+context.getPackageName();
                                             }
                                         } catch (Exception e) {
                                             e.printStackTrace();
                                             if (path != null && !path.contains("/Android/data/")) {
                                                 path+="/Android/data/"+context.getPackageName();
                                             }
                                         }
                                     } else {
                                         path+="/Android/data/"+context.getPackageName();
                                     }
                                 }
		    					 mountedPaths.add(path);
		    				 }
		    			 }
		    		 }
		    	  } catch (Exception e) {
		    		  e.printStackTrace();
					  String path= Environment.getExternalStorageDirectory().getAbsolutePath();
		  	   		  mountedPaths.add(path);
		  	   		  SharedPreferencesUtil.setConfig(context, "imusic",  "storagePath", path);
		    	  }
	      }

		  if(mountedPaths ==null || mountedPaths.size()==0){
			  String path= Environment.getExternalStorageDirectory().getAbsolutePath();
  	   		  mountedPaths.add(path);
  	   		  SharedPreferencesUtil.setConfig(context, "imusic",  "storagePath", path);
       }
		return mountedPaths;
	}

    /**
     * 获取手机存储器信息列表
     * @param context
     * @return
     */
    public static List<StorageModel> getStorageModelList(Context context){
        List<StorageModel> mountedPaths=new ArrayList<StorageModel>();
        String mountedState= Environment.getExternalStorageState();
        if(!Environment.MEDIA_MOUNTED.equals(mountedState)){
            AppUtils.showToast(context, "SD卡还没准备好");
            StorageModel storageModel = new StorageModel();
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            storageModel.storagePath = path;
            storageModel.isRemovable = false;
            mountedPaths.add(storageModel);
            SharedPreferencesUtil.setConfig(context, "imusic",  "storagePath", path);
            return mountedPaths;
        }

        try {
            File pub=context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if(!pub.exists()){
                pub.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(Build.VERSION.SDK_INT>=9){
            try {
                StorageManager storageManager=(StorageManager)context.getSystemService(Context.STORAGE_SERVICE);
                Method[] methods=storageManager.getClass().getMethods();
                boolean hasGetVolumeListMethod=false;
                boolean hasGetVolumeStateMethod=false;
                for (Method method : methods) {
                    if("getVolumeList".equals(method.getName())){
                        hasGetVolumeListMethod=true;
                    }
                    if("getVolumeState".equals(method.getName())){
                        hasGetVolumeStateMethod=true;
                    }
                }
                if(hasGetVolumeListMethod&&hasGetVolumeStateMethod){
                    Method getVolumeListMethod=storageManager.getClass().getMethod("getVolumeList");
                    Method getVolumeStateMethod=storageManager.getClass().getMethod("getVolumeState",String.class);
                    Object[] objects= (Object[]) getVolumeListMethod.invoke(storageManager);
                    for (Object object : objects) {
                        Method method=object.getClass().getMethod("getPath");
                        String path=(String) method.invoke(object);
                        String state=(String)getVolumeStateMethod.invoke(storageManager, path);
                        if(Environment.MEDIA_MOUNTED.equals(state)){
                            Method isEmulatedmethod=object.getClass().getMethod("isEmulated");
                            Method isRemovablemethod=object.getClass().getMethod("isRemovable");
                            boolean isRemovable=(Boolean)isRemovablemethod.invoke(object);
                            boolean isEmulated= (Boolean) isEmulatedmethod.invoke(object);
                            if(!isEmulated&&isRemovable&& Build.VERSION.SDK_INT>=19){
                                if (Build.MODEL != null && Build.MODEL.toLowerCase().equals("tcl p316l")) { //TCL P316L机型适配：存储到SD卡时可存储到非包名路径下
                                    try {
                                        String downloadPath = context.getString(R.string.data_download_path);
                                        downloadPath = downloadPath.replaceAll("sdcard", path);
                                        File file = new File(downloadPath);
                                        if (!file.exists()) {
                                            file.mkdirs();
                                        }
                                        if (!file.exists()) {
                                            path+="/Android/data/"+context.getPackageName();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        if (path != null && !path.contains("/Android/data/")) {
                                            path+="/Android/data/"+context.getPackageName();
                                        }
                                    }
                                } else {
                                    path+="/Android/data/"+context.getPackageName();
                                }
                            }
                            StorageModel storageModel = new StorageModel();
                            storageModel.storagePath = path;
                            storageModel.isRemovable = isRemovable;
                            mountedPaths.add(storageModel);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                String path= Environment.getExternalStorageDirectory().getAbsolutePath();
                StorageModel storageModel = new StorageModel();
                storageModel.storagePath = path;
                storageModel.isRemovable = false;
                mountedPaths.add(storageModel);
                SharedPreferencesUtil.setConfig(context, "imusic",  "storagePath", path);
            }
        }

        if(mountedPaths ==null || mountedPaths.size()==0){
            String path= Environment.getExternalStorageDirectory().getAbsolutePath();
            StorageModel storageModel = new StorageModel();
            storageModel.storagePath = path;
            storageModel.isRemovable = false;
            mountedPaths.add(storageModel);
            SharedPreferencesUtil.setConfig(context, "imusic",  "storagePath", path);
        }
        return mountedPaths;
    }

    public static List<String> getDIYScanStoragePathList(Context context){
	      List<String > mountedPaths=new ArrayList<String>();
	      String mountedState= Environment.getExternalStorageState();
	      if(!Environment.MEDIA_MOUNTED.equals(mountedState)){
	    	  AppUtils.showToast(context, "SD卡还没准备好");
	    	  String path= Environment.getExternalStorageDirectory().getAbsolutePath();
	   		  mountedPaths.add(path);
	   		  SharedPreferencesUtil.setConfig(context, "imusic",  "storagePath", path);
	    	  return mountedPaths;
	      }

	      File pub=context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
	      if(pub != null && !pub.exists()){
	    	  pub.mkdirs();
	      }
	      if(Build.VERSION.SDK_INT>=9){
		    	  StorageManager storageManager=(StorageManager)context.getSystemService(Context.STORAGE_SERVICE);
		    	  try {
		    		 Method[] methods=storageManager.getClass().getMethods();
		    		 boolean hasGetVolumeListMethod=false;
		    		 boolean hasGetVolumeStateMethod=false;
		    		 for (Method method : methods) {
		    			    if("getVolumeList".equals(method.getName())){
		    			    	    hasGetVolumeListMethod=true;
		    			    }
		    			    if("getVolumeState".equals(method.getName())){
		    			    	  hasGetVolumeStateMethod=true;
		    			    }
				  }
		    		 if(hasGetVolumeListMethod&&hasGetVolumeStateMethod){
		    			 Method getVolumeListMethod=storageManager.getClass().getMethod("getVolumeList");
		    			 Method getVolumeStateMethod=storageManager.getClass().getMethod("getVolumeState",String.class);
		    			 Object[] objects= (Object[]) getVolumeListMethod.invoke(storageManager);
		    			 for (Object object : objects) {
		    				 Method method=object.getClass().getMethod("getPath");
		    				 String path=(String) method.invoke(object);
		    				 String state=(String)getVolumeStateMethod.invoke(storageManager, path);
		    				 if(Environment.MEDIA_MOUNTED.equals(state)){
		    					 Method isEmulatedmethod=object.getClass().getMethod("isEmulated");
		    					 Method isRemovablemethod=object.getClass().getMethod("isRemovable");
		    					 boolean isRemovable=(Boolean)isRemovablemethod.invoke(object);
			    				 boolean isEmulated= (Boolean) isEmulatedmethod.invoke(object);
			    				 if(!isEmulated&&isRemovable&& Build.VERSION.SDK_INT>=19){
//			    					 path+="/Android/data/"+context.getPackageName();
			    				 }
		    					 mountedPaths.add(path);
		    				 }
		    			 }
		    		 }
		    	  } catch (Exception e) {
		    		  e.printStackTrace();
		    	  }
	      }

		  if(mountedPaths.size()==0){
			  String path= Environment.getExternalStorageDirectory().getAbsolutePath();
	   		  mountedPaths.add(path);
	   		  SharedPreferencesUtil.setConfig(context, "imusic",  "storagePath", path);
     }
		return mountedPaths;
	}

	public static String getStorageInfo(String path) {
		StatFs sFs = new StatFs(getStatFsPath(path));
		long blockSize = sFs.getBlockSize();
		long totalBlocks = sFs.getBlockCount();
		long availableBlocks = sFs.getAvailableBlocks();
		long usedBlocks = totalBlocks - availableBlocks;
		String[] total = fileSize(totalBlocks * blockSize);
		String[] used = fileSize(usedBlocks * blockSize);
		String[] available = fileSize(availableBlocks * blockSize);


		String sdInfo = "共" + total[0] + total[1]  + " ,  " + available[0] + available[1]+"可用";
		return sdInfo;
	}

	public static String getStatFsPath(String path){
    	String str="/Android/data/";
    	if(path.contains(str)){
    		path=path.substring(0, path.indexOf(str));
    	}
    	return path;
    }
    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    private static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }
	public static String[] fileSize(float size) {
		String str = "";
		if (size >= 1024) {
			str = "KB";
			size = size / 1024;
			if (size >= 1024) {
				str = "MB";
				size = size / 1024;
				if(size>=1024){
					 str="GB";
					 size = size / 1024;
					 if (size >= 1024) {
						 str="TB";
						 size = size / 1024;
					 }
				}
			}
		}

		DecimalFormat formatter = new DecimalFormat(".00");
		formatter.setGroupingSize(3);
		String result[] = new String[2];
		result[0] = formatter.format(size);
		result[1] = str;

		return result;
	}
	/**
	 * 保存缓存图片
	 *
	 * @param url
	 * @param bitmap
	 */
	public static void doBufferImage(Context context, String url, Bitmap bitmap) {
		try {
			if (!isSdCardExist(context)) {
				return;
			}
			String filePath = FileUtils.getImageCachePath(context)+"/"+ url.substring(url.lastIndexOf("/"));
	        File cacheFile = new File(filePath);
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(cacheFile));
			if (url.indexOf(".png") > 0) {
				bitmap.compress(Bitmap.CompressFormat.PNG, 90, bos);
			} else {
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
			}
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/** 加载SD卡图片 */
	public static Bitmap getSdCardImage(Context context, String path) {
		if (!isSdCardExist(context)) {
			return null;
		}
		Bitmap bitmap = null;
		File f = new File(FileUtils.getImageCachePath(context)+"/"+path.substring(path.lastIndexOf("/")));
		if (f != null && f.exists()) {
			bitmap = createImageThumbnail(f.getAbsolutePath());
		}
		return bitmap;
	}

	/**
	 * 判断SD卡是否存在
	 *
	 * @param context
	 * @return
	 */
	private static boolean isSdCardExist(Context context) {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	public static Bitmap createImageThumbnail(String filePath){
	     Bitmap bitmap = null;
	     BitmapFactory.Options opts = new BitmapFactory.Options();
	     opts.inJustDecodeBounds = true;  
	     BitmapFactory.decodeFile(filePath, opts);
	  
	     opts.inSampleSize = computeSampleSize(opts, -1, 720*720);  
	     opts.inJustDecodeBounds = false;  
	  
	     try {  
	         bitmap = BitmapFactory.decodeFile(filePath, opts);
	     }catch (Exception e) {
	        e.printStackTrace();  
	    }  
	    return bitmap;  
	}  
	
	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
	    int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);  
	    int roundedSize;  
	    if (initialSize <= 8) {  
	        roundedSize = 1;  
	        while (roundedSize < initialSize) {  
	            roundedSize <<= 1;  
	        }  
	    } else {  
	        roundedSize = (initialSize + 7) / 8 * 8;  
	    }  
	    return roundedSize;  
	}  
	
	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
	    double w = options.outWidth;  
	    double h = options.outHeight;  
	    int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
	    int upperBound = (minSideLength == -1) ? 128 :(int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
	    if (upperBound < lowerBound) {  
	        // return the larger one when there is no overlapping zone.  
	        return lowerBound;  
	    }  
	    if ((maxNumOfPixels == -1) && (minSideLength == -1)) {  
	        return 1;  
	    } else if (minSideLength == -1) {  
	        return lowerBound;  
	    } else {  
	        return upperBound;  
	    }  
	}  

	/**爱听支持的音乐格式*/
    public static boolean isAvailableMusicFormat(String filename) {
    	return (filename.endsWith(".mp3") || filename.endsWith(".wav") || filename.endsWith(".flac")
    			|| filename.endsWith(".aac") || filename.endsWith(".m4a") || filename.endsWith(".ape") || filename.endsWith(".wma"));
    }
	
    public static List<String> getStorageRootPathList(Context context ){
	      List<String > mountedPaths=new ArrayList<String>();
	      String mountedState= Environment.getExternalStorageState();
	      if(!Environment.MEDIA_MOUNTED.equals(mountedState)){
	    	  AppUtils.showToast(context, "SD卡还没准备好");
	    	  String path= Environment.getExternalStorageDirectory().getAbsolutePath();
	   		  mountedPaths.add(path);
	   		  SharedPreferencesUtil.setConfig(context, "imusic",  "storagePath", path);
	    	  return mountedPaths;
	      }
	      
	      try {
	    	  File pub=context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
	 	      if(!pub.exists()){
	 	    	  pub.mkdirs();
	 	      }
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
	      if(Build.VERSION.SDK_INT>=9){
		    	  try {
		    		  StorageManager storageManager=(StorageManager)context.getSystemService(Context.STORAGE_SERVICE);
		    		 Method[] methods=storageManager.getClass().getMethods();
		    		 boolean hasGetVolumeListMethod=false;
		    		 boolean hasGetVolumeStateMethod=false;
		    		 for (Method method : methods) {
		    			    if("getVolumeList".equals(method.getName())){
		    			    	    hasGetVolumeListMethod=true;
		    			    }
		    			    if("getVolumeState".equals(method.getName())){
		    			    	  hasGetVolumeStateMethod=true;
		    			    }
				  }
		    		 if(hasGetVolumeListMethod&&hasGetVolumeStateMethod){
		    			 Method getVolumeListMethod=storageManager.getClass().getMethod("getVolumeList");
		    			 Method getVolumeStateMethod=storageManager.getClass().getMethod("getVolumeState",String.class);
		    			 Object[] objects= (Object[]) getVolumeListMethod.invoke(storageManager);
		    			 for (Object object : objects) {
		    				 Method method=object.getClass().getMethod("getPath");
		    				 String path=(String) method.invoke(object);
		    				 String state=(String)getVolumeStateMethod.invoke(storageManager, path);
		    				 if(Environment.MEDIA_MOUNTED.equals(state)){
		    					 mountedPaths.add(path);
		    				 }
		    			 }
		    		 }
		    	  } catch (Exception e) {
		    		  e.printStackTrace();
					  String path= Environment.getExternalStorageDirectory().getAbsolutePath();
		  	   		  mountedPaths.add(path);
		  	   		  SharedPreferencesUtil.setConfig(context, "imusic",  "storagePath", path);
		    	  }
	      }

		  if(mountedPaths ==null || mountedPaths.size()==0){
			  String path= Environment.getExternalStorageDirectory().getAbsolutePath();
	   		  mountedPaths.add(path);
	   		  SharedPreferencesUtil.setConfig(context, "imusic",  "storagePath", path);
     }
		return mountedPaths;
	}

    public static boolean copyfile(File fromFile, File toFile, Boolean rewrite) {
        if (!fromFile.exists()) {
            return false;
        }
        if (!fromFile.isFile()) {
            return false;
        }
        if (!fromFile.canRead()) {
            return false;
        }
        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }
        if (toFile.exists() && rewrite) {
            toFile.delete();
        }
        try {
            FileInputStream fosfrom = new FileInputStream(fromFile);
            FileOutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c); //将内容写到新文件当中
            }
            fosfrom.close();
            fosto.close();
        } catch (Exception ex) {
            Log.e("readfile", ex.getMessage());
            return false;
        }
        return true;
    }



//    /**
//     * 添加日志
//     * @param str
//     * @return
//     */
//    public synchronized static boolean  addLog(String str)
//    {
//        str = DateUtil.getTime(new Date(System.currentTimeMillis()))+"--------------->:"+str;
//        System.out.println(str);
//        str=str+" \n";
//        FileOperation fp = null;
//        try{
//            fp = new FileOperation("Log", null);
//            fp.initFile(DateUtil.getDate()+"log");
//            if(fp.extraAddLine(str.getBytes()))
//            {
//                System.gc();
//                return true;
//            }else {
//
//                System.gc();
//                return false;
//            }
//        }catch (OutOfMemoryError error) {
//            error.printStackTrace();
//            return false;
//        } catch(Exception e)
//        {
//            e.printStackTrace();
//            return false;
//        }finally
//        {
//            if(fp!=null)
//            {
//                fp.closeFile();
//            }
//        }
//    }

}
