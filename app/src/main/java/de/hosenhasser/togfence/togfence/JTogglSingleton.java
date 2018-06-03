package de.hosenhasser.togfence.togfence;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.text.method.DateTimeKeyListener;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.List;

import ch.simas.jtoggl.JToggl;
import ch.simas.jtoggl.domain.TimeEntry;
import ch.simas.jtoggl.domain.Workspace;

public class JTogglSingleton {
    private static volatile JTogglSingleton sJToggleSingletonSingletonInstance;

    public static JTogglSingleton getInstance() {
        if (sJToggleSingletonSingletonInstance == null) {
            synchronized (JTogglSingleton.class) {
                if (sJToggleSingletonSingletonInstance == null) sJToggleSingletonSingletonInstance = new JTogglSingleton();
            }
        }

        return sJToggleSingletonSingletonInstance;
    }

    protected JTogglSingleton readResolve() {
        return getInstance();
    }

    private static JToggl jToggl;
//    private static Workspace workspace;

    private boolean checkApiKey() {
        String toggl_api_key = PreferenceManager.getDefaultSharedPreferences(TogfenceApplication.getAppContext()).getString("toggl_api_key", "INVALID");
        if (toggl_api_key.equals("INVALID")) {
            return false;
        }
        return true;
    }

    private boolean checkValid() {
        if (jToggl != null) {
            return true;
        }
        if (!checkApiKey()) {
            Toast.makeText(
                    TogfenceApplication.getAppContext(),
                    TogfenceApplication.getAppContext().getString(R.string.no_valid_toggl_api_key),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        jToggl = new JToggl(
                PreferenceManager.getDefaultSharedPreferences(TogfenceApplication.getAppContext()).getString("toggl_api_key", "INVALID")
        );
        jToggl.setThrottlePeriod(500l);
        jToggl.switchLoggingOn();

//        List<Workspace> workspaces = jToggl.getWorkspaces();
//        if (workspaces.size() < 1) {
//            Toast.makeText(
//                    TogfenceApplication.getAppContext(),
//                    TogfenceApplication.getAppContext().getString(R.string.no_toggl_workspaces),
//                    Toast.LENGTH_LONG).show();
//            return false;
//        }
//        workspace = workspaces.get(0);

        return true;
    }

    JTogglSingleton() {
        checkValid();
    }

    public void createNewStartTimeEntry(String name) {
        if (!checkValid()) {
            return;
        }

        TimeEntry entry = new TimeEntry();
        DateTime dt = new DateTime();
        entry.setStart(dt);
        entry.setDuration(1);
        entry.setDescription(
                TogfenceApplication.getAppContext().getString(R.string.toggl_description_start) +
                        "|" + name
        );
        entry.setCreatedWith(TogfenceApplication.getAppContext().getString(R.string.toggl_created_with));

        entry = jToggl.createTimeEntry(entry);
    }

    public void createNewStopTimeEntry(String name) {
        if (!checkValid()) {
            return;
        }

        TimeEntry entry = new TimeEntry();
        DateTime dt = new DateTime();
        entry.setStart(dt);
        entry.setDuration(1);
        entry.setDescription(
                TogfenceApplication.getAppContext().getString(R.string.toggl_description_stop) +
                        "|" + name
        );
        entry.setCreatedWith(TogfenceApplication.getAppContext().getString(R.string.toggl_created_with));

        entry = jToggl.createTimeEntry(entry);
    }

}
