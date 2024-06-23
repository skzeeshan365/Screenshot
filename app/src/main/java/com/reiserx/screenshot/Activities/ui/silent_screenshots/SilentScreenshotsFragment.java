package com.reiserx.screenshot.Activities.ui.silent_screenshots;

import static com.reiserx.screenshot.Adapters.ScreenshotsAdapter.SILENT_SCREENSHOT;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;

import com.reiserx.screenshot.Adapters.ScreenshotsAdapter;
import com.reiserx.screenshot.Models.Screenshots;
import com.reiserx.screenshot.ViewModels.ScreenshotsViewModel;
import com.reiserx.screenshot.databinding.FragmentSilentScreenshotsBinding;

import java.util.List;
import java.util.Random;

public class SilentScreenshotsFragment extends Fragment {

    private FragmentSilentScreenshotsBinding binding;
    private ScreenshotsViewModel viewModel;
    ScreenshotsAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext().getApplicationContext()).get(ScreenshotsViewModel.class);

        binding = FragmentSilentScreenshotsBinding.inflate(inflater, container, false);

        viewModel.getScreenshotsInApp(getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rec.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL));
        binding.rec.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        binding.rec.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new ScreenshotsAdapter(requireContext(), SILENT_SCREENSHOT, null);
        adapter.setActivity(requireActivity());
        binding.rec.setAdapter(adapter);

        binding.rec.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.GONE);

        viewModel.getSilentItemMutableLiveData().observe(getViewLifecycleOwner(), ItemList -> {
            binding.rec.setVisibility(View.VISIBLE);
            binding.progHolder.setVisibility(View.GONE);
            adapter.setData(ItemList);
            adapter.notifyItemChanged(0);
        });
        viewModel.getItemNativeAdMutableLiveData().observe(getViewLifecycleOwner(), Adlist -> {
            if (adapter.getData() != null && !adapter.getData().isEmpty()) {
                Random random = new Random();
                List<Screenshots> data = adapter.getData();
                for (int i = 0; i < Adlist.size(); i++) {
                    int randomPosition = random.nextInt(data.size() - 1);
                    data.add(randomPosition, new Screenshots(ScreenshotsAdapter.ITEM_TYPE_AD, Adlist.get(i)));
                }
                adapter.notifyDataSetChanged();
            }
        });
        viewModel.getErrorSilentMutableLiveData().observe(getViewLifecycleOwner(), error -> {
            binding.textView9.setText(error);
            binding.rec.setVisibility(View.GONE);
            binding.progHolder.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.getScreenshotsInApp(getContext());
    }
}