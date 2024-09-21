package com.example.metroid_prototype;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.example.metroid_prototype.helpers.Input;
import com.example.metroid_prototype.helpers.PlayerState;

import java.util.ArrayList;
import java.util.List;


// TODO: handlePlayerShooting(), put in update() ??
// TODO: createBullet()
// TODO: might need a waitOneFrameForBullet, boolean
// TODO: setPositionAndVelocityOfBullet(bullet), switch statement based on sprite

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;
    private GameLoop gameLoop;
    private MainActivity mainActivity;
    private static final int MAX_TOUCH_POINTS = 2;
    private MultiTouchEvent latestTouchEvent;
    private final Object touchEventLock = new Object();
    private ArrayList<TouchInput> inputArr = new ArrayList<TouchInput>();
    private int windowWidth;
    private int windowHeight;

    private Sprite triPadSprite;
    private int triPadWidth;
    private int triPadHeight;
    private int triPadStartXPos;
    private int capsuleOffset;
    private int capsuleWidth;
    private int capsuleHeight;
    private int capsuleStartXPos;
    private int capsuleStartYPos;
    private Sprite capsuleSprite;
    private float downX1;
    private float downY1;
    private float downX2;
    private float downY2;
    private float downX3;
    private float downY3;
    private int jumpAndFireOffset;
    //    private int rightAndLeftOffset;
    private float thumbRadius;

    // Control Colors
    private final int controlAlpha = 125;
    private final int upRightColor = Color.argb(controlAlpha, 138, 43, 226);
    //    private int downLeft = Color.argb(controlAlpha, 150, 123, 182);
//    private int downRight = Color.argb(controlAlpha, 199, 21, 133);
    private final int upLeftColor = Color.argb(controlAlpha, 199, 21, 133);
    private final int downColor = Color.argb(controlAlpha, 102, 152, 153);
    private final int jumpColor = Color.argb(controlAlpha, 138, 43, 226);
    private final int fireColor = Color.argb(controlAlpha, 199, 21, 133);

    //Player variables
    private Player player;
    private int playerStartXPos;
    private int playerStartYPos;
    private int playerRunSpeed = 10;
    private int playerFallingSpeed = 14;
    private int playerClimbingSpeed = 8;
    private int aniTick = 0;
    private int aniSpeed = 4;
    private int playerFrame = 0;
    private boolean isFirstRollingFrame = false;
    private boolean waitOneCycleForJumpUpdate = false;
    private boolean waitOneCycleToInitiateJump = false;
    private boolean uprightJump = false;
    private boolean isFirstStandingFrame = false;
    private final int jumpTimerMax = 10;
    private int jumpTimer = 0;
    private boolean isShooting = false;

    // bullet variables
    private final int shootingCounterMax = 10; // This is for shooting sprites (not bullets themselves)
    private int shootingCounter = shootingCounterMax + 1;
    private final int shotPauseTimerMax = 5;
    private int shotPauseTimer = shotPauseTimerMax + 1;
    private ArrayList<Bullet> bulletArr = new ArrayList<>();
    private final int bulletSpeed = 18;

    // TileMapComponents
    private TileMapComponent tilemap;
    private TileMapComponent tilemap1;
    // Cameras
    private SpriteFollowingCamera camera;
    // level Dimensions
    private int lvlWidth;
    private int lvlHeight;
    private int tileWidth = 48;
    private int tileHeight = 48;
