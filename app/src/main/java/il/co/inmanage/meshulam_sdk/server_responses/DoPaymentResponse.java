package il.co.inmanage.meshulam_sdk.server_responses;

import il.co.inmanage.meshulam_sdk.parser.Parser;

import org.json.JSONObject;

public class DoPaymentResponse extends BaseServerRequestResponse {

    private String paymentId;
    private String applicationLink;

    @Override
    protected void parseData(JSONObject response) {
        paymentId = Parser.jsonParse(response, "bit_payment_id", Parser.createTempString());
        applicationLink = Parser.jsonParse(response, "application_link", Parser.createTempString());
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getApplicationLink() {
        return applicationLink;
    }
}
