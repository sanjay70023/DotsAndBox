package com.tiptop.dotsandboxes.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tiptop.dotsandboxes.R;
import com.tiptop.dotsandboxes.services.MusicPlayerService;
import com.tiptop.dotsandboxes.services.MusicService;
import com.tiptop.dotsandboxes.utils.PrefUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

/**
 * This activity is not used
 */
public class MusicEnabledActivity extends AppCompatActivity {

    public MusicService mService;
    public PrefUtils prefUtils;
    public boolean doubleBackToExitPressedOnce;
    public ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((MusicService.MusicBinder) service).getService();

            // start playing music if the user specified so in the settings screen
            boolean playMusic = PreferenceManager.getDefaultSharedPreferences(MusicEnabledActivity.this).getBoolean(getString(R.string.pref_key_music), true);

            if (playMusic) {
                mService.sendCommand(new Intent(MusicPlayerService.ACTION_START_MUSIC));
            } else {
                mService.sendCommand(new Intent(MusicPlayerService.ACTION_STOP_MUSIC));
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            mService.sendCommand(new Intent(MusicService.ACTION_STOP_MUSIC));
            mService = null;
        }
    };
    private boolean mBoundMusicService = false;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        prefUtils = new PrefUtils(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        doBindService();
    }

    @Override
    protected void onStop() {
        doUnbindService();
        super.onStop();
    }

    void doBindService() {
        Intent intent = new Intent(this, MusicService.class);

        // start playing music if the user specified so in the settings screen
        if (!mBoundMusicService) {
            boolean playMusic = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_key_music), false);
            if (playMusic) {
                intent.setAction(MusicService.ACTION_START_MUSIC);
            } else {
                intent.setAction(MusicService.ACTION_STOP_MUSIC);
            }

            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            mBoundMusicService = true;
        }
    }

    void doUnbindService() {
        if (mBoundMusicService) {
            unbindService(mConnection);
            mBoundMusicService = false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    public void setImageToImageView(String imageUrl, ImageView imageView) {
        Glide.with(this)
                .load(imageUrl).apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }

    public void setImageToImageViewFromResource(int imageUrl, ImageView imageView) {
        Glide.with(this)
                .load(imageUrl).apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }
}
