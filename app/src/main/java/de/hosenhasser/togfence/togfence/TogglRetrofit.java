package de.hosenhasser.togfence.togfence;

import android.preference.PreferenceManager;
import android.widget.Toast;


public class TogglRetrofit {
    private static volatile TogglRetrofit sJToggleSingletonSingletonInstance;

    public static TogglRetrofit getInstance() {
        if (sJToggleSingletonSingletonInstance == null) {
            synchronized (TogglRetrofit.class) {
                if (sJToggleSingletonSingletonInstance == null) sJToggleSingletonSingletonInstance = new TogglRetrofit();
            }
        }

        return sJToggleSingletonSingletonInstance;
    }

    protected TogglRetrofit readResolve() {
        return getInstance();
    }


    private boolean checkApiKey() {
        String toggl_api_key = PreferenceManager.getDefaultSharedPreferences(TogfenceApplication.getAppContext()).getString("toggl_api_key", "INVALID");
        if (toggl_api_key.equals("INVALID")) {
            return false;
        }
        return true;
    }

    private boolean checkValid() {

        if (!checkApiKey()) {
            Toast.makeText(
                    TogfenceApplication.getAppContext(),
                    TogfenceApplication.getAppContext().getString(R.string.no_valid_toggl_api_key),
                    Toast.LENGTH_LONG).show();
            return false;
        }

//                PreferenceManager.getDefaultSharedPreferences(TogfenceApplication.getAppContext()).getString("toggl_api_key", "INVALID")

        return true;
    }

    TogglRetrofit() {
        checkValid();
    }

    public void createNewStartTimeEntry(String name) {
        if (!checkValid()) {
            return;
        }

//        TimeEntry entry = new TimeEntry();
//        DateTime dt = new DateTime();
//        entry.setStart(dt);
//        entry.setDuration(1);
//        entry.setDescription(
//                TogfenceApplication.getAppContext().getString(R.string.toggl_description_start) +
//                        "|" + name
//        );
//        entry.setCreatedWith(TogfenceApplication.getAppContext().getString(R.string.toggl_created_with));
//
//        entry = jToggl.createTimeEntry(entry);
    }

    public void createNewStopTimeEntry(String name) {
        if (!checkValid()) {
            return;
        }

//        TimeEntry entry = new TimeEntry();
//        DateTime dt = new DateTime();
//        entry.setStart(dt);
//        entry.setDuration(1);
//        entry.setDescription(
//                TogfenceApplication.getAppContext().getString(R.string.toggl_description_stop) +
//                        "|" + name
//        );
//        entry.setCreatedWith(TogfenceApplication.getAppContext().getString(R.string.toggl_created_with));
//
//        entry = jToggl.createTimeEntry(entry);
    }

}
