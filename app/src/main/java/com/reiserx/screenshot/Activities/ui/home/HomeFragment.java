package com.reiserx.screenshot.Activities.ui.home;

import static android.app.Activity.RESULT_OK;
import static android.content.ComponentCallbacks2.TRIM_MEMORY_BACKGROUND;
import static com.reiserx.screenshot.Activities.ui.settings.FragmentConsent.CONSENT_AGREE;
import static com.reiserx.screenshot.Activities.ui.settings.FragmentConsent.CONSENT_DEFAULT;
import static com.reiserx.screenshot.Activities.ui.settings.FragmentConsent.CONSENT_KEY;
import static com.reiserx.screenshot.Activities.ui.settings.FragmentConsent.CONSENT_REJECT;
import static com.reiserx.screenshot.Adapters.ScreenshotsAdapter.DEFAULT_SCREENSHOT;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.reiserx.screenshot.Activities.ui.settings.FragmentAbout;
import com.reiserx.screenshot.Activities.ui.settings.FragmentConsent;
import com.reiserx.screenshot.Adapters.ScreenshotsAdapter;
import com.reiserx.screenshot.Interfaces.OnConsentDismissListener;
import com.reiserx.screenshot.Models.Screenshots;
import com.reiserx.screenshot.Services.accessibilityService;
import com.reiserx.screenshot.Utils.DataStoreHelper;
import com.reiserx.screenshot.Utils.isAccessibilityEnabled;
import com.reiserx.screenshot.ViewModels.ScreenshotsViewModel;
import com.reiserx.screenshot.databinding.FragmentHomeBinding;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ScreenshotsViewModel viewModel;

    ArrayList<Screenshots> data;
    ArrayList<String> images;
    ScreenshotsAdapter adapter;

    public static String[] storage_permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storage_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
    };

    DataStoreHelper dataStoreHelper;

    static boolean CONSENT_GRANTED = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        viewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext().getApplicationContext()).get(ScreenshotsViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        dataStoreHelper = new DataStoreHelper();
        dataStoreHelper.putBooleanValue("isAccessibility", false);

        int consent = dataStoreHelper.getIntValue(CONSENT_KEY, 0);
        if (consent == CONSENT_AGREE) {
            consent_agreed();
            CONSENT_GRANTED = true;
        } else if (consent == CONSENT_REJECT) {
            consent_reject();
            CONSENT_GRANTED = false;
        } else {
            consent();
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        data = new ArrayList<>();
        images = new ArrayList<>();
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
            for (Screenshots screenshot : ItemList) {
                images.add(screenshot.getFilename());
            }
            adapter.setData(ItemList);
            adapter.notifyItemChanged(0);
        });
        viewModel.getErrorMutableLiveData().observe(getViewLifecycleOwner(), error -> {
            binding.textView9.setText(error);
            binding.rec.setVisibility(View.GONE);
            binding.progHolder.setVisibility(View.VISIBLE);
        });
    }

    void consent_agreed() {
        isAccessibilityEnabled isAccessibilityEnabled = new isAccessibilityEnabled(getContext());
        if (isPermissionGranted()) {
            viewModel.getScreenshots(getContext(), 100);
            if (!isAccessibilityEnabled.checkAccessibilityPermission(accessibilityService.class)) {
                dataStoreHelper.putBooleanValue("isAccessibility", true);
                FragmentAbout.display(requireActivity().getSupportFragmentManager());
            }
        }
        else
            requestPermissionLauncher.launch(permissions());
    }

    void consent() {
        FragmentConsent fragmentConsent = FragmentConsent.display(requireActivity().getSupportFragmentManager());
        FragmentConsent.setOnConsentDismissListener(fragmentConsent, consentResult -> {
            // Handle the consent result as needed
            if (consentResult == FragmentConsent.CONSENT_AGREE) {
                consent_agreed();
                CONSENT_GRANTED = true;
            } else if (consentResult == FragmentConsent.CONSENT_REJECT) {
                consent_reject();
                CONSENT_GRANTED = false;
            } else {
                consent_reject();
                CONSENT_GRANTED = false;
            }
        });
    }

    void consent_reject() {
        if (isPermissionGranted())
            viewModel.getScreenshots(getContext(), 100);
        else
            requestPermissionLauncher.launch(permissions());
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

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean allPermissionsGranted = true;

                // Check if all requested permissions are granted
                for (Boolean granted : result.values()) {
                    if (!granted) {
                        allPermissionsGranted = false;
                        break;
                    }
                }

                if (allPermissionsGranted) {
                    // All permissions are granted, you can now execute your code
                    // Call the method or perform actions that need permission here
                    performActionsOnPermissionsGranted();
                } else {
                    // Permissions were not granted, handle accordingly
                    // You may display a message or take appropriate action
                    // based on your application's requirements
                }
            });


    // Add this method to perform actions when permissions are granted
    private void performActionsOnPermissionsGranted() {
        if (CONSENT_GRANTED) {
            isAccessibilityEnabled isAccessibilityEnabled = new isAccessibilityEnabled(getContext());
            if (!isAccessibilityEnabled.checkAccessibilityPermission(accessibilityService.class)) {
                dataStoreHelper.putBooleanValue("isAccessibility", true);
                FragmentAbout.display(requireActivity().getSupportFragmentManager());
            }
        }
        viewModel.getScreenshots(getContext(), 100);
    }

    ActivityResultLauncher<IntentSenderRequest> deleteResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK){
                        Toast.makeText(getContext(), "Screenshot deleted.", Toast.LENGTH_SHORT).show();
                        adapter.closeImageViewer();
                        viewModel.getScreenshots(getContext(), 100);
                    }
                }
            }
    );
}