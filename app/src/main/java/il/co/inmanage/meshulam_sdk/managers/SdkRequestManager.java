package il.co.inmanage.meshulam_sdk.managers;

import static com.android.volley.Request.Method.GET;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.apache.http.params.BasicHttpParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;

import il.co.inmanage.meshulam_sdk.R;
import il.co.inmanage.meshulam_sdk.application.SdkBaseApplication;
import il.co.inmanage.meshulam_sdk.data.ApiCallLog;
import il.co.inmanage.meshulam_sdk.data.BaseSessionData;
import il.co.inmanage.meshulam_sdk.interfaces.IWebView;
import il.co.inmanage.meshulam_sdk.interfaces.OnServerRequestDoneListener;
import il.co.inmanage.meshulam_sdk.sdk.MeshulamSdk;
import il.co.inmanage.meshulam_sdk.server_requests.SdkServerRequest;
import il.co.inmanage.meshulam_sdk.server_responses.BaseGeneralDeclarationResponse;
import il.co.inmanage.meshulam_sdk.server_responses.BaseServerRequestResponse;
import il.co.inmanage.meshulam_sdk.utils.LoggingHelper;

public class SdkRequestManager extends BaseManager implements IWebView {

    /**
     * dev
     */
//    public static final String BASE_URL_HOST_PLUS = "https://dev.meshulam.co.il/";
    /**plus*/
//     public static final String BASE_URL_HOST_PLUS = "https://plusecure.meshulam.co.il/";
     public static final String BASE_URL_HOST_PLUS = "https://sandbox.meshulam.co.il/";
    /**
     * live
     */
    public static final String BASE_URL_HOST = "https://secure.meshulam.co.il/";
    public static final String SERVER_VERSION = "1.0" + "/";
    //public static final String BASE_URL_HOST = "https://plusecure.meshulam.co.il/";
    public static final String KEY_ALERT_NO_ACCESS_TO_SERVER = "alert_no_access_to_server";
    static final String FILENAME = SdkRequestManager.class.getSimpleName();
    static final String HOST_URL_KEY = "hostUrl";
    private static final String TOKEN_KEY = "applicationToken";
    private static final String BASE_URL_MIDDLE = "api/light/android/";
    public static final String FULL_BASE_URL = BASE_URL_HOST + BASE_URL_MIDDLE + SERVER_VERSION;
    private static final int DELAY_BETWEEN_REQUESTS = 15000;
    private static final int DEFAULT_TIMEOUT = 25 * TimeManager.SECOND;
    protected static SdkRequestManager sInstance;
    protected Set<SdkServerRequest> sdkServerRequestSet;
    /**
     * to change environment from dev to prod go to "build variants" and change the "active build variant". https://www.screencast.com/t/EN1T1ntW2b
     * <p>
     * if needed to change the url it self for each environment go to build.gradle file for Module: inmanage https://www.screencast.com/t/vYwBX7hLPb
     */

    boolean isDevMode = true;
    private String baseUrl;
    private String baseGetUrl;
    private ProgressDialog progressDialog;
    private RequestQueue mRequestQueue;
    private HashMap<String, SdkServerRequest> baseServerRequestMap;
    private List<ApiCallLog> apiCallsLog;
    private Set<OnLoginErrorRequestListener> onLoginErrorRequestListenersSet;
    private Set<OnDialogErrorClickListener> onDialogErrorClickListenersSet;
    private AbstractHttpClient httpClient;
    private List<OnServerRequestDoneListener> onServerRequestDoneListenerList;
    private int retryCounter = 0;
    private final OnServerRequestDoneListener onServerRequestDoneListener = new OnServerRequestDoneListener() {
        @Override
        public void onSuccess(String requestName, SdkServerRequest sdkServerRequest, BaseServerRequestResponse baseResponse) {
            notifyOnServerRequestDoneListeners(requestName, sdkServerRequest, baseResponse);
            onFinishRequest(sdkServerRequest);
            LoggingHelper.d(sdkServerRequest.getApiCallLog().toString());
            if (apiCallsLog != null && sdkServerRequest != null && sdkServerRequest.getApiCallLog() != null) {
                apiCallsLog.add(sdkServerRequest.getApiCallLog());
            }
           /* if(isOnline()) {
                baseApplication.getLocalStorageManager().saveResponse(baseServerRequest, baseResponse);
            }*/
        }

        private void hideProgressBar(SdkServerRequest sdkServerRequest) {
            if (sdkServerRequest.isShowProgressBar()) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        }

        private void onFinishRequest(SdkServerRequest sdkServerRequest) {
            retryCounter = 0;
            baseServerRequestMap.remove(sdkServerRequest.getMethodName());
            hideProgressBar(sdkServerRequest);
        }

        @Override
        public void onFailure(String requestName, SdkServerRequest sdkServerRequest, BaseServerRequestResponse.ServerRequestFailureResponse serverRequestFailure) {
            onFinishRequest(sdkServerRequest);
            if (sdkServerRequest.isShowErrorMessage()) {
                BaseDialogManager.getInstance(baseApplication).showErrorDialog(serverRequestFailure.getContent());
                if (sdkServerRequestSet != null) {
                    sdkServerRequestSet.clear(); // TODO: clear makes it so only the last failed request will be in the set, decide what to keep
                    sdkServerRequestSet.add(sdkServerRequest);
                }
            }
        }

    };
    private List<String> methodsToSendAsGetRequest;
    private String postUrl;
    private String getUrl;
    private HashMap<String, String> familiarIp;
    private BaseSessionData sessionData;

