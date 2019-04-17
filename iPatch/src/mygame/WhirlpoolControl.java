/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author chris
 */
public class WhirlpoolControl extends AbstractControl {
    
    private Vector3f teleportPosition;
    private GhostControl ghost;
    private PlayerControlState player;
    private Boolean inWhirlpool;
    private float animationTimer;
    private final float rotationSpeed = 0.5f;
    
    private final float animationTime = 2f;
    
    public WhirlpoolControl(Vector3f teleportPosition, GhostControl ghost, PlayerControlState player){
        this.teleportPosition = teleportPosition;
        this.ghost = ghost;
        this.player = player;
        inWhirlpool = false;
        animationTimer = 0;
    }

    @Override
    protected void controlUpdate(float tpf) {
        BetterCharacterControl playerControl = player.getPlayerSpatial().
                               getControl(BetterCharacterControl.class);
        inWhirlpool = false;
        for(PhysicsCollisionObject obj : ghost.getOverlappingObjects()){
            if (obj.getUserObject().getClass() == Node.class){
                Node userObject = (Node)obj.getUserObject();
                if(userObject.getName().equals("player")){
                    inWhirlpool = true;
                }
            }
        }
        if (!inWhirlpool) {
            animationTimer = animationTime;
        } else {
            if (animationTimer == animationTime){
                playerControl.warp(spatial.getWorldTranslation());
            }
            player.rotateShip(rotationSpeed);
            animationTimer -= tpf;
            if (animationTimer <= 0){
                playerControl.warp(teleportPosition);
            }
        }
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
}
