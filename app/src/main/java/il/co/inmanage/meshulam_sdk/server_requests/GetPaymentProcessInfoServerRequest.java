package il.co.inmanage.meshulam_sdk.server_requests;

import android.app.Activity;

import il.co.inmanage.meshulam_sdk.data.GetPaymentData;
import il.co.inmanage.meshulam_sdk.interfaces.OnServerRequestDoneListener;
import il.co.inmanage.meshulam_sdk.server_responses.BaseServerRequestResponse;
import il.co.inmanage.meshulam_sdk.server_responses.GetPaymentProcessInfoResponse;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class GetPaymentProcessInfoServerRequest extends SdkServerRequest {

    public static final String REQUEST_NAME = "getPaymentProcessInfo";

    public GetPaymentProcessInfoServerRequest(GetPaymentData getPaymentData, Activity activity, OnServerRequestDoneListener onServerRequestDone) {
        super(REQUEST_NAME, getParams(getPaymentData), new OptionsRequest(), onServerRequestDone, activity);
    }

    public static HashMap<String, String> getParams(GetPaymentData getPaymentData) {
        HashMap<String, String> params = new HashMap<>();
        params.put("pageCode", getPaymentData.getPageCode());
        params.put("processId", getPaymentData.getProcessId());
        params.put("processToken", getPaymentData.getProcessToken());
        return params;
    }

    @Override
    protected BaseServerRequestResponse buildResponse(JSONObject jsonObject) {
        return new GetPaymentProcessInfoResponse().createResponse(jsonObject);
    }

    @Override
    protected Map<String, String> getRequestHeaders() {
        Map<String ,String > stringStringMap = new HashMap<>();
        stringStringMap.put("TOKEN", "inmanga_secure");
        return stringStringMap;
    }

    @Override
    protected Type getType() {
        return new TypeToken<GetPaymentProcessInfoResponse>() {
        }.getType();
    }
}
