/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author fc831
 */

public class MiniGameBlockBuilder {
    AssetManager assetManager;
    PhysicsSpace physicsSpace;
    MiniGamePlayer player;
	
    /**
     * Class used to generate a simple enemy template.
     * @param assetManager Application's AssetManager.
     * @param physicsSpace Application's PhysicsSpace.
     */
    MiniGameBlockBuilder(AssetManager assetManager, PhysicsSpace physicsSpace, MiniGamePlayer player){
            this.physicsSpace = physicsSpace;
            this.assetManager = assetManager;
            this.player = player;
    }
    public Spatial generateBlock(String modelLocation,Vector3f translation){
        Spatial block = assetManager.loadModel(modelLocation);
        block.setLocalTranslation(translation);
        BetterCharacterControl characterControl = new BetterCharacterControl(1f, 1f, 20f); // CHANGE THESE
        block.addControl(characterControl);
        //BetterCharacterControl has to be added to the spatial BEFORE any AI control!
        block.addControl(new MiniGameBlockControl(physicsSpace,new RigidBodyControl(0.0f),this.player));
        block.setName("Block");
        physicsSpace.add(characterControl);
        return block;
        
        
    }
}