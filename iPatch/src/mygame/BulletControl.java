package mygame;

import com.jme3.bullet.PhysicsSpace;
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


public class BulletControl extends AbstractControl {
    
    private final float speed = 40f;
    public Vector3f direction;
    float lifeExpectancy = 5f; //Seconds before it is erased
    float lifetime;
    boolean isEnemy;
    PhysicsSpace physicsSpace;
    RigidBodyControl bullet_phys; //Remove the physics control on deletion

    public BulletControl(Vector3f direction, boolean isEnemy,
                      PhysicsSpace physicsSpace, RigidBodyControl bullet_phys) {
        this.direction = new Vector3f(direction);
        this.lifetime = 0;
        this.isEnemy = isEnemy;
        this.physicsSpace = physicsSpace;
        this.bullet_phys = bullet_phys;
    }
    
    protected void controlUpdate(float tpf) {
        spatial.move(direction.mult(tpf*speed));
        lifetime += tpf;
        if (lifetime > lifeExpectancy) {
            destroy();
        }
    }
    
    public void destroy(){
		physicsSpace.remove(bullet_phys);
		spatial.removeFromParent();
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
    @Override
    public Control cloneForSpatial(Spatial spatial) {
        BulletControl control = new BulletControl(direction, isEnemy,
                                                  physicsSpace, bullet_phys);
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
