/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author fc831
 */
public class MiniGame extends AbstractAppState {
    
    SimpleApplication app;
    AppStateManager stateManager;
    BulletAppState bulletAppState;
    NiftyController niftyController;
    AssetManager assetManager;
    InputManager inputManager;
    AudioRenderer audioRenderer;
    Node rootNode;
    MiniGamePlayer player;
    Spatial playerShip;
    GhostControl characterControl;
    long timer;
    Boolean end;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        this.app = (SimpleApplication)app;
        this.stateManager = stateManager;
        this.assetManager = this.app.getAssetManager();
        this.inputManager = this.app.getInputManager();
        this.audioRenderer = this.app.getAudioRenderer();
        this.rootNode = this.app.getRootNode();
        this.bulletAppState = this.app.getStateManager().getState(BulletAppState.class);
        inputManager.clearMappings();
        end = false;
        timer = System.currentTimeMillis();
        stateManager.getState(PlayerControlState.class).setEnabled(false);
        
        player = new MiniGamePlayer();
        stateManager.attach(player);
        
        initMiniGame();
    }
    
    
    
    @Override
    public void update(float tpf) {
        if(System.currentTimeMillis() - timer >= 20000){
            stateManager.getState(PlayerControlState.class).incrementGold(50);
            timer = System.currentTimeMillis();
            this.destroy();
        }
        if(end){
            this.destroy();
        }
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
    }
    public void destroy() {
        inputManager.clearMappings();
        PlayerControlState mainplayer = app.getStateManager().getState(PlayerControlState.class);
        mainplayer.setEnabled(true);
        mainplayer.initKeys();
        mainplayer.initCamera();
        rootNode.detachChild(this.playerShip);
        stateManager.getState(NiftyController.class).minigamerunning= false;
        this.player.setEnabled(false);
        this.setEnabled(false);
        inputManager.setCursorVisible(true);
        stateManager.detach(player);
        stateManager.detach(this);
        
    }
    
    private void initMiniGame(){
        playerShip = assetManager.loadModel("Models/pirateship/mesh.j3o");
        playerShip.setLocalRotation(Quaternion.DIRECTION_Z);
        playerShip.setLocalTranslation(-25, 0.5f, -25);
        characterControl = new GhostControl(new BoxCollisionShape(new Vector3f(1,1,1)));
        playerShip.addControl(characterControl);
        rootNode.attachChild(playerShip);
        playerShip.setName("minigameplayer");
        bulletAppState.getPhysicsSpace().add(characterControl);
        initCamera();
    }
    private void initCamera(){
        ChaseCamera chaseCam = new ChaseCamera(app.getCamera(), playerShip, inputManager);
    	chaseCam.setSmoothMotion(false); // If true, camera automatically adjusts
        chaseCam.setDragToRotate(false); // If true, player has to click and drag the screen to move the camera
        chaseCam.setRotationSpeed(0); // Sets the speed at which the player rotates the camera. 0 means no movement.
        chaseCam.setDefaultVerticalRotation(70*FastMath.DEG_TO_RAD); // Sets the angle at which the camera looks down at the player
        // Makes the player unable to zoom the camera.
        chaseCam.setDefaultDistance(chaseCam.getMaxDistance());
        chaseCam.setMinDistance(chaseCam.getMaxDistance());
    }
    
}
