package il.co.inmanage.meshulam_sdk.server_request_data;

import org.json.JSONObject;

import java.util.List;

import il.co.inmanage.meshulam_sdk.managers.TimeManager;
import il.co.inmanage.meshulam_sdk.parser.Parser;
import il.co.inmanage.meshulam_sdk.server_responses.BaseResponse;


public class TimeData extends BaseResponse {

    private long serverTimeInMili;
    private String timeZone;
    private List<Language> languageList;

    public TimeData() {

    }

    @SuppressWarnings("unchecked")

    @Override
    public BaseResponse createResponse(JSONObject jsonObject) {
        serverTimeInMili = Parser.jsonParse(jsonObject, "server_time", (System.currentTimeMillis() / TimeManager.SECOND)) * TimeManager.SECOND;
        timeZone = Parser.jsonParse(jsonObject, "time_zone", TimeManager.DEFAULT_TIME_ZONE);
        JSONObject languagesArr = Parser.jsonParse(jsonObject, "languagesArr", Parser.createTempJsonObject());
        this.languageList = (List<Language>) Parser.createList(languagesArr, new Language());
        return this;
    }

    public long getServerTimeInMili() {
        return serverTimeInMili;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public List<Language> getLanguageList() {
        return languageList;
    }

    public void setServerTimeInMili(long serverTimeInMili) {
        this.serverTimeInMili = serverTimeInMili;
    }
}
