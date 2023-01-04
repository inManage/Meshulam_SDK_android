package il.co.inmanage.meshulam_sdk.server_requests;

import android.app.Activity;

import il.co.inmanage.meshulam_sdk.data.DoPaymentData;
import il.co.inmanage.meshulam_sdk.interfaces.OnServerRequestDoneListener;
import il.co.inmanage.meshulam_sdk.server_responses.BaseServerRequestResponse;
import il.co.inmanage.meshulam_sdk.server_responses.DoPaymentResponse;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class DoPaymentServerRequest extends SdkServerRequest {

    public static final String REQUEST_NAME = "doPayment";

    public DoPaymentServerRequest(DoPaymentData doPaymentData, Activity activity, OnServerRequestDoneListener onServerRequestDone) {
        super(REQUEST_NAME, getParams(doPaymentData), new OptionsRequest(), onServerRequestDone, activity);
    }

    public static HashMap<String, String> getParams(DoPaymentData doPaymentData) {
        HashMap<String, String> params = new HashMap<>();
        if(doPaymentData.getFullName()!=null){
            params.put("full_name", doPaymentData.getFullName());
        }
        if(doPaymentData.getPhone()!=null){
            params.put("phone", doPaymentData.getPhone());
        }
        if(doPaymentData.getEmail()!=null){
            params.put("email", doPaymentData.getEmail());
        }
        if(doPaymentData.getSum()!=null){
            params.put("sum", doPaymentData.getSum());
        }
        if(doPaymentData.getDescription()!=null){
            params.put("description", doPaymentData.getDescription());
        }
        if(doPaymentData.getTransactionTypeId()!=null){
            params.put("transaction_type_id", doPaymentData.getTransactionTypeId());
        }
        if(doPaymentData.getPaymentNum()!=null){
            params.put("payment_num", doPaymentData.getPaymentNum());
        }
        if(doPaymentData.getTypeId()!=null){
            params.put("type_id", doPaymentData.getTypeId());
        }
        if(doPaymentData.getPageHash()!=null){
            params.put("page_hash", doPaymentData.getPageHash());
        }
        if(doPaymentData.getFirstPaymentSum()!=null){
            params.put("first_payment_sum", doPaymentData.getFirstPaymentSum());
        }
        if(doPaymentData.getPeriodicalPaymentSum()!=null){
            params.put("periodical_payment_sum", doPaymentData.getPeriodicalPaymentSum());
        }
        params.put("show_payments_select", doPaymentData.isShowPaymentsSelect() ?"1" : "0");
        params.put("platform", "android");
        return params;
    }

    @Override
    protected BaseServerRequestResponse buildResponse(JSONObject jsonObject) {
        return new DoPaymentResponse().createResponse(jsonObject);
    }

    @Override
    protected Map<String, String> getRequestHeaders() {
        Map<String ,String > stringStringMap = new HashMap<>();
        stringStringMap.put("TOKEN", "inmanga_secure");
        return stringStringMap;
    }

    @Override
    protected Type getType() {
        return new TypeToken<DoPaymentResponse>() {
        }.getType();
    }
}
