package il.co.inmanage.meshulam_sdk.managers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import il.co.inmanage.meshulam_sdk.application.SdkBaseApplication;

public abstract class BaseManager {

    protected final Activity baseApplication;
    private final Set<BroadcastReceiver> broadcastReceivers;
    protected boolean isInitialized;

    protected BaseManager(Activity context) {
        this.baseApplication = context;
        SdkBaseApplication.getApp().setCurrentActivity(context);
        broadcastReceivers = new HashSet<>();
        init();
    }

    public abstract void reset();

    void onAppCreate() {
    }


    public void onRequestPermissionsResult(int requestCode, String[] resultCode, int[] data) {
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    private void onRegisterReceivers() {

    }

    private void onUnregisterReceivers() {
        unregisterAllBroadcastReceivers();
    }

    /*protected void registerBroadcastReceiver(BroadcastReceiver broadcastReceiver, String... actions) {
        broadcastReceivers.add(broadcastReceiver);
        IntentFilter intentFilter = new IntentFilter();
        for (String action : actions) {
            intentFilter.addAction(action);
        }
        baseApplication.registerReceiver(broadcastReceiver, intentFilter);
    }*/

    private void unregisterAllBroadcastReceivers() {
        Iterator<BroadcastReceiver> iterator = broadcastReceivers.iterator();
        while (iterator.hasNext()) {
            BroadcastReceiver broadcastReceiver = iterator.next();
            baseApplication.unregisterReceiver(broadcastReceiver);
            iterator.remove();
        }
    }

    /*protected void unRegisterBroadcastReceiver(BroadcastReceiver broadcastReceiver) {
        if (broadcastReceivers.remove(broadcastReceiver)) {
            baseApplication.unregisterReceiver(broadcastReceiver);
        }
    }

    public void onTrimMemory(int level) {

    }*/

    public void init() {
        isInitialized = true;
    }

    public boolean isInitialized() {
        return isInitialized;
    }
}
