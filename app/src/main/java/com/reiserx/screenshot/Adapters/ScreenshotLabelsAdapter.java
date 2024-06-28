package com.reiserx.screenshot.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.reiserx.screenshot.Activities.ui.home.GalleryFragment;
import com.reiserx.screenshot.Advertisements.NativeAds;
import com.reiserx.screenshot.Models.ScreenshotLabels;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Utils.DataStoreHelper;
import com.reiserx.screenshot.databinding.ImageLabelAdplaceholderBinding;
import com.reiserx.screenshot.databinding.ImageLabelLayoutBinding;

import java.util.List;

public class ScreenshotLabelsAdapter extends RecyclerView.Adapter {

    Context context;
    List<ScreenshotLabels> data;
    NavController navController;

    public static int DATA_CONTENT = 0;
    public static int AD_CONTENT = 1;

    public ScreenshotLabelsAdapter(Context context, NavController navController) {
        this.context = context;
        this.navController = navController;
    }

    public List<ScreenshotLabels> getData() {
        return data;
    }

    public void setData(List<ScreenshotLabels> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == DATA_CONTENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.image_label_layout, parent, false);
            return new ImagesViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.image_label_adplaceholder, parent, false);
            return new AdsViewHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ScreenshotLabels screenshots = data.get(position);

        if (viewHolder.getClass() == ImagesViewHolder.class) {
            ImagesViewHolder holder = (ImagesViewHolder) viewHolder;
        if (screenshots.getFilepath() != null) {
            Glide.with(context)
                    .load(screenshots.getFilepath())
                    .thumbnail(0.01f)
                    .into(holder.binding.imageView);
        } else {
            Glide.with(context)
                    .load(R.drawable.baseline_crop_square_24)
                    .into(holder.binding.imageView);
        }

        holder.binding.textView6.setText(screenshots.getLabel());
        holder.binding.imageHolder.setOnClickListener(view -> {
            Bundle args = new Bundle();
            if (screenshots.getLabel().equals("All screenshots"))
                args.putString("label", "null");
            else
                args.putString("label", screenshots.getLabel());
            navController.navigate(R.id.action_navigation_home_to_navigation_gallery, args);
        });

        } else if (viewHolder.getClass() == AdsViewHolder.class) {
            if (screenshots.getNativeAd() != null) {
                AdsViewHolder holder = (AdsViewHolder) viewHolder;
                NativeAds.loadPrefetchedAds(context, screenshots.getNativeAd(), holder.binding.imageHolder);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        ScreenshotLabels model = data.get(position);
        if (model.getType() == DATA_CONTENT) {
            return DATA_CONTENT;
        } else {
            return AD_CONTENT;
        }
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        else
            return 0;
    }

    public class ImagesViewHolder extends RecyclerView.ViewHolder {

        ImageLabelLayoutBinding binding;

        public ImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ImageLabelLayoutBinding.bind(itemView);
        }
    }

    public class AdsViewHolder extends RecyclerView.ViewHolder {

        ImageLabelAdplaceholderBinding binding;

        public AdsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ImageLabelAdplaceholderBinding.bind(itemView);
        }
    }
}