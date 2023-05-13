package com.tiptop.dotsandboxes.fragments;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tiptop.dotsandboxes.R;
import com.tiptop.dotsandboxes.activities.MainActivity;
import com.tiptop.dotsandboxes.services.MusicPlayerService;

import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat /*implements Preference.OnPreferenceChangeListener*/ {

    private MusicPlayerService mService;
    private boolean serviceBound = false;
    private  Context context;
    private Activity activity;
    /**
     * Class for interacting with the game interface of the service.
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusicPlayerService.LocalBinder binder = (MusicPlayerService.LocalBinder) service;
            mService = binder.getService();
            serviceBound = true;

            Intent intent = new Intent(getActivity(), MusicPlayerService.class);

            boolean playMusic = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(getString(R.string.pref_key_music), true);

            if (playMusic) {
                intent.setAction(MusicPlayerService.ACTION_START_MUSIC);
            } else {
                intent.setAction(MusicPlayerService.ACTION_STOP_MUSIC);
            }

            getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            getActivity().startService(new Intent(MusicPlayerService.ACTION_STOP_MUSIC));
            serviceBound = false;
        }
    };

    public SettingsFragment() {
        // Required empty public constructor
    }

    void doBindService() {
        Intent intent = new Intent(getActivity(), MusicPlayerService.class);

        // start playing music if the user specified so in the settings screen
        boolean playMusic = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(getString(R.string.pref_key_music), true);
        if (playMusic) {
            intent.setAction(MusicPlayerService.ACTION_START_MUSIC);
        } else {
            intent.setAction(MusicPlayerService.ACTION_STOP_MUSIC);
        }

        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        serviceBound = true;
    }

    void doUnbindService() {
        if (serviceBound) {
            getActivity().unbindService(serviceConnection);
            serviceBound = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackground(getResources().getDrawable(R.drawable.blueback));
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(70,50,70,70);
        view.setLayoutParams(layoutParams);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);
        ((MainActivity) getActivity()).commonToolbar.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).ivToolbarBack.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).ivToolbarMusic.setVisibility(View.GONE);
        ((MainActivity) getActivity()).ivToolbarSound.setVisibility(View.GONE);
        ((MainActivity) getActivity()).ivToolbarVibrate.setVisibility(View.GONE);
        ((MainActivity) getActivity()).ivToolbarFacebook.setVisibility(View.GONE);
      //  ((MainActivity) getActivity()).frameContainer.setBackgroundColor(getActivity().getResources().getColor(R.color.icon_color_white));
        ((MainActivity) getActivity()).tvToolbarTitle.setText(getString(R.string.settings_screen));
        ((MainActivity) getActivity()).tvToolbarTitle.setVisibility(View.VISIBLE);
        findPreference(getString(R.string.pref_key_music)).setOnPreferenceChangeListener((preference, newValue) -> {
            Timber.e("onPreferenceChange: " + preference.getKey() + "  " + newValue.toString());

            if (preference.getKey().equals(getString(R.string.pref_key_music))) {
                if ((boolean) newValue) {
                    Intent intent = new Intent(getActivity(), MusicPlayerService.class);
                    intent.setAction(MusicPlayerService.ACTION_START_MUSIC);
                    mService.sendCommand(intent);
                } else {
                    Intent intent = new Intent(getActivity(), MusicPlayerService.class);
                    intent.setAction(MusicPlayerService.ACTION_STOP_MUSIC);
                    mService.sendCommand(intent);
                }
            }
            return true;
        });
        findPreference(getString(R.string.pref_key_music)).setPersistent(true);
        findPreference(getString(R.string.pref_key_sound)).setPersistent(true);
        findPreference(getString(R.string.pref_key_vibrate)).setPersistent(true);
        findPreference(getString(R.string.pref_key_privacy_policy)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (preference.getKey().equals(getString(R.string.pref_key_privacy_policy))) {
                    ((MainActivity) getActivity()).replaceFragment(new PrivacyFragment(), false);
                }
                return true;
            }
        });
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

   /* @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {


        return true;
    }*/

    @Override
    public void onStart() {
        super.onStart();
        doBindService();
    }

    @Override
    public void onStop() {
        doUnbindService();
        super.onStop();
    }


}
