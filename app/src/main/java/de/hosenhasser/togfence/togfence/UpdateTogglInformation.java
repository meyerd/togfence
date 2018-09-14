package de.hosenhasser.togfence.togfence;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import de.hosenhasser.togfence.togfence.Toggl.TogglRetrofit;

public class UpdateTogglInformation extends IntentService {
    private static final String ACTION_UPDATE_TOGGL_INFORMATION = "de.hosenhasser.togfence.togfence.action.UPDATE_TOGGL_INFORMATION";

    public UpdateTogglInformation() {
        super("UpdateTogglInformation");
    }

    public static void startUpdateTogglInformation(Context context) {
        Intent intent = new Intent(context, UpdateTogglInformation.class);
        intent.setAction(ACTION_UPDATE_TOGGL_INFORMATION);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_TOGGL_INFORMATION.equals(action)) {
                handleUpdateTogglInformation();
            }
        }
    }

    private void handleUpdateTogglInformation() {
        TogglRetrofit tr = TogglRetrofit.getInstance();
        tr.updateProjectsAndTags(getApplicationContext());
    }
}
