package il.co.inmanage.meshulam_sdk.data;

import il.co.inmanage.meshulam_sdk.utils.LoggingHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class ApiCallLog {

    private static final String GET_REQUEST_METHOD_PARAMETER_PREFIX = "/?";
    private static final String GET_REQUEST_METHOD_ADD_PARAMETER = "&";

    private String url;
    private String apiMethod;
    private HashMap<String, String> params;
    private String jsonResponse;
    private boolean isAfterParse;
    private long onResponseDate = -1;
    private long afterParseDate = -1;
    private long beforeSendRequestDate;

    public ApiCallLog(String url, String apiMethod, HashMap<String, String> params) {
        super();
        this.url = url;
        this.apiMethod = apiMethod;
        this.params = params;
        jsonResponse = "";
    }

    public static String getParamsAsGetRequest(HashMap<String, String> params) {
        if (params == null || params.size() == 0) {
            return "";
        }
        StringBuilder getParams = new StringBuilder(GET_REQUEST_METHOD_PARAMETER_PREFIX);
        int counter = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            getParams.append(key).append("=").append(value);
            if (counter == params.size() - 1) {
                break;
            }
            getParams.append(GET_REQUEST_METHOD_ADD_PARAMETER);
            counter++;
        }

        return getParams.toString();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApiMethod() {
        return apiMethod;
    }

    public void setApiMethod(String apiMethod) {
        this.apiMethod = apiMethod;
    }

    public HashMap<String, String> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public void saveBeforeSendRequest() {
        beforeSendRequestDate = new Date().getTime();
        LoggingHelper.d("ApiCallLog", apiMethod + "_BeforeSendRequest: " + beforeSendRequestDate);
    }

    public void saveOnResponse() {
        onResponseDate = new Date().getTime();
        LoggingHelper.d("ApiCallLog", apiMethod + "_before parse: " + onResponseDate);
    }

    public void saveAfterParse() {
        afterParseDate = new Date().getTime();
        LoggingHelper.d("ApiCallLog", apiMethod + "_after parse: " + afterParseDate);
    }

    @Override
    public String toString() {
        StringBuilder strB = new StringBuilder();
        String newLine = "\r\n";
        strB.append(newLine);
        strB.append("//////////////////start call//////////////////");
        strB.append(newLine);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS", Locale.getDefault());
        String date = sdf.format(new Date().getTime());
        strB.append(apiMethod);
        strB.append(newLine);
        strB.append("print time ").append(date);
        strB.append(newLine);
        strB.append("before send request ").append(sdf.format(beforeSendRequestDate));
        strB.append(newLine);
        strB.append("before parse ").append(sdf.format(onResponseDate));
        strB.append(newLine);
        strB.append("after parse ").append(sdf.format(afterParseDate));
        strB.append(newLine);
        strB.append(url);
        strB.append(newLine);
        strB.append(getGetRequestUrl());
        strB.append(newLine);
        strB.append(jsonResponse);
        strB.append(newLine);
        strB.append("////////////////// end call//////////////////");
        strB.append(newLine);
        strB.append(newLine);
        strB.append(newLine);
        return strB.toString();
    }

    private String getGetRequestUrl() {
        //  return url + GET_REQUEST_METHOD_PARAMETER_PREFIX + getParamsAsGetRequest(params);
        return url + getParamsAsGetRequest(params);
    }

    public void setResponse(String jsonObject) {
        this.jsonResponse = jsonObject;
    }
}