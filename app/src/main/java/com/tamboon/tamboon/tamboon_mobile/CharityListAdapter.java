package com.tamboon.tamboon.tamboon_mobile;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Milion on 12/27/2017.
 */

public class CharityListAdapter extends RecyclerView.Adapter<CharityListAdapter.ViewHolder> {
    private List<CharityObject> charityArray;
    private CharityListListener mListener;

    public interface CharityListListener {
        void charitySelected(CharityObject charity);
    }

    public CharityListAdapter(List<CharityObject> array, CharityListListener listener) {
        this.charityArray = array;
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.charity_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mItem = charityArray.get(position);
        holder.mNameTextView.setText(holder.mItem.getName());
        Log.d("123", "onBindViewHolder: " + holder.mNameTextView.getText().toString());
    }

    @Override
    public int getItemCount() {
        return charityArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView mNameTextView;
        public CharityObject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameTextView = (TextView) mView.findViewById(R.id.charityNameTextView);
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        int position = getLayoutPosition();
                        mListener.charitySelected(charityArray.get(position));
                    }
                }
            });
        }
    }
}
