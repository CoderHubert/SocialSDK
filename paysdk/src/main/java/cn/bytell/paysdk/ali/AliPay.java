package cn.bytell.paysdk.ali;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;

import java.util.Map;

import cn.bytell.paysdk.Pay;

public class AliPay implements Pay {

    public static final int SDK_ALI_PAY_FLAG = 0;
    public static final int ERROR_RESULT = 1;   //支付结果解析错误
    public static final int ERROR_PAY = 2;  //支付失败
    public static final int ERROR_NETWORK = 3;  //网络连接错误

    private String mParams;
    private PayTask mPayTask;
    private AliPayResultCallBack mCallback;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_ALI_PAY_FLAG: {
                    if(mCallback == null) {
                        return;
                    }
                    if(msg.obj == null){
                        mCallback.onError(ERROR_RESULT);
                        return;
                    }
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    if(TextUtils.equals(resultStatus, "9000")) {    //支付成功
                        mCallback.onSuccess();
                    } else if(TextUtils.equals(resultStatus, "8000")) { //支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        mCallback.onDealing();
                    } else if(TextUtils.equals(resultStatus, "6001")) {		//支付取消
                        mCallback.onCancel();
                    } else if(TextUtils.equals(resultStatus, "6002")) {     //网络连接出错
                        mCallback.onError(ERROR_NETWORK);
                    } else if(TextUtils.equals(resultStatus, "4000")) {        //支付错误
                        mCallback.onError(ERROR_PAY);
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };


    public AliPay(Context context, String params, AliPayResultCallBack callback) {
        mParams = params;
        mCallback = callback;
        mPayTask = new PayTask((Activity) context);
    }

    @Override
    public void doPay() {
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                Map<String, String> result = mPayTask.payV2(mParams, true);

                Message msg = new Message();
                msg.what = SDK_ALI_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    public interface AliPayResultCallBack {
        void onSuccess(); //支付成功
        void onDealing();    //正在处理中 小概率事件 此时以验证服务端异步通知结果为准
        void onError(int error_code);   //支付失败
        void onCancel();    //支付取消
    }
}
