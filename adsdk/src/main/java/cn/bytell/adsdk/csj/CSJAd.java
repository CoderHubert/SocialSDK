package cn.bytell.adsdk.csj;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.MainThread;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTSplashAd;

import cn.bytell.adsdk.SplashAdListener;

public class CSJAd {

    public static final String TAG = CSJAd.class.getSimpleName();

    public void loadSplashAd(Context context, String codeId, ViewGroup mSplashContainer, int expressWidth, int expressHeight, SplashAdListener splashAdListener) {
        TTAdNative mTTAdNative = TTAdManagerHolder.get().createAdNative(context);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setExpressViewAcceptedSize(expressWidth, expressHeight)
                .build();
        mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
            @Override
            @MainThread
            public void onError(int code, String message) {
                Log.e(TAG, message);
                if(splashAdListener != null) splashAdListener.onSplashAdFinish();
            }

            @Override
            @MainThread
            public void onTimeout() {
                Log.d(TAG, "onTimeout");
                if(splashAdListener != null) splashAdListener.onSplashAdFinish();
            }

            @Override
            @MainThread
            public void onSplashAdLoad(TTSplashAd ad) {
                if (ad == null) {
                    return;
                }

                View view = ad.getSplashView();
                if (mSplashContainer != null && !((Activity)context).isFinishing()) {
                    mSplashContainer.removeAllViews();
                    mSplashContainer.addView(view);
                }else {
                    if(splashAdListener != null) splashAdListener.onSplashAdFinish();
                }

                ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        Log.d(TAG, "onAdClicked");
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                        Log.d(TAG, "onAdShow");
                    }

                    @Override
                    public void onAdSkip() {
                        Log.d(TAG, "onAdSkip");
                        if(splashAdListener != null) splashAdListener.onSplashAdFinish();
                    }

                    @Override
                    public void onAdTimeOver() {
                        Log.d(TAG, "onAdTimeOver");
                        if(splashAdListener != null) splashAdListener.onSplashAdFinish();
                    }
                });
            }
        }, 3000);
    }
}
