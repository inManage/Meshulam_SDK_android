package il.co.inmanage.meshulam_sdk.data;


import il.co.inmanage.meshulam_sdk.server_responses.BaseGeneralDeclarationResponse;
import il.co.inmanage.meshulam_sdk.server_responses.InitSdkResponse;

public class BaseSessionData {

    private BaseGeneralDeclarationResponse generalDeclarationResponse;
    private String applicationToken;
    private String colorHeaderHex;
    private String splashImageUrl;
    private InitSdkResponse initSdkResponse;

    public BaseSessionData() {
    }

    public BaseGeneralDeclarationResponse getGeneralDeclarationResponse() {
        return generalDeclarationResponse;
    }

    public void setGeneralDeclarationResponse(BaseGeneralDeclarationResponse generalDeclarationResponse) {
        this.generalDeclarationResponse = generalDeclarationResponse;
    }

    public String getApplicationToken() {
        return applicationToken;
    }

    public void setApplicationToken(String applicationToken) {
        this.applicationToken = applicationToken;
    }

    public boolean hasApplicationToken() {
        return applicationToken != null && !applicationToken.isEmpty();
    }

    public String getColorHeaderHex() {
        return colorHeaderHex;
    }

    public void setColorHeaderHex(String colorHeaderHex) {
        this.colorHeaderHex = colorHeaderHex;
    }

    public String getSplashImageUrl() {
        return splashImageUrl;
    }

    public void setSplashImageUrl(String splashImageUrl) {
        this.splashImageUrl = splashImageUrl;
    }

    public InitSdkResponse getInitSdkResponse() {
        return initSdkResponse;
    }

    public void setInitSdkResponse(InitSdkResponse initSdkResponse) {
        this.initSdkResponse = initSdkResponse;
    }
}
