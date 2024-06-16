package com.reiserx.screenshot.Activities.ui.home;

import static android.app.Activity.RESULT_OK;
import static com.reiserx.screenshot.Adapters.ScreenshotsAdapter.DEFAULT_SCREENSHOT;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;

import com.reiserx.screenshot.Adapters.ScreenshotsAdapter;
import com.reiserx.screenshot.Models.Screenshots;
import com.reiserx.screenshot.Utils.DataStoreHelper;
import com.reiserx.screenshot.ViewModels.ScreenshotsViewModel;
import com.reiserx.screenshot.databinding.FragmentGalleryBinding;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private ScreenshotsViewModel viewModel;

    ArrayList<Screenshots> data;
    ScreenshotsAdapter adapter;

    public static String[] storage_permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storage_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
    };

    DataStoreHelper dataStoreHelper;

    public static String SCREENSHOT_LABEL = "SCREENSHOT_LABEL";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(this).get(ScreenshotsViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        dataStoreHelper = new DataStoreHelper();
        if (((AppCompatActivity)requireActivity()).getSupportActionBar() != null) {
            String title = dataStoreHelper.getStringValue(SCREENSHOT_LABEL, null);
            if (title.equals("null"))
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("All screenshots");
            else
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(title);
        }
        consent_agreed();
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
        binding.rec.setAdapter(adapter);

        binding.rec.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.GONE);

        viewModel.getItemMutableLiveData().observe(getViewLifecycleOwner(), ItemList -> {
            binding.rec.setVisibility(View.VISIBLE);
            binding.progHolder.setVisibility(View.GONE);
            adapter.setData(ItemList);
            adapter.notifyDataSetChanged();
        });

        viewModel.getItemNativeAdMutableLiveData().observe(getViewLifecycleOwner(), Adlist -> {
            if (adapter.getData() != null && !adapter.getData().isEmpty()) {
                for (Screenshots screenshot : adapter.getData()) {
                    if (!Adlist.isEmpty()) {
                        if (screenshot.getType() == ScreenshotsAdapter.ITEM_TYPE_AD) {
                            screenshot.setNativeAd(Adlist.get(0));
                            Adlist.remove(0);
                        }
                        adapter.notifyItemChanged(adapter.getData().indexOf(screenshot), screenshot);
                    }
                }
            }
        });

        viewModel.getErrorMutableLiveData().observe(getViewLifecycleOwner(), error -> {
            binding.textView9.setText(error);
            binding.rec.setVisibility(View.GONE);
            binding.progHolder.setVisibility(View.VISIBLE);
        });
    }

    void consent_agreed() {
        if (isPermissionGranted()) {
            viewModel.getScreenshots(getContext(), dataStoreHelper.getStringValue(SCREENSHOT_LABEL, null));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private boolean isPermissionGranted() {
        for (String permission : permissions()) {
            if (ActivityCompat.checkSelfPermission(requireContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storage_permissions_33;
        } else {
            p = storage_permissions;
        }
        return p;
    }

    ActivityResultLauncher<IntentSenderRequest> deleteResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK){
                        Toast.makeText(getContext(), "Screenshot deleted.", Toast.LENGTH_SHORT).show();
                        adapter.closeImageViewer();
                        viewModel.getScreenshots(getContext(), dataStoreHelper.getStringValue(SCREENSHOT_LABEL, null));
                    }
                }
            }
    );
}