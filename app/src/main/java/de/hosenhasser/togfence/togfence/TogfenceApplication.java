package de.hosenhasser.togfence.togfence;

import android.app.Application;
import android.content.Context;

public class TogfenceApplication extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        TogfenceApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return TogfenceApplication.context;
    }
}