package il.co.inmanage.meshulam_sdk.managers;

import android.app.Activity;
import android.content.Context;

import il.co.inmanage.meshulam_sdk.application.SdkBaseApplication;
import il.co.inmanage.meshulam_sdk.server_request_data.Language;
import il.co.inmanage.meshulam_sdk.server_request_data.TimeData;
import il.co.inmanage.meshulam_sdk.utils.LoggingHelper;
import il.co.inmanage.meshulam_sdk.utils.NumberUtils;
import il.co.inmanage.meshulam_sdk.utils.SharedPreferencesHandler;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


@SuppressWarnings("unused")
public class TimeManager extends BaseManager {

    public static final String DATE_FORMAT = "dd/MM/yy";
    public static final int NO_DAY = -1;

    public static final int SECOND = 1000;
    public static final int MINTUE = SECOND * 60;
    public static final String DEFAULT_TIME_ZONE = "Asia/Jerusalem";
    public static final String DEFAULT_LOCALE = "he";
    private static final int HOUR = MINTUE * 60;
    public static final int DAY = HOUR * 24;
    private static final String FILE_NAME = TimeManager.class.getSimpleName();
    private static final int SORT_ORDER = 16;
    private static final String OFFEST_TIME_IN_MILI = "offestTimeInMili";
    private static final String TIME_ZONE_ID = "timeZoneId";
    private static final String LANGUAGE = "language";
    private static final Type LANGUAGE_TYPE = new TypeToken<Language>() {
    }.getType();
    private static Locale locale = new Locale("iw", "IL");
    private static TimeManager timeManager;
    private static long offestServerTimeInMili;
    private static String timeZone;
    private long serverTimeInMili;
    private Language language;

    protected TimeManager(Activity baseApplication) {
        super(baseApplication);
    }

    public static TimeManager getInstance(Activity baseApplication) {
        if (timeManager == null) {
            timeManager = new TimeManager(baseApplication);
        }
        return timeManager;
    }

    public static TimeZone getTimeZone(Context context) {
        if (timeZone == null || timeZone.isEmpty()) {
            SdkBaseApplication sdkBaseApplication = (SdkBaseApplication) context.getApplicationContext();
            timeZone = sdkBaseApplication.readFromDisk(FILE_NAME, TIME_ZONE_ID);
            if (timeZone == null) {
                timeZone = DEFAULT_TIME_ZONE;
            }
        }
        return TimeZone.getTimeZone(timeZone);
    }

    public static String getTimeFormat4Digit(long timeInMilis) {
        Date date = new Date(timeInMilis);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", locale);
//        sdf.setTimeZone(getTimeZone());
        return sdf.format(date);
    }

    public static String getDay(long timeInMilliseconds) {
        return getDay(timeInMilliseconds, locale);
    }

    private static String getDay(long timeInMilliseconds, Locale locale) {
        return new SimpleDateFormat("EEEE", locale).format(timeInMilliseconds);
    }

    public static String getFormattedDate(long timeInMilliseconds, String pattern) {
        return getFormattedDate(timeInMilliseconds, pattern, locale);
    }

    private static String getFormattedDate(long timeInMilliseconds, String pattern, Locale locale) {
        return new SimpleDateFormat(pattern, locale).format(timeInMilliseconds);
    }

    private static String getHourOrMiuteTimeString(long timeInMilli, String hours, String mintues) {
        if (timeInMilli > HOUR) {
            return timeInMilli / TimeManager.HOUR + " " + hours;
        } else {
            return timeInMilli / TimeManager.MINTUE + " " + mintues;
        }
    }

    public static String getHourOrMinuteTimeString(long timeInMilli) {
        if (timeInMilli > HOUR) {
            return timeInMilli / TimeManager.HOUR + " ";
        } else {
            return timeInMilli / TimeManager.MINTUE + " ";
        }
    }

