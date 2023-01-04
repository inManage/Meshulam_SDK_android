package il.co.inmanage.meshulam_sdk.managers;

import android.app.Activity;
import android.os.Bundle;

import il.co.inmanage.meshulam_sdk.activities.MeshulamActivitySdk;
import il.co.inmanage.meshulam_sdk.data.DoPaymentData;
import il.co.inmanage.meshulam_sdk.data.GetPaymentData;
import il.co.inmanage.meshulam_sdk.interfaces.OnServerRequestDoneListener;
import il.co.inmanage.meshulam_sdk.parser.Parser;
import il.co.inmanage.meshulam_sdk.sdk.SdkManager;
import il.co.inmanage.meshulam_sdk.server_requests.SdkServerRequest;
import il.co.inmanage.meshulam_sdk.server_requests.CancelBitPaymentServerRequest;
import il.co.inmanage.meshulam_sdk.server_requests.CancelBitTransactionServerRequest;
import il.co.inmanage.meshulam_sdk.server_requests.DoPaymentServerRequest;
import il.co.inmanage.meshulam_sdk.server_requests.GetPaymentProcessInfoServerRequest;
import il.co.inmanage.meshulam_sdk.server_requests.SetBitPaymentServerRequest;
import il.co.inmanage.meshulam_sdk.server_requests.SettleSuspendedTransactionServerRequest;
import il.co.inmanage.meshulam_sdk.server_responses.BaseServerRequestResponse;
import il.co.inmanage.meshulam_sdk.server_responses.SetBitPaymentResponse;
import il.co.inmanage.meshulam_sdk.server_responses.SettleSuspendedTransactionResponse;
import il.co.inmanage.meshulam_sdk.utils.BaseEncryption;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static il.co.inmanage.meshulam_sdk.sdk.MeshulamSdk.STATUS_PAYMENT_SUCCESS;

public class PaymentManager extends BaseManager {

    private static PaymentManager paymentManager;
    private OnPaymentActivityResultListener onPaymentActivityResultListener;

    protected PaymentManager(Activity context) {
        super(context);
    }

    public static PaymentManager getInstance(Activity context) {
        if (paymentManager == null) {
            paymentManager = new PaymentManager(context);
        }
        return paymentManager;
    }

    public void sendSetBitPaymentServerRequest(String applicationToken, String bitPaymentId, OnServerRequestDoneListener onServerRequestDoneListener) {
        SetBitPaymentServerRequest setBitPaymentServerRequest = new SetBitPaymentServerRequest(applicationToken, bitPaymentId, baseApplication, onServerRequestDoneListener);
        SdkRequestManager.getInstance(baseApplication).addToRequestQueue(setBitPaymentServerRequest);
    }

