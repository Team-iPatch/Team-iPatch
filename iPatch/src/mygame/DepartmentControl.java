/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author cpl512
 */
public class DepartmentControl extends AbstractControl {
    private GhostControl ghost;
    private final String name;
    private float ghostRadius;
    private PhysicsSpace physicsSpace;
    
    DepartmentControl(String name, float ghostRadius, PhysicsSpace physicsSpace){
        this.physicsSpace = physicsSpace;
        this.name = name;
        this.ghostRadius = ghostRadius;
        this.ghost = new GhostControl(new SphereCollisionShape(this.ghostRadius));
        physicsSpace.add(ghost);
        spatial.addControl(ghost);
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        if (ghost.getOverlappingCount() > 0){
            System.out.println(ghost.getOverlappingObjects());
        }
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
    
    public Control cloneForSpatial(Spatial spatial) {
        DepartmentControl control = new DepartmentControl(name, ghostRadius,
                                                          physicsSpace);
        //TODO: copy parameters to new Control
        return control;
    }
    
    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        //TODO: load properties of this Control, e.g.
        //this.value = in.readFloat("name", defaultValue);
    }
    
    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
        //TODO: save properties of this Control, e.g.
        //out.write(this.value, "name", defaultValue);
    }
    
}
