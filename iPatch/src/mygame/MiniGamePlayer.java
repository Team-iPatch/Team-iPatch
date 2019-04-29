package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * Controls the players ship in the minigame and spawns the rocks.
 * @author fc831 & MagicSid
 */
public class MiniGamePlayer extends BaseAppState {

    private SimpleApplication app;
    private Node rootNode;
    private Spatial player;
    private InputManager inputManager;
    private BetterCharacterControl controller;
    private float speed;
    private Integer pointsearnt;
    private AppSettings settings;
    private long timeleft;
    
    @Override
    protected void initialize(Application app) {
        speed = 0f;
        this.app =(SimpleApplication) app;
        this.rootNode = this.app.getRootNode();
    	this.player = this.rootNode.getChild("minigameplayer");
    	this.inputManager = this.app.getInputManager();
    	this.settings = app.getContext().getSettings();
        
        timeleft = System.currentTimeMillis();
        // Defines the player's points, gold, and HP
        initKeys();        
    }
    
    @Override
    protected void cleanup(Application app) {

    }
     

    @Override

     
    protected void onEnable() {
        //Called when the state is fully enabled, ie: is attached and  
        //isEnabled() is true or when the setEnabled() status changes after the  
        //state is attached. 
    }
    
    @Override
    
     
    protected void onDisable() {
        //Called when the state was previously enabled but is now disabled  
        //either because setEnabled(false) was called or the state is being  
        //cleaned up. 
    }

          

    @Override
    
     
    public void update(float tpf) {
        //TODO: implement behavior during runtime 
        // Handles player movement while player and character controller are
        // both initialised. Does nothing if the player is destroyed.
        if(System.currentTimeMillis()-timeleft >= 4000){
            timeleft = System.currentTimeMillis();
            Spatial Block = this.rootNode.getChild("Block");
            Random rand = new Random();
            MiniGameBlockBuilder blockspawner = new MiniGameBlockBuilder(this.app,this.app.getStateManager().getState(BulletAppState.class).getPhysicsSpace(),this);
            for(int z=-rand.nextInt(1)-1;z<=(float)rand.nextInt(1);z++){
                blockspawner.generateBlock(player.getLocalTranslation().add(z*4,1.0f,15));
            }
        }
    }

    
        
        
    
     
    private void initKeys(){
    
    	inputManager.addMapping("MLeft",   new KeyTrigger(KeyInput.KEY_A));
    	inputManager.addMapping("MRight",  new KeyTrigger(KeyInput.KEY_D));
    	inputManager.addListener(analogListener, "MLeft", "MRight");
    }
    private final AnalogListener analogListener = new AnalogListener(){
	@Override
	public void onAnalog(String name, float value, float tpf){
            // Handles player input when not in a game over state.
                
            if(name.equals("MLeft") && player.getWorldTranslation().x < -12) {
                player.move(10*tpf, 0, 0);
            }

            if(name.equals("MRight") && player.getWorldTranslation().x > -30) {
                player.move(-10*tpf, 0, 0);
            }
            
        }
    };
}
