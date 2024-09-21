package com.example.metroid_prototype;


import android.graphics.Bitmap;

public class Tile {
    private int ID = -1;
    private Bitmap image;

    public Tile(Bitmap scaledImage) {
        this.image = scaledImage;
    }

    public void setID(int id) {
        this.ID = id;
    }

    public int getID() {
        return this.ID;
    }

    public Bitmap getImage() {
        return this.image;
    }
}