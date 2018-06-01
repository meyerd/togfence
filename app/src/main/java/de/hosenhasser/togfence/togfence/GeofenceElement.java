package de.hosenhasser.togfence.togfence;

import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

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

    public GeofenceElement() {
        this.name = "";
        this._id = -1;
        this.position = new LatLng(0, 0);
        this.radius = 0;
        this.toggl_project = "";
        this.toggl_tag = "";
        this.active = false;
    }

    public GeofenceElement(String name) {
        this.name = name;
        this._id = -1;
        this.position = new LatLng(0, 0);
        this.radius = 0;
        this.toggl_project = "";
        this.toggl_tag = "";
        this.active = false;
    }

    public static GeofenceElement fromCursor(Cursor mCursor) {
        Integer mId = mCursor.getInt(
                mCursor.getColumnIndex(GeofencesContentProvider._ID)
        );
        String mName = mCursor.getString(
                mCursor.getColumnIndex(GeofencesContentProvider.NAME)
        );
        Float mLat = mCursor.getFloat(
                mCursor.getColumnIndex(GeofencesContentProvider.LAT)
        );
        Float mLon = mCursor.getFloat(
                mCursor.getColumnIndex(GeofencesContentProvider.LON)
        );
        LatLng mPosition = new LatLng(mLat, mLon);
        int mRadius = mCursor.getInt(
                mCursor.getColumnIndex(GeofencesContentProvider.RADIUS)
        );
        String mTogglProject = mCursor.getString(
                mCursor.getColumnIndex(GeofencesContentProvider.TOGGL_PROJECT)
        );
        String mTogglTags = mCursor.getString(
                mCursor.getColumnIndex(GeofencesContentProvider.TOGGL_TAGS)
        );
        boolean mActive =
                (mCursor.getInt(
                        mCursor.getColumnIndex(GeofencesContentProvider.ACTIVE)
                ) == 1);
        return new GeofenceElement(
                mId, mName, mPosition, mRadius, mTogglProject, mTogglTags, mActive);
    }
}
