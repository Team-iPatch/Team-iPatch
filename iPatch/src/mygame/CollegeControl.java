/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author cpl512
 */
public class CollegeControl extends AbstractControl {
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.
    private GhostControl ghost;
    private final String name;
    private float ghostRadius;
    private Spatial cannons[];
    private Node collegeNode;
    private boolean inCombat;
    private Quaternion rotation;
    private double shootChance;
    private int hp;
    PhysicsSpace physicsSpace;
    
    CollegeControl(String name, GhostControl ghost, Spatial[] cannons, 
                   Node collegeNode, PhysicsSpace physicsSpace){
        this.name = name;
        this.ghost = ghost;
        this.cannons = cannons;
        this.collegeNode = collegeNode;
        this.inCombat = false;
        this.rotation = new Quaternion();
        rotation.fromAngleAxis(0.005f, Vector3f.UNIT_Y);
        shootChance = 0.05;
        this.hp = 100;
        this.physicsSpace = physicsSpace;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (hp <= 0){
            kill();
        }
        if(inCombat) {
            Quaternion rot = collegeNode.getLocalRotation();
            collegeNode.setLocalRotation(rot.mult(rotation));
            for(Spatial cannon : cannons){
                if(Math.random() < shootChance){
                    cannon.getControl(ShooterControl.class).shootBullet(
                                        rot.mult(cannon.getLocalTranslation().normalizeLocal()));
                }
            }
        } 
        else {
            for(PhysicsCollisionObject obj : ghost.getOverlappingObjects()){
                if (obj.getUserObject().getClass() == Node.class){
                    Node userObject = (Node)obj.getUserObject();
                    if(userObject.getName().equals("Player")){
                        inCombat = true;
                    }
                }
            }
        }
    }
    
    public void kill(){
        
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
}
