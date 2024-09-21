package com.example.metroid_prototype;

import com.example.metroid_prototype.helpers.PlayerState;

import java.util.HashMap;

public class Player {
    private int runSpeed;
    private int fallingSpeed;

    private GameObject playerObj;
    private PlayerState curState;
    private int startXPos;
    private int startYPos;
    private HashMap<Sprite, Integer> maxFrameMap = new HashMap<>();
    private Sprite runRightSprite;
    private Sprite runLeftSprite;
    private Sprite idleRightSprite;
    private Sprite idleLeftSprite;
    private Sprite duckRightSprite;
    private Sprite duckLeftSprite;
    private Sprite rollRightSprite;
    private Sprite rollLeftSprite;
//    private Sprite climbRightSprite;
//    private Sprite climbLeftSprite;
    private Sprite somersaultRightSprite;
    private Sprite somersaultLeftSprite;
    private Sprite fallingRightSprite;
    private Sprite fallingLeftSprite;
    private Sprite shootingRunRightSprite;
    private Sprite shootingRunLeftSprite;
    private Sprite shootingFallingRightSprite;
    private Sprite shootingFallingLeftSprite;


    public Player(int startXPos, int startYPos, int runSpeed, int fallingSpeed) {
        this.startXPos = startXPos;
        this.startYPos = startYPos;
        this.runSpeed = runSpeed;
        this.fallingSpeed = fallingSpeed;
        initiatePlayer();
    }

    private void initiatePlayer() {
        playerObj = new GameObject();
        TransformComponent playerTransform = new TransformComponent();
        playerObj.addTransformComponent(playerTransform);
        playerTransform.setXPos(this.startXPos);
        playerTransform.setYPos(this.startYPos);
        PhysicsComponent playerPhysics = new PhysicsComponent();
        playerPhysics.setXVel(this.runSpeed);
        playerPhysics.setYVel(this.fallingSpeed);
        playerObj.addPhysicsComponent(playerPhysics);
        JumpComponent playerJump = new JumpComponent(-320, 550, runSpeed, -2);
        playerObj.addJumpComponent(playerJump);
        createPlayerSprites();
    }

