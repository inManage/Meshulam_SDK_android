package il.co.inmanage.meshulam_sdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import il.co.inmanage.meshulam_sdk.R;

public class CustomizableDialog {

    private Dialog dialog;
    private Context context;
    private OnDialogClickListener onDialogClickListener;

    private TextView tvTitle,tvBlueButton;
    private RelativeLayout rlButtonBlue;
    private Button btnBlue, btnGrey;
    private ImageView ivBitIcon;

    public CustomizableDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.sdk_dialog_customizable);
        tvTitle = dialog.findViewById(R.id.tvTitle);
        btnBlue = dialog.findViewById(R.id.btnBlue);
        btnGrey = dialog.findViewById(R.id.btnGrey);
        tvBlueButton = dialog.findViewById(R.id.tvBlueButton);
        rlButtonBlue = dialog.findViewById(R.id.rlButtonBlue);
        ivBitIcon = dialog.findViewById(R.id.ivBitIcon);
        initListeners();
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    // If button text is never set, button will not appear
    public void setBlueBtnText(String text) {
        rlButtonBlue.setVisibility(View.VISIBLE);
        tvBlueButton.setText(text);
    }

    // If button text is never set, button will not appear
    public void setGreyBtnText(String text) {
        btnGrey.setVisibility(View.VISIBLE);
        btnGrey.setText(text);
    }

    public void hideGreyBtn() {
        btnGrey.setVisibility(View.GONE);
    }

    public void setShowBitIcon(){
        ivBitIcon.setVisibility(View.VISIBLE);
    }

    public void show() {
        dialog.show();
    }

    public void setOnDialogClickListener(OnDialogClickListener onDialogClickListener) {
        this.onDialogClickListener = onDialogClickListener;
    }

    private void initListeners() {
        btnBlue.setOnClickListener(v -> {
            dismiss();
            if (onDialogClickListener != null)
                onDialogClickListener.onClickBlue();
        });
        btnGrey.setOnClickListener(v -> {
            dismiss();
            if (onDialogClickListener != null)
                onDialogClickListener.onClickGrey();
        });
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public interface OnDialogClickListener {
        void onClickBlue();
        void onClickGrey();
    }
}

