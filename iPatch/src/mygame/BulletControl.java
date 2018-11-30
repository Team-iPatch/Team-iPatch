package mygame;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;
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
public class BulletControl extends AbstractControl {
    
    private final float speed = 110f;
    public Vector3f direction;
    float lifeExpectancy = 2f; //Seconds before it is erased
    float lifetime;
    boolean isEnemy;

    public BulletControl(Vector3f direction, boolean isEnemy) {
        this.direction = direction.normalize();
        this.direction.multLocal(speed);
        this.lifetime = 0;
        this.isEnemy = isEnemy;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        spatial.move(direction.mult(tpf));
        lifetime += tpf;
        if (lifetime > lifeExpectancy) {
            spatial.removeFromParent();
        }
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
    @Override
    public Control cloneForSpatial(Spatial spatial) {
        BulletControl control = new BulletControl(direction, isEnemy);
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
