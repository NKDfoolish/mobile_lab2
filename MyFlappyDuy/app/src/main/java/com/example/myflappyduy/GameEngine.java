package com.example.myflappyduy;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;


public class GameEngine {

    BackgroundImage backgroundImage;
    Bird bird;
    ArrayList<Tubes> tubes;
    int score;
    private Paint scorePaint;
    private Paint scoreBackgroundPaint;
    private Rect scoreBackgroundRect;
    private int scoreX;
    private int scoreY;
    private int scorePadding;

    static int gameState;
    private Rect srcRect;
    private Rect dstRect;


    // Tube generation constants
    private static final int INITIAL_TUBE_COUNT = 3;
    private static final int TUBE_DISTANCE = AppConstants.SCREEN_WIDTH * 3/4; // Distance between tubes

    public GameEngine(){
        backgroundImage = new BackgroundImage();
        bird = new Bird();
        tubes = new ArrayList<>();
        srcRect = new Rect();
        dstRect = new Rect();

        // Initialize score
        score = 0;

        scoreX = AppConstants.SCREEN_WIDTH / 2;
        scoreY = AppConstants.SCREEN_HEIGHT / 8; // Position at top 1/8 of screen
        scorePadding = 20;

        scorePaint = new Paint();
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(100);
        scorePaint.setTextAlign(Paint.Align.CENTER);
        scorePaint.setFakeBoldText(true); // Make text bold
        scorePaint.setShadowLayer(5, 3, 3, Color.BLACK); // Add shadow for better visibility

        scoreBackgroundPaint = new Paint();
        scoreBackgroundPaint.setColor(Color.argb(120, 0, 0, 0)); // Semi-transparent black
        scoreBackgroundPaint.setStyle(Paint.Style.FILL);

        scoreBackgroundRect = new Rect();
        // 0 = game is not started
        // 1 = game is started
        // 2 = game is over
        gameState = 0;

    }
    private void initTubes() {
        tubes.clear();
        score = 0;

        // Create initial tubes
        for (int i = 0; i < INITIAL_TUBE_COUNT; i++) {
            int xPos = AppConstants.SCREEN_WIDTH + (i * TUBE_DISTANCE);
            tubes.add(new Tubes(xPos));
        }
    }


    public void updateAndDrawableBackgroundImage(Canvas canvas) {
        // More efficient background scroll implementation
        backgroundImage.SetX(backgroundImage.getX() - backgroundImage.getVelocity());
        if (backgroundImage.getX() < -AppConstants.getBitMapBank().getBackgroundWidth()) {
            backgroundImage.SetX(0);
        }

        // Draw only what's needed
        int bgWidth = AppConstants.getBitMapBank().getBackgroundWidth();
        int firstImageX = backgroundImage.getX();
        canvas.drawBitmap(AppConstants.getBitMapBank().getBackground_game(), firstImageX, backgroundImage.getY(), null);

        if (firstImageX + bgWidth < AppConstants.SCREEN_WIDTH) {
            canvas.drawBitmap(AppConstants.getBitMapBank().getBackground_game(), firstImageX + bgWidth,
                    backgroundImage.getY(), null);
        }
    }

    public void updateAndDrawBird(Canvas canvas) {
        if(gameState == 1){
            if(bird.getY() < (AppConstants.SCREEN_HEIGHT - AppConstants.getBitMapBank().getBirdHeight())
                    || bird.getVelocity() < 0){
                bird.setVelocity(bird.getVelocity() + AppConstants.gravity);
                bird.setY(bird.getY() + bird.getVelocity());
            }
        }

        canvas.drawBitmap(AppConstants.getBitMapBank().getBird(), bird.getX(), bird.getY(), null);
    }

