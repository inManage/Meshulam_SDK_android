package il.co.inmanage.meshulam_sdk.interfaces;


import il.co.inmanage.meshulam_sdk.server_requests.SdkServerRequest;

import il.co.inmanage.meshulam_sdk.server_responses.BaseServerRequestResponse;

public interface OnFailureServerRequestListener {
    void onFailure(String requestName, SdkServerRequest sdkServerRequest, BaseServerRequestResponse.ServerRequestFailureResponse serverRequestFailure);
}
