package il.co.inmanage.meshulam_sdk.server_responses;

import android.os.Bundle;

import il.co.inmanage.meshulam_sdk.parser.Parser;

import org.json.JSONObject;

import java.io.Serializable;


public abstract class BaseServerRequestResponse extends BaseResponse implements Serializable {

    public static final String FILE_NAME = BaseServerRequestResponse.class.getSimpleName();
    private static final long serialVersionUID = 7557900302338025446L;
    private int status;
    private String responseMessage;
    private ServerRequestFailureResponse serverRequestFailureResponse;

    public BaseServerRequestResponse() {

    }

    protected abstract void parseData(JSONObject response);


    protected void parseData(JSONObject response, Bundle bundle) {
    }

    public BaseServerRequestResponse createResponse(JSONObject responseJsonObject) {
        return createResponse(responseJsonObject, null);
    }


    public BaseServerRequestResponse createResponse(JSONObject responseJsonObject, Bundle bundle) {
        this.status = Parser.jsonParse(responseJsonObject, "status", 0);
        if (isSuccess()) {
            JSONObject jsonObject = Parser.jsonParse(responseJsonObject, "data", responseJsonObject);
            //parseCart(jsonObject);
//             parseData(jsonObject != null ? jsonObject : responseJsonObject);
            if (bundle == null) {
                parseData(jsonObject);
            } else {
                parseData(jsonObject, bundle);
            }
        } else {
            JSONObject jsonObject = Parser.jsonParse(responseJsonObject, "err", new JSONObject());
            parseError(jsonObject);
        }
        this.responseMessage = Parser.jsonParse(responseJsonObject, "message", Parser.createTempString());
        return this;
    }

    public boolean isSuccess() {
        return status == 1;
    }

    /*public int getStatus() {
        return status;
    }*/

    public ServerRequestFailureResponse getServerRequestFailureResponse() {
        return serverRequestFailureResponse;
    }

    private void parseError(JSONObject jsonObject) {

        serverRequestFailureResponse = (ServerRequestFailureResponse) new ServerRequestFailureResponse().createResponse(jsonObject);
    }

    public String getResponseMessage() {
        return responseMessage;
    }


    public class ServerRequestFailureResponse extends BaseResponse implements Serializable {

        public static final int DEFAULT_ERROR_ID = -1;
        private static final long serialVersionUID = -8650985309276411016L;
        int errorId;
        String content;

        ServerRequestFailureResponse() {

        }

        public ServerRequestFailureResponse(String errorMessage, int errorId) {
            this.errorId = errorId;
            this.content = errorMessage;
        }

        @Override
        public BaseResponse createResponse(JSONObject jsonObject) {

            ServerRequestFailureResponse errorResponse = new ServerRequestFailureResponse();

            errorResponse.errorId = Parser.jsonParse(jsonObject, "id", errorId);
            errorResponse.content = Parser.jsonParse(jsonObject, "message", Parser.createTempString());

            return errorResponse;
        }

        public String getContent() {
            return content;
        }

        void setContent(String content) {
            this.content = content;
        }

        public int getErrorId() {
            return errorId;
        }

        void setErrorId(int errorId) {
            this.errorId = errorId;
        }

        public String getResponseMessage() {
            return responseMessage;
        }
    }
}
