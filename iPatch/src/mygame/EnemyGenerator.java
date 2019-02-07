/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * Class used to generate a simple enemy template.
 * @author Team iPatch
 */
public class EnemyGenerator {
	AssetManager assetManager;
	PhysicsSpace physicsSpace;
	
        /**
         * Class used to generate a simple enemy template.
         * @param assetManager Application's AssetManager.
         * @param physicsSpace Application's PhysicsSpace.
         */
	EnemyGenerator(AssetManager assetManager, PhysicsSpace physicsSpace){
		this.physicsSpace = physicsSpace;
		this.assetManager = assetManager;
	}
	
        /**
         * Generates an enemy.
         * @param modelLocation String locating the model to be used, usually
         * starting with /Models/
         * @param translation Vector3f in the world where the entity will be
         * spawned
         * @param radius Radius used for the capsule collider constructed by
         * BetterCharacterControl internally.
         * @param height Height used for the capsule collider constructed by
         * BetterCharacterControl internally. Must be at least twice the radius!
         * @param hp HP for the entity.
         * @return Spatial for generated enemy.
         */
	public Spatial generateEnemy(String modelLocation, Vector3f translation,
                                            float radius, float height, int hp){
            Spatial enemy = assetManager.loadModel(modelLocation);
            enemy.setLocalTranslation(translation);
            BetterCharacterControl characterControl = new BetterCharacterControl(radius, height, 20f);
            enemy.addControl(characterControl);
            //BetterCharacterControl has to be added to the spatial BEFORE any AI control!
            enemy.addControl(new EnemyControl(physicsSpace));
            enemy.setName("baddie");
            physicsSpace.add(characterControl);
            return enemy;
	}
}
