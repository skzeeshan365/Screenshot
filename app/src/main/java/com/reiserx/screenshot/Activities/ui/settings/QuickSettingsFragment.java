package com.reiserx.screenshot.Activities.ui.settings;

import static com.reiserx.screenshot.Activities.ui.settings.FragmentConsent.TAG;

import android.app.StatusBarManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Services.ScreenshotSelectedTile;
import com.reiserx.screenshot.Services.ScreenshotSilentTile;
import com.reiserx.screenshot.Services.ScreenshotTile;
import com.reiserx.screenshot.Utils.RequestResult;
import com.reiserx.screenshot.databinding.FragmentQuickSettingsBinding;

import java.util.Optional;
import java.util.concurrent.Executor;

public class QuickSettingsFragment extends Fragment {

    private FragmentQuickSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentQuickSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.checkBox.setOnClickListener(view16 -> {
            addTileIfSupported(getContext(), ScreenshotTile.class, getString(R.string.screenshot_label), R.drawable.ic_screenshot_white_24dp, binding.checkBox);
        });
        binding.checkBox2.setOnClickListener(view16 -> {
            addTileIfSupported(getContext(), ScreenshotSilentTile.class, getString(R.string.silent_screenshot_label), R.drawable.ic_screenshot_white_24dp, binding.checkBox2);
        });
        binding.checkBox3.setOnClickListener(view16 -> {
            addTileIfSupported(getContext(), ScreenshotSelectedTile.class, getString(R.string.selected_screenshot_label), R.drawable.baseline_crop_square_24, binding.checkBox3);
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
    }

    public void addTileIfSupported(Context context, Class<?> screenshotTileClass, String label, int drawable, CheckBox checkBox) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            StatusBarManager statusBarManager = context.getSystemService(StatusBarManager.class);
            if (statusBarManager == null) return;
            Executor resultSuccessExecutor = context.getMainExecutor();
            statusBarManager.requestAddTileService(
                    new ComponentName(context, screenshotTileClass),
                    label,
                    Icon.createWithResource(context, drawable),
                    resultSuccessExecutor,
                    (resultCodeFailure) -> {
                        Optional<RequestResult> result = RequestResult.findByCode(resultCodeFailure);
                        if (result.isPresent()) {
                            requireActivity().runOnUiThread(() -> {
                                checkBox.setChecked(result.get().getCode() == RequestResult.TILE_ADD_REQUEST_RESULT_TILE_ADDED.getCode());
                            });
                        } else {
                            requireActivity().runOnUiThread(() -> {
                                Log.d(TAG, "unknown resultCodeFailure: " + resultCodeFailure);
                            });
                        }
                    }
            );
        } else {
            Toast.makeText(context, "requestAddTileService can only be called in Android 13/Tiramisu.", Toast.LENGTH_SHORT).show();
        }
    }
}