package mygame;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
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


public class BulletControl extends AbstractControl 
                           implements PhysicsCollisionListener{
    
    private final float speed = 50f;
    public Vector3f direction;
    float lifeExpectancy = 2f; //Seconds before it is erased
    float lifetime;
    boolean isEnemy;
    PhysicsSpace physicsSpace;
    RigidBodyControl bullet_phys; //Remove the physics control on deletion
    PlayerControlState playerControlState;

    public BulletControl(Vector3f direction, boolean isEnemy,
                      PhysicsSpace physicsSpace, RigidBodyControl bullet_phys,
                      PlayerControlState playerControlState){
        this.direction = new Vector3f(direction);
        this.lifetime = 0;
        this.isEnemy = isEnemy;
        this.physicsSpace = physicsSpace;
        this.bullet_phys = bullet_phys;
        this.playerControlState = playerControlState;
        physicsSpace.addCollisionListener(this);
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        spatial.move(direction.mult(tpf*speed));
        lifetime += tpf;
        if (lifetime > lifeExpectancy) {
            destroy();
        }
    }
    
    public void destroy(){
        physicsSpace.remove(bullet_phys);
        physicsSpace.removeCollisionListener(this);
        spatial.removeFromParent();
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
    @Override
    public Control cloneForSpatial(Spatial spatial) {
        BulletControl control = new BulletControl(direction, isEnemy,
                                physicsSpace, bullet_phys, playerControlState);
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

    @Override
    public void collision(PhysicsCollisionEvent event) {
        if(event.getNodeA().equals(spatial) || event.getNodeB().equals(spatial)){
            if(!isEnemy){
                String nameA = event.getNodeA().getName();
                String nameB = event.getNodeB().getName();
                if(nameA.equals("baddie")){
                    event.getNodeA().getControl(EnemyControl.class).reduceHP(5);
                    playerControlState.incrementPoints(10);
                    this.lifetime = lifeExpectancy;
                } 
                else if(nameB.equals("baddie")){
                    event.getNodeB().getControl(EnemyControl.class).reduceHP(5);
                    playerControlState.incrementPoints(10);
                    this.lifetime = lifeExpectancy;
                }
            }
            else if(event.getNodeA().getName().equals("Player") || event.getNodeB().getName().equals("Player")){
                playerControlState.reduceHP(10);
                this.lifetime = lifeExpectancy;
                System.out.println(playerControlState.getHP());
            }
        }   
    }
}
    
