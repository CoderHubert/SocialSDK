package cn.bytell.paysdk.wechat;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import cn.bytell.paysdk.Pay;

public class WXPay implements Pay {

    private static WXPay mWXPay;
    public static final int NO_OR_LOW_WX = 1;   //未安装微信或微信版本过低
    public static final int ERROR_PAY_PARAM = 2;  //支付参数错误
    public static final int ERROR_PAY = 3;  //支付失败

    private IWXAPI mWXApi;
    private WeChatPayInfo mPayInfo;
    private WXPayResultCallBack mCallback;

    public WXPay(Context context, String wxAppId) {
        mWXApi = WXAPIFactory.createWXAPI(context, wxAppId,false); // appId, checkSignature
        mWXApi.registerApp(wxAppId);
    }

    /**
     * 调用接口前必须初始化
     * @param context Activity
     * @param wxAppId 微信ID
     */
    public static void init(Context context, String wxAppId) {
        if(mWXPay == null) {
            mWXPay = new WXPay(context, wxAppId);
        }
    }
    public static WXPay getInstance(){
        return mWXPay;
    }

    public IWXAPI getWXApi() {
        return mWXApi;
    }

    public void setPayInfo(WeChatPayInfo payInfo) {
        this.mPayInfo = payInfo;
    }

    public void setCallback(WXPayResultCallBack callback) {
        this.mCallback = callback;
    }

    @Override
    public void doPay() {
        if(!isSupportWXPay()) {
            if(mCallback != null) {
                mCallback.onError(NO_OR_LOW_WX);
            }
            return;
        }
        if(!checkParams()) {
            if(mCallback != null) {
                mCallback.onError(ERROR_PAY_PARAM);
            }
            return;
        }

        PayReq req = new PayReq();
        req.appId = this.mPayInfo.appId;
        req.partnerId = this.mPayInfo.partnerId;
        req.prepayId = this.mPayInfo.prepayId;
        req.packageValue = this.mPayInfo.packageValue;
        req.nonceStr = this.mPayInfo.nonceStr;
        req.timeStamp = this.mPayInfo.timeStamp;
        req.sign = this.mPayInfo.sign;

        mWXApi.sendReq(req);
    }

    /**
     * 检测是否支持微信支付
     * @return
     */
    private boolean isSupportWXPay() {
        return mWXApi.isWXAppInstalled() && mWXApi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
    }

    /**
     * 检测参数是否完整
     * @return
     */
    private boolean checkParams() {
        if(this.mPayInfo == null){
            return false;
        }
        if(TextUtils.isEmpty(this.mPayInfo.nonceStr)
                || TextUtils.isEmpty(this.mPayInfo.appId)
                || TextUtils.isEmpty(this.mPayInfo.packageValue)
                || TextUtils.isEmpty(this.mPayInfo.partnerId)
                || TextUtils.isEmpty(this.mPayInfo.prepayId)
                || TextUtils.isEmpty(this.mPayInfo.sign)
                || TextUtils.isEmpty(this.mPayInfo.timeStamp)){
            return false;
        }
        return true;
    }

    public void onResp(int error_code) {
        if(mCallback == null) {
            return;
        }
        if(error_code == 0) {   //成功
            mCallback.onSuccess();
        } else if(error_code == -1) {   //错误
            mCallback.onError(ERROR_PAY);
        } else if(error_code == -2) {   //取消
            mCallback.onCancel();
        }
        mPayInfo = null;
        mCallback = null;
    }

    public interface WXPayResultCallBack {
        void onSuccess(); //支付成功
        void onError(int error_code);   //支付失败
        void onCancel();    //支付取消
    }

}
