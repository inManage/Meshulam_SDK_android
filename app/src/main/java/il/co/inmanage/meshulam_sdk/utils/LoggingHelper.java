package il.co.inmanage.meshulam_sdk.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;


public class LoggingHelper {

    /**
     * to change DEBUG_MODE change value in build.gradle Module:inmanage inmanage https://www.screencast.com/t/vYwBX7hLPb
     */
    public static final boolean DEBUG_MODE = true;

    public static final boolean IS_SHOW_ABOUT_APPLICATION = DEBUG_MODE;

    public static void d(String tag, String msg) {
        if (DEBUG_MODE && msg != null) {
            Log.d(tag, msg);
        }
    }

    public static void d(String msg) {
        if (DEBUG_MODE && msg != null) {
            Log.d(getMethodName(), msg);
        }
    }

    public static void e(String msg) {
        if (DEBUG_MODE && msg != null) {
            Log.e(getMethodName(), msg);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG_MODE && msg != null) {
            Log.e(tag, msg);
        }
    }

    public static void entering() {
        if (DEBUG_MODE) {
            String methodName = getMethodName();
            Log.d(methodName, "--> Entering " + methodName);
        }
    }

    public static void entering(String string) {
        if (DEBUG_MODE) {
            String methodName = getMethodName();
            Log.d(methodName, "--> Entering " + methodName + " " + String.valueOf(string));
        }

    }

    /*public static void exiting(String msg) {
        if (DEBUG_MODE && msg != null) {
            String methodName = getMethodName();
            Log.d(methodName, "<-- Exiting " + methodName + " " + String.valueOf(msg));
        }
    }

    public static void exiting() {
        if (DEBUG_MODE) {
            String methodName = getMethodName();
            Log.d(methodName, "<-- Exiting " + methodName);
        }
    }*/
    public static void ptintTimeStamp(String msg) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        d(msg + " " + "Date:" + sdf.format(new Date()));

    }
    public static String getMethodName() {
        return getMethodName(5);
    }

	/*public static void addToList(String CLASS_NAME;){
        classes.add(CLASS_NAME;);
	}
	
	public static void removeFromList(String CLASS_NAME;){
		classes.remove(CLASS_NAME;);
	}*/

    private static String getMethodName(int depth) {
        try {
            StringBuilder buffer = new StringBuilder();
            //int size = Thread.currentThread().getStackTrace().length;
            StackTraceElement trace = Thread.currentThread().getStackTrace()[depth];
            String methodName = trace.getMethodName();
            String[] classNameSplited = trace.getClassName().split("\\.");
            int lineNumber = trace.getLineNumber();

            if (methodName.trim().equals("<init>".trim())) {
                methodName = "Constructor";
            }
            /*for (int i = 0; i < CLASS_NAME;Splited.length; i++) {
            Log.d("test", CLASS_NAME;Splited[i]);
		}
		Log.d("test", "array size is "+CLASS_NAME;Splited.length);*/
            int sizeOfArray = classNameSplited.length - 1;
            buffer.append(classNameSplited[sizeOfArray].replaceAll("\\$", "\\\\")).append(".")
                    .append(methodName).append("(").append(String.valueOf(lineNumber)).append(")");
			/*for (int i = 0; i < size; i++) {
            Log.d("test", "stack method names -> "+i+" = "+Thread.currentThread().getStackTrace()[i].getMethodName());
		}
		for (int i = 0; i < size; i++) {
			Log.d("test", "stack class name -> "+i+" = "+Thread.currentThread().getStackTrace()[i].getCLASS_NAME;());
		}*/
            return buffer.toString();
        } catch (Exception e) {
            return "error";
        }
    }
}

