package il.co.inmanage.meshulam_sdk.sdk;

import android.app.Activity;
import android.util.Log;

import il.co.inmanage.meshulam_sdk.interfaces.OnServerRequestDoneListener;
import il.co.inmanage.meshulam_sdk.managers.SdkRequestManager;
import il.co.inmanage.meshulam_sdk.server_requests.SdkServerRequest;
import il.co.inmanage.meshulam_sdk.server_requests.GetBitPaymentStatusServerRequest;
import il.co.inmanage.meshulam_sdk.server_responses.BaseServerRequestResponse;
import il.co.inmanage.meshulam_sdk.server_responses.CreatePaymentProcessResponse;
import il.co.inmanage.meshulam_sdk.server_responses.GetBitPaymentStatusResponse;

import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class PullBitPaymentStatus {

    public static final int PAYMENT_STATUS_SUCCESS = 1;
    public static final int PAYMENT_STATUS_FAILED = 2;
    public static final int PAYMENT_STATUS_PENDING = 3;

    private static PullBitPaymentStatus pullBitPaymentStatus;
    private Timer timer;
    private OnBitPaymentStatusListener onBitPaymentStatusListener;

    private PullBitPaymentStatus() {
    }

    public static PullBitPaymentStatus getInstance() {
        if (pullBitPaymentStatus == null)
            pullBitPaymentStatus = new PullBitPaymentStatus();
        return pullBitPaymentStatus;
    }

    public void startPullingPaymentStatus(long delay, long interval, long maxSampleTs, Activity activity, CreatePaymentProcessResponse createPaymentProcessResponse) {
        timer = new Timer();
        Log.e("meshulam", "Scheduled timer");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e("meshulam", "Task executed");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sendGetBitPaymentStatus(activity, createPaymentProcessResponse, interval, maxSampleTs);
                    }
                });
            }
        }, delay);
    }

    private void sendGetBitPaymentStatus(Activity activity, CreatePaymentProcessResponse createPaymentProcessResponse, long interval, long maxSampleTs) {
        GetBitPaymentStatusServerRequest getBitPaymentStatusServerRequest = new GetBitPaymentStatusServerRequest(createPaymentProcessResponse.getApplicationToken(),
                createPaymentProcessResponse.getBitPaymentId(), activity, new OnServerRequestDoneListener() {
            @Override
            public void onSuccess(String requestName, SdkServerRequest baseServerRequest, BaseServerRequestResponse baseResponse) {
                int paymentStatus = ((GetBitPaymentStatusResponse) baseResponse).getPaymentStatus();
                switch (paymentStatus) {
                    case PAYMENT_STATUS_PENDING:
                        long currentTs = Calendar.getInstance(new Locale("iw", "IL")).getTimeInMillis();
                        if (currentTs < maxSampleTs)
                            startPullingPaymentStatus(interval, interval, maxSampleTs, activity, createPaymentProcessResponse);
                        else{
                            stopPullingPaymentStatus();
                            if (onBitPaymentStatusListener != null)
                                onBitPaymentStatusListener.onFailure("Timeout Error");
                        }
                        break;
                    case PAYMENT_STATUS_SUCCESS:
                        stopPullingPaymentStatus();
                        if (onBitPaymentStatusListener != null)
                            onBitPaymentStatusListener.onSuccess();
                        break;
                    case PAYMENT_STATUS_FAILED:
                        stopPullingPaymentStatus();
                        if (onBitPaymentStatusListener != null)
                            onBitPaymentStatusListener.onFailure("Payment Failed");
                        break;
                }

            }

            @Override
            public void onFailure(String requestName, SdkServerRequest baseServerRequest, BaseServerRequestResponse.ServerRequestFailureResponse serverRequestFailure) {

            }
        });
        SdkRequestManager.getInstance(activity).addToRequestQueue(getBitPaymentStatusServerRequest);
    }


    public void stopPullingPaymentStatus() {
        if(timer!=null){
            timer.cancel();
            Log.e("meshulam", "Timer cancelled");
        }
    }

    public void setOnBitPaymentStatusListener(OnBitPaymentStatusListener onBitPaymentStatusListener){
        this.onBitPaymentStatusListener = onBitPaymentStatusListener;
    }

    public interface OnBitPaymentStatusListener {
        void onSuccess();
        void onFailure(String error);
        void onCanceled();
    }
}
