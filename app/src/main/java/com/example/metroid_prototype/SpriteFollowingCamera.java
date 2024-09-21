package com.example.metroid_prototype;

public class SpriteFollowingCamera {
    private int cameraWidth;
    private int cameraHeight;
    private int levelWidth;
    private int levelHeight;
    private Player target;  // The sprite that the camera follows
    private int x;
    private int y;

    public SpriteFollowingCamera(int camWidth, int camHeight, int lvlWidth, int lvlHeight, Player player) {
        this.cameraWidth = camWidth;
        this.cameraHeight = camHeight;
        this.levelWidth = lvlWidth;
        this.levelHeight = lvlHeight;
        this.target = player;

        // Calculate initial camera position with clamping
        this.x = clamp(target.getPlayerObj().getSprite().getCenterX() - cameraWidth / 2, 0, levelWidth - cameraWidth);
        this.y = clamp(target.getPlayerObj().getSprite().getCenterY() - cameraHeight / 2, 0, levelHeight - cameraHeight);
    }

    // Method to clamp a value within a specified range
    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    // Method to update the camera position based on the target
    public void update() {
        // Update x position, clamping to ensure the camera doesn't go out of bounds
        this.x = clamp(target.getPlayerObj().getSprite().getCenterX() - cameraWidth / 2, 0, levelWidth - cameraWidth);
        // You can update y similarly if you want vertical movement as well
        this.y = clamp(target.getPlayerObj().getSprite().getCenterY() - cameraHeight / 2, 0, levelHeight - cameraHeight);
    }

    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }
}
