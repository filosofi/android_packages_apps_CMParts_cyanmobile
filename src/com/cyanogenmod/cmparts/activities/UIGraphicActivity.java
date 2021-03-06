/*
 * Copyright (C) 2011 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyanogenmod.cmparts.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.app.Dialog;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.net.Uri;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;

import com.cyanogenmod.cmparts.R;
import com.cyanogenmod.cmparts.activities.ColorPickerDialog.OnColorChangedListener;

public class UIGraphicActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String PREF_SQUADZONE = "squadkeys";

    private static final String MOVE_APP_BACKGROUND_INTENT = "com.cyanogenmod.cmappbackgroundchooser.COPY_APP_BACKGROUND";

    private static final String PREF_APP_BACKGROUND_COLOR = "pref_app_background_color";

    private static final String PREF_TRANSPARENT_APP_BACKGROUND = "pref_transparent_app_background";

    private static final String PREF_TRANSPARENT_FULL_BACKGROUND = "pref_transparent_full_background";

    private static final String OVERSCROLL_PREF = "pref_overscroll_effect";

    private static final String OVERSCROLL_WEIGHT_PREF = "pref_overscroll_weight";

    private static final String OVERSCROLL_COLOR = "pref_overscroll_color";

    private static final String KEY_FONT_FONT_SETINGS = "fontsettings";

    private static final String PREF_TEXT_FULL_OF_COLOR = "pref_text_full_of_color";

    private static final String PREF_TEXT_GLOBAL_OF_COLOR = "pref_text_global_of_color";

    static Context mContext;

    private static final int REQUEST_CODE_PICK_FILE_BG = 998;

    private Preference mSquadzone;
    private Preference mAppBackgroundColor;
    private ListPreference mTransparentFullBackgroundPref;
    private ListPreference mTransparentAppBackgroundPref;
    private ListPreference mOverscrollPref;
    private ListPreference mOverscrollColor;
    private ListPreference mOverscrollWeightPref;
    private PreferenceScreen mFontsPref;
    private CheckBoxPreference mTextGlobalOfColor;
    private Preference mTextFullOfColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.ui_graphicss_title);
        addPreferencesFromResource(R.xml.ui_graphicss);

		mContext = this.getBaseContext();

        PreferenceScreen prefSet = getPreferenceScreen();

        mSquadzone = (Preference) prefSet.findPreference(PREF_SQUADZONE);
        mSquadzone.setSummary("CyanMobile");

        mAppBackgroundColor = (Preference) prefSet.findPreference(PREF_APP_BACKGROUND_COLOR);
        mAppBackgroundColor.setOnPreferenceChangeListener(this);

        int transparentAppBackgroundPref = Settings.System.getInt(getContentResolver(),
                Settings.System.TRANSPARENT_BACKGROUND_APP, 0);
        mTransparentAppBackgroundPref = (ListPreference) prefSet.findPreference(PREF_TRANSPARENT_APP_BACKGROUND);
        mTransparentAppBackgroundPref.setValue(String.valueOf(transparentAppBackgroundPref));
        mTransparentAppBackgroundPref.setOnPreferenceChangeListener(this);

        int appBackgroundColor = Settings.System.getInt(getContentResolver(),
                Settings.System.BACKGROUND_APP_COLOR, 0xFF33B5E5);
        mAppBackgroundColor.setSummary(Integer.toHexString(appBackgroundColor));
        mAppBackgroundColor.setEnabled(transparentAppBackgroundPref == 1);

        int transparentFullBackgroundPref = Settings.System.getInt(getContentResolver(),
                Settings.System.TRANSPARENT_BACKGROUND_FULL, 0);
        mTransparentFullBackgroundPref = (ListPreference) prefSet.findPreference(PREF_TRANSPARENT_FULL_BACKGROUND);
        mTransparentFullBackgroundPref.setValue(String.valueOf(transparentFullBackgroundPref));
        mTransparentFullBackgroundPref.setOnPreferenceChangeListener(this);
        mTransparentFullBackgroundPref.setEnabled(transparentAppBackgroundPref == 1);

        mFontsPref = (PreferenceScreen) prefSet.findPreference(KEY_FONT_FONT_SETINGS);

        mTextGlobalOfColor = (CheckBoxPreference) prefSet.findPreference(PREF_TEXT_GLOBAL_OF_COLOR);
        mTextGlobalOfColor.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.TEXT_GLOBALOFCOLOR, 0) == 1));

	mTextFullOfColor = (Preference) prefSet.findPreference(PREF_TEXT_FULL_OF_COLOR);
        mTextFullOfColor.setOnPreferenceChangeListener(this);

        /* Overscroll Effect */
        mOverscrollPref = (ListPreference) prefSet.findPreference(OVERSCROLL_PREF);
        int overscrollEffect = Settings.System.getInt(getContentResolver(),
                Settings.System.OVERSCROLL_EFFECT, 1);
        mOverscrollPref.setValue(String.valueOf(overscrollEffect));
        mOverscrollPref.setOnPreferenceChangeListener(this);

        mOverscrollColor = (ListPreference) prefSet.findPreference(OVERSCROLL_COLOR);
        mOverscrollColor.setOnPreferenceChangeListener(this);
        int overscrollColor = Settings.System.getInt(getContentResolver(),
                Settings.System.OVERSCROLL_COLOR, 0xFF38FF00);
        mOverscrollColor.setValue(String.valueOf(overscrollColor == 0 ? 0 : 1));

        mOverscrollWeightPref = (ListPreference) prefSet.findPreference(OVERSCROLL_WEIGHT_PREF);
        int overscrollWeight = Settings.System.getInt(getContentResolver(),
                Settings.System.OVERSCROLL_WEIGHT, 5);
        mOverscrollWeightPref.setValue(String.valueOf(overscrollWeight));
        mOverscrollWeightPref.setOnPreferenceChangeListener(this);

    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String val = newValue.toString();
        if (preference == mTransparentAppBackgroundPref) {
            int transparentAppBackgroundPref = Integer.parseInt(String.valueOf(newValue));
            Settings.System.putInt(getContentResolver(), Settings.System.TRANSPARENT_BACKGROUND_APP,
                                   transparentAppBackgroundPref);
            int FullBackgroundPref = Settings.System.getInt(getContentResolver(),
                Settings.System.TRANSPARENT_BACKGROUND_FULL, 0);
            if (FullBackgroundPref == 2) {
                Settings.System.putInt(getContentResolver(), Settings.System.TRANSPARENT_BACKGROUND_FULL, 0);
            }
            mAppBackgroundColor.setEnabled(transparentAppBackgroundPref == 1);
            mTransparentFullBackgroundPref.setEnabled(transparentAppBackgroundPref == 1);
            if (transparentAppBackgroundPref == 2) {
                Intent intent = new Intent("org.openintents.action.PICK_FILE");
                intent.setData(Uri.parse("file:///sdcard/"));
                intent.putExtra("org.openintents.extra.TITLE", "CyanMobile Please select a file");
                startActivityForResult(intent, REQUEST_CODE_PICK_FILE_BG);
            }
            return true;
	} else if (preference == mTransparentFullBackgroundPref) {
            int transparentFullBackgroundPref = Integer.parseInt(String.valueOf(newValue));
            if (transparentFullBackgroundPref == 1) {
               Settings.System.putInt(getContentResolver(), Settings.System.TRANSPARENT_BACKGROUND_FULL, 1);
            } else if (transparentFullBackgroundPref == 2) {
            new AlertDialog.Builder(this)
            .setTitle("CyanMobile Notice")
            .setMessage("Force show Wallpaper background can cause a problem with some window layer\n\n (This will be disabled if having problem with that, After system Reboot.)\n\n Are you sure want to enable this feature?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                         Settings.System.putInt(getContentResolver(), Settings.System.TRANSPARENT_BACKGROUND_FULL, 2);
                    }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                         Settings.System.putInt(getContentResolver(), Settings.System.TRANSPARENT_BACKGROUND_FULL, 0);
                         return;
                    }
             })
            .show();
            } else {
               Settings.System.putInt(getContentResolver(), Settings.System.TRANSPARENT_BACKGROUND_FULL, 0);
            }
            return true;
        } else if (preference == mOverscrollPref) {
            int overscrollEffect = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.OVERSCROLL_EFFECT,
                    overscrollEffect);
            return true;
        } else if (preference == mOverscrollWeightPref) {
            int overscrollWeight = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.OVERSCROLL_WEIGHT,
                    overscrollWeight);
            return true;
        } else if (preference == mOverscrollColor){
            if (mOverscrollColor.findIndexOfValue(val)==0){
                Settings.System.putInt(getContentResolver(), Settings.System.OVERSCROLL_COLOR, 0);
            }else{
                int overscrollColor = Settings.System.getInt(getContentResolver(),
                        Settings.System.OVERSCROLL_COLOR, 0xFF38FF00);
                ColorPickerDialog cp = new ColorPickerDialog(this,mPackageColorListener,
                        overscrollColor);
                cp.show();
            }
            return true;
        }
        return false;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        /* Preference Screens */
        if (preference == mAppBackgroundColor) {
            ColorPickerDialog cp = new ColorPickerDialog(this, mBgAppColorListener, getBgAppColor());
            cp.show();
            return true;
        } else if (preference == mTextGlobalOfColor) {
            value = mTextGlobalOfColor.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.TEXT_GLOBALOFCOLOR,
                    value ? 1 : 0);
            return true;
        } else if (preference == mTextFullOfColor) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
            mTextFullOfColorListener,
            readTextFullOfColor());
            cp.show();
	    return true;
        }  else if (preference == mFontsPref) {
              final String key = preference.getKey();
              if (KEY_FONT_FONT_SETINGS.equals(key)) {
                  return false;
              }
            return true;
	}
        return false;
    }

    private int readTextFullOfColor() {
        try {
            return Settings.System.getInt(getContentResolver(), Settings.System.TEXT_FULLOFCOLOR);
        }
        catch (SettingNotFoundException e) {
            return -1;
        }
    }

    ColorPickerDialog.OnColorChangedListener mTextFullOfColorListener = 
        new ColorPickerDialog.OnColorChangedListener() {
            public void colorChanged(int color) {
                Settings.System.putInt(getContentResolver(), Settings.System.TEXT_FULLOFCOLOR, color);
            }
            public void colorUpdate(int color) {
            }
    };

    ColorPickerDialog.OnColorChangedListener mPackageColorListener = new
    ColorPickerDialog.OnColorChangedListener() {
        public void colorChanged(int color) {
            Settings.System.putInt(getContentResolver(), Settings.System.OVERSCROLL_COLOR,color);
        }
        @Override
        public void colorUpdate(int color) {
        }
    };

    private int getBgAppColor() {
        try {
            return Settings.System.getInt(getContentResolver(),
                     Settings.System.BACKGROUND_APP_COLOR);
        } catch (SettingNotFoundException e) {
            return -16777216;
        }
    }

    ColorPickerDialog.OnColorChangedListener mBgAppColorListener =
        new ColorPickerDialog.OnColorChangedListener() {
            public void colorChanged(int color) {
                Settings.System.putInt(getContentResolver(), Settings.System.BACKGROUND_APP_COLOR, color);
                mAppBackgroundColor.setSummary(Integer.toHexString(color));
            }
            public void colorUpdate(int color) {
            }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Context context = getApplicationContext();
        switch (requestCode) {
            case REQUEST_CODE_PICK_FILE_BG:
                if (resultCode == RESULT_OK && data != null) {
                    // obtain the filename
                    Uri fileUri = data.getData();
                    if (fileUri != null) {
                        String filePath = fileUri.getPath();
                        if (filePath != null) {
                            Intent mvAppBackgroundImage = new Intent();
                            mvAppBackgroundImage.setAction(MOVE_APP_BACKGROUND_INTENT);
                            mvAppBackgroundImage.putExtra("fileNames", filePath);
                            sendBroadcast(mvAppBackgroundImage);
                        }
                    }
                }
            break;
        }
    }

}
