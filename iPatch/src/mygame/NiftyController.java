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
import com.sun.media.jfxmedia.logging.Logger;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.util.ArrayList;
import java.util.List;

/**
 * App state used to manipulate the GUI and implement UI events.
 * @author Team iPatch
 */
public class NiftyController extends AbstractAppState implements ScreenController{
    
    SimpleApplication app;
    AppStateManager stateManager;
    NiftyJmeDisplay niftyDisplay;
    Nifty nifty;
    Screen screen;
    Boolean shop;
    String shopName;
    PlayerControlState playerControlState;
    List<String> shopsUpgraded;
    
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
        this.shopName = "none";
        this.shopsUpgraded = new ArrayList();
    }

    
    @Override
    public void update(float tpf) {
        // If the player is in the hudScreen, updates relevant UI elements every 
        // frame.
        if(screen.getScreenId().equals("hudScreen")){
            updateHP();
            updatePoints();
            updateGold();
            updateShop();
        }
        
    }
    
    /**
     * Called by Department entities to show that the player is NOT in their range.
     * A second argument, shopName, is called when the player is in range and 
     * shows which type of department it's close to.
     * @param inShop Boolean, usually false.
     */
    public void showShop(Boolean inShop){
        this.shop = inShop;
        this.shopName = "none";
    }
    
    /**
     * Called by Department entities to show that the player is in range. 
     * @param inShop Boolean, usually true.
     * @param shopName Name of the department, choices hardcoded internally.
     * Currently, either Computer Science or Biology.
     */
    public void showShop(Boolean inShop, String shopName){
        this.shop = inShop;
        this.shopName = shopName;
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
    
    /**
     * Used to initialise the game from the start screen.
     */
    public void startGame(){
        stateManager.getState(GameManagementState.class).startGame();
        nifty.gotoScreen("hudScreen");
        screen = nifty.getCurrentScreen();
    }
    
    /**
     * Used to update shop interfaces whenever the player is in range of a 
     * Department.
     */
    public void updateShop(){
        // Gets UI elements as defined by mainMenu.xml. Only changes elements
        // that aren't constant, ie the header label and upgrade button
        Element layer = nifty.getScreen("hudScreen").findElementById("shopPanel");
        Button upgradeButton = screen.findNiftyControl("UpgradeButton", Button.class);
        Label departmentLabel = screen.findNiftyControl("departmentLabel", Label.class);
        
        // Checks if the player is currently within range of a Department.
        if(this.shop){
            layer.show();
            // Displays different text depending on whether the Department
            // is named Computer Science or Biology. Additional Departments
            // have to have their UI functionality implemented here.
            switch(this.shopName){
                case "Computer Science":
                    departmentLabel.setText("Computer Science Department");
                    // If the player already bought the Comp Sci upgrade, 
                    // displays a different message.
                    if(this.shopsUpgraded.contains("Computer Science")){
                        upgradeButton.setText("Upgrade: Sold out");
                    } else {
                        upgradeButton.setText("Upgrade: Piercing Cannonballs (50 gold)");
                    }
                    break;
                case "Biology":
                    departmentLabel.setText("Biology Department");
                    // If the player already bought the Biology upgrade,
                    // displays a different message.
                    if(this.shopsUpgraded.contains("Biology")){
                        upgradeButton.setText("Upgrade: Sold out");
                    } else {
                        upgradeButton.setText("Upgrade: +100 max HP (50 gold)");    
                    }
                    break;
                default:
                    System.out.println("Sorry, nothing");
            }
        } else {
            // Hides the UI layer if the player is not close to a Department.
            layer.hide();
        }
    }
    
    /**
     * Called when the player clicks the topmost, unique upgrade button.
     * Functionality depends on the shop the player is in.
     */
    public void shopUpgrade(){
        playerControlState = stateManager.getState(PlayerControlState.class);
        
        if(playerControlState.getGold()>=50){
            if(this.shopName.equals("Computer Science") && !this.shopsUpgraded.contains("Computer Science")){
                playerControlState.setPiercing(true);
                this.shopsUpgraded.add("Computer Science");
                playerControlState.incrementGold(-50);
            }
            else if(this.shopName.equals("Biology") && !this.shopsUpgraded.contains("Biology")){
                Integer maxHP = stateManager.getState(PlayerControlState.class).getMaxHP();
                playerControlState.setMaxHP(maxHP+100);
                playerControlState.addHP(100);
                this.shopsUpgraded.add("Biology");
                playerControlState.incrementGold(-50);
            }
        }
    }
    
    /**
     * Called when the player clicks the "Restore HP" button in any department.
     */
    public void shopHeal(){
        playerControlState = stateManager.getState(PlayerControlState.class);

        if(playerControlState.getGold()>=20){
            Integer maxHP = playerControlState.getMaxHP();
            playerControlState.setHP(maxHP);
            playerControlState.incrementGold(-20);
        }
    }
    
    /**
     * Updates HP display on the HUD.
     */
    public void updateHP(){
        Integer hp = stateManager.getState(PlayerControlState.class).getHP();
        Integer maxHP = stateManager.getState(PlayerControlState.class).getMaxHP();
        Element label = nifty.getScreen("hudScreen").findElementById("HPLabel");
        label.getRenderer(TextRenderer.class).setText(" HP: " + hp + "/" + maxHP);
    }
    
    /**
     * Updates the point display on the HUD.
     */
    public void updatePoints(){
        Integer points = stateManager.getState(PlayerControlState.class).getPoints();
        Element label = nifty.getScreen("hudScreen").findElementById("PointLabel");
        label.getRenderer(TextRenderer.class).setText(" Points: " + points);
    }
    
    /**
     * Updates the gold display on the HUD.
     */
    public void updateGold(){
        Integer gold = stateManager.getState(PlayerControlState.class).getGold();
        Element label = nifty.getScreen("hudScreen").findElementById("GoldLabel");
        label.getRenderer(TextRenderer.class).setText(" Gold: " + gold);
    }
    
    /**
     * Called when the player dies, displays a game over screen.
     */
    public void gameOver(){
        nifty.gotoScreen("gameoverScreen");
    }
}
