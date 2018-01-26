package lee.vioson.aliyweichatpay.weichat;

import android.content.Context;
import android.support.annotation.NonNull;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by viosonlee
 * on 2017/9/26.
 * for
 */

public class WeiChatPay {
    private static IWXAPI api = null;

    public static void init(Context context, String appid) {
        api = WXAPIFactory.createWXAPI(context, null);
        api.registerApp(appid);
    }

    @NonNull
    @Deprecated
    public static WeiChatPay.Builder build(Context context) {
        if (api == null)
            throw new RuntimeException("you must init API first");
        return new Builder(api);
    }

    @NonNull
    public static WeiChatPay.Builder build(Context context, String appID) {
        if (api == null)
            init(context, appID);
        return new Builder(api);
    }

    public static class Builder {
        private IWXAPI api;

        public Builder(IWXAPI api) {
            this.api = api;
        }

        public IWXAPI getApi() {
            return api;
        }

        @Deprecated
        public Builder register(String appID) {
            api.registerApp(appID);
            return this;
        }

        public void pay(RequestParams params) {
            if (api.isWXAppInstalled() && api.isWXAppSupportAPI()) {
                PayReq request = new PayReq();
                request.appId = params.appId;
                request.partnerId = params.partnerId;
                request.prepayId = params.prepayId;
                request.packageValue = params.packageValue;
                request.nonceStr = params.nonceStr;
                request.timeStamp = params.timeStamp;
                request.sign = params.sign;
                api.sendReq(request);
            } else {
//            ToastUtil.showToast("你没有安装微信");
            }
        }
    }


    public static class RequestParams {
        String appId;
        String partnerId;
        String prepayId;
        String packageValue;
        String nonceStr;
        String timeStamp;
        String sign;

        public RequestParams(String appId, String partnerId, String prepayId, String packageValue, String nonceStr, String timeStamp, String sign) {
            this.appId = appId;
            this.partnerId = partnerId;
            this.prepayId = prepayId;
            this.packageValue = packageValue;
            this.nonceStr = nonceStr;
            this.timeStamp = timeStamp;
            this.sign = sign;
        }
    }

}
