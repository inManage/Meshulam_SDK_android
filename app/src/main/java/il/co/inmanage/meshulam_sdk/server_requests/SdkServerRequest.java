package il.co.inmanage.meshulam_sdk.server_requests;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.android.volley.AuthFailureError;
import com.android.volley.BuildConfig;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import il.co.inmanage.meshulam_sdk.application.SdkBaseApplication;
import il.co.inmanage.meshulam_sdk.data.ApiCallLog;
import il.co.inmanage.meshulam_sdk.interfaces.OnFailureServerRequestListener;
import il.co.inmanage.meshulam_sdk.interfaces.OnServerRequestDoneListener;
import il.co.inmanage.meshulam_sdk.managers.SdkRequestManager;
import il.co.inmanage.meshulam_sdk.sdk.MeshulamSdk;
import il.co.inmanage.meshulam_sdk.server_responses.BaseServerRequestResponse;
import il.co.inmanage.meshulam_sdk.server_responses.GeneralFailureResponse;
import il.co.inmanage.meshulam_sdk.utils.LoggingHelper;
import il.co.inmanage.meshulam_sdk.utils.SharedPreferencesHandler;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public abstract class SdkServerRequest extends Request<Object> {

    public static final int TIMEOUT = 45;
    public static final int MAX_SEND_RETRY_REQUEST = 3;
    protected static final String TAG = SdkServerRequest.class.getSimpleName();
    private final ApiCallLog apiCallLog;
    private Set<OnServerRequestDoneListener> onServerRequestDoneListeners;
    private OptionsRequest optionsRequest;
    private int retryCounter;
    private int maxSendRetryRequest;
    private StorageTypeEnum storageTypeEnum;
    private String userAgent;
    private boolean reSendRequest = true;
    private Map<String, String> requestHeaders;
    private Activity activity;

    private SdkServerRequest(String method, OnServerRequestDoneListener onServerRequestDoneListeners, Activity activity) {
        this(method, null, new OptionsRequest(), onServerRequestDoneListeners, activity);
    }

    public SdkServerRequest(String method, OptionsRequest optionsRequest, OnServerRequestDoneListener onServerRequestDoneListeners, Activity activity) {
        this(method, null, optionsRequest, onServerRequestDoneListeners, activity);
    }

    private SdkServerRequest(String method, HashMap<String, String> params, Activity activity) {
        this(method, params, new OptionsRequest(),null,  activity);
    }

    public SdkServerRequest(String method, HashMap<String, String> params, OnServerRequestDoneListener onServerRequestDoneListener, Activity activity) {
        this(method, params, new OptionsRequest(), onServerRequestDoneListener,activity);
    }

    public SdkServerRequest(String method, HashMap<String, String> params, OptionsRequest optionsRequest, OnServerRequestDoneListener onServerRequestDoneListener, Activity activity) {
        this(getRequestMethodType(method,activity), method, params, optionsRequest, onServerRequestDoneListener, activity);
    }


    public SdkServerRequest(int methodType, String method, HashMap<String, String> params, OptionsRequest optionsRequest, OnServerRequestDoneListener onServerRequestDoneListener, Activity activity) {
        this(methodType, getUrl(method, methodType,activity, MeshulamSdk.getInstance(activity).isDevMode()), method, params, optionsRequest, onServerRequestDoneListener,activity );
    }

    public SdkServerRequest(int methodType, String url, String method, HashMap<String, String> params, OptionsRequest optionsRequest, OnServerRequestDoneListener onServerRequestDoneListener, Activity activity) {
        super(methodType, url + (methodType == Method.GET ? ApiCallLog.getParamsAsGetRequest(getFullParams(params,activity)) : ""), getErrorListener(method));
        this.activity = activity;
        this.optionsRequest = optionsRequest;
        this.apiCallLog = new ApiCallLog(getUrl(), method, params);
        addOnServerRequestDoneListener(onServerRequestDoneListener);
    }

    private static int getRequestMethodType(String methodName, Activity activity) {
        return getBaseRequestManager(activity).getMethodsToSendAsGetRequest().contains(methodName) ? Method.GET : Method.POST;
    }

    static String getUrl(String methodName, int methodType, Activity activity, boolean isDev) {
        String url = "";
        if (methodType == Method.GET) {
            url = getBaseRequestManager(activity).getGetUrl();
        } else {
            url = getBaseRequestManager(activity).getPostUrl();
        }
        if (url == null || url.isEmpty()) {
            url = getBaseRequestManager(activity).getUrlBaseExtension(isDev ? SdkRequestManager.BASE_URL_HOST_PLUS : SdkRequestManager.BASE_URL_HOST);
        }
        return url + methodName;
    }


    protected static HashMap<String, String> getFullParams(HashMap<String, String> params, Activity activity) {
        if (params == null) {
            params = new HashMap<>();
        }
        if (getBaseRequestManager(activity).getFamiliarIp() != null && !getBaseRequestManager(activity).getFamiliarIp().isEmpty()) {
            params.putAll(getBaseRequestManager(activity).getFamiliarIp());
        }
        return params;
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
        getBaseRequestManager(activity).sendRetry(error, this);
    }

    private static Response.ErrorListener getErrorListener(final String apiMethod) {
        return volleyError -> {
            LoggingHelper.d("volleyError:" + volleyError.getMessage() + " (" + apiMethod + ")");
        };
    }

    protected String getTemplateResponse() {
        return "";
    }

    protected boolean shouldUseTemplateResponse() {
        return false;
    }

    protected abstract BaseServerRequestResponse buildResponse(JSONObject jsonObject);

    protected abstract Type getType();

    public BaseServerRequestResponse buildResponse(SharedPreferencesHandler sharedPreferencesHandler) {
        return new Gson().fromJson(sharedPreferencesHandler.readFromDisk(BaseServerRequestResponse.FILE_NAME, getMethodName()), getType());
    }

    // private static Handler handler = new Handler();

    private Response.Listener<Object> responseListener() {
        return object -> {
            LoggingHelper.d("json response: " + object.toString());
            boolean isRunInBackgroundThread = true;
            JSONObject jsonObject = (JSONObject) object;
            apiCallLog.saveOnResponse();
            if (isRunInBackgroundThread) {
                new Thread(new BuildResponseTask(jsonObject)).start();
            } else {
                notifyResponse(onResponseData(jsonObject));
            }
        };
    }

    private BaseServerRequestResponse onResponseData(JSONObject jsonObject) {
        apiCallLog.setResponse(jsonObject.toString());
        BaseServerRequestResponse baseServerRequestResponse = buildResponse(jsonObject);
        apiCallLog.saveAfterParse();
        return baseServerRequestResponse;
    }

    private void notifyResponse(BaseServerRequestResponse baseServerRequestResponse) {
        if (baseServerRequestResponse.isSuccess()) {
            notifySuccess(baseServerRequestResponse);
        } else {
            notifyFailure(baseServerRequestResponse.getServerRequestFailureResponse());
        }
    }

    private void notifySuccess(BaseServerRequestResponse baseServerRequestResponse) {
        for (OnServerRequestDoneListener onServerRequestDoneListener : onServerRequestDoneListeners) {
            onServerRequestDoneListener.onSuccess(apiCallLog.getApiMethod(), SdkServerRequest.this, baseServerRequestResponse);
        }
    }

    private void notifyFailure(BaseServerRequestResponse.ServerRequestFailureResponse serverRequestFailureResponse) {
        for (OnServerRequestDoneListener onServerRequestDoneListener : onServerRequestDoneListeners) {
            onServerRequestDoneListener.onFailure(apiCallLog.getApiMethod(), SdkServerRequest.this, serverRequestFailureResponse);
        }
    }

    public void notifyFailure() {
        BaseServerRequestResponse.ServerRequestFailureResponse serverRequestFailureResponse = createGeneralFailureRequest();
        notifyFailure(serverRequestFailureResponse);
    }

    public void addOnServerRequestDoneListener(OnServerRequestDoneListener onServerRequestDoneListener) {
        if (onServerRequestDoneListeners == null) {
            this.onServerRequestDoneListeners = new HashSet<>();
        }
        if (onServerRequestDoneListener != null) {
            this.onServerRequestDoneListeners.add(onServerRequestDoneListener);
        }
    }

    public void removeOnServerRequestDoneListener(OnServerRequestDoneListener onServerRequestDoneListener) {
        this.onServerRequestDoneListeners.remove(onServerRequestDoneListener);
    }

    public Response<Object> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString;
            JSONObject jsonObject;
            if (BuildConfig.DEBUG && shouldUseTemplateResponse()) {
                jsonString = getTemplateResponse();
            } else {
                jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            }
            Object json = new JSONTokener(jsonString).nextValue();
            if (json instanceof JSONObject) {
                jsonObject = new JSONObject(jsonString);
                if (jsonObject.isNull("status")) {
                    jsonObject.put("status", 1);
                }
            } else {
                JSONArray jsonArray = new JSONArray(jsonString);
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("status", 1);
                jsonObject1.put("com/example/utils/data", jsonArray);
                jsonObject = jsonObject1;

            }
            LoggingHelper.d("jsonObject response:" + jsonObject.toString());
            return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));

        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(Object response) {
        LoggingHelper.d("json response: " + response.toString());
        responseListener().onResponse(response);
    }


    public HashMap<String, String> getParams() {
        return apiCallLog.getParams();
    }

    public String getMethodName() {
        return apiCallLog.getApiMethod();
    }

    private BaseServerRequestResponse.ServerRequestFailureResponse createGeneralFailureRequest() {
        return new GeneralFailureResponse().createGeneralFailureRequest();
    }

    public void addRetry() {
        retryCounter++;
    }

    public void resetRetry() {
        retryCounter = 0;
    }

    public boolean isSendRetry() {
        if (maxSendRetryRequest == 0) {
            maxSendRetryRequest = MAX_SEND_RETRY_REQUEST;
        }
        return retryCounter <= maxSendRetryRequest;
    }

    public void setMaxSendRetryRequest(int max) {
        this.maxSendRetryRequest = max;
    }

    public int getMaxSendRetryRequest() {
        return maxSendRetryRequest;
    }

    public ApiCallLog getApiCallLog() {
        return apiCallLog;
    }

    public boolean isShowProgressBar() {
        return optionsRequest.isShowProgressDialog();
    }

    public boolean isShowMessage() {
        return optionsRequest.isShowMessage();
    }

    public boolean isShowErrorMessage() {
        return optionsRequest.isShowErrorMessage();
    }

    public static SdkRequestManager getBaseRequestManager(Activity activity){
        return SdkRequestManager.getInstance(activity);
    }

    public StorageTypeEnum getStorageTypeEnum() {
        if (storageTypeEnum == null) {
            storageTypeEnum = StorageTypeEnum.SHARED_PREFERENCES;
        }
        return storageTypeEnum;
    }

    void setStorageTypeEnum(StorageTypeEnum storageTypeEnum) {
        this.storageTypeEnum = storageTypeEnum;
    }

    public OptionsRequest getOptionsRequest() {
        return optionsRequest;
    }

    public void setOptionsRequest(OptionsRequest optionsRequest) {
        this.optionsRequest = optionsRequest;
    }

    @Override
    public final Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();
        if (getMethod() == Method.GET) {
            return headers;
        }
        if (headers == null || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<>();
        }
        final String UserAgentKey = "User-agent";
        headers.put(UserAgentKey, userAgent);
        headers.putAll(getRequestHeaders());
        return headers;
    }

    protected Map<String, String> getRequestHeaders() {
        if (requestHeaders == null) {
            requestHeaders = new HashMap<>();
        }
        return requestHeaders;
    }

    @Override
    public Cache.Entry getCacheEntry() {
        return getMethod() == Method.GET ? null : super.getCacheEntry();
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void addParam(String key, String value) {
        getParams().put(key, value);
    }

    public enum StorageTypeEnum {
        LOCAL_SQL,
        SHARED_PREFERENCES,
        COMPLEX
    }

    private class BuildResponseTask implements Runnable {

        private static final int RESPONSE_DATA_PARSE_FINISHED_CODE = 1;
        private final JSONObject jsonObject;
        private Handler requestHandler;

        BuildResponseTask(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
            initRequestHandler();
        }

        @Override
        public void run() {
            final BaseServerRequestResponse baseServerRequestResponse = onResponseData(jsonObject);
            Message requestMessage = requestHandler.obtainMessage(RESPONSE_DATA_PARSE_FINISHED_CODE, baseServerRequestResponse);
            requestMessage.sendToTarget();
        }

        private void initRequestHandler() {
            requestHandler = new Handler(Looper.getMainLooper()) {

                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case RESPONSE_DATA_PARSE_FINISHED_CODE:
                            BaseServerRequestResponse baseServerRequestResponse = (BaseServerRequestResponse) msg.obj;
                            notifyResponse(baseServerRequestResponse);
                            break;
                    }
                }
            };
        }
    }

    public static void notifySuccessRequest(SdkServerRequest sdkServerRequest, BaseServerRequestResponse baseServerRequestResponse, OnServerRequestDoneListener onServerRequestDoneListener) {
        if (onServerRequestDoneListener != null) {
            onServerRequestDoneListener.onSuccess(sdkServerRequest.getMethodName(), sdkServerRequest, baseServerRequestResponse);
        }
    }

    public static void notifyFailureRequest(SdkServerRequest sdkServerRequest, BaseServerRequestResponse.ServerRequestFailureResponse serverRequestFailure, OnFailureServerRequestListener onServerRequestFailureListener) {
        if (onServerRequestFailureListener != null) {
            onServerRequestFailureListener.onFailure(sdkServerRequest.getMethodName(), sdkServerRequest, serverRequestFailure);
        }
    }

    public boolean isReSendRequest() {
        return reSendRequest;
    }

    public void setReSendRequest(boolean reSendRequest) {
        this.reSendRequest = reSendRequest;
    }

    public int getRetryCounter() {
        return retryCounter;
    }


}
