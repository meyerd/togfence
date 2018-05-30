package de.hosenhasser.togfence.togfence;

import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import static de.hosenhasser.togfence.togfence.MainTogfence.GeofenceContentProvider;

public class GeofenceElement {
    public Integer _id;
    public String name;
    public LatLng position;
    public int radius;
    public String toggl_project;
    public String toggl_tag;
    public boolean active;

    public GeofenceElement(Integer _id, String name, LatLng position, int radius,
                           String toggl_project, String toggl_tag, boolean active) {
        this.name = name;
        this._id = _id;
        this.position = position;
        this.radius = radius;
        this.toggl_project = toggl_project;
        this.toggl_tag = toggl_tag;
        this.active = active;
    }
}
