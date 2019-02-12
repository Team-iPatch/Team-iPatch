/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
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
 * @author fc831
 */
public class MiniGameBlockControl extends AbstractControl  implements PhysicsCollisionListener {
    PhysicsSpace physicsSpace;
    private final float speed = 30f;
    float lifeExpectancy = 2f; //Seconds before it is erased
    float lifetime; //Counts up to lifeExpectancy
    boolean isEnemy;
    RigidBodyControl block_phys; //Remove the physics control on deletion
    
    MiniGameBlockControl(PhysicsSpace physicsSpace,RigidBodyControl block_phy, 
            MiniGamePlayer MiniGamePlayer) {
        
        
        this.lifetime = 0;
        this.physicsSpace = physicsSpace;
        this.block_phys = block_phy;
        physicsSpace.addCollisionListener(this);
        this.physicsSpace = physicsSpace;
        physicsSpace.addCollisionListener(this);

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }
    
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.

    @Override
    protected void controlUpdate(float tpf) {
        lifetime += tpf;
        if(this.lifetime >= lifeExpectancy){
                destroy();
        }        

    }
     
    
    public void destroy(){
        physicsSpace.remove(block_phys);
        physicsSpace.removeCollisionListener(this);
        spatial.removeFromParent();
        spatial.getParent().detachChild(spatial);
        physicsSpace.remove(spatial.getControl(BetterCharacterControl.class));
    }
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
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

    @Override
    public void collision(PhysicsCollisionEvent event) {
        // EDIT THIS
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}