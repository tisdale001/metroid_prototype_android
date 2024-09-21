package com.example.metroid_prototype;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Sprite {
    private boolean readLeftToRight;
    private int lastFrame;
    private int numPixelsToTrimFromWidth;
    private int srcWidth;
    private int srcHeight;
    private int destWidth;
    private int destHeight;
    private int numSpritesInRow;
    private int startX;
    private int startY;
    private int numRows;
    private Bitmap curSprite;
    private int xPos;
    private int yPos;

    private Bitmap spriteSheet;
    private Bitmap[][] spritesArr;
    private BitmapFactory.Options options = new BitmapFactory.Options();

    public Sprite(boolean readLeftToRight) {
        this.readLeftToRight = readLeftToRight;
    }

    public void setSpriteSheetDimensions(int frameWidth, int frameHeight, int startX, int startY, int numSpritesInRow, int totalSprites, int numPixelsToTrimFromWidth) {
        this.lastFrame = totalSprites - 1;
        this.numPixelsToTrimFromWidth = numPixelsToTrimFromWidth;
        this.srcWidth = frameWidth - numPixelsToTrimFromWidth;
        this.srcHeight = frameHeight;
        this.numSpritesInRow = numSpritesInRow;
        this.startX = startX;
        this.startY = startY;
        this.numRows = totalSprites / numSpritesInRow;
    }

    public void setRectangleDimensions(int width, int height) {
        this.destWidth = width;
        this.destHeight = height;
    }

    public void loadDrawable(int resID) {
        spritesArr = new Bitmap[this.numRows][this.numSpritesInRow];
        options.inScaled = false;
        spriteSheet = BitmapFactory.decodeResource(MainActivity.getGameContext().getResources(), resID, options);
        if (this.readLeftToRight) {
            for(int j = 0; j < spritesArr.length; j++) {
                for(int i = 0; i < spritesArr[j].length; i++) {
                    spritesArr[j][i] = getScaledBitmap(Bitmap.createBitmap(spriteSheet, this.startX + (i * (this.srcWidth + this.numPixelsToTrimFromWidth)),
                            this.startY + (j * this.srcHeight), this.srcWidth, this.srcHeight));
                }
            }
        } else {
            // starting Position will be upper right corner of spritesheet
            for (int j = 0; j < spritesArr.length; j++) {
                for (int i = 0; i < spritesArr[j].length; i++) {
                    spritesArr[j][i] = getScaledBitmap(Bitmap.createBitmap(spriteSheet, this.startX - ((i + 1) * (this.srcWidth + this.numPixelsToTrimFromWidth)),
                            this.startY + (j * this.srcHeight), this.srcWidth, this.srcHeight));
                }
            }
        }
    }

    private Bitmap getScaledBitmap(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, this.destWidth, this.destHeight, false);
    }

    // this updates the animation frame AND the x, y coordinate of the sprite
    public void update(int x, int y, int frame) {
        if (frame > this.lastFrame) {
            frame = 0;
        }
        // set position
        this.xPos = x;
        this.yPos = y;
        // frame 0 is first in array, no matter if it was read left-to-right or not
        int row = frame / this.numSpritesInRow;
        int col = frame % this.numSpritesInRow;
        this.curSprite = spritesArr[row][col];
    }

    public void render(Canvas c, int camOffsetX, int camOffsetY) {
        c.drawBitmap(this.curSprite, this.xPos - camOffsetX, this.yPos - camOffsetY, null);
    }

    public int getWidth() {
        return this.destWidth;
    }

    public int getHeight() {
        return this.destHeight;
    }

    public int getCenterX() {
        return this.xPos + this.destWidth / 2;
    }

    public int getCenterY() {
        return this.yPos + this.destHeight / 2;
    }
}
