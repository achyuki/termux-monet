package com.termux.app.fragments.settings.termux;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;
import com.termux.R;
import com.termux.app.style.TermuxBackgroundManager;
import com.termux.shared.file.FileUtils;
import com.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences;
import static com.termux.shared.termux.TermuxConstants.TERMUX_DATA_HOME_DIR_PATH;

@Keep
public class TermuxStylePreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null)
            return;
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceDataStore(TermuxStylePreferencesDataStore.getInstance(context));
        setPreferencesFromResource(R.xml.termux_style_preferences, rootKey);
        configurePreferences(context);
    }

    /**
     * Configure background preferences and make appropriate changes in the state of components.
     *
     * @param context The context for operations.
     */
    private void configurePreferences(@NonNull Context context) {
        SwitchPreferenceCompat backgroundImagePreference = findPreference("background_image_enabled");
        if (backgroundImagePreference != null) {
            TermuxAppSharedPreferences preferences = TermuxAppSharedPreferences.build(context, true);
            if (preferences == null)
                return;
            // If background image preference is disabled and background images are
            // missing, then don't allow user to enable it from setting.
            if (!preferences.isBackgroundImageEnabled() && !TermuxBackgroundManager.isImageFilesExist(context, false)) {
                backgroundImagePreference.setEnabled(false);
            }
        }

        SwitchPreferenceCompat biometricAuthPreference = findPreference("biometric_auth_enabled");
        if (biometricAuthPreference != null && !isBiometricAuthAvailable(context)) {
            biometricAuthPreference.setEnabled(false);
        }
    }

    private boolean isBiometricAuthAvailable(Context context) {
        BiometricManager biometricManager = BiometricManager.from(context);
        return biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS;
    }
}

class TermuxStylePreferencesDataStore extends PreferenceDataStore {

    private final Context mContext;

    private final TermuxAppSharedPreferences mPreferences;

    private static TermuxStylePreferencesDataStore mInstance;

    private final String BUILTIN_TAPI_ENABLED_FLAG_PATH = TERMUX_DATA_HOME_DIR_PATH + "/enable-built-in-tapi";

    private TermuxStylePreferencesDataStore(Context context) {
        mContext = context;
        mPreferences = TermuxAppSharedPreferences.build(context, true);
    }

    public static synchronized TermuxStylePreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TermuxStylePreferencesDataStore(context);
        }
        return mInstance;
    }

    @Override
    public void putBoolean(String key, boolean value) {
        if (mPreferences == null)
            return;
        if (key == null)
            return;
        switch(key) {
            case "background_image_enabled":
                mPreferences.setBackgroundImageEnabled(value);
                break;
            case "extrakeys_blur_enabled":
                mPreferences.setExtraKeysBlurEnabled(value);
                break;
            case "sessions_blur_enabled":
                mPreferences.setSessionsBlurEnabled(value);
                break;
            case "monet_background_enabled":
                mPreferences.setMonetBackgroundEnabled(value);
                break;
            case "biometric_auth_enabled":
                mPreferences.setBiometricAuthEnabled(value);
                break;
            case "builtin_tapi_enabled":
                if (value)
                    FileUtils.createRegularFile(BUILTIN_TAPI_ENABLED_FLAG_PATH);
                else
                    FileUtils.deleteFile(null, BUILTIN_TAPI_ENABLED_FLAG_PATH, true);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        if (mPreferences == null)
            return false;
        switch(key) {
            case "background_image_enabled":
                return mPreferences.isBackgroundImageEnabled();
            case "extrakeys_blur_enabled":
                return mPreferences.isExtraKeysBlurEnabled();
            case "sessions_blur_enabled":
                return mPreferences.isSessionsBlurEnabled();
            case "monet_background_enabled":
                return mPreferences.isMonetBackgroundEnabled();
            case "biometric_auth_enabled":
                return mPreferences.isBiometricAuthEnabled();
            case "builtin_tapi_enabled":
                return FileUtils.fileExists(BUILTIN_TAPI_ENABLED_FLAG_PATH, true);
            default:
                return false;
        }
    }
}
