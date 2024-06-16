package com.reiserx.screenshot.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.reiserx.screenshot.Activities.OCRActivity;
import com.reiserx.screenshot.Activities.ui.TextDrawable;
import com.reiserx.screenshot.Advertisements.NativeAds;
import com.reiserx.screenshot.Models.Screenshots;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Utils.SaveBitmap;
import com.reiserx.screenshot.databinding.ImageLabelAdplaceholderBinding;
import com.reiserx.screenshot.databinding.ImageLayoutBinding;
import com.stfalcon.imageviewer.StfalconImageViewer;

import java.util.ArrayList;
import java.util.List;

public class ScreenshotsAdapter extends RecyclerView.Adapter {

    Context context;
    List<Screenshots> data;

    FloatingActionButton shareFAB, deleteFAB, ocrFAB;
    ExtendedFloatingActionButton mAddFab;
    TextView shareFAB_Text, deleteFAB_Text, ocrFAB_Text;
    Boolean isAllFabsVisible;
    Screenshots temp;
    StfalconImageViewer stfalconImageViewer;

    public static int SILENT_SCREENSHOT = 1;
    public static int DEFAULT_SCREENSHOT = 2;

    private int SCREENSHOT_TYPE = 0;

    public static int ITEM_TYPE_DATA = 0;
    public static int ITEM_TYPE_AD = 1;

    ActivityResultLauncher<IntentSenderRequest> deleteResultLauncher;

    public ScreenshotsAdapter(Context context, int SCREENSHOT_TYPE, ActivityResultLauncher<IntentSenderRequest> deleteResultLauncher) {
        this.context = context;
        this.SCREENSHOT_TYPE = SCREENSHOT_TYPE;
        this.deleteResultLauncher = deleteResultLauncher;
    }

    public void setData(List<Screenshots> data) {
        if (this.data != null)
            this.data.clear();
        this.data = data;
    }

    public List<Screenshots> getData() {
        return data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_DATA) {
            View view = LayoutInflater.from(context).inflate(R.layout.image_layout, parent, false);
            return new ImagesViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.image_label_adplaceholder, parent, false);
            return new AdsViewHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Screenshots screenshots = data.get(position);
        if (viewHolder.getClass() == ImagesViewHolder.class) {
            ImagesViewHolder holder = (ImagesViewHolder) viewHolder;
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
            stfalconImageViewer = new StfalconImageViewer.Builder<>(context, data, (imageView, imageUrl) -> {
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
        } else if (viewHolder.getClass() == AdsViewHolder.class) {
            if (screenshots.getNativeAd() != null) {
                AdsViewHolder holder = (AdsViewHolder) viewHolder;
                NativeAds.loadPrefetchedAds(context, screenshots.getNativeAd(), holder.binding.imageHolder);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        Screenshots model = data.get(position);
        if (model.getType() == ITEM_TYPE_DATA) {
            return ITEM_TYPE_DATA;
        } else {
            return ITEM_TYPE_AD;
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

        ImageLayoutBinding binding;

        public ImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ImageLayoutBinding.bind(itemView);
        }
    }

    public class AdsViewHolder extends RecyclerView.ViewHolder {

        ImageLabelAdplaceholderBinding binding;

        public AdsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ImageLabelAdplaceholderBinding.bind(itemView);
        }
    }

    void customViewOps(View view) {
        mAddFab = view.findViewById(R.id.add_fab);
        shareFAB = view.findViewById(R.id.share_image_fab);
        deleteFAB = view.findViewById(R.id.delete_file_fab);
        shareFAB_Text = view.findViewById(R.id.share_image_text);
        deleteFAB_Text = view.findViewById(R.id.delete_image_text);
        ocrFAB = view.findViewById(R.id.ocr_FAB);
        ocrFAB_Text = view.findViewById(R.id.ocr_image_text);

        shareFAB.setVisibility(View.GONE);
        deleteFAB.setVisibility(View.GONE);
        shareFAB_Text.setVisibility(View.GONE);
        deleteFAB_Text.setVisibility(View.GONE);
        ocrFAB.setVisibility(View.GONE);
        ocrFAB_Text.setVisibility(View.GONE);

        TextDrawable textDrawable = new TextDrawable("T");
        textDrawable.setTextColor(context.getColor(R.color.white));
        textDrawable.setTextSize(50);
        textDrawable.setFont(context, R.font.source_serif_pro_semibold);
        ocrFAB.setImageDrawable(textDrawable);

        isAllFabsVisible = false;

        mAddFab.shrink();

        mAddFab.setOnClickListener(view13 -> {
            if (!isAllFabsVisible) {
                shareFAB.show();
                deleteFAB.show();
                ocrFAB.show();
                shareFAB_Text.setVisibility(View.VISIBLE);
                deleteFAB_Text.setVisibility(View.VISIBLE);
                ocrFAB_Text.setVisibility(View.VISIBLE);

                mAddFab.extend();

                isAllFabsVisible = true;
            } else {
                shareFAB.hide();
                deleteFAB.hide();
                ocrFAB.hide();
                shareFAB_Text.setVisibility(View.GONE);
                deleteFAB_Text.setVisibility(View.GONE);
                ocrFAB_Text.setVisibility(View.GONE);

                mAddFab.shrink();

                isAllFabsVisible = false;
            }
        });
        shareFAB.setOnClickListener(view1 -> shareImage(temp));
        deleteFAB.setOnClickListener(view12 -> saveImage());
        ocrFAB.setOnClickListener(view14 -> {
            Intent intent = new Intent(context, OCRActivity.class);
            intent.setData(Uri.fromFile(temp.getFile()));
            context.startActivity(intent);
        });
    }

    void saveImage() {
        if (SCREENSHOT_TYPE == 1) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Delete screenshot");
            alert.setMessage("Are you sure you want to delete this screenshot?");
            alert.setPositiveButton("delete", (dialogInterface, i) -> {
            if (temp.getFile().delete()) {
                data.remove(temp);
                stfalconImageViewer.close();
                notifyDataSetChanged();
            }
            });
            alert.setNegativeButton("cancel", null);
            alert.show();
        } else if (SCREENSHOT_TYPE == 2) {
                ArrayList<Uri> arrayList = new ArrayList<>();
                arrayList.add(Uri.parse(temp.getFile().getAbsolutePath()));
                SaveBitmap.deleteScreenshotDCIM(context, arrayList, deleteResultLauncher);
        }
    }

    void shareImage(Screenshots screenshots) {
        Uri imgUri;

        if (SCREENSHOT_TYPE == 1)
            imgUri = FileProvider.getUriForFile(context, "com.reiserx.screenshot.fileprovider", screenshots.getFile());
        else
            imgUri = FileProvider.getUriForFile(context, "com.reiserx.screenshot.fileprovider", screenshots.getFile());
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
            // Handle case where no suitable app is installed
            // or the user cancels the operation
            e.printStackTrace();
        }
    }

    public void closeImageViewer() {
        stfalconImageViewer.close();
    }
}