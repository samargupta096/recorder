package com.safecall.recorder.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.core.content.ContextCompat;

import com.safecall.recorder.data.local.prefs.PreferencesManager;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * AccessibilityService used to bypass Android 12+ background execution limits
 * and Android 11+ background microphone restrictions.
 * It listens to Phone state changes and starts the CallRecorderService.
 */
@AndroidEntryPoint
public class CallRecordingAccessibilityService extends AccessibilityService {

    private static final String TAG = "CallRecordAccessibility";

    @Inject
    PreferencesManager preferencesManager;

    private TelephonyManager telephonyManager;
    private CallStateListener callStateListener;

    private int lastState = TelephonyManager.CALL_STATE_IDLE;
    private boolean isIncoming = false;
    private String savedNumber = "Unknown";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Optional: Could be used to detect dialer screen text as a fallback.
        // For now, we rely on PhoneStateListener which is exempt from background limits
        // while this AccessibilityService is running.
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Accessibility Service interrupted");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "Accessibility Service connected");

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        callStateListener = new CallStateListener();
        
        // Register listener
        if (telephonyManager != null) {
            telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            Log.d(TAG, "PhoneStateListener registered");
        }
    }

    @Override
    public void onDestroy() {
        if (telephonyManager != null && callStateListener != null) {
            telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
        }
        super.onDestroy();
    }

    private class CallStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            super.onCallStateChanged(state, phoneNumber);

            if (preferencesManager == null || 
                !preferencesManager.hasAcceptedConsent() || 
                !preferencesManager.isAutoRecordingEnabled()) {
                return;
            }

            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                savedNumber = phoneNumber;
            }

            // Incoming call started ringing
            if (lastState == TelephonyManager.CALL_STATE_IDLE && 
                state == TelephonyManager.CALL_STATE_RINGING) {
                isIncoming = true;
                Log.d(TAG, "Call State: RINGING (Incoming)");
            }
            // Call answered (either incoming or outgoing)
            else if ((lastState == TelephonyManager.CALL_STATE_RINGING || 
                      lastState == TelephonyManager.CALL_STATE_IDLE) && 
                     state == TelephonyManager.CALL_STATE_OFFHOOK) {
                Log.d(TAG, "Call State: OFFHOOK (Answered/Dialing)");
                startRecordingService(savedNumber, isIncoming);
            }
            // Call ended
            else if ((lastState == TelephonyManager.CALL_STATE_OFFHOOK || 
                      lastState == TelephonyManager.CALL_STATE_RINGING) && 
                     state == TelephonyManager.CALL_STATE_IDLE) {
                Log.d(TAG, "Call State: IDLE (Ended)");
                stopRecordingService();
                savedNumber = "Unknown";
                isIncoming = false;
            }

            lastState = state;
        }
    }

    private void startRecordingService(String phoneNumber, boolean isIncoming) {
        Log.d(TAG, "Starting CallRecorderService from AccessibilityService");
        Intent serviceIntent = new Intent(this, CallRecorderService.class);
        serviceIntent.setAction(CallRecorderService.ACTION_START_RECORDING);
        serviceIntent.putExtra(CallRecorderService.EXTRA_PHONE_NUMBER, phoneNumber);
        serviceIntent.putExtra(CallRecorderService.EXTRA_IS_INCOMING, isIncoming);
        try {
            // Because we are an AccessibilityService, we are exempt from background start limits!
            ContextCompat.startForegroundService(this, serviceIntent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to start recording service", e);
        }
    }

    private void stopRecordingService() {
        Log.d(TAG, "Stopping CallRecorderService from AccessibilityService");
        Intent serviceIntent = new Intent(this, CallRecorderService.class);
        serviceIntent.setAction(CallRecorderService.ACTION_STOP_RECORDING);
        try {
            ContextCompat.startForegroundService(this, serviceIntent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to stop recording service", e);
        }
    }
}
