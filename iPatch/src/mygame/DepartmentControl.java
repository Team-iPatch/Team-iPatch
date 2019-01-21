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
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
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
    /**
     * Sets player HP to their maximum.
     */
    public void healPlayer(){
        player.setHP(player.getMaxHP());
    }
    
    /**
     * Increses player's max HP by an integer
     * @param amount HP to add to the player's max
     */
    public void healthUpgrade(int amount){
        int newMaxHP = player.getMaxHP() + amount;
        player.setMaxHP(newMaxHP);
        player.setHP(newMaxHP);
    }
    
    /**
     * Gives the player an extra cannon facing backwards.
     */
    public void addBackwardsShooter(){
        Quaternion quaternion = new Quaternion();
        quaternion.fromAngleAxis(180, Vector3f.UNIT_Y);
        player.addShooter(quaternion, app);
    }
    
    /**
     * Gives the player an extra cannon facing to their right.
     */
    public void addRightShooter(){
        Quaternion quaternion = new Quaternion();
        quaternion.fromAngleAxis(90, Vector3f.UNIT_Y);
        player.addShooter(quaternion, app);
    }
    
    /**
     * Gives the player an extra cannon facing to their left.
     */
    public void addLeftShooter(){
        Quaternion quaternion = new Quaternion();
        quaternion.fromAngleAxis(270, Vector3f.UNIT_Y);
        player.addShooter(quaternion, app);
    }
    
    /**
     * Increases the amount of damage dealt by each shot the player fires.
     * @param amount Extra damage dealt by player.
     */
    public void increaseShotDamage(int amount){
        for(ShooterControl shooter : player.getShooters()){
            shooter.setDamage(shooter.getDamage() + amount);
        }
    }
}
