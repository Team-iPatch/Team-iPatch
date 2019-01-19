package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import java.util.logging.Level;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    private BulletAppState bulletAppState;
    private PlayerControlState playerControlState;
    private NiftyController niftyController;
    BetterCharacterControl character;
    Node ship;
    Node sceneNode;
    EnemyGenerator enemyGenerator;
    GhostControl ghost;
    Spatial department;
    public Boolean paused;
    
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
        loadGUI();
        startGame();
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    private void startGame(){
        paused = true;
        playerControlState = new PlayerControlState();

        loadBulletPhysics();
        loadEnemyGenerator();
        loadScene();
        loadPlayer();
        loadBox();
        loadBaddie();
        
        //loadMap();
        //bulletAppState.setDebugEnabled(true);
        //BuildingGeneratorState b = new BuildingGeneratorState();
        //department = b.generateDepartment("compsci", 5f, this);     
        //b.generateCollege("Alcuin", new Vector3f(10,1,10), 5f, this);
    }
	
    private void loadGUI(){
        niftyController = new NiftyController();
        stateManager.attach(niftyController);
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        Nifty nifty = niftyDisplay.getNifty();
        guiViewPort.addProcessor(niftyDisplay);
        nifty.fromXml("Interface/Controls/mainMenu.xml", "start", niftyController);
        nifty.setIgnoreKeyboardEvents(true);
        //nifty.setDebugOptionPanelColors(true);
    }
    
    private void loadEnemyGenerator(){
        enemyGenerator = new EnemyGenerator(assetManager, 
                                            bulletAppState.getPhysicsSpace());
    }
    
    private void loadBaddie(){
        Spatial baddie = enemyGenerator.generateEnemy("Models/turret02/turret02.j3o",
                                          new Vector3f(-4, 3, 0), 1.5f, 3f, 10);
        rootNode.attachChild(baddie);        
        Spatial baddie2 = enemyGenerator.generateEnemy("Models/pirateship/mesh.j3o",
                                          new Vector3f(15, 3, 0), 1.5f, 3f, 10);
	rootNode.attachChild(baddie2);
        baddie2.addControl(new AIChaserControl(this.ship, 3, playerControlState,
                                            bulletAppState.getPhysicsSpace()));
    }

    private void loadBox(){
        Box box = new Box(1, 1, 1);
        Geometry box_geom = new Geometry("box", box);
        box_geom.setMaterial(new Material(assetManager, "Common/MatDefs/Misc/"
                                                            + "Unshaded.j3md"));
        box_geom.setLocalTranslation(10, 1, 0);
        rootNode.attachChild(box_geom);
        RigidBodyControl box_phys = new RigidBodyControl(0f);
        box_geom.addControl(box_phys);
        bulletAppState.getPhysicsSpace().add(box_geom);
    }
    
    private void loadScene(){
        sceneNode = (Node)assetManager.loadModel("Scenes/newScene.j3o");
        sceneNode.scale(10);
        rootNode.attachChild(sceneNode);
        sceneNode.setName("Scene");
        RigidBodyControl landscape = new RigidBodyControl(0);
        sceneNode.addControl(landscape);
        bulletAppState.getPhysicsSpace().add(landscape);
        
    }
    
    private void loadBulletPhysics(){
        bulletAppState = new BulletAppState();
        bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        stateManager.attach(bulletAppState);
        stateManager.attach(playerControlState);
    }
    
    private void loadPlayer(){
        ship = (Node)assetManager.loadModel("Models/pirateship/mesh.j3o");
        ship.setLocalTranslation(new Vector3f(0, 3, 0));
        character = new BetterCharacterControl(1.5f, 3f, 10f);
        ship.addControl(character);
        rootNode.attachChild(ship);
        ship.setName("player"); // Required for collision detection do not change
        bulletAppState.getPhysicsSpace().add(character);
    }
    
    private void loadMap(){
        MapGeneration.assetManager = assetManager;
        MapGeneration.rootNode = rootNode;
        MapGeneration.bulletAppState = bulletAppState;
        TerrainType[][][] testArray = MapGeneration.generateArray();
        MapGeneration.loadArrayIntoWorld(testArray);
    }

}