    protected SdkRequestManager(Activity baseApplication) {
        super(baseApplication);
        this.isDevMode = MeshulamSdk.getInstance(baseApplication).isDevMode();
    }

    public static synchronized SdkRequestManager getInstance(Activity baseApplication) {
        if (sInstance == null) {
            sInstance = new SdkRequestManager(baseApplication);
        }
        return sInstance;
    }

    public void addOnServerRequestDoneListener(OnServerRequestDoneListener onServerRequestDoneListener) {
        onServerRequestDoneListenerList.add(onServerRequestDoneListener);
    }

    public void removeOnServerRequestDoneListener(OnServerRequestDoneListener onServerRequestDoneListener) {
        onServerRequestDoneListenerList.remove(onServerRequestDoneListener);
    }

    /**
     * Set null to singleton for correct reset operation
     */
    @Override
    public void reset() {
        sInstance = null;
    }

    public String getUserAgentName() {
        return "meshulam_sdk" + "/" + SERVER_VERSION + "(" + System.getProperty("http.agent") + ")";
    }

    public void addOnDialogErrorClickListener(OnLoginErrorRequestListener onLoginErrorRequestListener) {
        if (onLoginErrorRequestListener != null) {
            onLoginErrorRequestListenersSet.add(onLoginErrorRequestListener);
        }
    }

    public void removeOnLoginErrorRequestListener(OnLoginErrorRequestListener onLoginErrorRequestListener) {
        if (onLoginErrorRequestListener != null) {
            onLoginErrorRequestListenersSet.remove(onLoginErrorRequestListener);
        }
    }

    public void addOnDialogErrorClickListener(OnDialogErrorClickListener onDialogErrorClickListener) {
        if (onDialogErrorClickListenersSet != null) {
            onDialogErrorClickListenersSet.add(onDialogErrorClickListener);
        }
    }

    public void removeOnDialogErrorClickListener(OnDialogErrorClickListener onDialogErrorClickListener) {
        if (onDialogErrorClickListenersSet != null) {
            onDialogErrorClickListenersSet.remove(onDialogErrorClickListener);
        }
    }

    @Override
    public void init() {
        baseServerRequestMap = new HashMap<>();
        onServerRequestDoneListenerList = new ArrayList<>();
        apiCallsLog = new ArrayList<>();
        onLoginErrorRequestListenersSet = new HashSet<>();
        onDialogErrorClickListenersSet = new HashSet<>();
        sdkServerRequestSet = new HashSet<>();
    }

