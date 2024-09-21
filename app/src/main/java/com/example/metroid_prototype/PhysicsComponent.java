package com.example.metroid_prototype;

import android.util.Log;

public class PhysicsComponent {
    private int xVel;
    private int yVel;

    public void updateX(GameObject o) {
        TransformComponent temp = o.getTransformComponent();
        temp.setXPos(temp.getXPos() + xVel);
        Log.d("GamePanel", String.format("updateX() xVel = %d", xVel));
    }

    public void updateY(GameObject o) {
        TransformComponent temp = o.getTransformComponent();
        temp.setYPos(temp.getYPos() + yVel);
        Log.d("GamePanel", String.format("updateY() yVel = %d", yVel));
    }

    public int getXVel() {
        return this.xVel;
    }

    public int getYVel() {
        return this.yVel;
    }

    public void setXVel(int xVel) {
        this.xVel = xVel;
    }

    public void setYVel(int yVel) {
        this.yVel = yVel;
    }

}
