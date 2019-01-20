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
import java.util.ArrayList;

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
    private Integer maxHP;
    private Integer gold;
    private Vector3f viewDirection;
    private ArrayList<ShooterControl> shooters;
    private ArrayList<Quaternion> shooterDirections;
    private Boolean piercing;
    
    @Override 
    protected void initialize(Application app){
        speed = 0f;
        this.app = (SimpleApplication)app;
        this.rootNode = this.app.getRootNode();
    	this.player = this.rootNode.getChild("player");
    	this.inputManager = this.app.getInputManager();
    	this.controller = player.getControl(BetterCharacterControl.class);
        this.controller.setGravity(Vector3f.UNIT_Y.mult(-20));
    	this.settings = app.getContext().getSettings();
    	this.points = 0;
        this.gold = 40;
        this.maxHP = 100;
        this.hp = 100;
    	ChaseCamera chaseCam = new ChaseCamera(app.getCamera(), this.player, inputManager);
    	chaseCam.setSmoothMotion(false);
        chaseCam.setDragToRotate(true);
        chaseCam.setRotationSpeed(0);

        chaseCam.setDefaultVerticalRotation(70*FastMath.DEG_TO_RAD);
        chaseCam.setDefaultDistance(chaseCam.getMaxDistance());
        chaseCam.setMinDistance(chaseCam.getMaxDistance());
        
        this.piercing = false;
        shooters = new ArrayList<>();
        shooterDirections = new ArrayList<>();
        this.viewDirection = controller.getViewDirection();
        ShooterControl shooterControl = new ShooterControl(
                       viewDirection, false, this.app);
        this.player.addControl(shooterControl);
        shooters.add(shooterControl);
        Quaternion shooterDir = new Quaternion();
        shooterDir.fromAngleAxis(270*FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);
        shooterDirections.add(shooterDir);
        this.settings.setFrameRate(60);
        this.app.restart();
    	initKeys();
    }
    
    public Integer getMaxHP(){
        return this.maxHP;
    }
    
    public void setMaxHP(int amount){
        this.maxHP = amount;
    }
    
    public Integer getHP(){
        return this.hp;
    }
    
    public void setHP(int hp){
        this.hp = hp;
    }
    
    public void reduceHP(int hp){
        this.hp -= hp;
    }
    
    public void addHP(int hp){
        this.hp += hp;
    }
    
    public Integer getPoints(){
        return this.points;
    }
	
    public void incrementPoints(int points){
        this.points += points;
    }
    
    public Integer getGold(){
        return this.gold;
    }
    
    public void incrementGold(int gold){
        this.gold += gold;
    }
    
    public Spatial getPlayerSpatial(){
        return this.player;
    }
    
    public ArrayList<ShooterControl> getShooters(){
        return shooters;
    }
    
    public void addShooter(Quaternion direction, SimpleApplication app){
	ShooterControl shooterControl = new ShooterControl(viewDirection, false, app);
        player.addControl(shooterControl);
        shooters.add(shooterControl);
        shooterDirections.add(direction);
    }
    
    public void setPiercing(Boolean piercing){
        this.piercing = piercing;
    }
    
    public Boolean isPiercing(){
        return this.piercing;
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
        if(player == null || controller == null){
            
        } else {
            Vector3f playerRotation = player.getWorldRotation().mult(Vector3f.UNIT_Z);
            controller.setWalkDirection(playerRotation.mult(speed));
            speed *= 0.99;

            if(hp<=0){
                NiftyController niftyController = this.app.getStateManager().getState(NiftyController.class);
                niftyController.gameOver();
                player.removeFromParent();
                player = null;
                controller = null;
            }
        }

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
            if(player == null || controller == null){
          
            } else {
                if(name.equals("ChangeRes")) {
                    changeResolution();
                }
                if(name.equals("Shoot") && isPressed) {
                    for(int i = 0; i < shooters.size(); i++){
                        shooters.get(i).shootBullet(shooterDirections.get(i).mult(
                                                    controller.getViewDirection()));
                    }
                }
            }
            
	}
    };
    private final AnalogListener analogListener = new AnalogListener(){
	@Override
	public void onAnalog(String name, float value, float tpf){
            if(player == null || controller == null){
                
            } else {
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
            }

    };
}
