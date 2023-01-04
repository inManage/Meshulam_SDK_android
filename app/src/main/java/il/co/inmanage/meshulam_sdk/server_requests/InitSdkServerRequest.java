package il.co.inmanage.meshulam_sdk.server_requests;

import android.app.Activity;

import il.co.inmanage.meshulam_sdk.interfaces.OnServerRequestDoneListener;
import il.co.inmanage.meshulam_sdk.server_responses.BaseServerRequestResponse;
import il.co.inmanage.meshulam_sdk.server_responses.InitSdkResponse;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class InitSdkServerRequest extends SdkServerRequest {

    public static final String REQUEST_NAME = "initSDK/";

    public InitSdkServerRequest(String udid, Activity activity, OnServerRequestDoneListener onServerRequestDone) {
        super(REQUEST_NAME, getParams(udid), new OptionsRequest(), onServerRequestDone, activity);
    }

    public static HashMap<String, String> getParams(String udid) {
        HashMap<String, String> params = new HashMap<>();
        params.put("udid", udid);
        return params;
    }

    @Override
    protected BaseServerRequestResponse buildResponse(JSONObject jsonObject) {
        return new InitSdkResponse().createResponse(jsonObject);
    }

    @Override
    protected Map<String, String> getRequestHeaders() {
        Map<String ,String > stringStringMap = new HashMap<>();
        stringStringMap.put("TOKEN", "inmanga_secure");
        return stringStringMap;
    }

    @Override
    protected Type getType() {
        return new TypeToken<InitSdkResponse>() {
        }.getType();
    }
}
