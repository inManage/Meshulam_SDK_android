package il.co.inmanage.meshulam_sdk.data;

import il.co.inmanage.meshulam_sdk.parser.Parser;
import il.co.inmanage.meshulam_sdk.server_responses.SortResponse;

import org.json.JSONObject;

public class ActionButton extends SortResponse {

    private String title;
    private String deeplink;

    @Override
    protected SortResponse createSortResponse(JSONObject jsonObject) {
        ActionButton actionButton = new ActionButton();
        actionButton.title = Parser.jsonParse(jsonObject, "title", Parser.createTempString());
        actionButton.deeplink = Parser.jsonParse(jsonObject, "deeplink", Parser.createTempString());
        return actionButton;
    }

    public String getTitle() {
        return title;
    }

    public String getDeeplink() {
        return deeplink;
    }
}
