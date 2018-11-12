package de.hosenhasser.togfence.togfence;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class GeofenceElementFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private OnListFragmentInteractionListener mListener;
    private MyGeofenceElementRecyclerViewAdapter mCursorAdapter;
    private RecyclerView mRecyclerView;

    public GeofenceElementFragment() {
    }

    public static GeofenceElementFragment newInstance(int columnCount) {
        GeofenceElementFragment fragment = new GeofenceElementFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_geofenceelement_list, container, false);

        // set layout manager adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            Cursor mGeofenceCursor = GeofencesContentProvider.getAllGeofenceElementsCursor(
                    getActivity().getContentResolver());

            mCursorAdapter = new MyGeofenceElementRecyclerViewAdapter(
                    getContext(), mGeofenceCursor, mListener);

            mRecyclerView.setAdapter(mCursorAdapter);

            getLoaderManager().initLoader(0, null, this);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(GeofenceElement item);
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] mProjection = {
                GeofencesContentProvider._ID,
                GeofencesContentProvider.NAME,
                GeofencesContentProvider.LAT,
                GeofencesContentProvider.LON,
                GeofencesContentProvider.RADIUS,
                GeofencesContentProvider.ACTIVE,
                GeofencesContentProvider.TOGGL_PROJECT,
                GeofencesContentProvider.TOGGL_PROJECT_TEXT,
                GeofencesContentProvider.TOGGL_TAGS,
                GeofencesContentProvider.TOGGL_TAGS_TEXT,
                GeofencesContentProvider.RUNNING_ENTRY_ID
        };
        String mSelectionClause = null;
        String[] mSelectionArgs = {""};
        String mSortOrder = GeofencesContentProvider.NAME;
        return new CursorLoader(
                getActivity(),
                GeofencesContentProvider.CONTENT_URI,
                mProjection,
                null,
                null,
                mSortOrder
        );
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // this automatically reloads your adapter and updates your grid-view
        mCursorAdapter.swapCursor(cursor);
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
