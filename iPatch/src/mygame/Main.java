package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import java.util.logging.Level;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    GameManagementState gameManagementState;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
        app.setDisplayFps(false);
        app.setDisplayStatView(false);
    }
    
    @Override
    public void simpleInitApp() {
        //Level.SEVERE suppresses some warnings caused by libs which create
        //sphere colliders, lots of shooting floods output log
        //Set to Level.WARNING or Level.FINE for more messages
        java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);
        
        flyCam.setEnabled(false);
        
        // Main does nothing except initialise a GameManagementState.
        // All other initialisation is done there.
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