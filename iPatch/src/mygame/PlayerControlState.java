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
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.Random;

/**
 * App state which controls player input and the player's resources.
 *
 * @author Blue & team 1
 */
public class PlayerControlState extends BaseAppState {

    private SimpleApplication app;
    private Node rootNode;
    private Spatial player;
    private InputManager inputManager;
    private BetterCharacterControl controller;
    EnemyGenerator enemyGenerator;
    
    private float currentSpeed;
    private float maxSpeed;
    private float acceleration;
    private float deceleration;
    
    private AppSettings settings; 
    private Integer points;
    private Integer hp;
    private Integer maxHP;
    private Integer gold;
    private Vector3f viewDirection;
    private ArrayList<ShooterControl> shooters;
    private ArrayList<Quaternion> shooterDirections;
    private Boolean piercing;
    private int[][] spawnlist;
    private BulletAppState bulletAppState;
    private Boolean killQuest;
    private Boolean treasureQuest;
    private Boolean onQuest;
    private Integer Questcounter;
    private Integer Questprogress;
    
    int weathertimer = 0;
    
    @Override 
    protected void initialize(Application app){
        this.currentSpeed = 0f;
        this.maxSpeed = 10f;
        this.acceleration = 0.5f;
        this.deceleration = 0.4f;
        
        this.app = (SimpleApplication)app;
        this.rootNode = this.app.getRootNode();
    	this.player = this.rootNode.getChild("player");
    	this.inputManager = this.app.getInputManager();
    	this.controller = player.getControl(BetterCharacterControl.class);
        this.controller.setGravity(Vector3f.UNIT_Y.mult(-20));
    	this.settings = app.getContext().getSettings();
        enemyGenerator = new EnemyGenerator(this.app.getAssetManager(),
                             this.bulletAppState.getPhysicsSpace());
        
        // Defines the player's points, gold, and HP
    	this.points = 0;
        this.gold = 40;
        this.maxHP = 100;
        this.hp = 100;
        
        initCamera();
		
        // Define the quest trackers
        this.killQuest=false;
        this.treasureQuest=false;
        this.onQuest =false;
        this.Questcounter =0;
        this.Questprogress=0;
        
        // Initialises list where player's cannons are stored.
        this.piercing = false;
        shooters = new ArrayList<>();
        shooterDirections = new ArrayList<>();
        // Initialises a default cannon which shoots to the right of where
        // the player is facing
        this.viewDirection = controller.getViewDirection();
        ShooterControl shooterControl = new ShooterControl( viewDirection, false, this.app);
        this.player.addControl(shooterControl);
        shooters.add(shooterControl);
        Quaternion shooterDir = new Quaternion();
        //Here's where I need to add in the mouse input to change fire direction
        //Vector2f mouse= inputManager.getCursorPosition();
        //we only need X,Y 
        //We need to change this so that EVERY time they fire, they update the 
        //direction independant of 
        
        shooterDirections.add(shooterDir);
        // Caps the frame rate at 60 FPS and initialises the input handler
        this.settings.setFrameRate(60);
        this.app.restart();
    	initKeys();
    }
    
    /**
     * Initialises Camera.
     */
    public void initCamera(){
         
        ChaseCamera chaseCam = new ChaseCamera(app.getCamera(), player, inputManager);
    	chaseCam.setSmoothMotion(false); // If true, camera automatically adjusts
        chaseCam.setDragToRotate(false); // If true, player has to click and drag the screen to move the camera
        chaseCam.setRotationSpeed(0); // Sets the speed at which the player rotates the camera. 0 means no movement.
        chaseCam.setDefaultVerticalRotation(70*FastMath.DEG_TO_RAD); // Sets the angle at which the camera looks down at the player
        // Makes the player unable to zoom the camera.
        chaseCam.setDefaultDistance(chaseCam.getMaxDistance());
        chaseCam.setMinDistance(chaseCam.getMaxDistance());
        
        
        // New camera
//        ChaseCamera chaseCam = new ChaseCamera(app.getCamera(), this.getPlayerSpatial());
//        chaseCam.setDefaultDistance(30);
//        chaseCam.setMinDistance(10);
//        chaseCam.setMaxDistance(55);
//        chaseCam.setZoomSensitivity(2);
//        chaseCam.setSmoothMotion(true);
//        chaseCam.setChasingSensitivity(10);
    }
    
    /**
     * Returns player HP cap.
     * @return Integer, maximum HP
     */
    public Integer getMaxHP(){
        return this.maxHP;
    }
    /**
     * Returns player quest state
     * @return onQuest
     */
    public Boolean getQuestState(){
        return this.onQuest;
    }
    public void  takeKillQuest(int killcount){
        this.killQuest=true;    
        this.Questcounter=killcount;
    }
    public void takeTreasureQuest(int treasurecount){
        this.treasureQuest=true;
        this.Questcounter=treasurecount;
    }
    public Integer getQuestProgress(){
        int Progress=this.Questcounter-this.Questprogress;
        return Progress;
    }
    public Boolean isKillQuest(){
        return this.killQuest;
    }
    
