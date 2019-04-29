package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * Generates Rocks for the player to dodge in the minigame.
 * @author fc831 & MagicSid
 */

public class MiniGameBlockBuilder {
    AssetManager assetManager;
    PhysicsSpace physicsSpace;
    MiniGamePlayer player;
    SimpleApplication app;
    /**
     * Class used to generate a simple enemy template.
     * @param assetManager Application's AssetManager.
     * @param physicsSpace Application's PhysicsSpace.
     */
    MiniGameBlockBuilder(SimpleApplication app, PhysicsSpace physicsSpace, MiniGamePlayer player){
            this.physicsSpace = physicsSpace;
            this.app = app;
            this.assetManager = app.getAssetManager();
            this.player = player;
    }
    public void generateBlock(Vector3f translation){
        Spatial block = assetManager.loadModel("Models/IpatchRockPile/IpatchRockPile.j3o");
        block.setLocalTranslation(translation);
        BetterCharacterControl characterControl = new BetterCharacterControl(1f, 1f, 1f); // CHANGE THESE
        block.addControl(characterControl);
        //BetterCharacterControl has to be added to the spatial BEFORE any AI control!
        block.addControl(new MiniGameBlockControl(app,new BetterCharacterControl((float)0.75,1,1),this.player));
        block.setName("rock");
        physicsSpace.add(characterControl);
        app.getRootNode().attachChild(block);
        block.getControl(BetterCharacterControl.class).setWalkDirection(new Vector3f(0,0,-10));
        
    }
}
