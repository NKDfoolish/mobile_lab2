package com.example.myflappyduy;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitMapBank {
    Bitmap background_game;
    Bitmap bird;
    Bitmap tubeTop, tubeBottom;

    public BitMapBank(Resources res) {
        // Create bitmap loading options to reduce memory usage
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565; // Uses less memory than ARGB_8888
        options.inSampleSize = 1; // No downsampling by default

        // Load background with options
        background_game = BitmapFactory.decodeResource(res, R.drawable.background_game, options);
        background_game = scaleImage(background_game);

        // Load bird with options
        bird = BitmapFactory.decodeResource(res, R.drawable.bird_frame1, options);
        bird = scaleBirdImage(bird);

        // Load tubes with options
        tubeTop = BitmapFactory.decodeResource(res, R.drawable.tube_top, options);
        tubeBottom = BitmapFactory.decodeResource(res, R.drawable.tube_bottom, options);
        tubeTop = scaleTubeImage(tubeTop);
        tubeBottom = scaleTubeImage(tubeBottom);
    }

    public Bitmap scaleTubeImage(Bitmap bitmap) {
        int tubeWidth = AppConstants.SCREEN_WIDTH / 6; // Adjust width as needed
        int tubeHeight = (int)(bitmap.getHeight() * ((float)tubeWidth / bitmap.getWidth()));
        return Bitmap.createScaledBitmap(bitmap, tubeWidth, tubeHeight, false);
    }

    // Getter methods for tubes
    public Bitmap getTubeTop() {
        return tubeTop;
    }

    public Bitmap getTubeBottom() {
        return tubeBottom;
    }

    public int getTubeWidth() {
        return tubeTop.getWidth();
    }

    public int getTubeHeight() {
        return tubeTop.getHeight();
    }

    // Return bird bitmap
    public Bitmap getBird() {
        return bird;
    }

    public int getBirdWidth(){
        return bird.getWidth();
    }

    public int getBirdHeight(){
        return bird.getHeight();
    }

    // return background bitmap
    public Bitmap getBackground_game(){
        return background_game;
    }

    // return background width
    public int getBackgroundWidth(){
        return background_game.getWidth();
    }

    // return background height
    public int getBackgroundHeight(){
        return background_game.getHeight();
    }

    public Bitmap scaleImage(Bitmap bitmap) {
        float widthHeightRatio = bitmap.getWidth() / (float) bitmap.getHeight();
        int backgroundScaleWidth = (int) (widthHeightRatio * AppConstants.SCREEN_HEIGHT);
        return Bitmap.createScaledBitmap(bitmap, backgroundScaleWidth, AppConstants.SCREEN_HEIGHT, false);
    }

    public Bitmap scaleBirdImage(Bitmap bitmap) {
        // Scale down by a factor (e.g., 0.1 makes it 10% of original size)
        float scaleFactor = 0.1f;
        int newWidth = (int) (bitmap.getWidth() * scaleFactor);
        int newHeight = (int) (bitmap.getHeight() * scaleFactor);
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
    }
}