    private void createPlayerSprites() {
        runRightSprite = new Sprite(true);
        runRightSprite.setRectangleDimensions(54, 96); // was 68, 120
        runRightSprite.setSpriteSheetDimensions(20, 32, 135, 117, 3, 3, 0);
        runRightSprite.loadDrawable(R.drawable.metroid_spritesheet2);
        maxFrameMap.put(this.runRightSprite, 2);

        runLeftSprite = new Sprite(true);
        runLeftSprite.setRectangleDimensions(54, 96);
        runLeftSprite.setSpriteSheetDimensions(20, 32, 68, 117, 3, 3, 0);
        runLeftSprite.loadDrawable(R.drawable.metroid_spritesheet2);
        maxFrameMap.put(this.runLeftSprite, 2);

        idleRightSprite = new Sprite(true);
        idleRightSprite.setRectangleDimensions(54, 96);
        idleRightSprite.setSpriteSheetDimensions(22, 32, 137, 40, 1, 1, 0);
        idleRightSprite.loadDrawable(R.drawable.metroid_spritesheet2);
        maxFrameMap.put(this.idleRightSprite, 0);

        idleLeftSprite = new Sprite(true);
        idleLeftSprite.setRectangleDimensions(54, 96);
        idleLeftSprite.setSpriteSheetDimensions(22, 32, 109, 40, 1, 1, 0);
        idleLeftSprite.loadDrawable(R.drawable.metroid_spritesheet2);
        maxFrameMap.put(this.idleLeftSprite, 0);

        duckRightSprite = new Sprite(true);
        duckRightSprite.setRectangleDimensions(54, 65); // was 68, 80
        duckRightSprite.setSpriteSheetDimensions(22, 23, 181, 50, 1, 1, 0);
        duckRightSprite.loadDrawable(R.drawable.metroid_spritesheet2);
        maxFrameMap.put(this.duckRightSprite, 0);

        duckLeftSprite = new Sprite(true);
        duckLeftSprite.setRectangleDimensions(54, 65);
        duckLeftSprite.setSpriteSheetDimensions(22, 23, 63, 50, 1, 1, 0);
        duckLeftSprite.loadDrawable(R.drawable.metroid_spritesheet2);
        maxFrameMap.put(this.duckLeftSprite, 0);

        rollRightSprite = new Sprite(true);
        rollRightSprite.setRectangleDimensions(47, 47); // was 64, 64
        rollRightSprite.setSpriteSheetDimensions(15, 15, 135, 150, 4, 4, 0);
        rollRightSprite.loadDrawable(R.drawable.metroid_spritesheet2);
        maxFrameMap.put(this.rollRightSprite, 3);

        rollLeftSprite = new Sprite(false);
        rollLeftSprite.setRectangleDimensions(47, 47);
        rollLeftSprite.setSpriteSheetDimensions(15, 15, 131, 150, 4, 4, 0);
        rollLeftSprite.loadDrawable(R.drawable.metroid_spritesheet2);
        maxFrameMap.put(this.rollLeftSprite, 3);

        somersaultRightSprite = new Sprite(true);
        somersaultRightSprite.setRectangleDimensions(60, 66); // was 76, 84
        somersaultRightSprite.setSpriteSheetDimensions(19, 26, 135, 195, 4, 4, 0);
        somersaultRightSprite.loadDrawable(R.drawable.metroid_spritesheet2);
        maxFrameMap.put(this.somersaultRightSprite, 3);

        somersaultLeftSprite = new Sprite(false);
        somersaultLeftSprite.setRectangleDimensions(60, 66);
        somersaultLeftSprite.setSpriteSheetDimensions(20, 26, 133, 195, 4, 4, 0);
        somersaultLeftSprite.loadDrawable(R.drawable.metroid_spritesheet2);
        maxFrameMap.put(this.somersaultLeftSprite, 3);

        fallingRightSprite = new Sprite(true);
        fallingRightSprite.setRectangleDimensions(54, 87); // was 68, 110
        fallingRightSprite.setSpriteSheetDimensions(22, 27, 135, 168, 1, 1, 0);
        fallingRightSprite.loadDrawable(R.drawable.metroid_spritesheet2);
        maxFrameMap.put(this.fallingRightSprite, 0);

        fallingLeftSprite = new Sprite(false);
        fallingLeftSprite.setRectangleDimensions(54, 87);
        fallingLeftSprite.setSpriteSheetDimensions(22, 27, 131, 168, 1, 1, 0);
        fallingLeftSprite.loadDrawable(R.drawable.metroid_spritesheet2);
        maxFrameMap.put(this.fallingLeftSprite, 0);

        shootingRunRightSprite = new Sprite(true);
        shootingRunRightSprite.setRectangleDimensions(54, 96);
        shootingRunRightSprite.setSpriteSheetDimensions(25, 32, 135, 221, 3, 3, 0);
        shootingRunRightSprite.loadDrawable(R.drawable.metroid_spritesheet2);
        maxFrameMap.put(this.shootingRunRightSprite, 2);

        shootingRunLeftSprite = new Sprite(true);
        shootingRunLeftSprite.setRectangleDimensions(54, 96);
        shootingRunLeftSprite.setSpriteSheetDimensions(25, 32, 55, 221, 3, 3, 0);
        shootingRunLeftSprite.loadDrawable(R.drawable.metroid_spritesheet2);
        maxFrameMap.put(this.shootingRunLeftSprite, 2);

        shootingFallingRightSprite = new Sprite(true);
        shootingFallingRightSprite.setRectangleDimensions(54, 87);
        shootingFallingRightSprite.setSpriteSheetDimensions(25, 27, 135, 255, 1, 1, 0);
        shootingFallingRightSprite.loadDrawable(R.drawable.metroid_spritesheet2);
        maxFrameMap.put(this.shootingFallingRightSprite, 0);

        shootingFallingLeftSprite = new Sprite(false);
        shootingFallingLeftSprite.setRectangleDimensions(54, 87);
        shootingFallingLeftSprite.setSpriteSheetDimensions(25, 27, 131, 255, 1, 1, 0);
        shootingFallingLeftSprite.loadDrawable(R.drawable.metroid_spritesheet2);
        maxFrameMap.put(this.shootingFallingLeftSprite, 0);

        playerObj.addSpriteComponent(idleRightSprite);
        this.curState = PlayerState.IDLE_RIGHT;
    }

    public GameObject getPlayerObj() {
        return this.playerObj;
    }

