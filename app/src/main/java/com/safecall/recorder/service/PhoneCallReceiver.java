package com.safecall.recorder.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.safecall.recorder.data.local.prefs.PreferencesManager;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * BroadcastReceiver that detects incoming and outgoing phone calls.
 * Starts/stops the CallRecorderService based on call state.
 */
@AndroidEntryPoint
public class PhoneCallReceiver extends BroadcastReceiver {

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static boolean isIncoming = false;
    private static String savedNumber = null;

    @Inject
    PreferencesManager preferencesManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Check if user has consented and recording is enabled
        if (preferencesManager == null || 
            !preferencesManager.hasAcceptedConsent() || 
            !preferencesManager.isAutoRecordingEnabled()) {
            return;
        }

        String action = intent.getAction();

        if (Intent.ACTION_NEW_OUTGOING_CALL.equals(action)) {
            // Outgoing call detected
            savedNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            isIncoming = false;
        } else if (TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(action)) {
            // Phone state changed
            String stateStr = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if (number != null) {
                savedNumber = number;
            }

            int state = TelephonyManager.CALL_STATE_IDLE;
            if (TelephonyManager.EXTRA_STATE_IDLE.equals(stateStr)) {
                state = TelephonyManager.CALL_STATE_IDLE;
            } else if (TelephonyManager.EXTRA_STATE_RINGING.equals(stateStr)) {
                state = TelephonyManager.CALL_STATE_RINGING;
            } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(stateStr)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            }

            onCallStateChanged(context, state);
        }
    }

    /**
     * Handle call state transitions and control recording service.
     */
    private void onCallStateChanged(Context context, int state) {
        // Incoming call started ringing
        if (lastState == TelephonyManager.CALL_STATE_IDLE && 
            state == TelephonyManager.CALL_STATE_RINGING) {
            isIncoming = true;
        }
        // Call answered (either incoming or outgoing)
        else if ((lastState == TelephonyManager.CALL_STATE_RINGING || 
                  lastState == TelephonyManager.CALL_STATE_IDLE) && 
                 state == TelephonyManager.CALL_STATE_OFFHOOK) {
            startRecording(context, savedNumber != null ? savedNumber : "Unknown", isIncoming);
        }
        // Call ended
        else if ((lastState == TelephonyManager.CALL_STATE_OFFHOOK || 
                  lastState == TelephonyManager.CALL_STATE_RINGING) && 
                 state == TelephonyManager.CALL_STATE_IDLE) {
            stopRecording(context);
            savedNumber = null;
            isIncoming = false;
        }

        lastState = state;
    }

    /**
     * Start the call recording service.
     */
    private void startRecording(Context context, String phoneNumber, boolean isIncoming) {
        Intent serviceIntent = new Intent(context, CallRecorderService.class);
        serviceIntent.setAction(CallRecorderService.ACTION_START_RECORDING);
        serviceIntent.putExtra(CallRecorderService.EXTRA_PHONE_NUMBER, phoneNumber);
        serviceIntent.putExtra(CallRecorderService.EXTRA_IS_INCOMING, isIncoming);
        try {
            androidx.core.content.ContextCompat.startForegroundService(context, serviceIntent);
        } catch (Exception e) {
            android.util.Log.e("PhoneCallReceiver", "Failed to start recording service", e);
        }
    }

    /**
     * Stop the call recording service.
     */
    private void stopRecording(Context context) {
        Intent serviceIntent = new Intent(context, CallRecorderService.class);
        serviceIntent.setAction(CallRecorderService.ACTION_STOP_RECORDING);
        try {
            androidx.core.content.ContextCompat.startForegroundService(context, serviceIntent);
        } catch (Exception e) {
            android.util.Log.e("PhoneCallReceiver", "Failed to stop recording service", e);
        }
    }
}
