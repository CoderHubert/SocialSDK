package cn.bytell.paysdk.wechat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.utils.Log;

public class WXPayCallbackActivity extends Activity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(WXPay.getInstance() != null) {
            WXPay.getInstance().getWXApi().handleIntent(getIntent(), this);
        } else {
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if(WXPay.getInstance() != null) {
            WXPay.getInstance().getWXApi().handleIntent(intent, this);
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        if(baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if(WXPay.getInstance() != null) {
                if(baseResp.errStr != null) {
                    Log.e("wxpay", "errstr=" + baseResp.errStr);
                }
                WXPay.getInstance().onResp(baseResp.errCode);
                finish();
            }
        }
    }
}
