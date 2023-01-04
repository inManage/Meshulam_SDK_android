package il.co.inmanage.meshulam_sdk.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Calendar;
import java.util.Locale;

import il.co.inmanage.meshulam_sdk.R;
import il.co.inmanage.meshulam_sdk.dialog.CustomizableDialog;
import il.co.inmanage.meshulam_sdk.interfaces.OnServerRequestDoneListener;
import il.co.inmanage.meshulam_sdk.managers.BaseDialogManager;
import il.co.inmanage.meshulam_sdk.managers.PaymentManager;
import il.co.inmanage.meshulam_sdk.managers.TimeManager;
import il.co.inmanage.meshulam_sdk.sdk.BitManager;
import il.co.inmanage.meshulam_sdk.sdk.PullBitPaymentStatus;
import il.co.inmanage.meshulam_sdk.sdk.SdkManager;
import il.co.inmanage.meshulam_sdk.server_requests.SdkServerRequest;
import il.co.inmanage.meshulam_sdk.server_responses.BaseServerRequestResponse;
import il.co.inmanage.meshulam_sdk.server_responses.CreatePaymentProcessResponse;
import il.co.inmanage.meshulam_sdk.server_responses.DoPaymentResponse;
import il.co.inmanage.meshulam_sdk.server_responses.SetBitPaymentResponse;

import static il.co.inmanage.meshulam_sdk.sdk.MeshulamSdk.ERROR_KEY;
import static il.co.inmanage.meshulam_sdk.sdk.MeshulamSdk.KEY_BIT_PAYMENT_ID;
import static il.co.inmanage.meshulam_sdk.sdk.MeshulamSdk.KEY_CUSTOM_FIELDS;
import static il.co.inmanage.meshulam_sdk.sdk.MeshulamSdk.KEY_PROCESS_ID;
import static il.co.inmanage.meshulam_sdk.sdk.MeshulamSdk.KEY_PROCESS_TOKEN;
import static il.co.inmanage.meshulam_sdk.sdk.MeshulamSdk.KEY_TRANSACTION_ID;


public class MeshulamActivitySdk extends SdkBaseFragmentActivity implements SdkManager.OnPaymentDataListener {

    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_CANCELED = 2;
    public static final int RESULT_FAILED = 3;
    public final static String KEY_START_SAMPLE = "start_sample";
    public final static String KEY_INTERVAL = "interval";
    public final static String KEY_MAX_TIME = "max_time";
    public final static String KEY_PAGE_CODE = "page_code";
    public final static String KEY_BIT_DATA = "bit_data";
    // Returning bundle keys
    private static MeshulamActivitySdk instance;
    private long startSampleTs;
    private long maxSampleTime;
    private long interval;
    private String pageCode = "";
    private CreatePaymentProcessResponse createPaymentProcessResponse;
    private boolean isStartPullOrderStatus = true;
    private LinearLayout rlStartPayment;
    private RelativeLayout rlFinishPayment;
    private LottieAnimationView lottieBitAnimation;

    public static MeshulamActivitySdk getInstance() {
        return instance;
    }

    @Override
    public String getActivitySimpleName() {
        return getClass().getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        if (getIntent() != null) {
            long currentTs = Calendar.getInstance(new Locale("iw", "IL")).getTimeInMillis();
            startSampleTs = getIntent().getLongExtra(KEY_START_SAMPLE, 5 * TimeManager.SECOND);
            maxSampleTime = currentTs + getIntent().getLongExtra(KEY_MAX_TIME, 900 * TimeManager.SECOND);
            interval = getIntent().getLongExtra(KEY_INTERVAL, 5 * TimeManager.SECOND);
            pageCode = getIntent().getStringExtra(KEY_PAGE_CODE);

        }
        SdkManager.getInstance(activity()).addOnPaymentDataListener(this);
        // Test
        //BaseDialogManager.getInstance(this).showTestDialog(this);
    }

    private void setOnBitPaymentStatusListener() {
        PullBitPaymentStatus.getInstance().setOnBitPaymentStatusListener(new PullBitPaymentStatus.OnBitPaymentStatusListener() {
            @Override
            public void onSuccess() {
                sendSetBitPaymentServerRequest(createPaymentProcessResponse.getApplicationToken(), createPaymentProcessResponse.getBitPaymentId());
            }

            @Override
            public void onFailure(String error) {
                Bundle bundle = new Bundle();
                bundle.putString(ERROR_KEY, error);
                finishActivityAndNotifyListener(RESULT_FAILED, bundle);
            }

            @Override
            public void onCanceled() {
                finishActivityAndNotifyListener(RESULT_CANCELED, null);
            }
        });
    }

    @Override
    protected void initViews() {
        super.initViews();
        rlStartPayment = findViewById(R.id.rlStartPayment);
        rlFinishPayment = findViewById(R.id.rlFinishPayment);
        lottieBitAnimation = findViewById(R.id.lottieBitAnimation);
    }

    @Override
    protected void initListeners() {
        super.initListeners();
    }

