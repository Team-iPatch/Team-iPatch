/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.control.GhostControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author jake
 */
public class BadWeatherControl extends AbstractControl{

    private GhostControl ghost;
    private final String name;
    private PlayerControlState player;
    private SimpleApplication app;
    
    BadWeatherControl(String name,GhostControl ghost,SimpleApplication app){
        this.name = name;
        this.ghost = ghost;
        this.app = app;
        this.player = app.getStateManager().getState(PlayerControlState.class);
    }
    
    
    
    @Override
    protected void controlUpdate(float tpf) {
        for(PhysicsCollisionObject obj : ghost.getOverlappingObjects()){
            if(obj.getUserObject().getClass() == Node.class){
                Node userObject = (Node) obj.getUserObject();
                if(userObject.getName().equals("player")){
                    player.incrementGold(2);
                    player.reduceHP(1); // May Require changing.
                }
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
