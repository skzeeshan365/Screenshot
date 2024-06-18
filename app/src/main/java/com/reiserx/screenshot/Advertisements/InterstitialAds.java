package com.reiserx.screenshot.Advertisements;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.reiserx.screenshot.BuildConfig;
import com.reiserx.screenshot.Utils.BaseApplication;

import java.util.Random;

public class InterstitialAds {
    Context context;
    public InterstitialAd mInterstitialAd;
    String TAG = "InterstitialAds";
    String AD_ID;

    public InterstitialAds(Context context) {
        this.context = context;
        if (BuildConfig.DEBUG)
            AD_ID = AdBase.INTERSTITIAL_AD_ID_DEBUG;
        else
            AD_ID = AdBase.INTERSTITIAL_AD_ID_RELEASE;
    }

    public void loadAd() {
        if (mInterstitialAd == null) {
            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(context, AD_ID, adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            mInterstitialAd = interstitialAd;
                            Log.d(TAG, "Ad was loaded.");
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            mInterstitialAd = null;
                            Log.d(TAG, loadAdError.getMessage());
                        }
                    });
        }
    }

    public void displayAd(Activity activity, Runnable runnable) {
            if (mInterstitialAd != null) {
                Random random = new Random();
                int randomNumber = random.nextInt(10); // Generate a random number between 0 and 99

                // Show ad 30% of the time (adjust probability as needed)
                if (randomNumber < 6) {
                    mInterstitialAd.show(activity);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdClicked() {
                            // Called when a click is recorded for an ad.
                            Log.d(TAG, "Ad was clicked.");
                            mInterstitialAd = null;
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when ad is dismissed.
                            // Set the ad reference to null so you don't show the ad a second time.
                            Log.d(TAG, "Ad dismissed fullscreen content.");
                            mInterstitialAd = null;
                            runnable.run();
                            InterstitialAds interstitialAds = new InterstitialAds(context);
                            interstitialAds.loadAd();
                            BaseApplication.setInterstitialAds(interstitialAds);
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when ad fails to show.
                            Log.e(TAG, "Ad failed to show fullscreen content.");
                            mInterstitialAd = null;
                            runnable.run();
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
                } else
                    runnable.run();
            } else
                runnable.run();
        }
}
