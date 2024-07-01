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
import com.reiserx.screenshot.Models.ImageEntity;
import com.reiserx.screenshot.Models.Screenshots;
import com.reiserx.screenshot.ViewModels.LabelsViewModel;
import com.reiserx.screenshot.databinding.FragmentScreenshotsRecyclerBinding;

import java.io.File;
import java.util.ArrayList;

public class LabelScreenshotsFragment extends Fragment {
    private FragmentScreenshotsRecyclerBinding binding;
    ArrayList<Screenshots> data;
    ScreenshotsAdapter adapter;

    LabelsViewModel viewModel;

    public static String LABEL_ID = "LABEL_ID";
    public static String LABEL_NAME = "LABEL_NAME";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentScreenshotsRecyclerBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(LabelsViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        data = new ArrayList<>();
        binding.rec.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL));
        binding.rec.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        binding.rec.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new ScreenshotsAdapter(requireContext(), DEFAULT_SCREENSHOT, deleteResultLauncher);
        adapter.setActivity(requireActivity());
        binding.rec.setAdapter(adapter);

        if (getArguments() != null) {
            int labelId = getArguments().getInt("label_id");
            String label = getArguments().getString("label");

            if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(label);
            }

            viewModel.getImagesByLabel(labelId).observe(getViewLifecycleOwner(), imageEntities -> {
                for (ImageEntity imageEntity : imageEntities) {
                    File file = new File(imageEntity.getFilePath());
                    if (file.exists()) {
                        Screenshots screenshots = new Screenshots(imageEntity.getFilePath());
                        screenshots.setFile(file);
                        data.add(screenshots);
                    }
                }
                adapter.setData(data);
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