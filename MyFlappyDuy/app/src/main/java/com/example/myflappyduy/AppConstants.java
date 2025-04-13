package com.example.myflappyduy;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class AppConstants {

    static BitMapBank bitMapBank;

    static int SCREEN_WIDTH, SCREEN_HEIGHT;

    static int gravity;

    static int VELOCITY_WHEN_JUMPED;

    static GameEngine gameEngine;

    public static void initialization(Context context){
        setScreenSize(context);
        bitMapBank = new BitMapBank(context.getResources());
        gameEngine = new GameEngine();

        AppConstants.gravity = 3;
        AppConstants.VELOCITY_WHEN_JUMPED = -40;
    }

    public static BitMapBank getBitMapBank(){
        return bitMapBank ;
    }

    public static GameEngine getGameEngine(){
        return gameEngine;
    }

    public static void setScreenSize(Context context){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        AppConstants.SCREEN_WIDTH = width;
        AppConstants.SCREEN_HEIGHT = height;
    }
}
