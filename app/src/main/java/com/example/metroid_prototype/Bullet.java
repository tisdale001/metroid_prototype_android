package com.example.metroid_prototype;

public class Bullet {
    private GameObject bulletObj;
    private TransformComponent bulletTransform;
    private PhysicsComponent bulletPhysics;
    private Sprite bulletSprite;
    private int shotLifeCounterMax = 30;
    private int shotLifeCounter = 0;
    private boolean readyToDestroy = false;

    public Bullet() {
        createBullet();
    }

    private void createBullet() {
        bulletObj = new GameObject();
        bulletTransform = new TransformComponent();
        bulletPhysics = new PhysicsComponent();
        bulletSprite = new Sprite(true);
        bulletSprite.setRectangleDimensions(10, 10);
        bulletSprite.setSpriteSheetDimensions(8, 8, 21, 289, 1, 1, 0);
        bulletSprite.loadDrawable(R.drawable.metroid_weapons_sprite_sheet);
        bulletObj.addSpriteComponent(bulletSprite);
        bulletObj.addTransformComponent(bulletTransform);
        bulletObj.addPhysicsComponent(bulletPhysics);
    }

    public void setStartingPoint(int x, int y) {
        this.bulletTransform.setXPos(x);
        this.bulletTransform.setYPos(y);
    }

    public void setVelocity(int xVel, int yVel) {
        this.bulletPhysics.setXVel(xVel);
        this.bulletPhysics.setYVel(yVel);
    }

    public PhysicsComponent getBulletPhysics() {
        return this.bulletPhysics;
    }

    public TransformComponent getBulletTransform() {
        return this.bulletTransform;
    }

    public GameObject getBulletObj() {
        return this.bulletObj;
    }

    public void incrementShotLifeCounter() {
        this.shotLifeCounter += 1;
        if (this.shotLifeCounter > this.shotLifeCounterMax) {
            this.readyToDestroy = true;
        }
    }

    public boolean isReadyToDestroy() {
        return this.readyToDestroy;
    }
}
