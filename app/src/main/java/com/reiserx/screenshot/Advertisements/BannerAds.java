package com.reiserx.screenshot.Advertisements;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.reiserx.screenshot.BuildConfig;

import org.checkerframework.checker.units.qual.A;

public class BannerAds {
    Context context;
    FrameLayout placeholder;
    String AD_ID;

    public BannerAds(Context context, FrameLayout placeholder) {
        this.context = context;
        this.placeholder = placeholder;
        if (BuildConfig.DEBUG)
            AD_ID = AdBase.BANNER_AD_ID_DEBUG;
        else
            AD_ID = AdBase.BANNER_AD_ID_RELEASE;
    }

    private AdSize getAdSize() {
        // Determine the screen width (less decorations) to use for the ad width.
        DisplayManager displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        Display display = displayManager.getDisplay(Display.DEFAULT_DISPLAY);
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = placeholder.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }

    public void loadBanner() {

        // Create a new ad view.
        AdView adView = new AdView(context);
        adView.setAdSize(getAdSize());
        adView.setAdUnitId(AD_ID);

        // Replace ad container with new ad view.
        placeholder.removeAllViews();
        placeholder.addView(adView);

        // Start loading the ad in the background.
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public void loadBannerLarge() {

        // Create a new ad view.
        AdView adView = new AdView(context);
        adView.setAdSize(AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(context, 320));
        adView.setAdUnitId(AD_ID);

        // Replace ad container with new ad view.
        placeholder.removeAllViews();
        placeholder.addView(adView);

        // Start loading the ad in the background.
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}
