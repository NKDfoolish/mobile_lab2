package com.example.myflappyduy;

import java.util.Random;

public class Tubes {
    private int xPos;
    private int topTubeY;
    private int tubeGap;
    private Random random;
    private int tubeVelocity;
    private boolean isScored;

    public Tubes(int xPos) {
        this.xPos = xPos;
        random = new Random();

        // Define the gap between tubes
        tubeGap = AppConstants.SCREEN_HEIGHT / 4;

        // Giới hạn phạm vi hợp lý cho topTubeY
        int tubeHeight = AppConstants.getBitMapBank().getTubeHeight();
        int minTopTubeY = -tubeHeight / 2;
        int maxTopTubeY = AppConstants.SCREEN_HEIGHT - tubeHeight - tubeGap - tubeHeight / 2;

        // Sinh vị trí ngẫu nhiên cho ống trên
        topTubeY = minTopTubeY + random.nextInt(maxTopTubeY - minTopTubeY + 1);

        tubeVelocity = 12;
        isScored = false;
    }

    public void updatePosition() {
        xPos -= tubeVelocity;
    }

    public int getXPos() {
        return xPos;
    }

    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    public int getTopTubeY() {
        return topTubeY;
    }

    public int getBottomTubeY() {
        return topTubeY + AppConstants.getBitMapBank().getTubeHeight() + tubeGap;
    }

    public boolean isScored() {
        return isScored;
    }

    public void setScored(boolean scored) {
        isScored = scored;
    }

    public void regenerateYPositions() {
        // Define the gap between tubes
        tubeGap = AppConstants.SCREEN_HEIGHT / 4;

        // Set logical limits for topTubeY
        int tubeHeight = AppConstants.getBitMapBank().getTubeHeight();
        int minTopTubeY = -tubeHeight / 2;
        int maxTopTubeY = AppConstants.SCREEN_HEIGHT - tubeHeight - tubeGap - tubeHeight / 2;

        // Generate random position for top tube
        topTubeY = minTopTubeY + random.nextInt(maxTopTubeY - minTopTubeY + 1);
    }

    public boolean collidesWith(Bird bird) {
        if (bird.getX() + AppConstants.getBitMapBank().getBirdWidth() > xPos &&
                bird.getX() < xPos + AppConstants.getBitMapBank().getTubeWidth()) {
            if (bird.getY() < topTubeY + AppConstants.getBitMapBank().getTubeHeight()) {
                return true;
            }
            if (bird.getY() + AppConstants.getBitMapBank().getBirdHeight() > getBottomTubeY()) {
                return true;
            }
        }
        return false;
    }
}