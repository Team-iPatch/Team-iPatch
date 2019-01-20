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
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import de.lessvoid.nifty.Nifty;

/**
 *
 * @author Blue
 */
public class GameManagementState extends AbstractAppState {
    
    SimpleApplication app;
    AppStateManager stateManager;
    BulletAppState bulletAppState;
    NiftyController niftyController;
    AssetManager assetManager;
    InputManager inputManager;
    AudioRenderer audioRenderer;
    ViewPort guiViewPort;
    EnemyGenerator enemyGenerator;
    Node rootNode;
    Node playerShip;
    PlayerControlState playerControlState;
    BetterCharacterControl characterControl;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication)app;
        this.stateManager = stateManager;
        this.assetManager = this.app.getAssetManager();
        this.inputManager = this.app.getInputManager();
        this.audioRenderer = this.app.getAudioRenderer();
        this.guiViewPort = this.app.getGuiViewPort();
        this.rootNode = this.app.getRootNode();
        
        playerControlState = new PlayerControlState();
        
        loadGUI();
    }
    
    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
    }
    
    public void startGame(){
        //loadPhysics() has to be executed first
        loadPhysics();
        loadEnemyGenerator();
        loadScene();
        loadPlayer();
        loadBox();
        loadBaddies();
        loadMap();
        loadBuildings();
    }
    
    private void loadGUI(){
        niftyController = new NiftyController();
        stateManager.attach(niftyController);
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        Nifty nifty = niftyDisplay.getNifty();
        guiViewPort.addProcessor(niftyDisplay);
        nifty.fromXml("Interface/Controls/mainMenu.xml", "start", niftyController);
        nifty.setIgnoreKeyboardEvents(true);
    }
    
    private void loadPhysics(){
        bulletAppState = new BulletAppState();
        bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        stateManager.attach(bulletAppState);
        stateManager.attach(playerControlState);
    }
    
    private void loadScene(){
        Node sceneNode = (Node)assetManager.loadModel("Scenes/newScene.j3o");
        sceneNode.scale(10);
        rootNode.attachChild(sceneNode);
        sceneNode.setName("scene");
        RigidBodyControl landscape = new RigidBodyControl(0);
        sceneNode.addControl(landscape);
        bulletAppState.getPhysicsSpace().add(landscape);
        
        //determines the size of the water plane, which has no collisions
        //and is purely for the effect of sailing on the sea
        float waterX = 700;
        float waterY = 700;
        Quad quad = new Quad(waterX, waterY);
        Geometry quad_geom = new Geometry("quad", quad);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = assetManager.loadTexture("Textures/water.jpg");
        tex.setWrap(Texture.WrapMode.Repeat);
        quad.scaleTextureCoordinates(new Vector2f(25, 25));
        mat.setTexture("ColorMap", tex);
        quad_geom.setMaterial(mat);
        Quaternion rot90 = new Quaternion();
        rot90.fromAngleAxis(-90*FastMath.DEG_TO_RAD, Vector3f.UNIT_X);
        quad_geom.setLocalRotation(rot90);
        quad_geom.setLocalTranslation(-waterX/2, 1.2f, waterY/2);
        rootNode.attachChild(quad_geom);
    }
    
    private void loadBox(){
        Box box = new Box(1, 1, 1);
        Geometry box_geom = new Geometry("box", box);
        box_geom.setMaterial(new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"));
        box_geom.setLocalTranslation(new Vector3f(10f, 1f, 0));
        rootNode.attachChild(box_geom);
        RigidBodyControl box_phys = new RigidBodyControl(0f);
        box_geom.addControl(box_phys);
        bulletAppState.getPhysicsSpace().add(box_geom);
    }
    
    private void loadPlayer(){
        playerShip = (Node)assetManager.loadModel("Models/pirateship/mesh.j3o");
        playerShip.setLocalTranslation(new Vector3f(0, 3, 0));
        characterControl = new BetterCharacterControl(1.5f, 3f, 10f);
        playerShip.addControl(characterControl);
        rootNode.attachChild(playerShip);
        playerShip.setName("player");
        bulletAppState.getPhysicsSpace().add(characterControl);
    }
    
    private void loadEnemyGenerator(){
        enemyGenerator = new EnemyGenerator(assetManager, bulletAppState.getPhysicsSpace());
    }
    
    private void loadBaddies(){
        Spatial baddie1 = enemyGenerator.generateEnemy("Models/turret02/turret02.j3o", new Vector3f(-15, 3, 0),1.5f, 3f, 10);
        rootNode.attachChild(baddie1);
        
        Spatial baddie2 = enemyGenerator.generateEnemy("Models/pirateship/mesh.j3o", new Vector3f(15, 3, 0), 1.5f, 3f, 10);
        rootNode.attachChild(baddie2);
        baddie2.addControl(new AIChaserControl(this.playerShip, 3, playerControlState, bulletAppState.getPhysicsSpace()));
        Spatial baddie3 = enemyGenerator.generateEnemy("Models/pirateship/mesh.j3o", new Vector3f(15, 3, 0), 1.5f, 3f, 10);
        rootNode.attachChild(baddie3);
        baddie3.addControl(new AIChaserControl(this.playerShip, 3, playerControlState, bulletAppState.getPhysicsSpace()));
        Spatial baddie4 = enemyGenerator.generateEnemy("Models/pirateship/mesh.j3o", new Vector3f(15, 3, 0), 1.5f, 3f, 10);
        rootNode.attachChild(baddie4);
        baddie4.addControl(new AIChaserControl(this.playerShip, 3, playerControlState, bulletAppState.getPhysicsSpace()));
        Spatial baddie5 = enemyGenerator.generateEnemy("Models/pirateship/mesh.j3o", new Vector3f(15, 3, 0), 1.5f, 3f, 10);
        rootNode.attachChild(baddie5);
        baddie5.addControl(new AIChaserControl(this.playerShip, 3, playerControlState, bulletAppState.getPhysicsSpace()));
        Spatial baddie6 = enemyGenerator.generateEnemy("Models/pirateship/mesh.j3o", new Vector3f(15, 3, 0), 1.5f, 3f, 10);
        rootNode.attachChild(baddie6);
        baddie6.addControl(new AIChaserControl(this.playerShip, 3, playerControlState, bulletAppState.getPhysicsSpace()));
    }
    
    private void loadMap(){
        MapGeneration.assetManager = assetManager;
        MapGeneration.rootNode = rootNode;
        MapGeneration.bulletAppState = bulletAppState;
        TerrainType[][][] testArray = MapGeneration.generateArray();
        MapGeneration.loadArrayIntoWorld(testArray);
    }
    
    private void loadBuildings(){
        BuildingGeneratorState b = new BuildingGeneratorState();
        Spatial department = b.generateDepartment("Computer Science", 5f, app);
        b.generateCollege("Alcuin", new Vector3f(10,1,10), 5f, app);
    }
    
}
