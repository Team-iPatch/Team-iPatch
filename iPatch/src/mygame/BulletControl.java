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
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 * BulletControl used to control individual projectiles, typically attached by
 * ShooterControl. Determines various bullet properties and controls its 
 * trajectory and collisions.
 * 
 * @author Team iPatch
 */
public class BulletControl extends AbstractControl 
                           implements PhysicsCollisionListener{
    
    private final float speed = 30f;
    public Vector3f direction;
    private int damage;
    float lifeExpectancy = 2f; //Seconds before it is erased
    float lifetime; //Counts up to lifeExpectancy
    boolean isEnemy;
    PhysicsSpace physicsSpace;
    RigidBodyControl bullet_phys; //Remove the physics control on deletion
    PlayerControlState playerControlState;
    Spatial lastEnemyHit;

    /**
     * BulletControl used to control individual projectiles, typically attached
     * by ShooterControl. Determines various bullet properties and controls its
     * trajectory and collisions.
     * @param direction Vector3f indicating direction the bullet will travel
     * @param damage int deciding how much damage the bullet deals on impact
     * @param isEnemy indicates whether the bullet was shot by the player
     * or an enemy
     * @param physicsSpace PhysicsSpace to which the bullet will attach its 
     * colliders
     * @param bullet_phys RigidBodyControl the Control will control.
     * @param playerControlState PlayerControlState, used to deal damage
     */
    public BulletControl(Vector3f direction, int damage, boolean isEnemy,
                      PhysicsSpace physicsSpace, RigidBodyControl bullet_phys,
                      PlayerControlState playerControlState){
        this.damage = damage;
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
    
    /**
     * Destroys the bullet, detaching spatial and removing RigidBodyControl
     * from the physics space
     */
    public void destroy(){
        physicsSpace.remove(bullet_phys);
        physicsSpace.removeCollisionListener(this);
        spatial.removeFromParent();
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}

    /**
     * Used internally, collision handling for the bullets.
     * @param event Automatically passed whenever this method is called.
     */ 
    @Override
    public void collision(PhysicsCollisionEvent event) {
        // Finds which node was not the bullet
        Spatial testNode = null;
        if(event.getNodeA().equals(spatial)){
            testNode = event.getNodeB();
        } else if (event.getNodeB().equals(spatial)){
            testNode = event.getNodeA();
        }
        // Tests whether there is a second body in the collision and whether
        // the bullet came from the player
        if(testNode != null && !isEnemy){
            // lastEnemyHit is used to prevent piercing bullets from dealing more
            // than a single tick of damage per hit
            if(!testNode.equals(lastEnemyHit)){
                if(testNode.getName().equals("baddie")){
                    testNode.getControl(EnemyControl.class).reduceHP(damage);

                    if(testNode.getControl(EnemyControl.class).getHP() <= 0){
                        playerControlState.incrementPoints(20);
                        playerControlState.incrementGold(10);
                    }

                    if(!playerControlState.isPiercing()){
                        this.lifetime = lifeExpectancy;
                    }
                } else if(testNode.getName().equals("college")){
                    CollegeControl collegeControl = testNode.getControl(CollegeControl.class);
                    collegeControl.reduceHP(damage);
                    if(collegeControl.getHP() <= 0){
                        playerControlState.incrementGold(1000);
                        playerControlState.incrementPoints(2000);
                    }
                    if(!playerControlState.isPiercing()){
                        this.lifetime = lifeExpectancy;
                    }
                } else if(testNode.getName().equals("hitBox")){
                    this.lifetime = lifeExpectancy;
                }
                lastEnemyHit = testNode;                
            }
        }
        else if(testNode != null && testNode.getName().equals("player")){
            playerControlState.reduceHP(damage);
            this.lifetime = lifeExpectancy;
        } else if(testNode != null && testNode.getName().equals("hitBox")) {
            this.lifetime = lifeExpectancy;
        }
    }
}
    
