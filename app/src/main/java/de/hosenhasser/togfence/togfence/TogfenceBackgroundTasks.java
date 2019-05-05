package de.hosenhasser.togfence.togfence;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import de.hosenhasser.togfence.togfence.Toggl.TogglRetrofit;

public class TogfenceBackgroundTasks extends JobService {
    private static final String PACKAGE_NAME = "de.hosenhasser.togfence.togfence";
    static final String MAIN_SHOWN_KEY = PACKAGE_NAME + ".MAIN_SHOWN_KEY";

    private static final String TAG = "TogfenceBackgroundTasks";

    public TogfenceBackgroundTasks() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        final Context context = getApplicationContext();


        Boolean mainShown = PreferenceManager.getDefaultSharedPreferences(
                TogfenceApplication.getAppContext())
                .getBoolean(MAIN_SHOWN_KEY, false);
        Boolean autostartGeofences = PreferenceManager.getDefaultSharedPreferences(
                TogfenceApplication.getAppContext())
                .getBoolean("autostart_geofencing_on_boot", false);
//        if (mainShown) {
//            UpdateTogglInformation.startUpdateTogglInformation(context);
//        }
        if(mainShown && autostartGeofences) {
            Log.i(TAG, "removing and adding geofences");
            GeofencesManagerService.startActionStopGeofencing(context);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    GeofencesManagerService.startActionStartGeofencing(context);
                }
            }, 1000);
        }
        StartBackgroundTasksOnBootReceiver.scheduleTogfenceBackgroundTasks(context);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
