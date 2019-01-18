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
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 *
 * @author cpl512
 */
public class BuildingGeneratorState extends BaseAppState {
    
    @Override
    protected void initialize(Application app) {
        
    }
    
   //They require app in their parameters because of an issue with null pointers
    
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
    
    public void generateCollege(String name, Vector3f location, float ghostRadius, SimpleApplication app){
        PhysicsSpace physicsSpace = app.getStateManager().getState(BulletAppState.class).getPhysicsSpace();
        Node collegeNode = new Node();
        collegeNode.setLocalTranslation(location);
        Spatial college = app.getAssetManager().loadModel("Models/"
                                                     + "turret02/turret02.j3o");
        college.setName("college");
        GhostControl ghost = new GhostControl(new SphereCollisionShape(ghostRadius));
        college.addControl(ghost);
        physicsSpace.add(ghost);
        RigidBodyControl rigidBody = new RigidBodyControl(2f);
        college.addControl(rigidBody);
        rigidBody.setKinematic(true);
        physicsSpace.add(rigidBody);
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
        
        college.addControl(new CollegeControl(name, ghost, cannons, collegeNode, physicsSpace));
        collegeNode.attachChild(college);
        app.getRootNode().attachChild(collegeNode);
    }
    
    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime
    }


    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}
