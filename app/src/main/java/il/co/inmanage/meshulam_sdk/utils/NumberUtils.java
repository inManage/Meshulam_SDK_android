package il.co.inmanage.meshulam_sdk.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.EditText;


import il.co.inmanage.meshulam_sdk.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;



public class NumberUtils {

    private static final int _MIN_NUMBER_FOR_COMMAS = 1000;

    public static Integer getIntegerFromString(String string) {
        try {
            String numberAsString = string.replace(",", "");
            return Integer.parseInt(numberAsString);
        } catch (Exception e) {
            return -1;
        }
    }

    public static String[] getStringArrayNumber(double number) {
        try {
            String string = getPriceFormatter().format(number);
            String numberAsString = string.replace(",", ".");
            return StringUtils.split(
                    numberAsString, ".");
        } catch (Exception e) {
            return new String[2];
        }
    }

    public static Integer getNumberFromStringInteger(String string) {
        try {
            return Integer.parseInt(string.replaceAll("\\D+", ""));
        } catch (Exception e) {
            return -1;
        }
    }

    public static Float getFloatFromString(String string) {
        try {
            String numberAsString = string.replace(",", "");
            return Float.parseFloat(numberAsString);
        } catch (Exception e) {
            return (float) 0;
        }
    }

    public static double getDoubleFromString(String string) {
        try {
            String numberAsString = string.replace(",", "");
            return Double.parseDouble(numberAsString);
        } catch (Exception e) {
            return (float) 0;
        }
    }

