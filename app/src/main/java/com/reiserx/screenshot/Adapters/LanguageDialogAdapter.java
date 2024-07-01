package com.reiserx.screenshot.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.reiserx.screenshot.Interfaces.LanguageSelect;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.databinding.SearchListItemBinding;

import java.util.ArrayList;
import java.util.List;

public class LanguageDialogAdapter extends RecyclerView.Adapter<LanguageDialogAdapter.LangViewHolder> {

    Context context;
    List<String> data;
    LanguageSelect languageSelect;

    public LanguageDialogAdapter(Context context, ArrayList<String> data, LanguageSelect languageSelect) {
        this.context = context;
        this.data = data;
        this.languageSelect = languageSelect;
    }

    @NonNull
    @Override
    public LanguageDialogAdapter.LangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_list_item, parent, false);
        return new LanguageDialogAdapter.LangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageDialogAdapter.LangViewHolder holder, int position) {
        String lang = data.get(position);

        holder.binding.textView11.setText(lang);
        holder.binding.imageView4.setVisibility(View.GONE);
        holder.binding.textView13.setVisibility(View.GONE);
        holder.binding.getRoot().setOnClickListener(view -> {
            languageSelect.onLanguageSelected(lang);
        });
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        else
            return 0;
    }

    public class LangViewHolder extends RecyclerView.ViewHolder {

        SearchListItemBinding binding;

        public LangViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SearchListItemBinding.bind(itemView);
        }
    }
}