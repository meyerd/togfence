package de.hosenhasser.togfence.togfence;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.hosenhasser.togfence.togfence.GeofenceElementFragment.OnListFragmentInteractionListener;
import de.hosenhasser.togfence.togfence.dummy.DummyContent.DummyItem;

import java.util.List;

public class MyGeofenceElementRecyclerViewAdapter extends RecyclerView.Adapter<MyGeofenceElementRecyclerViewAdapter.ViewHolder> {

    private final List<GeofenceElement> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyGeofenceElementRecyclerViewAdapter(List<GeofenceElement> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_geofenceelement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).name);
        holder.mItemNumberView.setText(Integer.toString(position));
        holder.mLatitudeView.setText(Double.toString(mValues.get(position).position.latitude));
        holder.mLongitudeView.setText(Double.toString(mValues.get(position).position.longitude));
        holder.mRadiusView.setText(Integer.toString(mValues.get(position).radius));

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
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mItemNumberView;
        public final TextView mNameView;
        public final TextView mLatitudeView;
        public final TextView mLongitudeView;
        public final TextView mRadiusView;
        public GeofenceElement mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mItemNumberView = (TextView) view.findViewById(R.id.item_number);
            mNameView = (TextView) view.findViewById(R.id.name);
            mLongitudeView = (TextView) view.findViewById(R.id.latitude);
            mLatitudeView = (TextView) view.findViewById(R.id.longitude);
            mRadiusView = (TextView) view.findViewById(R.id.radius);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
