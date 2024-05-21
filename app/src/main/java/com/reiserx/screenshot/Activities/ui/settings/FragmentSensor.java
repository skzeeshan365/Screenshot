package com.reiserx.screenshot.Activities.ui.settings;

import static com.reiserx.screenshot.Activities.ui.settings.FragmentConsent.CONSENT_AGREE;
import static com.reiserx.screenshot.Activities.ui.settings.FragmentConsent.CONSENT_KEY;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Toast;

import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Services.accessibilityService;
import com.reiserx.screenshot.Utils.DataStoreHelper;
import com.reiserx.screenshot.Utils.isAccessibilityEnabled;
import com.reiserx.screenshot.databinding.FragmentSensorBinding;
import com.reiserx.screenshot.databinding.FragmentSettingsBinding;

import java.math.BigInteger;

public class FragmentSensor extends Fragment {

    private FragmentSensorBinding binding;

    public static String SENSOR_KEY = "SENSOR_KEY";
    public static String SCREENSHOT_TYPE_KEY = "SCREENSHOT_TYPE_KEY";
    public static int DEFAULT = 0;
    public static int SCREENSHOT = 1;
    public static int SILENT_SCREENSHOT = 2;
    public static int SNAPSHOT = 3;
    public static String SHAKE_COUNT = "SHAKE_COUNT";
    DataStoreHelper dataStoreHelper;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSensorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataStoreHelper = new DataStoreHelper();

        if (accessibilityService.instance != null) {
            binding.enableSwitch.setChecked(accessibilityService.instance.isProximitySensorEnabled());
            binding.enableShakeSwitch.setChecked(accessibilityService.instance.isShakeSensorEnabled());
        } else {
            binding.enableSwitch.setChecked(false);
            binding.enableShakeSwitch.setChecked(false);
        }

        binding.enableShakeSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (accessibilityService.instance != null) {
                if (b) {
                    accessibilityService.instance.enableShakeDetection();
                    binding.captureWithShakeSensorHolder.setEnabled(true);
                } else {
                    accessibilityService.instance.disableShakeDetection();
                    binding.captureWithShakeSensorHolder.setEnabled(false);
                }
            }
        });

        binding.captureWithShakeSensorHolder.setOnClickListener(view12 -> {
            setSensorCount(getContext());
        });

        binding.enableSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (accessibilityService.instance != null) {
                if (b) {
                    accessibilityService.instance.enableProximitySensor();
                } else {
                    accessibilityService.instance.disableProximitySensor();
                }
            }
            dataStoreHelper.putBooleanValue(SENSOR_KEY, b);
        });

        binding.captureWithSensorHolder.setOnClickListener(view1 -> {
            setScreenshotType(getContext());
        });

        binding.captureWithShakeSensorHolder.setEnabled(binding.enableShakeSwitch.isChecked());

        updateValues();
    }

    void updateValues() {
        if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == 0)
            binding.sensorValue.setText("Default");
        else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == 1)
            binding.sensorValue.setText(getString(R.string.screenshot_label));
        else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == 2)
            binding.sensorValue.setText(getString(R.string.silent_screenshot_label));
        else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == 3)
            binding.sensorValue.setText(getString(R.string.selected_screenshot_label));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void setScreenshotType(Context context) {

        AlertDialog alert = new AlertDialog.Builder(context).create();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.screenshot_types, null);

        final RadioButton btn1 = mView.findViewById(R.id.radioButton);
        final RadioButton btn2 = mView.findViewById(R.id.radioButton2);
        final RadioButton btn3 = mView.findViewById(R.id.radioButton3);
        final RadioButton btn4 = mView.findViewById(R.id.radioButton4);

        alert.setTitle("Select default screenshot type");
        alert.setMessage("Select default screenshot type for capturing with sensor");
        alert.setView(mView);

        if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == 0)
            btn1.setChecked(true);
        else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == 1)
            btn2.setChecked(true);
        else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == 2)
            btn3.setChecked(true);
        else if (dataStoreHelper.getIntValue(SCREENSHOT_TYPE_KEY, 0) == 3)
            btn4.setChecked(true);

        btn1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn1.isChecked()) {
                dataStoreHelper.putIntValue(SCREENSHOT_TYPE_KEY, DEFAULT);
            }
            alert.dismiss();
            updateValues();
        });

        btn2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn2.isChecked()) {
                dataStoreHelper.putIntValue(SCREENSHOT_TYPE_KEY, SCREENSHOT);
            }
            alert.dismiss();
            updateValues();
        });

        btn3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn3.isChecked()) {
                dataStoreHelper.putIntValue(SCREENSHOT_TYPE_KEY, SILENT_SCREENSHOT);
            }
            alert.dismiss();
            updateValues();
        });
        btn4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (btn4.isChecked()) {
                dataStoreHelper.putIntValue(SCREENSHOT_TYPE_KEY, SNAPSHOT);
            }
            alert.dismiss();
            updateValues();
        });
        alert.show();
    }

    public void setSensorCount(Context context) {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.number_picker, null);

        final NumberPicker picker = mView.findViewById(R.id.numberpicker_main_picker);

        alert.setTitle("Select default screenshot type");
        alert.setMessage("Select default screenshot type for capturing with sensor");
        alert.setView(mView);

        picker.setMaxValue(5);
        picker.setMinValue(1);

        alert.setPositiveButton("select", (dialogInterface, i) -> {
            int select_value = picker.getValue();
            binding.shakeSensorValue.setText(String.valueOf(select_value));
            dataStoreHelper.putIntValue(SHAKE_COUNT, select_value);
        });
        alert.setNegativeButton("cancel", null);

        AlertDialog builder = alert.create();
        builder.show();
    }
}