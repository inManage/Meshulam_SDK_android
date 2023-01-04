package il.co.inmanage.meshulam_sdk.server_responses;

import il.co.inmanage.meshulam_sdk.managers.SdkRequestManager;
import il.co.inmanage.meshulam_sdk.managers.TimeManager;
import il.co.inmanage.meshulam_sdk.parser.Parser;
import il.co.inmanage.meshulam_sdk.server_request_data.TimeData;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")

public class BaseGeneralDeclarationResponse extends BaseServerRequestResponse {

    private static final int DEFAULT_VALUE_SERVER_RECALL = 5;
    private static final int DEFAULT_VALUE_RESTART_ON_IDLE = 25;
    private static final int DEFAULT_VALUE_METHOD_TIMEOUT = 4;
    private static final int DEFAULT_VALUE_BANNER_DISPLAY_SECONDS = 4;
    private static final int DEFAULT_VALUE_EXCEPTIONAL_METHOD_TIMEOUT = 40;
    //public static final String TRANSLATION_MAP_KEY = "TRANSLATION_MAP_KEY";
    private static final long serialVersionUID = 3710677812992715796L;

    //private static final Integer DEFAULT_TIME_OUT_GPS = 5000;
    //private int gpsTimeout;
    private int restartOnIdle, methodTimeout, methodAttempts;
    private long translationsLastUpdate, zipFileLength;
    private String zipFilePath, mediaServer, htmlFontStyle;
    private boolean isZipMediaDownload, showErrorId;
    private TimeData timeData;
    private HashMap<String, String> translationsMap;
    private List<Integer> throwErrorList, registrationErrorList, loginErrorList;
    private int bannerDisplaySeconds, exceptionalMethodTimeout, serverRecall, messageInterval;
    private List<String> exceptionalTimeoutMethodsList;
    private HashMap<String, String> colorsHashMap;
    //private HashMap<String, Boolean> featureFlagsMap;

    @Override
    protected void parseData(JSONObject responseJsonObject) {
        parseTranslations(responseJsonObject);
        parseErrors(responseJsonObject);
        parseTimes(responseJsonObject);
        parseMisc(responseJsonObject);
        parseDurations(responseJsonObject);
        messageInterval = Parser.jsonParse(responseJsonObject, "message_interval", 1) * TimeManager.SECOND;
    }

    public int getMessageInterval() {
        return messageInterval;
    }


    private void parseDurations(JSONObject responseJsonObject) {
        restartOnIdle = Parser.jsonParse(responseJsonObject, "restart_on_idle", DEFAULT_VALUE_RESTART_ON_IDLE) * TimeManager.MINTUE;
        methodTimeout = Parser.jsonParse(responseJsonObject, "method_timeout", DEFAULT_VALUE_METHOD_TIMEOUT) * TimeManager.SECOND;
        exceptionalMethodTimeout = Parser.jsonParse(responseJsonObject, "exceptional_method_timeout", DEFAULT_VALUE_EXCEPTIONAL_METHOD_TIMEOUT) * TimeManager.SECOND;
    }

    private void parseTranslations(JSONObject responseJsonObject) {
        JSONObject translationsArr = Parser.jsonParse(responseJsonObject, "translationsArr", Parser.createTempJsonObject());
        buildTranslationsMap(translationsArr);
        translationsLastUpdate = Parser.jsonParse(responseJsonObject, "translations_last_update", translationsLastUpdate);
    }

    private void parseErrors(JSONObject responseJsonObject) {
        showErrorId = Parser.jsonParse(responseJsonObject, "show_error_id", showErrorId);

        JSONObject throwErrorArr = Parser.jsonParse(responseJsonObject, "throw_errorArr", Parser.createTempJsonObject());
        this.throwErrorList = Parser.createList(throwErrorArr, -1);

        JSONObject throwErrorRegisterArr = Parser.jsonParse(responseJsonObject, "throw_to_registrationArr", Parser.createTempJsonObject());
        registrationErrorList = Parser.createList(throwErrorRegisterArr, -1);

        JSONObject throwErrorLoginArr = Parser.jsonParse(responseJsonObject, "throw_to_loginArr", Parser.createTempJsonObject());
        loginErrorList = Parser.createList(throwErrorLoginArr, -1);

        JSONObject exceptionalTimeoutMethods = Parser.jsonParse(responseJsonObject, "exceptional_timeout_methods", Parser.createTempJsonObject());
        this.exceptionalTimeoutMethodsList = Parser.createList(exceptionalTimeoutMethods, Parser.createTempString());

    }

    /*private void parseMediaFile(JSONObject responseJsonObject) {
        JSONObject mediaFile = Parser.jsonParse(responseJsonObject, "media_file", Parser.createTempJsonObject());
        HashMap<String, String> mediaFileHashMap = Parser.getHashMapFromJson(mediaFile);
    }*/

    public HashMap<String, String> getColorsHashMap() {
        if (colorsHashMap == null) {
            colorsHashMap = new HashMap<>();
        }
        return colorsHashMap;
    }

    /*public HashMap<String, Boolean> getFeatureFlagsMap() {
        return featureFlagsMap;
    }|*/

    public String getZipFilePath() {
        return zipFilePath;
    }

    public long getZipFileLength() {
        return zipFileLength;
    }

    public boolean isZipMediaDownload() {
        return isZipMediaDownload;
    }

    private void parseTimes(JSONObject responseJsonObject) {
        timeData = (TimeData) new TimeData().createResponse(responseJsonObject);
    }

    private void parseMisc(JSONObject responseJsonObject) {
        mediaServer = Parser.jsonParse(responseJsonObject, "media_server", SdkRequestManager.BASE_URL_HOST);
        methodAttempts = Parser.jsonParse(responseJsonObject, "method_attempts", 0);
        htmlFontStyle = Parser.jsonParse(responseJsonObject, "font_android", Parser.createTempString());
        serverRecall = Parser.jsonParse(responseJsonObject, "server_recall", DEFAULT_VALUE_SERVER_RECALL) * TimeManager.SECOND;
    }

    public TimeData getTimeData() {
        return timeData;
    }

    public int getRestartOnIdle() {
        return restartOnIdle;
    }

    public int getMethodTimeout() {
        return methodTimeout;
    }

    public int getExceptionalMethodTimeout() {
        return exceptionalMethodTimeout;
    }

    public List<Integer> getThrowErrorList() {
        return throwErrorList;
    }

    public HashMap<String, String> getTranslationsMap() {
        return translationsMap;
    }

    public String getMediaServer() {
        if (mediaServer.isEmpty() || mediaServer.equals("null")) {
            mediaServer = SdkRequestManager.BASE_URL_HOST;
        }
        return mediaServer;
    }

    public int getMethodAttempts() {
        return methodAttempts;
    }

    public List<Integer> getRegistrationErrorList() {
        return registrationErrorList;
    }


    private void buildTranslationsMap(JSONObject translationData) {
        this.translationsMap = Parser.getHashMapFromJson(translationData);
    }

    public String getHtmlFontStyle() {
         return htmlFontStyle;
    }


    public int getBannerDisplaySeconds() {
        return bannerDisplaySeconds;
    }

    public List<String> getExceptionalTimeoutMethodsList() {
        return exceptionalTimeoutMethodsList;
    }

    public int getServerRecall() {
        return serverRecall;
    }

    public List<Integer> getLoginErrorList() {
        return loginErrorList;
    }
}