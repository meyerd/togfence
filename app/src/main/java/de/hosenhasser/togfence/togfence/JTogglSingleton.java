package de.hosenhasser.togfence.togfence;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import ch.simas.jtoggl.JToggl;

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
        } else {
            jToggl = new JToggl(
                    PreferenceManager.getDefaultSharedPreferences(TogfenceApplication.getAppContext()).getString("toggl_api_key", "INVALID")
            );
        }
        return true;
    }

    JTogglSingleton() {
        checkValid();
    }

}
