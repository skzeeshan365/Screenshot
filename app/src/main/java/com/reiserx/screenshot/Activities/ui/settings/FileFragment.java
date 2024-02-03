package com.reiserx.screenshot.Activities.ui.settings;

import static com.reiserx.screenshot.Adapters.ScreenshotsAdapter.DEFAULT_SCREENSHOT;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reiserx.screenshot.Adapters.FilesAdapter;
import com.reiserx.screenshot.Adapters.ScreenshotsAdapter;
import com.reiserx.screenshot.Interfaces.FolderCreation;
import com.reiserx.screenshot.Interfaces.OnConsentDismissListener;
import com.reiserx.screenshot.Interfaces.OnFileDismissListener;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Services.accessibilityService;
import com.reiserx.screenshot.Utils.BuildConfig;
import com.reiserx.screenshot.Utils.ButtonDesign;
import com.reiserx.screenshot.Utils.CreateFolder;
import com.reiserx.screenshot.Utils.isAccessibilityEnabled;
import com.reiserx.screenshot.ViewModels.ScreenshotsViewModel;
import com.reiserx.screenshot.databinding.FragmentAboutBinding;
import com.reiserx.screenshot.databinding.FragmentFileBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileFragment extends DialogFragment implements FolderCreation {

    public static final String TAG = "about_dialog";
    public static String DEFAULT_STORAGE_KEY = "DEFAULT_STORAGE_KEY";
    public static String PRIMARY_DEFAULT_STORAGE = "DCIM";
    FragmentFileBinding binding;

    FilesAdapter adapter;
    List<String> data;
    ScreenshotsViewModel viewModel;

    public OnFileDismissListener onFileDismissListener;

    public static FileFragment display(FragmentManager fragmentManager) {
        FileFragment exampleDialog = new FileFragment();
        exampleDialog.show(fragmentManager, TAG);
        return exampleDialog;
    }

    public static void setOnFileDismissListener(FileFragment fragment, OnFileDismissListener listener) {
        if (fragment != null) {
            fragment.setFileDismissListener(listener);
        }
    }

    public void setFileDismissListener(OnFileDismissListener listener) {
        this.onFileDismissListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentFileBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ScreenshotsViewModel.class);

        data = new ArrayList<>();
        binding.rec.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FilesAdapter(requireContext(), this);
        binding.rec.setAdapter(adapter);

        viewModel.getDCIMDirectory();
        viewModel.getFilesMutableLiveData().observe(getViewLifecycleOwner(), FileList -> {
            adapter.setData(FileList);
            adapter.notifyItemChanged(0);
        });

        binding.btnDismiss.setOnClickListener(view1 -> {
            onFileDismissListener.onDismiss(null);
            dismiss();
        });

        binding.floatingActionButton.setOnClickListener(view12 -> {
            CreateFolder createFolder = new CreateFolder(getContext(), null);
            createFolder.create(this);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.Slide);
        }
    }

    @Override
    public void onDismiss(boolean created) {
        viewModel.getDCIMDirectory();
    }
}