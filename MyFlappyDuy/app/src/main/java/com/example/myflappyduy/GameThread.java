package com.example.myflappyduy;

import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameThread extends Thread{
    SurfaceHolder surfaceHolder;

    boolean IsRunning;

    long startTime, loopTime;
    long DELAY = 33;
    public GameThread(SurfaceHolder surfaceHolder){
        this.surfaceHolder = surfaceHolder;
        IsRunning = true;
    }
    @Override
    public void run() {
        while (IsRunning) {
            startTime = SystemClock.uptimeMillis();

            Canvas canvas = surfaceHolder.lockCanvas(null);
            if (canvas != null) {
                synchronized (surfaceHolder) {
                    AppConstants.getGameEngine().update(canvas);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            loopTime = SystemClock.uptimeMillis() - startTime;
            if (loopTime < DELAY) {
                try {
                    Thread.sleep(DELAY - loopTime);
                } catch (InterruptedException e) {
                    Log.e("Interrupted", "Interrupted while sleeping");
                }
            }
        }
    }

    public boolean isRunning() {
        return IsRunning;
    }

    public void setIsRunning(boolean state){
        IsRunning = state;
    }
}