//    private final ArrayList<Integer> climbableTileIDArr = new ArrayList<Integer>(List.of(214, 215, 216, 217, 218, 219, 220, 221, 222, 223));


    public GamePanel(Context context) {
        super(context);
        this.mainActivity = (MainActivity) context;
        holder = getHolder();
        holder.addCallback(this);
        setWindowDimensions();
        createControls();
        createPlayer();
        Log.d("GamePanel", "before createTileMaps()");
        createTileMaps(context);
        Log.d("GamePanel", "before createLevelDimensions()");
        createLevelDimensions();
        Log.d("GamePanel", "before createCameras()");
        createCameras();

        gameLoop = new GameLoop(this);
    }

    private void createLevelDimensions() {
        this.lvlWidth = this.tilemap.getCols() * this.tileWidth;
        this.lvlHeight = this.tilemap.getRows() * this.tileHeight;
    }

    private void createCameras() {
        this.camera = new SpriteFollowingCamera(windowWidth, windowHeight, lvlWidth, lvlHeight, player);
    }

    private void setWindowDimensions() {
        this.windowWidth = this.mainActivity.getWindowWidth();
        this.windowHeight = this.mainActivity.getWindowHeight();
    }

    private void createTileMaps(Context context) {
        ArrayList<TileSet> fgTileSetArr = new ArrayList<>();
        ArrayList<TileSet> bgTileSetArr  = new ArrayList<>();

        TileSet fgTileSet1 = createTileSet(R.drawable.metroid_tileset, 2, 5, 18, 18, 11, 35);
        fgTileSetArr.add(fgTileSet1);

        this.tilemap1 = new TileMapComponent(context, "met_practice_1.lvl", fgTileSetArr, bgTileSetArr, this.tileWidth, this.tileHeight);

        this.tilemap = tilemap1;
        Log.d("GamePanel", "end of createTileMaps");
    }

    private TileSet createTileSet(int resID, int tileSetRows, int tileSetCols, int width, int height, int offSetX, int offSetY) {
        TileSet tileSet = new TileSet();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap tileSheet = BitmapFactory.decodeResource(MainActivity.getGameContext().getResources(), resID, options);
        for(int j = 0; j < tileSetRows; j++) {
            for(int i = 0; i < tileSetCols; i++) {
                Bitmap scaledImage = getScaledBitmap(Bitmap.createBitmap(tileSheet, offSetX + (i * width),
                        offSetY + (j * height), width, height));
                Tile tile = new Tile(scaledImage);
                tileSet.addTile(tile);
            }
        }

        return tileSet;
    }

    private Bitmap getScaledBitmap(Bitmap bitmap) {
        Log.d("GamePanel", "called getScaledBitmap()");
        return Bitmap.createScaledBitmap(bitmap, this.tileWidth, this.tileHeight, true);
    }

    private void createPlayer() {
        this.playerStartXPos = this.mainActivity.getWindowWidth() / 2;
        this.playerStartYPos = this.mainActivity.getWindowHeight() / 2;
        this.player = new Player(playerStartXPos, playerStartYPos, playerRunSpeed, playerFallingSpeed);
    }

    private void createControls() {
        // set width and height of dPad
        this.triPadStartXPos = this.mainActivity.getWindowWidth() / 22;
        this.triPadWidth = this.mainActivity.getWindowHeight() / 3;
        this.triPadHeight = this.mainActivity.getWindowHeight() / 3;
        this.triPadSprite = new Sprite(true);
        this.triPadSprite.setRectangleDimensions(this.triPadWidth, this.triPadHeight);
        this.triPadSprite.setSpriteSheetDimensions(696, 696, 50, 50, 1, 1, 0);
        this.triPadSprite.loadDrawable(R.drawable.white_outline_tri_pad_transparent);
//        this.rightAndLeftOffset = triPadHeight / 6;
        this.thumbRadius = (float) this.mainActivity.getWindowHeight() / 5;
        // set width and height of capsule
        this.capsuleOffset = this.mainActivity.getWindowWidth() / 40;
        this.capsuleWidth = this.mainActivity.getWindowWidth() / 10;
        this.capsuleHeight = this.mainActivity.getWindowHeight() / 2;
        this.capsuleStartXPos = this.mainActivity.getWindowWidth() - (this.capsuleOffset + this.capsuleWidth);
        this.capsuleStartYPos = (this.mainActivity.getWindowHeight() / 2) - (this.capsuleHeight / 2);
        // capsule Sprite
        this.capsuleSprite = new Sprite(true);
        this.capsuleSprite.setRectangleDimensions(this.capsuleWidth, this.capsuleHeight);
        this.capsuleSprite.setSpriteSheetDimensions(100, 180, 100, 40, 1, 1, 0);
        this.capsuleSprite.loadDrawable(R.drawable.white_outline_capsule_transparent);
        this.jumpAndFireOffset = this.capsuleHeight / 10;
        // UP triangle points
        this.downX1 = triPadStartXPos + ((float) triPadWidth / 2);
        this.downY1 = (((float) this.mainActivity.getWindowHeight() / 3) + ((float) triPadHeight / 6) * 5);
        this.downX2 = 0;
        this.downY2 = this.mainActivity.getWindowHeight();
        this.downX3 = triPadStartXPos * 2 + triPadWidth;
        this.downY3 = this.mainActivity.getWindowHeight();
    }

    private void updatePlayer() {
        int x = player.getPlayerObj().getTransformComponent().getXPos();
        int y = player.getPlayerObj().getTransformComponent().getYPos();
        player.getPlayerObj().getSprite().update(x, y, this.playerFrame);
        updatePlayerAnimation();
    }

    private void updateControls() {
        // TODO: Do we need this anymore?
        this.triPadSprite.update(this.triPadStartXPos, (this.mainActivity.getWindowHeight() / 2) - (triPadHeight / 2), 0);
        this.capsuleSprite.update(this.capsuleStartXPos, this.capsuleStartYPos, 0);
    }

    private void clearInputArrayList() {
        this.inputArr.clear();
    }

    private void handlePlayerState() {
        ArrayList<Input> userInputArr = new ArrayList<>();
        for (TouchInput touchInput : this.inputArr) {
            userInputArr.add(touchInput.input);
        }
        // check here if fire is pressed
        if (userInputArr.contains(Input.FIRE) || userInputArr.contains(Input.JUMP_AND_FIRE)) {
            this.isShooting = true;
            this.shootingCounter = 0;
        }
        // process user input
        if (userInputArr.contains(Input.DOWN)) {
            // DOWN
            PlayerState curState = player.getCurState();
            player.getPlayerObj().getPhysicsComponent().setXVel(0);
            if (curState == PlayerState.RUN_RIGHT || curState == PlayerState.IDLE_RIGHT || curState == PlayerState.SHOOTING_RUN_RIGHT ||
            curState == PlayerState.SHOOTING_IDLE_RIGHT) {
                // Standing right
                player.changeSprite(PlayerState.DUCK_RIGHT);
            } else if (curState == PlayerState.RUN_LEFT || curState == PlayerState.IDLE_LEFT || curState == PlayerState.SHOOTING_RUN_LEFT ||
            curState == PlayerState.SHOOTING_IDLE_LEFT) {
                // Standing left
                player.changeSprite(PlayerState.DUCK_LEFT);
            } else if (curState == PlayerState.DUCK_RIGHT) {
                player.changeSprite(PlayerState.ROLL_RIGHT);
            } else if (curState == PlayerState.DUCK_LEFT) {
                player.changeSprite(PlayerState.ROLL_LEFT);
            } else if (curState == PlayerState.ABOUT_TO_STAND_RIGHT) {
                player.changeSprite(PlayerState.ROLL_RIGHT);
            } else if (curState == PlayerState.ABOUT_TO_STAND_LEFT) {
                player.changeSprite(PlayerState.ROLL_LEFT);
            }
        }
        else if ((userInputArr.contains(Input.LEFT)) && !(userInputArr.contains(Input.RIGHT))) {
            // LEFT
            PlayerState curState = player.getCurState();
            player.getPlayerObj().getPhysicsComponent().setXVel(-playerRunSpeed);
            if (curState == PlayerState.RUN_RIGHT || curState == PlayerState.IDLE_RIGHT || curState == PlayerState.RUN_LEFT ||
                    curState == PlayerState.IDLE_LEFT || curState == PlayerState.SHOOTING_RUN_RIGHT || curState == PlayerState.SHOOTING_IDLE_RIGHT ||
                    curState == PlayerState.SHOOTING_RUN_LEFT || curState == PlayerState.SHOOTING_IDLE_LEFT) {
                if (isShooting) {
                    player.changeSprite(PlayerState.SHOOTING_RUN_LEFT);
                } else {
                    player.changeSprite(PlayerState.RUN_LEFT);
                }
            } else if (curState == PlayerState.FALLING_RIGHT || curState == PlayerState.FALLING_LEFT || curState == PlayerState.SHOOTING_FALL_RIGHT ||
                    curState == PlayerState.SHOOTING_FALL_LEFT) {
                if (isShooting) {
                    player.changeSprite(PlayerState.SHOOTING_FALL_LEFT);
                    player.getPlayerObj().getPhysicsComponent().setYVel(playerFallingSpeed);
                } else {
                    player.changeSprite(PlayerState.FALLING_LEFT);
                    player.getPlayerObj().getPhysicsComponent().setYVel(playerFallingSpeed);
                }
            } else if (curState == PlayerState.DUCK_LEFT || curState == PlayerState.DUCK_RIGHT) {
                player.changeSprite(PlayerState.ROLL_LEFT);
            } else if (curState == PlayerState.ABOUT_TO_STAND_RIGHT || curState == PlayerState.ABOUT_TO_STAND_LEFT) {
                if (isShooting) {
                    this.changePlayerSpriteTaller(curState, PlayerState.SHOOTING_RUN_LEFT);
                } else {
                    this.changePlayerSpriteTaller(curState, PlayerState.RUN_LEFT);
                }
            } else if (curState == PlayerState.ROLL_RIGHT || curState == PlayerState.ROLL_LEFT) {
                player.changeSprite(PlayerState.ROLL_LEFT);
            } else if (curState == PlayerState.JUMPING_RIGHT || curState == PlayerState.SHOOTING_JUMP_RIGHT || curState == PlayerState.JUMPING_LEFT ||
                    curState == PlayerState.SHOOTING_JUMP_LEFT) {
                if (isShooting) {
                    player.changeSprite(PlayerState.SHOOTING_JUMP_LEFT);
                } else {
                    player.changeSprite(PlayerState.JUMPING_LEFT);
                }
            } else if (curState == PlayerState.SOMERSAULT_RIGHT || curState == PlayerState.SOMERSAULT_LEFT) {
                if (isShooting) {
                    player.changeSprite(PlayerState.SHOOTING_JUMP_LEFT);
                }
            }
        }
        else if ((userInputArr.contains(Input.RIGHT)) && !(userInputArr.contains(Input.LEFT))) {
            // RIGHT
            PlayerState curState = player.getCurState();
            player.getPlayerObj().getPhysicsComponent().setXVel(playerRunSpeed);
            if (curState == PlayerState.RUN_RIGHT || curState == PlayerState.IDLE_RIGHT || curState == PlayerState.RUN_LEFT ||
                    curState == PlayerState.IDLE_LEFT || curState == PlayerState.SHOOTING_RUN_RIGHT || curState == PlayerState.SHOOTING_IDLE_RIGHT ||
                    curState == PlayerState.SHOOTING_RUN_LEFT || curState == PlayerState.SHOOTING_IDLE_LEFT) {
                if (isShooting) {
                    player.changeSprite(PlayerState.SHOOTING_RUN_RIGHT);
                } else {
                    player.changeSprite(PlayerState.RUN_RIGHT);
                }
            } else if (curState == PlayerState.FALLING_RIGHT || curState == PlayerState.FALLING_LEFT || curState == PlayerState.SHOOTING_FALL_RIGHT ||
                    curState == PlayerState.SHOOTING_FALL_LEFT) {
                if (isShooting) {
                    player.changeSprite(PlayerState.SHOOTING_FALL_RIGHT);
                    player.getPlayerObj().getPhysicsComponent().setYVel(playerFallingSpeed);
                } else {
                    player.changeSprite(PlayerState.FALLING_RIGHT);
                    player.getPlayerObj().getPhysicsComponent().setYVel(playerFallingSpeed);
                }

            } else if (curState == PlayerState.DUCK_LEFT || curState == PlayerState.DUCK_RIGHT) {
                player.changeSprite(PlayerState.ROLL_RIGHT);
            } else if (curState == PlayerState.ABOUT_TO_STAND_RIGHT || curState == PlayerState.ABOUT_TO_STAND_LEFT) {
                if (isShooting) {
                    this.changePlayerSpriteTaller(curState, PlayerState.SHOOTING_RUN_RIGHT);
                } else {
                    this.changePlayerSpriteTaller(curState, PlayerState.RUN_RIGHT);
                }
            } else if (curState == PlayerState.ROLL_RIGHT || curState == PlayerState.ROLL_LEFT) {
                player.changeSprite(PlayerState.ROLL_RIGHT);
            } else if (curState == PlayerState.JUMPING_LEFT || curState == PlayerState.SHOOTING_JUMP_LEFT || curState == PlayerState.JUMPING_RIGHT ||
                    curState == PlayerState.SHOOTING_JUMP_RIGHT) {
                if (isShooting) {
                    player.changeSprite(PlayerState.SHOOTING_JUMP_RIGHT);
                } else {
                    player.changeSprite(PlayerState.JUMPING_RIGHT);
                }
            } else if (curState == PlayerState.SOMERSAULT_RIGHT || curState == PlayerState.SOMERSAULT_LEFT) {
                if (isShooting) {
                    player.changeSprite(PlayerState.SHOOTING_JUMP_RIGHT);
                }
            }
        }
        else {
            // Neither RIGHT nor LEFT nor UP
            PlayerState curState = player.getCurState();
            player.getPlayerObj().getPhysicsComponent().setXVel(0);
            if (curState == PlayerState.RUN_RIGHT || curState == PlayerState.IDLE_RIGHT || curState == PlayerState.SHOOTING_RUN_RIGHT ||
                    curState == PlayerState.SHOOTING_IDLE_RIGHT) {
                if (isShooting) {
                    player.changeSprite(PlayerState.SHOOTING_IDLE_RIGHT);
                } else {
                    player.changeSprite(PlayerState.IDLE_RIGHT);
                }
            } else if (curState == PlayerState.RUN_LEFT || curState == PlayerState.IDLE_LEFT || curState == PlayerState.SHOOTING_RUN_LEFT ||
                    curState == PlayerState.SHOOTING_IDLE_LEFT) {
                if (isShooting) {
                    player.changeSprite(PlayerState.SHOOTING_IDLE_LEFT);
                } else {
                    player.changeSprite(PlayerState.IDLE_LEFT);
                }
            } else if (curState == PlayerState.DUCK_RIGHT) {
                player.changeSprite(PlayerState.ROLL_RIGHT);
            } else if (curState == PlayerState.DUCK_LEFT) {
                player.changeSprite(PlayerState.ROLL_LEFT);
            } else if (curState == PlayerState.ABOUT_TO_STAND_RIGHT) {
                if (isShooting) {
                    this.changePlayerSpriteTaller(curState, PlayerState.SHOOTING_IDLE_RIGHT);
                } else {
                    this.changePlayerSpriteTaller(curState, PlayerState.IDLE_RIGHT);
                }
            } else if (curState == PlayerState.ABOUT_TO_STAND_LEFT) {
                if (isShooting) {
                    this.changePlayerSpriteTaller(curState, PlayerState.SHOOTING_IDLE_LEFT);
                } else {
                    this.changePlayerSpriteTaller(curState, PlayerState.IDLE_LEFT);
                }
            } else if (curState == PlayerState.JUMPING_RIGHT || curState == PlayerState.SHOOTING_JUMP_RIGHT) {
                if (isShooting) {
                    player.changeSprite(PlayerState.SHOOTING_JUMP_RIGHT);
                } else {
                    player.changeSprite(PlayerState.JUMPING_RIGHT);
                }
            } else if (curState == PlayerState.JUMPING_LEFT || curState == PlayerState.SHOOTING_JUMP_LEFT) {
                if (isShooting) {
                    player.changeSprite(PlayerState.SHOOTING_JUMP_LEFT);
                } else {
                    player.changeSprite(PlayerState.JUMPING_LEFT);
                }
            } else if (curState == PlayerState.SOMERSAULT_RIGHT) {
                if (isShooting) {
                    player.changeSprite(PlayerState.SHOOTING_JUMP_RIGHT);
                }
            } else if (curState == PlayerState.SOMERSAULT_LEFT) {
                if (isShooting) {
                    player.changeSprite(PlayerState.SHOOTING_JUMP_LEFT);
                }
            } else if (curState == PlayerState.FALLING_RIGHT || curState == PlayerState.SHOOTING_FALL_RIGHT) {
                if (isShooting) {
                    player.changeSprite(PlayerState.SHOOTING_FALL_RIGHT);
                } else {
                    player.changeSprite(PlayerState.FALLING_RIGHT);
                }
            } else if (curState == PlayerState.FALLING_LEFT || curState == PlayerState.SHOOTING_FALL_LEFT) {
                if (isShooting) {
                    player.changeSprite(PlayerState.SHOOTING_FALL_LEFT);
                } else {
                    player.changeSprite(PlayerState.FALLING_LEFT);
                }
            }
        }
    }

    private void changePlayerSpriteTaller(PlayerState curState, PlayerState nextState) {
        // nextState must be taller (greater height)
        Sprite curSprite = player.getAnySprite(curState);
        Sprite nextSprite = player.getAnySprite(nextState);
        int delta = 0;
        if (nextSprite.getHeight() >= curSprite.getHeight()) {
            delta = nextSprite.getHeight() - curSprite.getHeight();
        }
        player.changeSprite(nextState);
        // move sprite upward
        player.getPlayerObj().getTransformComponent().setYPos(player.getPlayerObj().getTransformComponent().getYPos() - delta);
    }

    private void handleJumpToStand() {
        PlayerState curState = player.getCurState();
        ArrayList<Input> userInputArr = new ArrayList<>();
        for (TouchInput touchInput : this.inputArr) {
            userInputArr.add(touchInput.input);
        }
        if ((userInputArr.contains(Input.JUMP) || userInputArr.contains(Input.JUMP_AND_FIRE)) && !(userInputArr.contains(Input.DOWN))) {
            if (tilemap.tileAtXY(player.getPlayerObj().getTransformComponent().getXPos(), player.getPlayerObj().getTransformComponent().getYPos() -
                    player.getPlayerObj().getSprite().getHeight()) == -1 && tilemap.tileAtXY(player.getPlayerObj().getTransformComponent().getXPos() +
                    player.getPlayerObj().getSprite().getWidth(), player.getPlayerObj().getTransformComponent().getYPos() -
                    player.getPlayerObj().getSprite().getHeight()) == -1) {
                if (curState == PlayerState.ROLL_RIGHT) {
                    this.changePlayerSpriteTaller(curState, PlayerState.ABOUT_TO_STAND_RIGHT);
                    this.jumpTimer = this.jumpTimerMax;
                } else if (curState == PlayerState.ROLL_LEFT) {
                    this.changePlayerSpriteTaller(curState, PlayerState.ABOUT_TO_STAND_LEFT);
                    this.jumpTimer = this.jumpTimerMax;
                }
            }
        }
    }

    private void handlePlayerJump() {
        decrementJumpTimer();
        PlayerState curState = player.getCurState();
        ArrayList<Input> userInputArr = new ArrayList<>();
        for (TouchInput touchInput : this.inputArr) {
            userInputArr.add(touchInput.input);
        }
        if (curState == PlayerState.SOMERSAULT_RIGHT || curState == PlayerState.SOMERSAULT_LEFT || curState == PlayerState.JUMPING_RIGHT ||
                curState == PlayerState.JUMPING_LEFT || curState == PlayerState.SHOOTING_JUMP_RIGHT || curState == PlayerState.SHOOTING_JUMP_LEFT) {
            boolean wait = false; // change back to false if waiting for one cycle to initiate
            if (this.waitOneCycleToInitiateJump) {
                wait = true;
                this.waitOneCycleToInitiateJump = false;
            }
            if (!wait && !player.getPlayerObj().getJumpComponent().stillJumping()) {
                // player is not jumping anymore and the wait period is over
                Log.d("GamePanel", "changeToFalling() called because jump ended and wait period is over");
                changeToFalling();
            }
            else if ((userInputArr.contains(Input.JUMP) || userInputArr.contains(Input.JUMP_AND_FIRE)) && this.jumpTimer <= 0) {
                // continue jump
                Log.d("GamePanel", "continuing jump");
                player.getPlayerObj().getJumpComponent().update(player.getPlayerObj());
                if (this.uprightJump) {
                    // upright jump
                    if (curState == PlayerState.SOMERSAULT_RIGHT || curState == PlayerState.JUMPING_RIGHT || curState == PlayerState.SHOOTING_JUMP_RIGHT) {
                        if (isShooting) {
                            this.changePlayerSpriteTaller(curState, PlayerState.SHOOTING_JUMP_RIGHT);
                        } else {
                            this.changePlayerSpriteTaller(curState, PlayerState.JUMPING_RIGHT);
                        }
                    } else if (curState == PlayerState.SOMERSAULT_LEFT || curState == PlayerState.JUMPING_LEFT || curState == PlayerState.SHOOTING_JUMP_LEFT) {
                        if (isShooting) {
                            this.changePlayerSpriteTaller(curState, PlayerState.SHOOTING_JUMP_LEFT);
                        } else {
                            this.changePlayerSpriteTaller(curState, PlayerState.JUMPING_LEFT);
                        }
                    }
                } else {
                    // somersault jump
                    if (curState == PlayerState.SOMERSAULT_RIGHT) {
                        if (isShooting) {
                            this.changePlayerSpriteTaller(curState, PlayerState.SHOOTING_JUMP_RIGHT);
                            this.uprightJump = true;
                        }
                    } else if (curState == PlayerState.SOMERSAULT_LEFT) {
                        if (isShooting) {
                            this.changePlayerSpriteTaller(curState, PlayerState.SHOOTING_JUMP_LEFT);
                            this.uprightJump = true;
                        }
                    } else if (curState == PlayerState.JUMPING_RIGHT || curState == PlayerState.SHOOTING_JUMP_RIGHT) {
                        if (isShooting) {
                            this.changePlayerSpriteTaller(curState, PlayerState.SHOOTING_JUMP_RIGHT);
                        } else {
                            this.changePlayerSpriteTaller(curState, PlayerState.JUMPING_RIGHT);
                        }
                    } else if (curState == PlayerState.JUMPING_LEFT || curState == PlayerState.SHOOTING_JUMP_LEFT) {
                        if (isShooting) {
                            this.changePlayerSpriteTaller(curState, PlayerState.SHOOTING_JUMP_LEFT);
                        } else {
                            this.changePlayerSpriteTaller(curState, PlayerState.JUMPING_LEFT);
                        }
                    }
                }
            } else {
                // hit ceiling
                if (curState == PlayerState.SOMERSAULT_RIGHT || curState == PlayerState.JUMPING_RIGHT) {
                    if (isShooting) {
                        this.changePlayerSpriteTaller(curState, PlayerState.SHOOTING_JUMP_RIGHT);
                        this.uprightJump = true;
                    }
                } else if (curState == PlayerState.SHOOTING_JUMP_RIGHT) {
                    if (!isShooting) {
                        player.changeSprite(PlayerState.JUMPING_RIGHT);
                    }
                }
                else if (curState == PlayerState.SOMERSAULT_LEFT || curState == PlayerState.JUMPING_LEFT) {
                    if (isShooting) {
                        this.changePlayerSpriteTaller(curState, PlayerState.SHOOTING_JUMP_LEFT);
                        this.uprightJump = true;
                    }
                } else if (curState == PlayerState.SHOOTING_JUMP_LEFT) {
                    if (!isShooting) {
                        player.changeSprite(PlayerState.JUMPING_LEFT);
                    }
                }
                // do hitCeiling here, but don't end the jump
                // end jump should be done automatically in jumpComponent or in the case of floor collision
                if (player.getPlayerObj().getPhysicsComponent().getYVel() < 0) {
                    player.getPlayerObj().getJumpComponent().hitCeiling(player.getPlayerObj(), 1, this.playerFallingSpeed);
                }
                player.getPlayerObj().getJumpComponent().update(player.getPlayerObj());
            }
        } else {
            if (curState == PlayerState.RUN_RIGHT || curState == PlayerState.RUN_LEFT || curState == PlayerState.IDLE_RIGHT || curState == PlayerState.IDLE_LEFT ||
                    curState == PlayerState.SHOOTING_RUN_RIGHT || curState == PlayerState.SHOOTING_RUN_LEFT || curState == PlayerState.SHOOTING_IDLE_RIGHT ||
                    curState == PlayerState.SHOOTING_IDLE_LEFT) {
                // begin jump
                if (userInputArr.contains(Input.JUMP) || userInputArr.contains(Input.JUMP_AND_FIRE)) {
                    if (this.jumpTimer <= 0) {
                        Log.d("GamePanel", "setInitiateJump(true)");
                        player.getPlayerObj().setInitiateJump(true);
//                    this.waitOneCycleForJumpUpdate = true;
                        this.waitOneCycleToInitiateJump = true;
                        if (userInputArr.contains(Input.RIGHT)) {
                            // somersault jump RIGHT
                            this.uprightJump = false;
                            // must move away from right wall when changing sprite here
                            int tempWidth = player.getPlayerObj().getSprite().getWidth();
                            if (isShooting) {
                                player.changeSprite(PlayerState.SHOOTING_JUMP_RIGHT);
                                this.uprightJump = true;
                            } else {
                                player.changeSprite(PlayerState.SOMERSAULT_RIGHT);
                            }
                            player.getPlayerObj().getPhysicsComponent().setYVel(0);
                            Collision collision = tilemap.isTouchingRightWall(player.getPlayerObj());
                            if (collision.getIsColliding()) {
                                player.getPlayerObj().getTransformComponent().setXPos(player.getPlayerObj().getTransformComponent().getXPos() -
                                        (player.getPlayerObj().getSprite().getWidth() - tempWidth));
                            }
                        } else if (userInputArr.contains(Input.LEFT)) {
                            // somersault jump LEFT
                            this.uprightJump = false;
                            if (isShooting) {
                                player.changeSprite(PlayerState.SHOOTING_JUMP_LEFT);
                                this.uprightJump = true;
                            } else {
                                player.changeSprite(PlayerState.SOMERSAULT_LEFT);
                            }
                            player.getPlayerObj().getPhysicsComponent().setYVel(0);
                        } else {
                            // upright Jump in current direction
                            this.uprightJump = true;
                            if (curState == PlayerState.IDLE_RIGHT || curState == PlayerState.RUN_RIGHT || curState == PlayerState.SHOOTING_IDLE_RIGHT ||
                                    curState == PlayerState.SHOOTING_RUN_RIGHT) {
                                // must move sprite away from right wall if sprite is different size
                                int tempWidth = player.getPlayerObj().getSprite().getWidth();
                                if (isShooting) {
                                    player.changeSprite(PlayerState.SHOOTING_JUMP_RIGHT);
                                } else {
                                    player.changeSprite(PlayerState.JUMPING_RIGHT);
                                }
                                player.getPlayerObj().getPhysicsComponent().setYVel(0);
                                Collision collision = tilemap.isTouchingRightWall(player.getPlayerObj());
                                if (collision.getIsColliding()) {
                                    player.getPlayerObj().getTransformComponent().setXPos(player.getPlayerObj().getTransformComponent().getXPos() -
                                            (player.getPlayerObj().getSprite().getWidth() - tempWidth));
                                }
                            } else if (curState == PlayerState.IDLE_LEFT || curState == PlayerState.RUN_LEFT || curState == PlayerState.SHOOTING_IDLE_LEFT ||
                                    curState == PlayerState.SHOOTING_RUN_LEFT) {
                                // change sprite on left wall, you don't have to adjust xPos
                                if (isShooting) {
                                    player.changeSprite(PlayerState.SHOOTING_JUMP_LEFT);
                                } else {
                                    player.changeSprite(PlayerState.JUMPING_LEFT);
                                }
                                player.getPlayerObj().getPhysicsComponent().setYVel(0);
                            }
                        }
                    }
                }
            }
        }
    }

    private void decrementJumpTimer() {
        if (this.jumpTimer > 0) {
            this.jumpTimer--;
        }
    }


    private void handlePlayerWallCollision() {
        // TODO: setting xVel and yVel might be a problem
        Collision collision = this.tilemap.isTouchingRightWall(player.getPlayerObj());
        PlayerState curState = player.getCurState();
        if (collision.getIsColliding()) {
            player.getPlayerObj().getTransformComponent().setXPos(tileWidth * collision.getFirstTileColumn() - player.getPlayerObj().getSprite().getWidth() - 1);
        }
        collision = this.tilemap.isTouchingLeftWall(player.getPlayerObj());
        if (collision.getIsColliding()) {
            player.getPlayerObj().getTransformComponent().setXPos(tileWidth * (collision.getFirstTileColumn() + 1));
        }
    }

    private void handlePlayerFloorCollision() {
        ArrayList<Input> userInputArr = new ArrayList<>();
        for (TouchInput touchInput : this.inputArr) {
            userInputArr.add(touchInput.input);
        }
        PlayerState curState = player.getCurState();
        if (player.getPlayerObj().getPhysicsComponent().getYVel() > 0) {
            Collision collision = this.tilemap.isTouchingGround(player.getPlayerObj());
            if (collision.getIsColliding()) {
                Log.d("GamePanel", "floor collision");
                player.getPlayerObj().getPhysicsComponent().setYVel(this.playerFallingSpeed);
                if (curState == PlayerState.FALLING_RIGHT || curState == PlayerState.SOMERSAULT_RIGHT || curState == PlayerState.JUMPING_RIGHT ||
                        curState == PlayerState.SHOOTING_FALL_RIGHT || curState == PlayerState.SHOOTING_JUMP_RIGHT) {
                    // you don't need changePlayerSpriteTaller here because we deal with the height difference later...
                    if (userInputArr.contains(Input.RIGHT)) {
                        if (isShooting) {
                            player.changeSprite(PlayerState.SHOOTING_RUN_RIGHT);
                        } else {
                            player.changeSprite(PlayerState.RUN_RIGHT);
                        }
                    } else if (userInputArr.contains(Input.LEFT)) {
                        if (isShooting) {
                            player.changeSprite(PlayerState.SHOOTING_RUN_LEFT);
                        } else {
                            player.changeSprite(PlayerState.RUN_LEFT);
                        }
                    } else {
                        if (isShooting) {
                            player.changeSprite(PlayerState.SHOOTING_IDLE_RIGHT);
                        } else {
                            player.changeSprite(PlayerState.IDLE_RIGHT);
                        }
                    }
                } else if (curState == PlayerState.FALLING_LEFT || curState == PlayerState.SOMERSAULT_LEFT || curState == PlayerState.JUMPING_LEFT ||
                        curState == PlayerState.SHOOTING_FALL_LEFT || curState == PlayerState.SHOOTING_JUMP_LEFT) {
                    if (userInputArr.contains(Input.RIGHT)) {
                        if (isShooting) {
                            player.changeSprite(PlayerState.SHOOTING_RUN_RIGHT);
                        } else {
                            player.changeSprite(PlayerState.RUN_RIGHT);
                        }
                    } else if (userInputArr.contains(Input.LEFT)) {
                        if (isShooting) {
                            player.changeSprite(PlayerState.SHOOTING_RUN_LEFT);
                        } else {
                            player.changeSprite(PlayerState.RUN_LEFT);
                        }
                    } else {
                        if (isShooting) {
                            player.changeSprite(PlayerState.SHOOTING_IDLE_LEFT);
                        } else {
                            player.changeSprite(PlayerState.IDLE_LEFT);
                        }
                    }
                }
                // Here we deal with the height difference
                player.getPlayerObj().getTransformComponent().setYPos(tileHeight * collision.getFirstTileRow() - player.getPlayerObj().getSprite().getHeight() - 1);
                player.getPlayerObj().getJumpComponent().endJump();
//                this.uprightJump = true;
            } else {
                if (curState != PlayerState.SOMERSAULT_RIGHT && curState != PlayerState.SOMERSAULT_LEFT) {
                    Log.d("GamePanel", "changeToFalling() in handlePlayerFloorCollision");
                    changeToFalling();
                }
            }

        }
    }

    private void changeToFalling() {
        // TODO: handle if player is shooting
//        Log.d("GamePanel", "changeToFalling()");
        ArrayList<Input> userInputArr = new ArrayList<>();
        for (TouchInput touchInput : this.inputArr) {
            userInputArr.add(touchInput.input);
        }
        // end jump here, just in case; TODO: need to check?
        player.getPlayerObj().getJumpComponent().endJump();
        player.getPlayerObj().getPhysicsComponent().setYVel(this.playerFallingSpeed);
        PlayerState curState = player.getCurState();
        if (!userInputArr.contains(Input.RIGHT) && !userInputArr.contains(Input.LEFT)) {
            if (curState == PlayerState.RUN_RIGHT || curState == PlayerState.IDLE_RIGHT || curState == PlayerState.SHOOTING_RUN_RIGHT ||
                    curState == PlayerState.SHOOTING_IDLE_RIGHT) {
                if (isShooting) {
                    player.changeSprite(PlayerState.SHOOTING_FALL_RIGHT);
                } else {
                    player.changeSprite(PlayerState.FALLING_RIGHT);
                }
            } else if (curState == PlayerState.RUN_LEFT || curState == PlayerState.IDLE_LEFT || curState == PlayerState.SHOOTING_RUN_LEFT ||
                    curState == PlayerState.SHOOTING_IDLE_LEFT) {
                if (isShooting) {
                    player.changeSprite(PlayerState.SHOOTING_FALL_LEFT);
                } else {
                    player.changeSprite(PlayerState.FALLING_LEFT);
                }
            } else if (curState == PlayerState.JUMPING_RIGHT || curState == PlayerState.SHOOTING_JUMP_RIGHT) {
                if (isShooting) {
                    player.changeSprite(PlayerState.SHOOTING_FALL_RIGHT);
                } else {
                    player.changeSprite(PlayerState.FALLING_RIGHT);
                }
            } else if (curState == PlayerState.JUMPING_LEFT || curState == PlayerState.SHOOTING_JUMP_LEFT) {
                if (isShooting) {
                    player.changeSprite(PlayerState.SHOOTING_FALL_LEFT);
                } else {
                    player.changeSprite(PlayerState.FALLING_LEFT);
                }
            } else if (curState == PlayerState.SOMERSAULT_RIGHT) {
                if (isShooting) {
                    player.changeSprite(PlayerState.SHOOTING_FALL_RIGHT);
                } else {
                    player.changeSprite(PlayerState.FALLING_RIGHT);
                }
            } else if (curState == PlayerState.SOMERSAULT_LEFT) {
                if (isShooting) {
                    player.changeSprite(PlayerState.SHOOTING_FALL_LEFT);
                } else {
                    player.changeSprite(PlayerState.FALLING_LEFT);
                }
            }
        } else if (userInputArr.contains(Input.RIGHT)) {
            // RIGHT
            if (isShooting) {
                player.changeSprite(PlayerState.SHOOTING_FALL_RIGHT);
            } else {
                player.changeSprite(PlayerState.FALLING_RIGHT);
            }
        } else {
            // LEFT
            if (isShooting) {
                player.changeSprite(PlayerState.SHOOTING_FALL_LEFT);
            } else {
                player.changeSprite(PlayerState.FALLING_LEFT);
            }
        }
        Log.d("GamePanel", "end of changeToFalling()");
    }

    private void handlePlayerCeilingCollision() {
        if (player.getPlayerObj().getPhysicsComponent().getYVel() <= 0) {
            Collision collision = this.tilemap.isTouchingCeiling(player.getPlayerObj());
            if (collision.getIsColliding()) {
                Log.d("GamePanel", "ceiling collision");
                player.getPlayerObj().getJumpComponent().hitCeiling(player.getPlayerObj(), 2, this.playerFallingSpeed);
                player.getPlayerObj().getTransformComponent().setYPos(tileHeight * (collision.getFirstTileRow() + 1) + 1);
                PlayerState curState = player.getCurState();
                ArrayList<Input> userInputArr = new ArrayList<>();
                for (TouchInput touchInput : this.inputArr) {
                    userInputArr.add(touchInput.input);
                }
                if (userInputArr.contains(Input.RIGHT)) {
                    if (isShooting) {
                        player.changeSprite(PlayerState.SHOOTING_JUMP_RIGHT);
                    } else {
                        player.changeSprite(PlayerState.JUMPING_RIGHT);
                    }
                } else if (userInputArr.contains(Input.LEFT)) {
                    if (isShooting) {
                        player.changeSprite(PlayerState.SHOOTING_JUMP_LEFT);
                    } else {
                        player.changeSprite(PlayerState.JUMPING_LEFT);
                    }
                }
                else {
                    if (curState == PlayerState.SOMERSAULT_RIGHT || curState == PlayerState.JUMPING_RIGHT || curState == PlayerState.SHOOTING_JUMP_RIGHT) {
                        if (isShooting) {
                            player.changeSprite(PlayerState.SHOOTING_JUMP_RIGHT);
                        } else {
                            player.changeSprite(PlayerState.JUMPING_RIGHT);
                        }
                    } else if (curState == PlayerState.SOMERSAULT_LEFT || curState == PlayerState.JUMPING_LEFT || curState == PlayerState.SHOOTING_JUMP_LEFT) {
                        if (isShooting) {
                            player.changeSprite(PlayerState.SHOOTING_JUMP_LEFT);
                        } else {
                            player.changeSprite(PlayerState.JUMPING_LEFT);
                        }
                    }
                }
            }
        }
    }

    private void handlePlayerShooting() {
        PlayerState curState = player.getCurState();
        ArrayList<Input> userInputArr = new ArrayList<>();
        for (TouchInput touchInput : this.inputArr) {
            userInputArr.add(touchInput.input);
        }
        if ((userInputArr.contains(Input.FIRE) || userInputArr.contains(Input.JUMP_AND_FIRE)) && !(curState == PlayerState.ROLL_RIGHT ||
                curState == PlayerState.ROLL_LEFT || curState == PlayerState.DUCK_RIGHT || curState == PlayerState.DUCK_LEFT ||
                curState == PlayerState.ABOUT_TO_STAND_RIGHT || curState == PlayerState.ABOUT_TO_STAND_LEFT)) {
            Log.d("GamePanel", "Fire pressed in handlePlayerShooting()");
            this.shootingCounter = 0;
            this.isShooting = true;
            if (this.shotPauseTimer > this.shotPauseTimerMax) {
                createBullet();
                this.shotPauseTimer = 0;
            }
        }
        // increment counters
        this.shotPauseTimer += 1;
        if (this.shootingCounter <= this.shootingCounterMax) {
            this.shootingCounter += 1;
        } else {
            this.isShooting = false;
        }
        Log.d("GamePanel", "end of handlePlayerShooting()");
    }

    private void createBullet() {
        Log.d("GamePanel", "createBullet()");
        Bullet bullet = new Bullet();
        setPositionAndVelocityOfBullet(bullet);
        this.bulletArr.add(bullet);
        Log.d("GamePanel", "end of createBullet()");
    }

    private void setPositionAndVelocityOfBullet(Bullet bullet) {
        PlayerState curState = player.getCurState();
        switch (curState) {
            case SHOOTING_RUN_RIGHT:
                bullet.setVelocity(bulletSpeed, 0);
                bullet.setStartingPoint((int) (player.getPlayerObj().getTransformComponent().getXPos() + (player.getAnySprite(PlayerState.SHOOTING_RUN_RIGHT).getWidth() * 0.60)),
                        player.getPlayerObj().getTransformComponent().getYPos() + (int) (player.getAnySprite(PlayerState.SHOOTING_RUN_RIGHT).getHeight() * 0.25));
                break;
            case SHOOTING_RUN_LEFT:
                bullet.setVelocity(-bulletSpeed, 0);
                bullet.setStartingPoint((int) (player.getPlayerObj().getTransformComponent().getXPos() + (player.getAnySprite(PlayerState.SHOOTING_RUN_LEFT).getWidth() * 0.10)),
                        player.getPlayerObj().getTransformComponent().getYPos() + (int) (player.getAnySprite(PlayerState.SHOOTING_RUN_LEFT).getHeight() * 0.25));
                break;
            case SHOOTING_IDLE_RIGHT:
                bullet.setVelocity(bulletSpeed, 0);
                bullet.setStartingPoint((int) (player.getPlayerObj().getTransformComponent().getXPos() + (player.getAnySprite(PlayerState.SHOOTING_IDLE_RIGHT).getWidth() * 0.60)),
                        player.getPlayerObj().getTransformComponent().getYPos() + (int) (player.getAnySprite(PlayerState.SHOOTING_IDLE_RIGHT).getHeight() * 0.25));
                break;
            case SHOOTING_IDLE_LEFT:
                bullet.setVelocity(-bulletSpeed, 0);
                bullet.setStartingPoint((int) (player.getPlayerObj().getTransformComponent().getXPos() + (player.getAnySprite(PlayerState.SHOOTING_IDLE_LEFT).getWidth() * 0.05)),
                        player.getPlayerObj().getTransformComponent().getYPos() + (int) (player.getAnySprite(PlayerState.SHOOTING_IDLE_LEFT).getHeight() * 0.25));
                break;
            case SHOOTING_FALL_RIGHT:
                bullet.setVelocity(bulletSpeed, 0);
                bullet.setStartingPoint((int) (player.getPlayerObj().getTransformComponent().getXPos() + (player.getAnySprite(PlayerState.SHOOTING_FALL_RIGHT).getWidth() * 0.70)),
                        player.getPlayerObj().getTransformComponent().getYPos() + (int) (player.getAnySprite(PlayerState.SHOOTING_FALL_RIGHT).getHeight() * 0.30));
                break;
            case SHOOTING_FALL_LEFT:
                bullet.setVelocity(-bulletSpeed, 0);
                bullet.setStartingPoint((int) (player.getPlayerObj().getTransformComponent().getXPos() + (player.getAnySprite(PlayerState.SHOOTING_FALL_LEFT).getWidth() * 0.10)),
                        player.getPlayerObj().getTransformComponent().getYPos() + (int) (player.getAnySprite(PlayerState.SHOOTING_FALL_LEFT).getHeight() * 0.30));
                break;
            case SHOOTING_JUMP_RIGHT:
                bullet.setVelocity(bulletSpeed, 0);
                bullet.setStartingPoint((int) (player.getPlayerObj().getTransformComponent().getXPos() + (player.getAnySprite(PlayerState.SHOOTING_JUMP_RIGHT).getWidth() * 0.70)),
                        player.getPlayerObj().getTransformComponent().getYPos() + (int) (player.getAnySprite(PlayerState.SHOOTING_JUMP_RIGHT).getHeight() * 0.30));
                break;
            case SHOOTING_JUMP_LEFT:
                bullet.setVelocity(-bulletSpeed, 0);
                bullet.setStartingPoint((int) (player.getPlayerObj().getTransformComponent().getXPos() + (player.getAnySprite(PlayerState.SHOOTING_JUMP_LEFT).getWidth() * 0.10)),
                        player.getPlayerObj().getTransformComponent().getYPos() + (int) (player.getAnySprite(PlayerState.SHOOTING_JUMP_LEFT).getHeight() * 0.30));
                break;
            default:
                bullet.setVelocity(-bulletSpeed, 0);
                bullet.setStartingPoint((int) (player.getPlayerObj().getTransformComponent().getXPos() + (player.getAnySprite(curState).getWidth() * 0.5)),
                        (player.getPlayerObj().getTransformComponent().getYPos()));
                break;
        }
    }

    private void bulletsUpdate() {
        Log.d("GamePanel", "bulletsUpdate()");
        ArrayList<Integer> indexArr = new ArrayList<>();
        for (int i = bulletArr.size() - 1; i >= 0; i--) {
            Log.d("GamePanel", String.format("Bullet idx = %d", i));
            Bullet bullet = bulletArr.get(i);
            bullet.getBulletPhysics().updateX(bullet.getBulletObj());
            Collision collision1 = new Collision();
            if (bullet.getBulletPhysics().getXVel() > 0) {
                collision1 = this.tilemap.isTouchingRightWall(bullet.getBulletObj());
            } else {
                collision1 = this.tilemap.isTouchingLeftWall(bullet.getBulletObj());
            }
            bullet.getBulletPhysics().updateY(bullet.getBulletObj());
            // update the sprite
            int x = bullet.getBulletObj().getTransformComponent().getXPos();
            int y = bullet.getBulletObj().getTransformComponent().getYPos();
            bullet.getBulletObj().getSprite().update(x, y, 0);
            // increment shot life counter
            bullet.incrementShotLifeCounter();
            if (collision1.getIsColliding() || bullet.isReadyToDestroy()) {
                // put in array to be destroyed
                indexArr.add(i);
            }
        }
        for (int idx : indexArr) {
            this.bulletArr.remove(idx);
        }
    }

    public void update() {
        handlePlayerState();
        player.getPlayerObj().getPhysicsComponent().updateX(player.getPlayerObj());
        handlePlayerWallCollision();
        handlePlayerJump();
        handleJumpToStand();
        player.getPlayerObj().getPhysicsComponent().updateY(player.getPlayerObj());
        handlePlayerFloorCollision();
        handlePlayerCeilingCollision();
        handlePlayerShooting();
        bulletsUpdate();

        updatePlayer();
        updateControls();
        this.camera.update();
        Log.d("GamePanel", "end of update()");
    }

    private void renderPlayer(Canvas c) {
        player.getPlayerObj().getSprite().render(c, this.camera.getX(), this.camera.getY());
    }

    private void renderBullets(Canvas c) {
        Log.d("GamePanel", "renderBullets()");
        for (Bullet bullet : this.bulletArr) {
            bullet.getBulletObj().getSprite().render(c, this.camera.getX(), this.camera.getY());
        }
        Log.d("GamePanel", "end of renderBullets()");
    }

    private void renderControls(Canvas c) {
        this.triPadSprite.render(c, 0, 0);
        this.capsuleSprite.render(c, 0, 0);
    }

    private void renderControlColors(Canvas c) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        // draw Circles
        for (TouchInput touchInput : inputArr) {
            switch (touchInput.input) {
                case LEFT:
                    // draw circle
                    paint.setColor(this.upLeftColor);
                    c.drawCircle(touchInput.x, touchInput.y, thumbRadius, paint);
                    break;
                case RIGHT:
                    // draw circle
                    paint.setColor(this.upRightColor);
                    c.drawCircle(touchInput.x, touchInput.y, thumbRadius, paint);
                    break;
                case DOWN:
                    // draw circle
                    paint.setColor(this.downColor);
                    c.drawCircle(touchInput.x, touchInput.y, thumbRadius, paint);
                    break;
                case JUMP:
                    // draw circle
                    paint.setColor(this.jumpColor);
                    c.drawCircle(touchInput.x, touchInput.y, thumbRadius, paint);
                    break;
                case FIRE:
                    // draw circle
                    paint.setColor(this.fireColor);
                    c.drawCircle(touchInput.x, touchInput.y, thumbRadius, paint);
                    break;
                case JUMP_AND_FIRE:
                    // draw circle
                    paint.setColor(this.jumpColor);
                    c.drawCircle(touchInput.x, touchInput.y, thumbRadius, paint);
                    // draw circle
                    paint.setColor(this.fireColor);
                    c.drawCircle(touchInput.x, touchInput.y, thumbRadius, paint);
                    break;
            }
        }
    }

    public void render() {
        Log.d("GamePanel", "render()");
        Canvas c = holder.lockCanvas();
        //c.drawColor(Color.BLACK); // TODO: change back to BLACK
        c.drawColor(Color.DKGRAY);
        this.tilemap.render(c, this.camera.getX(), this.camera.getY());
        Log.d("GamePanel", "after tilemap.render()");
        renderPlayer(c);
        renderBullets(c);

        renderControlColors(c);
        renderControls(c);
        holder.unlockCanvasAndPost(c);
        clearInputArrayList();
        Log.d("GamePanel", "end of render()");
    }

    private void updatePlayerAnimation() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            // change frame in sprite here
            this.playerFrame++;
            int maxFrame = player.getMaxFrame();
            if (this.playerFrame > maxFrame) {
                this.playerFrame = 0;
            }
        }
    }

    public void processInput() {
        processMultiTouchEvent();
    }

    private void processMultiTouchEvent() {
        synchronized (touchEventLock) {
            MultiTouchEvent event = this.latestTouchEvent;
            if (event != null) {
                for (int i = 0; i < event.pointerCount; i++) {
                    float x = event.touchX[i];
                    float y = event.touchY[i];
                    int action = event.action[i];

                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_POINTER_DOWN:
                            //Log.d("GamePanel", String.format("Touch: x = %f, y = %f", x, y));
                            populateInputArray(x, y);
                            break;

                        case MotionEvent.ACTION_MOVE:
                            //Log.d("GamePanel", String.format("Touch: x = %f, y = %f", x, y));
                            populateInputArray(x, y);
                            break;

                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_POINTER_UP:
                            Log.d("GamePanel", "ACTION_POINTER_UP");
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            // Handle the touch up event for this point, if necessary
                            break;
                    }
                }
            }
        }
    }

    private void populateInputArray(float x, float y) {
        if (isTouchPointValid(x, y) && (isTouchPointInDownTriangle(x, y) || isTouchPointInDownRectangle(x, y))) {
            // UP
            this.inputArr.add(new TouchInput(Input.DOWN, x, y));
        } else if (x > 0 && x <= (float)triPadStartXPos + (float)(triPadWidth / 2)) {
            // LEFT
            this.inputArr.add(new TouchInput(Input.LEFT, x, y));
        } else if (x > (float)triPadStartXPos + (float)(triPadWidth / 2) && x <= (float)triPadStartXPos * 2 + (float)triPadWidth) {
            // RIGHT
            this.inputArr.add(new TouchInput(Input.RIGHT, x, y));
        } else if (x > capsuleStartXPos - capsuleOffset) {
            if (y > (float)(this.mainActivity.getWindowHeight() / 2) - jumpAndFireOffset &&
                    y < (float)(this.mainActivity.getWindowHeight() / 2) + jumpAndFireOffset) {
                // Jump and Fire
                this.inputArr.add(new TouchInput(Input.JUMP_AND_FIRE, x, y));
            } else if (y <= (float)(this.mainActivity.getWindowHeight() / 2)) {
                // fire
                this.inputArr.add(new TouchInput(Input.FIRE, x, y));
            } else {
                // jump
                this.inputArr.add(new TouchInput(Input.JUMP, x, y));
            }
        }
    }

    private boolean isTouchPointValid(float x, float y) {
        return x > 0 && y > 0 && x < this.mainActivity.getWindowWidth() && y < this.mainActivity.getWindowHeight();
    }

    private boolean isTouchPointInDownRectangle(float x, float y) {
        float px1 = 0;
        float px2 = this.triPadStartXPos * 2 + this.triPadWidth;
        float py1 = ((float) this.mainActivity.getWindowHeight() / 4) * 3;
        float py2 = this.mainActivity.getWindowHeight();
        return (x > px1 && x < px2) && (y > py1 && y < py2);
    }

    private boolean isTouchPointInDownTriangle(float px, float py) {
        float denominator = ((downY2 - downY3) * (downX1 - downX3) + (downX3 - downX2) * (downY1 - downY3));
        float a = ((downY2 - downY3) * (px - downX3) + (downX3 - downX2) * (py - downY3)) / denominator;
        float b = ((downY3 - downY1) * (px - downX3) + (downX1 - downX3) * (py - downY3)) / denominator;
        float c = 1 - a - b;

        // Check if the point is inside the triangle
        return a >= 0 && b >= 0 && c >= 0 && a <= 1 && b <= 1 && c <= 1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        synchronized (touchEventLock) {
            int action = event.getActionMasked();
            int pointerIndex = event.getActionIndex();
            int pointerCount = event.getPointerCount();

            if (action == MotionEvent.ACTION_CANCEL) {
                latestTouchEvent = null;
                return true;
            }

            MultiTouchEvent tempTouchEvent = new MultiTouchEvent(pointerCount);
            int currentPointerIndex = 0;
            for (int i = 0; i < pointerCount; i++) {
                if (i == pointerIndex && (action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_UP)) {
                    // Skip the pointer that is going up
                    continue;
                }
                float x = event.getX(i);
                float y = event.getY(i);
                int pointerAction = (i == pointerIndex) ? action : MotionEvent.ACTION_MOVE; // change to ACTION_MOVE here to register the remaining finger
                tempTouchEvent.setPointerData(currentPointerIndex++, x, y, pointerAction);
            }
            latestTouchEvent = tempTouchEvent;
        }

        return true;  // Return true to indicate the event was handled
    }


    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        gameLoop.startGameLoop();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }
}
