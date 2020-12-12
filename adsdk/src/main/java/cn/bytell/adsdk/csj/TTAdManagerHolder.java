package cn.bytell.adsdk.csj;

import android.content.Context;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTCustomController;

/**
 * 可以用一个单例来保存TTAdManager实例，在需要初始化sdk的时候调用
 */
public class TTAdManagerHolder {

    private static boolean sInit;
    private static String mAppId;
    private static String mAppName;

    public static TTAdManager get() {
        if (!sInit) {
            throw new RuntimeException("TTAdSdk is not init, please check.");
        }
        return TTAdSdk.getAdManager();
    }

    public static void init(Context context, String appId, String appName) {
        mAppId = appId;
        mAppName = appName;
        doInit(context);
    }

    private static void doInit(Context context) {
        if (!sInit) {
            TTAdSdk.init(context, buildConfig());
            sInit = true;
        }
    }

    private static TTAdConfig buildConfig() {
        return new TTAdConfig.Builder()
                .appId(mAppId)
                .appName(mAppName)
                .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_NO_TITLE_BAR)
                .allowShowNotify(true) //是否允许sdk展示通知栏提示
                .allowShowPageWhenScreenLock(true) //是否在锁屏场景支持展示广告落地页
                .debug(false) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI) //允许直接下载的网络状态集合
                .supportMultiProcess(false) //是否支持多进程，true支持
                .asyncInit(true) //异步初始化sdk，开启可减少初始化耗时
                .customController(new TTCustomController() {
                    @Override
                    public boolean isCanUseLocation() {
                        return false;
                    }
                })
                .build();
    }


}
