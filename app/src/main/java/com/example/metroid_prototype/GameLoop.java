package com.example.metroid_prototype;

import android.util.Log;
import android.view.MotionEvent;

public class GameLoop implements Runnable {
    private Thread gameThread;
    private GamePanel gamePanel;

    private long startTime;
    private long targetFPS = 50; // return to 50
    private long minTicksPerFrame = 1000 / targetFPS;

    public GameLoop(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        gameThread = new Thread(this);
    }

    @Override
    public void run() {

        while (true) {
            startTime = System.currentTimeMillis();

            gamePanel.processInput();

            gamePanel.update();
            gamePanel.render();

            long frameTicks = System.currentTimeMillis() - startTime;
            if (frameTicks < minTicksPerFrame) {
                try {
                    gameThread.sleep(minTicksPerFrame - frameTicks);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void startGameLoop() {
        gameThread.start();
    }

}

