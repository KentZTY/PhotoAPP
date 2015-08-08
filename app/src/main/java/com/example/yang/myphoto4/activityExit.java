package com.example.yang.myphoto4;

/**
 * Created by Yang on 08/08/2015.
 */
import java.util.LinkedList;
import java.util.List;
import android.app.Activity;
import android.app.Application;
/**
 * To exit all activity
 */
public class activityExit extends Application {

    private List<Activity> mList = new LinkedList<Activity>();

    private static activityExit instance;

    private activityExit(){}

    public synchronized static activityExit getInstance(){
        if (null == instance) {
            instance = new activityExit();
        }
        return instance;
    }

    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exit() {
        try {
            for (Activity activity:mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}