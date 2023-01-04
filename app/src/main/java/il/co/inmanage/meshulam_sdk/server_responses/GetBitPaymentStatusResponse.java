package il.co.inmanage.meshulam_sdk.server_responses;

import il.co.inmanage.meshulam_sdk.parser.Parser;

import org.json.JSONObject;

public class GetBitPaymentStatusResponse extends BaseServerRequestResponse {

    private int paymentStatus;

    @Override
    protected void parseData(JSONObject response) {
        paymentStatus = Parser.jsonParse(response, "payment_status", 0);
    }

    public int getPaymentStatus(){ return paymentStatus; }
}
