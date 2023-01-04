package il.co.inmanage.meshulam_sdk.interfaces;

import android.database.Cursor;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

import il.co.inmanage.meshulam_sdk.server_responses.BaseResponse;


public interface Parseable extends Serializable {

    /**
     * create response from jsonObject the json return from server
     *
     * @param jsonObject the json return from server
     * @return the current response
     */
    BaseResponse createResponse(JSONObject jsonObject);

    /**
     * create response from jsonObject the json return from server
     *
     * @param bundle create the response with inner params
     * @return the current response
     */
    BaseResponse createResponse(JSONObject jsonObject, Bundle bundle);

    /**
     * create response from jsonArray the json return from server
     *
     * @return the current response
     */
    BaseResponse createResponse(JSONArray jsonArray);

    /**
     * create response from jsonArray the json return from server
     *
     * @param bundle create the response with inner params
     * @return the current response
     */
    BaseResponse createResponse(JSONArray jsonArray, Bundle bundle);

    /**
     * create response from cursor the cursor return from local sql
     *
     * @return the current response
     */
    BaseResponse createResponse(Cursor cursor);

    /**
     * create response from cursor the cursor return from local sql
     *
     * @param bundle create the response with inner params
     * @return the current response
     */
    BaseResponse createResponse(Cursor cursor, Bundle bundle);
}
