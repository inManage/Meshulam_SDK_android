package il.co.inmanage.meshulam_sdk.server_responses;

import android.database.Cursor;
import android.os.Bundle;

import il.co.inmanage.meshulam_sdk.interfaces.Parseable;

import org.json.JSONArray;
import org.json.JSONObject;


public abstract class BaseResponse implements Parseable {

    @Override
    public BaseResponse createResponse(JSONArray jsonArray) {
        return this;
    }

    @Override
    public BaseResponse createResponse(Cursor cursor) {
        return this;
    }

    @Override
    public BaseResponse createResponse(Cursor cursor, Bundle bundle) {
        return this;
    }

    @Override
    public BaseResponse createResponse(JSONArray jsonArray, Bundle bundle) {
        return this;
    }

    @Override
    public BaseResponse createResponse(JSONObject jsonObject, Bundle bundle) {
        return this;
    }
}
