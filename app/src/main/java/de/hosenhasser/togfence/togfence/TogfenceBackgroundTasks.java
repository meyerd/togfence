package de.hosenhasser.togfence.togfence;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.preference.PreferenceManager;

public class TogfenceBackgroundTasks extends JobService {
    private static final String PACKAGE_NAME = "de.hosenhasser.togfence.togfence";
    static final String MAIN_SHOWN_KEY = PACKAGE_NAME + ".MAIN_SHOWN_KEY";

    public TogfenceBackgroundTasks() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Context context = getApplicationContext();


        Boolean mainShown = PreferenceManager.getDefaultSharedPreferences(
                TogfenceApplication.getAppContext())
                .getBoolean(MAIN_SHOWN_KEY, false);
//        if (mainShown) {
//            UpdateTogglInformation.startUpdateTogglInformation(context);
//        }
        StartBackgroundTasksOnBootReceiver.scheduleTogfenceBackgroundTasks(context);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
