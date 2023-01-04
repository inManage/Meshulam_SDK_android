package il.co.inmanage.meshulam_sdk.sdk;

import android.app.Activity;

import androidx.annotation.Keep;

import il.co.inmanage.meshulam_sdk.data.CreatePaymentData;
import il.co.inmanage.meshulam_sdk.data.GetPaymentData;
import il.co.inmanage.meshulam_sdk.interfaces.MeshulamSdkInterface;
import il.co.inmanage.meshulam_sdk.interfaces.OnServerRequestDoneListener;
import il.co.inmanage.meshulam_sdk.managers.BaseManager;

import org.jetbrains.annotations.NotNull;

public abstract class MeshulamSdk extends BaseManager implements MeshulamSdkInterface {

    private static MeshulamSdk meshulamSdk;

    public static final String KEY_CUSTOM_FIELDS = "custom_fields";
    public static final String KEY_TRANSACTION_ID = "transaction_id";
    public static final String KEY_PROCESS_ID = "process_id";
    public static final String KEY_PROCESS_TOKEN = "process_token";
    public static final String KEY_BIT_PAYMENT_ID = "bit_payment_id";
    public final static String ERROR_KEY = "error_key";

    public boolean devMode = true;

    protected MeshulamSdk(Activity context) {
        super(context);
    }

    public SdkManager getSdkManager(){
        return SdkManager.getInstance(baseApplication);
    }

    public static MeshulamSdk getInstance(Activity activity) {
        return SdkManager.getInstance(activity);
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

    public boolean isDevMode() {
        return devMode;
    }

    @Override
    public void createPaymentProcess(CreatePaymentData createPaymentData, @NotNull SdkManager.OnPaymentResultListener onPaymentResultListener) {

    }

    @Override
    public void settleSuspendedTransaction(GetPaymentData getPaymentData, @NotNull SdkManager.OnSettlePaymentListener onSettlePaymentListener) {

    }

    @Override
    public void getPaymentProcessInfo(GetPaymentData getPaymentData, @NotNull SdkManager.OnGetInfoListener onGetInfoListener) {

    }

    @Override
    public void cancelBitPayment(GetPaymentData getPaymentData, @NotNull SdkManager.OnResultResultListener onPaymentResultListener) {

    }
}