    public static long getLongFromString(String string) {
        try {
            return Long.parseLong(string);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getStringWithReplace(String key, String... replaces) {
        final String startRegix = "{";
        final String endRegix = "}";
        String value = key;
        for (String replace : replaces) {
            if (value.contains(startRegix) && value.contains(endRegix)) {
                int start = value.indexOf(startRegix);
                int end = value.indexOf(endRegix);
                String requiredString = value.substring(start + 1, end);
                if (replace.isEmpty()) {
                    value = value.replace(startRegix + requiredString + endRegix, requiredString);
                } else {
                    value = value.replace(startRegix + requiredString + endRegix, replace);
                }
            }
        }
        return value;
    }

    @SuppressLint("DefaultLocale")
    public static String formatToDecimal(double d) {
        if (d == (int) d) {
            return getWithCommas(String.format("%d", (int) d));
        } else {
            float number = Math.round(d * 2) / 2f;
            if (number == (int) number) {
                return getWithCommas(String.format("%d", (int) number));
            }
            return String.format("%s", number);
        }
    }

    private static String getWithCommasAndDecimal(String number) {
        double numberString;
        try {
            numberString = Double.parseDouble(number);
        } catch (Exception e) {
            return number;
        }
        DecimalFormat formatter = new DecimalFormat("###,###,###,###.##");
        formatter.setDecimalSeparatorAlwaysShown(false);
        return formatter.format(numberString);
    }

    public static String getFormmatedStringFromDouble(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("###.#");
        return decimalFormat.format(number);
    }

    private static String getWithCommas(String number) {
        long numberString;
        try {
            numberString = Long.parseLong(number);
        } catch (Exception e) {
            return number;
        }
        if (numberString < _MIN_NUMBER_FOR_COMMAS) {
            return String.valueOf(numberString);
        }

        NumberFormat formatter = new DecimalFormat("###,###,###,###");
        return formatter.format(numberString);

    }

    private static boolean isPricePlus(String priceAsString) {
        try {
            double price = getDoubleFromString(priceAsString);
            return price > 0;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    public static String getTextWithPricePrefixAndSuffix(Context context, String prefix, String price, String suffix) {
        String nis = context.getString(R.string.nis);
        return price.isEmpty() ? "" : prefix + " " + NumberUtils.getWithCommasAndDecimal(price) + " " + nis + " " + suffix;
    }

    public static String getTextWithPrice(String priceAsString, Context context) {
        String nis = context.getString(R.string.nis);
        return priceAsString.isEmpty() ? "" : nis + NumberUtils.getWithCommasAndDecimal(priceAsString) ;
    }

    public static String getPriceTextSolution(String priceAsString, Context context) {
        String nis = context.getString(R.string.nis);
        try {
            priceAsString = formatStringToDouble(priceAsString);
            if (isPricePlus(priceAsString)) {
                return nis + priceAsString + "+";
            } else {
                return nis + replacePlaceOfMinus(priceAsString);
            }
        } catch (NumberFormatException exception) {
            return priceAsString;
        }
    }

    public static String getPriceText(String priceAsString, Context context) {
        String nis = context.getString(R.string.nis);
        try {
            priceAsString = formatStringToDouble(priceAsString);
            if (isPricePlus(priceAsString)) {
                return "+" + nis + priceAsString;
            } else {
                return "-" + removeSignFromPrice(priceAsString, context);
            }
        } catch (NumberFormatException exception) {
            return priceAsString;
        }
    }

    public static String removeSignFromPrice(String price, Context context) {
        return context.getString(R.string.nis) + price.replace("+", "").replace("-", "");
    }

    public static String getPriceTextSmallerThanOne(String priceAsString, Context context) {
        if (!stringIsNumber(priceAsString)) {
            priceAsString = "0";
        }
        double price = Double.parseDouble(priceAsString);
        DecimalFormat df = getPriceFormatter();
        priceAsString = df.format(price);
        return priceAsString.replace(",", ".");
    }

    private static String formatStringToDouble(String priceAsString) {
        double price = Double.parseDouble(priceAsString);
        DecimalFormat df = getPriceFormatter();
        priceAsString = df.format(price);
        return priceAsString;
    }

    private static DecimalFormat getPriceFormatter() {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyLocalizedPattern("0.00");
        return df;
    }

    private static String replacePlaceOfMinus(String price) {
        if (price.contains("-")) {
            price = price.replace("-", "");
            price = price + "-";
        }
        return price;
    }

    public static boolean priceIsNotZero(String priceAsString) {
        try {
            double price = Double.parseDouble(priceAsString);
            return price > 0 || price < 0;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    public static boolean stringIsNumber(String numberAsString) {
        if (numberAsString == null) {
            return false;
        }
        try {
            Double.parseDouble(numberAsString);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static String customFormat(String pattern, double value) {
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        return myFormatter.format(value);
    }

    public static boolean stringIsnumber(String numberAsString) {
        if (numberAsString == null) {
            return false;
        }
        try {
            Double.parseDouble(numberAsString);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    // if value = 4.5 return 4.5
    // if value = 4.0 return 4
    public static String getNumberWihtoutZeroAfterDecimalPoint(double value) {
        String valueAsString = String.valueOf(value);
        if ((value * 10) % 10 == 0) {
            valueAsString = String.valueOf((int) value);
        }
        return valueAsString;
    }

    public static List<Integer> getBitwiseComponents(int value) {
        int currentComponent = 1;
        List<Integer> components = new ArrayList<>();
        while (currentComponent <= value) {
            if ((value & currentComponent) == currentComponent) {
                components.add(currentComponent);
                value -= currentComponent;
            }
            currentComponent *= 2;
        }
        return components;
    }

    protected void insertCommaIntoNumber(EditText etText, CharSequence s) {
        try {
            if (s.toString().length() > 0) {
                String convertedStr = s.toString();
                if (s.toString().contains(".")) {
                    if (chkConvert(s.toString()))
                        convertedStr = customFormat("###,###.##", Double.parseDouble(s.toString().replace(",", "")));
                } else {
                    convertedStr = customFormat("###,###.##", Double.parseDouble(s.toString().replace(",", "")));
                }

                if (!etText.getText().toString().equals(convertedStr) && convertedStr.length() > 0) {
                    etText.setText(convertedStr);
                    etText.setSelection(etText.getText().length());
                }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean chkConvert(String s) {
        String tempArray[] = s.split("\\.");
        return tempArray.length > 1 && Integer.parseInt(tempArray[1]) > 0;
    }


}
