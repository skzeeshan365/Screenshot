package com.reiserx.screenshot.Activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.reiserx.screenshot.Activities.ui.settings.FileFragment;
import com.reiserx.screenshot.Advertisements.InterstitialAds;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Utils.BaseApplication;
import com.reiserx.screenshot.Utils.DataStoreHelper;
import com.reiserx.screenshot.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        DataStoreHelper dataStoreHelper = new DataStoreHelper();
        if (dataStoreHelper.getStringValue(FileFragment.DEFAULT_STORAGE_KEY, null).equals("null"))
            dataStoreHelper.putStringValue(FileFragment.DEFAULT_STORAGE_KEY, "Screenshots");

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration
                .Builder(R.id.navigation_home,
                R.id.navigation_silent_screenshots,
                R.id.navigation_search,
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

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            auth.signInAnonymously().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("Alert");
                    alert.setMessage("An error occurred: "+task.getException().getMessage());
                    alert.setPositiveButton("OK", (dialog, which) -> finish());
                    alert.setCancelable(false);
                    alert.show();
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        InterstitialAds interstitialAds = new InterstitialAds(this);
        interstitialAds.loadAd();
        BaseApplication.setInterstitialAds(interstitialAds);
    }
}