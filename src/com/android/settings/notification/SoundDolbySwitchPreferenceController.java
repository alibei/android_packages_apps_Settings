package com.android.settings.notification;

import static com.android.settings.notification.SoundDolbyConstants.DOLBY_DAP_STATE;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Switch;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.widget.SwitchBar;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.widget.LayoutPreference;

public class SoundDolbySwitchPreferenceController extends AbstractPreferenceController
        implements SwitchBar.OnSwitchChangeListener, LifecycleObserver, OnResume, OnPause {

    private final Context mContext;
    private SettingObserver mSettingObserver;
    private SwitchBar mSwitch;

    public SoundDolbySwitchPreferenceController(Context context, Lifecycle lifecycle) {
        super(context);
        mContext = context;
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override
    public String getPreferenceKey() {
        return "sound_dolby_settings_switch";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void displayPreference(PreferenceScreen preferenceScreen) {
        LayoutPreference layoutPreference;
        super.displayPreference(preferenceScreen);
        if (!isAvailable() || (layoutPreference = (LayoutPreference) preferenceScreen.findPreference(getPreferenceKey())) == null) {
            return;
        }
        mSettingObserver = new SettingObserver(layoutPreference);
        mSwitch = layoutPreference.findViewById(R.id.switch_bar);
        if (mSwitch != null) {
            mSwitch.addOnSwitchChangeListener(this);
            mSwitch.show();
        }
    }

    protected void setChecked(boolean checked) {
        SwitchBar switchBar = mSwitch;
        if (switchBar != null) {
            switchBar.setChecked(checked);
        }
    }

    @Override
    public void updateState(Preference preference) {
        setChecked(Settings.Global.getInt(mContext.getContentResolver(), DOLBY_DAP_STATE, 1) == 1);
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        if ((Settings.Global.getInt(mContext.getContentResolver(), DOLBY_DAP_STATE, 1) == 1) != isChecked) {
            Settings.Global.putInt(mContext.getContentResolver(), DOLBY_DAP_STATE, isChecked ? 1 : 0);
        }
    }

    @Override
    public void onResume() {
        SettingObserver settingObserver = mSettingObserver;
        if (settingObserver != null) {
            settingObserver.register(mContext.getContentResolver());
        }
    }

    @Override
    public void onPause() {
        SettingObserver settingObserver = mSettingObserver;
        if (settingObserver != null) {
            settingObserver.unregister(mContext.getContentResolver());
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
                updateState(mPreference);
            }
        }
    }
}