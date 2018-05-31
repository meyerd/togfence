package de.hosenhasser.togfence.togfence;

import android.content.ContentResolver;
import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import de.hosenhasser.togfence.togfence.GeofenceElement;

public final class GeofenceElementListFromContentResolver {
    private GeofenceElementListFromContentResolver() {};

    public static List<GeofenceElement> getAllElementsList(ContentResolver
                                                                   contentResolver) {
        ArrayList<GeofenceElement> mGeofenceElements = new ArrayList<>();
        String[] mProjection = {
                GeofencesContentProvider._ID,
                GeofencesContentProvider.NAME,
                GeofencesContentProvider.LAT,
                GeofencesContentProvider.LON,
                GeofencesContentProvider.RADIUS,
                GeofencesContentProvider.ACTIVE,
                GeofencesContentProvider.TOGGL_PROJECT,
                GeofencesContentProvider.TOGGL_TAGS
        };
        String mSelectionClause = null;
        String[] mSelectionArgs = {""};
        String mSortOrder = GeofencesContentProvider.NAME;
        Cursor mCursor = contentResolver.query(
                GeofencesContentProvider.CONTENT_URI,
                mProjection,
                mSelectionClause,
                null,
                mSortOrder
        );
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
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