    public static String getMonth(int month) {
        return DateFormatSymbols.getInstance(locale).getMonths()[month - 1];
    }

    public static long getCurrentTimeWithOffestInMili(Context context) {
        return getCurrentTimeWithOffestInMili(context, locale);
    }

    static long getCurrentTimeWithOffestInMili(Context context, Locale locale) {
        if (offestServerTimeInMili == 0) {
            SdkBaseApplication sdkBaseApplication = (SdkBaseApplication) context.getApplicationContext();
            offestServerTimeInMili = NumberUtils.getLongFromString(sdkBaseApplication.readFromDisk(FILE_NAME, OFFEST_TIME_IN_MILI));
        }
        Calendar cal = Calendar.getInstance(getTimeZone(context), locale);
        return cal.getTimeInMillis() - offestServerTimeInMili;
    }

    public static Locale getLocale() {
        return locale != null ? locale : new Locale("iw", "IL");
    }

    /**
     * Set null to singleton for correct reset operation
     */
    @Override
    public void reset() {
        timeManager = null;
    }

    public void setTimeData(TimeData timeData) {
        this.serverTimeInMili = timeData.getServerTimeInMili();
        timeZone = timeData.getTimeZone();
        List<Language> languageList = timeData.getLanguageList();
        for (Language language : languageList) {
            if (language.isActive()) {
                locale = new Locale(language.getTitle());
                calculateOffestTime();
                setLanguage(language);
                return;
            }
            saveTimeZone();
            LoggingHelper.d("serverTimeInMili:" + serverTimeInMili);
            LoggingHelper.d("timeZone:" + timeZone);
            LoggingHelper.d("offestServerTimeInMili:" + offestServerTimeInMili);
            LoggingHelper.d("locate:" + locale);
        }
    }

