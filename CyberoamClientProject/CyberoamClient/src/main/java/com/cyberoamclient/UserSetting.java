package com.cyberoamclient;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Shantanu on 7/1/13.
 */
public class UserSetting extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

    }
}
