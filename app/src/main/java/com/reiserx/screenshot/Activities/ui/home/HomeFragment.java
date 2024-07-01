package com.reiserx.screenshot.Activities.ui.home;

import static com.reiserx.screenshot.Activities.ui.settings.FragmentConsent.CONSENT_AGREE;
import static com.reiserx.screenshot.Activities.ui.settings.FragmentConsent.CONSENT_KEY;
import static com.reiserx.screenshot.Activities.ui.settings.FragmentConsent.CONSENT_REJECT;
import static com.reiserx.screenshot.Activities.ui.settings.FragmentConsent.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;

import com.reiserx.screenshot.Activities.ui.settings.FragmentAbout;
import com.reiserx.screenshot.Activities.ui.settings.FragmentConsent;
import com.reiserx.screenshot.Adapters.ScreenshotLabelsAdapter;
import com.reiserx.screenshot.Models.ScreenshotLabels;
import com.reiserx.screenshot.Services.accessibilityService;
import com.reiserx.screenshot.Utils.DataStoreHelper;
import com.reiserx.screenshot.Utils.isAccessibilityEnabled;
import com.reiserx.screenshot.ViewModels.ScreenshotsViewModel;
import com.reiserx.screenshot.databinding.FragmentScreenshotsRecyclerBinding;

import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {

    private FragmentScreenshotsRecyclerBinding binding;
    private ScreenshotsViewModel viewModel;

    ScreenshotLabelsAdapter adapter;

    public static String[] storage_permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storage_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    DataStoreHelper dataStoreHelper;

    static boolean CONSENT_GRANTED = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        viewModel = new ViewModelProvider((ViewModelStoreOwner) requireActivity()).get(ScreenshotsViewModel.class);

        binding = FragmentScreenshotsRecyclerBinding.inflate(inflater, container, false);
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

        binding.rec.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL));
        binding.rec.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        binding.rec.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new ScreenshotLabelsAdapter(requireContext(), NavHostFragment.findNavController(HomeFragment.this));
        binding.rec.setAdapter(adapter);

        binding.rec.setVisibility(View.GONE);
        binding.progHolder.setVisibility(View.GONE);

        viewModel.getItemLabelsMutableLiveData().observe(getViewLifecycleOwner(), ItemList -> {
            binding.rec.setVisibility(View.VISIBLE);
            binding.progHolder.setVisibility(View.GONE);
            adapter.setData(ItemList);
            adapter.notifyDataSetChanged();
        });
        viewModel.getItemNativeAdMutableLiveData().observe(getViewLifecycleOwner(), Adlist -> {
            Random random = new Random();
            List<ScreenshotLabels> data = adapter.getData();
            if (data != null && !data.isEmpty()) {
                for (int i = 0; i < Adlist.size(); i++) {
                    int randomPosition = random.nextInt(data.size() - 1) + 1;
                    data.add(randomPosition, new ScreenshotLabels(ScreenshotLabelsAdapter.AD_CONTENT, Adlist.get(i)));
                }
                adapter.notifyDataSetChanged();
            }
        });
        viewModel.getErrorLabelsMutableLiveData().observe(getViewLifecycleOwner(), error -> {
            Log.d(TAG, "fdfsdfdsf");
            binding.textView9.setText(error);
            binding.rec.setVisibility(View.GONE);
            binding.progHolder.setVisibility(View.VISIBLE);
        });
    }

    void consent_agreed() {
        isAccessibilityEnabled isAccessibilityEnabled = new isAccessibilityEnabled(getContext());
        if (isPermissionGranted()) {
            viewModel.getScreenshotLabels(getContext());
            if (!isAccessibilityEnabled.checkAccessibilityPermission(accessibilityService.class)) {
                dataStoreHelper.putBooleanValue("isAccessibility", true);
                FragmentAbout.display(requireActivity().getSupportFragmentManager());
            }
        } else
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
            viewModel.getScreenshotLabels(getContext());
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
            if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                continue;
            }
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

                for (Boolean granted : result.values()) {
                    if (!granted) {
                        allPermissionsGranted = false;
                        break;
                    }
                }

                if (allPermissionsGranted) {
                    performActionsOnPermissionsGranted();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Permission denied");
                    alert.setMessage("Please grant necessary permission to continue");
                    alert.setPositiveButton("ok", (dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    });
                    alert.setCancelable(false);
                    alert.show();
                }
            });


    private void performActionsOnPermissionsGranted() {
        if (CONSENT_GRANTED) {
            isAccessibilityEnabled isAccessibilityEnabled = new isAccessibilityEnabled(getContext());
            if (!isAccessibilityEnabled.checkAccessibilityPermission(accessibilityService.class)) {
                dataStoreHelper.putBooleanValue("isAccessibility", true);
                FragmentAbout.display(requireActivity().getSupportFragmentManager());
            }
        }
        viewModel.getScreenshotLabels(getContext());
    }
}