    private void calculateOffestTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timeZone), locale);
        offestServerTimeInMili = cal.getTimeInMillis() - serverTimeInMili;
    }

    private void saveTimeZone() {
        new SharedPreferencesHandler(baseApplication).writeToDisk(FILE_NAME, TIME_ZONE_ID, timeZone);
    }

    private TimeZone getTimeZone() {
        return getTimeZone(baseApplication);
    }

    public int getDayInWeek(long timeInMilis) {
        Date date = new Date(timeInMilis);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public String getTime(long timeInMilis) {
        Date date = new Date(timeInMilis);
        SimpleDateFormat sdf = new SimpleDateFormat("H:mm", locale);
        sdf.setTimeZone(getTimeZone());
        return sdf.format(date);
    }

    public String getTimeFormat6Digit(long timeInMilis) {
        Date date = new Date(timeInMilis);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", locale);
        sdf.setTimeZone(getTimeZone());
        return sdf.format(date);
    }

    public String getTimeDate(long timeInMilis) {
        Date date = new Date(timeInMilis);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", locale);
        sdf.setTimeZone(getTimeZone());
        return sdf.format(date);
    }

    public String getFullTimeDate(long timeInMilis) {
        Date date = new Date(timeInMilis);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", locale);
        sdf.setTimeZone(getTimeZone());
        return sdf.format(date);
    }

    public String getDate(long timeInMilis) {
        Date date = new Date(timeInMilis);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", locale);
        sdf.setTimeZone(getTimeZone());
        return sdf.format(date);
    }

    public String getDateWithDayAndMonth(long timeInMilis) {
        Date date = new Date(timeInMilis);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM", locale);
        sdf.setTimeZone(getTimeZone());
        return sdf.format(date);
    }

    public String getDateByFormat(long timeInMilis, String format) {
        Date date = new Date(timeInMilis);
        SimpleDateFormat sdf = new SimpleDateFormat(format, locale);
        sdf.setTimeZone(getTimeZone());
        return sdf.format(date);
    }

    public Date getDateFrom4DigitString(String fourDigits, long currentTime) {
        return getOpenOrCloseTSFrom4DigitString(fourDigits, currentTime, false);
    }

    private Date getOpenOrCloseTSFrom4DigitString(String fourDigits, long currentTime, boolean shouldCheckCloseHoure) {
        if (fourDigits.isEmpty() || fourDigits.length() < 5) {
            return null;
        }

        String hourString = fourDigits.substring(0, 2);
        String minutesString = fourDigits.substring(3, 5);
        int hour = NumberUtils.getNumberFromStringInteger(hourString);
        int mintues = NumberUtils.getNumberFromStringInteger(minutesString);
        boolean isPM = hour > 12 || hour == 0;
        Calendar c = Calendar.getInstance(getTimeZone(), locale);
        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        c.setTimeInMillis(currentTime);
        int currentHour = c.get(Calendar.HOUR_OF_DAY);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), dayOfMonth, hour, mintues);
        if (shouldCheckCloseHoure && currentHour < 6) c.add(Calendar.DATE, 1);
        return c.getTime();
    }

    public long stringHoureToTS(String fourDigits, Context context) {
        if (fourDigits.isEmpty() || fourDigits.length() < 4) {
            return 0;
        }
        String hourString = fourDigits.substring(0, 2);
        String minutesString = fourDigits.substring(0, 2);
        int hour = NumberUtils.getNumberFromStringInteger(hourString);
        int mintues = NumberUtils.getNumberFromStringInteger(minutesString);
        Calendar c = Calendar.getInstance(getTimeZone(), locale);
        c.setTimeInMillis(getCurrentTimeWithOffestInMili(context, locale));
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, mintues);
        return c.getTime().getTime();
    }

    public String getTimeString(long time, String days, String hours, String mintues) {
        if (time > DAY) {
            return time / TimeManager.DAY + " " + days;
        }
        return getHourOrMiuteTimeString(time, hours, mintues);
    }

    public String getTimeString(long time, long untilValidTime, String days, String hours, String minutes) {
        if (time > DAY) {
            Calendar maxDate = Calendar.getInstance();
            maxDate.add(Calendar.MONTH, 3);
            if (untilValidTime <= maxDate.getTime().getTime()) {
                return time / TimeManager.DAY + " " + days;
            } else {
                return "";
            }
        }
        return getHourOrMinuteTimeString(time);
    }

    public void setOffestWithServerTime(long serverTimeInMili) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timeZone), locale);
        offestServerTimeInMili = cal.getTimeInMillis() - serverTimeInMili;
        new SharedPreferencesHandler(baseApplication).writeToDisk(FILE_NAME, OFFEST_TIME_IN_MILI, offestServerTimeInMili + "");
    }

    private Language getLanguage() {
        if (language == null) {
            try {
                String languageJsonAsString = new SharedPreferencesHandler(baseApplication).readFromDisk(FILE_NAME, LANGUAGE);
                language = (Language) new Language().createResponse(new JSONObject(languageJsonAsString));
            } catch (Exception e) {
                return null;
            }
        }
        return language;
    }

    private void setLanguage(Language language) {
        this.language = language;
        new SharedPreferencesHandler(baseApplication).writeToDisk(FILE_NAME, LANGUAGE, language.getLanguageAsJsonString());
    }

/*    public String getLocaleLanguage() {
        Language langauge = getLanguage();
        return langauge == null ? DEFAULT_LOCALE_AS_STRING : langauge.getTitle();
    }

    public String getLangId() {
        Language langauge = getLanguage();
        return langauge == null ? DEFAULT_LANG_ID : langauge.getId();
    }*/

    public boolean isRtl() {
        Language language = getLanguage();
        return language == null || language.getDirection() == Language.LanguageDirectionEnum.RTL;
    }

    public void setServerTimeInMili(long serverTimeInMili) {
        this.serverTimeInMili = serverTimeInMili;
    }

    public long getServerTimeInMili() {
        return serverTimeInMili;
    }
}
