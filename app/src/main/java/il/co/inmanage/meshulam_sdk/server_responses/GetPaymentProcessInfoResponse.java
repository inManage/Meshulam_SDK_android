package il.co.inmanage.meshulam_sdk.server_responses;

import org.json.JSONObject;

public class GetPaymentProcessInfoResponse extends BaseServerRequestResponse {

    String  response;
    @Override
    protected void parseData(JSONObject response) {
        this.response = String.valueOf(response);
    }

    public String getResponse() {
        return response;
    }
}
