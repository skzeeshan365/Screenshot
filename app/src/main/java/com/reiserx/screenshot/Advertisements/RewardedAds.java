package com.reiserx.screenshot.Advertisements;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.reiserx.screenshot.BuildConfig;
import com.reiserx.screenshot.Interfaces.RewardedAdCallback;

public class RewardedAds {
    Context context;
    String TAG = "RewardedAds";
    RewardedAd rewardedAd;
    Activity activity;
    String AD_ID;

    public RewardedAds(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        if (BuildConfig.DEBUG)
            AD_ID = AdBase.REWARDED_AD_DEBUG;
        else
            AD_ID = AdBase.REWARDED_AD_RELEASE;
    }

    public void load() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(context, AD_ID,
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.toString());
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        Log.d(TAG, "Ad was loaded.");
                    }
                });

    }

    public void load(Runnable runnable) {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(context, AD_ID,
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.toString());
                        rewardedAd = null;
                        runnable.run();
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        Log.d(TAG, "Ad was loaded.");
                        runnable.run();
                    }
                });

    }

    public void display(RewardedAdCallback rewardedAdCallback) {
        if (rewardedAd != null) {
            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "Ad was clicked.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Log.d(TAG, "Ad dismissed fullscreen content.");
                    rewardedAd = null;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when ad fails to show.
                    Log.e(TAG, "Ad failed to show fullscreen content.");
                    rewardedAd = null;
                }

                @Override
                public void onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d(TAG, "Ad recorded an impression.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad showed fullscreen content.");
                }
            });

            rewardedAd.show(activity, rewardItem -> {
                Log.d(TAG, rewardItem.getType());
                rewardedAdCallback.onAdDisplaySuccessful();
            });
        } else {
            rewardedAdCallback.onAdDisplayFailed("Ad not available");
        }
    }
}
