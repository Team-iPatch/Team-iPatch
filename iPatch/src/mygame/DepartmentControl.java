/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.GhostControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * Controls interactions with Departments, which act as shops.
 * @author Team iPatch
 */
public class DepartmentControl extends AbstractControl{
    private GhostControl ghost;
    private final String name;
    private PlayerControlState player;
    private SimpleApplication app;
    
    /**
     * Controls interactions with Departments, which act as shops.
     * @param name Name which indicates functionality, have to be hardcoded.
     * Currently only "Computer Science" and "Biology" are implemented.
     * @param ghost GhostControl with the collider used to find when the player
     * is close enough to the Department to show a shop interface.
     * @param app SimpleApplication of the main program.
     */
    DepartmentControl(String name, GhostControl ghost, SimpleApplication app){
        this.name = name;
        this.ghost = ghost;
        this.player = app.getStateManager().getState(PlayerControlState.class);
        this.app = app;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        // Finds whether to display the shop, checking every frame
        // Passes the name of the Department if colliding.
        Boolean showshop = false;
        for(PhysicsCollisionObject obj : ghost.getOverlappingObjects()){
            if (obj.getUserObject().getClass() == Node.class){
                Node userObject = (Node)obj.getUserObject();
                if(userObject.getName().equals("player")){
                    showshop = true;
                }
            }
        }
        if(showshop){
            this.app.getStateManager().getState(NiftyController.class).showShop(true, this.name);
        }
    }
 
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
}