    public void updateAndDrawTubes(Canvas canvas) {
        if (gameState == 1) {
            // Initialize tubes when game starts
            if (tubes.isEmpty()) {
                initTubes();
            }

            // Update and draw tubes
            for (int i = 0; i < tubes.size(); i++) {
                Tubes tube = tubes.get(i);
                tube.updatePosition();

                // Get tube dimensions
                int tubeWidth = AppConstants.getBitMapBank().getTubeWidth();
                int tubeHeight = AppConstants.getBitMapBank().getTubeHeight();

                // Reuse rectangle objects for top tube
                srcRect.set(0, 0, tubeWidth, tubeHeight);
                dstRect.set(
                        tube.getXPos(),
                        0,
                        tube.getXPos() + tubeWidth,
                        tube.getTopTubeY() + tubeHeight
                );
                canvas.drawBitmap(AppConstants.getBitMapBank().getTubeTop(), srcRect, dstRect, null);

                // Reuse rectangle objects for bottom tube
                srcRect.set(0, 0, tubeWidth, tubeHeight);
                dstRect.set(
                        tube.getXPos(),
                        tube.getBottomTubeY(),
                        tube.getXPos() + tubeWidth,
                        AppConstants.SCREEN_HEIGHT
                );
                canvas.drawBitmap(AppConstants.getBitMapBank().getTubeBottom(), srcRect, dstRect, null);

                // Check if bird passed the tube to increment score
                if (!tube.isScored() && tube.getXPos() + tubeWidth < bird.getX()) {
                    tube.setScored(true);
                    score++;
                }

                // Check for collision with tubes
                if (tube.collidesWith(bird)) {
                    gameState = 2; // Game over
                }
            }

            // Replace the tube generation with recycling logic
            recycleOffscreenTube();

            // Check if bird hits ground or ceiling
            if (bird.getY() <= 0 ||
                    bird.getY() >= AppConstants.SCREEN_HEIGHT - AppConstants.getBitMapBank().getBirdHeight()) {
                gameState = 2; // Game over
            }
        }


    }
    private void recycleOffscreenTube() {
        if (!tubes.isEmpty() && tubes.get(0).getXPos() < -AppConstants.getBitMapBank().getTubeWidth()) {
            // Recycle tube by moving it to the end rather than creating new one
            Tubes recycleTube = tubes.remove(0);

            // Calculate new position based on the last tube
            int newXPos = tubes.get(tubes.size() - 1).getXPos() + TUBE_DISTANCE;

            // Reset tube properties
            recycleTube.setXPos(newXPos);
            recycleTube.regenerateYPositions();
            recycleTube.setScored(false);

            // Add recycled tube at the end
            tubes.add(recycleTube);
        }
    }

    public void drawScore(Canvas canvas) {
        if (gameState == 1 || gameState == 2) {
            String scoreText = String.valueOf(score);

            // Measure text dimensions
            Rect textBounds = new Rect();
            scorePaint.getTextBounds(scoreText, 0, scoreText.length(), textBounds);

            // Set background rectangle
            scoreBackgroundRect.set(
                    scoreX - textBounds.width()/2 - scorePadding,
                    scoreY - textBounds.height() - scorePadding,
                    scoreX + textBounds.width()/2 + scorePadding,
                    scoreY + scorePadding
            );

            // Draw background for better visibility
            canvas.drawRoundRect(
                    scoreBackgroundRect.left,
                    scoreBackgroundRect.top,
                    scoreBackgroundRect.right,
                    scoreBackgroundRect.bottom,
                    20, 20, // Corner radius
                    scoreBackgroundPaint
            );

            // Draw score text on top
            canvas.drawText(scoreText, scoreX, scoreY, scorePaint);
        }
    }

    public void update(Canvas canvas) {
        // Update and draw game elements
        updateAndDrawableBackgroundImage(canvas);
        updateAndDrawTubes(canvas);
        updateAndDrawBird(canvas);
        drawScore(canvas);
    }

    public void reset() {
        bird.reset();
        tubes.clear();
        score = 0;
        gameState = 0;
    }
}
