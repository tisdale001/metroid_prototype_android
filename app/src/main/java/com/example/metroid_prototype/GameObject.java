package com.example.metroid_prototype;

public class GameObject {
    private TransformComponent transformComponent;
    private JumpComponent jumpComponent;
    private PhysicsComponent physicsComponent;
    private Sprite sprite;
    private boolean initiateJump = false;

    public void addTransformComponent(TransformComponent transformComponent) {
        this.transformComponent = transformComponent;
    }

    public void addJumpComponent(JumpComponent jumpComponent) {
        this.jumpComponent = jumpComponent;
    }

    public void addPhysicsComponent(PhysicsComponent physicsComponent) {
        this.physicsComponent = physicsComponent;
    }

    public void addSpriteComponent(Sprite sprite) {
        this.sprite = sprite;
    }

    public boolean getInitiateJump() {
        return this.initiateJump;
    }

    public void setInitiateJump(boolean initiate) {
        this.initiateJump = initiate;
    }

    public PhysicsComponent getPhysicsComponent() {
        return this.physicsComponent;
    }

    public TransformComponent getTransformComponent() {
        return this.transformComponent;
    }

    public JumpComponent getJumpComponent() {
        return this.jumpComponent;
    }

    public Sprite getSprite() {
        return this.sprite;
    }
}