    public Boolean isTreasureQuest(){
        return this.treasureQuest;
    }
    public int getProgress(){
        return this.Questprogress;
    }
    public int getCounter(){
        return this.Questcounter;
    }
    
    public void questStart(){
        this.onQuest=true;
        this.Questprogress=0;
    }
    public void Questprogress(int increment){
        this.Questprogress+=increment;
    }
    public void endQuest(){
        this.Questcounter=0;
        this.Questprogress=0;
        this.onQuest=false;          
                
    }
    public void endkillQuest(){
        this.killQuest=false;
    }
    public void endTreasureQuest(){
        this.treasureQuest=false;
    }
    /**
     * Changes player's max HP.
     * @param amount New maximum HP cap.
     */
    public void setMaxHP(int amount){
        this.maxHP = amount;
    }
    
    /**
     * Returns player's current HP.
     * @return Integer, current HP
     */
    public Integer getHP(){
        return this.hp;
    }
    
    /**
     * Changes player's current HP.
     * @param hp New HP.
     */
    public void setHP(int hp){
        this.hp = hp;
    }
    
    /**
     * Reduces player's HP by an integer.
     * @param hp Number to reduce player's health by.
     */
    public void reduceHP(int hp){
        this.hp -= hp;
    }
    
    /**
     * Increases player's HP by an integer.
     * @param hp Number to increase player's health by. 
     */
    public void addHP(int hp){
        this.hp += hp;
    }
    
    /**
     * Returns player's point count.
     * @return Integer, current score
     */
    public Integer getPoints(){
        return this.points;
    }
	
    /**
     * Increments points by a value.
     * @param points Points to give the player.
     */
    public void incrementPoints(int points){
        this.points += points;
    }
    
    /**
     * Returns player's gold count.
     * @return Integer, gold count
     */
    public Integer getGold(){
        return this.gold;
    }
    
    /**
     * Increases player's gold count by a value.
     * @param gold Gold to give the player.
     */
    public void incrementGold(int gold){
        this.gold += gold;
    }
    
    /**
     * Returns player's spatial.
     * @return Player spatial.
     */
    public Spatial getPlayerSpatial(){
        return this.player;
    }
    
    /**
     * Returns list of all cannons attached to the player.
     * @return ArrayList of ShooterControl
     */
    public ArrayList<ShooterControl> getShooters(){
        return shooters;
    }
    
    /**
     * Initialises new cannon, attaches it to player.
     * @param direction Quaternion indicating firing direction.
     * @param app SimpleApplication
     */
    public void addShooter(Quaternion direction, SimpleApplication app){
	ShooterControl shooterControl = new ShooterControl(viewDirection, false, app);
        player.addControl(shooterControl);
            
        shooters.add(shooterControl);
        shooterDirections.add(direction);
    }
    //This is STATIC, problem is we want a new direction independant of movement input
    /**
     * Sets whether the player's bullets pierce.
     * @param piercing
     */
    public void setPiercing(Boolean piercing){
        this.piercing = piercing;
    }
    
    /**
     * Returns whether player's bullets pierce.
     * @return Boolean, whether bullets pierce.
     */
    public Boolean isPiercing(){
        return this.piercing;
    }
    
    public void setSpawnList(int[][] spawnlist){
        this.spawnlist = spawnlist;
    }
    
    public void setBulletAppState(BulletAppState bulletappstate){
        this.bulletAppState = bulletappstate;
    }
    
    /**
     * Rotate the ship by a given angle
     * @param angle Clockwise rotation in radians
     */
    public void rotateBy(float angle){
        Vector3f dir = controller.getViewDirection();
        Quaternion quat = new Quaternion();
        quat.fromAngleAxis(FastMath.PI*(angle), Vector3f.UNIT_Y);
        quat.multLocal(dir);
        controller.setViewDirection(dir);
    }
    
