package il.co.inmanage.meshulam_sdk.server_responses;

import il.co.inmanage.meshulam_sdk.data.DoPaymentData;
import il.co.inmanage.meshulam_sdk.parser.Parser;

import org.json.JSONObject;

public class CreatePaymentProcessResponse extends BaseServerRequestResponse {

    private String bitPaymentId;
    private String processId;
    private String processToken;
    private String url;
    private DoPaymentData doPaymentData;
    private String applicationToken;

    @Override
    protected void parseData(JSONObject response) {
        bitPaymentId = Parser.jsonParse(response, "bit_payment_id", Parser.createTempString());
        processId = Parser.jsonParse(response, "processId", Parser.createTempString());
        processToken = Parser.jsonParse(response, "processToken", Parser.createTempString());
        url = Parser.jsonParse(response, "url", Parser.createTempString());
        JSONObject doPaymentRequestArr = Parser.jsonParse(response, "do_payment_requestArr", new JSONObject());
        doPaymentData = (DoPaymentData) new DoPaymentData().createResponse(doPaymentRequestArr);
    }

    public String getBitPaymentId() {
        return bitPaymentId;
    }

    public String getProcessId() {
        return processId;
    }

    public String getProcessToken() {
        return processToken;
    }

    public String getUrl() {
        return url;
    }

    public DoPaymentData getDoPaymentData() {
        return doPaymentData;
    }

    public String getApplicationToken() {
        return applicationToken;
    }

    public void setApplicationToken(String applicationToken) {
        this.applicationToken = applicationToken;
    }

    public void setBitPaymentId(String bitPaymentId) {
        this.bitPaymentId = bitPaymentId;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
