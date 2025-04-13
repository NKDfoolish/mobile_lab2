package com.example.myflappyduy;

public class Bird {
    private int birdX, birdY, velocity;

    public Bird(){
        birdX = AppConstants.SCREEN_WIDTH / 2 - AppConstants.getBitMapBank().getBirdWidth()/2;
        birdY = AppConstants.SCREEN_HEIGHT / 2 - AppConstants.getBitMapBank().getBirdHeight()/2;
        velocity = 0;
    }

    public void reset() {
        birdY = AppConstants.SCREEN_HEIGHT / 2;
        velocity = 0;
    }

    // getter method for velocity
    public int getVelocity() {
        return velocity;
    }

    // setter method for velocity
    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    //get method for getting X - coordinate of bird
    public int getX() {
        return birdX;
    }

    //get method for getting Y - coordinate of bird
    public int getY() {
        return birdY;
    }

    // set method for setting X - coordinate of bird
    public void setX(int birdX) {
        this.birdX = birdX;
    }

    // set method for setting Y - coordinate of bird
    public void setY(int birdY) {
        this.birdY = birdY;
    }
}