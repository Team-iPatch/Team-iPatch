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
 * Used to manage the health of an enemy Spatial, cleaning up entity after
 * it is destroyed.
 * @author Team iPatch
 */
public class EnemyControl extends AbstractControl{
    private int hp;
    PhysicsSpace physicsSpace;
    
    /**
     * Used to manage the health of an enemy Spatial, cleaning up the entity
     * after it is destroyed.
     * @param physicsSpace PhysicsSpace of the application.
     */
    public EnemyControl(PhysicsSpace physicsSpace){
        this.hp = 10;
        this.physicsSpace = physicsSpace;
    }
    
    /**
     * Used to manage the health of an enemy Spatial, cleaning up the entity
     * after it is destroyed.
     * @param physicsSpace PhysicsSpace of the application.
     * @param hp Enemy health.
     */
    public EnemyControl(PhysicsSpace physicsSpace, int hp){
        this.hp = hp;
        this.physicsSpace = physicsSpace;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        if(this.hp <= 0){
                kill();
        }        
    }
    
    public void kill(){
        // Do not call outside of controlUpdate, set hp to 0 instead
        spatial.getParent().detachChild(spatial);
        physicsSpace.remove(spatial.getControl(BetterCharacterControl.class));                                                                                                                              
    }
    
    /**
     * Sets HP to to value of argument.
     * @param hp Integer for new enemy HP.
     */
    public void setHP(int hp){
        this.hp = hp;
    }
    
    /**
     * Reduces HP by a value.
     * @param reduction Value to reduce HP by.
     */
    public void reduceHP(int reduction){
        this.hp -= reduction;
    }
    
    /**
     * Returns entity's HP.
     * @return int, entity HP
     */
    public int getHP(){
        return this.hp;
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
