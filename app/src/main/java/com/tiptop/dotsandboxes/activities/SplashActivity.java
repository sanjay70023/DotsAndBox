package com.tiptop.dotsandboxes.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.tiptop.dotsandboxes.R;
import com.tiptop.dotsandboxes.utils.AppLovinAds;
import com.tiptop.dotsandboxes.utils.Constants;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    private Timer timer;
    //timer task to load main activity after specified time
    private TimerTask loadNextActivity = new TimerTask() {
        @Override
        public void run() {
            Intent nextActivity;
            SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME_GENERAL, Context.MODE_PRIVATE);
            nextActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(nextActivity);
            finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        init();

    }

    /**
     * This function provide to load gif and perform timer task after gif loaded.
     */
    private void init() {

        timer = new Timer();
        timer.schedule(loadNextActivity, getResources().getInteger(R.integer.splash_duration));
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
