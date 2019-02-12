/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * 
 *
 * @author fc831
 */
public class MiniGamePlayer extends BaseAppState {

    private SimpleApplication app;
    private Node rootNode;
    private Spatial player;
    private InputManager inputManager;
    private BetterCharacterControl controller;
    private float speed;
    private AppSettings settings; 
    private Integer points;
    private Vector3f viewDirection;
    private int[][] spawnlist;
    private BulletAppState bulletappstate;
    
    @Override
    
     
    protected void initialize(Application app) {
        speed = 0f;
        this.app = (SimpleApplication)app;
        this.rootNode = this.app.getRootNode();
    	this.player = this.rootNode.getChild("player");
    	this.inputManager = this.app.getInputManager();
    	this.controller = player.getControl(BetterCharacterControl.class);
        this.controller.setGravity(Vector3f.UNIT_Y.mult(-20));
    	this.settings = app.getContext().getSettings();
        
        // Defines the player's points, gold, and HP
    	this.points = 0;
        
      
        initCamera();
        
    }
        //It is technically safe to do all initialization and cleanup in the  
        //onEnable()/onDisable() methods. Choosing to use initialize() and  
        //cleanup() for this is a matter of performance specifics for the  
        //implementor. 
        //TODO: initialize your AppState, e.g. attach spatials to rootNode 
    
    
    
    public void initCamera(){
        // Old camera
    	/*ChaseCamera chaseCam = new ChaseCamera(app.getCamera(), this.player, inputManager);
    	chaseCam.setSmoothMotion(false); // If true, camera automatically adjusts
        chaseCam.setDragToRotate(true); // If true, player has to click and drag the screen to move the camera
        chaseCam.setRotationSpeed(0); // Sets the speed at which the player rotates the camera. 0 means no movement.
        chaseCam.setDefaultVerticalRotation(70*FastMath.DEG_TO_RAD); // Sets the angle at which the camera looks down at the player
        // Makes the player unable to zoom the camera.
        chaseCam.setDefaultDistance(chaseCam.getMaxDistance());
        chaseCam.setMinDistance(chaseCam.getMaxDistance());*/
        
        // New camera
        ChaseCamera chaseCam = new ChaseCamera(app.getCamera(), this.getPlayerSpatial());
        chaseCam.setDefaultDistance(50);
        chaseCam.setMinDistance(10);
        chaseCam.setMaxDistance(75);
        chaseCam.setZoomSensitivity(2);
        chaseCam.setSmoothMotion(true);
        chaseCam.setChasingSensitivity(10);
    }
    
    

    public void incrementPoints(int points){
        this.points += points;
    }
    
    public Spatial getPlayerSpatial(){
        return this.player;
    }
    @Override
    protected void cleanup(Application app) {
        //TODO: clean up what you initialized in the initialize method, 
        //e.g. remove all spatials from rootNode 
    }
     
    //onEnable()/onDisable() can be used for managing things that should  
    //only exist while the state is enabled. Prime examples would be scene  
    //graph attachment or input listener attachment. 
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
        if(player == null || controller == null){
            
        } else {
            
            // Detaches player spatial from the rootNode, nulls out player and
            // controller objects.
            NiftyController niftyController = this.app.getStateManager().getState(NiftyController.class);
            if(10<=1){
                niftyController.gameOver();
                player.removeFromParent();
                player = null;
                controller = null;
                
            }else{
                Spatial Block = this.rootNode.getChild("Block");
                Random rand = new Random();
                MiniGameBlockBuilder blockspawner = new MiniGameBlockBuilder(this.app.getAssetManager(),this.app.getStateManager().getState(BulletAppState.class).getPhysicsSpace(),this);
                    for(int i=0;i<rand.nextInt(5)+1; i++){
                      
                        //Use random values to generate spawn vector
                        Vector3f tempvector = new Vector3f(0,0,0); //REPLACE ME
                        Spatial BlockSpacial = blockspawner.generateBlock("BLOCKMODLE___Models/pirateship/mesh.j3o",tempvector);
                        rootNode.attachChild(BlockSpacial);
                        
                            }
                    
                        }
                
            }
        }

    
        
        
    
     
    private void initKeys(){
    
    	inputManager.addMapping("Left",   new KeyTrigger(KeyInput.KEY_A));
    	inputManager.addMapping("Right",  new KeyTrigger(KeyInput.KEY_D));
    	inputManager.addListener(analogListener, "Left", "Right");
    }
    private final AnalogListener analogListener = new AnalogListener(){
	@Override
	public void onAnalog(String name, float value, float tpf){
            // Handles player input when not in a game over state.
            if(player == null || controller == null){
                
            } else {
                if(name.equals("Left")) {
                    if (speed < 15)
                        speed += 1;
                }

                if(name.equals("Right")) {
                    if (speed < 15)
                        speed += 1;	
                }

              
            }
        }
    };
            
        
}
