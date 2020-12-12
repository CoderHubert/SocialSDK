package cn.bytell.adsdk.gdt;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;

import cn.bytell.adsdk.SplashAdListener;

public class GDTAd {

    private static final String TAG = GDTAd.class.getSimpleName();
    private boolean isSplashAd = false;

    public void loadSplashAd(Context context, String codeId, ViewGroup mSplashContainer, SplashAdListener splashAdListener) {
        SplashAD splashAD = new SplashAD(context, codeId, new SplashADListener() {
            @Override
            public void onADClicked() {
                Log.d(TAG, "onADClicked");
            }

            @Override
            public void onADDismissed() {
                Log.d(TAG, "onADDismissed");
                if (splashAdListener != null) splashAdListener.onSplashAdFinish();
            }

            @Override
            public void onADExposure() {
                Log.d(TAG, "onADExposure");
            }

            @Override
            public void onADLoaded(long l) {
                Log.d(TAG, "onADLoaded " + l);
            }

            @Override
            public void onADPresent() {
                Log.d(TAG, "onADPresent");
            }

            @Override
            public void onADTick(long l) {
                Log.d(TAG, "onADTick" + l);
            }

            @Override
            public void onNoAD(AdError adError) {
                Log.e(TAG, adError.getErrorCode() + adError.getErrorMsg());
                if(splashAdListener != null) splashAdListener.onSplashAdFinish();
            }
        });
        splashAD.fetchAndShowIn(mSplashContainer);
    }

}
