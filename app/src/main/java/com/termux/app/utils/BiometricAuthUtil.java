package com.termux.app.utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.biometric.BiometricPrompt.AuthenticationCallback;
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executor;

public class BiometricAuthUtil {
    public static void launchBiometricAuth(AppCompatActivity context, AuthenticationCallback callback) {
        Executor executor = ContextCompat.getMainExecutor(context);

        BiometricPrompt biometricPrompt = new BiometricPrompt(context, executor, callback);
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Auth")
            .setSubtitle("Please verify your identity.")
            .setNegativeButtonText("Cancel")
            .build();

        biometricPrompt.authenticate(promptInfo);
    }
}
