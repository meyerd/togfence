package de.hosenhasser.togfence.togfence;

import android.Manifest;
import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import de.hosenhasser.togfence.togfence.Toggl.TogglRetrofit;

public class GeofencesManagerService extends IntentService implements OnCompleteListener<Void> {
    private final static String TAG = "GeofencesManagerService";

    private static final String ACTION_START_GEOFENCING = "de.hosenhasser.togfence.togfence.action.START_GEOFENCING";
    private static final String ACTION_STOP_GEOFENCING = "de.hosenhasser.togfence.togfence.action.STOP_GEOFENCING";
    private static final String ACTION_UPDATE_GEOFENCES = "de.hosenhasser.togfence.togfence.action.UPDATE_GEOFENCES";
    private static final String ACTION_PERFORM_PENDING_GEOFENCE_TASK = "de.hosenhasser.togfence.togfence.action.PERFORM_PENDING_GEOFENCE_TASK";

//    private static final String EXTRA_PARAM1 = "de.hosenhasser.togfence.togfence.extra.PARAM1";
//    private static final String EXTRA_PARAM2 = "de.hosenhasser.togfence.togfence.extra.PARAM2";

    private GeofencingClient mGeofencingClient;
    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;

    private enum PendingGeofenceTask {
        ADD, REMOVE, NONE
    }

    private static final String PACKAGE_NAME = "de.hosenhasser.togfence.togfence";
    static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";
    static final String MAIN_SHOWN_KEY = PACKAGE_NAME + ".MAIN_SHOWN_KEY";

    private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;

    private boolean isMonitoring = false;

    private Context mContext;

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        mContext = getApplicationContext();
        mGeofencePendingIntent = null;
        mGeofenceList = new ArrayList<>();

        mGeofencingClient = LocationServices.getGeofencingClient(mContext);

