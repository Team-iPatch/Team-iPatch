/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
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
public class EnemyControl extends AbstractControl{
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.
    private int hp;
    PhysicsSpace physicsSpace;
    
    public EnemyControl(PhysicsSpace physicsSpace){
        this.hp = 10;
        this.physicsSpace = physicsSpace;
    }
    
    public EnemyControl(PhysicsSpace physicsSpace, int hp){
		this.hp = hp;
		this.physicsSpace = physicsSpace;
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
        physicsSpace.remove(spatial.getControl(BetterCharacterControl.class));
    }
    
    public void setHP(int hp){
        this.hp = hp;
    }
    
    public void reduceHP(int reduction){
        this.hp -= reduction;
    }
    
    public int getHP(){
        return this.hp;
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
