package il.co.inmanage.meshulam_sdk.utils;

import android.content.DialogInterface.OnClickListener;

public class DialogButton {

    private String text;
    private OnClickListener onClickListener;

    public DialogButton(String text, OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.text = text;
    }
    public DialogButton(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}