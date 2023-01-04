package il.co.inmanage.meshulam_sdk.dialog_fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.view.ViewCompat;

import java.util.Objects;

import il.co.inmanage.meshulam_sdk.activities.SdkBaseFragmentActivity;
import il.co.inmanage.meshulam_sdk.application.SdkBaseApplication;
import il.co.inmanage.meshulam_sdk.utils.LoggingHelper;


public abstract class BaseDialogFragment extends AppCompatDialogFragment {

    public static final String BASE_DIALOG_TAG = "baseDialogTag";
    private OnCancelledListener onCancelledListener;
    private OnOkListener onOkListener;
    private OnBackListener onBackListener;
    private OnDismissListener onDismissListener;
    private boolean canceledOnTouchOutside = false;
    private boolean isDialogValid = true;
    private String btnAction;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AppCompatDialog dialog = new AppCompatDialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                if (isCancelable()) {
                    notifyBackListener();
                    notifyOnCancelledListener();
                }
            }
        };
        if (!isShowTitle() || getTitle() == null) {
            Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        } else {
            dialog.setTitle(getTitle());
        }
        dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        //set rtl on Samsung s5 after minimizing screen & change orientation:
        dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        // dialog.getWindow().getAttributes().windowAnimations = R.style.SmallDialogAnimation;
        return dialog;
    }

    protected void notifyBackListener() {
        if (onBackListener != null) {
            onBackListener.onBackClick(this);
        }
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        if(onCancelledListener!=null){
            onCancelledListener.onCancelledClick(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ViewCompat.setLayoutDirection(getDialog().getWindow().getDecorView(), ViewCompat.LAYOUT_DIRECTION_RTL);
        //private ProgressDialog progressBar;
        View view = inflater.inflate(getContentResource(), null);
        loadData();
        if (getArguments() != null) {
            loadBundleData(getArguments());
        }
        initViews(view);
        initTexts();
        initListeners();
        initDialogButtonsListener();
        initAdapters();
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LoggingHelper.entering();
    }

    @Override
    public void dismiss() {
        if (getActivity() != null) {
            ((SdkBaseFragmentActivity) getActivity()).hideKeyboard(getView());
            notifyOnDismissListener();
            super.dismiss();
        }
    }

    protected void notifyOnDismissListener() {
        if (onDismissListener != null) {
            onDismissListener.onDismissListener(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ViewCompat.setLayoutDirection(Objects.requireNonNull(getDialog().getWindow()).getDecorView(), ViewCompat.LAYOUT_DIRECTION_RTL);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initDialogButtonsListener() {
        if (getCancelView() != null || getOkView() != null) {
            View.OnClickListener onClickListener = v -> {
                if (getCancelView() != null && v.equals(getCancelView())) {
                    notifyOnCancelledListener();
                } else if (getOkView() != null && v.equals(getOkView())) {
                    notifyOnOkListener();
                }
            };
            if (getCancelView() != null) {
                getCancelView().setOnClickListener(onClickListener);

            }
            if (getOkView() != null) {
                getOkView().setOnClickListener(onClickListener);
            }
        }
    }

    /*public void showProgressDialog() {
        View progressView = app().getDialogManager().createProgressBar(app().getCurrentActivity(), PROGRESS_BAR);
        progressView.setTag(PROGRESS_BAR);
        ((ViewGroup) view).addView(progressView);
        ViewGroup.LayoutParams params = progressView.getLayoutParams();
        if (params instanceof RelativeLayout.LayoutParams) {
            ((RelativeLayout.LayoutParams) params).addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        } else if (params instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) params).gravity = Gravity.CENTER;
        }
        progressView.setLayoutParams(params);
    }

    public void hideProgressBar() {
        View progressView = view.findViewWithTag(PROGRESS_BAR);
        ((ViewGroup) view).removeView(progressView);
    }*/

    public void notifyOnCancelledListener() {
        dismiss();
        if (onCancelledListener != null) {
            onCancelledListener.onCancelledClick(this);
        }
    }

    private void notifyOnOkListener() {
        if (isDialogValid()) {
            dismiss();
        }
        if (onOkListener != null) {
            onOkListener.onOkClick(this);
        }
    }

    protected View getOkView() {
        return null;
    }

    protected View getCancelView() {
        return null;
    }

    protected void loadBundleData(Bundle bundle) {

    }

    private boolean isShowTitle() {
        return false;
    }

    private String getTitle() {
        return null;
    }

    private boolean isDialogValid() {
        return isDialogValid;
    }

    void setDialogValid(boolean dialogValid) {
        isDialogValid = dialogValid;
    }

    protected abstract void initViews(View view);

    protected abstract void initTexts();

    protected abstract void initListeners();

    protected abstract int getContentResource();

    /**
     * only when need to load il.co.inmanage.meshulam_sdk.data from server
     */
    private void loadData() {

    }

    private void initAdapters() {

    }

    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        this.canceledOnTouchOutside = canceledOnTouchOutside;
    }

    public void setOnCanceledListener(OnCancelledListener onCancelledListener) {
        this.onCancelledListener = onCancelledListener;
    }

    public void setOnOkListener(OnOkListener onOkListener) {
        this.onOkListener = onOkListener;
    }

    public void setOnBackListener(OnBackListener onBackListener) {
        this.onBackListener = onBackListener;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    protected SdkBaseFragmentActivity activity() {
        return (SdkBaseFragmentActivity) getActivity();
    }

    protected SdkBaseApplication app() {
        return SdkBaseApplication.getApp();
    }

    public Bundle getBundleData() {
        return new Bundle();
    }

    @Override
    public void setCancelable(boolean cancelable) {
        super.setCancelable(cancelable);
    }

    public String getBtnAction() {
        return btnAction;
    }

    public void setBtnAction(String deeplink) {
        btnAction = deeplink;
    }

    public interface OnCancelledListener {
        void onCancelledClick(BaseDialogFragment baseDialog);
    }

    public interface OnOkListener {
        void onOkClick(BaseDialogFragment baseDialog);
    }

    public interface OnBackListener {
        void onBackClick(BaseDialogFragment baseDialog);
    }

    public interface OnDismissListener {
        void onDismissListener(BaseDialogFragment baseDialog);
    }
}

