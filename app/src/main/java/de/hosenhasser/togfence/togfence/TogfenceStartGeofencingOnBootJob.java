package de.hosenhasser.togfence.togfence;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class TogfenceStartGeofencingOnBootJob extends JobService {
    private static final String PACKAGE_NAME = "de.hosenhasser.togfence.togfence";
    static final String MAIN_SHOWN_KEY = PACKAGE_NAME + ".MAIN_SHOWN_KEY";

    private static final String TAG = "StartGeofencingOnBoot";

    public TogfenceStartGeofencingOnBootJob() {
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        Context context = getApplicationContext();
        Boolean mainShown = PreferenceManager.getDefaultSharedPreferences(
                TogfenceApplication.getAppContext())
                .getBoolean(MAIN_SHOWN_KEY, false);
        Boolean autostartGeofences = PreferenceManager.getDefaultSharedPreferences(
                TogfenceApplication.getAppContext())
                .getBoolean("autostart_geofencing_on_boot", false);

        Toast.makeText(context, "boot job", Toast.LENGTH_LONG).show();

        Log.i(TAG, "starting geofencing on boot check");
        if (mainShown && autostartGeofences) {
            Log.i(TAG, "starting geofencing on boot");
            GeofencesManagerService.startActionStartGeofencing(context);
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
