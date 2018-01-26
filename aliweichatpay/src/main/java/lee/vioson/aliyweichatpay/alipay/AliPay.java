package lee.vioson.aliyweichatpay.alipay;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.alipay.sdk.app.PayTask;

import java.util.Map;

/**
 * Created by viosonlee
 * on 2017/9/25.
 * for 支付宝支付的封装
 */

public class AliPay {
    private static PayHandler mHandler = new PayHandler();
    private static final String MEMO = "memo";
    private static final String RESULT = "result";
    private static final String RESULT_STATUS = "resultStatus";
    private static final int PAY_RESULT = 0x0011;
    private static ResultHandler resultHandler;

    public static AlipayBuilder build(ResultHandler resultHandler) {
        AlipayBuilder alipayBuilder = new AlipayBuilder();
        AliPay.resultHandler = resultHandler;
        return alipayBuilder;
    }

    private static class PayHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == PAY_RESULT) {
                Bundle bundle = msg.getData();
                Result result = new Result(bundle.getString(MEMO),
                        bundle.getString(RESULT),
                        bundle.getString(RESULT_STATUS));
                resultHandler.onResultBack(result);
            }
        }
    }

    public static class AlipayBuilder {
        private Activity activity;
        private String orderInfo;
//        private ResultHandler resultHandler;

//        ResultHandler getResultHandler() {
//            return resultHandler;
//        }

        public AlipayBuilder with(Activity activity) {
            this.activity = activity;
            return this;
        }

        public AlipayBuilder setOrderInfo(String orderInfo) {
            this.orderInfo = orderInfo;
            return this;
        }

//        @Deprecated
//        public AlipayBuilder setResultHandler(ResultHandler resultHandler) {
//            this.resultHandler = resultHandler;
//            return this;
//        }

        private void pay() {
            PayTask alipay = new PayTask(activity);
            Map<String, String> resultMap = alipay.payV2(orderInfo, true);//Android平台而言是一个map结构体。里面有三个key，其中memo是描述信息(类型为字符串)；result是处理结果(类型为json结构字符串)；resultStatus是结果码(类型为字符串)。
            Bundle bundle = new Bundle();
            bundle.putString(MEMO, resultMap.get(MEMO));
            bundle.putString(RESULT, resultMap.get(RESULT));
            bundle.putString(RESULT_STATUS, resultMap.get(RESULT_STATUS));
            Message message = new Message();
            message.setData(bundle);
            message.what = PAY_RESULT;
            mHandler.sendMessage(message);
        }

        public void payAsync() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    pay();
                }
            }).start();
        }

    }

    public interface ResultHandler {
        void onResultBack(Result result);
    }

    public static class Result {
        String memo;
        String result;
        String resultStatus;

        //resultStatus结果码含义
        /*9000	订单支付成功
        8000	正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
        4000	订单支付失败
        5000	重复请求
        6001	用户中途取消
        6002	网络连接出错
        6004	支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
        其它	    其它支付错误*/
        Result(String memo, String result, String resultStatus) {
            this.memo = memo;
            this.result = result;
            this.resultStatus = resultStatus;
        }
    }
}

