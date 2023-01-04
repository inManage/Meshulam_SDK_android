package il.co.inmanage.meshulam_sdk.server_requests;

import android.app.Activity;

import il.co.inmanage.meshulam_sdk.data.GetPaymentData;
import il.co.inmanage.meshulam_sdk.interfaces.OnServerRequestDoneListener;
import il.co.inmanage.meshulam_sdk.server_responses.BaseServerRequestResponse;
import il.co.inmanage.meshulam_sdk.server_responses.CancelBitTransactionResponse;
import il.co.inmanage.meshulam_sdk.utils.BaseEncryption;

import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CancelBitTransactionServerRequest extends SdkServerRequest {
    public static final String REQUEST_NAME = "cancelBitTransaction";

    public CancelBitTransactionServerRequest(String applicationToken, GetPaymentData getPaymentData , Activity activity, OnServerRequestDoneListener onServerRequestDone) {
        super(REQUEST_NAME, getParams(applicationToken,getPaymentData), new OptionsRequest(), onServerRequestDone, activity);
    }

    public static HashMap<String, String> getParams(String applicationToken, GetPaymentData getPaymentData) {
        HashMap<String, String> params = new HashMap<>();
        try {
            params.put("pageCode", BaseEncryption.getInstance().encryptAesString(getPaymentData.getPageCode()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.put("processId", getPaymentData.getProcessId());
        params.put("processToken", getPaymentData.getProcessToken());
        return params;
    }

    @Override
    protected BaseServerRequestResponse buildResponse(JSONObject jsonObject) {
        return new CancelBitTransactionResponse().createResponse(jsonObject);
    }

    @Override
    protected Map<String, String> getRequestHeaders() {
        Map<String ,String > stringStringMap = new HashMap<>();
        stringStringMap.put("TOKEN", "inmanga_secure");
        return stringStringMap;
    }

    @Override
    protected Type getType() {
        return new TypeToken<CancelBitTransactionResponse>() {
        }.getType();
    }
}