    // Finished activity with provided result code and passes back bundle with data
    private void finishActivityAndNotifyListener(int resultCode, Bundle data) {
        if (data == null)
            data = new Bundle();
        // Including processId and processToken if available
        if (createPaymentProcessResponse != null) {
            if (createPaymentProcessResponse.getProcessId() != null && !createPaymentProcessResponse.getProcessId().isEmpty())
                data.putString(KEY_PROCESS_ID, createPaymentProcessResponse.getProcessId());
            if (createPaymentProcessResponse.getProcessToken() != null && !createPaymentProcessResponse.getProcessToken().isEmpty())
                data.putString(KEY_PROCESS_TOKEN, createPaymentProcessResponse.getProcessToken());
            if (createPaymentProcessResponse.getBitPaymentId() != null && !createPaymentProcessResponse.getBitPaymentId().isEmpty())
                data.putString(KEY_BIT_PAYMENT_ID, createPaymentProcessResponse.getBitPaymentId());
        }

        PaymentManager.getInstance(activity()).notifyOnPaymentActivityResultListener(resultCode, data);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isStartPullOrderStatus)
            if (createPaymentProcessResponse != null) {
                PullBitPaymentStatus.getInstance().startPullingPaymentStatus(startSampleTs, interval, maxSampleTime, this, createPaymentProcessResponse);
            }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getDataString() != null && intent.getDataString().contains("imeshulamsdk://")) {
            isStartPullOrderStatus = false;
        }
        // TODO pass data from intent to request instead of passing it from createPaymentProcessResponse
        sendSetBitPaymentServerRequest(createPaymentProcessResponse.getApplicationToken(), createPaymentProcessResponse.getBitPaymentId());
    }

    private void sendSetBitPaymentServerRequest(String applicationToken, String bitPaymentId) {
        PaymentManager.getInstance(activity()).sendSetBitPaymentServerRequest(applicationToken, bitPaymentId, new OnServerRequestDoneListener() {
            @Override
            public void onSuccess(String requestName, SdkServerRequest sdkServerRequest, BaseServerRequestResponse baseResponse) {
                // Case transaction was successful

                Bundle bundle = new Bundle();
                String transactionId = PaymentManager.getInstance(activity()).getTransactionId(baseResponse);
                if (transactionId != null && !transactionId.isEmpty()) {
                    bundle.putSerializable(KEY_TRANSACTION_ID, transactionId);
                }
                bundle.putSerializable(KEY_CUSTOM_FIELDS, ((SetBitPaymentResponse) baseResponse).getCustomFields());
                finishActivityAndNotifyListener(RESULT_SUCCESS, bundle);
            }

            @Override
            public void onFailure(String requestName, SdkServerRequest sdkServerRequest, BaseServerRequestResponse.ServerRequestFailureResponse serverRequestFailure) {
                // Case transaction failed
                BaseDialogManager.getInstance(activity()).showRetryBitPaymentDialog(new CustomizableDialog.OnDialogClickListener() {
                    @Override
                    public void onClickBlue() {
                        PaymentManager.getInstance(activity()).sendDoPaymentServerRequest(createPaymentProcessResponse.getDoPaymentData(), new OnServerRequestDoneListener() {
                            @Override
                            public void onSuccess(String requestName, SdkServerRequest sdkServerRequest, BaseServerRequestResponse baseResponse) {
                                DoPaymentResponse doPaymentResponse = (DoPaymentResponse) baseResponse;
                                createPaymentProcessResponse.setUrl(doPaymentResponse.getApplicationLink());
                                createPaymentProcessResponse.setBitPaymentId(doPaymentResponse.getPaymentId());
                                BitManager.getInstance(activity()).loadBitUrl(doPaymentResponse.getApplicationLink());
                            }

                            @Override
                            public void onFailure(String requestName, SdkServerRequest sdkServerRequest, BaseServerRequestResponse.ServerRequestFailureResponse serverRequestFailure) {

                            }
                        });
                    }

                    @Override
                    public void onClickGrey() {
                        PaymentManager.getInstance(activity()).sendCancelBitPaymentServerRequest(applicationToken, bitPaymentId, new OnServerRequestDoneListener() {
                            @Override
                            public void onSuccess(String requestName, SdkServerRequest sdkServerRequest, BaseServerRequestResponse baseResponse) {
                                finishActivityAndNotifyListener(RESULT_FAILED, null);
                            }

                            @Override
                            public void onFailure(String requestName, SdkServerRequest sdkServerRequest, BaseServerRequestResponse.ServerRequestFailureResponse serverRequestFailure) {
                                // Case canceling payment failed
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SdkManager.getInstance(activity()).removeOnPaymentDataListener(this);
    }

    @Override
    public void paymentDataResponse(CreatePaymentProcessResponse createPaymentProcessResponse) {
        this.createPaymentProcessResponse = createPaymentProcessResponse;
        setOnBitPaymentStatusListener();
        new Handler(Looper.getMainLooper()).postDelayed(() ->
                BitManager.getInstance(activity()).loadBitUrl(createPaymentProcessResponse.getUrl()), 200);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            rlFinishPayment.setVisibility(View.VISIBLE);
            rlStartPayment.setVisibility(View.GONE);
            lottieBitAnimation.playAnimation();
        }, 1000);
    }
}
