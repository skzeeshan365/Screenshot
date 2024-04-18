package com.reiserx.screenshot.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.reiserx.screenshot.Activities.ui.home.GalleryFragment;
import com.reiserx.screenshot.Models.ScreenshotLabels;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Utils.DataStoreHelper;
import com.reiserx.screenshot.databinding.ImageLabelLayoutBinding;

import java.util.List;

public class ScreenshotLabelsAdapter extends RecyclerView.Adapter<ScreenshotLabelsAdapter.ImagesViewHolder> {

    Context context;
    List<ScreenshotLabels> data;

    NavController navController;

    public ScreenshotLabelsAdapter(Context context, NavController navController) {
        this.context = context;
        this.navController = navController;
    }

    public void setData(List<ScreenshotLabels> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ScreenshotLabelsAdapter.ImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_label_layout, parent, false);
        return new ScreenshotLabelsAdapter.ImagesViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ScreenshotLabelsAdapter.ImagesViewHolder holder, int position) {
        ScreenshotLabels screenshots = data.get(position);
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
            DataStoreHelper dataStoreHelper = new DataStoreHelper();
            if (screenshots.getLabel().equals("All screenshots"))
                dataStoreHelper.putStringValue(GalleryFragment.SCREENSHOT_LABEL, null);
            else
                dataStoreHelper.putStringValue(GalleryFragment.SCREENSHOT_LABEL, screenshots.getLabel());
            navController.navigate(R.id.action_navigation_home_to_navigation_gallery);
        });
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
}