package il.co.inmanage.meshulam_sdk.utils;

import android.text.style.ClickableSpan;

public interface Translations {

    String getTranslation(String key, int resTranslation);

    String getTranslationWithReplace(String key, int resTranslation, String... replaces);

    CharSequence getSpannableTranslation(String key, int resTranslation);

    CharSequence getSpannableTranslation(String key, int resTranslation, ClickableSpan clickableSpan);

    CharSequence getSpannableTranslation(String key, int resTranslation, int color);

    CharSequence getSpannableTranslation(String key, int resTranslation, int color, ClickableSpan clickableSpan);

    CharSequence getSpannableTranslation(String key, int resTranslation, int color, String startRegx, String endRegx, ClickableSpan clickableSpan);

}
