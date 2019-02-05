/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.ColorRGBA;
import java.util.Random;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.scene.Spatial.CullHint;
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
    static int xAxis = 220;
    static int yAxis = 64;
    static int zAxis = 220;
    static int[][] heightMap = new int[xAxis][zAxis];
    static int[][] spawnmap = new int[xAxis][zAxis];

    
    public static int[][] loadArrayIntoWorld(TerrainType[][][] worldArray) 
    {
        for (int x = 0; x < worldArray.length; x += 1)
        {
            for (int y = 0; y < worldArray[x].length; y += 1)
            {
                for (int z = 0; z < worldArray[x][y].length; z += 1)
                {
                    if (worldArray[x][y][z] != null)
                    {
                        spawnmap[x][z] = 1;
                        loadBox(x, y, z, worldArray[x][y][z]); 
                    }
                }
            }
        }
        fixBounds();
        return spawnmap;
    }
    
    
    private static void loadBox(int xCo, int yCo, int zCo, TerrainType skin)
    {
        //System.out.println("in here");
        Box box = new Box(0.5f, 0.5f, 0.5f);
        Geometry box_geom = new Geometry("box", box);
        Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
        if (null == skin)
        {
            mat.setColor("Color", ColorRGBA.Green);
        }
        else switch (skin) {
            case SAND:
                mat.setColor("Color", ColorRGBA.Yellow);
                break;
            case WOOD:
                mat.setColor("Color", ColorRGBA.Blue);
                break;
            case AIR:
                mat.setColor("Color", ColorRGBA.Red);
                break;
            case DIRT:
                mat.setColor("Color", ColorRGBA.Orange);
                break;
            case STONE:
                mat.setColor("Color", ColorRGBA.DarkGray);
                break;
            default:
                mat.setColor("Color", ColorRGBA.Green);
                break;
        }
        box_geom.setMaterial(mat);
        box_geom.setLocalTranslation(xCo+0.5f, yCo+0.5f, zCo+0.5f);
        rootNode.attachChild(box_geom);
        //RigidBodyControl box_phys = new RigidBodyControl(0f);
        //box_geom.addControl(box_phys);
        //bulletAppState.getPhysicsSpace().add(box_phys);
        if (yCo == 1)
        {
            addHitBox(xCo,yCo,zCo);
        }
    }
    
    public static TerrainType[][][] generateArray()
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
        return mapArray;
    }
   
    private static void generateHeightMap()
    {
        Random rand = new Random();
        FastNoise.seed = 1923123; //rand.nextInt(100000000) + 1;
        FastNoise.init();
        for (int x = 0; x < MapGeneration.heightMap.length; x += 1)
        {
            for (int y = 0; y< MapGeneration.heightMap[x].length; y += 1)
            {
                float arbritraryNumber = 32.0f; 
                //heightMap[x][y] = (int)(64.0 * FastNoise.noise(0.1, 1.0));
                if (((int)(32.0 * FastNoise.noise(x / arbritraryNumber, y / arbritraryNumber)) > 0))
                {                 
                    int xd = (heightMap.length / 2 ) - x;
                    float xdf = xd / ((float) (heightMap.length/2));
                    int yd = (heightMap[x].length / 2 ) - y;
                    float ydf = yd / ((float) (heightMap[x].length/2));
                    float mask = (float)(Math.sqrt(1.0f - Math.abs(xdf)) * Math.sqrt(1.0f - Math.abs(ydf)));
                    float height = (float) (mask * (32.0f * FastNoise.noise(x / arbritraryNumber, y/arbritraryNumber)));
                    MapGeneration.heightMap[x][y] = (int) height;                    
                }
            }    
        }
    }
    
    private static void fixBounds()
    {
       for(int x=-2;x<=xAxis+1;x++){
           loadBox(x,1,-2,TerrainType.STONE);
           loadBox(x,1,zAxis+1,TerrainType.STONE);
       }
       
       for(int z=-1;z<=zAxis;z++){
           loadBox(-2,1,z,TerrainType.STONE);
           loadBox(xAxis+1,1,z,TerrainType.STONE);
       }
    }
    
    private static void fillMap(int x, int y,  TerrainType[][][] mapArray)
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
    
    private static void assignTexture(int x, int y, int z, TerrainType mapArray[][][])
    {
        if (z > 0)
            {
            if (z <= 3)
                        {
                            mapArray[x][z][y] = TerrainType.SAND;
                        }
                        else
                        {
                            mapArray[x][z][y] = TerrainType.GRASS;
                        }
            }
    }
    
    private static void fixHoles(int x, int y,  TerrainType[][][] mapArray)
    {
        if ((heightMap[x-1][y] - heightMap[x][y]) > 1)
            {
                int amountMissing = heightMap[x-1][y] - heightMap[x][y] - 1;
                for (int i = 0; i < amountMissing; i++)
                //assignTexture(x-1,y,heightMap[x-1][y]+i-1,mapArray);
                mapArray[x-1][heightMap[x-1][y]+i-1][y] = TerrainType.WOOD;        
            }
        else if ((heightMap[x+1][y] - heightMap[x][y]) > 1)
            {
                int amountMissing = heightMap[x+1][y] - heightMap[x][y] - 1;
                for (int i = 0; i < amountMissing; i++)
                //assignTexture(x+1,y,heightMap[x+1][y]+i-1,mapArray);
                mapArray[x+1][heightMap[x+1][y]+i-1][y] = TerrainType.AIR;        
            }
        else if ((heightMap[x][y-1] - heightMap[x][y]) > 1)
            {
                int amountMissing = heightMap[x][y-1] - heightMap[x][y] - 1;
                for (int i = 0; i < amountMissing; i++)
                //assignTexture(x-1,y,heightMap[x-1][y]+i-1,mapArray);
                mapArray[x][heightMap[x][y]+i+1][y-1] = TerrainType.STONE;        
            }
        else if ((heightMap[x][y+1] - heightMap[x][y]) > 1)
            {
                int amountMissing = heightMap[x][y+1] - heightMap[x][y] - 1;
                for (int i = 0; i < amountMissing; i++)
                //assignTexture(x-1,y,heightMap[x-1][y]+i-1,mapArray);
                mapArray[x][heightMap[x][y]+i+1][y+1] = TerrainType.DIRT;        
            }
    }
    
    private static void addHitBox(int xCo, int yCo, int zCo)
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
