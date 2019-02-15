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
    private float shootGap;
    private int hp;
    private boolean captured;
    PhysicsSpace physicsSpace;
    NiftyController nifty;
    private long timeCount = 0;
    
    
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
                this.hp = 200;
                shootGap = 1000;
                rotation.fromAngleAxis(0.005f, Vector3f.UNIT_Y);
                break;
            case "Vanbrugh":
                this.hp = 250;
                shootGap = 750;
                break;
            case "Alcuin":
                this.hp = 160;
                shootGap = 450;
                rotation.fromAngleAxis(0.02f, Vector3f.UNIT_Y);
            case "Constantine":
                this.hp = 120;
                shootGap = 500;
                rotation.fromAngleAxis(0.015f, Vector3f.UNIT_Y);
            case "Goodricke":
                this.hp = 140;
                shootGap = 625;
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
        
        if (timeCount == 0){
            timeCount = System.currentTimeMillis();
        }
        Boolean hasfired = false;
        
        if(!captured && inCombat) {
            Quaternion rot = collegeNode.getLocalRotation();
            switch(name) {
                case "Derwent":
                    collegeNode.setLocalRotation(rot.mult(rotation));
                    for(Spatial cannon : cannons){
                        if(System.currentTimeMillis() - timeCount > shootGap){
                            cannon.getControl(ShooterControl.class).shootBullet(
                                        rot.mult(cannon.getLocalTranslation().
                                                            normalizeLocal()));
                            hasfired = true;
                        }
                    }
                    if (hasfired){
                        timeCount = System.currentTimeMillis();
                    }
                     break;
                    
                case "Vanbrugh":
                    for(Spatial cannon : cannons){
                        if(System.currentTimeMillis() - timeCount > shootGap){
                            cannon.getControl(ShooterControl.class).shootBullet(
                                        rot.mult(cannon.getLocalTranslation().
                                                            normalizeLocal()));
                            hasfired = true;
                        }
                    }
                    if (hasfired){
                            timeCount = System.currentTimeMillis();
                    } break;
                    
                case "Alcuin":
                    if(Math.random() < 0.001){
                        rotation = rotation.inverse();
                    }
                    collegeNode.setLocalRotation(rot.mult(rotation));
                    for(Spatial cannon : cannons){
                        if(System.currentTimeMillis() - timeCount > shootGap){
                            cannon.getControl(ShooterControl.class).shootBullet(
                                        rot.mult(cannon.getLocalTranslation().
                                                            normalizeLocal()));
                            hasfired = true;
                        }
                    }
                    if (hasfired){
                            timeCount = System.currentTimeMillis();
                    } break;
                    
                case "Constantine":
                    if(Math.random() < 0.001){
                        rotation = rotation.inverse();
                    }
                    collegeNode.setLocalRotation(rot.mult(rotation));
                    for(Spatial cannon : cannons){
                        if(System.currentTimeMillis() - timeCount > shootGap){
                            cannon.getControl(ShooterControl.class).shootBullet(
                                        rot.mult(cannon.getLocalTranslation().
                                                            normalizeLocal()));
                            hasfired = true;
                        }
                    }
                    if (hasfired){
                        timeCount = System.currentTimeMillis();
                    }
                    break;
                    
                case "Goodricke":
                    if(Math.random() < 0.001){
                        rotation = rotation.inverse();
                    }
                    collegeNode.setLocalRotation(rot.mult(rotation));
                    for(Spatial cannon : cannons){
                        if(System.currentTimeMillis() - timeCount > shootGap){
                            cannon.getControl(ShooterControl.class).shootBullet(
                                        rot.mult(cannon.getLocalTranslation().
                                                            normalizeLocal()));
                            hasfired = true;
                        }
                    }
                    if (hasfired){
                        timeCount = System.currentTimeMillis();
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
