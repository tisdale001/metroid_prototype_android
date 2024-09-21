package com.example.metroid_prototype;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LevelReader {
    private int rows;
    private int cols;
    private String tileSetName;
    private ArrayList<Integer> tileIDArr = new ArrayList<>();

    public LevelReader(Context context, String fileName) {
        loadLevel(context, fileName);
    }

    public void loadLevel(Context context, String fileName) {
        AssetManager assetManager = context.getAssets();

        try (InputStream inputStream = assetManager.open(fileName);
             BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream))) {

            String[] dimensions = fileReader.readLine().split(" ");
            rows = Integer.parseInt(dimensions[0]);
            cols = Integer.parseInt(dimensions[1]);
            tileSetName = dimensions[2];
            tileIDArr.clear();
            String line;
            while ((line = fileReader.readLine()) != null) {
                String[] tileTypes = line.trim().split("\\s+");
                for (String tileType : tileTypes) {
                    tileIDArr.add(Integer.parseInt(tileType));
                }
            }
            Log.d("GamePanel", "end of loadLevel()");

        } catch (IOException e) {
            Log.d("GamePanel", String.format("Failed to open level file: %s", fileName));
            System.exit(1);
        } catch (Exception e) {
            Log.d("GamePanel", String.format("Failed to open level file: %s", fileName));
            System.exit(1);
        }
    }

    public int getRows() {
        return this.rows;
    }
    public int getCols() {
        return this.cols;
    }
    public String getTileSetName() {
        return this.tileSetName;
    }

    public ArrayList<Integer> getTileIDArr() {
        return this.tileIDArr;
    }

}
