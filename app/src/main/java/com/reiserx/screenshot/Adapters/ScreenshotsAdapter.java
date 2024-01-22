package com.reiserx.screenshot.Adapters;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.reiserx.screenshot.Models.Screenshots;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.databinding.ImageLayoutBinding;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.listeners.OnImageChangeListener;

import java.util.List;

public class ScreenshotsAdapter extends RecyclerView.Adapter<ScreenshotsAdapter.ImagesViewHolder> {

    Context context;
    List<Screenshots> data;

    FloatingActionButton shareFAB, saveFAB;
    ExtendedFloatingActionButton mAddFab;
    TextView shareFAB_Text, saveFAB_Text;
    Boolean isAllFabsVisible;
    Screenshots temp;

    public ScreenshotsAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Screenshots> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ScreenshotsAdapter.ImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_layout, parent, false);
        return new ScreenshotsAdapter.ImagesViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ScreenshotsAdapter.ImagesViewHolder holder, int position) {
        Screenshots screenshots = data.get(position);
        Glide.with(context)
                .load(screenshots.getFilename())
                .thumbnail(0.01f)
                .into(holder.binding.imageView);

        holder.binding.imageHolder.setOnClickListener(view -> {
            Context materialContext = new ContextThemeWrapper(context, com.google.android.material.R.style.Theme_MaterialComponents);
            LayoutInflater inflater = LayoutInflater.from(materialContext);

            View customView = inflater.inflate(R.layout.image_viewer_overlay, null, false);
            customViewOps(customView);
            temp = data.get(position);
            new StfalconImageViewer.Builder<>(context, data, (imageView, imageUrl) -> {
                Glide.with(context).load(imageUrl.getFilename()).into(imageView);
            })
                    .withStartPosition(position)
                    .withTransitionFrom(holder.binding.imageView)
                    .withHiddenStatusBar(true)
                    .withOverlayView(customView)
                    .withImageChangeListener(position1 -> {
                        temp = data.get(position1);
                    })
                    .show();
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

        ImageLayoutBinding binding;

        public ImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ImageLayoutBinding.bind(itemView);
        }
    }

    void customViewOps(View view) {
        mAddFab = view.findViewById(R.id.add_fab);
        shareFAB = view.findViewById(R.id.share_image_fab);
        saveFAB = view.findViewById(R.id.save_file_fab);
        shareFAB_Text = view.findViewById(R.id.share_image_text);
        saveFAB_Text = view.findViewById(R.id.save_image_text);

        shareFAB.setVisibility(View.GONE);
        saveFAB.setVisibility(View.GONE);
        shareFAB_Text.setVisibility(View.GONE);
        saveFAB_Text.setVisibility(View.GONE);

        isAllFabsVisible = false;

        mAddFab.shrink();

        mAddFab.setOnClickListener(view13 -> {
            if (!isAllFabsVisible) {
                shareFAB.show();
                saveFAB.show();
                shareFAB_Text.setVisibility(View.VISIBLE);
                saveFAB_Text.setVisibility(View.VISIBLE);

                mAddFab.extend();

                isAllFabsVisible = true;
            } else {
                shareFAB.hide();
                saveFAB.hide();
                shareFAB_Text.setVisibility(View.GONE);
                saveFAB_Text.setVisibility(View.GONE);

                mAddFab.shrink();

                isAllFabsVisible = false;
            }
        });
        shareFAB.setOnClickListener(view1 -> shareImage(temp));
        saveFAB.setOnClickListener(view12 -> saveImage());
    }

    void saveImage() {
        Toast.makeText(context, "save", Toast.LENGTH_SHORT).show();
    }

    void shareImage(Screenshots screenshots) {
        Uri imgUri = Uri.parse(screenshots.getFile().getAbsolutePath());
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Install https://play.google.com/store/apps/details?id=com.reiserx.screenshot on Google play store for easy screenshots");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imgUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            // Start the activity with a chooser
            context.startActivity(Intent.createChooser(shareIntent, "Share this with"));
        } catch (ActivityNotFoundException e) {
            Log.d("gsfsfsfs", e.toString());
            // Handle case where no suitable app is installed
            // or the user cancels the operation
            e.printStackTrace();
        }
    }
}