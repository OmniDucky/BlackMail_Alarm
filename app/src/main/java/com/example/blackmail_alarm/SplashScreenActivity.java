package com.example.blackmail_alarm;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EasySplashScreen config = new EasySplashScreen(SplashScreenActivity.this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(3000)
                .withBackgroundColor(Color.parseColor("#1a1b29"))
                .withFooterText("Copyright 2016")
                .withBeforeLogoText("Black Mail Alarm")
                .withLogo(R.mipmap.ic_launcher_round)
                .withAfterLogoText("Wake you up");
        config.getBeforeLogoTextView().setTextColor(Color.WHITE);
        config.getFooterTextView().setTextColor(Color.WHITE);
        config.getAfterLogoTextView().setTextColor(Color.WHITE);
        config.getBeforeLogoTextView().setTextColor(Color.WHITE);
        View easySplashScreenView = config.create();
        setContentView(easySplashScreenView);
    }
}