    public void changeSprite(PlayerState playerState) {
        switch (playerState) {
            case RUN_RIGHT:
                playerObj.addSpriteComponent(runRightSprite);
                curState = PlayerState.RUN_RIGHT;
                break;
            case RUN_LEFT:
                playerObj.addSpriteComponent(runLeftSprite);
                curState = PlayerState.RUN_LEFT;
                break;
            case IDLE_RIGHT:
                playerObj.addSpriteComponent(idleRightSprite);
                curState = PlayerState.IDLE_RIGHT;
                break;
            case IDLE_LEFT:
                playerObj.addSpriteComponent(idleLeftSprite);
                curState = PlayerState.IDLE_LEFT;
                break;
            case DUCK_RIGHT:
                playerObj.addSpriteComponent(duckRightSprite);
                curState = PlayerState.DUCK_RIGHT;
                break;
            case DUCK_LEFT:
                playerObj.addSpriteComponent(duckLeftSprite);
                curState = PlayerState.DUCK_LEFT;
                break;
            case ROLL_RIGHT:
                playerObj.addSpriteComponent(rollRightSprite);
                curState = PlayerState.ROLL_RIGHT;
                break;
            case ROLL_LEFT:
                playerObj.addSpriteComponent(rollLeftSprite);
                curState = PlayerState.ROLL_LEFT;
                break;
            case ABOUT_TO_STAND_RIGHT:
                playerObj.addSpriteComponent(duckRightSprite);
                curState = PlayerState.ABOUT_TO_STAND_RIGHT;
                break;
            case ABOUT_TO_STAND_LEFT:
                playerObj.addSpriteComponent(duckLeftSprite);
                curState = PlayerState.ABOUT_TO_STAND_LEFT;
                break;
            case SOMERSAULT_RIGHT:
                playerObj.addSpriteComponent(somersaultRightSprite);
                curState = PlayerState.SOMERSAULT_RIGHT;
                break;
            case SOMERSAULT_LEFT:
                playerObj.addSpriteComponent(somersaultLeftSprite);
                curState = PlayerState.SOMERSAULT_LEFT;
                break;
            case FALLING_RIGHT:
                playerObj.addSpriteComponent(fallingRightSprite);
                curState = PlayerState.FALLING_RIGHT;
                break;
            case FALLING_LEFT:
                playerObj.addSpriteComponent(fallingLeftSprite);
                curState = PlayerState.FALLING_LEFT;
                break;
            case JUMPING_RIGHT:
                playerObj.addSpriteComponent(fallingRightSprite);
                curState = PlayerState.JUMPING_RIGHT;
                break;
            case JUMPING_LEFT:
                playerObj.addSpriteComponent(fallingLeftSprite);
                curState = PlayerState.JUMPING_LEFT;
                break;
            case SHOOTING_FALL_RIGHT:
                playerObj.addSpriteComponent(shootingFallingRightSprite);
                curState = PlayerState.SHOOTING_FALL_RIGHT;
                break;
            case SHOOTING_FALL_LEFT:
                playerObj.addSpriteComponent(shootingFallingLeftSprite);
                curState = PlayerState.SHOOTING_FALL_LEFT;
                break;
            case SHOOTING_IDLE_RIGHT:
                playerObj.addSpriteComponent(idleRightSprite);
                curState = PlayerState.SHOOTING_IDLE_RIGHT;
                break;
            case SHOOTING_IDLE_LEFT:
                playerObj.addSpriteComponent(idleLeftSprite);
                curState = PlayerState.SHOOTING_IDLE_LEFT;
                break;
            case SHOOTING_RUN_RIGHT:
                playerObj.addSpriteComponent(shootingRunRightSprite);
                curState = PlayerState.SHOOTING_RUN_RIGHT;
                break;
            case SHOOTING_RUN_LEFT:
                playerObj.addSpriteComponent(shootingRunLeftSprite);
                curState = PlayerState.SHOOTING_RUN_LEFT;
                break;
            case SHOOTING_JUMP_RIGHT:
                playerObj.addSpriteComponent(shootingFallingRightSprite);
                curState = PlayerState.SHOOTING_JUMP_RIGHT;
                break;
            case SHOOTING_JUMP_LEFT:
                playerObj.addSpriteComponent(shootingFallingLeftSprite);
                curState = PlayerState.SHOOTING_JUMP_LEFT;
                break;
        }
    }

    public PlayerState getCurState() {
        return this.curState;
    }

    public int getMaxFrame() {
        return this.maxFrameMap.getOrDefault(playerObj.getSprite(), 0);
    }

    public Sprite getAnySprite(PlayerState playerState) {
        switch (playerState) {
            case RUN_RIGHT:
                return this.runRightSprite;
            case RUN_LEFT:
                return this.runLeftSprite;
            case IDLE_RIGHT:
                return this.idleRightSprite;
            case IDLE_LEFT:
                return this.idleLeftSprite;
            case FALLING_RIGHT:
                return this.fallingRightSprite;
            case FALLING_LEFT:
                return this.fallingLeftSprite;
            case DUCK_LEFT:
                return this.duckLeftSprite;
            case DUCK_RIGHT:
                return this.duckRightSprite;
            case ROLL_RIGHT:
                return this.rollRightSprite;
            case ROLL_LEFT:
                return this.rollLeftSprite;
            case ABOUT_TO_STAND_RIGHT:
                return this.duckRightSprite;
            case ABOUT_TO_STAND_LEFT:
                return this.duckLeftSprite;
            case SOMERSAULT_RIGHT:
                return this.somersaultRightSprite;
            case SOMERSAULT_LEFT:
                return this.somersaultLeftSprite;
            case JUMPING_RIGHT:
                return this.fallingRightSprite;
            case JUMPING_LEFT:
                return this.fallingLeftSprite;
            case SHOOTING_RUN_RIGHT:
                return this.shootingRunRightSprite;
            case SHOOTING_RUN_LEFT:
                return this.shootingRunLeftSprite;
            case SHOOTING_FALL_RIGHT:
                return this.shootingFallingRightSprite;
            case SHOOTING_FALL_LEFT:
                return this.shootingFallingLeftSprite;
            case SHOOTING_IDLE_RIGHT:
                return this.idleRightSprite;
            case SHOOTING_IDLE_LEFT:
                return this.idleLeftSprite;
            case SHOOTING_JUMP_RIGHT:
                return this.shootingFallingRightSprite;
            case SHOOTING_JUMP_LEFT:
                return this.shootingFallingLeftSprite;
            default:
                // change to null?
                return this.rollRightSprite;
        }
    }
}
