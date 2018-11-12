package de.hosenhasser.togfence.togfence;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
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

public class MainTogfence extends AppCompatActivity implements GeofenceElementFragment.OnListFragmentInteractionListener {
    private final static String TAG = "MainTogfence";

    private final static int REQUEST_LOCATION_PERMISSION_CODE = 5551;

    private static final String PACKAGE_NAME = "de.hosenhasser.togfence.togfence";
    static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";
    static final String MAIN_SHOWN_KEY = PACKAGE_NAME + ".MAIN_SHOWN_KEY";

    private Menu mOptionsMenu = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_togfence);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(view.getContext(), GeofenceEditor.class);
                startActivity(intent);
            }
        });

        Button startbutton = (Button) findViewById(R.id.start_geofence_button);
        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeofencesManagerService.startActionStartGeofencing(getApplicationContext());
            }
        });
        Button stopbutton = (Button) findViewById(R.id.stop_geofence_button);
        stopbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeofencesManagerService.startActionStopGeofencing(getApplicationContext());
            }
        });
        Button updatetoggldatabutton = (Button) findViewById(R.id.update_toggl_data);
        updatetoggldatabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               UpdateTogglInformation.startUpdateTogglInformation(getApplicationContext());
            }
        });
        Button testbutton1 = (Button) findViewById(R.id.testbutton1);
        testbutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TogglRetrofit tr = TogglRetrofit.getInstance();
//                Geofence g = mGeofenceList.get(0);
//                String req_id = g.getRequestId();
//                int id = Integer.parseInt(req_id);
//                GeofenceElement ge = GeofencesContentProvider.getGeofenceElement(getContentResolver(), id);
//                tr.createNewStartTimeEntry(getContentResolver(), ge);
                tr.getCurrentTimeEntry(getContentResolver());
            }
        });
        Button testbutton2 = (Button) findViewById(R.id.testbutton2);
        testbutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 StartBackgroundTasksOnBootReceiver.scheduleStartGeofencingOnBoot(getApplicationContext());
//                TogglRetrofit tr = TogglRetrofit.getInstance();
//                Geofence g = mGeofenceList.get(0);
//                String req_id = g.getRequestId();
//                int id = Integer.parseInt(req_id);
//                GeofenceElement ge = GeofencesContentProvider.getGeofenceElement(getContentResolver(), id);
//                tr.createNewStopTimeEntry(getContentResolver(), ge);
            }
        });

        checkGooglePlayServices(this);
    }

    private void checkGooglePlayServices(Activity activity) {
        Task task = GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(activity);
    }

    @Override
    public void onStart() {
        super.onStart();

        GeofencesManagerService.startActionUpdateGeofences(getApplicationContext());

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            GeofencesManagerService.startActionPerformPendingGeofenceTask(getApplicationContext());
        }

        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(MAIN_SHOWN_KEY, true)
                .apply();
        setStartStopGeofencesIcon();

        UpdateTogglInformation.startUpdateTogglInformation(getApplicationContext());
        UpdateTogglInformation.startUpdateTogglInformation(getApplicationContext());
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

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
//                    showSnackbar(R.string.permission_rationale, android.R.string.ok,
            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainTogfence.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.INTERNET},
                                    REQUEST_LOCATION_PERMISSION_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainTogfence.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.INTERNET},
                    REQUEST_LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onListFragmentInteraction(GeofenceElement item) {
        Log.i(TAG, "click on: " + item.name);
        Intent intent = new Intent(this, GeofenceEditor.class);
        intent.putExtra(GeofenceEditor.EXTRA_GEOFENCE_ELEMENT_ID, item._id);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_LOCATION_PERMISSION_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted.");
                GeofencesManagerService.startActionPerformPendingGeofenceTask(getApplicationContext());
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent(view.getContext(), SettingsActivity.class);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    private void showSnackbar(final String text) {
        View container = findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
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

    public void setStartStopGeofencesIcon() {
        if (mOptionsMenu != null) {
            boolean geofencesAdded = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                    GEOFENCES_ADDED_KEY, false);

            MenuItem startStopMenuItem = mOptionsMenu.findItem(R.id.action_geofences_active);
            startStopMenuItem.setIcon(geofencesAdded ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
        }
    }

    private void toggleStartStopGeofences() {
        boolean geofencesAdded = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                GEOFENCES_ADDED_KEY, false);
        if(geofencesAdded) {
            GeofencesManagerService.startActionStopGeofencing(getApplicationContext());
        } else {
            GeofencesManagerService.startActionStartGeofencing(getApplicationContext());
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_togfence, menu);
        mOptionsMenu = menu;
        setStartStopGeofencesIcon();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_geofences_active) {
            toggleStartStopGeofences();
        } else if (id == R.id.action_refresh_list) {
            UpdateTogglInformation.startUpdateRunningTask(getApplicationContext());
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!servicesConnected(this)) {
            Log.i(TAG, "Google Services not connected.");
        } else {
            Log.i(TAG, "Google Services available.");
        }
        setStartStopGeofencesIcon();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
