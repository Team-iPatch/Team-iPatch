/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author Blue
 */
public class NiftyController extends AbstractAppState implements ScreenController{
    
    SimpleApplication app;
    AppStateManager stateManager;
    NiftyJmeDisplay niftyDisplay;
    Nifty nifty;
    Screen screen;
    Boolean shop;
    PlayerControlState playerControlState;
    
    @Override
    public void bind(Nifty nifty, Screen screen){
        this.nifty = nifty;
        this.screen = screen;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication)app;
        this.stateManager = stateManager;
        this.playerControlState = this.stateManager.getState(PlayerControlState.class);
        this.shop = false;
    }

    
    @Override
    public void update(float tpf) {
        
        if(screen.getScreenId().equals("hudScreen")){
            updateHP();
            updatePoints();
            updateGold();
            updateShop();
        }
        
    }
    
    public void showShop(Boolean inShop){
        this.shop = inShop;
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }

    @Override
    public void onStartScreen() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onEndScreen() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void startGame(){
        stateManager.getState(GameManagementState.class).startGame();
        nifty.gotoScreen("hudScreen");
        screen = nifty.getCurrentScreen();
    }
    
    public void updateShop(){
        Element layer = nifty.getScreen("hudScreen").findElementById("shopPanel");
        if(this.shop){
            layer.show();
        } else {
            layer.hide();
        }
    }
    
    public void updateHP(){
        Integer hp = stateManager.getState(PlayerControlState.class).getHP();
        Integer maxHP = stateManager.getState(PlayerControlState.class).getMaxHP();
        Element label = nifty.getScreen("hudScreen").findElementById("HPLabel");
        label.getRenderer(TextRenderer.class).setText(" HP: " + hp + "/" + maxHP);
    }
    
    public void updatePoints(){
        Integer points = stateManager.getState(PlayerControlState.class).getPoints();
        Element label = nifty.getScreen("hudScreen").findElementById("PointLabel");
        label.getRenderer(TextRenderer.class).setText(" Points: " + points);
    }
    
    public void updateGold(){
        Integer gold = stateManager.getState(PlayerControlState.class).getGold();
        Element label = nifty.getScreen("hudScreen").findElementById("GoldLabel");
        label.getRenderer(TextRenderer.class).setText(" Gold: " + gold);
    }
}
