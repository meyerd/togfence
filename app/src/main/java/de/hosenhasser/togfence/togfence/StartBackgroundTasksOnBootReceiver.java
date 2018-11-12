package de.hosenhasser.togfence.togfence;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class StartBackgroundTasksOnBootReceiver extends BroadcastReceiver {
    private static final String PACKAGE_NAME = "de.hosenhasser.togfence.togfence";
    static final String MAIN_SHOWN_KEY = PACKAGE_NAME + ".MAIN_SHOWN_KEY";

//    PendingIntent pendingIntent;
//
//    private static final int ALARM_REQUEST_CODE = 1338289;

    private static final String TAG = "StartTaskOnBoot";

    public static void scheduleTogfenceBackgroundTasks(Context context) {
        ComponentName serviceComponent = new ComponentName(context, TogfenceBackgroundTasks.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(1000 * 60 * 60 * 2);  // 2 hours
        builder.setOverrideDeadline(1000 * 60 * 60 * 5);  // 5 hours
        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    public static void scheduleStartGeofencingOnBoot(Context context) {
        ComponentName serviceComponent = new ComponentName(context, TogfenceStartGeofencingOnBootJob.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
//        builder.setMinimumLatency(1000 * 60 * 1);  // 1 Minute
//        builder.setOverrideDeadline(1000 * 60 * 10);  // 10 Minutes
        builder.setMinimumLatency(1);  // 1 Minute
        builder.setOverrideDeadline(2);  // 10 Minutes
        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Boolean mainShown = PreferenceManager.getDefaultSharedPreferences(
                    TogfenceApplication.getAppContext())
                    .getBoolean(MAIN_SHOWN_KEY, false);

            Log.i(TAG, "schedule start geofencing on boot");
            scheduleStartGeofencingOnBoot(context);

            if (mainShown) {
                scheduleTogfenceBackgroundTasks(context);
            }
        }
    }
}
