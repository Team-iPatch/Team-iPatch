/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.control.GhostControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Control used to manage Colleges, tracking their health and controlling
 * where they fire.
 * 
 * @author Team iPatch
 */
public class CollegeControl extends AbstractControl {
    private GhostControl ghost;
    private int ghostCounter;
    private final String name;
    private Spatial cannons[];
    private Node collegeNode;
    private boolean inCombat;
    private Quaternion rotation;
    private double shootChance;
    private int hp;
    private boolean captured;
    PhysicsSpace physicsSpace;
    NiftyController nifty;
    
    /**
     * Control used to manage Colleges, tracking their health and controlling
     * where they fire.
     * @param name Name indicates the functionality of the college. Names have to
     * be hardcoded in the body of the code.
     * @param ghost GhostControl used to detect proximity collisions with the player.
     * Currently unused internally.
     * @param cannons Array of spatials of cannons. Indicates shooting direction.
     * @param collegeNode Node used to store the College entity.
     * @param physicsSpace PhysicsSpace where the College will register collisions.
     */
    CollegeControl(String name, GhostControl ghost, Spatial[] cannons, 
                   Node collegeNode, PhysicsSpace physicsSpace, NiftyController nifty){
        this.name = name;
        this.ghost = ghost;
        this.ghostCounter = 0;
        this.cannons = cannons;
        this.collegeNode = collegeNode;
        this.inCombat = false;
        this.rotation = new Quaternion();
        captured = false;
        switch(name){
            case "Derwent":
                this.hp = 100;
                shootChance = 0.05;
                rotation.fromAngleAxis(0.005f, Vector3f.UNIT_Y);
                break;
            case "Vanbrugh":
                this.hp = 200;
                shootChance = 0.1;
                break;
            case "Alcuin":
                this.hp = 70;
                shootChance = 0.05;
                rotation.fromAngleAxis(0.01f, Vector3f.UNIT_Y);                
        }
        this.physicsSpace = physicsSpace;
        this.nifty = nifty;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (hp <= 0){
            capture();
        }
        if(this.nifty.shop){
            inCombat = false;
        }
        if(!captured && inCombat) {
            Quaternion rot = collegeNode.getLocalRotation();
            switch(name) {
                case "Derwent":
                    collegeNode.setLocalRotation(rot.mult(rotation));
                    for(Spatial cannon : cannons){
                        if(Math.random() < shootChance){
                            cannon.getControl(ShooterControl.class).shootBullet(
                                        rot.mult(cannon.getLocalTranslation().
                                                            normalizeLocal()));
                        }
                    } break;
                case "Vanbrugh":
                    for(Spatial cannon : cannons){
                        if(Math.random() < shootChance){
                            cannon.getControl(ShooterControl.class).shootBullet(
                                        rot.mult(cannon.getLocalTranslation().
                                                            normalizeLocal()));
                        }
                    } break;
                case "Alcuin":
                    if(Math.random() < 0.001){
                        rotation = rotation.inverse();
                    }
                    collegeNode.setLocalRotation(rot.mult(rotation));
                    for(Spatial cannon : cannons){
                        if(Math.random() < shootChance){
                            cannon.getControl(ShooterControl.class).shootBullet(
                                        rot.mult(cannon.getLocalTranslation().
                                                            normalizeLocal()));
                        }
                    } break;
                default:
                    throw new NotImplementedException();
            }
        } 
        else if(!captured) {
            for(PhysicsCollisionObject obj : ghost.getOverlappingObjects()){
                if (obj.getUserObject().getClass() == Node.class){
                    Node userObject = (Node)obj.getUserObject();
                    if(userObject.getName().equals("player")){
                        if (ghostCounter > 1){
                           inCombat = true;
                        }
                        else{
                            ghostCounter++;
                        }
                    }
                }
            }
        }
    }
    
    public void reduceHP(int hp){
        this.hp -= hp;
    }
    
    public int getHP(){
        return this.hp;
    }
    
    public void capture(){
        captured = true;
        Geometry box = (Geometry) collegeNode.getChild("box");
        box.getMaterial().setColor("Color", ColorRGBA.Green);
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
}
