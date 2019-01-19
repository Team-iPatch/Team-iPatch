/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.control.GhostControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.scene.Node;

/**
 *
 * @author cpl512
 */
public class DepartmentControl extends AbstractControl{
    private GhostControl ghost;
    private final String name;
    private PlayerControlState player;
    
    DepartmentControl(String name, GhostControl ghost, PlayerControlState player){
        this.name = name;
        this.ghost = ghost;
        this.player = player;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        for(PhysicsCollisionObject obj : ghost.getOverlappingObjects()){
            if (obj.getUserObject().getClass() == Node.class){
                Node userObject = (Node)obj.getUserObject();
                if(userObject.getName().equals("Player")){
                    //TODO: Add function handling shop access here
                    System.out.println("Player in GhostControl");
                }
            }
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
}
