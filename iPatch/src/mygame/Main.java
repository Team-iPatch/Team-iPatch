package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.logging.Level;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    BetterCharacterControl character;
    Node ship;
    Node sceneNode;
    EnemyGenerator enemyGenerator;
    GhostControl ghost;
    Spatial department;
    GameManagementState gameManagementState;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
        
    }
    
    @Override
    public void simpleInitApp() {
        //Level.SEVERE suppresses some warnings caused by libs which create
        //sphere colliders, lots of shooting floods output log
        //Set to Level.WARNING or Level.FINE for more messages
        java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);
        
        flyCam.setEnabled(false);
        
        gameManagementState = new GameManagementState();
        stateManager.attach(gameManagementState);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}