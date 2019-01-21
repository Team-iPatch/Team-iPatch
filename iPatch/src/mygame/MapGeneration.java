
package mygame;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.Node;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.texture.Texture;
import java.util.Random;
import fastnoise.FastNoise;
/**
 *
 * @author Joseph Leigh
 */
public class MapGeneration 
        
     
{
    static BulletAppState bulletAppState;
    static Node rootNode;
    static AssetManager assetManager;
    static int xAxis = 220; //Map width
    static int yAxis = 64;  //Map heigh
    static int zAxis = 220; //Map length
    static int[][] heightMap = new int[xAxis][zAxis];
    static boolean accessibleLocations[][] = new boolean[xAxis][zAxis];
    
    public static void loadArrayIntoWorld(TerrainType[][][] worldArray) //This sub loads blocks into the world based off of and array created by generate Array.
    {
        for (int x = 0; x < worldArray.length; x += 1)
        {
            for (int y = 0; y < worldArray[x].length; y += 1)
            {
                for (int z = 0; z < worldArray[x][y].length; z += 1)
                {
                    if (worldArray[x][y][z] != null)
                    {
                        loadBox(x, y, z, worldArray[x][y][z]); 
                    }
                }
            }
        }
    }
    
    private static void loadBox(int xCo, int yCo, int zCo, TerrainType skin) //Loads a box into the world and gives it a skin and adds a hit box
    {
        Box box = new Box(0.5f, 0.5f, 0.5f);
        Geometry box_geom = new Geometry("box", box);
        Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        if (null == skin)
        {
            mat.setColor("Color", ColorRGBA.Green);
        }
        else switch (skin)      //Gives the box a texture based on the box's TerrainType value in the array
        {
            case SAND:
                Texture tex = assetManager.loadTexture("Textures/dirt.jpg");
                mat.setTexture("ColorMap", tex);
                break;
            default:
                Texture tex1 = assetManager.loadTexture("Textures/grass.png");
                mat.setTexture("ColorMap", tex1);
                break;
        }
        box_geom.setMaterial(mat);
        box_geom.setLocalTranslation(xCo+0.5f, yCo+0.5f, zCo+0.5f); //Raises box above scene floor
        rootNode.attachChild(box_geom);
        if (yCo == 1)             //If the box height == 1 then a hit box is added
        {
            addHitBox(xCo,yCo,zCo);  
        }
    }
    
    public static TerrainType[][][] generateArray() //Generates a 3-D array of TerrainType
    {
        generateHeightMap();            
        TerrainType[][][] mapArray = new TerrainType[MapGeneration.xAxis][MapGeneration.yAxis][MapGeneration.zAxis];
        for (int x = 0; x < MapGeneration.heightMap.length; x += 1)
        {
            for (int y = 0; y< MapGeneration.heightMap[x].length; y+= 1)
            {
               fillMap(x,y,mapArray);
            }
        }
        findAccessible();
        return mapArray;
    }
   
    private static void generateHeightMap() //Uses the fastnoise library to generate a 2-D height map 
    {
        Random rand = new Random();                                                                             //initiates random
        FastNoise.seed = rand.nextInt(100000000) + 1;                                                           //set fastnoise see to new random number above 1
        FastNoise.init();                                                                                       //generates fastnoise
        for (int x = 0; x < MapGeneration.heightMap.length; x += 1)                                             //iterates through a portion og the fast noise graph to fill out the map
        {
            for (int y = 0; y< MapGeneration.heightMap[x].length; y += 1)
            {
                float frequency = 32.0f; 
                if (((int)(32.0 * FastNoise.noise(x / frequency, y / frequency)) > 0))                          //if the fast noise value is above 0 then it is masked 
                {                                                                                               //(value is normalised towards 0 near the edge of the array are to one near the center)
                    int xd = (heightMap.length / 2 ) - x;                                                       //the masked value is then added to the height map.
                    float xdf = xd / ((float) (heightMap.length/2));
                    int yd = (heightMap[x].length / 2 ) - y;
                    float ydf = yd / ((float) (heightMap[x].length/2));
                    float mask = (float)(Math.sqrt(1.0f - Math.abs(xdf)) * Math.sqrt(1.0f - Math.abs(ydf)));
                    float height = (float) (mask * (32.0f * FastNoise.noise(x / frequency, y/frequency)));
                    MapGeneration.heightMap[x][y] = (int) height;                    
                }
            }    
        }
    }
    
