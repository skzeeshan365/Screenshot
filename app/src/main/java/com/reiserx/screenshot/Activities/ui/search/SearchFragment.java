package com.reiserx.screenshot.Activities.ui.search;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.reiserx.screenshot.Adapters.SearchAdapter;
import com.reiserx.screenshot.Advertisements.NativeAds;
import com.reiserx.screenshot.DAO.LabelDao;
import com.reiserx.screenshot.Models.SearchListModel;
import com.reiserx.screenshot.Utils.ScheduleWork;
import com.reiserx.screenshot.ViewModels.LabelsViewModel;
import com.reiserx.screenshot.databinding.FragmentSearchBinding;

import java.util.ArrayList;
import java.util.Random;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    ArrayList<SearchListModel> data;
    SearchAdapter adapter;

    LabelsViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(LabelsViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ScheduleWork.scheduleLabelProcessing(requireContext().getApplicationContext());

        data = new ArrayList<>();
        binding.rec.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SearchAdapter(getContext(), data, NavHostFragment.findNavController(SearchFragment.this));
        binding.rec.setAdapter(adapter);

        binding.rec.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.VISIBLE);
        binding.textView9.setVisibility(View.GONE);

        data.add(new SearchListModel("SEARCH BY", adapter.HEADER));

        viewModel.getAllLabels().observe(getViewLifecycleOwner(), labelWithImageCountList -> {
            if (labelWithImageCountList.isEmpty()) {
                binding.textView9.setText("Labelling screenshots, this may take a while");
                binding.rec.setVisibility(View.GONE);
                binding.progHolder.setVisibility(View.VISIBLE);
                binding.textView9.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            } else {
                binding.rec.setVisibility(View.VISIBLE);
                binding.progHolder.setVisibility(View.GONE);
                for (LabelDao.LabelWithImageCount labelEntity : labelWithImageCountList) {
                    data.add(new SearchListModel(labelEntity.label.getLabelName(), labelEntity.label.getId(), labelEntity.imageCount, adapter.DATA_CONTENT));
                }

                if (data.isEmpty())
                    adapter.notifyItemChanged(0);
                else {

                    Random random = new Random();
                    int numberOfAds = data.size() / 3; // Number of ad elements based on the list size

                    for (int i = 0; i < numberOfAds; i++) {
                        int randomPosition = random.nextInt(data.size() - 1) + 1; // Ensure not to overwrite the "All screenshots" label at index 0
                        data.add(randomPosition, new SearchListModel(SearchAdapter.AD_CONTENT)); // Add ad element with the appropriate label and ad object
                    }

                    adapter.notifyItemChanged(0);

                    new Thread(() -> {
                        NativeAds nativeAds = new NativeAds(getContext());
                        nativeAds.prefetchAds(numberOfAds, () -> {
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(() -> {
                                if (!data.isEmpty()) {
                                    for (SearchListModel model : data) {
                                        if (!nativeAds.getAdList().isEmpty()) {
                                            if (model.getType() == SearchAdapter.AD_CONTENT) {
                                                model.setNativeAd(nativeAds.getAdList().get(0));
                                                nativeAds.getAdList().remove(0);
                                            }
                                            adapter.notifyItemChanged(data.indexOf(model), model);
                                        }
                                    }
                                }
                            });
                        });
                    }).start();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}