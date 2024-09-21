package com.example.metroid_prototype;

import com.example.metroid_prototype.helpers.Input;

public class TouchInput {
    public float x;
    public float y;
    public Input input;

    public TouchInput(Input input, float x, float y) {
        this.input = input;
        this.x = x;
        this.y = y;
    }
}
