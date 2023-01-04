package il.co.inmanage.meshulam_sdk.server_request_data;

import org.json.JSONObject;

import java.io.Serializable;

import il.co.inmanage.meshulam_sdk.parser.Parser;
import il.co.inmanage.meshulam_sdk.server_responses.BaseResponse;

public class Language extends BaseResponse {

    private boolean active;
    private String id;
    private String title;
    //private String description;
    private LanguageDirectionEnum direction;
    private String languageAsJsonString;

    public Language() {

    }

    @Override
    public BaseResponse createResponse(JSONObject jsonObject) {
        Language language = new Language();
        language.active = Parser.jsonParse(jsonObject, "active", false);
        language.id = Parser.jsonParse(jsonObject, "id", Parser.createTempString());
        language.title = Parser.jsonParse(jsonObject, "title", Parser.createTempString());
        //language.description = Parser.jsonParse(jsonObject, "description", Parser.createTempString());
        language.direction = Parser.jsonParse(jsonObject, "direction", LanguageDirectionEnum.values()[LanguageDirectionEnum.RTL.ordinal()]);
        language.languageAsJsonString = jsonObject.toString();
        return language;
    }

    public boolean isActive() {
        return active;
    }

    /*public void setActive(boolean active) {
        this.active = active;
    }*/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /*public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }*/

    public LanguageDirectionEnum getDirection() {
        return direction;
    }

    /*public void setDirection(LanguageDirectionEnum direction) {
        this.direction = direction;
    }*/

    public String getLanguageAsJsonString() {
        return languageAsJsonString;
    }

    public enum LanguageDirectionEnum implements Serializable {
        DEFAULT,
        LTR,
        RTL
    }
}
