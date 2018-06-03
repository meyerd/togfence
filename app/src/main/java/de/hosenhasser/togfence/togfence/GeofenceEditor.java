package de.hosenhasser.togfence.togfence;

import android.content.ContentResolver;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

public class GeofenceEditor extends AppCompatActivity {
    public static final String TAG = "GeofenceEditor";

    public static final String EXTRA_GEOFENCE_ELEMENT_ID = "de.hosenhasser.togfence.togfence.GeofenceEditor.EXTRA_GEOFENCE_ELEMENT_ID";

    public static final int PLACE_PICKER_REQUEST = 1;

    private boolean mNewGeofence = false;
    private GeofenceElement mGeofenceElement;

    private EditText name_edit_text;
    private TextView latitude_text_view;
    private TextView longitude_text_view;
    private EditText radius_edit_text;
    private EditText toggl_project_edit_text;
    private EditText toggl_tags_edit_text;
    private CheckBox active_check_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence_editor);

        Intent intent = getIntent();
        if (intent != null) {
            int extra_id = intent.getIntExtra(GeofenceEditor.EXTRA_GEOFENCE_ELEMENT_ID, -1);

            if (extra_id < 0) {
                mNewGeofence = true;
                Log.i(TAG, "editor for new geofence element");
            } else {
                mGeofenceElement = GeofencesContentProvider.getGeofenceElement(
                        getContentResolver(), extra_id);
                if(mGeofenceElement != null) {
                    Log.i(TAG, "editor for: " + mGeofenceElement.name);
                    mNewGeofence = false;
                } else {
                    Log.w(TAG, "could not retrieve geofence element: " + extra_id);
                    mNewGeofence = true;
                }
            }

            if (mNewGeofence) {
                mGeofenceElement = new GeofenceElement(
                        getResources().getString(R.string.new_geofence_element));
            }
        }
        if (mNewGeofence || intent == null) {
            mGeofenceElement = new GeofenceElement(
                    getResources().getString(R.string.new_geofence_element));
        }

        name_edit_text = (EditText) findViewById(R.id.name_edit_text);
        latitude_text_view = (TextView) findViewById(R.id.latitude);
        longitude_text_view = (TextView) findViewById(R.id.longitude);
        radius_edit_text = (EditText) findViewById(R.id.radius_edit_text);
        toggl_project_edit_text = (EditText) findViewById(R.id.toggl_project_edit_text);
        toggl_tags_edit_text = (EditText) findViewById(R.id.toggl_tags_edit_text);
        active_check_box = (CheckBox) findViewById(R.id.active_check_box);
        updateTextfields();

        Button deletebutton = (Button) findViewById(R.id.delete_button);
        deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteGeofenceButtonHandler(view);
            }
        });
        Button savebutton = (Button) findViewById(R.id.save_button);
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveGeofenceButtonHandler(view);
            }
        });

        Button picklocationbutton = (Button) findViewById(R.id.pick_location_button);
        picklocationbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickLocationButtonHandler(view);
            }
        });
    }

    private void updateTextfields() {
        name_edit_text.setText(mGeofenceElement.name);
        latitude_text_view.setText(String.format("%.5f", mGeofenceElement.position.latitude));
        longitude_text_view.setText(String.format("%.5f", mGeofenceElement.position.longitude));
        radius_edit_text.setText(Integer.toString(mGeofenceElement.radius));
        toggl_project_edit_text.setText(mGeofenceElement.toggl_project);
        toggl_tags_edit_text.setText(mGeofenceElement.toggl_tag);
        active_check_box.setChecked(mGeofenceElement.active);
    }

    public void deleteGeofenceButtonHandler(View view) {
        if (!mNewGeofence) {
            GeofencesContentProvider.deleteGeofenceElement(
                    getContentResolver(), mGeofenceElement._id
            );
        }
        this.onBackPressed();
    }

    public void saveGeofenceButtonHandler(View view) {
        mGeofenceElement.name = name_edit_text.getText().toString();
        mGeofenceElement.active = active_check_box.isChecked();
        mGeofenceElement.radius = Integer.parseInt(radius_edit_text.getText().toString());
        mGeofenceElement.toggl_tag = toggl_tags_edit_text.getText().toString();
        mGeofenceElement.toggl_project = toggl_project_edit_text.getText().toString();
//        LatLng position = new LatLng(
//                Double.parseDouble(latitude_text_view.getText().toString()),
//                Double.parseDouble(longitude_text_view.getText().toString())
//        );
//        mGeofenceElement.position = position;
        if (mNewGeofence) {
            GeofencesContentProvider.insertGeofenceElement(
                    getContentResolver(), mGeofenceElement
            );
        } else {
            GeofencesContentProvider.updateGeofenceElement(
                    getContentResolver(), mGeofenceElement
            );
        }
        this.finish();
    }

    public void pickLocationButtonHandler(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, getResources().getString(R.string.play_services_not_available), Toast.LENGTH_LONG).show();
        } catch (GooglePlayServicesRepairableException e) {
            Toast.makeText(this, getResources().getString(R.string.play_services_problems), Toast.LENGTH_LONG).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                LatLng position = place.getLatLng();
//                String toastMsg = String.format("Place: %s", place.getName());
//                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                mGeofenceElement.position = position;
                updateTextfields();
            }
        }
    }
}
