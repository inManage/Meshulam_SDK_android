package il.co.inmanage.meshulam_sdk.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import il.co.inmanage.meshulam_sdk.R;
import il.co.inmanage.meshulam_sdk.application.SdkBaseApplication;
import il.co.inmanage.meshulam_sdk.dialog_fragments.BaseDialogFragment;
import il.co.inmanage.meshulam_sdk.managers.TimeManager;

@SuppressWarnings("unused")

public abstract class SdkBaseFragmentActivity extends FragmentActivity {

    protected static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 10000;
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 10001;
    public static final int KEY_BASE_LOCATION_OPEN_SETTINGS = 10005;
    public static final int ACTION_APPLICATION_DETAILS_SETTINGS = 10002;
    public static final int REQUEST_CAMERA_AND_STORAGE = 10003;
    public static final String KEY_SDK_ACTIVITY_TITLE  = "key_sdk_activity_title";
    public static final String KEY_SDK_ACTIVITY_WAIT  = "key_sdk_activity_wait";

    protected static final String KEY_ACCEPT_CAMERA_AND_STORAGE_MESSAGE = "mobile_camera_permission_text";

    public static final int NO_ANIMATION_FRAGMENT = -1;
    private static final int NO_ACTIVITY_RESOURCE = -1;

    private int requestCounter;
    private long lastTimeFragmentReplacedMilli = 0;
    protected boolean doubleBackToExitPressedOnce = false;

    private BaseDialogFragment dialogToRestore;
    private List<String> permissionsList;
    private TextView tvTitle,tvWait;
    protected ViewGroup mainViewGroup;

    protected SdkBaseFragmentActivity() {

    }

    /**
     * @return boolean that indicates if fragment is last in stack
     */
   /* protected boolean isLastFragment() {
        return fragmentsStack.size() == 1;
    }*/
    public abstract String getActivitySimpleName();

    /**
     * @return returns Activity layout resource
     *//*
    protected int getActivityResource() {
        return NO_ACTIVITY_RESOURCE;
    }
*/
    public boolean isFragmentSupported() {
        return true;
    }

   /* private void inflateFrameLayout() {
        if (getActivityResource() != NO_ACTIVITY_RESOURCE) {
            final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                inflater.inflate(getActivityResource(), containerLayout);
            }
        }
    }*/

    public SdkBaseFragmentActivity activity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentResourceId());
        initLayoutDirectionConfiguration();
        initView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initLayoutDirectionConfiguration();
    }

    /**
     * Setup App local language to support RTL or LTR
     */
    protected void initLayoutDirectionConfiguration() {
        Configuration configuration = new Configuration();
        Locale locale = TimeManager.getLocale();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    /**
     * Updates il.co.inmanage.meshulam_sdk.managers that result arrived
     * Updates current showing fragment that result arrived
     *
     * @param requestCode Specific operation requestCode
     * @param resultCode  Returned result value by activity
     * @param data        Data that returned from activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void initView() {
        initViews();
        initListeners();
    }

    protected void initViews() {
        tvWait = findViewById(R.id.tvWait);
        tvWait.setText(SdkBaseApplication.getApp().getTranslationsManager().getTranslation(KEY_SDK_ACTIVITY_TITLE,R.string.key_sdk_activity_title));
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(SdkBaseApplication.getApp().getTranslationsManager().getTranslation(KEY_SDK_ACTIVITY_WAIT,R.string.key_sdk_activity_wait));
    }

    protected void initListeners() {

    }

    public void hideKeyboard() {
        if (getWindow() != null) {
            hideKeyboard(getWindow().getCurrentFocus());
            hideKeyboard(getWindow().getDecorView().getRootView());
        }
    }

    public void hideKeyboard(View view) {
        if (view == null) {
            return;
        }
        View focusedView = getFocusView(view);
        InputMethodManager inputManager = (InputMethodManager) activity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            if (focusedView != null) {
                inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
            } else {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public void showKeyboard(final View view) {
//        view.requestFocus();
//        InputMethodManager inputMethodManager = (InputMethodManager) app().getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (inputMethodManager != null) {
//            inputMethodManager.toggleSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
//        }
    }

    public void showKeyboardImplicit(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }


    private View getFocusView(View view) {
        if (view instanceof ViewGroup) {
            return getFocusView(((ViewGroup) view).getFocusedChild());
        }
        return view;
    }


    public boolean isFragmentDialogShown() {

        BaseDialogFragment baseDialogFragment = getFragmentDialogShown();

        if (baseDialogFragment != null) {
            Dialog dialog = baseDialogFragment.getDialog();

            return dialog != null && dialog.isShowing();
        }

        return false;
    }

    protected int getContentResourceId() {
        return R.layout.sdk_activity_base;
    }

    public BaseDialogFragment getFragmentDialogShown() {
        Fragment f = getSupportFragmentManager().findFragmentByTag(BaseDialogFragment.BASE_DIALOG_TAG);
        return (BaseDialogFragment) f;
    }


    /**
     * Showing dialog
     *
     * @param baseDialog Dialog instance
     */

    public void launchDialog(BaseDialogFragment baseDialog) {
        launchDialog(baseDialog, null);
    }

    public void launchDialog(BaseDialogFragment baseDialog, final Bundle bundle) {
        if (bundle != null) {
            baseDialog.setArguments(bundle);
        }
        if (SdkBaseApplication.isAppInForeground(this) && getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            baseDialog.show(getSupportFragmentManager(), BaseDialogFragment.BASE_DIALOG_TAG);
        } else {
            dialogToRestore = baseDialog;
        }
    }

    public void disableTouching() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void enableTouching() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    private void onManagerRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        for (BaseManager baseManager : app().getManagers()) {
//            baseManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
    }

    private void checkPermission(List<String> permissionsNeeded, List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(permission);
        }
        permissionsList.add(permission);
    }

    public boolean isPermissionGranted(String permission) {
        int result = ContextCompat.checkSelfPermission(this, permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public boolean requestPermissions(int requestCode, String... permission) {
        List<String> permissionsNeeded = new ArrayList<>();
        for (String s : permission) {
            if (ContextCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(s);
            }
        }
        if (permissionsNeeded.size() > 0) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), requestCode);
        }
        return permissionsNeeded.size() > 0;
    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }

    public List<String> getPermissionsList() {
        return permissionsList;
    }
}
