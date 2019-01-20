/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * AIChaserControl implements a simple chasing AI mechanism when it is attached to 
 * any entity with a BetterCharacterControl.
 * 
 * @author Team iPatch
 * 
 */
public class AIChaserControl extends AbstractControl implements PhysicsCollisionListener{
    BetterCharacterControl enemyControl;
    Spatial target;
    State state;
    Vector3f moveDirection, viewDirection;
    float speed;
    int collisionDamage;
    PlayerControlState player;
    PhysicsSpace physicsSpace;
    boolean alive;

    private enum State{
        Idle,
        Chasing
    }
    
    /**
     * Implements a simple chasing AI mechanism when attached to any entity with a
     * BetterCharacterControl.
     * @param target Spatial the entity will chase.
     * @param speed Speed of the entity.
     * @param player PlayerControlState representing the player. Used for dealing damage.
     * @param physicsSpace PhysicsSpace to which the entity will attach its collision listener.
     */
    public AIChaserControl(Spatial target, float speed, 
                          PlayerControlState player, PhysicsSpace physicsSpace){
        alive = true;
        this.target = target;
        this.state = State.Idle;
        this.speed = speed;
        this.collisionDamage = 10;
        this.moveDirection = Vector3f.UNIT_XYZ;
        this.viewDirection = Vector3f.UNIT_XYZ;
        this.player = player;
        this.physicsSpace = physicsSpace;
        physicsSpace.addCollisionListener(this);
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        if (!alive){
            physicsSpace.removeCollisionListener(this);
        }
        switch(this.state){
            case Idle:
                Idle();
            case Chasing:
                Chase();
        }
    }
    
    private void Idle(){
        
    }
    
    private void Chase(){
        /*
        Simple chaser script. The entity will rotate itself to face the target and
        the BetterCharacterControl will be instructed to walk in the player's 
        direction.
        */
        if(this.target != null){
            // Gets vector3f which points the entity towards the player 
            Vector3f targetDirection = this.target.getWorldTranslation().subtract(spatial.getWorldTranslation());
            // Sets y value to 0 (no vertical movement) and normalises vector
            targetDirection.y = 0;
            targetDirection.normalizeLocal();
            this.viewDirection.set(targetDirection);
            spatial.getControl(BetterCharacterControl.class).setViewDirection(this.viewDirection);
            
            Vector3f forwardDirection = spatial.getWorldRotation().mult(Vector3f.UNIT_Z);
            this.moveDirection.addLocal(forwardDirection.mult(this.speed));
            spatial.getControl(BetterCharacterControl.class).setWalkDirection(this.moveDirection);
        }
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }    
    
    @Override
    public void collision(PhysicsCollisionEvent event) {
        /** 
        * Used internally to handle collisions with the player, do not
        * call this method. 
        */
        Spatial nodeA = event.getNodeA();
        Spatial nodeB = event.getNodeB();
        if((nodeA.equals(spatial) && nodeB.getName().equals("player")) ||
                   (nodeB.equals(spatial) && nodeA.getName().equals("player"))){
            alive = false;
            player.reduceHP(collisionDamage);
            spatial.getControl(EnemyControl.class).setHP(0);
        }
    }
}