    private static void findAccessible() //Finds acceasble locations in the array
    {

    }
    
    private static void fillMap(int x, int y,  TerrainType[][][] mapArray)  //sets block textures and fixes holes in the mapArray
    {
         if (MapGeneration.heightMap[x][y] > 0)
                {
                    assignTexture(x,y,MapGeneration.heightMap[x][y],mapArray);
                }
                             if (x > 0 && y > 0 && x < MapGeneration.heightMap.length && y < MapGeneration.heightMap[x].length && MapGeneration.heightMap[x][y] > 1) //do not change this lower than 1
                    {
                        fixHoles(x,y,mapArray);
                    } 
    }
    
    private static void assignTexture(int x, int y, int z, TerrainType mapArray[][][]) //assigens textures to boxes based off of their height.
    {
        if (z > 0)
            {
            if (z <= 2)
                        {
                            mapArray[x][z][y] = TerrainType.SAND;
                        }
                        else
                        {
                            mapArray[x][z][y] = TerrainType.GRASS;
                        }
            }
    }
    
    private static void fixHoles(int x, int y,  TerrainType[][][] mapArray) //The purpose of this sub is to check the adjecent and 1 down cubes to see if and are missing from the side of the terrain and patch them up.
    {
        if ((heightMap[x-1][y] - heightMap[x][y]) > 1)                              //checks if the difference between the height of (x-1,y) and (x,y) is more than 1
            {
                int amountMissing = heightMap[x-1][y] - heightMap[x][y] - 1;        //initiates the amount of missing blocks.
                for (int i = 0; i < amountMissing; i++)
                assignTexture(x-1,y,heightMap[x-1][y]+i-1,mapArray);   
            }
        if ((heightMap[x+1][y] - heightMap[x][y]) > 1)
            {
                int amountMissing = heightMap[x+1][y] - heightMap[x][y] - 1;
                for (int i = 0; i < amountMissing; i++)
                assignTexture(x+1,y,heightMap[x+1][y]+i-1,mapArray);     
            }
        if ((heightMap[x][y-1] - heightMap[x][y]) > 1)
            {
                int amountMissing = heightMap[x][y-1] - heightMap[x][y] - 1;
                for (int i = 0; i < amountMissing; i++)
                assignTexture(x,y-1,heightMap[x][y]+i+1,mapArray);       
            }
        if ((heightMap[x][y+1] - heightMap[x][y]) > 1)
            {
                int amountMissing = heightMap[x][y+1] - heightMap[x][y] - 1;
                for (int i = 0; i < amountMissing; i++)
                assignTexture(x,y+1,heightMap[x][y]+i+1,mapArray);      
            }
    }
    
    private static void addHitBox(int xCo, int yCo, int zCo)  //This function attaches a hit box to a box on the Co-ordinates input to stop the ship surfing up the terrain.
    {
       Box box = new Box(0.5f, 3f, 0.5f);
       Geometry box_geom = new Geometry("hitBox", box); 
       box_geom.setLocalTranslation(xCo+0.5f, yCo+0.5f, zCo+0.5f);
       rootNode.attachChild(box_geom);
       RigidBodyControl box_phys = new RigidBodyControl(0f);
       box_geom.addControl(box_phys);
       bulletAppState.getPhysicsSpace().add(box_phys);
       Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
       box_geom.setMaterial(mat);
       box_geom.setCullHint(CullHint.Always);
    }
}
