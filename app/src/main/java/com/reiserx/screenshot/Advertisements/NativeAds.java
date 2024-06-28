package com.reiserx.screenshot.Advertisements;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.reiserx.screenshot.BuildConfig;
import com.reiserx.screenshot.Interfaces.OnNativeAdLoaded;
import com.reiserx.screenshot.R;
import com.reiserx.screenshot.Utils.ButtonDesign;

import java.util.ArrayList;
import java.util.List;

public class NativeAds {
    Context context;
    FrameLayout placeholder;
    NativeAd nativeAd;
    NativeAdView adView;
    List<NativeAd> adList = new ArrayList<>();
    String AD_ID;
    AdLoader adLoader;


    public NativeAds(Context context, FrameLayout placeholder) {
        this.context = context;
        this.placeholder = placeholder;
        if (BuildConfig.DEBUG)
            AD_ID = AdBase.NATIVE_AD_ID_DEBUG;
        else
            AD_ID = AdBase.NATIVE_AD_ID_RELEASE;
    }

    public NativeAds(Context context) {
        this.context = context;
        if (BuildConfig.DEBUG)
            AD_ID = AdBase.NATIVE_AD_ID_DEBUG;
        else
            AD_ID = AdBase.NATIVE_AD_ID_RELEASE;
    }

    public List<NativeAd> getAdList() {
        return adList;
    }

