package com.example.metroid_prototype;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;

public class TileMapComponent {

    private int rows;
    private int cols;
    private String tileSetName;
    private int scaledWidth;
    private int scaledHeight;
    private ArrayList<Integer> tileIDArr;
    private ArrayList<Tile> fgTileArr = new ArrayList<>();
    private ArrayList<Tile> bgTileArr = new ArrayList<>();


    public TileMapComponent(Context context, String fileName, ArrayList<TileSet> fgTileSetArr, ArrayList<TileSet> bgTileSetArr, int scaledWidth, int scaledHeight) {

        LevelReader levelReader = new LevelReader(context, fileName);
        // rows and cols of the level
        this.rows = levelReader.getRows();
        this.cols = levelReader.getCols();
        this.tileSetName = levelReader.getTileSetName();
        this.scaledWidth = scaledWidth;
        this.scaledHeight = scaledHeight;
        this.tileIDArr = levelReader.getTileIDArr();
        loadTileSets(fgTileSetArr, bgTileSetArr);
    }

    private void loadTileSets(ArrayList<TileSet> fgTileSetArr, ArrayList<TileSet> bgTileSetArr) {
        createForeGroundSet(fgTileSetArr);
        createBackGroundSet(bgTileSetArr);
    }

    private void createForeGroundSet(ArrayList<TileSet> fgTileSetArr) {
        int counter = 100;
        for (TileSet tileSet : fgTileSetArr) {
            for (Tile tile : tileSet.getTileArr()) {
                tile.setID(counter);
                this.fgTileArr.add(tile);
                counter++;
            }
        }
    }

    private void createBackGroundSet(ArrayList<TileSet> bgTileSetArr) {
        int counter = -100;
        for (TileSet tileSet : bgTileSetArr) {
            for (Tile tile : tileSet.getTileArr()) {
                tile.setID(counter);
                this.bgTileArr.add(tile);
                counter--;
            }
        }
    }


    public void render(Canvas c, int camOffsetX, int camOffsetY) {
        int tileIdx = 0;
        for (int y = 0; y < rows * scaledHeight; y += scaledHeight) {
            for (int x = 0; x < cols * scaledWidth; x += scaledWidth) {
                int tileType = tileIDArr.get(tileIdx);
                if (tileType >= 0) {
                    c.drawBitmap(fgTileArr.get(tileType - 100).getImage(), x - camOffsetX, y - camOffsetY, null);
                } else if (tileType < -1) {
                    c.drawBitmap(bgTileArr.get((tileType + 100) * (-1)).getImage(), x - camOffsetX, y - camOffsetY, null);
                }
                tileIdx++;
            }
        }
    }

    public Collision isTouchingGround(GameObject o) {
        Collision collision = new Collision();
        if (o.getSprite() != null) {
            Sprite r = o.getSprite();
            int leftTile = (o.getTransformComponent().getXPos()) / scaledWidth;
            int rightTile = (o.getTransformComponent().getXPos() + r.getWidth()) / scaledWidth;
            int belowTile = (o.getTransformComponent().getYPos() + r.getHeight() + 1) / scaledHeight;
            for (int col = leftTile; col <= rightTile; col++) {
                int tileIdx = tileAt(belowTile, col);
                if (tileIdx >= 0) {
                    if (!collision.getIsColliding()) {
                        collision = new Collision(true, belowTile, col, tileIdx);
                        return collision;
                    }
                }
            }
        }
        return collision;
    }

    public Collision isTouchingCeiling(GameObject o) {
        Collision collision = new Collision();
        if (o.getSprite() != null) {
            Sprite r = o.getSprite();
            int leftTile = (o.getTransformComponent().getXPos()) / scaledWidth;
            int rightTile = (o.getTransformComponent().getXPos() + r.getWidth()) / scaledWidth;
            int aboveTile = (o.getTransformComponent().getYPos()) / scaledHeight;
            for (int col = leftTile; col <= rightTile; col++) {
                int tileIdx = tileAt(aboveTile, col);
                if (tileIdx >= 0) {
                    if (!collision.getIsColliding()) {
                        collision = new Collision(true, aboveTile, col, tileIdx);
                        return collision;
                    }
                }
            }
        }
        return collision;
    }

    public Collision isTouchingRightWall(GameObject o) {
        Collision collision = new Collision();
        if (o.getSprite() != null) {
            Sprite r = o.getSprite();
            int topTile = (o.getTransformComponent().getYPos()) / scaledHeight;
            int bottomTile = (o.getTransformComponent().getYPos() + r.getHeight() - 1) / scaledHeight;
            int rightTile = (o.getTransformComponent().getXPos() + r.getWidth() + 1) / scaledWidth;
            for (int row = topTile; row <= bottomTile; row++) {
                if (rightTile < getCols()) {
                    int tileIdx = tileAt(row, rightTile);
                    if (tileIdx >= 0) {
                        if (!collision.getIsColliding()) {
                            collision = new Collision(true, row, rightTile, tileIdx);
                            return collision;
                        }
                    }
                }
            }
        }
        return collision;
    }

    public Collision isTouchingLeftWall(GameObject o) {
        Collision collision = new Collision();
        if (o.getSprite() != null) {
            Sprite r = o.getSprite();
            int topTile = o.getTransformComponent().getYPos() / scaledHeight;
            int bottomTile = (o.getTransformComponent().getYPos() + r.getHeight() - 1) / scaledHeight;
            int leftTile = (o.getTransformComponent().getXPos() - 1) / scaledWidth;
            for (int row = topTile; row <= bottomTile; row++) {
                if (leftTile >= 0) {
                    int tileIdx = tileAt(row, leftTile);
                    if (tileIdx >= 0) {
                        if (!collision.getIsColliding()) {
                            collision = new Collision(true, row, leftTile, tileIdx);
                            return collision;
                        }
                    }
                }
            }
        }
        return collision;
    }

    public int tileAt(int row, int col) {
        int tileIdx = getTileIdx(row, col);
        if (tileIdx >= 0) {
            return tileIDArr.get(tileIdx);
        }
        return -1;
    }

    public int tileAtXY(int x, int y) {
        int col = x / this.scaledWidth;
        int row = y / this.scaledHeight;
        return tileAt(row, col);
    }

    public int getTileIdx(int row, int col) {
        if (isValidTile(row, col)) {
            return row * this.cols + col;
        }
        return -1;
    }

    public boolean isValidTile(int row, int col) {
        return row >= 0 && row < this.rows && col >= 0 && col < this.cols;
    }

    public int getRows() {
        return this.rows;
    }
    public int getCols() {
        return this.cols;
    }
    public int getWidth() {
        return this.scaledWidth;
    }
    public int getHeight() {
        return this.scaledHeight;
    }

}
