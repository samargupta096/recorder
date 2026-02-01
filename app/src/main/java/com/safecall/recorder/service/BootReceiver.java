package com.safecall.recorder.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Receiver that handles device boot to ensure call recording
 * is properly initialized after device restart.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // The PhoneCallReceiver is registered in manifest
            // and will automatically be active after boot.
        }
    }
}
