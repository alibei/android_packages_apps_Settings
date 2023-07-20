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
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.widget.RadioButtonPreference;

import java.util.HashMap;
import java.util.Map;

public class SoundDolbyPreferenceController extends AbstractPreferenceController
        implements RadioButtonPreference.OnClickListener, LifecycleObserver, OnResume, OnPause {

    private final Context mContext;
    private final Map<String, Integer> mDolbyProfileKeyToValueMap;
    private PreferenceCategory mPreferenceCategory;
    private RadioButtonPreference[] mRadioButtonPreferences;
    private SettingObserver mSettingObserver;

    public SoundDolbyPreferenceController(Context context, Lifecycle lifecycle) {
        super(context);
        mRadioButtonPreferences = new RadioButtonPreference[SoundDolbyConstants.PROFILE_KEYS.length];
        mDolbyProfileKeyToValueMap = new HashMap<>();
        mContext = context;
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    private Map<String, Integer> getProfileValueToKeyMap() {
        if (mDolbyProfileKeyToValueMap.isEmpty()) {
            for (int i = 0; i < SoundDolbyConstants.PROFILE_KEYS.length; i++) {
                mDolbyProfileKeyToValueMap.put(SoundDolbyConstants.PROFILE_KEYS[i], i);
            }
        }
        return mDolbyProfileKeyToValueMap;
    }

    @Override
    public String getPreferenceKey() {
        return "sound_dolby_settings_category";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        mPreferenceCategory = preferenceScreen.findPreference(getPreferenceKey());
        for (int i = 0; i < SoundDolbyConstants.PROFILE_KEYS.length; i++) {
            mRadioButtonPreferences[i] = createRadioButtonPreference(
                    SoundDolbyConstants.PROFILE_KEYS[i],
                    SoundDolbyConstants.PROFILE_STRING_IDS[i],
                    SoundDolbyConstants.SUMMARY_STRING_IDS[i]);
        }
        if (mPreferenceCategory != null) {
            mSettingObserver = new SettingObserver(mPreferenceCategory);
        }
    }

    @Override
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        int profileValue = getProfileValueToKeyMap().get(radioButtonPreference.getKey());
        if (profileValue != Settings.Global.getInt(mContext.getContentResolver(), DOLBY_DAP_PROFILE, 0)) {
            Settings.Global.putInt(mContext.getContentResolver(), DOLBY_DAP_PROFILE, profileValue);
        }
    }

    @Override
    public void updateState(Preference preference) {
        int dapProfile = Settings.Global.getInt(mContext.getContentResolver(), DOLBY_DAP_PROFILE, 0);
        boolean dapState = Settings.Global.getInt(mContext.getContentResolver(), DOLBY_DAP_STATE, 1) == 1;
        for (int i = 0; i < SoundDolbyConstants.PROFILE_KEYS.length; i++) {
            mRadioButtonPreferences[i].setEnabled(dapState);
            mRadioButtonPreferences[i].setChecked(i == dapProfile);
        }
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

    private RadioButtonPreference createRadioButtonPreference(String key, int titleId, int summaryId) {
        RadioButtonPreference radioButtonPreference = new RadioButtonPreference(mPreferenceCategory.getContext());
        radioButtonPreference.setKey(key);
        radioButtonPreference.setTitle(titleId);
        radioButtonPreference.setOnClickListener(this);
        radioButtonPreference.setSummary(summaryId);
        mPreferenceCategory.addPreference(radioButtonPreference);
        return radioButtonPreference;
    }

    private class SettingObserver extends ContentObserver {
        private final Uri SOUND_DOLBY_PROFILE;
        private final Uri SOUND_DOLBY_STATE;
        private final Preference mPreference;

        public SettingObserver(Preference preference) {
            super(new Handler());
            SOUND_DOLBY_PROFILE = Uri.parse("content://settings/global/" + DOLBY_DAP_PROFILE);
            SOUND_DOLBY_STATE = Uri.parse("content://settings/global/" + DOLBY_DAP_STATE);
            mPreference = preference;
        }

        public void register(ContentResolver contentResolver) {
            contentResolver.registerContentObserver(SOUND_DOLBY_PROFILE, false, this);
            contentResolver.registerContentObserver(SOUND_DOLBY_STATE, false, this);
        }

        public void unregister(ContentResolver contentResolver) {
            contentResolver.unregisterContentObserver(this);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            if (uri == null || SOUND_DOLBY_PROFILE.equals(uri) || SOUND_DOLBY_STATE.equals(uri)) {
                SoundDolbyPreferenceController.this.updateState(mPreference);
            }
        }
    }
}