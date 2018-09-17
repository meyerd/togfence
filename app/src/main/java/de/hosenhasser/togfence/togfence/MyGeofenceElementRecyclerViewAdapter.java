package de.hosenhasser.togfence.togfence;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import de.hosenhasser.togfence.togfence.GeofenceElementFragment.OnListFragmentInteractionListener;
import de.hosenhasser.togfence.togfence.Toggl.TogglContentProvider;
import de.hosenhasser.togfence.togfence.Toggl.TogglProject;

public class MyGeofenceElementRecyclerViewAdapter extends RecyclerView.Adapter<MyGeofenceElementRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private boolean mDataValid;
    private int mRowIdColumn;
    private DataSetObserver mDataSetObserver;
    private final OnListFragmentInteractionListener mListener;

    public MyGeofenceElementRecyclerViewAdapter(Context context, Cursor cursor, OnListFragmentInteractionListener listener) {
        mContext = context;
        mCursor = cursor;
        mDataValid = (cursor != null);
        mRowIdColumn = mDataValid ? mCursor.getColumnIndex(GeofencesContentProvider._ID) : -1;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (mCursor != null) {
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
        mListener = listener;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_geofenceelement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }

        GeofenceElement ge = GeofenceElement.fromCursor(mCursor);

        holder.mItem = ge;
        holder.mNameView.setText(ge.name);
        holder.mItemNumberView.setText(String.format("%d", position));
        holder.mActiveView.setText(ge.active ? "active" : "");
        holder.mLatitudeView.setText(String.format("%.5f", ge.position.latitude));
        holder.mLongitudeView.setText(String.format("%.5f", ge.position.longitude));
        holder.mRadiusView.setText(String.format("%d", ge.radius));
        holder.mTogglProjectView.setText(ge.toggl_project_text);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        final Cursor oldCursor = mCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (mCursor != null) {
            if (mDataSetObserver != null) {
                mCursor.registerDataSetObserver(mDataSetObserver);
            }
            mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mRowIdColumn = -1;
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return oldCursor;
    }

    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null && mCursor.moveToPosition(position)) {
            return mCursor.getLong(mRowIdColumn);
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mItemNumberView;
        public final TextView mNameView;
        public final TextView mActiveView;
        public final TextView mLatitudeView;
        public final TextView mLongitudeView;
        public final TextView mRadiusView;
        public final TextView mTogglProjectView;
        public GeofenceElement mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mItemNumberView = (TextView) view.findViewById(R.id.item_number);
            mNameView = (TextView) view.findViewById(R.id.name);
            mActiveView = (TextView) view.findViewById(R.id.active);
            mLongitudeView = (TextView) view.findViewById(R.id.latitude);
            mLatitudeView = (TextView) view.findViewById(R.id.longitude);
            mRadiusView = (TextView) view.findViewById(R.id.radius);
            mTogglProjectView = (TextView) view.findViewById(R.id.toggl_project);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
