package com.reiserx.screenshot.Adapters;

import static com.reiserx.screenshot.Activities.ui.search.LabelScreenshotsFragment.LABEL_NAME;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.reiserx.screenshot.Activities.ui.search.LabelScreenshotsFragment;
import com.reiserx.screenshot.Advertisements.NativeAds;
import com.reiserx.screenshot.Models.SearchListModel;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Utils.DataStoreHelper;
import com.reiserx.screenshot.databinding.ImageLabelAdplaceholderBinding;
import com.reiserx.screenshot.databinding.SearchListHeaderBinding;
import com.reiserx.screenshot.databinding.SearchListItemBinding;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<SearchListModel> data;

    public final int DATA_CONTENT = 0;
    public final int HEADER = 1;
    public static int AD_CONTENT = 2;

    NavController navController;

    public SearchAdapter(Context context, ArrayList<SearchListModel> data, NavController navController) {
        this.context = context;
        this.data = data;
        this.navController = navController;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == DATA_CONTENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.search_list_item, parent, false);
            return new DataViewHolder(view);
        } else if (viewType == HEADER){
            View view = LayoutInflater.from(context).inflate(R.layout.search_list_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.image_label_adplaceholder, parent, false);
            return new AdsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SearchListModel model = data.get(position);

        if (holder.getClass() == DataViewHolder.class) {
            DataViewHolder viewHolder = (DataViewHolder) holder;
            viewHolder.binding.textView11.setText(model.getName());
            if (model.getImageCount() > 1)
                viewHolder.binding.textView13.setText(String.valueOf(model.getImageCount()).concat(" screenshots"));
            else
                viewHolder.binding.textView13.setText(String.valueOf(model.getImageCount()).concat(" screenshot"));
            viewHolder.binding.getRoot().setOnClickListener(view -> {
                DataStoreHelper dataStoreHelper = new DataStoreHelper();
                dataStoreHelper.putIntValue(LabelScreenshotsFragment.LABEL_ID, model.getId());
                dataStoreHelper.putStringValue(LABEL_NAME, model.getName());
                navController.navigate(R.id.action_navigation_search_to_navigation_labelscreenshots);
            });
        } else if (holder.getClass() == HeaderViewHolder.class) {
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            viewHolder.binding.textView12.setText(model.getName());
        } else if (holder.getClass() == AdsViewHolder.class) {
            if (model.getNativeAd() != null) {
                AdsViewHolder viewHolder = (AdsViewHolder) holder;
                NativeAds.loadNoIconPrefetchedAds(context, model.getNativeAd(), viewHolder.binding.imageHolder);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        SearchListModel model = data.get(position);
        if (model.getType() == DATA_CONTENT) {
            return DATA_CONTENT;
        } else if (model.getType() == HEADER){
            return HEADER;
        } else
            return AD_CONTENT;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {

        SearchListItemBinding binding;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SearchListItemBinding.bind(itemView);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        SearchListHeaderBinding binding;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SearchListHeaderBinding.bind(itemView);
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