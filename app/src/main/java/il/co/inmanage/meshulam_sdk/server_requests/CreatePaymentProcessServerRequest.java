package il.co.inmanage.meshulam_sdk.server_requests;

import android.app.Activity;

import il.co.inmanage.meshulam_sdk.data.CreatePaymentData;
import il.co.inmanage.meshulam_sdk.interfaces.OnServerRequestDoneListener;
import il.co.inmanage.meshulam_sdk.server_responses.BaseServerRequestResponse;
import il.co.inmanage.meshulam_sdk.server_responses.CreatePaymentProcessResponse;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CreatePaymentProcessServerRequest extends SdkServerRequest {

    private static final String REQUEST_NAME = "createPaymentProcess";

    public CreatePaymentProcessServerRequest(CreatePaymentData createPaymentData, Activity activity, OnServerRequestDoneListener onServerRequestDone) {
        super(REQUEST_NAME, getParams(createPaymentData), new OptionsRequest(), onServerRequestDone, activity);
    }

    public static HashMap<String, String> getParams(CreatePaymentData createPaymentData) {
        HashMap<String, String> params = new HashMap<>();
        if(createPaymentData!=null && createPaymentData.getPageCode()!=null) params.put("pageCode", createPaymentData.getPageCode());
        if(createPaymentData!=null && createPaymentData.getUserId()!=null) params.put("userId", createPaymentData.getUserId());
        if(createPaymentData!=null && createPaymentData.getApiKey()!=null) params.put("apiKey", createPaymentData.getApiKey());
        if(createPaymentData!=null && createPaymentData.getSum()!=null) params.put("sum", createPaymentData.getSum());
        if(createPaymentData!=null && createPaymentData.getFullName()!=null) params.put("pageField[fullName]", createPaymentData.getFullName());
        if(createPaymentData!=null && createPaymentData.getChargeType()!=null) params.put("chargeType", createPaymentData.getChargeType());
        if(createPaymentData!=null && createPaymentData.getTemplateType()!=null) params.put("templateType", createPaymentData.getTemplateType());
        if(createPaymentData!=null && createPaymentData.getApplicationToken()!=null) params.put("applicationToken", createPaymentData.getApplicationToken());
        if(createPaymentData!=null && createPaymentData.getPhone()!=null) params.put("pageField[phone]", createPaymentData.getPhone());
        //optional
        if(createPaymentData!=null && createPaymentData.getDescription()!=null) params.put("description", createPaymentData.getDescription());
        if(createPaymentData!=null && createPaymentData.getEmail()!=null) params.put("pageField[email]", createPaymentData.getEmail());
//        if(createPaymentData!=null && createPaymentData.getcFieldsList()!=null){
//            for (int i = 0; i < createPaymentData.getcFieldsList().size(); i++) {
//                if(createPaymentData.getcFieldsList().get(i)!=null) params.put("cField" + (i+1), createPaymentData.getcFieldsList().get(i));
//            }
//        }
        return params;
    }

    @Override
    protected BaseServerRequestResponse buildResponse(JSONObject jsonObject) {
        return new CreatePaymentProcessResponse().createResponse(jsonObject);
    }

    @Override
    protected Map<String, String> getRequestHeaders() {
        Map<String ,String > stringStringMap = new HashMap<>();
        stringStringMap.put("TOKEN", "inmanga_secure");
        return stringStringMap;
    }

    @Override
    protected Type getType() {
        return new TypeToken<CreatePaymentProcessResponse>() {
        }.getType();
    }
}