    public void loadAdSmall() {
        AdLoader adLoader = new AdLoader.Builder(context, AD_ID)
                .forNativeAd(nativeAd -> {
                    if (nativeAd.getIcon() != null) {
                        LayoutInflater inflater = (LayoutInflater) context
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        adView = (NativeAdView) inflater
                                .inflate(R.layout.native_ad_small, null);
                        ImageView imageView = adView.findViewById(R.id.ad_app_icon);
                        imageView.setImageDrawable(nativeAd.getIcon().getDrawable());
                        adView.setIconView(imageView);
                    } else {
                        LayoutInflater inflater = (LayoutInflater) context
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        adView = (NativeAdView) inflater
                                .inflate(R.layout.native_ad_medium, null);
                        MediaView imageView = adView.findViewById(R.id.ad_app_icon);
                        imageView.setMediaContent(nativeAd.getMediaContent());
                        adView.setMediaView(imageView);
                    }

                    TextView headlineView = adView.findViewById(R.id.ad_headline);
                    headlineView.setText(nativeAd.getHeadline());
                    adView.setHeadlineView(headlineView);

                    TextView body = adView.findViewById(R.id.ad_body);
                    body.setText(nativeAd.getBody());
                    adView.setBodyView(body);

                    ButtonDesign design = new ButtonDesign(context);

                    Button action = adView.findViewById(R.id.ad_action);
                    action.setText(nativeAd.getCallToAction());
                    design.setButtonOutlineDark(action);
                    adView.setCallToActionView(action);

                    adView.setNativeAd(nativeAd);

                    // Ensure that the parent view doesn't already contain an ad view.
                    placeholder.removeAllViews();

                    // Place the AdView into the parent.
                    placeholder.addView(adView);
                    this.nativeAd = nativeAd;
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    public void loadAdSmallRunnable(OnNativeAdLoaded onNativeAdLoaded) {
        AdLoader adLoader = new AdLoader.Builder(context, AD_ID)
                .forNativeAd(onNativeAdLoaded::onAdsLoaded)
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    public void loadAdLarge() {
        AdLoader adLoader = new AdLoader.Builder(context, AD_ID)
                .forNativeAd(nativeAd -> {
                    LayoutInflater inflater = (LayoutInflater) context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    adView = (NativeAdView) inflater
                            .inflate(R.layout.native_ad_medium, null);
                    MediaView imageView = adView.findViewById(R.id.ad_app_icon);
                    imageView.setMediaContent(nativeAd.getMediaContent());
                    imageView.setImageScaleType(ImageView.ScaleType.FIT_CENTER);
                    adView.setMediaView(imageView);

                    TextView headlineView = adView.findViewById(R.id.ad_headline);
                    headlineView.setText(nativeAd.getHeadline());
                    adView.setHeadlineView(headlineView);

                    TextView body = adView.findViewById(R.id.ad_body);
                    body.setText(nativeAd.getBody());
                    adView.setBodyView(body);

                    ButtonDesign design = new ButtonDesign(context);

                    Button action = adView.findViewById(R.id.ad_action);
                    action.setText(nativeAd.getCallToAction());
                    design.setButtonOutlineDark(action);
                    adView.setCallToActionView(action);

                    adView.setNativeAd(nativeAd);

                    // Ensure that the parent view doesn't already contain an ad view.
                    placeholder.removeAllViews();

                    // Place the AdView into the parent.
                    placeholder.addView(adView);
                    this.nativeAd = nativeAd;
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    public void prefetchAds(int NUMBER_OF_ADS, Runnable onAdsLoadedCallback) {
            adList = new ArrayList<>();
            adLoader = new AdLoader.Builder(context, AD_ID)
                    .forNativeAd(nativeAd -> {
                        adList.add(nativeAd);
                        if (adLoader != null && !adLoader.isLoading()) {
                            if (context != null)
                                onAdsLoadedCallback.run();
                        }
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(LoadAdError adError) {
                            if (adLoader != null && !adLoader.isLoading()) {
                                onAdsLoadedCallback.run();
                            }
                        }
                    })
                    .withNativeAdOptions(new NativeAdOptions.Builder().build())
                    .build();

            int adsLoaded = 0;
            while (adsLoaded < NUMBER_OF_ADS) {
                int adsToLoad = Math.min(NUMBER_OF_ADS - adsLoaded, 5);
                adLoader.loadAds(new AdRequest.Builder().build(), adsToLoad);
                adsLoaded += adsToLoad;
            }
    }

    public static void loadPrefetchedAds(Context context, NativeAd nativeAd, FrameLayout placeholder) {
        NativeAdView adView;
        if (nativeAd.getIcon() != null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            adView = (NativeAdView) inflater
                    .inflate(R.layout.native_ad_image_label_list, null);
            ImageView imageView = adView.findViewById(R.id.ad_app_icon);
            imageView.setImageDrawable(nativeAd.getIcon().getDrawable());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            adView.setIconView(imageView);
        } else {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            adView = (NativeAdView) inflater
                    .inflate(R.layout.native_ad_media_label_list, null);
            MediaView mediaView = adView.findViewById(R.id.ad_app_icon);
            mediaView.setMediaContent(nativeAd.getMediaContent());
            mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            adView.setMediaView(mediaView);
        }

        TextView headlineView = adView.findViewById(R.id.ad_headline);
        headlineView.setText(nativeAd.getHeadline());
        adView.setHeadlineView(headlineView);

        adView.setNativeAd(nativeAd);

        // Ensure that the parent view doesn't already contain an ad view.
        placeholder.removeAllViews();

        // Place the AdView into the parent.
        placeholder.addView(adView);
    }

    public static void loadWithIconPrefetchedAds(Context context, NativeAd nativeAd, FrameLayout placeholder) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        NativeAdView adView = (NativeAdView) inflater
                .inflate(R.layout.native_ad_no_image, null);

        TextView headlineView = adView.findViewById(R.id.ad_headline);
        headlineView.setText(nativeAd.getHeadline());
        adView.setHeadlineView(headlineView);

        TextView body = adView.findViewById(R.id.ad_body);
        body.setText(nativeAd.getBody());
        adView.setBodyView(body);

        if (nativeAd.getIcon() != null) {
            ImageView imageView = adView.findViewById(R.id.ad_app_icon);
            imageView.setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.setIconView(imageView);
        }

        adView.setNativeAd(nativeAd);

        // Ensure that the parent view doesn't already contain an ad view.
        placeholder.removeAllViews();

        // Place the AdView into the parent.
        placeholder.addView(adView);
    }

    public static void loadNoIconPrefetchedAds(Context context, NativeAd nativeAd, FrameLayout placeholder) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        NativeAdView adView = (NativeAdView) inflater
                .inflate(R.layout.native_ad_no_image, null);

        TextView headlineView = adView.findViewById(R.id.ad_headline);
        headlineView.setText(nativeAd.getHeadline());
        adView.setHeadlineView(headlineView);

        TextView body = adView.findViewById(R.id.ad_body);
        body.setText(nativeAd.getBody());
        adView.setBodyView(body);

        ImageView imageView = adView.findViewById(R.id.ad_app_icon);
        imageView.setVisibility(View.GONE);

        adView.setNativeAd(nativeAd);

        // Ensure that the parent view doesn't already contain an ad view.
        placeholder.removeAllViews();

        // Place the AdView into the parent.
        placeholder.addView(adView);
    }

    public static void loadPrefetchedSmall(Context context, NativeAd nativeAd, FrameLayout placeholder) {
        NativeAdView adView;
        if (nativeAd.getIcon() != null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            adView = (NativeAdView) inflater
                    .inflate(R.layout.native_ad_small, null);
            ImageView imageView = adView.findViewById(R.id.ad_app_icon);
            imageView.setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.setIconView(imageView);
        } else {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            adView = (NativeAdView) inflater
                    .inflate(R.layout.native_ad_medium, null);
            MediaView imageView = adView.findViewById(R.id.ad_app_icon);
            imageView.setMediaContent(nativeAd.getMediaContent());
            adView.setMediaView(imageView);
        }

        TextView headlineView = adView.findViewById(R.id.ad_headline);
        headlineView.setText(nativeAd.getHeadline());
        adView.setHeadlineView(headlineView);

        TextView body = adView.findViewById(R.id.ad_body);
        body.setText(nativeAd.getBody());
        adView.setBodyView(body);

        ButtonDesign design = new ButtonDesign(context);

        Button action = adView.findViewById(R.id.ad_action);
        action.setText(nativeAd.getCallToAction());
        design.setButtonOutlineDark(action);
        adView.setCallToActionView(action);

        adView.setNativeAd(nativeAd);

        // Ensure that the parent view doesn't already contain an ad view.
        placeholder.removeAllViews();

        // Place the AdView into the parent.
        placeholder.addView(adView);
    }

    public static void loadPrefetchedOverlayHeadline(Context context, NativeAd nativeAd, FrameLayout placeholder) {
        NativeAdView adView;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        adView = (NativeAdView) inflater
                .inflate(R.layout.native_ad_head_overlay, null);
        MediaView imageView = adView.findViewById(R.id.media_view);
        imageView.setMediaContent(nativeAd.getMediaContent());
        imageView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
        adView.setMediaView(imageView);

        TextView headlineView = adView.findViewById(R.id.ad_headline);
        headlineView.setText(nativeAd.getHeadline());
        adView.setHeadlineView(headlineView);

        adView.setNativeAd(nativeAd);
        placeholder.removeAllViews();
        placeholder.addView(adView);
    }

    public void destroyAd() {
        if (nativeAd != null)
            nativeAd.destroy();
    }
}
