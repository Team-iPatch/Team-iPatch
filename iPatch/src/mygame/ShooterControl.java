/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Sphere;
import java.io.IOException;

/**
 * Controls a single "cannon", entity which fires bullets in a certain direction.
 * @author Team iPatch
 */
public class ShooterControl extends AbstractControl {
    Vector3f direction;
    int damage;
    boolean isEnemy;
    private static final Sphere sphere;
    SimpleApplication app;
    private BulletControl bulletControl;
    PhysicsSpace physicsSpace;
    PlayerControlState playerControlState;
    
    static {
        sphere = new Sphere(8, 8, 0.4f, true, false);
    }
    
    /**
     * Initialises a cannon.
     * @param direction Direction in which the cannon will fire.
     * @param isEnemy Whether the cannon is attached to an enemy.
     * @param app Application object.
     */
    public ShooterControl(Vector3f direction, boolean isEnemy, SimpleApplication app) {
        this.direction = direction;
        this.damage = 5;
        this.isEnemy = isEnemy;
        this.app = (SimpleApplication)app;
        this.physicsSpace = app.getStateManager().getState(BulletAppState.class)
                                                             .getPhysicsSpace();
        playerControlState = app.getStateManager().getState(PlayerControlState.class);
    }
    
    /**
     * Used to make the cannon fire a single bullet
     */
//    public void shootBullet(){    
//        // Makes the bullet a simple white sphere
//        Geometry bullet_geo = new Geometry("cannon ball", sphere);
//        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//        bullet_geo.setMaterial(mat);
//        app.getRootNode().attachChild(bullet_geo);
//        // Spawns the bullet at the location of the cannon
//	bullet_geo.setLocalTranslation(spatial.getWorldTranslation().add(
//                                       direction.mult(1f).add(0, 1f, 0))); 
//        // add(0,1f,0) raises bullet off floor
//        // addition is required for the bullets not to spawn underneath ships
//        
//        // Creates a collider for the bullet, instantiates a BulletControl
//        // to handle bullet trajectory.
//	RigidBodyControl bullet_phys = new RigidBodyControl(2f);
//	bullet_geo.addControl(bullet_phys);
//	physicsSpace.add(bullet_phys);
//	bullet_phys.setKinematic(true);
//	bulletControl = new BulletControl(direction, damage, isEnemy, physicsSpace,
//                                          bullet_phys, playerControlState);
//        bullet_geo.addControl(bulletControl);
//    }
    
    /**
     * Used to make the cannon fire a bullet in a particular direction.
     * @param direction Vector3f indicating direction to fire.
     */
    public void shootBullet(Vector3f direction){
        // For shooters that cannot pass a reference to their direction
        Geometry bullet_geo = new Geometry("cannon ball", sphere);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        bullet_geo.setMaterial(mat);
        app.getRootNode().attachChild(bullet_geo);
	bullet_geo.setLocalTranslation(spatial.getWorldTranslation().add(
                                       direction.mult(1f).add(0, 1f, 0)));
        // add(0,1f,0) raises bullet off floor
        // addition is required for the bullets not to spawn underneath
	RigidBodyControl bullet_phys = new RigidBodyControl(2f);
	bullet_geo.addControl(bullet_phys);
	physicsSpace.add(bullet_phys);
	bullet_phys.setKinematic(true);
	bulletControl = new BulletControl(direction, damage, isEnemy, physicsSpace,
                                          bullet_phys, playerControlState);
        bullet_geo.addControl(bulletControl);
    }
    
    /**
     * Returns damage dealt by bullets from this cannon.
     * @return int, bullet damage
     */
    public int getDamage(){
        return this.damage;
    }
    
    /**
     * Sets damage dealt by bullets from this cannon.
     * @param dmg int, new damage value
     */
    public void setDamage(int dmg){
        this.damage = dmg;
    }
    
    @Override
    protected void controlUpdate(float tpf) {}
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
}
