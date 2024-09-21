package com.example.metroid_prototype;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static Context gameContext;
    private WindowManager windowManager;
    private DisplayMetrics displayMetrics;
    private int windowWidth;
    private int windowHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameContext = this;
        EdgeToEdge.enable(this);
        // Keep the screen on; not sure this does anything
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hideSystemUI();

        // Use the decorView's post method to calculate the window size after the UI has been adjusted
        View decorView = getWindow().getDecorView();
        decorView.post(new Runnable() {
            @Override
            public void run() {
                calculateWindowSize();
                startGamePanel();
            }
        });
    }

    // Hides the system UI for full-screen experience
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    // Restores the system UI when needed
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void calculateWindowSize() {
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        this.windowWidth = displayMetrics.widthPixels;
        this.windowHeight = displayMetrics.heightPixels;
        Log.d("Main", String.format("windowWidth = %d, windowHeight = %d", this.windowWidth, this.windowHeight));
    }

    private void startGamePanel() {
        setContentView(new GamePanel(this));
    }

    public static Context getGameContext() {
        return gameContext;
    }

    public int getWindowWidth() {
        return this.windowWidth;
    }

    public int getWindowHeight() {
        return this.windowHeight;
    }
}