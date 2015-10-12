package com.playtang.commonnavigation;

/**
 * Created by 310131737 on 8/28/2015.
 */

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreen extends SplasLoginActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash_screen);

        /*

        new Handler().postDelayed(new Runnable() {

			*//*
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 *//*

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, Navigation.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);*/
    }


}
