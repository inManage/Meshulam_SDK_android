package il.co.inmanage.meshulam_sdk.application;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;

import il.co.inmanage.meshulam_sdk.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import il.co.inmanage.meshulam_sdk.activities.SdkBaseFragmentActivity;
import il.co.inmanage.meshulam_sdk.data.BaseSessionData;
import il.co.inmanage.meshulam_sdk.managers.BaseDialogManager;
import il.co.inmanage.meshulam_sdk.managers.BaseManager;
import il.co.inmanage.meshulam_sdk.managers.SdkRequestManager;
import il.co.inmanage.meshulam_sdk.managers.TranslationsManager;
import il.co.inmanage.meshulam_sdk.server_responses.BaseGeneralDeclarationResponse;
import il.co.inmanage.meshulam_sdk.utils.SharedPreferencesHandler;


public class SdkBaseApplication extends Application {

    private static final String FILENAME = SdkBaseApplication.class.getSimpleName();

    protected static SdkBaseApplication app;
    private SharedPreferencesHandler preferenceHandler;
    private List<BaseManager> managers;
    private BaseSessionData sessionData;
    private Activity currentActivity;

    public SdkBaseApplication() {
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public static SdkBaseApplication getApp() {
        if (app == null){
            app = new SdkBaseApplication();
        }
        return app;
    }

    /**
     * @return Enum value of current Application state
     */
    public static AppState getAppState(Context context) {
        if (isAppClosed(context)) {
            return AppState.CLOSED;
        } else if (isAppInBackground(context)) {
            return AppState.BACKGROUND;
        }
        return AppState.FOREGROUND;
    }

    /**
     * Checks if Application is in foreground
     *
     * @return boolean value that indicates if in foreground or not
     */
    public static boolean isAppInForeground(Context context) {
        String packageName = context.getApplicationContext().getPackageName();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services;
        assert manager != null;
        services = manager.getRunningTasks(1);
        if (services != null && services.isEmpty()) {
            return false;
        }
        ComponentName componentInfo;
        if (services != null) {
            componentInfo = services.get(0).topActivity;
            if (componentInfo != null) {
                return componentInfo.getPackageName().equals(packageName);
            }
        }
        return false;
    }


    public BaseSessionData getSessionData() {
        if (sessionData == null) {
            sessionData = new BaseSessionData();
        }
        return sessionData;
    }

    public BaseGeneralDeclarationResponse getGeneralDeclaration() {
        return getSessionData().getGeneralDeclarationResponse();
    }


    public void setSessionData(BaseSessionData sessionData) {
        this.sessionData = sessionData;
    }


    /**
     * Checks if Application is in background
     *
     * @return boolean value that indicates if App is in background or not
     */
    private static boolean isAppInBackground(Context context) {

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        List<ActivityManager.RunningTaskInfo> services = manager.getRunningTasks(Integer.MAX_VALUE);
        int counter = 0;
        for (ActivityManager.RunningTaskInfo runningTaskInfo : services) {
            if (counter == 0) {
                counter++;
                continue;
            }
            if (runningTaskInfo.topActivity.getPackageName().equalsIgnoreCase(context.getPackageName())) {
                return true;
            }
            counter++;
        }
        return false;
    }

    public List<BaseManager> getManagers() {
        return managers;
    }

    public void resetManagers() {
        for (BaseManager manager : managers) {
            manager.reset();
        }
        initManagers();
    }

    protected void initManagers() {
        managers = new ArrayList<>();
        managers.add(getDialogManager());
        managers.add(getTranslationsManager());
    }

    public BaseDialogManager getDialogManager() {
        return BaseDialogManager.getInstance(currentActivity);
    }

    public TranslationsManager getTranslationsManager() {
        return TranslationsManager.getInstance(currentActivity);
    }

    public static boolean isAppClosed(Context context) {
        return !SdkBaseApplication.isAppInBackground(context) && !SdkBaseApplication.isAppInForeground(context);
    }

   // public abstract Resources getAppResource();

//    public String getApplicationHeader() {
//        return getAppResource().getString(R.string.app_name_header);
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        preferenceHandler = new SharedPreferencesHandler(this);
    }

//    protected abstract String getActivityClassName();

    public boolean writeToDisk(String filename, String key, String value) {
        return getSharedPreferences().writeToDisk(filename, key, value);
    }

    public String readFromDisk(String filename, String key) {
        return getSharedPreferences().readFromDisk(filename, key);
    }

    public void removeFromDisk(String fileName, String key) {
        getSharedPreferences().removeFromDisk(fileName, key);
    }

    public SharedPreferencesHandler getSharedPreferences() {
        return preferenceHandler;
    }

    /**
     * @return List of all permission declared in Manifest
     */
    public List<String> getPermissions() {
        List<String> permissions = new ArrayList<>();
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
            if (info.requestedPermissions != null) {
                permissions.addAll(Arrays.asList(info.requestedPermissions));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return permissions;
    }

    public String getBaseHostUrl() {
        return SdkRequestManager.BASE_URL_HOST;
    }

    public void sendImplicitBroadcast(Context ctxt, Intent i) {
        PackageManager pm = ctxt.getPackageManager();
        List<ResolveInfo> matches = pm.queryBroadcastReceivers(i, 0);
        for (ResolveInfo resolveInfo : matches) {
            Intent explicit = new Intent(i);
            ComponentName cn = new ComponentName(resolveInfo.activityInfo.applicationInfo.packageName, resolveInfo.activityInfo.name);
            explicit.setComponent(cn);
            ctxt.sendBroadcast(explicit);
        }
    }

    /**
     * Enum of Application states
     */
    public enum AppState {
        FOREGROUND,
        BACKGROUND,
        CLOSED
    }

}
