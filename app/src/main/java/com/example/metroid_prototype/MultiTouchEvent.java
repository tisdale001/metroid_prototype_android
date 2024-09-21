package com.example.metroid_prototype;

public class MultiTouchEvent {
    public int pointerCount;
    public float[] touchX;
    public float[] touchY;
    public int[] action;

    public MultiTouchEvent(int pointerCount) {
        this.pointerCount = pointerCount;
        touchX = new float[pointerCount];
        touchY = new float[pointerCount];
        action = new int[pointerCount];
    }

    public void setPointerData(int index, float x, float y, int action) {
        if (index >= 0 && index < pointerCount) {
            this.touchX[index] = x;
            this.touchY[index] = y;
            this.action[index] = action;
        }
    }
}
