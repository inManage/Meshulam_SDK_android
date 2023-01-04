package il.co.inmanage.meshulam_sdk.sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import il.co.inmanage.meshulam_sdk.activities.MeshulamActivitySdk;
import il.co.inmanage.meshulam_sdk.application.SdkBaseApplication;
import il.co.inmanage.meshulam_sdk.data.BaseSessionData;
import il.co.inmanage.meshulam_sdk.data.CreatePaymentData;
import il.co.inmanage.meshulam_sdk.data.GetPaymentData;
import il.co.inmanage.meshulam_sdk.interfaces.OnServerRequestDoneListener;
import il.co.inmanage.meshulam_sdk.managers.SdkRequestManager;
import il.co.inmanage.meshulam_sdk.managers.PaymentManager;
import il.co.inmanage.meshulam_sdk.server_requests.SdkServerRequest;
import il.co.inmanage.meshulam_sdk.server_requests.CreatePaymentProcessServerRequest;
import il.co.inmanage.meshulam_sdk.server_requests.InitSdkServerRequest;
import il.co.inmanage.meshulam_sdk.server_responses.BaseServerRequestResponse;
import il.co.inmanage.meshulam_sdk.server_responses.CreatePaymentProcessResponse;
import il.co.inmanage.meshulam_sdk.server_responses.GetPaymentProcessInfoResponse;
import il.co.inmanage.meshulam_sdk.server_responses.InitSdkResponse;
import il.co.inmanage.meshulam_sdk.utils.BaseEncryption;
import il.co.inmanage.meshulam_sdk.utils.DeviceUtils;

import java.util.HashSet;
import java.util.Set;

import static il.co.inmanage.meshulam_sdk.activities.MeshulamActivitySdk.KEY_INTERVAL;
import static il.co.inmanage.meshulam_sdk.activities.MeshulamActivitySdk.KEY_MAX_TIME;
import static il.co.inmanage.meshulam_sdk.activities.MeshulamActivitySdk.KEY_PAGE_CODE;
import static il.co.inmanage.meshulam_sdk.activities.MeshulamActivitySdk.KEY_START_SAMPLE;

public class SdkManager extends MeshulamSdk {

    public static final int MESHULAM_ACTIVITY_REQUEST_CODE = 111;


    private static SdkManager sdkManager;
    private BaseSessionData baseSessionData;
    private OnPaymentResultListener onPaymentResultListener;
    private InitSdkResponse initSdk;
    private Set<OnPaymentDataListener> onPaymentDataListeners = new HashSet<>();


    protected SdkManager(Activity baseApplication) {
        super(baseApplication);
    }

    public static SdkManager getInstance(Activity baseApplication) {
        if (sdkManager == null) {
            sdkManager = new SdkManager(baseApplication);
        }
        return sdkManager;
    }

    @Override
    public void createPaymentProcess(CreatePaymentData createPaymentData, OnPaymentResultListener onPaymentResultListener) {
        this.onPaymentResultListener = onPaymentResultListener;
        sendInitSdkServerRequest(createPaymentData, new OnInitSdkListener() {
            @Override
            public void initSdkSuccess(InitSdkResponse initSdkResponse) {
                initSdk = initSdkResponse;
                Intent intent = new Intent(baseApplication, MeshulamActivitySdk.class);
                addDataToIntent(intent, baseSessionData.getInitSdkResponse());
                baseApplication.startActivityForResult(intent, MESHULAM_ACTIVITY_REQUEST_CODE);
                startCreatePaymentProcessServerRequest(createPaymentData);
            }
        });
    }

    @Override
    public void settleSuspendedTransaction(GetPaymentData getPaymentData, OnSettlePaymentListener onSettlePaymentListener) {
        sendInitSdkServerRequest(null, new OnInitSdkListener() {
            @Override
            public void initSdkSuccess(InitSdkResponse initSdkResponse) {
                sendSettleSuspendedTransactionRequest(getPaymentData, onSettlePaymentListener);
            }
        });
    }

    @Override
    public void getPaymentProcessInfo(GetPaymentData getPaymentData, OnGetInfoListener getInfoListener) {
        sendInitSdkServerRequest(null, new OnInitSdkListener() {
            @Override
            public void initSdkSuccess(InitSdkResponse initSdkResponse) {
                sendGetPaymentProcessInfoRequest(getPaymentData, new OnServerRequestDoneListener() {
                    @Override
                    public void onSuccess(String requestName, SdkServerRequest sdkServerRequest, BaseServerRequestResponse baseResponse) {
                        GetPaymentProcessInfoResponse getPaymentProcessInfoResponse = (GetPaymentProcessInfoResponse) baseResponse;
                        if(getInfoListener!=null){
                            getInfoListener.getPaymentInfoData(getPaymentProcessInfoResponse.getResponse());
                        }
                    }

                    @Override
                    public void onFailure(String requestName, SdkServerRequest sdkServerRequest, BaseServerRequestResponse.ServerRequestFailureResponse serverRequestFailure) {

                    }
                });
            }
        });
    }

