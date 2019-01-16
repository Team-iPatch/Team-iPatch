/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.control.BetterCharacterControl;
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
 * @author Blue
 */
public class AIChaserControl extends AbstractControl {
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.
    BetterCharacterControl enemyControl;
    Spatial target;
    State state;
    Vector3f moveDirection, viewDirection;
    float speed;

    private enum State{
        Idle,
        Chasing
    }
    
    public AIChaserControl(Spatial target, float speed){
        this.target = target;
        this.state = State.Idle;
        this.speed = speed;
        //this.enemyControl = spatial.getControl(BetterCharacterControl.class);
        this.moveDirection = Vector3f.UNIT_XYZ;
        this.viewDirection = Vector3f.UNIT_XYZ;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        switch(this.state){
            case Idle:
                Idle();
            case Chasing:
                Chase();
        }
    }
    
    private void Idle(){
        
    }
    
    private void Chase(){
        if(this.target != null){
            Vector3f targetDirection = this.target.getWorldTranslation().subtract(spatial.getWorldTranslation());
            targetDirection.y = 0;
            targetDirection.normalizeLocal();
            this.viewDirection.set(targetDirection);
            spatial.getControl(BetterCharacterControl.class).setViewDirection(this.viewDirection);
            
            Vector3f forwardDirection = spatial.getWorldRotation().mult(Vector3f.UNIT_Z);
            this.moveDirection.addLocal(forwardDirection.mult(this.speed));
            spatial.getControl(BetterCharacterControl.class).setWalkDirection(this.moveDirection);
        }
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
    
    
    @Override
    public Control cloneForSpatial(Spatial spatial) {
        AIChaserControl control = new AIChaserControl(this.target, this.speed);
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
