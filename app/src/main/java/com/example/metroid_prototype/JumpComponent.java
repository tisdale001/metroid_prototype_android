package com.example.metroid_prototype;

import android.util.Log;

public class JumpComponent {
    private boolean isJumping = false;
    private float xVelocity;
    private int x;
    private int y;
    private int height;
    private int initY;
    private int initX;
    private float upVelocity;
    private float gravity;
    private int distance;
    private int initHeight;
    private int initTotalDistance;
    private float initXVelocity;
    private float initGravityFactor;
    private boolean hasHitCeiling = false;
    private int numTicksPerChange;
    private int maxFallingSpeed;
    private int velTicks = 0;

    public JumpComponent(int height, int totalDistance, float xVelocity, int gravityFactor) {
        this.initHeight = height;
        this.height = height;
        this.initTotalDistance = totalDistance;
        this.distance = totalDistance / 2;
        this.initXVelocity = xVelocity;
        this.initGravityFactor = gravityFactor;
        this.gravity = (float) (gravityFactor * height) / (distance * distance);
        this.upVelocity = (float) (2 * height) / distance;
    }

    private void Initiate(int x, int y) {
        this.initX = x;
        this.initY = y;
        this.x = 0;
        this.y = y;
        this.xVelocity = Math.abs(this.initXVelocity);
    }

    public void update(GameObject obj) {
        if (obj.getInitiateJump()) {
            isJumping = true;
            Initiate(obj.getTransformComponent().getXPos(), obj.getTransformComponent().getYPos());
            obj.setInitiateJump(false);
        }
        if (!isJumping) {
            return;
        }
        // this is only for vertical, upward jumping
        // add modification for downward jump (flying enemies)
        if (this.hasHitCeiling) {
            velTicks++;
            if (velTicks > numTicksPerChange) {
                velTicks = 0;
                obj.getPhysicsComponent().setYVel(obj.getPhysicsComponent().getYVel() + 1);
                if (obj.getPhysicsComponent().getYVel() >= maxFallingSpeed) {
                    this.endJump();
                }
            }
            return;
        }
        x += (int) xVelocity;
        y = (int) (0.5 * gravity * x * x + upVelocity * x + initY);
        if ((height <= 0 && y > initY) || (height > 0 && y < initY)) {
            this.endJump();
        }
        Log.d("GamePanel", String.format("Setting yVel to: %d", y - obj.getTransformComponent().getYPos()));
        obj.getPhysicsComponent().setYVel(y - obj.getTransformComponent().getYPos());
    }

    public void changeInitialY(int delta) {
        if (isJumping) {
            initY += delta;
        }
    }

    public void endJump() {
        isJumping = false;
        hasHitCeiling = false;
    }

    public void hitCeiling(GameObject obj, int numTicksPerChange, int maxFallingSpeed) {
        if (this.hasHitCeiling) {
            return;
        }
        this.hasHitCeiling = true;
        this.numTicksPerChange = numTicksPerChange;
        this.maxFallingSpeed = maxFallingSpeed;
        obj.getPhysicsComponent().setYVel(0);
//        // works for both positive and negative heights
//        this.height = obj.getTransformComponent().getYPos() - this.initY;
//        // Ensure height is not zero to prevent division by zero
//        if (this.height == 0) {
//            this.height = 1; // Small adjustment to ensure it’s non-zero
//        }
//        this.distance = this.x - this.initX;
//        // Ensure distance is not zero to prevent division by zero
//        if (this.distance == 0) {
//            this.distance = 1; // Small adjustment to ensure it’s non-zero
//        }
////        this.gravity = (float) (this.initGravityFactor * (this.height)) / (this.distance * this.distance);
//        this.upVelocity = (float) (2 * (this.height)) / this.distance;
    }

    public boolean stillJumping() {
        return isJumping;
    }
}