    @Override
    public void cancelBitPayment(GetPaymentData getPaymentData, OnResultResultListener onPaymentResultListener) {
        sendInitSdkServerRequest(null, new OnInitSdkListener() {
            @Override
            public void initSdkSuccess(InitSdkResponse initSdkResponse) {
                SdkManager.this.initSdk = initSdkResponse;
                PaymentManager.getInstance(baseApplication).sendCancelBitTransactionServerRequest(baseSessionData.getApplicationToken(),
                        getPaymentData, new OnServerRequestDoneListener() {
                            @Override
                            public void onSuccess(String requestName, SdkServerRequest sdkServerRequest, BaseServerRequestResponse baseResponse) {
                                if(onPaymentResultListener!=null){
                                    onPaymentResultListener.onPaymentCanceled();
                                }
                            }

                            @Override
                            public void onFailure(String requestName, SdkServerRequest sdkServerRequest, BaseServerRequestResponse.ServerRequestFailureResponse serverRequestFailure) {
                                Bundle bundle = new Bundle();
                                if(serverRequestFailure!=null && serverRequestFailure.getContent()!=null){
                                    bundle.putString(ERROR_KEY, serverRequestFailure.getContent());
                                }
                                if(onPaymentResultListener!=null){
                                    onPaymentResultListener.onPaymentFailure(bundle);
                                }
                            }
                        });
            }
        });
    }

    private void sendInitSdkServerRequest(CreatePaymentData createPaymentData, OnInitSdkListener onInitSdkListener){
        if(baseSessionData == null || baseSessionData.getApplicationToken() == null || baseSessionData.getApplicationToken().isEmpty()){
            String udid = DeviceUtils.getUuid(baseApplication);
            SdkRequestManager.getInstance(baseApplication).addToRequestQueue(new InitSdkServerRequest(udid,baseApplication, new OnServerRequestDoneListener() {
                @Override
                public void onSuccess(String requestName, SdkServerRequest sdkServerRequest, BaseServerRequestResponse baseResponse) {
                    InitSdkResponse initSdkResponse = (InitSdkResponse) baseResponse;
                    if(createPaymentData!=null){
                        initSdkResponse.setPageCode(createPaymentData.getPageCode());
                    }
                    SdkRequestManager.getInstance(baseApplication).setGetUrl(initSdkResponse.getHostUrl());
                    SdkRequestManager.getInstance(baseApplication).setPostUrl(initSdkResponse.getHostUrl());
                    baseSessionData = new BaseSessionData();
                    baseSessionData.setApplicationToken(initSdkResponse.getApplicationToken());
                    baseSessionData.setInitSdkResponse(initSdkResponse);
                    SdkRequestManager.getInstance(baseApplication).setSessionData(baseSessionData);
                    if(onInitSdkListener!=null){
                        onInitSdkListener.initSdkSuccess(baseSessionData.getInitSdkResponse());
                    }
                }

                @Override
                public void onFailure(String requestName, SdkServerRequest sdkServerRequest, BaseServerRequestResponse.ServerRequestFailureResponse serverRequestFailure) {

                }
            }));
        }else {
            if(onInitSdkListener!=null){
                onInitSdkListener.initSdkSuccess(baseSessionData.getInitSdkResponse());
            }

        }

    }

    private void sendCreatePaymentProcessServerRequest(CreatePaymentData createPaymentData, InitSdkResponse initSdkResponse){
        SdkRequestManager.getInstance(baseApplication).addToRequestQueue(new CreatePaymentProcessServerRequest(createPaymentData,baseApplication, new OnServerRequestDoneListener() {
            @Override
            public void onSuccess(String requestName, SdkServerRequest sdkServerRequest, BaseServerRequestResponse baseResponse) {
                CreatePaymentProcessResponse createPaymentProcessResponse = (CreatePaymentProcessResponse) baseResponse;
                createPaymentProcessResponse.setApplicationToken(initSdkResponse.getApplicationToken());
                initOnMeshulamActivityResultListener();
                notifyOnPaymentDataListeners(createPaymentProcessResponse);
                if (onPaymentResultListener != null){
                    Bundle data = new Bundle();
                    if (createPaymentProcessResponse != null){
                        if (createPaymentProcessResponse.getProcessId() != null && !createPaymentProcessResponse.getProcessId().isEmpty())
                            data.putString(KEY_PROCESS_ID, createPaymentProcessResponse.getProcessId());
                        if (createPaymentProcessResponse.getProcessToken() != null && !createPaymentProcessResponse.getProcessToken().isEmpty())
                            data.putString(KEY_PROCESS_TOKEN, createPaymentProcessResponse.getProcessToken());
                    }
                    onPaymentResultListener.onGetPaymentData(data);
                }
            }

            @Override
            public void onFailure(String requestName, SdkServerRequest sdkServerRequest, BaseServerRequestResponse.ServerRequestFailureResponse serverRequestFailure) {

            }
        }));
    }

