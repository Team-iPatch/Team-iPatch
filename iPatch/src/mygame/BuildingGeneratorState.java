/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;

/**
 *
 * @author cpl512
 */
public class BuildingGeneratorState extends AbstractAppState {
    ArrayList<Spatial> buildings;
    Application app;
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = app;
        //TODO: initialize your AppState, e.g. attach spatials to rootNode
        //this is called on the OpenGL thread after the AppState has been attached
    }
    
    public void generateDepartment(String name){
       //TODO: replace box with department model
       Box model = new Box(2,2,2);
       Geometry department = new Geometry(name, model);
       //this will be given by the environment map
       department.setLocalTranslation(new Vector3f(2, 2, 2));
       Material mat1 = new Material(app.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
       mat1.setColor("Color", ColorRGBA.Blue);
       department.setMaterial(mat1);
       department.addControl(new DepartmentControl(name, 10,app.
               getStateManager().getState(BulletAppState.class).getPhysicsSpace()));
    }
    
    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }
    
}
