package il.co.inmanage.meshulam_sdk.server_responses;

import il.co.inmanage.meshulam_sdk.managers.TimeManager;
import il.co.inmanage.meshulam_sdk.parser.Parser;

import org.json.JSONObject;

import java.util.HashMap;

public class InitSdkResponse extends BaseServerRequestResponse {

    private String hostUrl;
    private String applicationToken;
    private long startDelay;
    private long intervalLength;
    private long maxTime;
    private String pageCode;
    private HashMap<String, String > translationsMap;
    @Override
    protected void parseData(JSONObject response) {
        hostUrl = Parser.jsonParse(response, "hostUrl", Parser.createTempString());
        applicationToken = Parser.jsonParse(response, "applicationToken", Parser.createTempString());
        startDelay = Parser.jsonParse(response, "startDelay", 5);
        intervalLength = Parser.jsonParse(response, "intervalLength", 5);
        maxTime = Parser.jsonParse(response, "maxTime", 900);
        JSONObject translationsArr = Parser.jsonParse(response, "translationArr", Parser.createTempJsonObject());
        buildTranslationsMap(translationsArr);
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public String getApplicationToken() {
        return applicationToken;
    }

    public long getStartDelay() {
        return startDelay* TimeManager.SECOND;
    }

    public long getIntervalLength() {
        return intervalLength* TimeManager.SECOND;
    }

    public long getMaxTime() {
        return maxTime* TimeManager.SECOND;
    }

    public HashMap<String, String> getTranslationsMap() {
        return translationsMap;
    }

    public void setPageCode(String pageCode) {
        this.pageCode = pageCode;
    }

    public String getPageCode() {
        return pageCode;
    }

    public void buildTranslationsMap(JSONObject translationData) {
        this.translationsMap = Parser.getHashMapFromJson(translationData);
    }
}
