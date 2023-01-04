package il.co.inmanage.meshulam_sdk.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;
import android.view.Display;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

import com.android.volley.BuildConfig;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class DeviceUtils {

    static String getModelDevice() {
        return Build.MODEL;
    }

    public static String getVersionOperationSystem() {
        return Build.VERSION.RELEASE + "";
    }

    @SuppressLint("HardwareIds")
    public static String getUuid(Context context) {
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE);
        String uuid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        LoggingHelper.d("uuid:" + uuid);
        return uuid;
    }

    public static int[] getDeviceSize(Context context) {
        int[] dimensions = new int[2];
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        dimensions[0] = size.x;
        dimensions[1] = size.y;
        return dimensions;
    }

    public static float getDeviceRatio(Context context) {
        int[] deviceDimensions = getDeviceSize(context);
        return (float) deviceDimensions[1] / (float) deviceDimensions[0];
    }

    public static void logKeyHash(Context context) {
        // Add code to print out the key hash
        if (BuildConfig.DEBUG) {
            String text = "";
            try {
                PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
                for (Signature signature : info.signatures) {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    LoggingHelper.d("keyHash= " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
                    // text = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                }
            } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ignored) {

            }
            //return text;
        }
    }

   /* public static void getGoogleAdvertisingId(final Context context) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
                        String adId = AdvertisingIdClient.getAdvertisingIdInfo(context).getId();
                        LoggingHelper.d("Advertisingid", adId.toString());
                    }
                } catch (IOException | GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException exception) {
                    LoggingHelper.d("Advertisingid", exception.getMessage());
                }
            }
        });
    }*/
}
