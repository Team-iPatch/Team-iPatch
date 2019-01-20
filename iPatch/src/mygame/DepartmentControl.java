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
 *
 * @author cpl512
 */
public class DepartmentControl extends AbstractControl{
    private GhostControl ghost;
    private final String name;
    private PlayerControlState player;
    private SimpleApplication app;
    
    DepartmentControl(String name, GhostControl ghost, SimpleApplication app){
        this.name = name;
        this.ghost = ghost;
        this.player = app.getStateManager().getState(PlayerControlState.class);
        this.app = app;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        Boolean showshop = false;
        for(PhysicsCollisionObject obj : ghost.getOverlappingObjects()){
            if (obj.getUserObject().getClass() == Node.class){
                Node userObject = (Node)obj.getUserObject();
                if(userObject.getName().equals("player")){
                    //TODO: Add function handling shop access here
                    showshop = true;
                }
            }
        }
        if(showshop){
            this.app.getStateManager().getState(NiftyController.class).showShop(true, this.name);
        }
        else{
            this.app.getStateManager().getState(NiftyController.class).showShop(false);
        }
    }
 
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
    
    public void healPlayer(){
        player.setHP(player.getMaxHP());
    }
    
    public void healthUpgrade(int amount){
        int newMaxHP = player.getMaxHP() + amount;
        player.setMaxHP(newMaxHP);
        player.setHP(newMaxHP);
    }
    
    public void addBackwardsShooter(){
        Quaternion quaternion = new Quaternion();
        quaternion.fromAngleAxis(180, Vector3f.UNIT_Y);
        player.addShooter(quaternion, app);
    }
    
    public void addRightShooter(){
        Quaternion quaternion = new Quaternion();
        quaternion.fromAngleAxis(90, Vector3f.UNIT_Y);
        player.addShooter(quaternion, app);
    }
    
    public void addLeftShooter(){
        Quaternion quaternion = new Quaternion();
        quaternion.fromAngleAxis(270, Vector3f.UNIT_Y);
        player.addShooter(quaternion, app);
    }
    
    public void increaseShotDamage(int amount){
        for(ShooterControl shooter : player.getShooters()){
            shooter.setDamage(shooter.getDamage() + amount);
        }
    }
}
