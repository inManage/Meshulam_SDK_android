package il.co.inmanage.meshulam_sdk.widgets;

import android.text.style.ClickableSpan;
import android.view.View;

public class DelayClick extends ClickableSpan implements View.OnClickListener  {

    private ClickableSpan clickableSpan;
    private View.OnClickListener onClickListener;
    private long clickTimeInMili = 0;
    private int delayInMili = 500;

    public DelayClick(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public DelayClick(ClickableSpan clickableSpan) {
        this.clickableSpan = clickableSpan;
    }

    @Override
    public void onClick(View view) {
        if (clickTimeInMili + delayInMili < System.currentTimeMillis()) {
            clickTimeInMili = System.currentTimeMillis();
            if (clickableSpan != null) {
                clickableSpan.onClick(view);
            } else if (onClickListener != null) {
                onClickListener.onClick(view);
            }
        }
    }
}
