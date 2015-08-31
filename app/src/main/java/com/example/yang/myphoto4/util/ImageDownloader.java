package com.example.yang.myphoto4.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import android.util.Log;


public class ImageDownloader {
    private static String TAG = "DownloadService" ;
    public static final int IO_BUFFER_SIZE = 8 * 1024;
    private static final String CACHE_FILENAME_PREFIX = "cache_";
    private static ExecutorService SINGLE_TASK_EXECUTOR = null;
    private static ExecutorService LIMITED_TASK_EXECUTOR = null;
    private static final ExecutorService FULL_TASK_EXECUTOR = null;
    private static final ExecutorService DEFAULT_TASK_EXECUTOR ;
    private static Object lock = new Object();
    static {
        // SINGLE_TASK_EXECUTOR = (ExecutorService)
        // Executors.newSingleThreadExecutor();
        LIMITED_TASK_EXECUTOR = (ExecutorService) Executors
                . newFixedThreadPool(1);
        // FULL_TASK_EXECUTOR = (ExecutorService)
        // Executors.newCachedThreadPool();
        DEFAULT_TASK_EXECUTOR = LIMITED_TASK_EXECUTOR ;
    };

    DownloadStateListener listener;

    private String downloadPath;

    private List<String> listURL;

    private String URL = "http://raptor.kent.ac.uk/~wz57/Ree/drawables/";

    private int size = 0;

    public interface DownloadStateListener {
        public void onFinish();

        public void onFailed();
    }

    public ImageDownloader(String downloadPath, List<String> listURL,
                           DownloadStateListener listener) {
        this.downloadPath = downloadPath;
        this.listURL = listURL;
        this.listener = listener;
    }



    public void startDownload() {
        // detact if path exists
        File downloadDirectory = new File(downloadPath );
        if (!downloadDirectory.exists()) {
            downloadDirectory.mkdirs();
        }

        for (final String url : listURL) {

            try {

                DEFAULT_TASK_EXECUTOR.execute(new Runnable() {

                    @Override
                    public void run() {
                        downloadBitmap(URL+url+".png");
                    }
                });
            } catch (RejectedExecutionException e) {
                e.printStackTrace();
                Log. e(TAG, "thread pool rejected error");
                listener.onFailed();
            } catch (Exception e) {
                e.printStackTrace();
                listener.onFailed();
            }

        }

    }

    /**
     *
     * @param urlString
     * @return
     */
    private File downloadBitmap(String urlString) {
        String fileName = urlString+".png";

        final File cacheFile = new File(createFilePath(new File(
                downloadPath), fileName));

        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;

        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            final InputStream in = new BufferedInputStream(
                    urlConnection.getInputStream(), IO_BUFFER_SIZE);
            out = new BufferedOutputStream(new FileOutputStream(cacheFile),
                    IO_BUFFER_SIZE);

            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }

            statDownloadNum();
            return cacheFile;

        } catch (final IOException e) {

            Log. e(TAG, "download " + urlString + " error");
            listener.onFailed();

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (out != null ) {
                try {
                    out.close();
                } catch (final IOException e) {
                    Log. e(TAG, "Error in downloadBitmap - " + e);
                }
            }
        }

        return null ;
    }

    /**
     * Creates a constant cache file path given a target cache directory and an
     * image key.
     *
     * @param cacheDir
     * @param key
     * @return
     */
    public static String createFilePath(File cacheDir, String key) {
        try {
            // Use URLEncoder to ensure we have a valid filename, a tad hacky
            // but it will do for
            // this example
            return cacheDir.getAbsolutePath() + File.separator + CACHE_FILENAME_PREFIX
                    + URLEncoder.encode(key.replace("*", ""), "UTF-8" );
        } catch (final UnsupportedEncodingException e) {
            Log. e(TAG, "createFilePath - " + e);
        }

        return null ;
    }



    private void statDownloadNum() {
        synchronized (lock ) {
            size++;
            if (size == listURL .size()) {
                Log. d(TAG, "download finished total " + size);

                DEFAULT_TASK_EXECUTOR.shutdownNow();

                listener.onFinish();
            }
        }
    }
}