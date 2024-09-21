package com.example.metroid_prototype;

public class Collision {
    private boolean isColliding;
    private int firstTileRow = 0;
    private int firstTileColumn = 0;
    private int firstTileID = 0;

    public Collision() {
        isColliding = false;
    }

    public Collision(boolean isColliding, int firstTileRow, int firstTileColumn, int firstTileID) {
        this.isColliding = isColliding;
        this.firstTileRow = firstTileRow;
        this.firstTileColumn = firstTileColumn;
        this.firstTileID = firstTileID;
    }

    public boolean getIsColliding() {
        return isColliding;
    }

    public int getFirstTileRow() {
        return firstTileRow;
    }

    public int getFirstTileColumn() {
        return firstTileColumn;
    }

    public int getFirstTileID() {
        return firstTileID;
    }

}
