package cn.bytell.adsdk.gdt;

import android.content.Context;

import com.qq.e.comm.managers.GDTADManager;

public class GDTAdManagerHolder {
    public static void init(Context context, String appId){
        GDTADManager.getInstance().initWith(context, appId);
    }
}
