/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.SimpleApplication;
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
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author cpl512
 */
public class BuildingGenerator {
    
    SimpleApplication app;
   
    BuildingGenerator(SimpleApplication app) {
        this.app = app;
    }
    
   //They require app in their parameters because of an issue with null pointers
    
    public Spatial generateDepartment(String name, Vector3f location, float ghostRadius){
        GhostControl ghost = new GhostControl(new SphereCollisionShape(ghostRadius));
        Box model = new Box(2,2,2);
        Geometry department = new Geometry(name, model);
        //this will be given by the environment map
        department.setLocalTranslation(location);
        Material mat1 = new Material(app.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
        switch(name){
            case "Computer Science":
                mat1.setColor("Color", ColorRGBA.Blue);
                break;
            case "Biology":
                mat1.setColor("Color", new ColorRGBA(0,0.4f,0,1));
                break;
            case "Dep3":
                mat1.setColor("Color", ColorRGBA.Orange);
                break;
            
        }
        department.setMaterial(mat1);
        app.getStateManager().getState(BulletAppState.class).getPhysicsSpace().add(ghost);
        department.addControl(ghost);
        PlayerControlState player = app.getStateManager().getState(PlayerControlState.class);
        department.addControl(new DepartmentControl(name, ghost, app));
        app.getRootNode().attachChild(department);
        return department;
    }
    
    public void generateTreasure(String name, Vector3f location, float ghostRadius){

        Node treasureNode = new Node();
        treasureNode.setLocalTranslation(location);
        Spatial treasure = app.getAssetManager().loadModel("Models/iPatchTreasure/iPatchTreasure.j3o");    
        treasure.setName(name);
        GhostControl ghost = new GhostControl(new SphereCollisionShape(ghostRadius));
        Material mat1 = new Material(app.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
        switch(name){
            case "Treasure1":
                mat1.setColor("Color", ColorRGBA.Brown);
                break;
            case "Treasure2":
                mat1.setColor("Color", ColorRGBA.Brown);
                break;            
        }
        treasure.setMaterial(mat1);
        app.getStateManager().getState(BulletAppState.class).getPhysicsSpace().add(ghost);
        treasure.addControl(ghost);
        PlayerControlState player = app.getStateManager().getState(PlayerControlState.class);
        treasure.addControl(new TreasureControl(name, ghost, app));
        treasureNode.attachChild(treasure);
        app.getRootNode().attachChild(treasureNode);

    }
    
    public void generateCollege(String name, Vector3f location, float ghostRadius){
        PhysicsSpace physicsSpace = app.getStateManager().getState(BulletAppState.class).getPhysicsSpace();
        Node collegeNode = new Node();
        collegeNode.setLocalTranslation(location);
        Spatial college = app.getAssetManager().loadModel("Models/turret02/turret02.j3o");
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
            cannons[i].setLocalTranslation(direction);
            cannons[i].rotate(0, i * (float)Math.PI/4f, 0);
            cannons[i].addControl(new ShooterControl(direction, true, app));
            collegeNode.attachChild(cannons[i]);
        }
        college.addControl(new CollegeControl(name, ghost, cannons, collegeNode, physicsSpace,this.app.getStateManager().getState(NiftyController.class)));
        collegeNode.attachChild(college);
        Geometry box = new Geometry("box", new Sphere(8,8,2f));
        box.setLocalTranslation(0,2,0);
        
        Material mat1 = new Material(app.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
        switch(name){
            case "Derwent":
                mat1.setColor("Color", ColorRGBA.LightGray);
                break;
            case "Vanbrugh":
                mat1.setColor("Color", ColorRGBA.Cyan);
                break;
            case "Alcuin":
                mat1.setColor("Color", ColorRGBA.Red);
                break;
            case "Constantine":
                mat1.setColor("Color", ColorRGBA.Pink);
                break;
            case "Goodricke":
                mat1.setColor("Color", ColorRGBA.Brown);
                break;
        } 
        box.setMaterial(mat1);
        collegeNode.attachChild(box);
        app.getRootNode().attachChild(collegeNode);
    }
}
