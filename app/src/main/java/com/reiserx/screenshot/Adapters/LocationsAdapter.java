package com.reiserx.screenshot.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.reiserx.screenshot.Advertisements.NativeAds;
import com.reiserx.screenshot.Models.ScreenshotLabels;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.ViewModels.ScreenshotsViewModel;
import com.reiserx.screenshot.databinding.LocationListLayoutBinding;

import java.util.List;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {

    private List<ScreenshotLabels> itemList;
    Context context;

    public final int DATA_CONTENT = 0;
    public static int AD_CONTENT = 1;

    NavController navController;

    public LocationsAdapter(Context context, NavController navController) {
        this.context = context;
        this.navController = navController;
    }

    public void setItemList(List<ScreenshotLabels> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    public List<ScreenshotLabels> getItemList() {
        return itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScreenshotLabels item = itemList.get(position);
        if (item.getType() == DATA_CONTENT) {
            holder.binding.textView26.setText(item.getLabel());
            Glide.with(context).load(item.getFilepath()).into(holder.binding.imageView5);
            holder.binding.textView26.setOnClickListener(view -> {
                Bundle args = new Bundle();
                args.putParcelable("location", item.getLocation());
                args.putString("label", item.getLabel());

                navController.navigate(R.id.action_navigation_search_to_fragment_location_screenshots, args);
            });
        } else if (item.getNativeAd() != null)
            NativeAds.loadPrefetchedOverlayHeadline(context, item.getNativeAd(), holder.binding.placeHolder);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LocationListLayoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = LocationListLayoutBinding.bind(itemView);
        }
    }
}
