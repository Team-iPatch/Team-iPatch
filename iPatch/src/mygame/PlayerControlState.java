/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;

/**
 *  
 *  
 *
 * @author Blue 
 */
public class PlayerControlState extends BaseAppState {

    private SimpleApplication app;
    private Node rootNode;
    private Spatial player;
    private InputManager inputManager;
    private BetterCharacterControl controller;
    private float speed;
    private AppSettings settings; 
    private Integer points;
    private Integer hp;
   
    @Override 
    protected void initialize(Application app){
        speed = 0f;
        this.app = (SimpleApplication)app;
        this.rootNode = this.app.getRootNode();
    	this.player = this.rootNode.getChild("Player");
    	this.inputManager = this.app.getInputManager();
    	this.controller = player.getControl(BetterCharacterControl.class);
        this.controller.setGravity(Vector3f.UNIT_Y.mult(-20));
    	this.settings = app.getContext().getSettings();
    	this.points = 0;
        this.hp = 100;
    	ChaseCamera chaseCam = new ChaseCamera(app.getCamera(), this.player, inputManager);
    	chaseCam.setSmoothMotion(true);
        ShooterControl shooterControl = new ShooterControl(controller.getViewDirection(), true, app);
        this.player.addControl(shooterControl);
        this.settings.setFrameRate(60);
        this.app.restart();
    	System.out.print(this.app.getCamera().getWidth() + " " + this.app.getCamera().getHeight());
    	initKeys();
    }
    
    public Integer getHP(){
        return this.hp;
    }
    
    public void reduceHP(int hp){
        this.hp -= hp;
    }
    
    public Integer getPoints(){
        return this.points;
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
        Vector3f playerRotation = player.getWorldRotation().mult(Vector3f.UNIT_Z);
        controller.setWalkDirection(playerRotation.mult(speed));
        speed *= 0.99;
        
    } 
	
    private void changeResolution(){
        this.settings.setResolution(1600, 900);
    	this.app.restart();
    }
	
    private void initKeys(){
    	inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_W));
    	inputManager.addMapping("Backward",  new KeyTrigger(KeyInput.KEY_S));
    	inputManager.addMapping("RotLeft",   new KeyTrigger(KeyInput.KEY_A));
    	inputManager.addMapping("RotRight",  new KeyTrigger(KeyInput.KEY_D));
    	inputManager.addMapping("ChangeRes", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addMapping("Shoot",     new KeyTrigger(KeyInput.KEY_SPACE));
    	inputManager.addListener(analogListener, "RotLeft", "RotRight", "Forward", "Backward");
    	inputManager.addListener(actionListener, "ChangeRes", "Shoot");
    }
	
    private final ActionListener actionListener = new ActionListener(){
	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
            if(name.equals("ChangeRes")) {
                changeResolution();
            }
            if(name.equals("Shoot") && isPressed) {
                player.getControl(ShooterControl.class ).shootBullet();
            }
	}
    };
    private final AnalogListener analogListener = new AnalogListener(){
	@Override
	public void onAnalog(String name, float value, float tpf){
        if(name.equals("RotLeft")) {
            Vector3f dir = controller.getViewDirection();
            Quaternion quat = new Quaternion();
            quat.fromAngleAxis(FastMath.PI*value*0.75f, Vector3f.UNIT_Y);
            quat.multLocal(dir);
            controller.setViewDirection(dir);
        }
		
        if(name.equals("RotRight")) {
            Vector3f dir = controller.getViewDirection();
            Quaternion quat = new Quaternion();
            quat.fromAngleAxis(FastMath.PI*-(value*0.75f), Vector3f.UNIT_Y);
            quat.multLocal(dir);
            controller.setViewDirection(dir);	
        }
	
        if (name.equals("Forward"))
            if (speed < 15)
                speed += 1;
           
        if (name.equals("Backward")) 
            if(speed>1)
                speed -= 0.1;
	}
    };
}
