package il.co.inmanage.meshulam_sdk.server_requests;

public class OptionsRequest {

    private boolean showErrorMessage = true;
    private boolean showProgressDialog;
    private boolean showMessage;
    private boolean checkRestartApp = true;
    private boolean shouldRetry = true;
    private boolean oneRequestInPool;


    public OptionsRequest(boolean showProgressDialog, boolean showMessage) {
        this(showProgressDialog, showMessage, true);
    }

    public OptionsRequest(boolean showProgressDialog, boolean showMessage, boolean showErrorMessage) {
        this.showProgressDialog = showProgressDialog;
        this.showMessage = showMessage;
        this.showErrorMessage = showErrorMessage;
    }

    public OptionsRequest(boolean oneRequestInPool) {
        this.oneRequestInPool = oneRequestInPool;
        this.showProgressDialog = true;
        this.showMessage = true;
    }


    public OptionsRequest() {
        this(true, true);
    }

    public boolean isShowProgressDialog() {
        return showProgressDialog;
    }

    public void setShowProgressDialog(boolean showProgressDialog) {
        this.showProgressDialog = showProgressDialog;
    }

    boolean isShowMessage() {
        return showMessage;
    }

    public void setShowMessage(boolean showMessage) {
        this.showMessage = showMessage;
    }

    public boolean isCheckRestartApp() {
        return checkRestartApp;
    }

    public void setCheckRestartApp(boolean checkRestartApp) {
        this.checkRestartApp = checkRestartApp;
    }

    boolean isShowErrorMessage() {
        return showErrorMessage;
    }

    public void setShowErrorMessage(boolean showErrorMessage) {
        this.showErrorMessage = showErrorMessage;
    }

    public boolean isShouldRetry() {
        return shouldRetry;
    }

    public void setShouldRetry(boolean shouldRetry) {
        this.shouldRetry = shouldRetry;
    }

    public boolean isOneRequestInPool() {
        return oneRequestInPool;
    }

    public void setOneRequestInPool(boolean oneRequestInPool) {
        this.oneRequestInPool = oneRequestInPool;
    }
}
