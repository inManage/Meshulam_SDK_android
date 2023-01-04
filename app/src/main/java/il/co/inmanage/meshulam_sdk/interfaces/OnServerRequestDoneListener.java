package il.co.inmanage.meshulam_sdk.interfaces;

import il.co.inmanage.meshulam_sdk.server_requests.SdkServerRequest;

import il.co.inmanage.meshulam_sdk.server_responses.BaseServerRequestResponse;

public interface OnServerRequestDoneListener extends OnFailureServerRequestListener {
    void onSuccess(String requestName, SdkServerRequest sdkServerRequest, BaseServerRequestResponse baseResponse);
}
