package com.android.settings.notification;

import static com.android.settings.notification.SoundDolbyConstants.DOLBY_DAP_PROFILE;
import static com.android.settings.notification.SoundDolbyConstants.DOLBY_DAP_STATE;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;

public class SoundDolbyParentPreferenceController extends BasePreferenceController
        implements LifecycleObserver, OnResume, OnPause {

    private Preference mPreference;
    private SettingObserver mSettingObserver;

    public SoundDolbyParentPreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);
    }

    @Override
    public int getAvailabilityStatus() {
        return BasePreferenceController.AVAILABLE;
    }

    private CharSequence updateSummary() {
        if (Settings.Global.getInt(mContext.getContentResolver(), DOLBY_DAP_STATE, 1) == 1) {
            int profileIndex = Settings.Global.getInt(mContext.getContentResolver(), DOLBY_DAP_PROFILE, 0);
            if (profileIndex >= 0 && profileIndex < SoundDolbyConstants.PROFILE_KEYS.length) {
                return mContext.getText(SoundDolbyConstants.PROFILE_STRING_IDS[profileIndex]);
            }
        }
        return mContext.getText(R.string.switch_off_text);
    }

    @Override
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        mPreference = preferenceScreen.findPreference(getPreferenceKey());
        if (mPreference != null) {
            mSettingObserver = new SettingObserver(mPreference);
        }
    }

    @Override
    public void updateState(Preference preference) {
        mPreference.setSummary(updateSummary());
    }

    @Override
    public CharSequence getSummary() {
        return updateSummary();
    }

    @Override
    public void onResume() {
        if (mSettingObserver != null) {
            mSettingObserver.register(mContext.getContentResolver());
        }
    }

    @Override
    public void onPause() {
        if (mSettingObserver != null) {
            mSettingObserver.unregister(mContext.getContentResolver());
        }
    }

    private class SettingObserver extends ContentObserver {
        private final Uri SOUND_DOLBY_STATE;
        private final Preference mPreference;

        public SettingObserver(Preference preference) {
            super(new Handler());
            SOUND_DOLBY_STATE = Uri.parse("content://settings/global/" + DOLBY_DAP_STATE);
            mPreference = preference;
        }

        public void register(ContentResolver contentResolver) {
            contentResolver.registerContentObserver(SOUND_DOLBY_STATE, false, this);
        }

        public void unregister(ContentResolver contentResolver) {
            contentResolver.unregisterContentObserver(this);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            if (uri == null || SOUND_DOLBY_STATE.equals(uri)) {
                SoundDolbyParentPreferenceController.this.updateState(mPreference);
            }
        }
    }
}