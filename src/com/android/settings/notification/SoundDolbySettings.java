package com.android.settings.notification;

import android.app.settings.SettingsEnums;
import android.content.Context;
import android.provider.SearchIndexableResource;

import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SoundDolbySettings extends DashboardFragment {

    public final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        @Override
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean enabled) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = R.xml.sound_dolby_settings;
            return Arrays.asList(searchIndexableResource);
        }

        @Override
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return buildPreferenceControllers(context, null);
        }
    };

    @Override
    protected String getLogTag() {
        return "SoundDolbySettings";
    }

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.NOTIFICATION_APP_NOTIFICATION;
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle) {
        ArrayList<AbstractPreferenceController> preferenceControllers = new ArrayList<>();
        preferenceControllers.add(new SoundDolbyPreferenceController(context, lifecycle));
        preferenceControllers.add(new SoundDolbySwitchPreferenceController(context, lifecycle));
        return preferenceControllers;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.sound_dolby_settings;
    }
}