    /**
     * Move the ship to a given position
     * @param position 
     */
    public void setPosition(Vector3f position){
        rootNode.move(0, 0, 0);
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
        // Handles player movement while player and character controller are
        // both initialised. Does nothing if the player is destroyed.
       
        if(player == null || controller == null){
            
        } else {
            Vector3f playerRotation = player.getWorldRotation().mult(Vector3f.UNIT_Z);
            controller.setWalkDirection(playerRotation.mult(currentSpeed));
            if (currentSpeed > 0){
                currentSpeed -= deceleration;
                if (currentSpeed < 0){
                    currentSpeed = 0;
                }
            }
            if(this.weathertimer >= 750){
                this.incrementGold(5);
                this.incrementPoints(5);
                this.reduceHP(2);
                this.weathertimer = 0;
            }
            
            
            // Detaches player spatial from the rootNode, nulls out player and
            // controller objects.
            NiftyController niftyController = this.app.getStateManager().getState(NiftyController.class);
            if(hp<=0 && niftyController.winner != true){
                niftyController.gameOver();
                player.removeFromParent();
                player = null;
                controller = null;
            }else{
                Spatial enemyship = this.rootNode.getChild("baddie");
                Random rand = new Random();
                if(enemyship == null){
                    for(int i=0;i<rand.nextInt(5)+1; i++){
                        
                        int xangle = rand.nextInt(11)+10;
                        int zangle = rand.nextInt(11)+10;
                        if(rand.nextBoolean()){
                            xangle *= -1;
                        }
                        if(rand.nextBoolean()){
                            zangle *= -1;
                        }
                        int playerx = Math.round(this.player.getLocalTranslation().x);
                        int playerz = Math.round(this.player.getLocalTranslation().z);
                        Vector3f spawnvector = player.getLocalTranslation().add(xangle+0.5f,1,zangle+0.5f);

                        if(playerx+xangle <spawnlist.length && playerz+zangle < spawnlist[0].length 
                            &&playerz+zangle> 0 && playerx+xangle>0){
                            if(this.spawnlist[playerx+xangle][playerz+zangle] == 0){
                                Spatial baddie = enemyGenerator.generateEnemy("Models/pirateship/mesh.j3o",spawnvector,1.5f, 3f,10);
                                rootNode.attachChild(baddie);
                                baddie.addControl(new AIChaserControl(this.player, 3, this, this.bulletAppState.getPhysicsSpace(),niftyController));
                            }
                        }
                    }
                }else{
                    if(enemyship.getLocalTranslation().distance(this.player.getLocalTranslation())>30){
                        enemyship.getControl(EnemyControl.class).setHP(0);
                    }
                }
            }
        }
    }
    
    public Vector3f getAimdir(){
        Vector2f cursorPos = inputManager.getCursorPosition();
        int x = this.settings.getWidth()/2;
        int y = this.settings.getHeight()/2;
        Vector2f centrePos = new Vector2f (x,y);
        cursorPos=cursorPos.subtract(centrePos);
        Vector3f mouseaim = new Vector3f(-cursorPos.y,0,-cursorPos.x);
        mouseaim.normalizeLocal();
        return mouseaim;
    }
	
    /**
     * Initialises player inputs.
     */
    public void initKeys(){
        inputManager.setCursorVisible(true);
    	inputManager.addMapping("Forward",   new KeyTrigger(KeyInput.KEY_W));
    	inputManager.addMapping("RotLeft",   new KeyTrigger(KeyInput.KEY_A));
    	inputManager.addMapping("RotRight",  new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("ToggleObjective", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Shoot",     new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
    	inputManager.addListener(analogListener, "RotLeft", "RotRight", "Forward");
    	inputManager.addListener(actionListener, "Shoot","ToggleObjective");
    }
	
    private final ActionListener actionListener = new ActionListener(){
	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
            // Handles player input when not in a game over state.
            if(player == null || controller == null){
          
            } else {
                if(name.equals("Shoot") && isPressed && !app.getStateManager().getState(NiftyController.class).inmenu) {
                    // Shoots every cannon attached to the player
                    for(int i = 0; i < shooters.size(); i++){
                        Vector3f mouseAim = getAimdir();
//                        System.out.println(mouseAim);
                        shooters.get(i).shootBullet(mouseAim);
                        //this particular line. if I'm correct, shoots a bullet according to geomtry orientation
                    }
                }
                if(name.equals("ToggleObjective")){
                    NiftyController nifty = app.getStateManager().getState(NiftyController.class);
                    nifty.showObjective(!nifty.objective);
                }
            }
            
	}
    };
    
    private final AnalogListener analogListener = new AnalogListener(){
	@Override
	public void onAnalog(String name, float value, float tpf){
            // Handles player input when not in a game over state.
            if(player == null || controller == null){
                
            } else {
                if(name.equals("RotLeft")) {
                    rotateBy(value*0.75f);
                }

                if(name.equals("RotRight")) {
                    rotateBy(-value*0.75f);
                }

                if (name.equals("Forward"))
                    if (currentSpeed < maxSpeed){
                        currentSpeed += acceleration;
                        if (currentSpeed > maxSpeed) {
                            currentSpeed = maxSpeed;
                        }
                    }
                }
            }

    };
}

   