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
import java.util.Random;

/**
 * Manager app state used to initialise and store the majority of program code.
 * Effectively a substitute for Main() which can be accessed at runtime.
 * @author Team iPatch
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
    int[][] spawnmap;
    Vector3f[] spawnlist;
    
    /**
     * Manager app state used to initialise and store the majority of program
     * code. Effectively a substitue for Main() which can be accessed at runtime.
     * @param stateManager Application's AppStateManager.
     * @param app Application object.
     */
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
        // Loads GUI first for a rudimentary start menu
        loadGUI();
    }
    
    @Override
    public void update(float tpf) {
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
    }
    
    /**
     * Used to initialise game entities, through private submethods which
     * have to be edited internally.
     */
    public void startGame(){
        //loadPhysics() has to be executed first
        loadPhysics();
        loadEnemyGenerator();
        loadScene();
        //loadBox(); // used to test physics, allows the ship to "surf" into the air
        loadMap();
        loadBuildings();
        loadPlayer();
        //loadBaddies(); //used to test baddies, depreciated due to enemy spawning now implemented
    }
    
    /**
     * Initialises GUI code.
     */
    private void loadGUI(){
        niftyController = new NiftyController();
        stateManager.attach(niftyController);
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        Nifty nifty = niftyDisplay.getNifty();
        guiViewPort.addProcessor(niftyDisplay);
        nifty.fromXml("Interface/Controls/mainMenu.xml", "start", niftyController);
        nifty.setIgnoreKeyboardEvents(true);
    }
    
    /**
     * Initialises physics code. 
     */
    private void loadPhysics(){
        bulletAppState = new BulletAppState();
        bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        stateManager.attach(bulletAppState);
        stateManager.attach(playerControlState);
        this.playerControlState.setbulletappstate(bulletAppState);
    }
    
    /**
     * Loads scene, changes made to the play area have to be done here.
     */
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
    
    /**
     * Loads a box.
     */
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
    
    /**
     * Initialises player.
     */
    private void loadPlayer(){
        playerShip = (Node)assetManager.loadModel("Models/pirateship/mesh.j3o");
        playerShip.setLocalTranslation(spawnlist[0]);
        characterControl = new BetterCharacterControl(0.2f, 3f, 10f);
        playerShip.addControl(characterControl);
        rootNode.attachChild(playerShip);
        playerShip.setName("player");
        bulletAppState.getPhysicsSpace().add(characterControl);
    }
    
    /**
     * Initialises enemy generator.
     */
    private void loadEnemyGenerator(){
        enemyGenerator = new EnemyGenerator(assetManager, bulletAppState.getPhysicsSpace());
    }
    
    /**
     * Initialises enemies, edit enemy instantiation here.
     */
    private void loadBaddies(){
        for(int i = 0; i < 5; i++){
            Spatial baddie = enemyGenerator.generateEnemy("Models/pirateship/mesh.j3o", new Vector3f(15, 3, 0), 1.5f, 3f, 10);
            rootNode.attachChild(baddie);
            baddie.addControl(new AIChaserControl(this.playerShip, 3, playerControlState, bulletAppState.getPhysicsSpace(),niftyController));
        }
    }
    
    /**
     * Initialises and generates voxel islands.
     */
    private void loadMap(){
        MapGeneration.assetManager = assetManager;
        MapGeneration.rootNode = rootNode;
        MapGeneration.bulletAppState = bulletAppState;
        TerrainType[][][] testArray = MapGeneration.generateArray();
        spawnmap = MapGeneration.loadArrayIntoWorld(testArray);
    }
    
    /**
     * Initialises departments and colleges.
     */
    private void loadBuildings(){
        int buildingcount = 5; // EDIT THIS WHEN YOU ADD MORE BUILDINGS. IF YOU DON'T YOU WILL CRASH.
        int rowcount = spawnmap.length;
        int colcount = spawnmap[0].length;
        spawnlist = new Vector3f[buildingcount];
        Random rand = new Random();

        while(buildingcount >= 1){
            int genx = rand.nextInt(rowcount-2);
            int genz = rand.nextInt(colcount-2);
            if(genx <2){
                genx = 2;
            }
            if(genz <2){
                genz = 2;
            }
            Vector3f tempvector = new Vector3f(genx+0.5f,1,genz+0.5f);
            boolean safedistance = true;
            
            if(spawnmap[genx][genz] == 1){
                safedistance = false;
            }
            
            for (int i = 0; i<spawnlist.length;i++) {
                if(spawnlist[i] != null){
                    
                    if(spawnlist[i].distance(tempvector)< 20){ // edit number here to change how close stuff can be
                        safedistance = false;
                    }
                }
            }
            if(spawnmap[genx+2][genz] == 1 && spawnmap[genx-2][genz] == 1 &&
                spawnmap[genx][genz+2] == 1 && spawnmap[genx][genz-2] == 1){
                safedistance = false;
            }
            
            if(safedistance == true){
                System.out.println(tempvector);
                spawnlist[buildingcount-1] = tempvector;
                buildingcount -= 1;
            }
            
        }
        
        playerControlState.setspawnlist(spawnmap);
        
        BuildingGenerator buildingGenerator = new BuildingGenerator(app);
        buildingGenerator.generateDepartment("Computer Science", spawnlist[0], 5f);
        buildingGenerator.generateDepartment("Biology", spawnlist[1], 5f);
        buildingGenerator.generateCollege("Alcuin", spawnlist[2], 15f);
        buildingGenerator.generateCollege("Vanbrugh", spawnlist[3],15f);
        buildingGenerator.generateCollege("Derwent", spawnlist[4], 15f);
    }
}
