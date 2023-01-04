package il.co.inmanage.meshulam_sdk.sdk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import il.co.inmanage.meshulam_sdk.managers.BaseManager;

public class BitManager extends BaseManager {

    private static BitManager bitManager;
    private Activity activity;


    protected BitManager(Activity baseApplication) {
        super(baseApplication);
        this.activity = baseApplication;
    }

    public static BitManager getInstance(Activity baseApplication) {
        if (bitManager == null) {
            bitManager = new BitManager(baseApplication);
        }
        return bitManager;
    }

    public void loadBitUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
    }

    @Override
    public void reset() {

    }
}
