package com.tiptop.dotsandboxes.utils;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.tiptop.dotsandboxes.R;
//import com.facebook.ads.AdSettings;
import java.util.concurrent.TimeUnit;

public class AppLovinAds {
    private Context context;
    private Activity activity;
    private MaxAdView adView;

    public AppLovinAds(Context context, Activity activity){
//        AdSettings.setDataProcessingOptions( new String[] {} );
        this.context = context;
        this.activity = activity;
        // Make sure to set the mediation provider value to "max" to ensure proper functionality
        AppLovinSdk.getInstance( this.context ).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk( this.context, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration)
            {
                // AppLovin SDK is initialized, start loading ads
            }
        } );
    }

    public void createBannerAd(LinearLayout linearLayout)
    {
        adView = new MaxAdView( this.context.getString(R.string.applovin_banner_id), this.context );
        adView.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(MaxAd maxAd) {

            }

            @Override
            public void onAdCollapsed(MaxAd maxAd) {

            }

            @Override
            public void onAdLoaded(MaxAd maxAd) {

            }

            @Override
            public void onAdDisplayed(MaxAd maxAd) {

            }

            @Override
            public void onAdHidden(MaxAd maxAd) {

            }

            @Override
            public void onAdClicked(MaxAd maxAd) {

            }

            @Override
            public void onAdLoadFailed(String s, MaxError maxError) {

            }

            @Override
            public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {

            }
        });

        // Stretch to the width of the screen for banners to be fully functional
        int width = ViewGroup.LayoutParams.MATCH_PARENT;

        // Banner height on phones and tablets is 50 and 90, respectively
        int heightPx = this.context.getResources().getDimensionPixelSize( R.dimen.banner_height );

        adView.setLayoutParams( new LinearLayout.LayoutParams( width, heightPx ) );

        // Set background or background color for banners to be fully functional
        adView.setBackgroundColor( this.context.getResources().getColor(R.color.trans) );

        linearLayout.addView(adView);
        // Load the ad
        adView.loadAd();
    }


}
