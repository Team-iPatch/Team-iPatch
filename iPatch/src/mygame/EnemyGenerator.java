/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * Class used to generate a simple enemy template.
 * @author Team iPatch
 */
public class EnemyGenerator {
        SimpleApplication app;
        PhysicsSpace physicsSpace;
        AssetManager assetManager;
        PlayerControlState player;
	
        /**
         * Class used to generate a simple enemy template.
         * @param assetManager Application's AssetManager.
         * @param physicsSpace Application's PhysicsSpace.
         */
	EnemyGenerator(Application app){
            this.app = (SimpleApplication) app;
            this.physicsSpace = app.getStateManager().
                        getState(BulletAppState.class).getPhysicsSpace();
            this.assetManager = app.getAssetManager();
            this.player = app.getStateManager().getState(PlayerControlState.class);
	}
	
        /**
         * Generates an enemy.
         * @param modelLocation String locating the model to be used, usually
         * starting with /Models/
         * @param translation Vector3f in the world where the entity will be
         * spawned
         * @return Spatial for generated enemy.
         */
	public Spatial generateEnemy(String modelLocation, Vector3f translation){
            Spatial enemy = assetManager.loadModel(modelLocation);
            enemy.setLocalTranslation(translation);
            BetterCharacterControl characterControl = new BetterCharacterControl(1.5f, 3f, 20f);
            enemy.addControl(characterControl);
            //BetterCharacterControl has to be added to the spatial BEFORE any AI control!
            enemy.addControl(new EnemyControl(physicsSpace, player.getLevel()));
            enemy.setName("baddie");
            physicsSpace.add(characterControl);
            return enemy;
	}
}
