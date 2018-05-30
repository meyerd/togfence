package de.hosenhasser.togfence.togfence;

import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import de.hosenhasser.togfence.togfence.GeofenceElement;

import static de.hosenhasser.togfence.togfence.MainTogfence.GeofenceContentProvider;

public final class GeofenceElementListFromContentProvider {
    private GeofenceElementListFromContentProvider() {};

    public static List<GeofenceElement> getAllElementsList(GeofencesContentProvider
                                                                   contentProvider) {
        ArrayList<GeofenceElement> mGeofenceElements = new ArrayList<>();
        String[] mProjection = {
                GeofenceContentProvider._ID,
                GeofenceContentProvider.NAME,
                GeofenceContentProvider.LAT,
                GeofenceContentProvider.LON,
                GeofenceContentProvider.RADIUS,
                GeofenceContentProvider.ACTIVE,
                GeofenceContentProvider.TOGGL_PROJECT,
                GeofenceContentProvider.TOGGL_TAGS
        };
        String mSelectionClause = null;
        String[] mSelectionArgs = {""};
        String mSortOrder = "ORDER BY " + GeofenceContentProvider.NAME + " asc";
        Cursor mCursor = contentProvider.query(
                GeofencesContentProvider.CONTENT_URI,
                mProjection,
                mSelectionClause,
                mSelectionArgs,
                mSortOrder
        );
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                Integer mId = mCursor.getInt(
                        mCursor.getColumnIndex(GeofenceContentProvider._ID)
                );
                String mName = mCursor.getString(
                        mCursor.getColumnIndex(GeofenceContentProvider.NAME)
                );
                Float mLat = mCursor.getFloat(
                        mCursor.getColumnIndex(GeofenceContentProvider.LAT)
                );
                Float mLon = mCursor.getFloat(
                        mCursor.getColumnIndex(GeofenceContentProvider.LON)
                );
                LatLng mPosition = new LatLng(mLat, mLon);
                int mRadius = mCursor.getInt(
                        mCursor.getColumnIndex(GeofenceContentProvider.RADIUS)
                );
                String mTogglProject = mCursor.getString(
                        mCursor.getColumnIndex(GeofenceContentProvider.TOGGL_PROJECT)
                );
                String mTogglTags = mCursor.getString(
                        mCursor.getColumnIndex(GeofenceContentProvider.TOGGL_TAGS)
                );
                boolean mActive =
                        mCursor.getInt(
                                mCursor.getColumnIndex(GeofenceContentProvider.ACTIVE)
                        ) == 1 ? true : false;
                mGeofenceElements.add(
                        new GeofenceElement(
                                mId, mName, mPosition, mRadius, mTogglProject, mTogglTags, mActive
                        )
                );
            }
        }
        return mGeofenceElements;
    }
}