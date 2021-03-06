package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * App state used to manipulate the GUI and implement UI events.
 * @author Team iPatch & team 1
 */
public class NiftyController extends AbstractAppState implements ScreenController{
    
    SimpleApplication app;
    AppStateManager stateManager;
    NiftyJmeDisplay niftyDisplay;
    Nifty nifty;
    Screen screen;
    Boolean shop;
    String shopName;
    Boolean tPopup;
    String tPopupName;
    Boolean objective;
    PlayerControlState player;
    List<String> shopsUpgraded;
    List<String> treasureCollected;
    int totalTreasures;
    String remainingTreasure;
    int totalColleges;
    int totalPoints;
    Boolean inMenu;
    int collegesdefeated;
    List<String> questList;
    Boolean winner;
    Boolean minigamerunning = false;
    
    private Boolean paused = false;
	
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
        this.player = this.stateManager.getState(PlayerControlState.class);
        this.shop = false;
        this.shopName = "none";
        this.shopsUpgraded = new ArrayList();
        this.tPopup = false;
        this.tPopupName = "none";
        this.treasureCollected = new ArrayList();
        this.totalColleges = 5;
        this.totalPoints = 750;
        this.inMenu = false;
        this.collegesdefeated = 0;
        this.winner = false;
        this.totalTreasures = 2;
        showObjective(false);
        updateCollegesDefeated(0);
        this.questList = new ArrayList();
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
            updateTPopup();
            updateQuest();
        }
        if(collegesdefeated == totalColleges && !winner){
            this.win();
        }
        showShop(false);
        showTPopup(false);
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
        this.inMenu = inShop;
    }
    
    /**
     * Called by Department entities to show that the player is in range. 
     * @param inShop Boolean, usually true.
     * @param shopName Name of the department, choices hardcoded internally.
     * Currently, either Computer Science, Biology or Maths.
     */
    public void showShop(Boolean inShop, String shopName){
        this.shop = inShop;
        this.shopName = shopName;
        this.inMenu = inShop;
    }
 
    public void showTPopup(Boolean inTPopup){
        this.tPopup = inTPopup;
        this.tPopupName = "none";
        this.inMenu = inTPopup;
    }
    
    public void showTPopup(Boolean inTPopup, String tPopupName){
        this.tPopup = inTPopup;
        this.tPopupName = tPopupName;
        this.inMenu = inTPopup;
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
        Button acceptQuest = screen.findNiftyControl("acceptQuest", Button.class);
        Button completeQuest = screen.findNiftyControl("completeQuest", Button.class);
        Label departmentLabel = screen.findNiftyControl("departmentLabel", Label.class);
        player=stateManager.getState(PlayerControlState.class);
        // Checks if the player is currently within range of a Department.
        
        if(this.shop && this.app.getRootNode().getChild("minigameplayer")==null){
            layer.show();
            // Displays different text depending on whether the Department
            // is named Computer Science or Biology. Additional Departments
            // have to have their UI functionality implemented here.
            switch(this.shopName){
                case "Computer Science":
                    int killShip=5;
                    departmentLabel.setText("Computer Science Department");
                    // If the player already bought the Comp Sci upgrade, 
                    // displays a different message.
                    if(this.shopsUpgraded.contains("Computer Science")){
                        upgradeButton.setText("Upgrade: Sold out");
                    } else {
                        upgradeButton.setText("Upgrade: Piercing Cannonballs (50 gold)");
                    }
                    //Check whether they're on a quest, and whether the current one is done
                    if (this.questList.contains("Computer Science")){
                        acceptQuest.setText("Quest already finished");
                        completeQuest.disable();
                    }   else if (player.getQuestState()==true &&                            
                            player.isKillQuest()==false){
                        acceptQuest.setText("You are currently busy on another quest");
                    }   else if (player.getQuestState()==true &&
                            player.getQuestProgress()>0){
                        acceptQuest.setText("You have not complete our quest yet");
                    }   else if (player.getQuestState()==true &&
                            player.getQuestProgress()==0){
                        acceptQuest.setText("");
                        completeQuest.enable();                      
                    }   else{
                        acceptQuest.setText("Kill "+ killShip +" ships for us");                      
                        completeQuest.disable();
                    }
                    break;
                case "Biology":
                    int treasureTarget=10;
                    departmentLabel.setText("Biology Department");
                    // If the player already bought the Biology upgrade,
                    // displays a different message.
                    
                    if(this.shopsUpgraded.contains("Biology")){
                        upgradeButton.setText("Upgrade: Sold out");
                    } else {
                        upgradeButton.setText("Upgrade: +100 max HP (50 gold)");    
                    }
                    //Check whether they're on a quest, and whether the current one is done
                    if (this.questList.contains("Biology")){
                        acceptQuest.setText("Quest already finished");
                        completeQuest.disable();
                    }
                    else if (player.getQuestState()==true &&                            
                            player.isTreasureQuest()==false
                            ){
                        acceptQuest.setText("You are currently busy on another quest");
                    }
                    else if (player.getQuestState()==true &&
                            player.getQuestProgress()>0){
                        acceptQuest.setText("You have not complete our quest yet");
                    }
                    else if (player.getQuestState()==true &&
                            player.getQuestProgress()>0){
                        acceptQuest.setText("");
                        completeQuest.enable();                      
                    }
                    else{
                        acceptQuest.setText("Find "+ treasureTarget +" treasures for us");
                        completeQuest.disable();
                    }
                    break;
                case "Maths":
                    departmentLabel.setText("Maths Department");
                    // If the player already bought the Biology upgrade,
                    // displays a different message.
                    if(this.shopsUpgraded.contains("Maths")){
                        upgradeButton.setText("Upgrade: Sold out");
                    } else {
                        upgradeButton.setText("Upgrade: +5 damage (50 gold)");    
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
    
    public void updateTPopup(){
        // Gets UI elements as defined by mainMenu.xml. Only changes elements
        // that aren't constant, ie the header label and upgrade button
        Element layer = nifty.getScreen("hudScreen").findElementById("tPopupPanel");
        Button collectButton = screen.findNiftyControl("CollectButton", Button.class);
        Label treasureLabel = screen.findNiftyControl("treasureLabel", Label.class);
        remainingTreasure = ((totalTreasures - treasureCollected.size()) + " treasures remaining");
        
        // Checks if the player is currently within range of a Treasure.
        if(this.tPopup){
            layer.show();
            // Displays different text depending on whether the Treasure
            // is named Treasure1 or Treasure2. Additional Treasures
            // have to have their UI functionality implemented here.
            switch(this.tPopupName){
                case "Treasure1":
                    String labelText = ("Treasure1 - " + remainingTreasure);
                    treasureLabel.setText(labelText);
                    // If the player already collected this treasure, 
                    // displays a different message.
                    if(this.treasureCollected.contains("Treasure1")){
                        collectButton.setText("Treasure has already been collected");
                    } else {
                        collectButton.setText("Click to collect 50 gold");
                    }
                    break;
                case "Treasure2":
                    String labelText2 = ("Treasure2 - " + remainingTreasure);
                    treasureLabel.setText(labelText2);
                    // If the player already collected this treasure,
                    // displays a different message.
                    if(this.treasureCollected.contains("Treasure2")){
                        collectButton.setText("Treasure has already been collected");
                    } else {
                        collectButton.setText("Click to collect 20 gold");    
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
     * Same procedure for implementing quests
     * For now we'll have one quest per department
     */
    
    public void acceptQuest(){
        player=stateManager.getState(PlayerControlState.class);
        if (player.getQuestState()==false){
            if(this.shopName.equals("Computer Science") &&
               !this.questList.contains("Computer Science")){
                player.questStart();
                player.takeKillQuest(5);                
            }
            else if (this.shopName.equals("Biology") &&
                    !this.questList.contains("Biology")){
                player.questStart();
                player.takeTreasureQuest(10);
            }
                
        }
    
    }
	
    public void completeQuest(){
        player=stateManager.getState(PlayerControlState.class);
        if (player.getQuestProgress()==0){
            if(this.shopName.equals("Computer Science")){
                //reward, set quest complete, add to completed quest list
                this.questList.add("Computer Science");
                player.setMaxHP(player.getMaxHP()+50);    
                player.endQuest();
                player.endkillQuest();
            } else if (this.shopName.equals("Biology")){
                player.increaseSpeed(5f);
                player.endQuest();
                player.endTreasureQuest();
            }
        }
         
    }
    
    public void StartMinigame(){
        PlayerControlState player = stateManager.getState(PlayerControlState.class);
        if(stateManager.getState(MiniGame.class) == null 
                && player.getGold() >= 20){
            player.incrementGold(-20);
            MiniGame play = new MiniGame();
            stateManager.attach(play);
        }
    }
    
    
    /**
     * Called when the player clicks the topmost, unique upgrade button.
     * Functionality depends on the shop the player is in.
     */
    public void shopUpgrade(){
        player = stateManager.getState(PlayerControlState.class);
        
        if(player.getGold()>=50){
            if(this.shopName.equals("Computer Science") && !this.shopsUpgraded.contains("Computer Science")){
                player.setPiercing(true);
                this.shopsUpgraded.add("Computer Science");
                player.incrementGold(-50);
            }
            else if(this.shopName.equals("Biology") && !this.shopsUpgraded.contains("Biology")){
                Integer maxHP = stateManager.getState(PlayerControlState.class).getMaxHP();
                player.setMaxHP(maxHP+100);
                player.addHP(100);
                this.shopsUpgraded.add("Biology");
                player.incrementGold(-50);
            }
            else if(this.shopName.equals("Maths") && !this.shopsUpgraded.contains("Maths")){
                player.increaseShotDamage(5);
                this.shopsUpgraded.add("Maths");
                player.incrementGold(-50);
            }
        }
    }
    
    /**
     * Called when the player clicks the "Restore HP" button in any department.
     */
    public void shopHeal(){
        player = stateManager.getState(PlayerControlState.class);
        if(player.getGold()>=20 && !Objects.equals(player.getHP(), player.getMaxHP())){
            Integer maxHP = player.getMaxHP();
            player.setHP(maxHP);
            player.incrementGold(-20);
        }
    }
    
    public void collectTreasure(){
        player = stateManager.getState(PlayerControlState.class);
        if(this.tPopupName.equals("Treasure1") && !this.treasureCollected.contains("Treasure1")){
            this.treasureCollected.add("Treasure1");
            player.incrementGold(25);
            player.incrementPoints(25);
        }
        else if(this.tPopupName.equals("Treasure2") && !this.treasureCollected.contains("Treasure2")){
            this.treasureCollected.add("Treasure2");
            player.incrementGold(20);
            player.incrementPoints(25);
        }
        
    }
    /**
     * Shows the menu .
     * @param show false unless holding down the key to show the menu.
     */
    public void showObjective(boolean show){
        Element layer = nifty.getScreen("hudScreen").findElementById("ObjectivePanel");
        if(show){
            layer.show();
            objective = true;
        }else{
            layer.hide();
            objective = false;
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
    
    public void updateQuest(){
        Label QuestLabel = screen.findNiftyControl("QuestLabel", Label.class);
        player = stateManager.getState(PlayerControlState.class);
        if (player.getQuestState()==true){
            int total = player.getCounter();
            int progress = player.getProgress();
            int questProgress=player.getQuestProgress();
            if (player.isKillQuest()==true){
                if (questProgress==0){
                    QuestLabel.setText("Quest Complete, Return to Computer Science");
                }
                else{
                    QuestLabel.setText(progress + "/" + total + " Ships killed");      
                }                      
            }
            else if (player.isTreasureQuest()==true){
                if (questProgress==0){
                    QuestLabel.setText("Quest Complete, Return to Biology");
                }
                else{
                QuestLabel.setText(progress + "/" + total + " Treasure found");                
            }
            }
        }
        else{
            QuestLabel.setText("No active Quest");
        }
        
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
    
    public void updateCollegesDefeated(int killnum){
        collegesdefeated += killnum;
        Element label = nifty.getScreen("hudScreen").
                        findElementById("CollegeObjectiveLabel");
        label.getRenderer(TextRenderer.class).setText(" Colleges Defeated: " + 
                                        collegesdefeated + "/"+totalColleges);
    }
    
    /**
     * Called when the player dies, displays a game over screen.
     */
    public void gameOver(){
        nifty.gotoScreen("gameoverScreen");
    }
    
    /**
     * Called when player completes all objectives, a win screen.
     * Added for assessment 4: win screen shows time taken and points obtained
     */
    public void win(){
        player = stateManager.getState(PlayerControlState.class);
        Screen winScreen = nifty.getScreen("winScreen");
        Element pointsLabel = winScreen.findElementById("pointsLabel");
        pointsLabel.getRenderer(TextRenderer.class).setText("Points obtained: " 
                                                          + player.getPoints());
        Element timeLabel = winScreen.findElementById("timeLabel");
        float timeTaken = player.getTime();
        timeLabel.getRenderer(TextRenderer.class).setText("Time taken: " + 
                                                    timeTaken + " seconds");
        nifty.gotoScreen("winScreen");
        winner = true;
    }
}
