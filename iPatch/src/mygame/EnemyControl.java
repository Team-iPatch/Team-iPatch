/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author Blue
 */
public class EnemyControl extends AbstractControl implements PhysicsCollisionListener {
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.
    int hp;
    Spatial enemy;
    BulletAppState bulletAppState;
    PlayerControlState playerControlState;
    
    public EnemyControl(BulletAppState bulletAppState, PlayerControlState playerControlState){
		this.hp = 10;
		this.enemy = spatial;
		this.bulletAppState = bulletAppState;
        this.playerControlState = playerControlState;
		bulletAppState.getPhysicsSpace().addCollisionListener(this);
        
    }
    
    public EnemyControl(BulletAppState bulletAppState, PlayerControlState playerControlState, int hp){
		this.hp = hp;
		this.enemy = spatial;
        this.playerControlState = playerControlState;
		bulletAppState.getPhysicsSpace().addCollisionListener(this);
		this.bulletAppState = bulletAppState;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
		if(this.hp <= 0){
			kill();
		}
        //Maybe usable to lock enemy to vertical location
        //spatial.setLocalTranslation(spatial.getLocalTranslation().x, 0.5f, spatial.getLocalTranslation().z);
        //spatial.getControl(BetterCharacterControl.class).warp(spatial.getLocalTranslation());
        
    }
    
    public void kill(){
        // Do not call outside of controlUpdate, set hp to 0 instead
        spatial.getParent().detachChild(spatial);
		bulletAppState.getPhysicsSpace().remove(spatial.getControl(BetterCharacterControl.class));
		bulletAppState.getPhysicsSpace().removeCollisionListener(this);
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
		//Only needed for rendering-related operations,
		//not called when spatial is culled.
    }
    
    @Override
    public Control cloneForSpatial(Spatial spatial) {
		EnemyControl control = new EnemyControl(bulletAppState, playerControlState);
		//TODO: copy parameters to new Control
		return control;
    }
    
    @Override
    public void collision(PhysicsCollisionEvent event) {
        if(event.getNodeA().equals(spatial) || event.getNodeB().equals(spatial)){
            if(event.getNodeA().getName().equals("cannon ball") || event.getNodeB().getName().equals("cannon ball")){
                this.hp -= 5;
                if(event.getNodeA().getName().equals("cannon ball"))
                    event.getNodeA().getControl(BulletControl.class).destroy();
                if(event.getNodeB().getName().equals("cannon ball"))
                    event.getNodeB().getControl(BulletControl.class).destroy();
            }
            else if(event.getNodeA().getName().equals("Player") || event.getNodeB().getName().equals("Player")){
                playerControlState.reduceHP(10);
                this.hp = 0;
            }
        }    
    }
}
