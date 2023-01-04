package il.co.inmanage.meshulam_sdk.managers;

import android.app.Activity;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import java.util.HashMap;

import il.co.inmanage.meshulam_sdk.application.SdkBaseApplication;
import il.co.inmanage.meshulam_sdk.sdk.SdkManager;
import il.co.inmanage.meshulam_sdk.utils.Translations;
import il.co.inmanage.meshulam_sdk.widgets.DelayClick;

public class TranslationsManager extends BaseManager implements Translations {

    public static  TranslationsManager translationsManager;
    private String requiredString;

    public static TranslationsManager getInstance(Activity baseApplication) {
        if (translationsManager == null) {
            translationsManager = new TranslationsManager(baseApplication);
        }
        return translationsManager;
    }

    protected TranslationsManager(Activity context) {
        super(context);
    }

    @Override
    public void reset() {

    }

    @Override
    public String getTranslation(String key, int resTranslation) {
        HashMap<String, String> translationsMap = SdkManager.getInstance(SdkBaseApplication.getApp().getCurrentActivity()).getInitSdkResponse().getTranslationsMap();
        if (translationsMap != null && !translationsMap.isEmpty()) {
            String value = translationsMap.get(key);
            return value == null ? baseApplication.getResources().getString(resTranslation) : value;
        }
        return "";
    }

    @Override
    public String getTranslationWithReplace(String key, int resTranslation, String... replaces) {
        final String startRegix = "<";
        final String endRegix = ">";
        String value = getTranslation(key, resTranslation);
        for (int i = 0; i < replaces.length; i++) {
            if (value.contains(startRegix) && value.contains(endRegix)) {
                int start = value.indexOf(startRegix);
                int end = value.indexOf(endRegix);

                requiredString = value.substring(start + 1, end);
                if (replaces == null || replaces[i] == null || replaces[i].isEmpty()) {
                    value = value.replace(startRegix + requiredString + endRegix, "");
                } else {
                    value = value.replace(startRegix + requiredString + endRegix, replaces[i]);
                }
            }
        }
        return value;
    }

    @Override
    public CharSequence getSpannableTranslation(String key, int resTranslation) {
        return getSpannableTranslation(key, resTranslation, 0, null);
    }

    @Override
    public CharSequence getSpannableTranslation(String key, int resTranslation, ClickableSpan clickableSpan) {
        return getSpannableTranslation(key, resTranslation, 0, clickableSpan);
    }

    @Override
    public CharSequence getSpannableTranslation(String key, int resTranslation, int color) {
        return getSpannableTranslation(key, resTranslation, color, null);
    }

    @Override
    public CharSequence getSpannableTranslation(String key, int resTranslation, int color, ClickableSpan clickableSpan) {
        return getSpannableTranslation(key, resTranslation, color, "{", "}", clickableSpan);
    }

    @Override
    public CharSequence getSpannableTranslation(String key, int resTranslation, int color, String startRegx, String endRegx, ClickableSpan clickableSpan) {
        String value = getTranslation(key, resTranslation);
        SpannableStringBuilder spanString = new SpannableStringBuilder(value);

        if (value.contains(String.valueOf(startRegx)) && value.contains(String.valueOf(endRegx))) {

            int start = value.indexOf(startRegx);
            int end = value.indexOf(endRegx);

            spanString.setSpan(new UnderlineSpan(), start, end, 0);

            if (clickableSpan != null) {
                spanString.setSpan(new DelayClick(clickableSpan), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (color != 0) {
                spanString.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                spanString.setSpan(new ForegroundColorSpan(Color.BLACK), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            spanString.delete(start, start + 1);
            spanString.delete(end - 1, end);
        }
        return spanString;
    }
}
