package il.co.inmanage.meshulam_sdk.server_responses;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;

import il.co.inmanage.meshulam_sdk.parser.Parser;

import org.json.JSONObject;

import java.util.List;

public abstract class SortResponse extends BaseResponse implements Comparable<SortResponse> {

    private int orderNum;
    private int idNum;

    protected SortResponse() {

    }

    protected SortResponse(SortResponse sortResponse) {
        orderNum = sortResponse.getOrderNum();
        idNum = sortResponse.getIdNum();
    }

    public static SortResponse getSortResponseById(List<SortResponse> sortResponseList, int id) {
        for (SortResponse sortResponse : sortResponseList) {
            if (sortResponse.getIdNum() == id) {
                return sortResponse;
            }
        }
        return null;
    }

    protected abstract SortResponse createSortResponse(JSONObject jsonObject);

    protected SortResponse createSortResponse(Cursor cursor) {
        return this;
    }

    protected SortResponse createSortResponse(JSONObject jsonObject, Bundle bundle) {
        return this;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public int getIdNum() {
        return idNum;
    }

    public String getIdNumAsString() {
        return idNum + "";
    }

    @Override
    public BaseResponse createResponse(JSONObject jsonObject) {
        SortResponse sortResponse = createSortResponse(jsonObject);
        parseSortResponse(jsonObject, sortResponse);
        return sortResponse;
    }

    @Override
    public BaseResponse createResponse(JSONObject jsonObject, Bundle bundle) {
        SortResponse sortResponse = createSortResponse(jsonObject, bundle);
        parseSortResponse(jsonObject, sortResponse);
        return sortResponse;
    }

    private void parseSortResponse(JSONObject jsonObject, SortResponse sortResponse) {
        sortResponse.orderNum = Parser.jsonParse(jsonObject, "order_num", 0);
        sortResponse.idNum = Parser.jsonParse(jsonObject, "id", 0);
    }


    @Override
    public int compareTo(@NonNull SortResponse another) {
        if (getOrderNum() == another.getOrderNum()) {
            return getIdNum() - another.getIdNum();
        }
        return getOrderNum() - another.getOrderNum();
    }

    protected void setIdNum( int idNum){
        this.idNum = idNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

}
