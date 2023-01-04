package il.co.inmanage.meshulam_sdk.server_requests;

import android.app.Activity;

import il.co.inmanage.meshulam_sdk.interfaces.OnServerRequestDoneListener;
import il.co.inmanage.meshulam_sdk.server_responses.BaseServerRequestResponse;
import il.co.inmanage.meshulam_sdk.server_responses.CancelBitPaymentResponse;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CancelBitPaymentServerRequest extends SdkServerRequest {
    public static final String REQUEST_NAME = "cancelBitPayment";

    public CancelBitPaymentServerRequest(String applicationToken, String bitPaymentId , Activity activity, OnServerRequestDoneListener onServerRequestDone) {
        super(REQUEST_NAME, getParams(applicationToken,bitPaymentId), new OptionsRequest(), onServerRequestDone, activity);
    }

    public static HashMap<String, String> getParams(String applicationToken, String bitPaymentId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("applicationToken", applicationToken);
        params.put("bit_payment_id", bitPaymentId);
        return params;
    }

    @Override
    protected BaseServerRequestResponse buildResponse(JSONObject jsonObject) {
        return new CancelBitPaymentResponse().createResponse(jsonObject);
    }

    @Override
    protected Map<String, String> getRequestHeaders() {
        Map<String ,String > stringStringMap = new HashMap<>();
        stringStringMap.put("TOKEN", "inmanga_secure");
        return stringStringMap;
    }

    @Override
    protected Type getType() {
        return new TypeToken<CancelBitPaymentResponse>() {
        }.getType();
    }
}
