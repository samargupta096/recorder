package com.safecall.recorder.service;

import android.app.Notification;
import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.safecall.recorder.data.local.prefs.PreferencesManager;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Service to listen for WhatsApp call notifications and trigger recording.
 */
@AndroidEntryPoint
public class WhatsAppCallListenerService extends NotificationListenerService {

    private static final String TAG = "WhatsAppCallListener";
    private static final String WA_PACKAGE = "com.whatsapp";
    private static final String WA_BUSINESS_PACKAGE = "com.whatsapp.w4b";

    @Inject
    PreferencesManager preferencesManager;

    private boolean isRecording = false;

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (!preferencesManager.isWhatsAppRecordingEnabled()) {
            return;
        }

        String packageName = sbn.getPackageName();
        if (!WA_PACKAGE.equals(packageName) && !WA_BUSINESS_PACKAGE.equals(packageName)) {
            return;
        }

        Notification notification = sbn.getNotification();
        if (notification == null || notification.extras == null) {
            return;
        }

        String title = notification.extras.getString(Notification.EXTRA_TITLE);
        String text = notification.extras.getString(Notification.EXTRA_TEXT);

        Log.d(TAG, "Notification posted: " + title + " - " + text);

        // DEBUG: Toast EVERY WhatsApp notification to verify content
        String finalTitle = title;
        String finalText = text;
        new android.os.Handler(android.os.Looper.getMainLooper())
                .post(() -> android.widget.Toast.makeText(getApplicationContext(),
                        "WA Notif: " + finalTitle + " | " + finalText, android.widget.Toast.LENGTH_LONG).show());

        if (isCallNotification(title, text)) {
            Log.d(TAG, "WhatsApp call detected");
            if (!isRecording) {
                startRecording();
                isRecording = true;
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (!isRecording)
            return;

        String packageName = sbn.getPackageName();
        if (!WA_PACKAGE.equals(packageName) && !WA_BUSINESS_PACKAGE.equals(packageName)) {
            return;
        }

        Notification notification = sbn.getNotification();
        String title = notification != null && notification.extras != null
                ? notification.extras.getString(Notification.EXTRA_TITLE)
                : "";
        String text = notification != null && notification.extras != null
                ? notification.extras.getString(Notification.EXTRA_TEXT)
                : "";

        Log.d(TAG, "Notification removed: " + title + " - " + text);

        // Heuristic: If the ongoing call notification is removed, stop recording
        // Note: Better logic might be needed to distinguish between "on hold" and
        // "ended"
        if (isCallNotification(title, text) || isRecording) {
            // Often the notification simply disappears or changes text when call ends
            // For safety, if we were recording and a WA notification is removed, we check
            // if we should stop.
            // However, aggressive stopping might be bad.
            // Let's assume if the specific "Voice call" notification is removed, the call
            // ended.

            // A safer bet is to stop recording if we were recording.
            // But we need to be careful not to stop on random message notifications.
            // The NotificationListenerService doesn't always make it easy to track *which*
            // notification ID was the call.
            // We'll rely on the user manually stopping or the generic 'Ongoing call'
            // notification being removed.

            // For MVP: Stop recording if ANY notification from WA is removed? No, that's
            // bad (new message removed).
            // We should check if *no* call notifications remain?

            if (getActiveNotifications() != null) {
                boolean callStillActive = false;
                for (StatusBarNotification activeSbn : getActiveNotifications()) {
                    if (WA_PACKAGE.equals(activeSbn.getPackageName())
                            || WA_BUSINESS_PACKAGE.equals(activeSbn.getPackageName())) {
                        Notification n = activeSbn.getNotification();
                        if (n != null && n.extras != null) {
                            String t = n.extras.getString(Notification.EXTRA_TITLE);
                            String txt = n.extras.getString(Notification.EXTRA_TEXT);
                            if (isCallNotification(t, txt)) {
                                callStillActive = true;
                                break;
                            }
                        }
                    }
                }

                if (!callStillActive) {
                    Log.d(TAG, "No active call notification found, stopping recording");
                    stopRecording();
                    isRecording = false;
                }
            }
        }
    }

    private boolean isCallNotification(String title, String text) {
        if (text == null)
            return false;
        // Keywords for English. Localization would be needed for other languages.
        return text.contains("Voice call") || text.contains("Video call") || text.contains("Incoming voice call")
                || text.contains("Ongoing voice call");
    }

    private void startRecording() {
        Intent intent = new Intent(this, CallRecorderService.class);
        intent.setAction(CallRecorderService.ACTION_START_RECORDING);
        intent.putExtra(CallRecorderService.EXTRA_PHONE_NUMBER, "WhatsApp Call"); // Placeholder
        intent.putExtra(CallRecorderService.EXTRA_IS_INCOMING, true); // Assume incoming/generic
        startForegroundService(intent);
    }

    private void stopRecording() {
        Intent intent = new Intent(this, CallRecorderService.class);
        intent.setAction(CallRecorderService.ACTION_STOP_RECORDING);
        startService(intent);
    }
}