    public HashMap<String, SdkServerRequest> getBaseServerRequestMap() {
        if (baseServerRequestMap == null) {
            baseServerRequestMap = new HashMap<>();
        }
        return baseServerRequestMap;
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) baseApplication.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private synchronized RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            BasicHttpParams params = new BasicHttpParams();
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            SSLSocketFactory sslSocketFactory = SSLSocketFactory
                    .getSocketFactory();
            sslSocketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);

            schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
            ClientConnectionManager cm = new ThreadSafeClientConnManager(
                    params, schemeRegistry);

            httpClient = new DefaultHttpClient(cm, params);
            // mRequestQueue = Volley.newRequestQueue(BaseApplication.getApp().getApplicationContext());
            mRequestQueue = Volley.newRequestQueue(baseApplication, new HttpClientStack(httpClient));
            setCookie(httpClient);
        }


        return mRequestQueue;
    }

    protected void setCookie(AbstractHttpClient httpClient) {
        org.apache.http.client.CookieStore cs = httpClient.getCookieStore();
        // create a cookie
        cs.addCookie(new BasicClientCookie2("cookie", ""));
    }

    private org.apache.http.client.CookieStore getCookieStore() {
        return httpClient.getCookieStore();
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? FILENAME : tag);
        getRequestQueue().add(req);
    }

    public void addToRequestQueue(SdkServerRequest request) {

        addToRequestQueue(request, BaseDialogManager.PROGRESS_BAR);
    }

    private synchronized void addToRequestQueue(SdkServerRequest request, String tag) {
        tag = tag == null ? "" : tag;
        request.setTag(tag);
        request.setUserAgent(getUserAgentName());
        if (request.getOptionsRequest().isShowProgressDialog()) {
            showRequestView(request, tag);
        }
        request.addOnServerRequestDoneListener(onServerRequestDoneListener);
//        getBaseServerRequestMap().put(request.getMethodName(), request);
        if (request.getMaxSendRetryRequest() == 0) {
            request.setMaxSendRetryRequest(request.getOptionsRequest().isShouldRetry() ? getMaxRetryRequest() : 0);
        }
        request.addRetry();
        if (request.getMethod() != GET && hasApplicationToken()) {
            request.addParam(TOKEN_KEY, getApplicationToken());
        }
        int timeoutRequest = getMethodTimeOut(request.getMethodName());
        request.setRetryPolicy(new DefaultRetryPolicy(timeoutRequest, 0, 0.0F));
        request.getApiCallLog().saveBeforeSendRequest();
        if (!request.getOptionsRequest().isOneRequestInPool() || request.getOptionsRequest().isOneRequestInPool() && !getBaseServerRequestMap().containsKey(request.getMethodName())) {
            getBaseServerRequestMap().put(request.getMethodName(), request);
            getRequestQueue().add(request);
        }
    }

    private int getMethodTimeOut(String methodName) {
        if (sessionData != null && sessionData.getGeneralDeclarationResponse() != null) {
            if (sessionData.getGeneralDeclarationResponse().getExceptionalTimeoutMethodsList().contains(methodName)) {
                return sessionData.getGeneralDeclarationResponse().getExceptionalMethodTimeout();
            }
            return sessionData.getGeneralDeclarationResponse().getMethodTimeout();
        } else {
            return DEFAULT_TIMEOUT;
        }
    }

    private void showRequestView(final SdkServerRequest request, final String finalTag) {
        SdkBaseApplication.AppState appState = SdkBaseApplication.getAppState(baseApplication);
        if (appState == SdkBaseApplication.AppState.FOREGROUND) {
            final String methodName = request.getMethodName();

            baseApplication.runOnUiThread(() -> showMethodNameToast(methodName));
            if (request.isShowProgressBar()) {
                showProgressDialog();
            }
        }
    }

    private void showProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(baseApplication, null, null, true);
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        progressDialog.setContentView(R.layout.sdk_view_progress_bar_dialog);
    }


    private void notifyOnServerRequestDoneListeners(String requestName, SdkServerRequest
            sdkServerRequest, BaseServerRequestResponse baseResponse) {
        if (onServerRequestDoneListenerList != null) {
            for (OnServerRequestDoneListener onServerRequestDoneListener : onServerRequestDoneListenerList) {
                if (onServerRequestDoneListener != null) {
                    onServerRequestDoneListener.onSuccess(requestName, sdkServerRequest, baseResponse);
                }
            }
        }
    }

    public boolean isRequestsShownProgressBarEmpty() {
        for (String methodName : baseServerRequestMap.keySet()) {
            SdkServerRequest sdkServerRequest = baseServerRequestMap.get(methodName);
            if (sdkServerRequest.isShowProgressBar()) {
                return false;
            }

        }
        return true;
    }

    public void sendRetry(String apiMethod, VolleyError volleyError) {
        SdkServerRequest sdkServerRequest = getBaseServerRequestMap().get(apiMethod);
        sendRetry(volleyError, sdkServerRequest);

    }

    public void sendRetry(VolleyError volleyError, SdkServerRequest sdkServerRequest) {
        if (sdkServerRequest != null) {
            boolean retry = sdkServerRequest.isSendRetry();
            if (retry) {
                sendRetryRequest(sdkServerRequest, volleyError instanceof TimeoutError ? DELAY_BETWEEN_REQUESTS : getServerRecall());
            } else {
                sdkServerRequest.notifyFailure();
            }
        }
    }

    private int getServerRecall() {
        BaseGeneralDeclarationResponse generalDeclarationResponse = sessionData.getGeneralDeclarationResponse();
        return generalDeclarationResponse == null ? 5000 : generalDeclarationResponse.getServerRecall();
    }

    private void notifyOnLoginErrorRequest(SdkServerRequest sdkServerRequest) {
        for (OnLoginErrorRequestListener onLoginErrorRequestListener : onLoginErrorRequestListenersSet) {
            onLoginErrorRequestListener.onLoginErrorRequest(sdkServerRequest);
        }
    }


    private void showLoginDialog(String errorMessage) {

    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void cancelPendingRequests(String tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    protected boolean hasApplicationToken() {
        return sessionData != null && sessionData.hasApplicationToken();

    }

    protected String getApplicationToken() {
        return sessionData.getApplicationToken();
    }


    @Override
    public List<Cookie> getCocCookieList() {
        return getCookieStore().getCookies();
    }

    public String getBaseHostUrl() {
        return BASE_URL_HOST;
    }

    private void sendRetryRequest(final SdkServerRequest serverRequest, int delay) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (baseApplication != null) {
                    baseApplication.runOnUiThread(() -> SdkRequestManager.getInstance(baseApplication).addToRequestQueue(serverRequest));
                }
            }
        }, delay);
    }

    public List<ApiCallLog> getApiCallsLog() {
        return apiCallsLog;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = getUrlBaseExtension(postUrl);
    }

    public String getGetUrl() {
        return getUrl;
    }

    public void setGetUrl(String getUrl) {
        this.getUrl = getUrlBaseExtension(getUrl);
    }

    public String getUrlBaseExtension(String url) {
        if (!url.contains(BASE_URL_MIDDLE)) {
            url += BASE_URL_MIDDLE;
        }
        if (!url.contains(SERVER_VERSION)) {
            url += SERVER_VERSION;
        }
        return url;
    }

    public List<String> getMethodsToSendAsGetRequest() {
        if (methodsToSendAsGetRequest == null) {
            methodsToSendAsGetRequest = new ArrayList<>();
        }
        return methodsToSendAsGetRequest;
    }

    public void setMethodsToSendAsGetRequest(List<String> methodsToSendAsGetRequest) {
        this.methodsToSendAsGetRequest = methodsToSendAsGetRequest;
    }

    private int getMaxRetryRequest() {
        if (sessionData == null) {
            return SdkServerRequest.MAX_SEND_RETRY_REQUEST;
        }
        BaseGeneralDeclarationResponse generalDeclarationResponse = sessionData.getGeneralDeclarationResponse();
        if (generalDeclarationResponse == null) {
            return SdkServerRequest.MAX_SEND_RETRY_REQUEST;
        }
        return generalDeclarationResponse.getMethodAttempts();
    }

    private void showMethodNameToast(String methodName) {
        Toast.makeText(baseApplication, methodName, Toast.LENGTH_SHORT).show();
    }

    public HashMap<String, String> getFamiliarIp() {
        return familiarIp;
    }

    public void setFamiliarIp(HashMap<String, String> familiarIp) {
        this.familiarIp = familiarIp;
    }

    public void setSessionData(BaseSessionData sessionData) {
        this.sessionData = sessionData;
    }

    public boolean isDevMode() {
        return isDevMode;
    }

    public interface OnLoginErrorRequestListener {
        void onLoginErrorRequest(SdkServerRequest sdkServerRequest);
    }

    public interface OnDialogErrorClickListener {
        void onDialogErrorClick(int errorId, SdkServerRequest sdkServerRequest);
    }
}
