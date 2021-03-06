package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.control.GhostControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

/**
 * Implements the weather physics and increments a player variable to give them money and deal damage.
 * @author MagicSid
 */
public class BadWeatherControl extends AbstractControl{

    private GhostControl ghost;
    private final String name;
    private PlayerControlState player;
    private SimpleApplication app;
    
    BadWeatherControl(String name,GhostControl ghost,SimpleApplication app){
        this.name = name;
        this.ghost = ghost;
        this.player = app.getStateManager().getState(PlayerControlState.class);
        this.app = app;
    }
    
    
    
    @Override
    protected void controlUpdate(float tpf) {
        for(PhysicsCollisionObject obj : ghost.getOverlappingObjects()){
            if(obj.getUserObject().getClass() == Node.class){
                Node userObject = (Node) obj.getUserObject();
                if(userObject.getName().equals("player")){
                    player.weathertimer += 1; // May Require changing.
                }
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
    
    
}