    public void sendSettleSuspendedTransactionServerRequest(GetPaymentData getPaymentData, SdkManager.OnSettlePaymentListener onSettlePaymentListener) {
        try {
            getPaymentData.setApiKey(BaseEncryption.getInstance().encryptAesString(getPaymentData.getApiKey()));
            getPaymentData.setUserId(BaseEncryption.getInstance().encryptAesString(getPaymentData.getUserId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        SettleSuspendedTransactionServerRequest settleSuspendedTransactionServerRequest = new SettleSuspendedTransactionServerRequest(getPaymentData,
                baseApplication, new OnServerRequestDoneListener() {
            @Override
            public void onSuccess(String requestName, SdkServerRequest baseServerRequest, BaseServerRequestResponse baseResponse) {
                SettleSuspendedTransactionResponse settleSuspendedTransactionResponse = (SettleSuspendedTransactionResponse) baseResponse;
                if(onSettlePaymentListener!=null){
                    onSettlePaymentListener.onSettlePaymentSuccess(settleSuspendedTransactionResponse.getResponse());
                }
            }

            @Override
            public void onFailure(String requestName, SdkServerRequest baseServerRequest, BaseServerRequestResponse.ServerRequestFailureResponse serverRequestFailure) {

            }
        });
        SdkRequestManager.getInstance(baseApplication).addToRequestQueue(settleSuspendedTransactionServerRequest);
    }

    public void sendDoPaymentServerRequest(DoPaymentData doPaymentData, OnServerRequestDoneListener onServerRequestDoneListener) {
        DoPaymentServerRequest doPaymentServerRequest = new DoPaymentServerRequest(doPaymentData, baseApplication, onServerRequestDoneListener);
        SdkRequestManager.getInstance(baseApplication).addToRequestQueue(doPaymentServerRequest);
    }

    public void sendGetPaymentProcessInfoServerRequest(GetPaymentData getPaymentData, OnServerRequestDoneListener onServerRequestDone) {
        try {
            getPaymentData.setPageCode(BaseEncryption.getInstance().encryptAesString(getPaymentData.getPageCode()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        GetPaymentProcessInfoServerRequest getPaymentProcessInfoServerRequest = new GetPaymentProcessInfoServerRequest(getPaymentData, baseApplication, onServerRequestDone);
        SdkRequestManager.getInstance(baseApplication).addToRequestQueue(getPaymentProcessInfoServerRequest);
    }

    public void sendCancelBitTransactionServerRequest(String applicationToken, GetPaymentData getPaymentData, OnServerRequestDoneListener onServerRequestDone) {
        CancelBitTransactionServerRequest cancelBitTransactionServerRequest = new CancelBitTransactionServerRequest(applicationToken, getPaymentData, baseApplication, onServerRequestDone);
        SdkRequestManager.getInstance(baseApplication).addToRequestQueue(cancelBitTransactionServerRequest);
    }

    public void sendCancelBitPaymentServerRequest(String applicationToken, String bitPaymentId, OnServerRequestDoneListener onServerRequestDone) {
        CancelBitPaymentServerRequest cancelBitTransactionServerRequest = new CancelBitPaymentServerRequest(applicationToken, bitPaymentId, baseApplication, onServerRequestDone);
        SdkRequestManager.getInstance(baseApplication).addToRequestQueue(cancelBitTransactionServerRequest);
    }

    public void notifyOnPaymentActivityResultListener(int resultCode, Bundle data) {
        switch (resultCode) {
            case MeshulamActivitySdk.RESULT_SUCCESS:
                if (onPaymentActivityResultListener != null)
                    onPaymentActivityResultListener.onPaymentSuccess(data);
                break;
            case MeshulamActivitySdk.RESULT_CANCELED:
                break;
            case MeshulamActivitySdk.RESULT_FAILED:
                if (onPaymentActivityResultListener != null)
                    onPaymentActivityResultListener.onPaymentFailure(data);
                break;
        }
    }

    public void setOnPaymentActivityResultListener(OnPaymentActivityResultListener listener) {
        this.onPaymentActivityResultListener = listener;
    }

    @Override
    public void reset() {
        paymentManager = null;
    }

    public interface OnPaymentActivityResultListener{
        void onPaymentSuccess(Bundle data);
        void onPaymentFailure(Bundle data);
    }

    public String getTransactionId(BaseServerRequestResponse baseResponse){
        SetBitPaymentResponse setBitPaymentResponse = (SetBitPaymentResponse) baseResponse;
        if(setBitPaymentResponse!=null && setBitPaymentResponse.getGetPaymentProcessResponse()!=null){
            JSONObject getPaymentProcessInfoResponseObj = Parser.createJsonObject(setBitPaymentResponse.getGetPaymentProcessResponse()) ;
            JSONArray transactionsArr = Parser.jsonParse(getPaymentProcessInfoResponseObj,"transactions", new JSONArray());
            for (int i = 0; i < transactionsArr.length(); i++) {
                String transactionId = null;
                int statusCode = -1;
                try {
                    statusCode = Parser.jsonParse((JSONObject) transactionsArr.get(i), "statusCode", -1);
                    if(statusCode == STATUS_PAYMENT_SUCCESS){
                        transactionId = Parser.jsonParse((JSONObject) transactionsArr.get(i), "transactionId", Parser.createTempString());
                        return transactionId;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "";
                }
            }
        }
        return "";

    }
}
