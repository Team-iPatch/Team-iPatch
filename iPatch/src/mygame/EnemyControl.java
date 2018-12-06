/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
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
    public EnemyControl(BulletAppState bulletAppState){
	this.hp = 10;
	this.enemy = spatial;
	this.bulletAppState = bulletAppState;
	bulletAppState.getPhysicsSpace().addCollisionListener(this);
    }
    
    public EnemyControl(BulletAppState bulletAppState, int hp){
	this.hp = hp;
	this.enemy = spatial;
	bulletAppState.getPhysicsSpace().addCollisionListener(this);
	this.bulletAppState = bulletAppState;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
	if(this.hp <= 0){
	    spatial.getParent().detachChild(spatial);
	    bulletAppState.getPhysicsSpace().remove(spatial.getControl(BetterCharacterControl.class));
	    bulletAppState.getPhysicsSpace().removeCollisionListener(this);
	}
	//TODO: add code that controls Spatial,
	//e.g. spatial.rotate(tpf,tpf,tpf);
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
	//Only needed for rendering-related operations,
	//not called when spatial is culled.
    }
    
    @Override
    public Control cloneForSpatial(Spatial spatial) {
	EnemyControl control = new EnemyControl(bulletAppState);
	//TODO: copy parameters to new Control
	return control;
    }
    
    @Override
    public void read(JmeImporter im) throws IOException {
	super.read(im);
	InputCapsule in = im.getCapsule(this);
	//TODO: load properties of this Control, e.g.
	//this.value = in.readFloat("name", defaultValue);
    }
    
    @Override
    public void write(JmeExporter ex) throws IOException {
	super.write(ex);
	OutputCapsule out = ex.getCapsule(this);
	//TODO: save properties of this Control, e.g.
	//out.write(this.value, "name", defaultValue);
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
	if(event.getNodeA().getName().equals("bullet") || event.getNodeB().getName().equals("bullet")){
	    this.hp -= 5;
	}
    }
    
}
