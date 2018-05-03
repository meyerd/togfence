package de.hosenhasser.togfence.togfence;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;


public class GeofenceRegistrationService extends IntentService {
    public static String TAG = "GeofenceRegistrationService";

    public GeofenceRegistrationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            if (geofencingEvent.hasError()) {
                Log.d(TAG, "GeofencingEvent error " + geofencingEvent.getErrorCode());
            } else {
                int transaction = geofencingEvent.getGeofenceTransition();
                List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();
                Geofence geofence = geofences.get(0);
//                if (transaction == Geofence.GEOFENCE_TRANSITION_ENTER && geofence.getRequestId().equals(Constants.GEOFENCE_ID_STAN_UNI)) {
//                    Log.d(TAG, "You are inside Stanford University");
//                } else {
//                    Log.d(TAG, "You are outside Stanford University");
//                }
            }

        }
    }
}
