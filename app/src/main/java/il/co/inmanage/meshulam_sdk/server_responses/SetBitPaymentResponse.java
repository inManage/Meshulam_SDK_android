package il.co.inmanage.meshulam_sdk.server_responses;

import il.co.inmanage.meshulam_sdk.parser.Parser;

import org.json.JSONObject;

import java.util.HashMap;

public class SetBitPaymentResponse extends BaseServerRequestResponse {

    private HashMap<String, String> customFields;
    private String getPaymentProcessResponse;

    @Override
    protected void parseData(JSONObject response) {
        JSONObject jsonObject = Parser.jsonParse(response, "customField", new JSONObject());
        JSONObject jsonObjectgetPaymentProcessResponse = Parser.jsonParse(response, "getPaymentProcessResponseInfo", new JSONObject());
        getPaymentProcessResponse = jsonObjectgetPaymentProcessResponse.toString();
        this.customFields = Parser.getHashMapFromJson(jsonObject);
    }

    public HashMap<String, String> getCustomFields() {
        return customFields;
    }

    public String getGetPaymentProcessResponse() {
        return getPaymentProcessResponse;
    }
}