        mPendingGeofenceTask = PendingGeofenceTask.NONE;
        return super.onStartCommand(intent, flags, startId);
    }

    public GeofencesManagerService() {
        super("GeofencesManagerService");
    }

    public static void startActionStartGeofencing(Context context) {
        Intent intent = new Intent(context, GeofencesManagerService.class);
        intent.setAction(ACTION_START_GEOFENCING);
//        intent.putExtra(EXTRA_PARAM1, param1);
//        intent.putExtra(EXTRA_PARAM2, param2);
//        ContextCompat.startForegroundService(intent);
        context.startService(intent);
    }

    public static void startActionStopGeofencing(Context context) {
        Intent intent = new Intent(context, GeofencesManagerService.class);
        intent.setAction(ACTION_STOP_GEOFENCING);
        context.startService(intent);
    }

    public static void startActionUpdateGeofences(Context context) {
        Intent intent = new Intent(context, GeofencesManagerService.class);
        intent.setAction(ACTION_UPDATE_GEOFENCES);
        context.startService(intent);
    }

    public static void startActionPerformPendingGeofenceTask(Context context) {
        Intent intent = new Intent(context, GeofencesManagerService.class);
        intent.setAction(ACTION_PERFORM_PENDING_GEOFENCE_TASK);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START_GEOFENCING.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionStartGeofencing();
            } else if (ACTION_STOP_GEOFENCING.equals(action)) {
                handleActionStopGeofencing();
            } else if (ACTION_UPDATE_GEOFENCES.equals(action)) {
                handleActionUpdateGeofences();
            } else if (ACTION_PERFORM_PENDING_GEOFENCE_TASK.equals(action)) {
                handleActionPerformPendingGeofenceTask();
            }
        }
    }

    private void handleActionStartGeofencing() {
        addGeofencesHandler();
//        if (!checkPermissions()) {
//            requestPermissions();
//            return;
//        }
//        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
//                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.i(TAG, "Geofencing started.");
////                        Snackbar.make(findViewById(R.id.toolbar), "Geofencing started", Snackbar.LENGTH_SHORT)
////                                .setAction("Action", null).show();
//                    }
//                })
//                .addOnFailureListener(this, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e(TAG, "Adding geofences failed: " + e.toString() + "(" +
//                                e.getMessage() + ")");
////                        Snackbar.make(findViewById(R.id.toolbar), "Geofence failed, make sure that location access is granted and network location is enabled.",
////                                // Snackbar.LENGTH_LONG)
////                                60 * 1000)
////                                .setAction("Dismiss", null).show();
//                    }
//                });
    }

    private void handleActionStopGeofencing() {
        removeGeofencesHandler();
//        mGeofencingClient.removeGeofences(getGeofencePendingIntent())
//                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.i(TAG, "Geofences removed.");
////                        Snackbar.make(findViewById(R.id.toolbar), "Geofences removed", Snackbar.LENGTH_SHORT)
////                                .setAction("Action", null).show();
//                    }
//                })
//                .addOnFailureListener(this, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e(TAG, "Removing geofences failed.");
////                        Snackbar.make(findViewById(R.id.toolbar), "Geofence remove failed", Snackbar.LENGTH_LONG)
////                                .setAction("Action", null).show();
//                    }
//                });
    }

    private void handleActionUpdateGeofences() {
        refreshGeofenceList();
    }

    private void handleActionPerformPendingGeofenceTask() {
        performPendingGeofenceTask();
    }

    private GeofencingRequest getGeofencingRequest() {
        if (mGeofenceList.size() < 1) {
            Toast.makeText(
                    TogfenceApplication.getAppContext(),
                    TogfenceApplication.getAppContext().getString(R.string.no_active_geofence),
                    Toast.LENGTH_LONG).show();
            Log.e(TAG, "No active geofences.");
        }
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    private boolean checkPermissions() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
//            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                        != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    public void addGeofencesHandler() {
//        if (!checkPermissions()) {
//            mPendingGeofenceTask = MainTogfence.PendingGeofenceTask.ADD;
//            requestPermissions();
//            return;
//        }
        refreshGeofenceList();
        addGeofences();
    }

    @SuppressWarnings("MissingPermission")
    private void addGeofences() {
        if(!getMainShown())
            return;
//        if (!checkPermissions()) {
//            showSnackbar(getString(R.string.insufficient_permissions));
//            return;
//        }

        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnCompleteListener(this);
    }

    public void removeGeofencesHandler() {
//        if (!checkPermissions()) {
//            mPendingGeofenceTask = MainTogfence.PendingGeofenceTask.REMOVE;
//            requestPermissions();
//            return;
//        }
        removeGeofences();
    }

    @SuppressWarnings("MissingPermission")
    private void removeGeofences() {
        if(!getMainShown())
            return;

//        if (!checkPermissions()) {
//            showSnackbar(getString(R.string.insufficient_permissions));
//            return;
//        }

        mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
    }

    private boolean getMainShown() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                MAIN_SHOWN_KEY, false);
    }

    private boolean getGeofencesAdded() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                GEOFENCES_ADDED_KEY, false);
    }

    private void updateGeofencesAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(GEOFENCES_ADDED_KEY, added)
                .apply();
    }

    private void performPendingGeofenceTask() {
        if (mPendingGeofenceTask == GeofencesManagerService.PendingGeofenceTask.ADD) {
            addGeofences();
        } else if (mPendingGeofenceTask == GeofencesManagerService.PendingGeofenceTask.REMOVE) {
            removeGeofences();
        }
    }

    public void refreshGeofenceList() {
        int ctr = 0;
        List<GeofenceElement> mGeofenceElements =
                GeofencesContentProvider.getAllGeofenceElementsList(
                        getContentResolver());
        mGeofenceList.clear();
        for (GeofenceElement ge : mGeofenceElements) {
            if (ge.active) {
                mGeofenceList.add(new Geofence.Builder()
                        // Set the request ID of the geofence. This is a string to identify this
                        // geofence.
                        .setRequestId(Integer.toString(ge._id))

                        .setCircularRegion(
                                ge.position.latitude,
                                ge.position.longitude,
                                ge.radius
                        )
                        .setNotificationResponsiveness(
                                Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString(
                                        "responsiveness", "300")) * 1000)
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setLoiteringDelay(5 * 60 * 1000)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                        .build());
                ctr += 1;
            }
        }

        Log.i(TAG, Integer.toString(ctr) + " geofences added to list.");
    }

    private boolean servicesConnected(Context context) {
        // Check that Google Play services is available
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (ConnectionResult.SUCCESS == resultCode) {
            // Handle success
            return true;
        } else {
            GoogleApiAvailability.getInstance().showErrorNotification(context, resultCode);
            return false;
        }
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        mPendingGeofenceTask = GeofencesManagerService.PendingGeofenceTask.NONE;
        if (task.isSuccessful()) {
            updateGeofencesAdded(!getGeofencesAdded());
//            setButtonsEnabledState();

            int messageId = getGeofencesAdded() ? R.string.geofences_added :
                    R.string.geofences_removed;
            Toast.makeText(getApplicationContext(), getString(messageId), Toast.LENGTH_SHORT).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this, task.getException());
            Log.w(TAG, errorMessage);
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }
}
