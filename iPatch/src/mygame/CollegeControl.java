package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
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
    private final String name;
    private Spatial cannons[];
    private Node collegeNode;
    private boolean inCombat;
    private Quaternion rotation;
    private float rotSpeed;
    private float shootGap;
    private int hp;
    private boolean captured;
    SimpleApplication app;
    PhysicsSpace physicsSpace;
    NiftyController nifty;
    private long timeCount = 0;
    
    
    /**
     * Control used to manage Colleges, tracking their health and controlling
     * where they fire.
     * @param name Name indicates the functionality of the college. Names have to
     * be hardcoded in the body of the code.
     * @param cannons Array of spatials of cannons. Indicates shooting direction.
     * @param collegeNode Node used to store the College entity.
     * @param physicsSpace PhysicsSpace where the College will register collisions.
     */
    CollegeControl(String name, Spatial[] cannons, 
                   Node collegeNode, Application app){
        this.name = name;
        this.cannons = cannons;
        this.collegeNode = collegeNode;
        this.inCombat = false;
        this.rotation = new Quaternion();
        captured = false;
        
        switch(name){
            case "Derwent":
                this.hp = 200;
                shootGap = 1000;
                rotSpeed = 0.005f;
                break;
            case "Vanbrugh":
                this.hp = 250;
                shootGap = 750;
                rotSpeed = 0.01f;
                break;
            case "Alcuin":
                this.hp = 160;
                shootGap = 450;
                rotSpeed = 0.02f;
            case "Constantine":
                this.hp = 120;
                shootGap = 500;
                rotSpeed = 0.015f;
            case "Goodricke":
                this.hp = 140;
                shootGap = 625;
                rotSpeed = 0.01f;
        }
        this.app = (SimpleApplication)app;
        this.physicsSpace = app.getStateManager().
                getState(BulletAppState.class).getPhysicsSpace();
        this.nifty = app.getStateManager().getState(NiftyController.class);
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (hp <= 0 && !captured){
            capture();
        }
        
        if (timeCount == 0){
            timeCount = System.currentTimeMillis();
        }
        Boolean hasfired = false;
        
        if(!captured && inCombat && !nifty.inMenu) {
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
    }
    
    public void reduceHP(int hp){
        this.hp -= hp;
        // Added for assessment 4: colleges get stronger with level
        if(!inCombat){
            int level = app.getStateManager().
                    getState(PlayerControlState.class).getLevel(); 
            this.hp *= level;
            System.out.println(this.hp);
            rotation.fromAngleAxis(rotSpeed * (float)level, Vector3f.UNIT_Y);
            shootGap /= (float)level;
            for (Spatial cannon : cannons) {
                cannon.getControl(ShooterControl.class).
                        setBulletSpeed(30f - 3f*level);
            }
            inCombat = true;
        }
    }
    
    public int getHP(){
        return this.hp;
    }
    
    public void capture(){
        captured = true;
        app.getStateManager().getState(PlayerControlState.class).levelUp();
        Geometry sphere = (Geometry) collegeNode.getChild("sphere");
        sphere.getMaterial().setColor("Color", ColorRGBA.Green);
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
}
