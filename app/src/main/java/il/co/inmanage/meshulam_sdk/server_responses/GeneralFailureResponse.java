package il.co.inmanage.meshulam_sdk.server_responses;

import org.json.JSONObject;

public class GeneralFailureResponse extends BaseServerRequestResponse {

    private static final long serialVersionUID = 6320634777330514426L;

    @Override
    protected void parseData(JSONObject response) {

    }

    public ServerRequestFailureResponse createGeneralFailureRequest() {
        ServerRequestFailureResponse serverRequestFailureResponse = new ServerRequestFailureResponse();
        serverRequestFailureResponse.setErrorId(ServerRequestFailureResponse.DEFAULT_ERROR_ID);
        serverRequestFailureResponse.setContent("");
        return serverRequestFailureResponse;
    }
}
