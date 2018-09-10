package de.hosenhasser.togfence.togfence;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class GeofenceElementListLoader extends AsyncTaskLoader<List<GeofenceElement>> {
    private static final String LOG_TAG = GeofenceElementListLoader.class.getSimpleName();
    private List<GeofenceElement> mGeofence;
    private ContentResolver mContentResolver;
    private Cursor mCursor;

    public GeofenceElementListLoader(Context context, Uri uri, ContentResolver contentResolver){
        super(context);
        mContentResolver = contentResolver;
    }

    @Override
    public List<GeofenceElement> loadInBackground() {
        ArrayList<GeofenceElement> mGeofenceElements = new ArrayList<>();
        String[] mProjection = {
                GeofencesContentProvider._ID,
                GeofencesContentProvider.NAME,
                GeofencesContentProvider.LAT,
                GeofencesContentProvider.LON,
                GeofencesContentProvider.RADIUS,
                GeofencesContentProvider.ACTIVE,
                GeofencesContentProvider.TOGGL_PROJECT,
                GeofencesContentProvider.TOGGL_PROJECT_TEXT,
                GeofencesContentProvider.TOGGL_TAGS
        };
        String mSelectionClause = null;
        String[] mSelectionArgs = {""};
        String mSortOrder = GeofencesContentProvider.NAME;
        Cursor mCursor = mContentResolver.query(
                GeofencesContentProvider.CONTENT_URI,
                mProjection,
                mSelectionClause,
                null,
                mSortOrder
        );

        if(mCursor.moveToFirst()) {
            do {
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
                int mTogglProject = mCursor.getInt(
                        mCursor.getColumnIndex(GeofencesContentProvider.TOGGL_PROJECT)
                );
                String mTogglProjectText = mCursor.getString(
                        mCursor.getColumnIndex(GeofencesContentProvider.TOGGL_PROJECT_TEXT)
                );
                int mTogglTags = mCursor.getInt(
                        mCursor.getColumnIndex(GeofencesContentProvider.TOGGL_TAGS)
                );
                String mTogglTagsText = mCursor.getString(
                        mCursor.getColumnIndex(GeofencesContentProvider.TOGGL_TAGS_TEXT)
                );
                int mRunningEntryId = mCursor.getInt(
                        mCursor.getColumnIndex(GeofencesContentProvider.RUNNING_ENTRY_ID)
                );
                boolean mActive =
                        (mCursor.getInt(
                                mCursor.getColumnIndex(GeofencesContentProvider.ACTIVE)
                        ) == 1);
                mGeofenceElements.add(
                        new GeofenceElement(
                                mId, mName, mPosition, mRadius, mTogglProject, mTogglProjectText,
                                mTogglTags, mTogglTagsText, mRunningEntryId, mActive
                        )
                );
            } while (mCursor.moveToNext());
        }

        return mGeofenceElements;
    }

    @Override
    public void deliverResult(List<GeofenceElement> geofence) {

        if(isReset()){
            if(geofence != null){
                mCursor.close();
            }
        }

        List<GeofenceElement> oldGeofenceElements = mGeofence;

        if(mGeofence == null || mGeofence.size() == 0){
            Log.d(LOG_TAG, "++++++++++++ NO DATA RETURNED ");
        }

        // Saving the country
        mGeofence = geofence;

        if(isStarted()){
            super.deliverResult(geofence);
        }

        if(oldGeofenceElements != null && oldGeofenceElements != geofence){
            mCursor.close();
        }
    }

    @Override
    protected void onStartLoading() {
        if(mGeofence != null){
            deliverResult(mGeofence);
        }

        if(takeContentChanged() || mGeofence == null){
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();
        if(mCursor != null){
            mCursor.close();
        }

        mGeofence = null;
    }

    @Override
    public void onCanceled(List<GeofenceElement> geofence) {
        super.onCanceled(geofence);
        if(mCursor != null){
            mCursor.close();
        }
    }

    @Override
    public void forceLoad() {
        super.forceLoad();
    }
}