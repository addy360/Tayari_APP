package com.lockminds.tayari.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.lockminds.tayari.R;
import com.lockminds.tayari.Tools;
import com.lockminds.tayari.model.Restaurant;
import com.lockminds.tayari.utils.ItemAnimation;

import java.util.ArrayList;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  implements Filterable {

    private List<Restaurant> items = new ArrayList<>();
    private List<Restaurant> itemsFiltered = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    itemsFiltered = items;
                } else {
                    List<Restaurant> filteredList = new ArrayList<>();
                    for (Restaurant row : items) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getBusiness_name().toLowerCase().contains(charString.toLowerCase()) ) {
                            filteredList.add(row);
                        }
                    }

                    itemsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = itemsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                itemsFiltered = (ArrayList<Restaurant>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Restaurant obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public RestaurantAdapter(Context context, List<Restaurant> items, int animation_type) {
        this.items = items;
        this.itemsFiltered = items;
        ctx = context;
        this.animation_type = animation_type;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurants, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int pos) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            Restaurant p = items.get(pos);
            view.name.setText(p.getBusiness_name());
            view.location.setText(p.getBusiness_location());
            view.address.setText(p.getBusiness_address());
            Tools.displayImageBusiness(ctx,view.logo,p.getBusiness_banner());
            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(pos), pos);
                    }
                }
            });
            setAnimation(view.itemView, pos);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return itemsFiltered.size();
    }

    private int lastPosition = -1;
    private boolean on_attach = true;

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView address,name,location;
        public ImageView logo;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            address = (TextView) v.findViewById(R.id.business_address);
            name = (TextView) v.findViewById(R.id.business_name);
            location = (TextView) v.findViewById(R.id.business_location);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
            logo = (ImageView) v.findViewById(R.id.business_logo);
        }
    }

}