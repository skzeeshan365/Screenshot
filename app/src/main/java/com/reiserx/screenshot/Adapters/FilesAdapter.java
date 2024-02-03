package com.reiserx.screenshot.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.reiserx.screenshot.Activities.ui.settings.FileFragment;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Utils.DataStoreHelper;
import com.reiserx.screenshot.databinding.FileListItemBinding;

import java.util.List;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FileViewHolder> {

    Context context;
    List<String> data;
    DataStoreHelper dataStoreHelper;
    FileFragment fragment;

    public FilesAdapter(Context context, FileFragment fileFragment) {
        this.context = context;
        dataStoreHelper = new DataStoreHelper();
        this.fragment = fileFragment;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public FilesAdapter.FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.file_list_item, parent, false);
        return new FilesAdapter.FileViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FilesAdapter.FileViewHolder holder, int position) {
        String filename = data.get(position);

        holder.binding.textView2.setText(filename);

        if (dataStoreHelper.getStringValue(FileFragment.DEFAULT_STORAGE_KEY, null).equals(filename))
            holder.binding.imgSelected.setVisibility(View.VISIBLE);
        else
            holder.binding.imgSelected.setVisibility(View.INVISIBLE);

        holder.binding.itemHolder.setOnClickListener(view -> {
            dataStoreHelper.putStringValue(FileFragment.DEFAULT_STORAGE_KEY, filename);
            fragment.dismiss();
            fragment.onFileDismissListener.onDismiss(null);
        });
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        else
            return 0;
    }

    public class FileViewHolder extends RecyclerView.ViewHolder {

        FileListItemBinding binding;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = FileListItemBinding.bind(itemView);
        }
    }
}