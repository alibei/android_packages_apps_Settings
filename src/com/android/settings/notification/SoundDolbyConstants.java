package com.android.settings.notification;

import com.android.settings.R;

public class SoundDolbyConstants {

    public static final String DOLBY_DAP_STATE = "dlb_dap_state";
    public static final String DOLBY_DAP_PROFILE = "dlb_dap_profile";

    public static final String[] PROFILE_KEYS = {
            "dlb_dap_profile_dynamic",
            "dlb_dap_profile_movie",
            "dlb_dap_profile_music"
    };

    public static final int[] PROFILE_STRING_IDS = {
            R.string.dolby_mode_intelligence_title,
            R.string.dolby_mode_movie_title,
            R.string.dolby_mode_music_title
    };

    public static final int[] SUMMARY_STRING_IDS = {
            R.string.dolby_mode_intelligence_summary,
            R.string.dolby_mode_movie_summary,
            R.string.dolby_mode_music_summary
    };
}