package com.example.yang.myphoto4.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileSystem {
    private static final String Util_LOG = makeLogTag(FileSystem.class);

    public static String makeLogTag(Class<?> cls) {
        return cls.getName();
    }

    public static void print(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    /**
     * check if sd card exist
     *
     * @return
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * create dir
     *
     * @param context
     * @param dirName folder name
     * @return
     */
    public static File createFileDir(Context context, String dirName) {
        String filePath;
        // if sd card exist, save to it or save to data
        if (hasSdcard()) {
            // SD card path
            filePath = Environment.getExternalStorageDirectory()
                    + File.separator + dirName;
        } else {
            filePath = context.getCacheDir().getPath() + File.separator
                    + dirName;
        }
        File destDir = new File(filePath);
        if (!destDir.exists()) {
            boolean isCreate = destDir.mkdirs();
            Log.i(Util_LOG, filePath + " has created. " + isCreate);
        }
        return destDir;
    }

    /**
     * delete files
     *
     * @param file
     * @param delThisPath
     */
    public static void delFile(File file, boolean delThisPath) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            if (subFiles != null) {
                int num = subFiles.length;
                // delete file and folder
                for (int i = 0; i < num; i++) {
                    delFile(subFiles[i], true);
                }
            }
        }
        if (delThisPath) {
            file.delete();
        }
    }

    /**
     * get file size by bite
     *
     * @param file
     * @return
     */
    public static long getFileSize(File file) {
        long size = 0;
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] subFiles = file.listFiles();
                if (subFiles != null) {
                    int num = subFiles.length;
                    for (int i = 0; i < num; i++) {
                        size += getFileSize(subFiles[i]);
                    }
                }
            } else {
                size += file.length();
            }
        }
        return size;
    }

    /**
     * save bitmap to dir
     *
     * @param dir
     * @param fileName
     * @param bitmap
     * @throws IOException
     */
    public static void savaBitmap(File dir, String fileName, Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        File file = new File(dir, fileName);
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * if file exists
     *
     * @param dir
     * @param fileName
     * @return
     */
    public static boolean isFileExists(File dir, String fileName) {
        return new File(dir, fileName).exists();
    }
}