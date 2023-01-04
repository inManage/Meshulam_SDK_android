package il.co.inmanage.meshulam_sdk.managers;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import il.co.inmanage.meshulam_sdk.R;
import il.co.inmanage.meshulam_sdk.activities.MeshulamActivitySdk;
import il.co.inmanage.meshulam_sdk.application.SdkBaseApplication;
import il.co.inmanage.meshulam_sdk.dialog.CustomizableDialog;
import il.co.inmanage.meshulam_sdk.sdk.MeshulamSdk;

public class BaseDialogManager extends BaseManager {

    static final String PROGRESS_BAR = "mProgressBar";
    public static final String KEY_REQUEST_NAME = "request_name";
    public static final String KEY_BASE_REQUEST = "base_request";
    //  static final String KEY_REQUEST_SUCCESS = "request_success";
    public static final String EXTRA_ERROR_MESSAGE_KEY = "errorMSG";
    private static BaseDialogManager dialogManager;
    private static int layoutXml;
    private Activity baseHostApplication;
    private MeshulamActivitySdk activitySdk;
    
    public static final String KEY_RETRY_PAYMENT_GRAY_BTN = "key_retry_payment_gray_btn";
    public static final String KEY_RETRY_PAYMENT_BLUE_BTN = "key_retry_payment_blue_btn";
    public static final String KEY_RETRY_PAYMENT_TITLE = "key_retry_payment_title";
    public static final String KEY_ERROR_DIALOG_BTN = "key_error_dialog_btn";

    public static final String KEY_CANCEL_PAYMENT_GRAY_BTN = "key_retry_payment_gray_btn";
    public static final String KEY_CANCEL_PAYMENT_BLUE_BTN = "key_retry_payment_blue_btn";
    public static final String KEY_CANCEL_PAYMENT_TITLE = "key_retry_payment_title";

    protected BaseDialogManager(Activity baseApplication) {
        super(baseApplication);
        this.baseHostApplication = baseApplication;
    }

    public static BaseDialogManager getInstance(Activity baseApplication) {
        if (dialogManager == null) {
            dialogManager = new BaseDialogManager(baseApplication);
        }
        return dialogManager;
    }

    public void showCancelDialog(CustomizableDialog.OnDialogClickListener onDialogClickListener) {
        Context context = MeshulamActivitySdk.getInstance() != null ? MeshulamActivitySdk.getInstance() : baseApplication;
        CustomizableDialog dialog = new CustomizableDialog(context);
        dialog.setTitle(SdkBaseApplication.getApp().getTranslationsManager().getTranslation(KEY_CANCEL_PAYMENT_TITLE,R.string.key_cancel_payment_title));
        dialog.setBlueBtnText(SdkBaseApplication.getApp().getTranslationsManager().getTranslation(KEY_CANCEL_PAYMENT_BLUE_BTN,R.string.key_cancel_payment_blue_btn));
        dialog.setGreyBtnText(SdkBaseApplication.getApp().getTranslationsManager().getTranslation(KEY_CANCEL_PAYMENT_GRAY_BTN,R.string.key_cancel_payment_gray_btn));
        dialog.setOnDialogClickListener(new CustomizableDialog.OnDialogClickListener() {
            @Override
            public void onClickBlue() {
                dialog.dismiss();

            }

            @Override
            public void onClickGrey() {
                if(onDialogClickListener!=null){
                    onDialogClickListener.onClickGrey();
                }
            }
        });
        dialog.show();
    }

    public void showRetryBitPaymentDialog(CustomizableDialog.OnDialogClickListener onDialogClickListener) {
        Context context = MeshulamActivitySdk.getInstance() != null ? MeshulamActivitySdk.getInstance() : baseApplication;
        CustomizableDialog dialog = new CustomizableDialog(context);
        dialog.setTitle(SdkBaseApplication.getApp().getTranslationsManager().getTranslation(KEY_RETRY_PAYMENT_TITLE,R.string.key_retry_payment_title));
        dialog.setBlueBtnText(SdkBaseApplication.getApp().getTranslationsManager().getTranslation(KEY_RETRY_PAYMENT_BLUE_BTN,R.string.key_retry_payment_blue_btn));
        dialog.setGreyBtnText(SdkBaseApplication.getApp().getTranslationsManager().getTranslation(KEY_RETRY_PAYMENT_GRAY_BTN,R.string.key_retry_payment_gray_btn));
        dialog.setOnDialogClickListener(onDialogClickListener);
        dialog.show();
    }

    public void showErrorDialog(String text) {
        Context context = MeshulamActivitySdk.getInstance() != null ? MeshulamActivitySdk.getInstance() : baseApplication;
        CustomizableDialog dialog = new CustomizableDialog(context);
        dialog.setTitle(text);
        dialog.setBlueBtnText(SdkBaseApplication.getApp().getTranslationsManager().getTranslation(KEY_ERROR_DIALOG_BTN,R.string.key_error_dialog_btn));
        dialog.hideGreyBtn();
        dialog.setOnDialogClickListener(new CustomizableDialog.OnDialogClickListener() {
            @Override
            public void onClickBlue() {
                dialog.dismiss();
                if (MeshulamActivitySdk.getInstance() != null) {
                    MeshulamActivitySdk.getInstance().finish();
                }
            }

            @Override
            public void onClickGrey() {
                dialog.dismiss();
                if (MeshulamActivitySdk.getInstance() != null) {
                    MeshulamActivitySdk.getInstance().finish();
                }
            }
        });
        dialog.show();
    }

    /**
     * Set null to singleton for correct reset operation
     */


    @Override
    public void reset() {
        dialogManager = null;
    }

}