    private void addDataToIntent(Intent intent, InitSdkResponse initSdkResponse){
        intent.putExtra(KEY_START_SAMPLE,initSdkResponse.getStartDelay());
        intent.putExtra(KEY_INTERVAL,initSdkResponse.getIntervalLength());
        intent.putExtra(KEY_MAX_TIME,initSdkResponse.getMaxTime());
        intent.putExtra(KEY_PAGE_CODE,initSdkResponse.getPageCode());
    }

    private void initOnMeshulamActivityResultListener () {
        PaymentManager.getInstance(baseApplication).setOnPaymentActivityResultListener(new PaymentManager.OnPaymentActivityResultListener() {
            @Override
            public void onPaymentSuccess(Bundle data) {
                if (onPaymentResultListener != null){
                    onPaymentResultListener.onPaymentSuccess(data);
                }
            }

            @Override
            public void onPaymentFailure(Bundle data) {
                if (onPaymentResultListener != null){
                    onPaymentResultListener.onPaymentFailure(data);
                }
            }
        });
    }

    private void sendGetPaymentProcessInfoRequest(GetPaymentData getPaymentData, OnServerRequestDoneListener onServerRequestDoneListener) {
        PaymentManager.getInstance(baseApplication).sendGetPaymentProcessInfoServerRequest(getPaymentData, onServerRequestDoneListener);
    }

    private void sendSettleSuspendedTransactionRequest(GetPaymentData getPaymentData, OnSettlePaymentListener onSettlePaymentListener) {
        PaymentManager.getInstance(baseApplication).sendSettleSuspendedTransactionServerRequest(getPaymentData, onSettlePaymentListener);
    }

    private void startCreatePaymentProcessServerRequest(CreatePaymentData createPaymentData){
        createPaymentData.setChargeType("2");
        createPaymentData.setTemplateType("8");
        createPaymentData.setApplicationToken(baseSessionData.getApplicationToken());
        encryptParams(createPaymentData);
        sendCreatePaymentProcessServerRequest(createPaymentData, baseSessionData.getInitSdkResponse());
    }

    private void encryptParams(CreatePaymentData createPaymentData){
        try {
            String encryptPageCode = BaseEncryption.getInstance().encryptAesString(createPaymentData.getPageCode());
            createPaymentData.setPageCode(encryptPageCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String encryptApiKey = BaseEncryption.getInstance().encryptAesString(createPaymentData.getApiKey());

            createPaymentData.setApiKey(encryptApiKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String encryptUserId = BaseEncryption.getInstance().encryptAesString(createPaymentData.getUserId());

            createPaymentData.setUserId(encryptUserId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addOnPaymentDataListener(OnPaymentDataListener onPaymentDataListener) {
        onPaymentDataListeners.add(onPaymentDataListener);
    }

    public void removeOnPaymentDataListener(OnPaymentDataListener onPaymentDataListener) {
        onPaymentDataListeners.remove(onPaymentDataListener);
    }

    private void notifyOnPaymentDataListeners(CreatePaymentProcessResponse createPaymentProcessResponse){
        if(onPaymentDataListeners == null) return;
        for (OnPaymentDataListener onPaymentDataListener: onPaymentDataListeners) {
            onPaymentDataListener.paymentDataResponse(createPaymentProcessResponse);
        }
    }

    public InitSdkResponse getInitSdkResponse() {
        return initSdk;
    }

    @Override
    public void reset() {
        sdkManager = null;
    }

    public interface OnResultResultListener {
        void onPaymentFailure(Bundle data);
        void onPaymentCanceled();
    }

    public interface OnPaymentResultListener extends OnResultResultListener {
        void onGetPaymentData(Bundle getPaymentData);
        void onPaymentSuccess(Bundle data);
    }

    public interface OnGetInfoListener {
        void getPaymentInfoData(String data);
    }

    public interface OnPaymentDataListener{
        void paymentDataResponse(CreatePaymentProcessResponse createPaymentProcessResponse);
    }

    public interface OnSettlePaymentListener{
        void onSettlePaymentSuccess(String data);
    }

    private interface OnInitSdkListener{
        void initSdkSuccess(InitSdkResponse initSdkResponse);
    }
}
