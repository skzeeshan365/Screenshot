package com.reiserx.screenshot.Activities.ui.search;

import static android.app.Activity.RESULT_OK;
import static com.reiserx.screenshot.Adapters.ScreenshotsAdapter.DEFAULT_SCREENSHOT;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;

import com.reiserx.screenshot.Adapters.ScreenshotsAdapter;
import com.reiserx.screenshot.Models.LocationData;
import com.reiserx.screenshot.ViewModels.ScreenshotsViewModel;
import com.reiserx.screenshot.databinding.FragmentScreenshotsRecyclerBinding;

public class LocationScreenshotsFragment extends Fragment {
    private FragmentScreenshotsRecyclerBinding binding;
    ScreenshotsAdapter adapter;

    ScreenshotsViewModel viewModel;

    String TAG = "dfdfsfdsfs";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentScreenshotsRecyclerBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(ScreenshotsViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rec.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL));
        binding.rec.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        binding.rec.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new ScreenshotsAdapter(requireContext(), DEFAULT_SCREENSHOT, deleteResultLauncher);
        adapter.setActivity(requireActivity());
        binding.rec.setAdapter(adapter);

        if (getArguments() != null) {
            LocationData location = getArguments().getParcelable("location");
            String label = getArguments().getString("label");

            if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
                    ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(label);
            }

            viewModel.getLocationScreenshots(getContext(), location);
            viewModel.getItemMutableLiveData().observe(getViewLifecycleOwner(), imageEntities -> {
                adapter.setData(imageEntities);
                adapter.notifyItemChanged(0);
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    ActivityResultLauncher<IntentSenderRequest> deleteResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK){
                        Toast.makeText(getContext(), "Screenshot deleted", Toast.LENGTH_SHORT).show();
                        adapter.closeImageViewer();
                    }
                }
            }
    );
}