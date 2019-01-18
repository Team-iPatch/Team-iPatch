/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author blue
 */
public class EnemyGenerator {
	AssetManager assetManager;
	PhysicsSpace physicsSpace;
	
	EnemyGenerator(AssetManager assetManager, PhysicsSpace physicsSpace){
		this.physicsSpace = physicsSpace;
		this.assetManager = assetManager;
	}
	

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
