package com.reiserx.screenshot.Activities;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.reiserx.screenshot.Activities.ui.settings.FileFragment;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Utils.DataStoreHelper;
import com.reiserx.screenshot.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DataStoreHelper dataStoreHelper = new DataStoreHelper();
        if (dataStoreHelper.getStringValue(FileFragment.DEFAULT_STORAGE_KEY, null).equals("null"))
            dataStoreHelper.putStringValue(FileFragment.DEFAULT_STORAGE_KEY, "Screenshots");

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration
                .Builder(R.id.navigation_home,
                R.id.navigation_silent_screenshots,
                R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        if (!dataStoreHelper.getBooleanValue("FCM_SUB", false)) {
            FirebaseMessaging.getInstance().subscribeToTopic("updates")
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful())
                            dataStoreHelper.putBooleanValue("FCM_SUB", true);

                    });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    public void setActionBarTitle(String title) {
        setTitle(title);
    }
}