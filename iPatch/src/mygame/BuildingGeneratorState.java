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
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;

/**
 *
 * @author cpl512
 */
public class BuildingGeneratorState extends AbstractAppState {
    ArrayList<Spatial> buildings;
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        //TODO: initialize your AppState, e.g. attach spatials to rootNode
        //this is called on the OpenGL thread after the AppState has been attached
    }
    
    public void generateDepartment(String name, float ghostRadius, SimpleApplication app){
        GhostControl ghost = new GhostControl(new SphereCollisionShape(ghostRadius));
        Box model = new Box(2,2,2);
        Geometry department = new Geometry(name, model);
        //this will be given by the environment map
        department.setLocalTranslation(new Vector3f(2, 2, 2));
        Material mat1 = new Material(app.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Blue);
        department.setMaterial(mat1);
        app.getStateManager().getState(BulletAppState.class).getPhysicsSpace().add(ghost);
        department.addControl(ghost);
        department.addControl(new DepartmentControl(name, ghost));
        app.getRootNode().attachChild(department);
    }
    
    public void generateCollege(String name, Vector3f location, 
                                float ghostRadius, SimpleApplication app){
        Node collegeNode = new Node();
        collegeNode.setLocalTranslation(location);
        Spatial college = app.getAssetManager().loadModel("Models/"
                                                     + "turret02/turret02.j3o");
        GhostControl ghost = new GhostControl(new SphereCollisionShape(ghostRadius));
        college.addControl(ghost);
        app.getStateManager().getState(BulletAppState.class).getPhysicsSpace().
                                                                     add(ghost);
        Spatial[] cannons = new Spatial[8];
        for (int i=0;i<cannons.length;i++) {
            cannons[i] = app.getAssetManager().loadModel("Models/"
                                                     + "turret02/turret02.j3o");
            double rotAngle = (double)i * Math.PI / 4f;
            Vector3f direction = new Vector3f((float)Math.sin(rotAngle), 0, 
                                              (float)Math.cos(rotAngle));
            cannons[i].setLocalTranslation(direction.mult(4));
            cannons[i].rotate(0, i * (float)Math.PI/4f, 0);
            cannons[i].addControl(new ShooterControl(direction, true, app));
            collegeNode.attachChild(cannons[i]);
        }
        college.addControl(new CollegeControl(name, ghost, cannons, collegeNode));
        collegeNode.attachChild(college);
        app.getRootNode().attachChild(collegeNode);
        System.out.println(collegeNode.getLocalTranslation());
        System.out.println(collegeNode.getWorldTranslation());
        System.out.println(college.getWorldTranslation());
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
