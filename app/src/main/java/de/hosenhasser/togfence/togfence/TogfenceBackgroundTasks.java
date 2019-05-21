package de.hosenhasser.togfence.togfence;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import de.hosenhasser.togfence.togfence.Toggl.TogglRetrofit;

public class TogfenceBackgroundTasks extends JobService {
    private static final String PACKAGE_NAME = "de.hosenhasser.togfence.togfence";
    static final String MAIN_SHOWN_KEY = PACKAGE_NAME + ".MAIN_SHOWN_KEY";

    private static final String ACTION_START_GEOFENCING = "de.hosenhasser.togfence.togfence.action.START_GEOFENCING";
    private static final String ACTION_STOP_GEOFENCING = "de.hosenhasser.togfence.togfence.action.STOP_GEOFENCING";
    private static final String NO_USER_POPUP = "de.hosenhasser.togfence.togfence.extra.NO_USER_POPUP";

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
            Intent intent = new Intent(context, GeofencesManagerService.class);
            intent.setAction(ACTION_STOP_GEOFENCING);
            intent.putExtra(NO_USER_POPUP, true);
            context.startService(intent);
//            GeofencesManagerService.startActionStopGeofencing(context);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(context, GeofencesManagerService.class);
                    intent.setAction(ACTION_START_GEOFENCING);
                    intent.putExtra(NO_USER_POPUP, true);
                    context.startService(intent);
//                    GeofencesManagerService.startActionStartGeofencing(context);
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
