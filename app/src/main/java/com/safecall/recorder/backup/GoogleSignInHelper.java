package com.safecall.recorder.backup;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.drive.DriveScopes;
import com.safecall.recorder.data.local.prefs.PreferencesManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * Helper class for Google Sign-In with Drive API scope.
 */
@Singleton
public class GoogleSignInHelper {

    private final Context context;
    private final PreferencesManager preferencesManager;
    private final GoogleSignInClient googleSignInClient;

    @Inject
    public GoogleSignInHelper(
            @ApplicationContext Context context,
            PreferencesManager preferencesManager
    ) {
        this.context = context;
        this.preferencesManager = preferencesManager;

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                .build();

        this.googleSignInClient = GoogleSignIn.getClient(context, signInOptions);
    }

    /**
     * Get the current signed-in account, if any.
     */
    public GoogleSignInAccount getSignedInAccount() {
        return GoogleSignIn.getLastSignedInAccount(context);
    }

    /**
     * Check if user is currently signed in.
     */
    public boolean isSignedIn() {
        GoogleSignInAccount account = getSignedInAccount();
        return account != null && GoogleSignIn.hasPermissions(
                account,
                new Scope(DriveScopes.DRIVE_FILE)
        );
    }

    /**
     * Get sign-in intent to launch sign-in flow.
     */
    public Intent getSignInIntent() {
        return googleSignInClient.getSignInIntent();
    }

    /**
     * Handle sign-in result and update preferences.
     */
    public void handleSignInResult(GoogleSignInAccount account) {
        if (account != null) {
            preferencesManager.setGoogleAccountEmail(account.getEmail());
            preferencesManager.setDriveBackupEnabled(true);
        }
    }

    /**
     * Sign out from Google account.
     */
    public void signOut(Runnable onComplete) {
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            preferencesManager.setGoogleAccountEmail(null);
            preferencesManager.setDriveBackupEnabled(false);
            preferencesManager.setScheduledBackupEnabled(false);
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }

    /**
     * Revoke access and disconnect.
     */
    public void revokeAccess(Runnable onComplete) {
        googleSignInClient.revokeAccess().addOnCompleteListener(task -> {
            preferencesManager.setGoogleAccountEmail(null);
            preferencesManager.setDriveBackupEnabled(false);
            preferencesManager.setScheduledBackupEnabled(false);
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }
}
